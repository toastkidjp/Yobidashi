package jp.toastkid.wiki.control;

import java.util.Optional;
import java.util.function.Consumer;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Tab;

/**
 * Content tab.
 *
 * @author Toast kid
 */
public class ContentTab extends ReloadableTab {

    /**
     * {@link ArticleTab}'s builder.
     *
     * @author Toast kid
     */
    public static class Builder {

        private String title;

        private Consumer<Tab> closeAction;

        private Node content;

        public Builder setTitle(final String title) {
            this.title = title;
            return this;
        }

        public Builder setContent(final Node content) {
            this.content = content;
            return this;
        }

        public Builder setOnClose(final Consumer<Tab> closeAction) {
            this.closeAction = closeAction;
            return this;
        }

        public ContentTab build() {
            return new ContentTab(this);
        }

    }

    private ContentTab(final Builder b) {
        super(b.title, b.content, b.closeAction);

    }

    @Override
    public void reload() {
        // NOP.
    }

    @Override
    public void loadUrl(String url) {
        // NOP.
    }

    @Override
    public boolean canLoadUrl() {
        return false;
    }

    @Override
    public void print(final PrinterJob job) {
        // NOP
    }

    @Override
    public String getUrl() {
        return "";
    }

    @Override
    public void moveToTop() {
        // NOP.
    }

    @Override
    public void moveToBottom() {
        // NOP.
    }

    @Override
    public void stop() {
        // NOP.
    }

    @Override
    public void highlight(Optional<String> word, String script) {
        // NOP.
    }

    @Override
    public DoubleProperty zoomProperty() {
        return new SimpleDoubleProperty(1.0d);
    }

    @Override
    public String getTitle() {
        return this.getText();
    }

}
