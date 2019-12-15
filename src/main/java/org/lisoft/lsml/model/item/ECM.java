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
package org.lisoft.lsml.model.item;

import org.lisoft.lsml.model.chassi.HardPointType;

/**
 * A Module representing ECM
 *
 * @author Emily Björk
 *
 */
public class ECM extends Module {

    public ECM(String aName, String aDesc, String aMwoName, int aMwoId, int aSlots, double aTons, double aHP,
            Faction aFaction, Integer aAllowedAmount) {
        super(aName, aDesc, aMwoName, aMwoId, aSlots, aTons, HardPointType.ECM, aHP, aFaction, null, null,
                aAllowedAmount);
    }
}
