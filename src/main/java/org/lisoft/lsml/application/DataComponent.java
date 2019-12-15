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
package org.lisoft.lsml.application;

import org.lisoft.lsml.model.database.DatabaseProvider;
import org.lisoft.lsml.model.export.Base64LoadoutCoder;
import org.lisoft.lsml.model.export.MWOCoder;

import dagger.Component;

/**
 * Interface that defines how a data {@link Component} should look.
 *
 * @author Emily Björk
 */
public interface DataComponent {

    ErrorReporter errorReporter();

    Base64LoadoutCoder loadoutCoder();

    MWOCoder mwoLoadoutCoder();

    DatabaseProvider mwoDatabaseProvider();
}
