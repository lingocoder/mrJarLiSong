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
package org.lisoft.lsml.model.modifiers;

/**
 * This attribute defines how a modifier is applied.
 *
 * The formula to use is: modifiedValue = (baseValue + sum(additive)) * (1.0 + sum(multiplicative)).
 *
 * Source: Email conversation with Brian Buckton @ PGI.
 *
 * @author Emily Björk
 */
public enum Operation {
    ADD, MUL;

    public static Operation fromString(String aString) {
        final String canon = aString.toLowerCase();
        if (canon.contains("mult") || aString.contains("*")) {
            return MUL;
        }
        else if (canon.contains("add") || aString.contains("+")) {
            return ADD;
        }
        else {
            throw new IllegalArgumentException("Unknown operation: " + aString);
        }
    }

    @Override
    public String toString() {
        if (this == ADD) {
            return "+";
        }
        return "*";
    }

    /**
     * @return The name of the operation as used when looking up the modifier in the UI translation table.
     */
    public String uiAbbrev() {
        switch (this) {
            case ADD:
                return "add";
            case MUL:
                return "mult";
            default:
                throw new RuntimeException("Unknown modifier!");
        }
    }
}