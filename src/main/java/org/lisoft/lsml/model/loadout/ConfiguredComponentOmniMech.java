/*
 * @formatter:off
 * Li Song Mechlab - A 'mech building tool for PGI's MechWarrior: Online.
 * Copyright (C) 2013  Emily Björk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
//@formatter:on
package org.lisoft.lsml.model.loadout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lisoft.lsml.model.chassi.ComponentOmniMech;
import org.lisoft.lsml.model.chassi.HardPoint;
import org.lisoft.lsml.model.chassi.HardPointType;
import org.lisoft.lsml.model.chassi.OmniPod;
import org.lisoft.lsml.model.database.ItemDB;
import org.lisoft.lsml.model.item.Engine;
import org.lisoft.lsml.model.item.HeatSink;
import org.lisoft.lsml.model.item.Item;
import org.lisoft.lsml.model.item.Weapon;
import org.lisoft.lsml.model.loadout.EquipResult.EquipResultType;

/**
 * This class models a configured {@link OmniPod} on an {@link LoadoutOmniMech}.
 *
 * @author Emily Björk
 */
public class ConfiguredComponentOmniMech extends ConfiguredComponent {
    private OmniPod omniPod;
    private final Map<Item, Boolean> toggleStates = new HashMap<>();

    public ConfiguredComponentOmniMech(ComponentOmniMech aComponentOmniMech, boolean aManualArmour) {
        super(aComponentOmniMech, aManualArmour);
        if (!aComponentOmniMech.hasFixedOmniPod()) {
            throw new IllegalArgumentException("Component without fixed omnipod was constructed without omnipod!");
        }

        setOmniPod(aComponentOmniMech.getFixedOmniPod());
    }

    public ConfiguredComponentOmniMech(ComponentOmniMech aComponentOmniMech, boolean aManualArmour, OmniPod aOmniPod) {
        super(aComponentOmniMech, aManualArmour);

        if (aComponentOmniMech.hasFixedOmniPod()) {
            throw new IllegalArgumentException("Component with fixed omnipod was constructed with omnipod!");
        }
        setOmniPod(aOmniPod);
    }

    public ConfiguredComponentOmniMech(ConfiguredComponentOmniMech aConfiguredOmnipod) {
        super(aConfiguredOmnipod);
        changeOmniPod(aConfiguredOmnipod.omniPod);
        toggleStates.putAll(aConfiguredOmnipod.toggleStates);
    }

    @Override
    public EquipResult canEquip(Item aItem) {
        final EquipResult superResult = super.canEquip(aItem);
        if (superResult != EquipResult.SUCCESS) {
            return superResult;
        }

        int slotComp = 0;
        if (aItem instanceof Weapon && ((Weapon) aItem).isLargeBore()) {
            if (getToggleState(ItemDB.HA)) {
                slotComp++;
            }
            if (getToggleState(ItemDB.LAA)) {
                slotComp++;
            }
        }

        if (getSlotsFree() + slotComp < aItem.getSlots()) {
            return EquipResult.make(getInternalComponent().getLocation(), EquipResultType.NotEnoughSlots);
        }
        return EquipResult.SUCCESS;
    }

    /**
     * Checks local conditions if the given item can be toggled on. The loadout must have enough free slots and tonnage
     * globally too which is up to the caller to make sure.
     *
     * @param aItem
     *            The item to try to enable.
     * @return <code>true</code> if the item can be toggled on.
     */
    public EquipResult canToggleOn(Item aItem) {
        if (!toggleStates.containsKey(aItem)) {
            return EquipResult.make(getInternalComponent().getLocation(), EquipResultType.NotToggleable);
        }

        if (getSlotsFree() < 1) {
            return EquipResult.make(getInternalComponent().getLocation(), EquipResultType.NotEnoughSlots);
        }

        boolean removeHALAA = false;

        for (final Item item : getItemsEquipped()) {
            if (item instanceof Weapon && ((Weapon) item).isLargeBore()) {
                removeHALAA = true;
                break;
            }
        }

        if (!removeHALAA) {
            for (final Item item : getInternalComponent().getFixedItems()) {
                if (item instanceof Weapon && ((Weapon) item).isLargeBore()) {
                    removeHALAA = true;
                    break;
                }
            }
        }

        if (removeHALAA) {
            return EquipResult.make(getInternalComponent().getLocation(), EquipResultType.LargeBoreWeaponPresent);
        }

        if (aItem == ItemDB.HA) {
            if (!toggleStates.get(ItemDB.LAA)) {
                return EquipResult.make(EquipResultType.LaaBeforeHa);
            }
        }
        return EquipResult.SUCCESS; // This can only be LAA, which can always be enabled if there is at least one free
                                    // slot locally and globally
    }

