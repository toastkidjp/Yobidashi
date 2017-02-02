package jp.toastkid.article.control;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.stage.Stage;
import jp.toastkid.article.models.ContentType;

/**
 * {@link WebTab}'s test.
 * @author Toast kid
 *
 */
public class WebTabTest extends ApplicationTest {

    /** Test object. */
    private WebTab tab;

    /** Tab with content. */
    private WebTab tabWithContent;

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
            assertNull(tab.getUrl());
        });
    }

    /**
     * Test of tab with content.
     */
    @Test
    public void test_tabWithContent() {
        assertNull(tabWithContent.getTitle());
    }

    /**
     * Test of {@link WebTab#print(javafx.print.PrinterJob)}.
     */
    @Test
    public void testPrint() {
        // FIXME implement.
        Platform.runLater(() -> tab.print(null));;
    }

    @Override
    public void start(Stage stage) throws Exception {
        tab = new WebTab.Builder()
                .setTitle("Title")
                .setUrl("https://www.yahoo.co.jp")
                .setOnClose(tab -> assertTrue(true))
                .build();

        tabWithContent = new WebTab.Builder()
                .setTitle("WithContent")
                .setUrl("https://www.yahoo.co.jp")
                .setContent("Content")
                .setContentType(ContentType.TEXT)
                .setOnClose(tab -> assertTrue(true))
                .build();
    }

}
