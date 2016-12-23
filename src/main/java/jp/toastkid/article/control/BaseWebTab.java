package jp.toastkid.article.control;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.beans.property.DoubleProperty;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import jp.toastkid.article.models.Defines;

/**
 * Base of Web tab.
 *
 * @author Toast kid
 *
 */
public abstract class BaseWebTab extends ReloadableTab {

    /** WebView. */
    private final WebView wv;

    /**
     *
     * @param title
     * @param content
     * @param closeAction
     */
    public BaseWebTab(final String title, final Node content, final Consumer<Tab> closeAction) {
        super(title, content, closeAction);
        wv = new WebView();
        Optional.ofNullable(getHandler()).ifPresent(wv.getEngine()::setCreatePopupHandler);
    }

    /**
     * Return CreatePopupHandler.
     * @return CreatePopupHandler
     */
    protected abstract Callback<PopupFeatures, WebEngine> getHandler();

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
        return wv.getEngine().getLocation();
    }

    @Override
    public void moveToTop() {
        wv.getEngine()
            .executeScript(findScrollTop(getWebView().getEngine().getLocation()));
    }

    @Override
    public void moveToBottom() {
        wv.getEngine()
            .executeScript(findScrollBottom(getWebView().getEngine().getLocation()));
    }

    /**
     * find scroll script.
     * @param url
     * @return
     */
    private String findScrollTop(final String url) {
        return url.endsWith(Defines.TEMP_FILE_NAME)
                ? "$('html,body').animate({ scrollTop: 0 }, 'fast');"
                : "window.scrollTo(0, 0);";
    }

    /**
     * find scroll script.
     * @param url
     * @return
     */
    private String findScrollBottom(final String url) {
        return url.endsWith(Defines.TEMP_FILE_NAME)
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
        wv.getEngine().reload();
    }

    @Override
    public void loadUrl(final String url) {
        wv.getEngine().load(url);
    }

    /**
     * Get WebView.
     * @return WebView
     */
    public WebView getWebView() {
        return wv;
    }

}
