/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.article.control;

import com.jfoenix.controls.JFXTabPane;

import javafx.scene.control.Skin;
import javafx.scene.control.TabPane;

/**
 * Ripple {@link TabPane}'s skin.
 *
 * @author Toast kid
 *
 */
public class RippleTabPane extends JFXTabPane {

    @Override
    protected Skin<?> createDefaultSkin() {
        return new RippleTabSkin(this);
    }
}
