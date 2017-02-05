package jp.toastkid.article.control.editor;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.fxmisc.richtext.CodeArea;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import jp.toastkid.article.control.BaseWebTab;

/**
 * {@link Editor}'s test cases.
 *
 * @author Toast kid
 */
public class EditorTest extends ApplicationTest {

    /** Test key event. */
    private static final KeyEvent DIGIT = new KeyEvent(
            KeyEvent.KEY_PRESSED, "input", "input", KeyCode.DIGIT1, false, false, false, false);

    /** Test key event. */
    private static final KeyEvent SPACE = new KeyEvent(
            KeyEvent.KEY_PRESSED, "input", "input", KeyCode.SPACE, false, false, false, false);

    /** Test key event. */
    private static final KeyEvent UP = new KeyEvent(
            KeyEvent.KEY_PRESSED, "input", "input", KeyCode.UP, false, false, false, false);

    /** Test key event. */
    private static final KeyEvent LETTER = new KeyEvent(
            KeyEvent.KEY_PRESSED, "input", "input", KeyCode.A, true, false, false, false);

    /** Test key event. */
    private static final KeyEvent CONTROL_DOWN = new KeyEvent(
            KeyEvent.KEY_PRESSED, "input", "input", KeyCode.A, false, true, false, false);

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

    /**
     * Test of key event.
     */
    @Test
    public void test_keyEvent() {
        final CodeArea area = (CodeArea) Whitebox.getInternalState(editor, "area");
        area.fireEvent(CONTROL_DOWN);
        area.fireEvent(LETTER);
        area.fireEvent(UP);
        area.fireEvent(SPACE);
        area.fireEvent(DIGIT);
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
