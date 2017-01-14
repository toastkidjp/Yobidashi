package jp.toastkid.yobidashi.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * {@link SnackbarMessage}'s test case.
 *
 * @author Toast kid
 *
 */
public class SnackbarMessageTest {

    /**
     * Check {@link SnackbarMessage#makeShow(String)}.
     */
    @Test
    public void test_make() {
        final String text = "text";
        final SnackbarMessage message = SnackbarMessage.make(text);
        assertEquals(text, message.getText());
    }

}
