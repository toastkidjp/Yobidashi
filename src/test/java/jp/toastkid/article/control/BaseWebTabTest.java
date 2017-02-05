package jp.toastkid.article.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import org.testfx.framework.junit.ApplicationTest;

import com.jfoenix.controls.JFXSpinner;

import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * Test of {@link BaseWebTab}.
 *
 * @author Toast kid
 *
 */
public class BaseWebTabTest extends ApplicationTest {

    /** Title. */
    private static final String TITLE = "Title";

    /** Test object. */
    private BaseWebTab tab;

    /**
     * Test of {@link BaseWebTab#reload()}.
     */
    @Test
    public void testReload() {
        Platform.runLater(tab::reload);
    }

    /**
     * Test of {@link BaseWebTab#loadUrl(String)}.
     */
    @Test
    public void testLoadUrl() {
        Platform.runLater(() -> tab.loadUrl("https://www.yahoo.co.jp"));
    }

    /**
     * Test of {@link BaseWebTab#canLoadUrl()}.
     */
    @Test
    public void testCanLoadUrl() {
        assertTrue(tab.canLoadUrl());
    }

    /**
     * Test of {@link BaseWebTab#print(javafx.print.PrinterJob)}.
     */
    @Test
    public void testPrint() {
        //FIXME tab.print(mock(PrinterJob.class));
    }

    /**
     * Test of {@link BaseWebTab#getUrl()}.
     */
    @Test
    public void testGetUrl() {
        assertNull(tab.getUrl());
    }

    /**
     * Test of {@link BaseWebTab#moveToTop()}.
     */
    @Test
    public void testMoveToTop() {
        Platform.runLater(tab::moveToTop);
    }

    /**
     * Test of {@link BaseWebTab#moveToBottom()}.
     */
    @Test
    public void testMoveToBottom() {
        Platform.runLater(tab::moveToBottom);
    }

    /**
     * Test of {@link BaseWebTab#stop()}.
     */
    @Test
    public void testStop() {
        Platform.runLater(tab::stop);
    }

    /**
     * Test of {@link BaseWebTab#highlight(Optional, String)}.
     */
    @Test
    public void testHighlight() {
        Platform.runLater(() -> tab.highlight(Optional.of("Ramen"), ""));
    }

    /**
     * Test of {@link BaseWebTab#zoomProperty()}.
     */
    @Test
    public void testZoomProperty() {
        assertSame(tab.getWebView().zoomProperty(), tab.zoomProperty());
    }

    /**
     * Test of {@link BaseWebTab#htmlSource()}.
     */
    @Test
    public void testHtmlSource() {
        Platform.runLater(() -> assertEquals("<head></head><body></body>", tab.htmlSource()));
    }

    /**
     * Test of {@link BaseWebTab#getWebView()}.
     */
    @Test
    public void testGetWebView() {
        assertNotNull(tab.getWebView());
        assertTrue(tab.getWebView() instanceof WebView);
    }

    /**
     * Test of {@link BaseWebTab#showSpinner()} and {@link BaseWebTab#hideSpinner()}.
     */
    @Test
    public void testShowAndHideSpinner() {
        final JFXSpinner spinner = (JFXSpinner) Whitebox.getInternalState(tab, "spinner");
        assertTrue(spinner.isVisible());
        assertTrue(spinner.isManaged());
        tab.showSpinner();
        assertTrue(spinner.isVisible());
        assertTrue(spinner.isManaged());
        tab.hideSpinner();
        assertFalse(spinner.isVisible());
        assertFalse(spinner.isManaged());
    }

    @Override
    public void start(Stage stage) throws Exception {
        tab = new BaseWebTab(TITLE, new HBox(), tab -> assertTrue(true)) {
            @Override
            public String getTitle() {
                return TITLE;
            }
        };
    }

}
