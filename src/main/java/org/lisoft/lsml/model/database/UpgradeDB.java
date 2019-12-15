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
import java.util.Map;
import java.util.stream.Stream;

import org.lisoft.lsml.model.NoSuchItemException;
import org.lisoft.lsml.model.chassi.Chassis;
import org.lisoft.lsml.model.item.Faction;
import org.lisoft.lsml.model.upgrades.ArmourUpgrade;
import org.lisoft.lsml.model.upgrades.GuidanceUpgrade;
import org.lisoft.lsml.model.upgrades.HeatSinkUpgrade;
import org.lisoft.lsml.model.upgrades.StructureUpgrade;
import org.lisoft.lsml.model.upgrades.Upgrade;
import org.lisoft.lsml.view_fx.LiSongMechLab;

/**
 * A database class that holds all the {@link Upgrade}s parsed from the game files.
 *
 * @author Emily Björk
 */
public class UpgradeDB {
    public static final int IS_SHS_ID = 3003;
    public static final int IS_DHS_ID = 3002;
    public static final int CLAN_SHS_ID = 3006;
    public static final int CLAN_DHS_ID = 3005;

    public static final int IS_STD_ARMOUR_ID = 2810;
    public static final int CLAN_STD_ARMOUR_ID = 2816;

    public static final int IS_STD_STRUCTURE_ID = 3100;
    public static final int IS_ES_STRUCTURE_ID = 3101;
    public static final int CLAN_STD_STRUCTURE_ID = 3103;
    public static final int CLAN_ES_STRUCTURE_ID = 3102;

    public static final GuidanceUpgrade STD_GUIDANCE;
    public static final GuidanceUpgrade ARTEMIS_IV;

    public static final HeatSinkUpgrade IS_SHS;
    public static final HeatSinkUpgrade IS_DHS;
    public static final HeatSinkUpgrade CLAN_SHS;
    public static final HeatSinkUpgrade CLAN_DHS;

    public static final ArmourUpgrade IS_STD_ARMOUR;
    public static final ArmourUpgrade IS_FF_ARMOUR;
    public static final ArmourUpgrade IS_LIGHT_FF_ARMOUR;
    public static final ArmourUpgrade IS_STEALTH_ARMOUR;
    public static final ArmourUpgrade CLAN_STD_ARMOUR;
    public static final ArmourUpgrade CLAN_FF_ARMOUR;

    public static final StructureUpgrade IS_STD_STRUCTURE;
    public static final StructureUpgrade IS_ES_STRUCTURE;
    public static final StructureUpgrade CLAN_STD_STRUCTURE;
    public static final StructureUpgrade CLAN_ES_STRUCTURE;

    private static final Map<Integer, Upgrade> id2upgrade;

