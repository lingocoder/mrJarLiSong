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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.lisoft.lsml.model.database.ItemDB;
import org.lisoft.lsml.model.helpers.MockLoadoutContainer;
import org.lisoft.lsml.model.item.Engine;
import org.lisoft.lsml.model.item.Weapon;
import org.lisoft.lsml.model.modifiers.Modifier;
import org.mockito.Mockito;

/**
 * Test suite for {@link MaxSustainedDPS} {@link Metric}.
 *
 * @author Emily Björk
 */
@SuppressWarnings("javadoc")
public class MaxSustainedDPSTest {
    private HeatDissipation heatDissipation;
    private final MockLoadoutContainer mlc = new MockLoadoutContainer();
    private MaxSustainedDPS cut;
    private final List<Weapon> items = new ArrayList<>();

    @Before
    public void setup() {
        when(mlc.loadout.items(Weapon.class)).thenReturn(items);
        heatDissipation = mock(HeatDissipation.class);
        cut = new MaxSustainedDPS(mlc.loadout, heatDissipation);
    }

    /**
     * Damage shall be correctly calculated, taking high DpH weapons into account first regardless of order they are
     * found.
     */
    @Test
    public void testCalculate() throws Exception {
        // Setup
        final Weapon gauss = (Weapon) ItemDB.lookup("GAUSS RIFLE");
        final Weapon erppc = (Weapon) ItemDB.lookup("ER PPC");
        final Weapon llas = (Weapon) ItemDB.lookup("LARGE LASER");

        final long seed = 1;
        final Random rng = new Random(seed);
        items.add(gauss);
        items.add(erppc);
        items.add(erppc);
        items.add(llas);
        Collections.shuffle(items, rng); // "Deterministically random" shuffle

        // There is enough heat to dissipate the GAUSS, LLaser and 1.5 ER PPCs
        final double heat = gauss.getStat("h/s", null) + erppc.getStat("h/s", null) * 1.5 + llas.getStat("h/s", null);

        when(heatDissipation.calculate()).thenReturn(heat);

        // Execute
        final double result = cut.calculate(300.0); // 300.0 is inside LLAS optimal

        // Verify
        final double expected = gauss.getStat("d/s", null) + erppc.getStat("d/s", null) * 1.5
                + llas.getStat("d/s", null);
        assertEquals(expected, result, 0.0);
    }

    /**
     * AMS shall not be added to DPS
     */
    @Test
    public void testCalculate_ams() throws Exception {
        // Setup
        final Weapon gauss = (Weapon) ItemDB.lookup("GAUSS RIFLE");
        items.add(gauss);
        items.add(ItemDB.AMS);

        when(heatDissipation.calculate()).thenReturn(1.0);

        final double result = cut.calculate(0.0);

        assertEquals(gauss.getStat("d/s", null), result, 0.0);
    }

    /**
     * If an engine is equipped, its heat should be taken into account.
     */
    @Test
    public void testCalculate_EngineHeat() {
        // Setup
        final List<Modifier> modifiers = mock(List.class);
        when(mlc.loadout.getAllModifiers()).thenReturn(modifiers);

        final double range = 100.0;
        final double dph = 2.0;
        final double dps = 4.0;
        final double hps = 1.0;
        final Weapon weapon = mock(Weapon.class);
        when(weapon.getRangeEffectiveness(anyDouble(), same(modifiers))).thenReturn(1.0);
        when(weapon.getRangeMax(same(modifiers))).thenReturn(range);
        when(weapon.isOffensive()).thenReturn(true);
        when(weapon.getStat("d/h", modifiers)).thenReturn(dph);
        when(weapon.getStat("d/s", modifiers)).thenReturn(dps);
        when(weapon.getStat("h/s", modifiers)).thenReturn(hps);
        items.add(weapon);

        final double engineHeat = 0.2;
        final Engine engine = Mockito.mock(Engine.class);
        when(engine.getHeat(modifiers)).thenReturn(engineHeat);
        when(mlc.loadout.getEngine()).thenReturn(engine);

        final double heatDiss = 1.0;
        when(heatDissipation.calculate()).thenReturn(heatDiss);

        // Execute
        final double result = cut.calculate(range); // 300.0 is inside LLAS optimal

        // Verify
        final double expectedRatio = (heatDiss - engineHeat) / hps;
        final double expectedDPS = expectedRatio * dps;
        assertEquals(expectedDPS, result, 0.0);
    }

