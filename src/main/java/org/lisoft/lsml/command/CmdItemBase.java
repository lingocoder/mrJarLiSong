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

import org.lisoft.lsml.messages.ItemMessage;
import org.lisoft.lsml.messages.ItemMessage.Type;
import org.lisoft.lsml.messages.MessageDelivery;
import org.lisoft.lsml.model.chassi.Location;
import org.lisoft.lsml.model.item.Engine;
import org.lisoft.lsml.model.item.Item;
import org.lisoft.lsml.model.loadout.ConfiguredComponent;
import org.lisoft.lsml.model.loadout.Loadout;
import org.lisoft.lsml.model.loadout.LoadoutStandard;
import org.lisoft.lsml.util.CommandStack.Command;

/**
 * A helper class for implementing {@link Command}s that affect items on a {@link ConfiguredComponent}.
 * 
 * @author Emily Björk
 */
public abstract class CmdItemBase extends MessageCommand {
    protected final ConfiguredComponent component;
    protected final Loadout loadout;
    protected final Item item;

    /**
     * Creates a new {@link CmdItemBase}. The deriving classes shall throw if the the operation with the given item
     * would violate the {@link LoadoutStandard} or {@link ConfiguredComponent} invariant.
     * 
     * @param aMessageDelivery
     *            The {@link MessageDelivery} to send messages to when changes occur.
     * @param aLoadout
     *            The {@link Loadout} to operate on.
     * @param aComponent
     *            The {@link ConfiguredComponent} that this operation will affect.
     * @param aItem
     *            The {@link Item} to add or remove.
     */
    protected CmdItemBase(MessageDelivery aMessageDelivery, Loadout aLoadout, ConfiguredComponent aComponent,
            Item aItem) {
        super(aMessageDelivery);
        loadout = aLoadout;
        component = aComponent;
        item = aItem;
    }

    protected void add(ConfiguredComponent aComponent, Item aItem) {
        int index = aComponent.addItem(aItem);
        post(aComponent, Type.Added, aItem, index);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((component == null) ? 0 : component.hashCode());
        result = prime * result + ((item == null) ? 0 : item.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof CmdItemBase))
            return false;
        CmdItemBase other = (CmdItemBase) obj;
        if (component != other.component)
            return false;
        if (item != other.item)
            return false;
        return true;
    }

    protected void post(ConfiguredComponent aComponent, Type aType, Item aItem, int aIndex) {
        post(new ItemMessage(aComponent, aType, aItem, aIndex));
    }

    protected void remove(ConfiguredComponent aComponent, Item aItem) {
        int index = aComponent.removeItem(aItem);
        post(aComponent, Type.Removed, aItem, index);
    }

    protected void addXLSides(Engine engine) {
        engine.getSide().ifPresent(xlSide -> {
            ConfiguredComponent lt = loadout.getComponent(Location.LeftTorso);
            ConfiguredComponent rt = loadout.getComponent(Location.RightTorso);
            add(lt, xlSide);
            add(rt, xlSide);
        });
    }

    protected void removeXLSides(Engine engine) {
        engine.getSide().ifPresent(xlSide -> {
            ConfiguredComponent lt = loadout.getComponent(Location.LeftTorso);
            ConfiguredComponent rt = loadout.getComponent(Location.RightTorso);
            remove(lt, xlSide);
            remove(rt, xlSide);
        });
    }
}
