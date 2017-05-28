/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi.message;

import jp.toastkid.libs.WebServiceHelper;
import jp.toastkid.libs.WebServiceHelper.Type;

/**
 * Web search event's message.
 *
 * @author Toast kid
 *
 */
public class WebSearchMessage implements Message {

    /** search query. */
    private final String query;

    /** search type. */
    private final Type type;

    /**
     * Call from internal.
     * @param query
     * @param type
     */
    private WebSearchMessage(String query, String type) {
        this.query = query;
        this.type  = WebServiceHelper.Type.find(type);
    }

    /**
     * Make with query and title.
     * @param query
     * @param type
     */
    public static WebSearchMessage make(String query, String type) {
        return new WebSearchMessage(query, type);
    }

    public String query() {
        return query;
    }

    public Type type() {
        return type;
    }
}
