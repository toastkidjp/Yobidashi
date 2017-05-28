/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.article.control;

/**
 * Browser's user agents.
 *
 * @author Toast kid
 *
 */
public enum UserAgent {

    DEFAULT("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/538.19"
            + " (KHTML, like Gecko) JavaFX/8.0 Safari/538.19"),
    MAC("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/601.5.17"
            + " (KHTML, like Gecko) Version/9.1 Safari/601.5.17"),
    IPHONE("Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_2 like Mac OS X)"
            + " AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13F69 Safari/601.1"),
    IPAD("Mozilla/5.0 (iPad; CPU OS 9_3_2 like Mac OS X)"
            + " AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13F69 Safari/601.1");

    private final String ua;

    UserAgent(final String ua) {
        this.ua = ua;
    }

    public String text() {
        return ua;
    }
}
