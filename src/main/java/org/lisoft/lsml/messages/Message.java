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
package org.lisoft.lsml.messages;

import org.lisoft.lsml.model.loadout.Loadout;

/**
 * A base interface for all messages sent on the {@link MessageXBar}.
 * 
 * @author Emily Björk
 */
public interface Message {
    /**
     * Checks if this message is related to a specific {@link Loadout}.
     * 
     * @param aLoadout
     *            The {@link Loadout} to check.
     * @return <code>true</code> if this message affects the given {@link Loadout}.
     */
    boolean isForMe(Loadout aLoadout);

    /**
     * @return <code>true</code> if this message can affect the damage or heat output of the related {@link Loadout} .
     */
    boolean affectsHeatOrDamage();
}