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
package org.lisoft.lsml.view_fx.properties;

import java.util.function.Predicate;

import javax.inject.Inject;
import javax.inject.Named;

import org.lisoft.lsml.messages.ItemMessage;
import org.lisoft.lsml.messages.LoadoutMessage;
import org.lisoft.lsml.messages.LoadoutMessage.Type;
import org.lisoft.lsml.messages.Message;
import org.lisoft.lsml.messages.MessageXBar;
import org.lisoft.lsml.messages.OmniPodMessage;
import org.lisoft.lsml.messages.PilotSkillMessage;
import org.lisoft.lsml.model.chassi.MovementProfile;
import org.lisoft.lsml.model.environment.Environment;
import org.lisoft.lsml.model.loadout.Loadout;
import org.lisoft.lsml.model.loadout.WeaponGroups;
import org.lisoft.lsml.model.metrics.AlphaHeat;
import org.lisoft.lsml.model.metrics.AlphaHeatPercent;
import org.lisoft.lsml.model.metrics.AlphaStrike;
import org.lisoft.lsml.model.metrics.ArmRotatePitchSpeed;
import org.lisoft.lsml.model.metrics.ArmRotateYawSpeed;
import org.lisoft.lsml.model.metrics.BurstDamageOverTime;
import org.lisoft.lsml.model.metrics.BurstHeat;
import org.lisoft.lsml.model.metrics.CoolingRatio;
import org.lisoft.lsml.model.metrics.GhostHeat;
import org.lisoft.lsml.model.metrics.HeatCapacity;
import org.lisoft.lsml.model.metrics.HeatDissipation;
import org.lisoft.lsml.model.metrics.HeatGeneration;
import org.lisoft.lsml.model.metrics.HeatOverTime;
import org.lisoft.lsml.model.metrics.MASCSpeed;
import org.lisoft.lsml.model.metrics.MaxDPS;
import org.lisoft.lsml.model.metrics.MaxDPSTimeToOverHeat;
import org.lisoft.lsml.model.metrics.MaxSustainedDPS;
import org.lisoft.lsml.model.metrics.TimeToCool;
import org.lisoft.lsml.model.metrics.TopSpeed;
import org.lisoft.lsml.model.metrics.TorsoTwistPitchSpeed;
import org.lisoft.lsml.model.metrics.TorsoTwistYawSpeed;
import org.lisoft.lsml.model.metrics.TurningSpeed;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * This class wraps all the metrics that are calculated for a {@link Loadout} in a JavaFX friendly manner.
 *
 * @author Emily Björk
 */
public class LoadoutMetrics {
    public static class GroupMetrics {
        public final RangeMetricBinding<AlphaStrike> alphaDamage;
        public final MetricBinding<GhostHeat> alphaGhostHeat;
        public final MetricBinding<AlphaHeat> alphaHeat; // Doesn't include ghost heat
        public final MetricBinding<AlphaHeatPercent> alphaHeatPct; // Includes ghost heat

        public final RangeTimeMetricBinding<BurstDamageOverTime> burstDamage;
        public final MetricBinding<BurstHeat> burstHeat;

        public final RangeMetricBinding<MaxDPS> maxDPS;
        public final MetricBinding<CoolingRatio> maxDPSCoolingRatio;
        public final MetricBinding<MaxDPSTimeToOverHeat> maxDPSTtO;
        public final RangeMetricBinding<MaxSustainedDPS> sustainedDPS;

