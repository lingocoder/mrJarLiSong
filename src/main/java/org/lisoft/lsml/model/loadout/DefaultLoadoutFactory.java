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
package org.lisoft.lsml.model.loadout;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.lisoft.lsml.command.CmdDistributeArmour;
import org.lisoft.lsml.command.CmdLoadStock;
import org.lisoft.lsml.model.NoSuchItemException;
import org.lisoft.lsml.model.chassi.ArmourSide;
import org.lisoft.lsml.model.chassi.Chassis;
import org.lisoft.lsml.model.chassi.ChassisOmniMech;
import org.lisoft.lsml.model.chassi.ChassisStandard;
import org.lisoft.lsml.model.chassi.ComponentStandard;
import org.lisoft.lsml.model.chassi.Location;
import org.lisoft.lsml.model.chassi.OmniPod;
import org.lisoft.lsml.model.database.ItemDB;
import org.lisoft.lsml.model.database.OmniPodDB;
import org.lisoft.lsml.model.database.UpgradeDB;
import org.lisoft.lsml.model.item.Consumable;
import org.lisoft.lsml.model.item.Faction;
import org.lisoft.lsml.model.item.Item;
import org.lisoft.lsml.model.upgrades.ArmourUpgrade;
import org.lisoft.lsml.model.upgrades.HeatSinkUpgrade;
import org.lisoft.lsml.model.upgrades.StructureUpgrade;
import org.lisoft.lsml.model.upgrades.Upgrade;
import org.lisoft.lsml.model.upgrades.Upgrades;
import org.lisoft.lsml.model.upgrades.UpgradesMutable;
import org.lisoft.lsml.util.CommandStack;
import org.lisoft.lsml.view_fx.Settings;

/**
 * This class produces loadouts as they are typically used by the application.
 *
 * @author Emily Björk
 *
 */
@Singleton
public class DefaultLoadoutFactory implements LoadoutFactory {
    private final CommandStack stack = new CommandStack(0);

    @Inject
    public DefaultLoadoutFactory() {
        // NOP
    }

    @Override
    public Loadout produceClone(Loadout aSource) {
        final Loadout target = produceEmpty(aSource.getChassis());
        target.setName(aSource.getName());

        // Base attributes
        target.getWeaponGroups().assign(aSource.getWeaponGroups());
        target.getEfficiencies().assign(aSource.getEfficiencies());
        target.getUpgrades().assign(aSource.getUpgrades());

        // Modules
        for (final Consumable module : aSource.getConsumables()) {
            target.addModule(module);
        }

        for (final ConfiguredComponent srcCmpnt : aSource.getComponents()) {
            final Location loc = srcCmpnt.getInternalComponent().getLocation();
            final ConfiguredComponent tgtCmpnt = target.getComponent(loc);

            // Omnipod + Actuator
            if (srcCmpnt instanceof ConfiguredComponentOmniMech) {
                final ConfiguredComponentOmniMech omniSourceComponent = (ConfiguredComponentOmniMech) srcCmpnt;
                final ConfiguredComponentOmniMech omniTargetComponent = (ConfiguredComponentOmniMech) tgtCmpnt;
                if (!omniTargetComponent.getInternalComponent().hasFixedOmniPod()) {
                    omniTargetComponent.changeOmniPod(omniSourceComponent.getOmniPod());
                }

                matchToggleState(omniTargetComponent, omniSourceComponent, ItemDB.HA);
                matchToggleState(omniTargetComponent, omniSourceComponent, ItemDB.LAA);
            }

            // Armour
            for (final ArmourSide side : ArmourSide.allSides(srcCmpnt.getInternalComponent())) {
                tgtCmpnt.setArmour(side, srcCmpnt.getArmour(side), srcCmpnt.hasManualArmour());
            }

            // Equipment
            for (final Item item : srcCmpnt.getItemsEquipped()) {
                tgtCmpnt.addItem(item);
            }
        }
        return target;
    }

