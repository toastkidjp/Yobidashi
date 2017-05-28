/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi.message;

/**
 * Show search dialog.
 *
 * @author Toast kid
 *
 */
public class ShowSearchDialog implements Message {

    /**
     * Call only internal.
     */
    private ShowSearchDialog() {
        // NOP.
    }

    /**
     * Make empty instance.
     * @return {@link ShowSearchDialog}
     */
    public static ShowSearchDialog make() {
        return new ShowSearchDialog();
    }

}
