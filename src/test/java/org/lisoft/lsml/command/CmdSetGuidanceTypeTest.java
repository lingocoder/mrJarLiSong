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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lisoft.lsml.messages.MessageXBar;
import org.lisoft.lsml.model.database.UpgradeDB;
import org.lisoft.lsml.model.helpers.MockLoadoutContainer;
import org.lisoft.lsml.model.item.Ammunition;
import org.lisoft.lsml.model.item.Item;
import org.lisoft.lsml.model.item.MissileWeapon;
import org.lisoft.lsml.model.loadout.EquipResult;
import org.lisoft.lsml.model.loadout.Loadout;
import org.lisoft.lsml.model.loadout.LoadoutStandard;
import org.lisoft.lsml.model.upgrades.GuidanceUpgrade;
import org.lisoft.lsml.model.upgrades.Upgrades;
import org.lisoft.lsml.util.CommandStack;
import org.lisoft.lsml.util.TestHelpers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Test suite for {@link CmdSetGuidanceType}.
 *
 * @author Emily Björk
 */
@SuppressWarnings("javadoc")
@RunWith(MockitoJUnitRunner.Silent.class)
public class CmdSetGuidanceTypeTest {
    MockLoadoutContainer mlc = new MockLoadoutContainer();

    @Mock
    GuidanceUpgrade oldGuidance;
    @Mock
    GuidanceUpgrade newGuidance;
    @Mock
    MessageXBar xBar;

    /**
     * Apply shall change the {@link GuidanceUpgrade} of the {@link Upgrades}s object of the {@link LoadoutStandard}
     * given as argument.
     */
    @Test
    public void testApply() throws Exception {
        Mockito.when(mlc.upgrades.getGuidance()).thenReturn(oldGuidance);
        final CommandStack stack = new CommandStack(0);
        Mockito.when(mlc.loadout.getFreeMass()).thenReturn(100.0);
        Mockito.when(mlc.loadout.getFreeSlots()).thenReturn(100);

        stack.pushAndApply(new CmdSetGuidanceType(xBar, mlc.loadout, newGuidance));

        Mockito.verify(mlc.upgrades).setGuidance(newGuidance);
    }

    /**
     * Apply shall delegate to the upgrades object to change all Missile Weapons and Ammunition types.
     */
    @Test
    public void testApply_changeMissileLaunchersAndAmmo() throws Exception {
        Mockito.when(mlc.upgrades.getGuidance()).thenReturn(oldGuidance);
        final CommandStack stack = new CommandStack(0);
        Mockito.when(mlc.loadout.getFreeMass()).thenReturn(100.0);
        Mockito.when(mlc.loadout.getFreeSlots()).thenReturn(100);
        Mockito.when(mlc.loadout.canEquipDirectly(any(Item.class))).thenReturn(EquipResult.SUCCESS);

        final MissileWeapon lrm5 = Mockito.mock(MissileWeapon.class);
        final MissileWeapon lrm5Artemis = Mockito.mock(MissileWeapon.class);
        final MissileWeapon narc = Mockito.mock(MissileWeapon.class);
        final Ammunition lrmAmmo = Mockito.mock(Ammunition.class);
        final Ammunition lrmAmmoArtemis = Mockito.mock(Ammunition.class);
        final Ammunition narcAmmo = Mockito.mock(Ammunition.class);

        final List<Item> rlItems = Arrays.asList(lrm5, lrmAmmo);
        final List<Item> ltItems = Arrays.asList(lrm5, narcAmmo, narc, lrmAmmo);

        Mockito.when(newGuidance.upgrade(lrm5)).thenReturn(lrm5Artemis);
        Mockito.when(newGuidance.upgrade(narc)).thenReturn(narc);
        Mockito.when(newGuidance.upgrade(lrmAmmo)).thenReturn(lrmAmmoArtemis);
        Mockito.when(newGuidance.upgrade(narcAmmo)).thenReturn(narcAmmo);
        Mockito.when(mlc.rl.canEquip(any(Item.class))).thenReturn(EquipResult.SUCCESS);
        Mockito.when(mlc.lt.canEquip(any(Item.class))).thenReturn(EquipResult.SUCCESS);
        Mockito.when(mlc.rl.getItemsEquipped()).thenReturn(rlItems);
        Mockito.when(mlc.lt.getItemsEquipped()).thenReturn(ltItems);
        Mockito.when(mlc.rl.canRemoveItem(any(Item.class))).thenReturn(true);
        Mockito.when(mlc.lt.canRemoveItem(any(Item.class))).thenReturn(true);

        stack.pushAndApply(new CmdSetGuidanceType(xBar, mlc.loadout, newGuidance));

        // FIXME: Verify... I can't gain access to verify this in any way...
        // assertEquals(2, rlItems.size());
        // assertEquals(4, ltItems.size());
        // assertTrue(rlItems.remove(lrm5Artemis));
        // assertTrue(rlItems.remove(lrmAmmoArtemis));
        // assertTrue(ltItems.remove(lrm5Artemis));
        // assertTrue(ltItems.remove(lrmAmmoArtemis));
        // assertTrue(ltItems.remove(narcAmmo));
        // assertTrue(ltItems.remove(narc));
    }

    /**
     * If apply fails, the changes shall have been rolled back completely.
     */
    @Test
    public void testApply_FailRollback() {
        Mockito.when(mlc.loadout.getFreeMass()).thenReturn(0.0);
        Mockito.when(newGuidance.getTotalTons(mlc.loadout)).thenReturn(1.0);
        Mockito.when(mlc.upgrades.getGuidance()).thenReturn(oldGuidance);

        try {
            new CommandStack(0).pushAndApply(new CmdSetGuidanceType(xBar, mlc.loadout, newGuidance));
        }
        catch (final Throwable t) {
            /* No-Op */
        }

        Mockito.verify(mlc.upgrades, Mockito.never()).setGuidance(any(GuidanceUpgrade.class));
    }

    @Test
    public void testUndo() throws Exception {
        final Loadout loadout = TestHelpers.parse("lsml://rR4AEURNB1QScQtNB1REvqCEj9P37332SAXGzly5WoqI0fyo");
        final Loadout loadoutOriginal = TestHelpers.parse("lsml://rR4AEURNB1QScQtNB1REvqCEj9P37332SAXGzly5WoqI0fyo");
        final CommandStack stack = new CommandStack(1);

        stack.pushAndApply(new CmdSetGuidanceType(xBar, loadout, UpgradeDB.STD_GUIDANCE));
        stack.undo();

        assertEquals(loadoutOriginal, loadout);
    }
}
