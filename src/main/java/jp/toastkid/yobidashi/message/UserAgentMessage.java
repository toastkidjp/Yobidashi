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
