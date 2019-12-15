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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lisoft.lsml.model.chassi.MovementProfile;
import org.lisoft.lsml.model.loadout.Loadout;
import org.lisoft.lsml.model.modifiers.Modifier;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * A test suite for {@link TurningSpeed}.
 *
 * @author Emily Björk
 */
@RunWith(MockitoJUnitRunner.class)
public class TurningSpeedTest {
    double moveSpeed = 4.0;
    double lowSpeed = 0.2;
    double midSpeed = 0.4;
    double hiSpeed = 0.8;
    double lowRate = 0.8;
    double midRate = 0.4;
    double hiRate = 0.2;

    @Mock
    MovementProfile movementProfile;
    @Mock
    Loadout loadout;
    List<Modifier> modifiers = new ArrayList<>();

    @Before
    public void setup() {
        Mockito.when(movementProfile.getTurnLerpLowSpeed(modifiers)).thenReturn(lowSpeed);
        Mockito.when(movementProfile.getTurnLerpMidSpeed(modifiers)).thenReturn(midSpeed);
        Mockito.when(movementProfile.getTurnLerpHighSpeed(modifiers)).thenReturn(hiSpeed);
        Mockito.when(movementProfile.getTurnLerpLowRate(modifiers)).thenReturn(lowRate);
        Mockito.when(movementProfile.getTurnLerpMidRate(modifiers)).thenReturn(midRate);
        Mockito.when(movementProfile.getTurnLerpHighRate(modifiers)).thenReturn(hiRate);
        Mockito.when(movementProfile.getSpeedFactor(modifiers)).thenReturn(moveSpeed);
        Mockito.when(loadout.getAllModifiers()).thenReturn(modifiers);
        Mockito.when(loadout.getMovementProfile()).thenReturn(movementProfile);
    }

    /**
     * Test that the calculate() methods returns the correct result.
     */
    @Test
    public final void testCalculate() {
        final double factor = 0.2;
        Mockito.when(movementProfile.getTurnLerpLowRate(modifiers)).thenReturn(factor * Math.PI / 180.0);

        final TurningSpeed cut = new TurningSpeed(loadout);
        assertEquals(factor, cut.calculate(), 1E-8);
    }

    /**
     * Test that calculate(double) returns a monotonically decreasing sequence of values.
     */
    @Test
    public final void testCalculateVariable_monotonic() {
        final double topSpeed = 100.0;

        final TurningSpeed cut = new TurningSpeed(loadout);

        double prev = Double.POSITIVE_INFINITY;
        for (double x = 0.0; x < topSpeed; x += 0.01) {
            final double ans = cut.calculate(x);
            assertTrue("x=" + x + " ans=" + ans + " prev=" + prev, ans <= prev);
            prev = ans;
        }
    }

    /**
     * Test that getArgumentValues() returns the correct values.
     */
    @Test
    public final void testGetArgumentValues() {
        final TurningSpeed cut = new TurningSpeed(loadout);
        final List<Double> args = cut.getArgumentValues();

        assertEquals(5, args.size());
        assertEquals(0.0, args.get(0), 0.0);
        assertEquals(lowSpeed, args.get(1), 0.0);
        assertEquals(midSpeed, args.get(2), 0.0);
        assertEquals(hiSpeed, args.get(3), 0.0);
        assertEquals(1.0, args.get(4), 0.0);
    }

    /**
     * Test that getArgumentValues() returns no doubles.
     */
    @Test
    public final void testGetArgumentValues_NoDoubles() {
        lowSpeed = 0.0;
        midSpeed = 0.0;
        hiSpeed = 1.0;
        setup();

        final TurningSpeed cut = new TurningSpeed(loadout);
        final List<Double> args = cut.getArgumentValues();

        assertEquals(2, args.size());
        assertEquals(0.0, args.get(0), 0.0);
        assertEquals(1.0, args.get(1), 0.0);
    }

    @Test
    public void testGetMetricName() {
        final String name = new TurningSpeed(loadout).getMetricName().toLowerCase();
        assertTrue(name.contains("turn"));
        assertTrue(name.contains("speed"));
        assertTrue(name.contains("°/s"));
    }
}
