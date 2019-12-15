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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.*;
import org.junit.runner.RunWith;
import org.lisoft.lsml.messages.*;
import org.lisoft.lsml.messages.ArmourMessage.Type;
import org.lisoft.lsml.model.chassi.*;
import org.lisoft.lsml.model.database.*;
import org.lisoft.lsml.model.loadout.*;
import org.lisoft.lsml.util.CommandStack;

import junitparams.*;

/**
 * Test suite for {@link CmdLoadStock}.
 *
 * @author Emily Björk
 */
@SuppressWarnings("javadoc")
@RunWith(JUnitParamsRunner.class)
public class CmdLoadStockTest {
    private static final Set<Chassis> PGI_BROKE_ME = new HashSet<>(
            Arrays.asList(ChassisDB.lookup("JR7-IIC-FY"), ChassisDB.lookup("BSW-HR"), ChassisDB.lookup("CP-10-Q")));
    private MessageXBar xBar;

    private final LoadoutFactory loadoutFactory = new DefaultLoadoutFactory();

    public Object[] allChassis() {
        final List<Chassis> chassii = new ArrayList<>();
        chassii.addAll(ChassisDB.lookup(ChassisClass.LIGHT));
        chassii.addAll(ChassisDB.lookup(ChassisClass.MEDIUM));
        chassii.addAll(ChassisDB.lookup(ChassisClass.HEAVY));
        chassii.addAll(ChassisDB.lookup(ChassisClass.ASSAULT));
        return chassii.stream().filter(c -> !PGI_BROKE_ME.contains(c)).collect(Collectors.toList()).toArray();
    }

    @Before
    public void setup() {
        xBar = mock(MessageXBar.class);
    }

    /**
     * Loading stock configuration shall produce a complete loadout for all chassis
     *
     * @param aChassis
     *            Chassis to test on.
     */
    @Test
    @Parameters(method = "allChassis")
    public void testApply(Chassis aChassis) throws Exception {
        // Setup
        final Loadout loadout = loadoutFactory.produceEmpty(aChassis);

        // Execute
        final CommandStack opstack = new CommandStack(0);
        opstack.pushAndApply(new CmdLoadStock(aChassis, loadout, xBar));

        // Verify (What the hell is up with the misery's stock loadout with almost one
        // ton free mass and not full armour?!)
        assertTrue(loadout.getFreeMass() < 0.5 || loadout.getName().contains("STK-M") && loadout.getFreeMass() < 1);
        for (final ConfiguredComponent part : loadout.getComponents()) {
            verify(xBar, atLeast(1)).post(new ArmourMessage(part, Type.ARMOUR_CHANGED, true));
        }
        verify(xBar, atLeast(1)).post(isA(ItemMessage.class));
    }

    /**
     * Actuator state shall be set on arms for OmniMechs.
     */
    @Test
    public void testApply_ActuatorState() throws Exception {
        // Setup
        final LoadoutOmniMech loadout = (LoadoutOmniMech) loadoutFactory.produceEmpty(ChassisDB.lookup("SCR-PRIME(S)"));

        // Execute
        final CommandStack opstack = new CommandStack(0);
        opstack.pushAndApply(new CmdLoadStock(loadout.getChassis(), loadout, xBar));

        assertFalse(loadout.getComponent(Location.LeftArm).getToggleState(ItemDB.HA));
        assertFalse(loadout.getComponent(Location.LeftArm).getToggleState(ItemDB.LAA));
        assertTrue(loadout.getComponent(Location.RightArm).getToggleState(ItemDB.HA));
        assertTrue(loadout.getComponent(Location.RightArm).getToggleState(ItemDB.LAA));
    }

    /**
     * Loading stock shall handle Artemis changes on February 4th patch.
     */
    @Test
    public void testApply_artemisFeb4() throws Exception {
        // Setup
        final Loadout loadout = loadoutFactory.produceEmpty(ChassisDB.lookup("CN9-D"));

        // Execute
        final CommandStack opstack = new CommandStack(0);
        opstack.pushAndApply(new CmdLoadStock(loadout.getChassis(), loadout, xBar));

        assertTrue(loadout.getComponent(Location.LeftTorso).getItemsEquipped()
                .contains(ItemDB.lookup("LRM 10 + ARTEMIS")));
    }

    /**
     * Loading stock shall succeed even if there is an automatic armour distribution going on.
     */
    @Test
    public void testApply_InPresenceOfAutomaticArmour() throws Exception {
        // Setup
        final Loadout loadout = loadoutFactory.produceStock(ChassisDB.lookup("BNC-3S"));
        final CommandStack stack = new CommandStack(0);

        doAnswer(aInvocation -> {
            final Message aMsg = (Message) aInvocation.getArguments()[0];
            if (aMsg.isForMe(loadout) && aMsg instanceof ArmourMessage) {
                final ArmourMessage message = (ArmourMessage) aMsg;
                if (!message.manualArmour) {
                    return null;
                }
                stack.pushAndApply(new CmdDistributeArmour(loadout, loadout.getChassis().getArmourMax(), 10, xBar));
            }
            return null;
        }).when(xBar).post(any(ArmourMessage.class));

        // Execute
        final CmdLoadStock cut = new CmdLoadStock(loadout.getChassis(), loadout, xBar);
        stack.pushAndApply(cut);

        // Verify
        assertEquals(95.0, loadout.getMass(), 0.01);
        assertEquals(480, loadout.getArmour());
    }

    /**
     * Loading stock configuration shall succeed even if the loadout isn't empty to start with.
     */
    @Test
    public void testNotEmpty() throws Exception {
        // Setup
        final ChassisStandard chassi = (ChassisStandard) ChassisDB.lookup("JR7-F");
        final Loadout loadout = loadoutFactory.produceStock(chassi);
        final CommandStack opstack = new CommandStack(0);
        assertTrue(loadout.getMass() > 34.9);

        // Execute
        opstack.pushAndApply(new CmdLoadStock(chassi, loadout, xBar));
    }

    /**
     * Undoing load stock shall produce previous loadout.
     */
    @Test
    public void testUndo() throws Exception {
        // Setup
        final ChassisStandard chassi = (ChassisStandard) ChassisDB.lookup("JR7-F");
        final LoadoutStandard reference = (LoadoutStandard) loadoutFactory.produceEmpty(chassi);
        final LoadoutStandard loadout = (LoadoutStandard) loadoutFactory.produceEmpty(chassi);
        final CommandStack opstack = new CommandStack(1);
        opstack.pushAndApply(new CmdLoadStock(loadout.getChassis(), loadout, xBar));

        // Execute
        opstack.undo();

        // Verify
        assertEquals(reference, loadout);
    }

    /**
     * Loading stock configuration shall succeed even if the loadout as armour set.
     */
    @Test
    public void testWithArmour() throws Exception {
        // Setup
        final ChassisStandard chassi = (ChassisStandard) ChassisDB.lookup("LCT-3S");
        final Loadout loadout = loadoutFactory.produceEmpty(chassi);

        new CmdSetMaxArmour(loadout, null, 4.0, false).apply();

        // Execute
        new CmdLoadStock(chassi, loadout, xBar).apply();

        assertTrue(loadout.getMass() > 19.8);
    }
}
