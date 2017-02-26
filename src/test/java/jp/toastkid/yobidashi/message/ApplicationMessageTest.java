package jp.toastkid.yobidashi.message;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import jp.toastkid.yobidashi.message.ApplicationMessage.Command;

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

    /**
     * {@link ApplicationMessage#makeMinimize()}
     */
    @Test
    public void test_makeMinimize() {
        final ApplicationMessage makeQuit = ApplicationMessage.makeMinimize();
        assertEquals(ApplicationMessage.Command.MINIMIZE, makeQuit.getCommand());
    }

    /**
     * Test for coverage.
     */
    @Test
    public void test_valueOf() {
        final Command valueOf = ApplicationMessage.Command.valueOf("QUIT");
        assertEquals(Command.QUIT, valueOf);
    }

    /**
     * Test for coverage.
     */
    @Test
    public void test_values() {
        final Command[] values = ApplicationMessage.Command.values();
        assertEquals("[MINIMIZE, QUIT]", Arrays.deepToString(values));
    }

}
