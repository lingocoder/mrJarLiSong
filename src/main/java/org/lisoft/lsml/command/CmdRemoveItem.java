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
package org.lisoft.lsml.command;

import org.lisoft.lsml.messages.MessageDelivery;
import org.lisoft.lsml.model.database.UpgradeDB;
import org.lisoft.lsml.model.item.*;
import org.lisoft.lsml.model.loadout.*;
import org.lisoft.lsml.model.loadout.EquipResult.EquipResultType;
import org.lisoft.lsml.util.CommandStack.Command;

/**
 * This {@link Command} removes an {@link Item} from a {@link ConfiguredComponent}.
 *
 * @author Emily Björk
 */
public class CmdRemoveItem extends CmdItemBase {
    private int numEngineHS = 0;

    /**
     * Creates a new operation.
     *
     * @param aMessageDelivery
     *            The {@link MessageDelivery} to send messages on when items are removed.
     * @param aLoadout
     *            The {@link Loadout} to remove the item from.
     * @param aComponent
     *            The {@link ConfiguredComponent} to remove from.
     * @param aItem
     *            The {@link Item} to remove.
     */
    public CmdRemoveItem(MessageDelivery aMessageDelivery, Loadout aLoadout, ConfiguredComponent aComponent,
            Item aItem) {
        super(aMessageDelivery, aLoadout, aComponent, aItem);
        if (aItem instanceof Internal) {
            throw new IllegalArgumentException("Internals cannot be removed!");
        }
    }

    @Override
    public String describe() {
        return "remove " + item.getName() + " from " + component.getInternalComponent().getLocation();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + numEngineHS;
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof CmdRemoveItem)) {
            return false;
        }
        final CmdRemoveItem other = (CmdRemoveItem) obj;
        if (numEngineHS != other.numEngineHS) {
            return false;
        }
        return true;
    }

    @Override
    public void undo() {
        add(component, item);

        if (item instanceof Engine) {
            final Engine engine = (Engine) item;
            addXLSides(engine);

            final HeatSink heatSinkType = loadout.getUpgrades().getHeatSink().getHeatSinkType();
            while (numEngineHS > 0) {
                numEngineHS--;
                add(component, heatSinkType);
            }
        }
    }

    @Override
    public void apply() throws EquipException {
        if (item instanceof ECM && loadout.getUpgrades().getArmour() == UpgradeDB.IS_STEALTH_ARMOUR) {
            EquipException.checkAndThrow(
                    EquipResult.make(EquipResultType.CannotRemoveECM));
        }

        if (!component.canRemoveItem(item)) {
            throw new IllegalArgumentException("Can not remove item: " + item + " from " + component);
        }

        if (item instanceof Engine) {
            final Engine engine = (Engine) item;
            removeXLSides(engine);

            int engineHsLeft = component.getEngineHeatSinks();
            final HeatSink heatSinkType = loadout.getUpgrades().getHeatSink().getHeatSinkType();
            while (engineHsLeft > 0) {
                engineHsLeft--;
                numEngineHS++;
                remove(component, heatSinkType);
            }
        }
        remove(component, item);
    }
}