        public GroupMetrics(MessageXBar aRcv, Loadout aLoadout, int aGroup, HeatCapacity aHeatCapacity,
                HeatDissipation aHeatDissipation, Predicate<Message> aFilter) {
            final HeatGeneration heatGeneration = new HeatGeneration(aLoadout, aGroup);
            final HeatOverTime heatOverTime = new HeatOverTime(aLoadout, aRcv, aGroup);

            maxDPSTtO = new MetricBinding<>(aRcv,
                    new MaxDPSTimeToOverHeat(aHeatCapacity, heatOverTime, aHeatDissipation), aFilter);
            alphaGhostHeat = new MetricBinding<>(aRcv, new GhostHeat(aLoadout, aGroup), aFilter);
            alphaDamage = new RangeMetricBinding<>(aRcv, new AlphaStrike(aLoadout, aGroup), aFilter);
            alphaHeat = new MetricBinding<>(aRcv, new AlphaHeat(aLoadout, aGroup), aFilter);
            alphaHeatPct = new MetricBinding<>(aRcv, new AlphaHeatPercent(alphaHeat.getMetric(),
                    alphaGhostHeat.getMetric(), aHeatDissipation, aHeatCapacity, aLoadout, aGroup), aFilter);
            final BurstDamageOverTime burstDamageOverTime = new BurstDamageOverTime(aLoadout, aRcv, aGroup);
            burstDamage = new RangeTimeMetricBinding<>(aRcv, burstDamageOverTime, aFilter);
            burstHeat = new MetricBinding<>(aRcv, new BurstHeat(burstDamageOverTime, heatOverTime), aFilter);
            maxDPS = new RangeMetricBinding<>(aRcv, new MaxDPS(aLoadout, aGroup), aFilter);
            sustainedDPS = new RangeMetricBinding<>(aRcv, new MaxSustainedDPS(aLoadout, aHeatDissipation, aGroup),
                    aFilter);
            maxDPSCoolingRatio = new MetricBinding<>(aRcv, new CoolingRatio(aHeatDissipation, heatGeneration), aFilter);
        }

        /**
         * @param aRange
         *            The new range, or -1 for optimal.
         */
        public void changeRange(Double aRange) {
            alphaDamage.setUserRange(aRange);
            maxDPS.setUserRange(aRange);
            sustainedDPS.setUserRange(aRange);
            burstDamage.setUserRange(aRange);
        }

        /**
         * @param aTime
         *            The new time to use for time dependent metrics.
         */
        public void changeTime(double aTime) {
            burstDamage.getMetric().changeTime(aTime);
        }
    }

    private static final double DEFAULT_BURST_TIME = 5.0;

    private static final Double DEFAULT_RANGE = null;

    public final GroupMetrics alphaGroup;

    public final DoubleBinding armPitch;
    public final MetricBinding<ArmRotatePitchSpeed> armPitchSpeed;
    public final DoubleBinding armYaw;
    public final MetricBinding<ArmRotateYawSpeed> armYawSpeed;
    // Offensive
    @Deprecated
    public final DoubleProperty burstTime = new SimpleDoubleProperty();
    public final ObjectProperty<Environment> environmentProperty = new SimpleObjectProperty<>();
    public final MetricBinding<HeatCapacity> heatCapacity;

    public final MetricBinding<HeatDissipation> heatDissipation;
    // Heat
    public final IntegerBinding heatSinkCount;
    public final IntegerBinding jumpJetCount;
    public final IntegerBinding jumpJetMax;
    public final MetricBinding<MASCSpeed> mascSpeed;
    @Deprecated
    public final ObjectProperty<Double> range = new SimpleObjectProperty<>();

    public final MetricBinding<TimeToCool> timeToCool;
    // Mobility
    public final MetricBinding<TopSpeed> topSpeed;
    public final DoubleBinding torsoPitch;
    public final MetricBinding<TorsoTwistPitchSpeed> torsoPitchSpeed;

    public final DoubleBinding torsoYaw;
    public final MetricBinding<TorsoTwistYawSpeed> torsoYawSpeed;
    public final MetricBinding<TurningSpeed> turnSpeed;

    public final GroupMetrics[] weaponGroups = new GroupMetrics[WeaponGroups.MAX_WEAPONS];
    private final MessageXBar xBar;

