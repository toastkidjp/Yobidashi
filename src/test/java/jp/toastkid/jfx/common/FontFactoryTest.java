package jp.toastkid.jfx.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * {@link FontFactory}'s test.
 *
 * @author Toast kid
 *
 */
public class FontFactoryTest extends ApplicationTest {

    /**
     * Test of {@link FontFactory#make()}.
     */
    @Test
    public void test_make() {
        final Font font = FontFactory.make("System", 12);
        assertNotNull(font);
        assertEquals("System", font.getFamily());
        assertEquals(12.0d, font.getSize(), 0.0d);
    }

    /**
     * Test of {@link FontFactory#make()} with null.
     */
    @Test
    public void test_make_with_null_family() {
        final Font font = FontFactory.make(null, 12);
        assertNotNull(font);
        assertEquals("System", font.getFamily());
        assertEquals(15.0d, font.getSize(), 0.0d);
    }

    /**
     * Test of {@link FontFactory#make()} with minus font size.
     */
    @Test
    public void test_make_with_minus_font_size() {
        final Font font = FontFactory.make("System", -1);
        assertNotNull(font);
        assertEquals("System", font.getFamily());
        assertEquals(15.0d, font.getSize(), 0.0d);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // NOP.
    }

}
