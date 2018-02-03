/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.libs.http;

import jp.toastkid.libs.http.Request.HttpMethod;

/**
 * Http instant client.
 * <hr/>
 *
 * <h2>Getting start</h2>
 * <pre>
 * System.out.println(Http.GET.url("http://www.yahoo.co.jp").fetch().body);
 * </pre>
 * @author Toast kid
 *
 */
public final class Http {

    /** Request factory with HTTP GET method. */
    public static final Request.Factory GET    = new Request.Factory(HttpMethod.GET);

    /** Request factory with HTTP POST method. */
    public static final Request.Factory POST   = new Request.Factory(HttpMethod.POST);

    /** Request factory with HTTP HEAD method. */
    public static final Request.Factory HEAD   = new Request.Factory(HttpMethod.HEAD);

    /** Request factory with HTTP PUT method. */
    public static final Request.Factory PUT    = new Request.Factory(HttpMethod.PUT);

    /** Request factory with HTTP DELETE method. */
    public static final Request.Factory DELETE = new Request.Factory(HttpMethod.DELETE);

    /** Request factory with HTTP OPTIONS method. */
    public static final Request.Factory OPTIONS = new Request.Factory(HttpMethod.OPTIONS);

    /** Request factory with HTTP TRACE method. */
    public static final Request.Factory TRACE = new Request.Factory(HttpMethod.TRACE);

    /**
     * deny make instance.
     * @param url
     */
    private Http() {
        // NOP.
    }

}
