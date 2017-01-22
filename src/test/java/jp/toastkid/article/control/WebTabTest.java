package jp.toastkid.article.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * {@link WebTab}'s test.
 * @author Toast kid
 *
 */
public class WebTabTest extends ApplicationTest {

    /** Test object. */
    private WebTab tab;

    /**
     * Test of {@link WebTab#reload()}.
     */
    @Test
    public void testReload() {
        Platform.runLater(tab::reload);
    }

    /**
     * Test of {@link WebTab#loadUrl(String)}.
     */
    @Test
    public void testLoadUrl() {
        Platform.runLater(() -> {
            tab.loadUrl("http://www.yahoo.co.jp");
            assertEquals("http://www.yahoo.co.jp", tab.getUrl());
        });
    }

    /**
     * Test of {@link WebTab#print(javafx.print.PrinterJob)}.
     */
    @Test
    public void testPrint() {
        // FIXME implement.
    }

    @Override
    public void start(Stage stage) throws Exception {
        tab = new WebTab.Builder()
                .setUrl("https://www.yahoo.co.jp")
                .setOnClose(tab -> assertTrue(true))
                .build();
    }

}
