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
package org.lisoft.lsml.model.chassi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.lisoft.lsml.model.item.Faction;
import org.lisoft.lsml.model.item.Item;
import org.lisoft.lsml.model.modifiers.Modifier;
import org.mockito.Mockito;

/**
 * Test suite for {@link OmniPod} class.
 *
 * @author Emily Björk
 */
public class OmniPodTest {
    private String chassisName = "tbr-prime";
    private final List<Item> fixedItems = new ArrayList<>();
    private final List<HardPoint> hardPoints = new ArrayList<>();
    private final Location location = Location.CenterTorso;
    private final int maxJumpJets = 2;
    private final int mwoID = 30012;
    private final List<Modifier> quirks = new ArrayList<>();
    private String series = "timber wolf";
    private final List<Item> toggleableItems = new ArrayList<>();
    private final OmniPodSet omniPodSet = mock(OmniPodSet.class);
    private final Faction faction = Faction.CLAN;

    @Test
    public void testGetChassisName() {
        assertEquals(chassisName.toUpperCase(), makeCUT().getChassisName());
    }

    @Test
    public void testGetChassisSeries() {
        assertEquals(series.toUpperCase(), makeCUT().getChassisSeries());
    }

    @Test
    public void testGetFixedItem() {
        final Item i0 = Mockito.mock(Item.class);
        final Item i1 = Mockito.mock(Item.class);
        fixedItems.add(i0);
        fixedItems.add(i1);

        final List<Item> ans = new ArrayList<>(makeCUT().getFixedItems());

        assertEquals(2, ans.size());
        assertTrue(ans.remove(i0));
        assertTrue(ans.remove(i1));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetFixedItems_NoMod() {
        makeCUT().getFixedItems().add(Mockito.mock(Item.class));
    }

    @Test
    public void testGetHardPointCount() {
        final HardPoint hp1 = Mockito.mock(HardPoint.class);
        final HardPoint hp2 = Mockito.mock(HardPoint.class);
        final HardPoint hp3 = Mockito.mock(HardPoint.class);
        final HardPoint hp4 = Mockito.mock(HardPoint.class);

        Mockito.when(hp1.getType()).thenReturn(HardPointType.MISSILE);
        Mockito.when(hp2.getType()).thenReturn(HardPointType.MISSILE);
        Mockito.when(hp3.getType()).thenReturn(HardPointType.ECM);
        Mockito.when(hp4.getType()).thenReturn(HardPointType.ENERGY);

        hardPoints.add(hp1);
        hardPoints.add(hp2);
        hardPoints.add(hp3);
        hardPoints.add(hp4);

        assertEquals(2, makeCUT().getHardPointCount(HardPointType.MISSILE));
        assertEquals(1, makeCUT().getHardPointCount(HardPointType.ECM));
        assertEquals(1, makeCUT().getHardPointCount(HardPointType.ENERGY));
        assertEquals(0, makeCUT().getHardPointCount(HardPointType.BALLISTIC));
    }

    @Test
    public void testGetHardPoints() {
        final HardPoint hp1 = Mockito.mock(HardPoint.class);
        final HardPoint hp2 = Mockito.mock(HardPoint.class);
        hardPoints.add(hp1);
        hardPoints.add(hp2);

        final List<HardPoint> ans = new ArrayList<>(makeCUT().getHardPoints());

        assertEquals(2, ans.size());
        assertTrue(ans.remove(hp1));
        assertTrue(ans.remove(hp2));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetHardPoints_NoMod() {
        makeCUT().getHardPoints().add(Mockito.mock(HardPoint.class));
    }

    @Test
    public void testGetJumpJetsMax() {
        assertEquals(maxJumpJets, makeCUT().getJumpJetsMax());
    }

    @Test
    public void testGetLocation() {
        assertEquals(location, makeCUT().getLocation());
    }

    @Test
    public void testGetMwoId() {
        assertEquals(mwoID, makeCUT().getId());
    }

    @Test
    public void testGetOmniPodSet() {
        assertEquals(omniPodSet, makeCUT().getOmniPodSet());
    }

    @Test
    public void testGetQuirks() {
        assertSame(quirks, makeCUT().getQuirks());
    }

    @Test
    public void testGetToggleableItems() {
        final Item i0 = Mockito.mock(Item.class);
        final Item i1 = Mockito.mock(Item.class);
        toggleableItems.add(i0);
        toggleableItems.add(i1);

        final List<Item> ans = new ArrayList<>(makeCUT().getToggleableItems());

        assertEquals(2, ans.size());
        assertTrue(ans.remove(i0));
        assertTrue(ans.remove(i1));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetToggleableItems_NoMod() {
        makeCUT().getToggleableItems().add(Mockito.mock(Item.class));
    }

    @Test
    public void testHasMissileBayDoors_No() {
        final HardPoint hp1 = Mockito.mock(HardPoint.class);
        final HardPoint hp2 = Mockito.mock(HardPoint.class);
        final HardPoint hp3 = Mockito.mock(HardPoint.class);
        final HardPoint hp4 = Mockito.mock(HardPoint.class);

        Mockito.when(hp1.getType()).thenReturn(HardPointType.MISSILE);
        Mockito.when(hp2.getType()).thenReturn(HardPointType.MISSILE);
        Mockito.when(hp3.getType()).thenReturn(HardPointType.ECM);
        Mockito.when(hp4.getType()).thenReturn(HardPointType.ENERGY);

        hardPoints.add(hp1);
        hardPoints.add(hp2);
        hardPoints.add(hp3);
        hardPoints.add(hp4);

        assertFalse(makeCUT().hasMissileBayDoors());
    }

    @Test
    public void testHasMissileBayDoors_Yes() {
        final HardPoint hp1 = Mockito.mock(HardPoint.class);
        final HardPoint hp2 = Mockito.mock(HardPoint.class);
        final HardPoint hp3 = Mockito.mock(HardPoint.class);
        final HardPoint hp4 = Mockito.mock(HardPoint.class);

        Mockito.when(hp1.getType()).thenReturn(HardPointType.MISSILE);
        Mockito.when(hp2.getType()).thenReturn(HardPointType.MISSILE);
        Mockito.when(hp2.hasMissileBayDoor()).thenReturn(true);
        Mockito.when(hp3.getType()).thenReturn(HardPointType.ECM);
        Mockito.when(hp4.getType()).thenReturn(HardPointType.ENERGY);

        hardPoints.add(hp1);
        hardPoints.add(hp2);
        hardPoints.add(hp3);
        hardPoints.add(hp4);

        assertTrue(makeCUT().hasMissileBayDoors());
    }

    @Test
    public void testIsCompatible() {
        series = "TIMBER WOLF";
        chassisName = "TBR-PRIME";

        final ChassisOmniMech chassisP = Mockito.mock(ChassisOmniMech.class);
        Mockito.when(chassisP.getSeriesName()).thenReturn(series.toLowerCase());
        Mockito.when(chassisP.getName()).thenReturn(series.toLowerCase() + " tBR-PRIME");
        Mockito.when(chassisP.getShortName()).thenReturn("TBR-PRImE");

        final ChassisOmniMech chassisPI = Mockito.mock(ChassisOmniMech.class);
        Mockito.when(chassisPI.getSeriesName()).thenReturn(series.toLowerCase());
        Mockito.when(chassisPI.getName()).thenReturn(series.toLowerCase() + " TBR-PRIME(I)");
        Mockito.when(chassisPI.getShortName()).thenReturn("TBR-PRiME");

        final ChassisOmniMech chassisPG = Mockito.mock(ChassisOmniMech.class);
        Mockito.when(chassisPG.getSeriesName()).thenReturn(series.toLowerCase());
        Mockito.when(chassisPG.getName()).thenReturn(series.toLowerCase() + " TBR-PRIME(G)");
        Mockito.when(chassisPG.getShortName()).thenReturn("TBr-PRIME(G)");

        final ChassisOmniMech chassisC = Mockito.mock(ChassisOmniMech.class);
        Mockito.when(chassisC.getSeriesName()).thenReturn(series.toLowerCase());
        Mockito.when(chassisC.getName()).thenReturn(series.toLowerCase() + " TBR-C");
        Mockito.when(chassisC.getShortName()).thenReturn("TBr-c");

        final ChassisOmniMech scr = Mockito.mock(ChassisOmniMech.class);
        Mockito.when(scr.getSeriesName()).thenReturn("stormcrow");
        Mockito.when(scr.getName()).thenReturn("stormcrow scr-C");
        Mockito.when(scr.getShortName()).thenReturn("scrr-c");

        assertTrue(makeCUT().isCompatible(chassisP));
        assertTrue(makeCUT().isCompatible(chassisPI));
        assertTrue(makeCUT().isCompatible(chassisPG));
        assertTrue(makeCUT().isCompatible(chassisC));

        assertFalse(makeCUT().isCompatible(scr));
    }

    @Test
    public void testToString() {
        assertEquals(chassisName.toUpperCase(), makeCUT().toString());
    }

    protected OmniPod makeCUT() {
        return new OmniPod(mwoID, location, series, chassisName, omniPodSet, quirks, hardPoints, fixedItems,
                toggleableItems, maxJumpJets, faction);
    }
}
