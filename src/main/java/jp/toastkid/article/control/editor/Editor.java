package jp.toastkid.article.control.editor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javafx.PlatformUtil;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker.State;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodTextRun;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import jp.toastkid.article.models.Article;
import jp.toastkid.jfx.common.transition.SplitterTransitionFactory;
import jp.toastkid.libs.utils.MathUtil;

/**
 * Markdown editor.
 *
 * @author Toast kid
 *
 */
public class Editor {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Editor.class);

    /** Default scale factor. */
    private static final double DEFAULT_SCALE_FACTOR = 2.2d;

    /** WebView - Editor y pos. */
    private double scaleFactor = DEFAULT_SCALE_FACTOR;

    /** File Path. */
    private final Path path;

    /** Splitter. */
    private final SplitPane split;

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

    /** Modified value. */
    private final BooleanProperty isModified;

    /** WebView engine. */
    private final WebEngine engine;

    /** Preview's html height. */
    private double previewHeight;

    /**
     * Initialize with path.
     * @param path
     */
    Editor(final Path path, final WebView webView) {
        this.path      = path;
        this.engine    = webView.getEngine();
        this.area      = new CodeArea();

        initArea();

        engine.getLoadWorker().stateProperty().addListener((observable, prev, next) -> {
            if (!State.SUCCEEDED.equals(observable.getValue())) {
                return;
            }
            previewHeight
                = Double.parseDouble(engine.executeScript("document.body.scrollHeight").toString());
        });

        vsp = new VirtualizedScrollPane<>(area);
        vsp.estimatedScrollYProperty().addListener(
                (value, prev, next) -> scrollTo(convertToWebViewY(value.getValue().doubleValue())));
        vsp.setPrefHeight(700.0d);

        split = new SplitPane(vsp, webView);
        split.setDividerPositions(0.5);

        isModified = new SimpleBooleanProperty(false);
    }

    /**
     * Initialize text area.
     */
    private void initArea() {
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
        area.totalHeightEstimateProperty().addListener((value, prev, next) -> {
            if (next == null) {
                return;
            }
            final double newValue = previewHeight / next;
            scaleFactor = 0 < newValue
                    ? 1 < newValue ? newValue * 1.2d : newValue * 0.8d
                    : DEFAULT_SCALE_FACTOR;
        });
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
     * Switch editor's visibility.
     */
    void switchEditorVisible() {
        if (isNotEditorVisible()) {
            SplitterTransitionFactory.makeHorizontalSlide(split, 0.5d, 1.0d).play();
            split.setDividerPositions(0.5);
            show();
            return;
        }

        SplitterTransitionFactory.makeHorizontalSlide(split, 0.0d, 1.0d).play();
        hide();
        split.setDividerPositions(0.0);
    }

    /**
     * Return current yPosition.
     * @return yPosition(yOffest)
     */
    int getYPosition() {
        final Object script = engine.executeScript("window.pageYOffset;");
        return MathUtil.parseOrZero(Optional.ofNullable(script).orElse("0").toString());
    }

    /**
     * Convert to WebView y position.
     * @param value
     * @return
     */
    private double convertToWebViewY(double value) {
        return value * scaleFactor;
    }

    /**
     * Return Node.
     * @return Node
     */
    Node getNode() {
        return split;
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
            Files.write(getPath(), getContent().getBytes(Article.ENCODE));
            isModified.set(false);
        } catch (final IOException e) {
            LOGGER.error("Error", e);
        }
        return "";
    }

    /**
     * Return isn't visible of editor.
     * @return
     */
    boolean isNotEditorVisible() {
        return split.getDividerPositions()[0] < 0.1d;
    }

    /**
     * Scroll to specified y position.
     * @param scrollTo
     */
    void scrollTo(final double scrollTo) {
        engine.executeScript(String.format("window.scrollTo(0, %f);", scrollTo));
    }

}