    /**
     * @param aOmniPod
     *            The {@link OmniPod} to set for this component.
     */
    public void changeOmniPod(OmniPod aOmniPod) {
        if (getInternalComponent().hasFixedOmniPod()) {
            throw new UnsupportedOperationException("Cannot change omnipod on a component with a fixed omnipod.");
        }
        setOmniPod(aOmniPod);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConfiguredComponentOmniMech other = (ConfiguredComponentOmniMech) obj;
        if (omniPod != other.omniPod) {
            return false;
        }
        if (!toggleStates.equals(other.toggleStates)) {
            return false;
        }
        return true;
    }

    @Override
    public int getHardPointCount(HardPointType aHardpointType) {
        return omniPod.getHardPointCount(aHardpointType);
    }

    @Override
    public Collection<HardPoint> getHardPoints() {
        return omniPod.getHardPoints();
    }

    @Override
    public ComponentOmniMech getInternalComponent() {
        return (ComponentOmniMech) super.getInternalComponent();
    }

    @Override
    public List<Item> getItemsFixed() {
        final List<Item> fixed = new ArrayList<>(getInternalComponent().getFixedItems());
        fixed.addAll(getOmniPod().getFixedItems());

        final Boolean laa = toggleStates.get(ItemDB.LAA);
        if (laa != null && laa == true) {
            fixed.add(ItemDB.LAA);
        }
        final Boolean ha = toggleStates.get(ItemDB.HA);
        if (ha != null && ha == true) {
            fixed.add(ItemDB.HA);
        }
        return fixed;
    }

    /**
     * @return The currently mounted {@link OmniPod}.
     */
    public OmniPod getOmniPod() {
        return omniPod;
    }

    @Override
    public int getSlotsUsed() {
        int slots = 0;
        int engineHsSlots = 0;
        int numHs = 0;
        int hsSize = 0;
        for (final Item item : getItemsFixed()) {
            slots += item.getSlots();
            if (item instanceof Engine) {
                engineHsSlots = ((Engine) item).getNumHeatsinkSlots();
            }
            else if (item instanceof HeatSink) {
                hsSize = item.getSlots();
                numHs++;
            }
        }
        for (final Item item : getItemsEquipped()) {
            slots += item.getSlots();
            if (item instanceof Engine) {
                engineHsSlots = ((Engine) item).getNumHeatsinkSlots();
            }
            else if (item instanceof HeatSink) {
                hsSize = item.getSlots();
                numHs++;
            }
        }
        return slots + getInternalComponent().getDynamicArmourSlots()
                + getInternalComponent().getDynamicStructureSlots() - Math.min(engineHsSlots, numHs) * hsSize;
    }

    /**
     * @param aItem
     *            The item to get the toggle state for.
     * @return <code>true</code> if the given item is toggled on. Returns <code>false</code> for items that are not
     *         toggleable.
     */
    public boolean getToggleState(Item aItem) {
        final Boolean ans = toggleStates.get(aItem);
        return ans == null ? false : ans;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (omniPod == null ? 0 : omniPod.hashCode());
        result = prime * result + (toggleStates == null ? 0 : toggleStates.hashCode());
        return result;
    }

    @Override
    public boolean hasMissileBayDoors() {
        return getOmniPod().hasMissileBayDoors();
    }

    /**
     * Sets the toggle state of the item without any questions asked. The caller must verify that the toggle will result
     * in a valid loadout.
     *
     * @param aItem
     *            The item to toggle. If this is not a toggleable item, an {@link IllegalArgumentException} will be
     *            thrown.
     * @param aNewState
     *            The new state of the toggle.
     */
    public void setToggleState(Item aItem, boolean aNewState) {
        if (!toggleStates.containsKey(aItem)) {
            throw new IllegalArgumentException("Not a toggleable item: " + aItem);
        }
        toggleStates.put(aItem, aNewState);
    }

    private void setOmniPod(OmniPod aOmniPod) {
        omniPod = aOmniPod;

        // Well, I assume that the toggleable internals are only ever defined in the omnipods and the only fixed items
        // ever defined in the omnipods.
        toggleStates.clear();
        for (final Item item : omniPod.getToggleableItems()) {
            toggleStates.put(item, true); // Default enabled
        }
    }

}
