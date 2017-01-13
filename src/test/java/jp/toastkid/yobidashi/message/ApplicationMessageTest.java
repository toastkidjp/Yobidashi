package jp.toastkid.yobidashi.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * {@link ApplicationMessage}'s test case.
 *
 * @author Toast kid
 *
 */
public class ApplicationMessageTest {

    /**
     * {@link ApplicationMessage#makeQuit}
     */
    @Test
    public void test_makeQuit() {
        final ApplicationMessage makeQuit = ApplicationMessage.makeQuit();
        assertEquals(ApplicationMessage.Command.QUIT, makeQuit.getCommand());
    }

}
