package jp.toastkid.yobidashi.message;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * {@link ToolsDrawerMessage}'s test case.
 *
 * @author Toast kid
 *
 */
public class ToolsDrawerMessageTest {

    /**
     * Check {@link ToolsDrawerMessage#make()}.
     */
    @Test
    public void testMake() {
        assertNotNull(ToolsDrawerMessage.make());
    }

}
