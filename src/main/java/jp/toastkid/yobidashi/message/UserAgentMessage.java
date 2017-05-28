/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi.message;

import jp.toastkid.article.control.UserAgent;

/**
 * Message of browser's User Agent.
 *
 * @author Toast kid
 *
 */
public class UserAgentMessage implements Message {

    /** User agent. */
    private final UserAgent ua;

    /**
     * Internal constructor.
     * @param ua
     */
    private UserAgentMessage(final UserAgent ua) {
        this.ua = ua;
    }

    /**
     * Make new message with {@link UserAgent}.
     * @param ua
     * @return
     */
    public static UserAgentMessage make(final UserAgent ua) {
        return new UserAgentMessage(ua);
    }

    /**
     * Return User Agent.
     * @return
     */
    public UserAgent getUserAgent() {
        return ua;
    }

}
