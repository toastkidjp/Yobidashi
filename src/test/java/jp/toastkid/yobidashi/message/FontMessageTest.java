package jp.toastkid.yobidashi.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * {@link FontMessage}'s test case.
 *
 * @author Toast kid
 *
 */
public class FontMessageTest extends ApplicationTest {

    /**
     * Test of {@link FontMessage#make(int)}.
     */
    @Test
    public void testMake() {
        final FontMessage fe = FontMessage.make(Font.getDefault(), 10);
        assertEquals(10, fe.getSize());
        assertNotNull(fe.getFont().toString());
    }

    /**
     * Test of {@link FontMessage#make(int)}.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testCannotMake() {
        FontMessage.make(Font.getDefault(), -1);
    }

    /**
     * Test of {@link FontMessage#make(int)}.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testCannotMake_null() {
        FontMessage.make(null, 10);
    }

    @Override
    public void start(Stage stage) throws Exception {
       // NOP.
    }

}
