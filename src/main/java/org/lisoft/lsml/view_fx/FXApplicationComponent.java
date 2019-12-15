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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Optional;

import javax.inject.*;

import org.lisoft.lsml.application.*;
import org.lisoft.lsml.messages.MessageXBar;
import org.lisoft.lsml.model.export.*;
import org.lisoft.lsml.model.loadout.LoadoutFactory;
import org.lisoft.lsml.view_fx.controllers.*;

import dagger.Component;

/**
 * This dagger Component provides the services necessary for the main application.
 *
 * @author Emily Björk
 */
@Singleton
@Component(dependencies = DataComponent.class, modules = { BaseModule.class, FXMainModule.class })
public interface FXApplicationComponent {
    GlobalGarage garage();

    Optional<LsmlProtocolIPC> ipc();

    LinkPresenter linkPresenter();

    LoadoutFactory loadoutFactory();

    MainWindowController mainWindow();

    MechlabSubComponent mechlabComponent(FXMechlabModule aMechlabModule);

    @Named("global")
    MessageXBar messageXBar();

    OSIntegration osIntegration();

    Settings settings();

    SmurfyImportExport smurfyImportExport();

    // TODO: Put splash in a sub/dep-component and tie the lifetime to that component
    SplashScreenController splash();

    UncaughtExceptionHandler uncaughtExceptionHandler();

    // void inject(MainWindowController aMainWindowController);

    Optional<UpdateChecker> updateChecker();
}
