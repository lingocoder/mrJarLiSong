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
package org.lisoft.lsml.model.database;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.lisoft.lsml.model.NoSuchItemException;
import org.lisoft.lsml.model.item.Weapon;
import org.lisoft.lsml.model.modifiers.ModifierDescription;
import org.lisoft.lsml.model.modifiers.ModifierType;
import org.lisoft.lsml.model.modifiers.Operation;
import org.lisoft.lsml.view_fx.LiSongMechLab;

/**
 * A database of all the quirks in the game.
 *
 * @author Emily Björk
 */
public class ModifiersDB {
    private final static Map<String, ModifierDescription> mwoname2modifier;
    public final static ModifierDescription HEAT_MOVEMENT_DESC;

    /**
     * A decision has been made to rely on static initialisers for *DB classes. The motivation is that all items are
     * immutable, and this is the only way that allows providing global item constants such as ItemDB.AMS.
     */
    static {
        final Database database = LiSongMechLab.getDatabase()
                .orElseThrow(() -> new RuntimeException("Cannot run without database"));

        mwoname2modifier = new HashMap<>();
        final Collection<ModifierDescription> modifiers = database.getModifierDescriptions().values();
        for (final ModifierDescription description : modifiers) {
            mwoname2modifier.put(canonicalize(description.getKey()), description);
        }

        HEAT_MOVEMENT_DESC = new ModifierDescription("ENGINE HEAT", "movementheat_multiplier", Operation.MUL,
                ModifierDescription.SEL_HEAT_MOVEMENT, null, ModifierType.NEGATIVE_GOOD);
    }

    public static Collection<String> getAllSelectors(Class<? extends Weapon> aClass) {
        final Set<String> ans = new HashSet<>();
        for (final Weapon w : ItemDB.lookup(aClass)) {
            ans.addAll(w.getAliases());
        }
        ans.addAll(ModifierDescription.SEL_HEAT_DISSIPATION);
        ans.addAll(ModifierDescription.SEL_HEAT_LIMIT);
        return ans;
    }

    public static Collection<String> getAllWeaponSelectors() {
        final Set<String> ans = new HashSet<>();
        for (final Weapon w : ItemDB.lookup(Weapon.class)) {
            ans.addAll(w.getAliases());
        }
        ans.addAll(ModifierDescription.SEL_HEAT_DISSIPATION);
        ans.addAll(ModifierDescription.SEL_HEAT_LIMIT);
        ans.addAll(ModifierDescription.SEL_HEAT_EXTERNALTRANSFER);
        return ans;
    }

    /**
     * Looks up a {@link ModifierDescription} by a MWO key.
     *
     * @param aKey
     *            The lookup key.
     * @return A {@link ModifierDescription}.
     * @throws NoSuchItemException
     *             if no {@link ModifierDescription} was found with that key.
     */
    public static ModifierDescription lookup(String aKey) throws NoSuchItemException {
        final ModifierDescription description = mwoname2modifier.get(canonicalize(aKey));
        if (description == null) {
            throw new NoSuchItemException("Unknown key!");
        }
        return description;
    }

    /**
     * Canonicalizes a string for lookup in the maps.
     *
     * @param aName
     *            The string to canonicalize.
     * @return A canonicalized {@link String}.
     */
    private static String canonicalize(String aName) {
        return aName.toLowerCase();
    }
}
