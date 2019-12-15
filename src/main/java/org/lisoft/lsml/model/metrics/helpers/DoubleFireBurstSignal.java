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
package org.lisoft.lsml.model.metrics.helpers;

import java.util.Collection;

import org.lisoft.lsml.model.item.BallisticWeapon;
import org.lisoft.lsml.model.modifiers.Modifier;

/**
 * This class calculates the burst damage to a time for a weapon that is capable of double fire, such as the Ultra AC/5.
 *
 * @author Emily Björk
 */
public class DoubleFireBurstSignal implements IntegratedSignal {

    private final BallisticWeapon weapon;
    private final double range;
    private final Collection<Modifier> modifiers;

    /**
     * @param aWeapon
     *            The weapon to generate the signal for.
     * @param aPilotModules
     *            A {@link Collection} of modifiers that could affect the signal.
     * @param aRange
     *            The range of the weapon to calculate the signal for.
     */
    public DoubleFireBurstSignal(BallisticWeapon aWeapon, Collection<Modifier> aPilotModules, double aRange) {
        if (!aWeapon.canDoubleFire()) {
            throw new IllegalArgumentException(
                    "DoubleFireBurstSignal is only usable with weapons that can actually double fire!");
        }
        if (aRange < 0.0) {
            throw new IllegalArgumentException("Range must be larger than or equal to 0.0m!");
        }
        weapon = aWeapon;
        range = aRange;
        modifiers = aPilotModules;
    }

    @Override
    public double integrateFromZeroTo(double aTime) {
        final double shots = probableDamage(aTime);
        final double damage = weapon.getDamagePerShot();
        final double rangeFactor = weapon.getRangeEffectiveness(range, modifiers);
        return shots * damage * rangeFactor;
    }

    private double probableDamage(double aTime) {
        if (aTime < 0) {
            return 0;
        }
        final double p_jam = weapon.getJamProbability(modifiers);
        final double cd = weapon.getRawSecondsPerShot(modifiers);
        final double jamtime = weapon.getJamTime(modifiers);
        return p_jam * (1 + probableDamage(aTime - jamtime - cd)) + (1 - p_jam) * (2 + probableDamage(aTime - cd));
    }
}
