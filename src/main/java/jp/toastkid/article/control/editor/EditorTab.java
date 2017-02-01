package jp.toastkid.article.control.editor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import jp.toastkid.article.ArticleGenerator;
import jp.toastkid.article.control.BaseWebTab;
import jp.toastkid.jfx.common.transition.SplitterTransitionFactory;
import jp.toastkid.libs.utils.Strings;
import jp.toastkid.yobidashi.models.Config;
import jp.toastkid.yobidashi.models.Defines;

/**
 * Editor tab.
 *
 * @author Toast kid
 *
 */
public class EditorTab extends BaseWebTab implements Editable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EditorTab.class);

    /** WebView - Editor y pos. */
    private static final double SCALE_FACTOR = 3.0d;

    /** Article HTML generator. */
    private final ArticleGenerator generator;

    /** Article. */
    private final Path path;

    /** Content editor. */
    private final CodeArea editor;

    /** Splitter. */
    private final SplitPane split;

    /** Editor's scroll bar. */
    private final VirtualizedScrollPane<CodeArea> vsp;

    /**
     * {@link ArticleTab}'s builder.
     *
     * @author Toast kid
     */
    public static class Builder {

        private Path path;

        private Consumer<Tab> closeAction;

        private Config conf;

        public Builder setConfig(final Config conf) {
            this.conf = conf;
            return this;
        }

        public Builder setPath(final Path path) {
            this.path = path;
            return this;
        }

        public Builder setOnClose(final Consumer<Tab> closeAction) {
            this.closeAction = closeAction;
            return this;
        }

        public EditorTab build() {
            return new EditorTab(this);
        }

    }

    /**
     * Call only internal.
     * @param b Builder
     */
    private EditorTab(final Builder b) {
        super(b.path.getFileName().toString(), null, b.closeAction);
        this.path = b.path;
        this.generator = new ArticleGenerator(b.conf);

        this.editor = new CodeArea();
        initEditor();
        vsp = new VirtualizedScrollPane<>(editor);
        vsp.estimatedScrollYProperty()
            .addListener((value, prev, next) -> scrollTo(convertToWebViewY(value.getValue().doubleValue())));
        split = new SplitPane(vsp, getWebView());
        split.setDividerPositions(0.5);

        this.setContent(split);
        switchEditorVisible();
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
     * Initialize editor.
     */
    private void initEditor() {
        editor.setParagraphGraphicFactory(LineNumberFactory.get(editor));
        editor.setOnKeyPressed(event -> {
            if (getText().startsWith("* ")) {
                return;
            }

            if (event.isControlDown()) {
                return;
            }

            if (!event.getCode().isLetterKey()
                    && !event.getCode().isDigitKey()
                    && !event.getCode().isWhitespaceKey()) {
                return;
            }
            setText("* " + getText());
        });
    }

    /**
     * Switch editor's visibility.
     */
    private void switchEditorVisible() {
        if (isNotEditorVisible()) {
            SplitterTransitionFactory.makeHorizontalSlide(split, 0.5d, 1.0d).play();
            split.setDividerPositions(0.5);
            vsp.setVisible(true);
            vsp.setManaged(true);
            editor.requestFocus();
            editor.moveTo(0, 0);
            reload();
            return;
        }

        SplitterTransitionFactory.makeHorizontalSlide(split, 0.0d, 1.0d).play();
        vsp.setVisible(false);
        vsp.setManaged(false);
        split.setDividerPositions(0.0);
        reload();
    }

    /**
     * Return is visible of editor.
     * @return
     */
    private boolean isEditorVisible() {
        return !isNotEditorVisible();
    }

    /**
     * Return isn't visible of editor.
     * @return
     */
    private boolean isNotEditorVisible() {
        return split.getDividerPositions()[0] < 0.1d;
    }

    @Override
    public void loadUrl(final String url) {
        reload();
    }

    @Override
    public void reload() {
        final WebEngine engine = getWebView().getEngine();
        engine.loadContent(this.generator.decorate("Open file", path));
    }

    /**
     * Get this tab's title.
     * @return title
     */
    @Override
    public String getTitle() {
        return path.getFileName().toString();
    }

    @Override
    public String htmlSource() {
        return editor.getText();
    }

    @Override
    public String edit() {
        final Path openTarget = path;
        if (!Files.exists(openTarget)){
            return "File not exists.";
        }

        try {
            final String content = Files.readAllLines(openTarget)
                                        .stream()
                                        .collect(Collectors.joining(Strings.LINE_SEPARATOR));
            editor.replaceText(content);
            switchEditorVisible();
        } catch (final IOException e) {
            LOGGER.error("ERROR!", e);
            return e.getMessage();
        }
        return "";
    }

    @Override
    public boolean isEditing() {
        return isEditorVisible();
    }

    @Override
    public String saveContent() {
        try {
            Files.write(path, editor.getText().getBytes(Defines.ARTICLE_ENCODE));
        } catch (final IOException e) {
            LOGGER.error("Error", e);
            return e.getMessage();
        }
        reload();
        return String.format("Save to file 「%s」", getTitle());
    }

    @Override
    public String getUrl() {
        return path.toUri().toString();
    }

    @Override
    public void setFont(final Font font) {
        editor.setStyle(String.format("-fx-font-family: %s; -fx-font-size: %f;",
                font.getFamily(), font.getSize()));
    }

}
