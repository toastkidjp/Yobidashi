package jp.toastkid.article.control.editor;

import java.util.Optional;

import org.fxmisc.richtext.CodeArea;

import javafx.geometry.Point2D;
import javafx.scene.control.IndexRange;
import javafx.scene.input.InputMethodRequests;

/**
 * Implement for multi-byte text input.
 *
 * @author Toast kid
 *
 */
public class EditorInputMethodRequests implements InputMethodRequests {

    /** Editor. */
    private final CodeArea editor;

    /**
     * Initialize with editor.
     * @param editor
     */
    public EditorInputMethodRequests(final CodeArea editor) {
        this.editor = editor;
    }

    @Override
    public Point2D getTextLocation(final int offset) {
        final Optional<Point2D> pointOr = editor.getCaretBounds()
                .map(bounds -> new Point2D(bounds.getMinX(), bounds.getMinY() + 20));
        return pointOr.isPresent() ? pointOr.get() : null;
    }

    @Override
    public int getLocationOffset(final int x, final int y) {
        return 0;
    }

    @Override
    public void cancelLatestCommittedText() {
        // NOP
    }

    @Override
    public String getSelectedText() {
        final IndexRange selection = editor.getSelection();

        return editor.getText(selection.getStart(), selection.getEnd());
    }
}
