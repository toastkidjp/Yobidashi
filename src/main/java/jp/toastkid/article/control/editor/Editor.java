package jp.toastkid.article.control.editor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javafx.PlatformUtil;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodTextRun;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import jp.toastkid.yobidashi.models.Defines;
import reactor.core.publisher.Flux;

/**
 * Markdown editor.
 *
 * @author Toast kid
 *
 */
public class Editor {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Editor.class);

    /** WebView - Editor y pos. */
    private static final double SCALE_FACTOR = 3.0d;

    /** File Path. */
    private final Path path;

    /** Content editor. */
    private final CodeArea area;

    /** Editor's scroll bar. */
    private final VirtualizedScrollPane<CodeArea> vsp;

    /** Start of the text under input method composition. */
    private int imstart;

    /** Length of the text under input method composition. */
    private int imlength;

    /** Holds concrete attributes for the composition runs. */
    private final List<Shape> imattrs = new ArrayList<Shape>();

    /** Emit scroll value. */
    private final Flux<Double> scroll;

    /** Modified value. */
    private final BooleanProperty isModified;

    /**
     * Initialize with path.
     * @param path
     */
    Editor(final Path path) {
        this.path = path;

        this.area = new CodeArea();
        initEditor();
        vsp = new VirtualizedScrollPane<>(area);
        scroll = Flux.<Double>create(emitter ->
            vsp.estimatedScrollYProperty().addListener(
                    (value, prev, next) -> emitter.next(convertToWebViewY(value.getValue().doubleValue())))
        );
        vsp.setPrefHeight(700.0d);
        isModified = new SimpleBooleanProperty(false);
    }

    /**
     * Initialize editor.
     */
    private void initEditor() {
        area.setParagraphGraphicFactory(LineNumberFactory.get(area));
        area.setOnKeyPressed(event -> {

            if (event.isControlDown()) {
                return;
            }

            if (!event.getCode().isLetterKey()
                    && !event.getCode().isDigitKey()
                    && !event.getCode().isWhitespaceKey()) {
                return;
            }
            isModified.set(true);
        });

        if (area.getOnInputMethodTextChanged() == null) {
            area.setOnInputMethodTextChanged(this::handleInputMethodEvent);
        }

        area.setInputMethodRequests(new EditorInputMethodRequests(area));
    }

    /**
     * Handle input method event.
     * @param event
     */
    private void handleInputMethodEvent(final InputMethodEvent event) {
        if (!area.isEditable() || area.isDisabled()) {
            return;
        }

        // just replace the text on iOS
        if (PlatformUtil.isIOS()) {
           area.replaceText(event.getCommitted());
           return;
        }

        // remove previous input method text (if any) or selected text
        if (imlength != 0) {
            //removeHighlight(imattrs);
            imattrs.clear();
            area.selectRange(imstart, imstart + imlength);
        }

        // Insert committed text
        if (event.getCommitted().length() != 0) {
            final String committed = event.getCommitted();
            area.replaceText(area.getSelection(), committed);
        }

        // Replace composed text
        imstart = area.getSelection().getStart();
        final StringBuilder composed = new StringBuilder();
        for (final InputMethodTextRun run : event.getComposed()) {
            composed.append(run.getText());
        }
        area.replaceText(area.getSelection(), composed.toString());
        imlength = composed.length();
        if (imlength == 0) {
            return;
        }
        int pos = imstart;
        for (final InputMethodTextRun run : event.getComposed()) {
            final int endPos = pos + run.getText().length();
            //createInputMethodAttributes(run.getHighlight(), pos, endPos);
            pos = endPos;
        }
        //addHighlight(imattrs, imstart);

        // Set caret position in composed text
        final int caretPos = event.getCaretPosition();
        if (caretPos >= 0 && caretPos < imlength) {
            area.selectRange(imstart + caretPos, imstart + caretPos);
        }
    }

    /**
     * Convert to WebView y position.
     * @param value
     * @return
     */
    private double convertToWebViewY(double value) {
        return value * SCALE_FACTOR;
    }

    /**
     * Return Node.
     * @return Node
     */
    Node getNode() {
        return vsp;
    }

    /**
     * Set font.
     * @param font
     */
    void setFont(final Font font) {
        area.setStyle(String.format("-fx-font-family: %s; -fx-font-size: %f;",
                font.getFamily(), font.getSize()));
    }

    /**
     * Set content text.
     * @param content text
     */
    void setContent(final String content) {
        area.replaceText(content);
    }

    /**
     * Return current content.
     * @return content text
     */
    String getContent() {
        return area.getText();
    }

    /**
     * Hide node.
     */
    void hide() {
        vsp.setVisible(false);
        vsp.setManaged(false);
    }

    /**
     * Show node.
     */
    void show() {
        vsp.setVisible(true);
        vsp.setManaged(true);
        area.requestFocus();
        area.moveTo(0, 0);
    }

    /**
     * Return this object's target path.
     * @return path
     */
    Path getPath() {
        return this.path;
    }

    /**
     * Emitter of scroll value.
     * @return
     */
    Flux<Double> getScrollEmitter() {
        return scroll;
    }

    /**
     * Set modified listener.
     * @param listener
     */
    void setModifiedListener(final ChangeListener<? super Boolean> listener) {
        isModified.addListener(listener);;
    }

    /**
     * Save content text to file.
     * @return message.
     */
    String saveContent() {
        try {
            Files.write(getPath(), getContent().getBytes(Defines.ARTICLE_ENCODE));
            isModified.set(false);
        } catch (final IOException e) {
            LOGGER.error("Error", e);
        }
        return "";
    }

}
