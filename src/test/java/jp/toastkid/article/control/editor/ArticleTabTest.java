/**
 *
 */
package jp.toastkid.article.control.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import jp.toastkid.article.control.editor.ArticleTab;
import jp.toastkid.article.models.Article;
import jp.toastkid.yobidashi.models.ConfigTest;

/**
 * {@link ArticleTab}'s test cases.
 *
 * @author Toast kid
 *
 */
public class ArticleTabTest extends ApplicationTest {

    /** Test article. */
    private static final Article TEST_ARTICLE
        = new Article(Paths.get("src/test/resources/tab/C6FCB5AD323031332D30382D333128C5DA29.md"));

    /** Test object. */
    private ArticleTab tab;

    /**
     * {@link jp.toastkid.article.control.editor.ArticleTab#reload()} 's test method.
     */
    @Test
    public void testReload() {
        Platform.runLater(tab::reload);
    }

    /**
     * {@link jp.toastkid.article.control.editor.ArticleTab#loadUrl(java.lang.String)} 's test method.
     */
    @Test
    public void testLoadUrlString() {
        Platform.runLater(() -> tab.loadUrl("http://www.yahoo.co.jp"));
    }

    /**
     * {@link jp.toastkid.article.control.editor.ArticleTab#getTitle()} 's test method.
     */
    @Test
    public void testGetTitle() {
        assertEquals("日記2013-08-31(土)", tab.getTitle());
    }

    /**
     * {@link jp.toastkid.article.control.editor.ArticleTab#getUrl()} 's test method.
     */
    @Test
    public void testGetUrl() {
        assertEquals("file:///internal//md/C6FCB5AD323031332D30382D333128C5DA29.md", tab.getUrl());
    }

    /**
     * {@link jp.toastkid.article.control.editor.ArticleTab#edit()} 's test method.
     */
    @Test
    public void testEdit() {
        Platform.runLater(() -> assertEquals("", tab.edit()));
    }

    /**
     * Test of {@link ArticleTab#isEditing()}.
     */
    @Test
    public void testIsEditing() {
        assertFalse(tab.isEditing());
    }

    /**
     * {@link jp.toastkid.article.control.editor.ArticleTab#saveContent()} 's test method.
     */
    @Test
    public void testSaveContent() {
        Platform.runLater(() -> assertEquals("Save to file 「日記2013-08-31(土)」", tab.saveContent()));
    }

    /**
     * {@link jp.toastkid.article.control.editor.ArticleTab#htmlSource()} 's test method.
     */
    @Test
    public void testHtmlSource() {
        Platform.runLater(() -> assertNotNull(tab.htmlSource()));
    }

    /**
     * {@link jp.toastkid.article.control.editor.ArticleTab#getWebView()} 's test method.
     */
    @Test
    public void testGetWebView() {
        final WebView webView = tab.getWebView();
        assertNotNull(webView);
        assertTrue(webView instanceof WebView);
    }

    /**
     * {@link jp.toastkid.article.control.editor.ArticleTab#loadUrl(java.lang.String, boolean)} 's test method.
     */
    @Test
    public void testLoadUrlStringBoolean() {
        Platform.runLater(() -> tab.loadUrl("", false));
    }

    /**
     * Test of {@link EditorTab#setFont(Font)()}.
     */
    @Test
    public void test_setFont() {
        Platform.runLater(() -> tab.setFont(Font.getDefault()));
    }

    /**
     * {@link jp.toastkid.article.control.editor.ArticleTab#getArticle()} 's test method.
     */
    @Test
    public void testGetArticle() {
        assertSame(TEST_ARTICLE, tab.getArticle());
    }

    @Override
    public void start(Stage stage) throws Exception {
        tab = new ArticleTab.Builder()
                .setArticle(TEST_ARTICLE)
                .setConfig(ConfigTest.makeConfig())
                .setOnClose(t -> assertTrue(true))
                .setOnLoad(() -> assertTrue(true))
                .build();
    }

}
