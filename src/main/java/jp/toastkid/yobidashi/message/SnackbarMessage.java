/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi.message;

/**
 * Snackbar event message.
 * @author Toast kid
 *
 */
public class SnackbarMessage implements Message {

    /** This instance's text. */
    private final String text;

    /**
     * Call from internal.
     * @param c
     * @param text
     */
    private SnackbarMessage(final String text) {
        this.text = text;
    }

    /**
     * Make message.
     * @param text
     * @return
     */
    public static SnackbarMessage make(final String text) {
        return new SnackbarMessage(text);
    }

    /**
     * Return text.
     * @return
     */
    public String getText() {
        return text;
    }
}
