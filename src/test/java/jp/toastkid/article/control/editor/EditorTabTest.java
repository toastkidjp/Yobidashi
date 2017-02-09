package jp.toastkid.article.control.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import jp.toastkid.yobidashi.models.ConfigTest;

/**
 * {@link EditorTab}'s test case.
 *
 * @author Toast kid
 *
 */
public class EditorTabTest extends ApplicationTest {

    /** Test object. */
    private EditorTab tab;

    /**
     * Check of {@link EditorTab#reload()}.
     */
    @Test
    public void testReload() {
        Platform.runLater(tab::reload);
    }

    /**
     * Check of {@link EditorTab#loadUrl(String)}.
     */
    @Test
    public void testLoadUrl() {
        Platform.runLater(() -> tab.loadUrl(""));
    }

    /**
     * Check of {@link EditorTab#getTitle()}.
     */
    @Test
    public void testGetTitle() {
        assertEquals("C6FCB5AD323031332D30382D333128C5DA29.md", tab.getTitle());
    }

    /**
     * Check of {@link EditorTab#getUrl()}.
     */
    @Test
    public void testGetUrl() {
        final String url = tab.getUrl();
        assertTrue(url.startsWith("file:/"));
        assertTrue(url.endsWith("C6FCB5AD323031332D30382D333128C5DA29.md"));
    }

    /**
     * Check of {@link EditorTab#edit()}.
     */
    @Test
    public void testEdit() {
        Platform.runLater(() -> assertEquals("", tab.edit()));
    }

    /**
     * Check of {@link EditorTab#saveContent()}.
     */
    @Test
    public void testSaveContent() {
        Platform.runLater(() -> assertEquals(
                "Save to file 「C6FCB5AD323031332D30382D333128C5DA29.md」", tab.saveContent()));
    }

    /**
     * Check of {@link EditorTab#isEditing()}.
     */
    @Test
    public void testIsEditing() {
        assertFalse(tab.isEditing());
    }

    /**
     * Test of {@link EditorTab#htmlSource()}.
     */
    @Test
    public void testHtmlSource() {
        assertNotNull(tab.htmlSource());
    }

    /**
     * Test of {@link EditorTab#setFont(Font)()}.
     */
    @Test
    public void test_setFont() {
        Platform.runLater(() -> tab.setFont(Font.getDefault()));
    }

    @Override
    public void start(Stage stage) throws Exception {
        tab = new EditorTab.Builder()
            .setPath(testPath())
            .setConfig(ConfigTest.makeConfig())
            .setOnClose(t -> assertTrue(true))
            .build();
    }

    /**
     * Return test file Path.
     * @return
     * @throws URISyntaxException
     */
    private Path testPath() throws URISyntaxException {
        return Paths.get(getClass().getClassLoader()
                .getResource("article/C6FCB5AD323031332D30382D333128C5DA29.md").toURI());
    }
}
