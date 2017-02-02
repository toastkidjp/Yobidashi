package jp.toastkid.article.control.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.fxmisc.richtext.CodeArea;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import org.reactfx.value.Val;
import org.testfx.framework.junit.ApplicationTest;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
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
     * Test of {@link EditorInputMethodRequests#getTextLocation(int)}.
     */
    @Test
    public void testGetTextLocation_presentCase() {
        final Bounds b = new Bounds(10, 10, 0, 0, 0, 0) {

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean intersects(double x, double y, double z, double w, double h, double d) {
                return false;
            }

            @Override
            public boolean intersects(double x, double y, double w, double h) {
                return false;
            }

            @Override
            public boolean intersects(Bounds b) {
                return false;
            }

            @Override
            public boolean contains(double x, double y, double z, double w, double h, double d) {
                return false;
            }

            @Override
            public boolean contains(double x, double y, double w, double h) {
                return false;
            }

            @Override
            public boolean contains(double x, double y, double z) {
                return false;
            }

            @Override
            public boolean contains(double x, double y) {
                return false;
            }

            @Override
            public boolean contains(Bounds b) {
                return false;
            }

            @Override
            public boolean contains(Point3D p) {
                return false;
            }

            @Override
            public boolean contains(Point2D p) {
                return false;
            }
        };
        final Optional<Bounds> boundsOr = Optional.of(b);
        final CodeArea area = mock(CodeArea.class);
        Whitebox.setInternalState(area, "caretBounds", Val.create(() -> boundsOr));
        final Point2D point2d = new EditorInputMethodRequests(area).getTextLocation(0);
        assertEquals(10.0d, point2d.getX(), 0.0d);
        assertEquals(30.0d, point2d.getY(), 0.0d);
    }

    /*
     * final Optional<Bounds> caretBoundsOr = editor.getCaretBounds();
        if (caretBoundsOr.isPresent()) {
            final Bounds bounds = caretBoundsOr.get();
            return new Point2D(bounds.getMinX(), bounds.getMinY() + 20);
        }
     */

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
