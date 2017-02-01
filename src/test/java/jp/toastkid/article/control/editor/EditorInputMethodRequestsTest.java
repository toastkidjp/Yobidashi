package jp.toastkid.article.control.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.fxmisc.richtext.CodeArea;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.stage.Stage;

/**
 * {@link EditorInputMethodRequests}'s test.
 *
 * @author Toast kid
 *
 */
public class EditorInputMethodRequestsTest extends ApplicationTest {

    /** Test object. */
    private EditorInputMethodRequests requests;

    /** Editor. */
    private CodeArea editor;

    /**
     * Test of {@link EditorInputMethodRequests#getTextLocation(int)}.
     */
    @Test
    public void testGetTextLocation() {
        assertNull(requests.getTextLocation(0));
    }

    /**
     * Test of {@link EditorInputMethodRequests#getLocationOffset(int, int)}.
     */
    @Test
    public void testGetLocationOffset() {
        assertEquals(0, requests.getLocationOffset(1, 1));
    }

    /**
     * Test of {@link EditorInputMethodRequests#cancelLatestCommittedText()}.
     */
    @Test
    public void testCancelLatestCommittedText() {
        requests.cancelLatestCommittedText();
    }

    /**
     * Test of {@link EditorInputMethodRequests#getSelectedText()}.
     */
    @Test
    public void testGetSelectedText() {
        assertTrue(requests.getSelectedText().isEmpty());
        editor.replaceText("abｃｄef");
        editor.selectRange(3, 5);
        assertEquals("ｄe", requests.getSelectedText());
    }

    @Override
    public void start(Stage stage) throws Exception {
        editor = new CodeArea();
        requests = new EditorInputMethodRequests(editor);
    }

}