    /**
     * A decision has been made to rely on static initialisers for *DB classes. The motivation is that all items are
     * immutable, and this is the only way that allows providing global item constants such as ItemDB.AMS.
     */
    static {
        final Database database = LiSongMechLab.getDatabase()
                .orElseThrow(() -> new RuntimeException("Cannot run without database"));

        id2upgrade = new HashMap<>();
        for (final Upgrade upgrade : database.getUpgrades()) {
            id2upgrade.put(upgrade.getId(), upgrade);
        }

        try {
            IS_STD_ARMOUR = (ArmourUpgrade) lookup(IS_STD_ARMOUR_ID);
            IS_FF_ARMOUR = (ArmourUpgrade) lookup(2811);
            IS_LIGHT_FF_ARMOUR = (ArmourUpgrade) lookup(2812);
            IS_STEALTH_ARMOUR = (ArmourUpgrade) lookup(2814);
            CLAN_FF_ARMOUR = (ArmourUpgrade) lookup(2815);
            CLAN_STD_ARMOUR = (ArmourUpgrade) lookup(CLAN_STD_ARMOUR_ID);

            IS_STD_STRUCTURE = (StructureUpgrade) lookup(IS_STD_STRUCTURE_ID);
            IS_ES_STRUCTURE = (StructureUpgrade) lookup(IS_ES_STRUCTURE_ID);
            CLAN_ES_STRUCTURE = (StructureUpgrade) lookup(CLAN_ES_STRUCTURE_ID);
            CLAN_STD_STRUCTURE = (StructureUpgrade) lookup(CLAN_STD_STRUCTURE_ID);

            IS_SHS = (HeatSinkUpgrade) lookup(IS_SHS_ID);
            IS_DHS = (HeatSinkUpgrade) lookup(IS_DHS_ID);
            CLAN_DHS = (HeatSinkUpgrade) lookup(CLAN_DHS_ID);
            CLAN_SHS = (HeatSinkUpgrade) lookup(CLAN_SHS_ID);

            STD_GUIDANCE = (GuidanceUpgrade) lookup(3051);
            ARTEMIS_IV = (GuidanceUpgrade) lookup(3050);
        }
        catch (final NoSuchItemException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the standard armour type for the respective faction.
     *
     * @param aFaction
     *            The {@link Faction} to get the armour type for.
     * @return A {@link ArmourUpgrade} suitable for 'Mechs of the given {@link Faction}.
     */
    public static ArmourUpgrade getDefaultArmour(Faction aFaction) {
        if (Faction.CLAN == aFaction) {
            return CLAN_STD_ARMOUR;
        }
        return IS_STD_ARMOUR;
    }

    /**
     * Returns the standard guidance type for the respective faction.
     *
     * @param aFaction
     *            The {@link Faction} to get the guidance type for.
     * @return A {@link GuidanceUpgrade} suitable for 'Mechs of the given {@link Faction}.
     */
    public static GuidanceUpgrade getDefaultGuidance(Faction aFaction) {
        return STD_GUIDANCE;
    }

    /**
     * Returns the standard heat sink type for the respective faction.
     *
     * @param aFaction
     *            The {@link Faction} to get the heat sink type for.
     * @return A {@link HeatSinkUpgrade} suitable for 'Mechs of the given {@link Faction}.
     */
    public static HeatSinkUpgrade getDefaultHeatSinks(Faction aFaction) {
        if (Faction.CLAN == aFaction) {
            return CLAN_SHS;
        }
        return IS_SHS;
    }

    /**
     * Returns the standard structure type for the respective faction.
     *
     * @param aFaction
     *            The {@link Faction} to get the structure type for.
     * @return A {@link StructureUpgrade} suitable for 'Mechs of the given {@link Faction}.
     */
    public static StructureUpgrade getDefaultStructure(Faction aFaction) {
        if (Faction.CLAN == aFaction) {
            return CLAN_STD_STRUCTURE;
        }
        return IS_STD_STRUCTURE;
    }

    /**
     * Get the default upgrade by class type.
     *
     * @param aFaction
     *            The {@link Faction} to get the default value for.
     * @param aClass
     *            The type of default upgrade to get.
     * @return A {@link Upgrade} of the type <code>T</code> which is the default for the given faction.
     */
    public static <T extends Upgrade> T getDefaultUpgrade(Faction aFaction, Class<T> aClass) {
        if (aClass.isAssignableFrom(ArmourUpgrade.class)) {
            return aClass.cast(getDefaultArmour(aFaction));
        }
        if (aClass.isAssignableFrom(StructureUpgrade.class)) {
            return aClass.cast(getDefaultStructure(aFaction));
        }
        if (aClass.isAssignableFrom(GuidanceUpgrade.class)) {
            return aClass.cast(getDefaultGuidance(aFaction));
        }
        if (aClass.isAssignableFrom(HeatSinkUpgrade.class)) {
            return aClass.cast(getDefaultHeatSinks(aFaction));
        }
        throw new IllegalArgumentException("getUpgradeOfType must be called with an upgrade type class!");

    }

    public static GuidanceUpgrade getGuidance(@SuppressWarnings("unused") Faction aFaction, boolean aUpgraded) {
        return aUpgraded ? ARTEMIS_IV : STD_GUIDANCE;
    }

    /**
     * Looks up an {@link Upgrade} by its MW:O ID.
     *
     * @param aMwoId
     *            The ID to look up.
     * @return The {@link Upgrade} for the sought for ID.
     * @throws IllegalArgumentException
     *             Thrown if the ID is not a valid upgrade ID.
     * @throws NoSuchItemException
     *             if no upgrade could be found with the given ID.
     */
    public static Upgrade lookup(int aMwoId) throws NoSuchItemException {
        final Upgrade ans = id2upgrade.get(aMwoId);
        if (null == ans) {
            throw new NoSuchItemException("The ID: " + aMwoId + " is not a valid MWO upgrade ID!");
        }
        return ans;
    }

    /**
     * Finds all the upgrades of the given type that are usable on the given chassis.
     *
     * @param aChassis
     *            The chassis to look up for.
     * @param aUpgradeType
     *            The type of upgrades to find.
     * @return A {@link Collection} of all the upgrades.
     */
    public static <T extends Upgrade> Stream<T> streamCompatible(Chassis aChassis, Class<T> aUpgradeType) {
        return id2upgrade.values().stream()
                .filter(x -> aChassis.canUseUpgrade(x) && aUpgradeType.isAssignableFrom(x.getClass()))
                .map(x -> aUpgradeType.cast(x));

    }
}
