package jp.toastkid.article.control.editor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.Tab;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import jp.toastkid.article.ArticleGenerator;
import jp.toastkid.article.control.BaseWebTab;
import jp.toastkid.libs.utils.Strings;
import jp.toastkid.yobidashi.models.Config;

/**
 * Editor tab.
 *
 * @author Toast kid
 *
 */
public class EditorTab extends BaseWebTab implements Editable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EditorTab.class);

    /** Markdown editor. */
    private final Editor editor;

    /** Article generator. */
    private final ArticleGenerator generator;

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

        this.generator  = new ArticleGenerator(b.conf);

        this.editor = new Editor(b.path, getWebView());
        editor.setModifiedListener((v, prev, next) -> {
            if (v.getValue()) {
                setText("* " + getTitle());
                return;
            }
            setText(getTitle());
        });

        this.setContent(editor.getNode());
        editor.switchEditorVisible();
    }

    /**
     * Return is visible of editor.
     * @return
     */
    private boolean isEditorVisible() {
        return !editor.isNotEditorVisible();
    }

    @Override
    public void loadUrl(final String url) {
        reload();
    }

    @Override
    public void reload() {
        final WebEngine engine = getWebView().getEngine();
        engine.loadContent(this.generator.decorate("Open file", editor.getPath()));
    }

    /**
     * Get this tab's title.
     * @return title
     */
    @Override
    public String getTitle() {
        return editor.getPath().getFileName().toString();
    }

    @Override
    public String htmlSource() {
        return editor.getContent();
    }

    @Override
    public String edit() {
        final Path openTarget = editor.getPath();
        if (!Files.exists(openTarget)){
            return "File not exists.";
        }

        try {
            final String content = Files.readAllLines(openTarget)
                                        .stream()
                                        .collect(Collectors.joining(Strings.LINE_SEPARATOR));
            editor.setContent(content);
            editor.switchEditorVisible();
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

        final String result = editor.saveContent();
        if (!result.isEmpty()) {
            return result;
        }

        reload();
        return String.format("Save to file 「%s」", getTitle());
    }

    @Override
    public String getUrl() {
        return editor.getPath().toUri().toString();
    }

    @Override
    public void setFont(final Font font) {
        editor.setFont(font);
    }

}
