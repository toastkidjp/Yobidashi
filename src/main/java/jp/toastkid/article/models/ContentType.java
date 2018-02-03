/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.article.models;

/**
 * ContentType.
 *
 * @author Toast kid
 */
public enum ContentType {

    HTML("text/html"), TEXT("text/plain");

    /** type. */
    private final String text;

    /**
     * Call from internal.
     * @param text
     */
    private ContentType(final String text) {
        this.text = text;
    }

    /**
     * Get text.
     * @return text/html, text/plain
     */
    public String getText() {
        return text;
    }
}
