/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.article.control;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.controls.JFXSpinner;

import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Worker.State;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import jp.toastkid.article.control.editor.EditorTab;
import jp.toastkid.article.models.Articles;

/**
 * Base of Web tab.
 *
 * @author Toast kid
 *
 */
public abstract class BaseWebTab extends ReloadableTab {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EditorTab.class);

    /** WebView. */
    private final WebView wv;

    /** Loading indicator. */
    private final JFXSpinner spinner;

    /**
     *
     * @param title
     * @param content
     * @param closeAction
     */
    public BaseWebTab(final String title, final Node content, final Consumer<Tab> closeAction) {
        super(title, content, closeAction);
        spinner = new JFXSpinner();
        this.setGraphic(new HBox(this.getGraphic(), spinner));
        wv = new WebView();
        initWebView();
    }

    /**
     * Set up WebView.
     */
    protected void initWebView() {

        wv.setOnKeyPressed(event -> {
            if (!event.isControlDown()) {
                return;
            }
            if (KeyCode.UP.equals(event.getCode())) {
                moveToTop();
                return;
            }
            if (KeyCode.DOWN.equals(event.getCode())) {
                moveToBottom();
                return;
            }
        });

        final WebEngine engine = wv.getEngine();
        engine.getLoadWorker().stateProperty().addListener((value, prev, next) -> {
            final State state = value.getValue();
            switch (state) {
                case SCHEDULED:
                    setText(LOADING);
                    showSpinner();
                    break;
                case FAILED:
                case CANCELLED:
                case SUCCEEDED:
                    final String currentTitle = getText();
                    final String newTitle     = getTitle();
                    setText(StringUtils.isNotEmpty(newTitle) ? newTitle : currentTitle);
                    hideSpinner();
                    break;
                case READY:
                case RUNNING:
                default:
                    break;
            }
        });
        engine.setOnAlert(e -> LOGGER.info(e.getData()));
    }

    @Override
    public boolean canLoadUrl() {
        return true;
    }

    @Override
    public void stop() {
        getWebView().getEngine().getLoadWorker().cancel();
    };

    @Override
    public void print(final PrinterJob job) {
        getWebView().getEngine().print(job);
    }

    /**
     * Highlight keyword.
     * @param word keyword which is wanted highlighting
     * @see <a href="http://aoe-tk.hatenablog.com/entry/2015/06/15/001217">
     * JavaFXのWebViewの検索を実現するのにもっと簡単な方法がありました</a>
     */
    @Override
    public final void highlight(
            final Optional<String> word, final String script) {
        word.ifPresent(keyword ->
            getWebView().getEngine().executeScript(MessageFormat.format(script, keyword)));
    }

    @Override
    public String getUrl() {
        return getWebView().getEngine().getLocation();
    }

    @Override
    public void moveToTop() {
        getWebView().getEngine().executeScript(findScrollTop(getUrl()));
    }

    @Override
    public void moveToBottom() {
        getWebView().getEngine().executeScript(findScrollBottom(getUrl()));
    }

    /**
     * find scroll script.
     * @param url
     * @return
     */
    private String findScrollTop(final String url) {
        return StringUtils.isEmpty(url) || Articles.isInternalLink(url)
                ? "$('html,body').animate({ scrollTop: 0 }, 'fast');"
                : "window.scrollTo(0, 0);";
    }

    /**
     * find scroll script.
     * @param url
     * @return
     */
    private String findScrollBottom(final String url) {
        return StringUtils.isEmpty(url) || Articles.isInternalLink(url)
                ? "$('html,body').animate({ scrollTop: document.body.scrollHeight }, 'fast');"
                : "window.scrollTo(0, document.body.scrollHeight);";
    }

    /**
     * Get HTML source.
     * @return HTML source
     */
    public String htmlSource() {
        return getWebView().getEngine()
                .executeScript("document.getElementsByTagName('html')[0].innerHTML;")
                .toString();
    }

    @Override
    public DoubleProperty zoomProperty() {
        return getWebView().zoomProperty();
    }

    @Override
    public void reload() {
        getWebView().getEngine().reload();
    }

    @Override
    public void loadUrl(final String url) {
        getWebView().getEngine().load(url);
    }

    /**
     * Get WebView.
     * @return WebView
     */
    public WebView getWebView() {
        return wv;
    }

    /**
     * Show spinner.
     */
    protected void showSpinner() {
        spinner.setVisible(true);
        spinner.setManaged(true);
    }

    /**
     * Hide spinner.
     */
    protected void hideSpinner() {
        spinner.setVisible(false);
        spinner.setManaged(false);
    }

    /**
     * Set user agent.
     * @param ua
     */
    public void setUserAgent(final UserAgent ua) {
        if (ua.text() == null) {
            return;
        }
        getWebView().getEngine().setUserAgent(ua.text());
        reload();
    }

}
