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
package org.lisoft.lsml.model;

/**
 * An exception throw by the various database objects when a lookup fails.
 *
 * @author Emily Björk
 */
public class NoSuchItemException extends Exception {
    private static final long serialVersionUID = -4566933571289442372L;

    public NoSuchItemException() {
        // NOP
    }

    public NoSuchItemException(String aMessage) {
        super(aMessage);
        // NOP
    }

    public NoSuchItemException(String aMessage, Throwable aCause) {
        super(aMessage, aCause);
        // NOP
    }

    public NoSuchItemException(String aMessage, Throwable aCause, boolean aEnableSuppression,
            boolean aWritableStackTrace) {
        super(aMessage, aCause, aEnableSuppression, aWritableStackTrace);
        // NOP
    }

    public NoSuchItemException(Throwable aCause) {
        super(aCause);
        // NOP
    }
}