    @Inject
    public LoadoutMetrics(Loadout aLoadout, @Named("local") MessageXBar aRcv) {
        xBar = aRcv;
        final Environment aEnvironment = Environment.NEUTRAL;
        final MovementProfile mp = aLoadout.getMovementProfile();
        // Update predicates
        final Predicate<Message> itemsChanged = (aMsg) -> aMsg instanceof ItemMessage;
        final Predicate<Message> effsChanged = (aMsg) -> aMsg instanceof PilotSkillMessage;
        final Predicate<Message> omniPodChanged = (aMsg) -> aMsg instanceof OmniPodMessage;
        final Predicate<Message> affectsHeatOrDamage = (aMsg) -> aMsg.affectsHeatOrDamage();
        final Predicate<Message> engineOrEffsChanged = (aMsg) -> itemsChanged.test(aMsg) || effsChanged.test(aMsg);
        final Predicate<Message> itemsOrPodsChanged = (aMsg) -> itemsChanged.test(aMsg) || omniPodChanged.test(aMsg);

        // Mobility
        topSpeed = new MetricBinding<>(aRcv, new TopSpeed(aLoadout), engineOrEffsChanged);
        mascSpeed = new MetricBinding<>(aRcv, new MASCSpeed(aLoadout, topSpeed.getMetric()), engineOrEffsChanged);
        turnSpeed = new MetricBinding<>(aRcv, new TurningSpeed(aLoadout), engineOrEffsChanged);
        torsoPitchSpeed = new MetricBinding<>(aRcv, new TorsoTwistPitchSpeed(aLoadout), engineOrEffsChanged);
        torsoYawSpeed = new MetricBinding<>(aRcv, new TorsoTwistYawSpeed(aLoadout), engineOrEffsChanged);
        armPitchSpeed = new MetricBinding<>(aRcv, new ArmRotatePitchSpeed(aLoadout), engineOrEffsChanged);
        armYawSpeed = new MetricBinding<>(aRcv, new ArmRotateYawSpeed(aLoadout), engineOrEffsChanged);

        jumpJetCount = new LsmlIntegerBinding(aRcv, aLoadout::getJumpJetCount, itemsOrPodsChanged);
        jumpJetMax = new LsmlIntegerBinding(aRcv, aLoadout::getJumpJetsMax, itemsOrPodsChanged);
        torsoPitch = new LsmlDoubleBinding(aRcv, () -> mp.getTorsoPitchMax(aLoadout.getAllModifiers()),
                engineOrEffsChanged);
        torsoYaw = new LsmlDoubleBinding(aRcv, () -> mp.getTorsoYawMax(aLoadout.getAllModifiers()),
                engineOrEffsChanged);
        armPitch = new LsmlDoubleBinding(aRcv, () -> mp.getArmPitchMax(aLoadout.getAllModifiers()),
                engineOrEffsChanged);
        armYaw = new LsmlDoubleBinding(aRcv, () -> mp.getArmYawMax(aLoadout.getAllModifiers()), engineOrEffsChanged);

        // Heat
        heatSinkCount = new LsmlIntegerBinding(aRcv, () -> aLoadout.getHeatsinksCount(), itemsOrPodsChanged);
        heatCapacity = new MetricBinding<>(aRcv, new HeatCapacity(aLoadout, aEnvironment), affectsHeatOrDamage);
        heatDissipation = new MetricBinding<>(aRcv, new HeatDissipation(aLoadout, aEnvironment), affectsHeatOrDamage);
        timeToCool = new MetricBinding<>(aRcv, new TimeToCool(heatCapacity.getMetric(), heatDissipation.getMetric()),
                affectsHeatOrDamage);

        alphaGroup = new GroupMetrics(xBar, aLoadout, -1, heatCapacity.getMetric(), heatDissipation.getMetric(),
                affectsHeatOrDamage);

        for (int i = 0; i < WeaponGroups.MAX_GROUPS; ++i) {
            weaponGroups[i] = new GroupMetrics(xBar, aLoadout, i, heatCapacity.getMetric(), heatDissipation.getMetric(),
                    affectsHeatOrDamage);
        }

        burstTime.addListener((aObservable, aOld, aNew) -> {
            alphaGroup.changeTime(aNew.doubleValue());
            for (int i = 0; i < WeaponGroups.MAX_GROUPS; ++i) {
                weaponGroups[i].changeTime(aNew.doubleValue());
            }
            updateHeatAndDamageMetrics();
        });
        burstTime.set(DEFAULT_BURST_TIME);

        range.addListener((aObservable, aOld, aNew) -> {
            alphaGroup.changeRange(aNew);
            for (int i = 0; i < WeaponGroups.MAX_GROUPS; ++i) {
                weaponGroups[i].changeRange(aNew);
            }
            updateHeatAndDamageMetrics();
        });

        range.set(DEFAULT_RANGE);

        environmentProperty.addListener((aObservable, aOld, aNew) -> {
            heatDissipation.getMetric().changeEnvironment(aNew);
            heatCapacity.getMetric().changeEnvironment(aNew);
            updateHeatAndDamageMetrics();
        });
    }

    private void updateHeatAndDamageMetrics() {
        xBar.post(new LoadoutMessage(null, Type.UPDATE));
    }
}
