/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.jfx.common.control;

import java.util.Collections;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.LabeledSkinBase;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;

/**
 * Overwrite for register shortcut.
 *
 * @author Toast kid
 *
 */
public class MenuLabelSkin extends LabeledSkinBase<Label, BehaviorBase<Label>> {

    /**
     * Initialize with {@link MenuLabel}.
     * @param label {@link MenuLabel}
     */
    public MenuLabelSkin(final MenuLabel label) {
        super(label, new BehaviorBase<>(label, Collections.emptyList()));

        // Labels do not block the mouse by default, unlike most other UI Controls.
        consumeMouseEvents(false);

        registerChangeListener(label.labelForProperty(), "LABEL_FOR");

        if (label.getAccelerator() == null) {
            return;
        }

        label.setText(label.getText() + "\t|\t" + label.getAccelerator().getDisplayText());
        getSkinnable().getScene().getAccelerators()
            .put(label.getAccelerator(), () -> label.getOnAction().handle(new ActionEvent()));
    }

    @Override
    protected void handleControlPropertyChanged(final String p) {
        super.handleControlPropertyChanged(p);
        if ("LABEL_FOR".equals(p)) {
            mnemonicTargetChanged();
        }
    }
}
