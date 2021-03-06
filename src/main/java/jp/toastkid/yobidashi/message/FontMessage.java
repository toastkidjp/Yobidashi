/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi.message;

import javafx.scene.text.Font;

/**
 * Font event.
 *
 * @author Toast kid
 *
 */
public class FontMessage implements Message {

    /** Font family. */
    private final Font font;

    /** Font size. */
    private final int size;

    /**
     * Constructor.
     * @param size
     */
    private FontMessage(final Font font, final int size) {
        this.font = font;
        this.size = size;
    }

    /**
     * Make object with font size.
     * @param font
     * @param size
     * @return FontEvent object
     */
    public static FontMessage make(final Font font, final int size) {
        if (font == null) {
            throw new IllegalArgumentException("You should pass correct font object.");
        }
        if (size < 0) {
            throw new IllegalArgumentException("You should pass positive int value.");
        }
        return new FontMessage(font, size);
    }

    /**
     * Return font.
     * @return font
     */
    public Font getFont() {
        return font;
    }

    /**
     * Return size.
     * @return size
     */
    public int getSize() {
        return size;
    }
}
