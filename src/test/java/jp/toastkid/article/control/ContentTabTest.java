package jp.toastkid.article.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * {@link ContentTab}'s test case.
 *
 * @author Toast kid
 *
 */
public class ContentTabTest extends ApplicationTest {

    /** Title. */
    private static final String TITLE = "Title";

    /** Test object. */
    private ContentTab tab;

    /**
     * Test of {@link ContentTab#reload()}.
     */
    @Test
    public void testReload() {
        tab.reload();
    }

    /**
     * Test of {@link ContentTab#loadUrl(String)}.
     */
    @Test
    public void testLoadUrl() {
        tab.loadUrl("");
    }

    /**
     * Test of {@link ContentTab#canLoadUrl()}.
     */
    @Test
    public void testCanLoadUrl() {
        assertFalse(tab.canLoadUrl());
    }

    /**
     * Test of {@link ContentTab#print(javafx.print.PrinterJob)}.
     */
    @Test
    public void testPrint() {
        tab.print(null);
    }

    /**
     * Test of {@link ContentTab#getTitle()}.
     */
    @Test
    public void testGetTitle() {
        assertEquals(TITLE, tab.getTitle());
    }

    /**
     * Test of {@link ContentTab#getUrl()}.
     */
    @Test
    public void testGetUrl() {
        assertEquals("", tab.getUrl());
    }

    /**
     * Test of {@link ContentTab#moveToTop()}.
     */
    @Test
    public void testMoveToTop() {
        tab.moveToTop();
    }

    /**
     * Test of {@link ContentTab#moveToBottom()}.
     */
    @Test
    public void testMoveToBottom() {
        tab.moveToBottom();
    }

    /**
     * Test of {@link ContentTab#stop()}.
     */
    @Test
    public void testStop() {
        tab.stop();
    }

    /**
     * Test of {@link ContentTab#highlight(java.util.Optional, String)}.
     */
    @Test
    public void testHighlight() {
        tab.highlight(null, null);;
    }

    /**
     * Test of {@link ContentTab#getTypeSelector()}.
     */
    @Test
    public void testZoomProperty() {
        assertEquals(1.0d, tab.zoomProperty().get(), 0.1d);
    }

    @Override
    public void start(Stage stage) throws Exception {
        tab = new ContentTab.Builder()
                .setContent(new HBox())
                .setOnClose(tab -> assertTrue(true))
                .setTitle(TITLE)
                .build();
    }

}
