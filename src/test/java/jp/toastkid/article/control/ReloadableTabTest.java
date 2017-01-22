package jp.toastkid.article.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.beans.property.DoubleProperty;
import javafx.print.PrinterJob;
import javafx.stage.Stage;

/**
 * {@link ReloadableTab}'s test.
 *
 * @author Toast kid
 *
 */
public class ReloadableTabTest extends ApplicationTest {

    /** Title. */
    private static final String TITLE = "Title";

    /** Test target. */
    private ReloadableTab tab;

    /**
     * Test of {@link ReloadableTab#getCloseAction()}.
     */
    @Test
    public void testGetCloseAction() {
        assertNull(tab.getCloseAction());
    }

    /**
     * Test of {@link ReloadableTab#setCloseAction(java.util.function.Consumer)}.
     */
    @Test
    public void testSetCloseAction() {
        tab.setCloseAction(tab -> assertTrue(true));
        tab.close(tab -> assertTrue(true));
    }

    /**
     * Test of {@link ReloadableTab#edit()}.
     */
    @Test
    public void testEdit() {
        assertEquals("This tab's content can't edit.", tab.edit());
    }

    /**
     * Test of {@link ReloadableTab#saveContent()}.
     */
    @Test
    public void testSaveContent() {
        assertEquals("This tab's content can't save.", tab.saveContent());
    }

    /**
     * Test of {@link ReloadableTab#isEditing()}.
     */
    @Test
    public void testIsEditing() {
        assertEquals(false, tab.isEditing());
    }

    @Override
    public void start(Stage stage) throws Exception {
        tab = new ReloadableTab(TITLE, null, null) {

            @Override
            public DoubleProperty zoomProperty() {
                return null;
            }

            @Override
            public void stop() {
                // NOP.
            }

            @Override
            public void reload() {
                // NOP.
            }

            @Override
            public void print(PrinterJob job) {
                // NOP.
            }

            @Override
            public void moveToTop() {
                // NOP.
            }

            @Override
            public void moveToBottom() {
                // NOP.
            }

            @Override
            public void loadUrl(String url) {
                // NOP.
            }

            @Override
            public void highlight(Optional<String> word, String script) {
                // NOP.
            }

            @Override
            public String getUrl() {
                return null;
            }

            @Override
            public String getTitle() {
                return null;
            }

            @Override
            public boolean canLoadUrl() {
                return false;
            }
        };
    }

}
