package jp.toastkid.wiki.control;

import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;

/**
 * This tab use only loading web content.
 *
 * @author Toast kid
 *
 */
public class WebTab extends BaseWebTab {

    /** WebView. */
    private final WebView wv;

    /** CreatePopupHandler. */
    private Callback<PopupFeatures, WebEngine> handler;

    /** Content's mimetype. */
    private String mimetype;

    /**
     * {@link ArticleTab}'s builder.
     *
     * @author Toast kid
     */
    public static class Builder {

        private String title;

        private String url;

        private Consumer<Tab> closeAction;

        private String content;

        private Callback<PopupFeatures, WebEngine> handler;

        private String mimetype;

        public Node makeContent() {
            return null;
        }

        public Builder setTitle(final String title) {
            this.title = title;
            return this;
        }

        public Builder setUrl(final String url) {
            this.url = url;
            return this;
        }

        public Builder setOnClose(final Consumer<Tab> closeAction) {
            this.closeAction = closeAction;
            return this;
        }

        public Builder setContent(final String content) {
            this.content = content;
            return this;
        }

        public Builder setHandler(final Callback<PopupFeatures, WebEngine> handler) {
            this.handler = handler;
            return this;
        }

        public Builder setMimetype(final String mimetype) {
            this.mimetype = mimetype;
            return this;
        }

        public WebTab build() {
            return new WebTab(this);
        }

    }

    /**
     * Call from only internal.
     * @param b
     */
    private WebTab(final Builder b) {
        super(b.title, null, b.closeAction);
        this.handler = b.handler;
        this.mimetype = StringUtils.isNotBlank(b.mimetype) ? b.mimetype : "text/html";
        wv = getWebView();
        this.setContent(wv);
        if (StringUtils.isNotBlank(b.content)) {
            wv.getEngine().loadContent(b.content, this.mimetype);
            return;
        }
        loadUrl(b.url);
    }

    @Override
    public void reload() {
        wv.getEngine().reload();
    }

    @Override
    public void loadUrl(final String url) {
        wv.getEngine().load(url);
    }

    @Override
    public void print(PrinterJob job) {
        // TODO implement
    }

    @Override
    public String getUrl() {
        return wv.getEngine().getLocation();
    }

    @Override
    public String getTitle() {
        return wv.getEngine().getTitle();
    }

    @Override
    protected Callback<PopupFeatures, WebEngine> getHandler() {
        return this.handler;
    }

}
