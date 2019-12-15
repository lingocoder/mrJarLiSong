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

import org.lisoft.lsml.model.NoSuchItemException;
import org.lisoft.lsml.model.chassi.Chassis;
import org.lisoft.lsml.model.chassi.ChassisStandard;
import org.lisoft.lsml.model.loadout.StockLoadout;
import org.lisoft.lsml.view_fx.LiSongMechLab;

/**
 * A database class that holds descriptions of all stock loadouts.
 *
 * @author Emily Björk
 */
public class StockLoadoutDB {
    private static final Map<Chassis, StockLoadout> stockloadouts;

    /**
     * A decision has been made to rely on static initializers for *DB classes. The motivation is that all items are
     * immutable, and this is the only way that allows providing global item constans such as ItemDB.AMS.
     */
    static {
        final Database database = LiSongMechLab.getDatabase()
                .orElseThrow(() -> new RuntimeException("Cannot run without database"));

        stockloadouts = new HashMap<>();
        for (final StockLoadout stock : database.getStockLoadouts()) {
            try {
                stockloadouts.put(stock.getChassis(), stock);
            }
            catch (final NoSuchItemException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Collection<StockLoadout> all() {
        return stockloadouts.values();
    }

    /**
     * Will find the stock loadout matching the given {@link ChassisStandard}.
     *
     * @param aChassis
     *            The {@link ChassisStandard} to get the stock loadout for.
     * @return A {@link StockLoadout} description of the stock loadout.
     * @throws NoSuchItemException
     *             if no stock loadout was found for the chassis.
     */
    public static StockLoadout lookup(Chassis aChassis) throws NoSuchItemException {
        final StockLoadout ans = stockloadouts.get(aChassis);
        if (null == ans) {
            throw new NoSuchItemException("No stock loadouts found for: " + aChassis);
        }
        return ans;
    }
}
