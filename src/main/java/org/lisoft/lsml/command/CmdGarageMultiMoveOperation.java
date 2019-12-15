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
 * This command moves multiple garage paths into the given path.
 *
 * @author Emily Björk
 * @param <T>
 *            The type of the object that is being moved.
 */
public class CmdGarageMultiMoveOperation<T extends NamedObject> extends CompositeCommand {

    private final Collection<GaragePath<T>> srcPaths;
    private final GaragePath<T> dstDir;

    public CmdGarageMultiMoveOperation(MessageDelivery aMessageTarget, GaragePath<T> aDstDir,
            Collection<GaragePath<T>> aSourcePaths) {
        super("multiple move", aMessageTarget);
        srcPaths = aSourcePaths;
        dstDir = aDstDir;
    }

    @Override
    protected void buildCommand() throws EquipException {
        for (final GaragePath<T> path : srcPaths) {
            addOp(new CmdGarageMove<>(messageBuffer, dstDir, path));
        }
    }

}
