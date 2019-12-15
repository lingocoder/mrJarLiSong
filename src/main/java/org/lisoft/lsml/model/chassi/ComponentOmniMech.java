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
package org.lisoft.lsml.model.chassi;

import java.util.List;

import org.lisoft.lsml.model.item.Engine;
import org.lisoft.lsml.model.item.Item;
import org.lisoft.lsml.model.modifiers.Attribute;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * A component specific to omnimechs.
 *
 * @author Emily Björk
 */
public class ComponentOmniMech extends Component {
    private final OmniPod fixedOmniPod;
    @XStreamAsAttribute
    private final int dynamicArmour;
    @XStreamAsAttribute
    private final int dynamicStructure;

    /**
     * Creates a new {@link ComponentOmniMech}.
     *
     * @param aCriticalSlots
     *            The number of critical slots in the component.
     * @param aHitPoints
     *            The number of internal hit points on the component (determines armour too).
     * @param aLocation
     *            The location of the component.
     * @param aFixedOmniPod
     *            If this component has a fixed {@link OmniPod}, a reference to said {@link OmniPod} otherwise
     *            <code>null</code> if the {@link OmniPod} can be changed.
     * @param aFixedItems
     *            An array of fixed {@link Item}s for this component.
     * @param aDynamicStructureSlots
     *            An array where each element represents the ordinal of a {@link Location} and how many dynamic
     *            structure slots are fixed at that location.
     * @param aDynamicArmourSlots
     *            An array where each element represents the ordinal of a {@link Location} and how many dynamic armour
     *            slots are fixed at that location.
     */
    public ComponentOmniMech(Location aLocation, int aCriticalSlots, Attribute aHitPoints, List<Item> aFixedItems,
            OmniPod aFixedOmniPod, int aDynamicStructureSlots, int aDynamicArmourSlots) {
        super(aCriticalSlots, aHitPoints, aLocation, aFixedItems);
        fixedOmniPod = aFixedOmniPod;
        dynamicArmour = aDynamicArmourSlots;
        dynamicStructure = aDynamicStructureSlots;
    }

    /**
     * @return The number of dynamic armour slots in the given location.
     */
    public int getDynamicArmourSlots() {
        return dynamicArmour;
    }

    /**
     * @return The number of dynamic structure slots in the given location.
     */
    public int getDynamicStructureSlots() {
        return dynamicStructure;
    }

    /**
     * @return If this component has a fixed {@link OmniPod}, it returns the {@link OmniPod}. Otherwise it returns
     *         <code>null</code>.
     */
    public OmniPod getFixedOmniPod() {
        return fixedOmniPod;
    }

    /**
     * @return True if this {@link ComponentOmniMech} has a fixed {@link OmniPod} that can't be changed.
     */
    public boolean hasFixedOmniPod() {
        return null != fixedOmniPod;
    }

    @Override
    public boolean isAllowed(Item aItem, Engine aEngine) {
        // Toggleable actuators are not part of the component, but rather of the omnipod.
        // So we don't need to consider them here.
        int usedSlots = getFixedItemSlots() + getDynamicArmourSlots() + getDynamicStructureSlots();
        if (aEngine != null && aEngine.getSide().isPresent()) {
            usedSlots += aEngine.getSide().get().getSlots();
        }

        if (aItem.getSlots() > getSlots() - usedSlots) {
            return false;
        }
        return super.isAllowed(aItem, aEngine);
    }
}
