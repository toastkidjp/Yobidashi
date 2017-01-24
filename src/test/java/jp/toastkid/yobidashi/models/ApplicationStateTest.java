package jp.toastkid.yobidashi.models;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

/**
 * {@link ApplicationState}'s test case.
 * @author Toast kid
 *
 */
public class ApplicationStateTest {

    /**
     * Test of {@link ApplicationState#getConfigMap()}.
     */
    @Test
    public void testGetConfigMap() {
        final Map<String, String> map = ApplicationState.getConfigMap();
        assertTrue(map.containsKey("Java Home"));
        assertTrue(map.containsKey("Java Version"));
        assertTrue(map.containsKey("Total Memory"));
        assertTrue(map.containsKey("Available Processor"));
        assertTrue(map.containsKey("Max Memory"));
        assertTrue(map.containsKey("Free Memory"));
        //
    }

}
