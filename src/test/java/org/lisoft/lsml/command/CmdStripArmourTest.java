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
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lisoft.lsml.messages.MessageDelivery;
import org.lisoft.lsml.model.database.ChassisDB;
import org.lisoft.lsml.model.loadout.ConfiguredComponent;
import org.lisoft.lsml.model.loadout.DefaultLoadoutFactory;
import org.lisoft.lsml.model.loadout.Loadout;
import org.lisoft.lsml.model.loadout.LoadoutFactory;
import org.lisoft.lsml.util.CommandStack;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Test suite for {@link CmdStripArmour}.
 *
 * @author Emily Björk
 */
@SuppressWarnings("javadoc")
@RunWith(MockitoJUnitRunner.Silent.class)
public class CmdStripArmourTest {
    private final LoadoutFactory loadoutFactory = new DefaultLoadoutFactory();
    @Mock
    private MessageDelivery messageDelivery;

    /**
     * Stripping a loadout shall remove all upgrades, items and armour.
     */
    @Test
    public void testStrip() throws Exception {
        // Setup
        final Loadout loadout = loadoutFactory.produceStock(ChassisDB.lookup("AS7-BH"));
        // Has Endo-Steel standard and lots of stuff

        assertTrue(loadout.getMass() > 99.0);

        // Execute
        final CommandStack opStack = new CommandStack(0);
        opStack.pushAndApply(new CmdStripArmour(loadout, messageDelivery));

        // Verify
        for (final ConfiguredComponent loadoutPart : loadout.getComponents()) {
            assertEquals(0, loadoutPart.getArmourTotal());
        }
    }

}
