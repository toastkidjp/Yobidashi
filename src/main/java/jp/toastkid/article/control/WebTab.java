/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.article.control;

import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import javafx.print.PrinterJob;
import javafx.scene.control.Tab;
import javafx.scene.web.WebView;
import jp.toastkid.article.control.editor.ArticleTab;
import jp.toastkid.article.models.ContentType;

/**
 * This tab use only loading web content.
 *
 * @author Toast kid
 *
 */
public class WebTab extends BaseWebTab {

    /** WebView. */
    private final WebView wv;

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

        private ContentType contentType;

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

        public Builder setContentType(final ContentType contentType) {
            this.contentType = contentType;
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
        wv = getWebView();
        this.setContent(wv);

        if (StringUtils.isNotBlank(b.content)) {
            wv.getEngine().loadContent(b.content, b.contentType.getText());
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

}
