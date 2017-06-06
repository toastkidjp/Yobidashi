/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.article.converter;

import java.io.Serializable;

/**
 * Html subheading.
 * @author Toast kid
 *
 */
public class Subheading implements Serializable {

    /** serialVersionUID. */
    private static final long serialVersionUID = 6144685566158773878L;

    /** title. */
    public final String title;

    /** subheading id. */
    public final String id;

    /** html depth. */
    public final int    depth;


    /**
     * initialize object.
     * @param str
     * @param id
     * @param i
     */
    public Subheading(final String str, final String id, final int depth) {
        this.title = str;
        this.id    = id;
        this.depth = depth;
    }
}
