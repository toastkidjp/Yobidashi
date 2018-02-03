/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.jfx.common;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.text.Font;

/**
 * Font factory.
 *
 * @author Toast kid
 *
 */
public class FontFactory {

    /** Default font. */
    private static final Font DEFAULT_FONT = Font.getDefault();

    /**
     * Deny make instance.
     */
    private FontFactory() {
        // NOP.
    }

    /**
     * Return font.
     * @return Font
     */
    public static final Font make(final String family, final int size) {
        if (StringUtils.isBlank(family) || size < 0) {
            return DEFAULT_FONT;
        }
        return Font.font(family, size);
    }

}
