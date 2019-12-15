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
package org.lisoft.lsml.view_fx;

import javax.inject.Singleton;

import org.lisoft.lsml.application.ErrorReporter;
import org.lisoft.lsml.model.database.DatabaseProvider;

import dagger.Binds;
import dagger.Module;

/**
 * This Dagger 2 {@link Module} provides the necessary data dependencies specialised for the JavaFX GUI application.
 *
 * @author Emily Björk
 */
@Module
public abstract class FXDataModule {
    @Singleton
    @Binds
    abstract DatabaseProvider provideDatabaseProvider(FXDatabaseProvider aFxProvider);

    @Singleton
    @Binds
    abstract ErrorReporter provideErrorReporter(DialogErrorReporter aErrorReporter);
}
