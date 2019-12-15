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
package org.lisoft.lsml.view_fx.controllers.mainwindow;

import static org.lisoft.lsml.view_fx.util.FxTableUtils.*;

import java.util.*;
import java.util.function.Predicate;

import javax.inject.*;

import org.lisoft.lsml.messages.*;
import org.lisoft.lsml.model.chassi.*;
import org.lisoft.lsml.model.database.ChassisDB;
import org.lisoft.lsml.model.item.Faction;
import org.lisoft.lsml.model.loadout.*;
import org.lisoft.lsml.model.metrics.PayloadStatistics;
import org.lisoft.lsml.model.modifiers.Modifier;
import org.lisoft.lsml.view_fx.*;
import org.lisoft.lsml.view_fx.controllers.AbstractFXController;
import org.lisoft.lsml.view_fx.style.FilteredModifierFormatter;
import org.lisoft.lsml.view_fx.util.*;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/**
 * This is a controller class for the chassis page.
 *
 * @author Emily Björk
 */
public class ChassisPageController extends AbstractFXController {
    private static class ChassisFilter implements Predicate<DisplayLoadout> {
        private final Faction faction;
        private final boolean showVariants;

        public ChassisFilter(Faction aFaction, boolean aShowVariants) {
            faction = aFaction;
            showVariants = aShowVariants;
        }

        @Override
        public boolean test(DisplayLoadout aLoadout) {
            final Chassis chassis = aLoadout.loadout.getChassis();
            if (!showVariants && chassis.getVariantType().isVariation()) {
                return false;
            }
            return chassis.getFaction().isCompatible(faction);
        }
    }

    private final FilteredModifierFormatter modifierFormatter;
    private final Settings settings;
    @FXML
    private TableView<DisplayLoadout> tableLights;
    @FXML
    private TableView<DisplayLoadout> tableMediums;
    @FXML
    private TableView<DisplayLoadout> tableHeavies;
    @FXML
    private TableView<DisplayLoadout> tableAssaults;
    @FXML
    private LineChart<Double, Double> payloadGraph;
    @FXML
    private CheckBox payloadXLEngine;
    @FXML
    private CheckBox payloadEndoSteel;
    @FXML
    private CheckBox payloadFerroFibrous;
    @FXML
    private CheckBox payloadMaxArmour;
    @FXML
    private ListView<ChassisGroup> payloadChassis;
    private final MessageXBar globalXBar;
    private final LoadoutFactory loadoutFactory;
    private final ObjectBinding<Faction> factionFilter;
    @FXML
    private ToggleGroup factionFilterGroup;
    @FXML
    private RadioButton factionFilterAll;
    @FXML
    private RadioButton factionFilterIS;
    @FXML
    private RadioButton factionFilterClan;

    @Inject
    public ChassisPageController(Settings aSettings, @Named("global") MessageXBar aGlobalXBar,
            @Named("mainwindowFilterFormatter") FilteredModifierFormatter aModifierFormatter,
            LoadoutFactory aLoadoutFactory) {
        globalXBar = aGlobalXBar;
        settings = aSettings;
        modifierFormatter = aModifierFormatter;
        loadoutFactory = aLoadoutFactory;
        factionFilter = FxBindingUtils.createFactionBinding(factionFilterGroup.selectedToggleProperty(),
                factionFilterClan, factionFilterIS);

        setupChassisTable(tableLights, ChassisClass.LIGHT, factionFilter);
        setupChassisTable(tableMediums, ChassisClass.MEDIUM, factionFilter);
        setupChassisTable(tableHeavies, ChassisClass.HEAVY, factionFilter);
        setupChassisTable(tableAssaults, ChassisClass.ASSAULT, factionFilter);

        setupPayloadGraph();
        updateGraph();
    }

    private void openChassis(Chassis aChassis) {
        try {
            final Loadout loadout = loadoutFactory.produceDefault(aChassis, settings);
            globalXBar.post(new ApplicationMessage(loadout, ApplicationMessage.Type.OPEN_LOADOUT, root));
        }
        catch (final Exception e) {
            LiSongMechLab.showError(root, e);
        }
    }

