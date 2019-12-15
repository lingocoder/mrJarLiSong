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
package org.lisoft.lsml.model.item;

import java.util.Collection;

import org.lisoft.lsml.model.chassi.HardPointType;
import org.lisoft.lsml.model.modifiers.*;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Base class for weapons that consume ammunition.
 *
 * @author Emily Björk
 *
 */
public class AmmoWeapon extends Weapon {
    @XStreamAsAttribute
    private final String ammoTypeId;
    @XStreamAsAttribute
    private final boolean oneShot;

    /**
     * This field will be set through reflection in a post-processing pass.
     */
    @XStreamAsAttribute
    private final Ammunition ammoType = null;

    /**
     * This field will be set through reflection in a post-processing pass.
     */
    @XStreamAsAttribute
    private final Ammunition ammoHalfType = null;

    public AmmoWeapon(
            // Item Arguments
            String aName, String aDesc, String aMwoName, int aMwoId, int aSlots, double aTons,
            HardPointType aHardPointType, double aHP, Faction aFaction,
            // HeatSource Arguments
            Attribute aHeat,
            // Weapon Arguments
            Attribute aCoolDown, WeaponRangeProfile aRangeProfile, int aRoundsPerShot, double aDamagePerProjectile,
            int aProjectilesPerRound, Attribute aProjectileSpeed, int aGhostHeatGroupId, double aGhostHeatMultiplier,
            Attribute aGhostHeatMaxFreeAlpha, double aVolleyDelay, double aImpulse,
            // AmmoWeapon Arguments
            String aAmmoType, boolean aOneShot) {
        super(aName, aDesc, aMwoName, aMwoId, aSlots, aTons, aHardPointType, aHP, aFaction, aHeat, aCoolDown,
                aRangeProfile, aRoundsPerShot, aDamagePerProjectile, aProjectilesPerRound, aProjectileSpeed,
                aGhostHeatGroupId, aGhostHeatMultiplier, aGhostHeatMaxFreeAlpha, aVolleyDelay, aImpulse);
        ammoTypeId = aAmmoType;
        oneShot = aOneShot;
    }

    public Ammunition getAmmoHalfType() {
        return ammoHalfType;
    }

    public boolean isOneShot() {
        return oneShot;
    }

    /**
     * @return The {@link String} name of the ammo type required for this weapon.
     */
    public String getAmmoId() {
        return ammoTypeId;
    }

    public Ammunition getAmmoType() {
        return ammoType;
    }

    public int getBuiltInRounds() {
        return hasBuiltInAmmo() ? getAmmoPerPerShot() : 0;
    }

    @Override
    public double getCoolDown(Collection<Modifier> aModifiers) {
        return isOneShot() ? Double.POSITIVE_INFINITY : super.getCoolDown(aModifiers);
    }

    @Override
    public double getSecondsPerShot(Collection<Modifier> aModifiers) {
        return isOneShot() ? Double.POSITIVE_INFINITY : super.getSecondsPerShot(aModifiers);
    }

    /**
     * @return <code>true</code> if the weapon has builtin ammo.
     */
    public boolean hasBuiltInAmmo() {
        return null == ammoTypeId;
    }

    public boolean isCompatibleAmmo(Ammunition aAmmunition) {
        if (hasBuiltInAmmo()) {
            return false;
        }
        return ammoTypeId.equals(aAmmunition.getAmmoId());
    }
}
