package jp.toastkid.article.control.editor;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import jp.toastkid.article.control.BaseWebTab;

/**
 * {@link Editor}'s test cases.
 *
 * @author Toast kid
 *
 */
public class EditorTest extends ApplicationTest {

    /** Editor. */
    private Editor editor;

    /**
     * Test of {@link BaseWebTab#getYPosition()}.
     */
    @Test
    public void testGetYPosition() {
        Platform.runLater(() -> {
            editor.scrollTo(200d);
            System.out.println(editor.getYPosition());
        });
    }

    @Override
    public void start(Stage stage) throws Exception {
        editor = new Editor(testPath(), new WebView());
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
