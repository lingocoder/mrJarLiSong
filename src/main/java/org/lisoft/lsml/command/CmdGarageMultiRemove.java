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

import java.util.Collection;

import org.lisoft.lsml.messages.MessageDelivery;
import org.lisoft.lsml.model.NamedObject;
import org.lisoft.lsml.model.garage.GaragePath;
import org.lisoft.lsml.model.loadout.EquipException;
import org.lisoft.lsml.util.CommandStack.CompositeCommand;

/**
 * This command removes multiple paths from the garage.
 *
 * @author Emily
 * @param <T>
 *            The type of the object that is being moved.
 */
public class CmdGarageMultiRemove<T extends NamedObject> extends CompositeCommand {
    private final Collection<GaragePath<T>> paths;

    /**
     * Constructs a new command to remove multiple paths.
     * 
     * @param aMessageTarget
     *            Where to send messages from this garage operation.
     * @param aPaths
     *            A {@link Collection} of {@link GaragePath}s to remove.
     */
    public CmdGarageMultiRemove(MessageDelivery aMessageTarget, Collection<GaragePath<T>> aPaths) {
        super("multiple remove", aMessageTarget);
        paths = aPaths;
    }

    @Override
    protected void buildCommand() throws EquipException {
        for (final GaragePath<T> path : paths) {
            addOp(new CmdGarageRemove<>(messageBuffer, path));
        }
    }
}