    /**
     * Damage shall be correctly calculated for only the selected weapon group
     */
    @Test
    public void testCalculate_WeaponGroups() throws Exception {
        // Setup
        final Weapon gauss = (Weapon) ItemDB.lookup("GAUSS RIFLE");
        final Weapon erppc = (Weapon) ItemDB.lookup("ER PPC");
        final Weapon llas = (Weapon) ItemDB.lookup("LARGE LASER");

        items.add(gauss);
        items.add(erppc);
        items.add(erppc);
        items.add(llas);

        final int group = 0;
        final Collection<Weapon> groupWeapons = new ArrayList<>();
        groupWeapons.add(gauss);
        groupWeapons.add(erppc);
        Mockito.when(mlc.weaponGroups.getWeapons(group, mlc.loadout)).thenReturn(groupWeapons);

        // There is enough heat to dissipate the GAUSS, LLaser and 1.5 ER PPCs
        final double heat = gauss.getStat("h/s", null) + erppc.getStat("h/s", null) * 1.5 + llas.getStat("h/s", null);

        when(heatDissipation.calculate()).thenReturn(heat);

        // Execute
        cut = new MaxSustainedDPS(mlc.loadout, heatDissipation, group);
        final double result = cut.calculate(300.0); // 300.0 is inside LLAS optimal

        // Verify
        final double expected = gauss.getStat("d/s", null) + erppc.getStat("d/s", null);
        assertEquals(expected, result, 0.0);
    }

    @Test
    public void testGetWeaponRatios() throws Exception {
        // Setup
        items.add((Weapon) ItemDB.lookup("MACHINE GUN"));
        items.add((Weapon) ItemDB.lookup("MACHINE GUN"));
        items.add((Weapon) ItemDB.lookup("GAUSS RIFLE"));
        items.add(ItemDB.AMS);
        items.add((Weapon) ItemDB.lookup("STREAK SRM 2"));
        items.add((Weapon) ItemDB.lookup("LRM 20"));
        items.add((Weapon) ItemDB.lookup("ER PPC"));
        items.add((Weapon) ItemDB.lookup("ER PPC"));

        when(heatDissipation.calculate()).thenReturn(1.0);

        // Execute & Verify Range = 0
        final Map<Weapon, Double> range0 = cut.getWeaponRatios(0);
        assertEquals(2.0, range0.remove(ItemDB.lookup("MACHINE GUN")), 0.0); // Two of them!
        assertEquals(1.0, range0.remove(ItemDB.lookup("GAUSS RIFLE")), 0.0);
        assertTrue(range0.remove(ItemDB.lookup("STREAK SRM 2")) > 0.0);
        assertEquals(0.0, range0.remove(ItemDB.lookup("LRM 20")), 0.0);
        assertFalse(range0.containsKey(ItemDB.AMS));

        // Execute & Verify Range = 750
        final Map<Weapon, Double> range750 = cut.getWeaponRatios(750);
        assertEquals(0.0, range750.remove(ItemDB.lookup("MACHINE GUN")), 0.0); // Two of them!
        assertEquals(1.0, range750.remove(ItemDB.lookup("GAUSS RIFLE")), 0.00001);
        assertEquals(0.0, range750.remove(ItemDB.lookup("STREAK SRM 2")), 0.0);
        assertTrue(range750.remove(ItemDB.lookup("LRM 20")) > 0.0);
        assertFalse(range750.containsKey(ItemDB.AMS));
    }

    /**
     * Tests that getWeaponRatios correctly handles the machine guns zero heat production.
     */
    @Test
    public void testGetWeaponRatios_machineGun() throws Exception {
        // Setup
        final Weapon mg = (Weapon) ItemDB.lookup("MACHINE GUN");
        items.add(mg);

        when(heatDissipation.calculate()).thenReturn(0.0);

        final Map<Weapon, Double> result_0 = cut.getWeaponRatios(-1);

        assertTrue(result_0.containsKey(mg));
        assertEquals(0.0, result_0.get(mg), 0.0);
    }

    /**
     * PPC shall have an instant fall off (patch 2013-09-03)
     */
    @Test
    public void testGetWeaponRatios_ppc() throws Exception {
        // Setup
        final Weapon ppc = (Weapon) ItemDB.lookup("PPC");
        items.add(ppc);

        when(heatDissipation.calculate()).thenReturn(10.0);

        assertEquals(ppc.getStat("d/s", null), cut.calculate(90.0 + 0.001), 0.0);
        assertEquals(0.0, cut.calculate(90.0 - 0.001), 0.0);
    }
}
