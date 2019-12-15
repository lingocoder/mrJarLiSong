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
package org.lisoft.lsml.model.metrics;

import java.util.Collection;

import org.lisoft.lsml.model.item.Weapon;
import org.lisoft.lsml.model.loadout.Loadout;
import org.lisoft.lsml.model.loadout.LoadoutStandard;
import org.lisoft.lsml.model.modifiers.Modifier;

/**
 * This {@link Metric} calculates the maximal DPS a {@link LoadoutStandard} can output.
 *
 * @author Emily Björk
 */
public class MaxDPS extends AbstractRangeMetric {
    private final int weaponGroup;

    /**
     * Creates a new {@link MaxDPS} that calculates the maximal DPS for a given loadout using all weapons.
     *
     * @param aLoadout
     *            The loadout to calculate for.
     */
    public MaxDPS(final Loadout aLoadout) {
        this(aLoadout, -1);
    }

    /**
     * Creates a new {@link MaxDPS} metric that calculates the maximal DPS for the given weapon group.
     *
     * @param aLoadout
     *            The loadout to calculate for.
     * @param aGroup
     *            The weapon group to calculate for.
     */
    public MaxDPS(Loadout aLoadout, int aGroup) {
        super(aLoadout);
        weaponGroup = aGroup;
    }

    @Override
    public double calculate(double aRange) {
        checkRange(aRange);

        double ans = 0;
        final Collection<Modifier> modifiers = loadout.getAllModifiers();
        final Iterable<Weapon> weapons;
        if (weaponGroup < 0) {
            weapons = loadout.items(Weapon.class);
        }
        else {
            weapons = loadout.getWeaponGroups().getWeapons(weaponGroup, loadout);
        }

        for (final Weapon weapon : weapons) {
            if (weapon.isOffensive()) {
                ans += weapon.getRangeEffectiveness(aRange, modifiers) * weapon.getStat("d/s", modifiers);
            }

        }
        return ans;
    }
}