    @Override
    public Loadout produceDefault(Chassis aChassis, Settings aSettings) {
        final Loadout ans = produceEmpty(aChassis);
        final Faction faction = ans.getChassis().getFaction();

        if (aSettings.getBoolean(Settings.UPGRADES_DEFAULT_ARTEMIS).getValue()) {
            ans.getUpgrades().setGuidance(UpgradeDB.getGuidance(faction, true));
        }

        if (ans instanceof LoadoutStandard) {
            final LoadoutStandard loadoutStandard = (LoadoutStandard) ans;
            final UpgradesMutable upgrades = loadoutStandard.getUpgrades();

            final String armourKey = faction == Faction.CLAN ? Settings.UPGRADES_DEFAULT_CLAN_ARMOUR
                    : Settings.UPGRADES_DEFAULT_IS_ARMOUR;
            final String structureKey = faction == Faction.CLAN ? Settings.UPGRADES_DEFAULT_CLAN_STRUCTURE
                    : Settings.UPGRADES_DEFAULT_IS_STRUCTURE;
            final String heatSinksKey = faction == Faction.CLAN ? Settings.UPGRADES_DEFAULT_CLAN_HEAT_SINKS
                    : Settings.UPGRADES_DEFAULT_IS_HEAT_SINKS;

            try {
                final Upgrade armour = UpgradeDB.lookup(aSettings.getInteger(armourKey).getValue());
                final Upgrade structure = UpgradeDB.lookup(aSettings.getInteger(structureKey).getValue());
                final Upgrade heatSink = UpgradeDB.lookup(aSettings.getInteger(heatSinksKey).getValue());

                upgrades.setStructure((StructureUpgrade) structure);
                upgrades.setArmour((ArmourUpgrade) armour);
                upgrades.setHeatSink((HeatSinkUpgrade) heatSink);
            }
            catch (final NoSuchItemException e) {
                throw new RuntimeException(e);
            }
        }

        if (aSettings.getBoolean(Settings.MAX_ARMOUR).getValue()) {
            final int ratio = aSettings.getInteger(Settings.ARMOUR_RATIO).getValue();
            final CmdDistributeArmour cmd = new CmdDistributeArmour(ans, ans.getChassis().getArmourMax(), ratio, null);
            try {
                stack.pushAndApply(cmd);
            }
            catch (final Exception e) {
                throw new AssertionError("Armour distribution failed when it shouldn't be possible", e);
            }
        }

        return ans;
    }

    @Override
    public Loadout produceEmpty(Chassis aChassis) {
        if (aChassis instanceof ChassisStandard) {
            final ChassisStandard chassis = (ChassisStandard) aChassis;
            final Faction faction = aChassis.getFaction();
            final UpgradesMutable upgrades = new UpgradesMutable(UpgradeDB.getDefaultArmour(faction),
                    UpgradeDB.getDefaultStructure(faction), UpgradeDB.STD_GUIDANCE,
                    UpgradeDB.getDefaultHeatSinks(faction));

            final ConfiguredComponentStandard[] components = new ConfiguredComponentStandard[Location.values().length];
            for (final ComponentStandard component : chassis.getComponents()) {
                components[component.getLocation().ordinal()] = new ConfiguredComponentStandard(component, false);
            }

            return new LoadoutStandard(components, chassis, upgrades, new WeaponGroups());
        }
        else if (aChassis instanceof ChassisOmniMech) {
            final ChassisOmniMech chassis = (ChassisOmniMech) aChassis;
            final Upgrades upgrades = new Upgrades(chassis.getFixedArmourType(), chassis.getFixedStructureType(),
                    UpgradeDB.STD_GUIDANCE, chassis.getFixedHeatSinkType());

            final ConfiguredComponentOmniMech[] components = new ConfiguredComponentOmniMech[Location.values().length];
            for (final Location location : Location.values()) {
                final Optional<OmniPod> pod = OmniPodDB.lookupStock(chassis, location);

                final ConfiguredComponentOmniMech component;
                if (pod.isPresent()) {
                    component = new ConfiguredComponentOmniMech(chassis.getComponent(location), false, pod.get());
                }
                else {
                    component = new ConfiguredComponentOmniMech(chassis.getComponent(location), false);
                }
                components[location.ordinal()] = component;
            }
            return new LoadoutOmniMech(components, chassis, upgrades, new WeaponGroups());
        }
        throw new IllegalArgumentException("Unknown chassis type!");
    }

    @Override
    public Loadout produceStock(Chassis aChassis) throws Exception {
        final Loadout ans = produceEmpty(aChassis);
        stack.pushAndApply(new CmdLoadStock(aChassis, ans, null));
        return ans;
    }

    private void matchToggleState(ConfiguredComponentOmniMech aTarget, ConfiguredComponentOmniMech aSource,
            Item aItem) {
        if (EquipResult.SUCCESS == aTarget.canToggleOn(aItem)) {
            aTarget.setToggleState(aItem, aSource.getToggleState(aItem));
        }
    }
}
