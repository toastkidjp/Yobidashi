package jp.toastkid.gui.jfx.music;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * {@link Controller}'s test.
 *
 * @author Toast kid
 *
 */
public class ControllerTest {

    /**
     * check {@link Controller#isValidFile(String)}'s behavior.
     */
    @Test
    public final void testIsValidMusicFile() {
        assertTrue(Controller.isValidFile("echo.mp3"));
    }

    /**
     * check {@link Controller#formatPath(String)}'s behavior.
     */
    @Test
    public final void test_formatPath() {
        assertEquals(
                "file:///path/to/music　dir/echo.mp3",
                Controller.formatPath("path\\to/music　dir\\echo.mp3")
                );;
    }
}
