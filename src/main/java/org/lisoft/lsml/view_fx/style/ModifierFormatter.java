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
package org.lisoft.lsml.view_fx.style;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.lisoft.lsml.model.modifiers.Modifier;
import org.lisoft.lsml.model.modifiers.ModifierDescription;
import org.lisoft.lsml.model.modifiers.ModifierType;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * This class will format {@link Modifier}s to a {@link Label}s or containers.
 *
 * @author Emily Björk
 */
public class ModifierFormatter {

    public void format(Collection<Modifier> aModifiers, ObservableList<Node> aTarget) {
        final TreeMap<ModifierDescription, Double> collated = new TreeMap<>((aLeft, aRight) -> {
            return aLeft.getUiName().compareTo(aRight.getUiName());
        });

        for (final Modifier modifier : aModifiers) {
            Double v = collated.get(modifier.getDescription());
            if (null == v) {
                v = 0.0;
            }
            v = v.doubleValue() + modifier.getValue();
            collated.put(modifier.getDescription(), v);
        }

        for (final Entry<ModifierDescription, Double> e : collated.entrySet()) {
            aTarget.add(format(new Modifier(e.getKey(), e.getValue())));
        }
    }

    public Label format(Modifier aModifier) {
        final Label label = new Label(aModifier.toString());
        final double value = aModifier.getValue();
        final ModifierType type = aModifier.getDescription().getModifierType();

        final String color;
        switch (type) {
            case INDETERMINATE:
                color = StyleManager.COLOUR_QUIRK_NEUTRAL;
                break;
            case NEGATIVE_GOOD:
                if (value < 0) {
                    color = StyleManager.COLOUR_QUIRK_GOOD;
                }
                else {
                    color = StyleManager.COLOUR_QUIRK_BAD;
                }
                break;
            case POSITIVE_GOOD:
                if (value < 0) {
                    color = StyleManager.COLOUR_QUIRK_BAD;
                }
                else {
                    color = StyleManager.COLOUR_QUIRK_GOOD;
                }
                break;
            default:
                throw new RuntimeException("Unknown modifier type!");
        }

        label.setStyle("-fx-text-fill:" + color);
        return label;
    }
}
