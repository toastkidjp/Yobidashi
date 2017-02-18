package jp.toastkid.yobidashi.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import jp.toastkid.article.control.UserAgent;

/**
 * {@link UserAgentMessage}'s test cases.
 *
 * @author Toast kid
 *
 */
public class UserAgentMessageTest {

    /**
     * Test of {@link UserAgentMessage#make(UserAgent)} with null.
     */
    @Test
    public void test_make_with_null() {
        final UserAgentMessage withNull = UserAgentMessage.make(null);
        assertNotNull(withNull);
        assertNull(withNull.getUserAgent());
    }

    /**
     * Test of {@link UserAgentMessage#make(UserAgent)}.
     */
    @Test
    public void test_make() {
        final UserAgentMessage ua = UserAgentMessage.make(UserAgent.IPAD);
        assertEquals(UserAgent.IPAD, ua.getUserAgent());
    }

}