    private void setupChassisTable(TableView<DisplayLoadout> aTable, ChassisClass aChassisClass,
            ObjectExpression<Faction> aFactionFilter) {

        setupTableData(aTable, aChassisClass, aFactionFilter);
        aTable.setRowFactory(aView -> {
            final TableRow<DisplayLoadout> tr = new TableRow<>();
            tr.setOnMouseClicked(aEvent -> {
                if (FxControlUtils.isDoubleClick(aEvent)) {
                    final DisplayLoadout item = tr.getItem();
                    if (item != null) {
                        openChassis(item.loadout.getChassis());
                    }
                }
            });
            return tr;
        });

        aTable.getColumns().clear();
        addAttributeColumn(aTable, "Name", "loadout.chassis.shortName", "Name of the chassis.");
        addAttributeColumn(aTable, "Mass", "loadout.chassis.massMax", "The maximal mass of the chassis.");
        addAttributeColumn(aTable, "Fctn", "loadout.chassis.faction.uiShortName", "The faction of the chassis.");
        addTopSpeedColumn(aTable);
        addHardPointsColumn(aTable, Location.RightArm);
        addHardPointsColumn(aTable, Location.RightTorso);
        addHardPointsColumn(aTable, Location.Head);
        addHardPointsColumn(aTable, Location.CenterTorso);
        addHardPointsColumn(aTable, Location.LeftTorso);
        addHardPointsColumn(aTable, Location.LeftArm);
        addAttributeColumn(aTable, "JJ", "loadout.jumpJetsMax", "The maximal number of jump jets for this chassis.");

        final TableColumn<DisplayLoadout, Collection<Modifier>> quirksCol = new TableColumn<>("Weapon Quirks");
        quirksCol.setCellValueFactory(aFeatures -> new ReadOnlyObjectWrapper<>(aFeatures.getValue().filteredModifiers));
        quirksCol.setCellFactory(aView -> new TableCell<DisplayLoadout, Collection<Modifier>>() {
            private final VBox box = new VBox();

            @Override
            protected void updateItem(Collection<Modifier> aObject, boolean aEmpty) {
                if (null != aObject && !aEmpty) {
                    box.getChildren().clear();
                    modifierFormatter.format(aObject, box.getChildren());
                    setGraphic(box);
                }
                else {
                    setGraphic(null);
                }
            }
        });
        quirksCol.setSortable(false);
        aTable.getColumns().add(quirksCol);
        addColumnToolTip(quirksCol, "A summary of the quirks that affect your damage stats.");

        setupSortable(aTable, 1, 2, 0);
    }

    /**
     *
     */
    private void setupPayloadGraph() {
        // Group all chassis by mass
        final Map<Integer, ChassisGroup> groups = new TreeMap<>();
        for (final Chassis chassis : ChassisDB.lookupAll()) {
            if (chassis.getVariantType().isVariation()) {
                continue;
            }
            final int mass = chassis.getMassMax();
            final ChassisGroup group = groups.computeIfAbsent(mass, x -> new ChassisGroup(mass + " tons"));
            group.add(chassis);
        }
        payloadChassis.getItems().setAll(groups.values());
        payloadChassis.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Setup axis
        payloadGraph.getXAxis().setLabel("Speed");
        payloadGraph.getYAxis().setLabel("Payload mass");
        payloadGraph.getData().clear();

        // Setup hooks to update the graphs when settings change
        final InvalidationListener il = aObservable -> {
            Platform.runLater(() -> {
                updateGraph();
            });
        };
        payloadXLEngine.selectedProperty().addListener(il);
        payloadEndoSteel.selectedProperty().addListener(il);
        payloadFerroFibrous.selectedProperty().addListener(il);
        payloadMaxArmour.selectedProperty().addListener(il);
        payloadChassis.getSelectionModel().getSelectedItems().addListener(il);
        factionFilter.addListener(il);
    }

    private void setupTableData(TableView<DisplayLoadout> aTable, ChassisClass aChassisClass,
            ObjectExpression<Faction> aFactionFilter) {
        final Property<Boolean> showMechVariants = settings.getBoolean(Settings.UI_MECH_VARIANTS);

        final ObservableList<DisplayLoadout> loadouts = FXCollections.observableArrayList();
        for (final Chassis chassis : ChassisDB.lookup(aChassisClass)) {
            loadouts.add(new DisplayLoadout(loadoutFactory.produceEmpty(chassis)));
        }

        final FilteredList<DisplayLoadout> filtered = new FilteredList<>(loadouts,
                new ChassisFilter(aFactionFilter.get(), showMechVariants.getValue()));
        aTable.setItems(filtered);

        showMechVariants.addListener((aObs, aOld, aNew) -> {
            filtered.setPredicate(new ChassisFilter(aFactionFilter.get(), aNew));
            // Don't consume event, others may listen for it too.
        });

        aFactionFilter.addListener((aObs, aOld, aNew) -> {
            filtered.setPredicate(new ChassisFilter(aNew, showMechVariants.getValue()));
            // Don't consume event, others may listen for it too.
        });
    }

    /**
     *
     */
    private void updateGraph() {
        final List<PayloadGrouping> dataGroups = new ArrayList<>();

        for (final ChassisGroup selectionGroup : payloadChassis.getSelectionModel().getSelectedItems()) {
            if (selectionGroup == null) {
                continue;
            }
            for (final Chassis chassis : selectionGroup) {
                if (!chassis.getFaction().isCompatible(factionFilter.get())) {
                    continue;
                }

                boolean consumed = false;
                for (final PayloadGrouping dataGroup : dataGroups) {
                    if (dataGroup.offer(chassis)) {
                        consumed = true;
                        break;
                    }
                }
                if (!consumed) {
                    // FIXME: Inject this
                    final PayloadStatistics statistics = new PayloadStatistics(payloadXLEngine.isSelected(),
                            payloadMaxArmour.isSelected(), payloadEndoSteel.isSelected(),
                            payloadFerroFibrous.isSelected());
                    dataGroups.add(new PayloadGrouping(chassis, statistics));
                }
            }
        }
        payloadGraph.getData().clear();
        for (final PayloadGrouping dataGroup : dataGroups) {
            dataGroup.addToGraph(payloadGraph);
        }

        FxGraphUtils.setTightBounds(payloadGraph.getXAxis(), payloadGraph.getYAxis(), 10.0, 5.0,
                payloadGraph.getData());

    }
}
