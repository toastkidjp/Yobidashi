/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.jfx.common.transition;

import javafx.animation.Transition;
import javafx.scene.control.SplitPane;
import javafx.util.Duration;

/**
 * Split transition's factory.
 *
 * @author Toast kid
 *
 */
public class SplitterTransitionFactory {

    /**
     * Make sliding transition.
     *
     * @param split SplitPane
     * @param endPosition
     * @param max
     * @return Transition
     */
    public static Transition makeHorizontalSlide(final SplitPane split, final double endPosition, final double max) {

        return new Transition() {
            private final double start;
            {
                final double current = split.getDividerPositions()[0];
                start = current < max ? current : max;
                setCycleDuration(Duration.millis(250));
            }

            @Override
            protected void interpolate(double frac) {
                final double gap = endPosition - start;
                split.setDividerPositions(start + gap * frac);
            }
        };
    }
}
