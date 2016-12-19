package jp.toastkid.wiki.control;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.controls.JFXButton;

import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import jp.toastkid.libs.utils.MathUtil;
import jp.toastkid.wiki.ArticleGenerator;
import jp.toastkid.wiki.lib.PostProcessor;
import jp.toastkid.wiki.models.Article;
import jp.toastkid.wiki.models.Config;
import jp.toastkid.wiki.models.Config.Key;
import jp.toastkid.yobidashi.Controller;

/**
 * Article tab.
 *
 * @author Toast kid
 */
public class ArticleTab extends BaseWebTab {

    /** Default loading tab title. */
    private static final String LOADING = "Now Loading...";

    /** Article HTML generator. */
    private static final ArticleGenerator GENERATOR = new ArticleGenerator();

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleTab.class);

    /** Article. */
    private final Article article;

    /** Load action. */
    private final Runnable onLoad;

    /** CreatePopupHandler. */
    private final Callback<PopupFeatures, WebEngine> handler;

    /** HTML content. */
    private String content;

    /**
     * {@link ArticleTab}'s builder.
     *
     * @author Toast kid
     */
    public static class Builder {

        private Article article;

        private EventHandler<ContextMenuEvent> onContextMenuRequested;

        private Callback<PopupFeatures, WebEngine> createPopupHandler;

        private Consumer<Tab> closeAction;

        private Runnable onLoad;

        private Callback<PopupFeatures, WebEngine> handler;

        private Consumer<Article> onOpenNewArticle;

        private BiConsumer<String, String> onOpenUrl;

        public Node makeContent() {
            return null;
        }

        public Builder setArticle(final Article article) {
            this.article = article;
            return this;
        }

        public Builder setOnClose(final Consumer<Tab> closeAction) {
            this.closeAction = closeAction;
            return this;
        }

        public Builder setOnContextMenuRequested(final EventHandler<ContextMenuEvent> e) {
            this.onContextMenuRequested = e;
            return this;
        }

        public Builder setCreatePopupHandler(
                final Callback<PopupFeatures, WebEngine> createPopupHandler) {
            this.createPopupHandler = createPopupHandler;
            return this;
        }

        public Builder setOnLoad(final Runnable onLoad) {
            this.onLoad = onLoad;
            return this;
        }

        public Builder setHandler(final Callback<PopupFeatures, WebEngine> handler) {
            this.handler = handler;
            return this;
        }

        public ArticleTab build() {
            return new ArticleTab(this);
        }

        public Builder setOnOpenNewArticle(final Consumer<Article> onOpenNewArticle) {
            this.onOpenNewArticle = onOpenNewArticle;
            return this;
        }

        public Builder setOnOpenUrl(final BiConsumer<String, String> onOpenUrl) {
            this.onOpenUrl = onOpenUrl;
            return this;
        }

    }

    /**
     * Call only internal.
     * @param b Builder
     */
    private ArticleTab(final Builder b) {
        super(b.article.title, b.makeContent(), b.closeAction);
        this.article = b.article;
        this.handler = b.handler;

        Optional.ofNullable(b.closeAction).ifPresent(action -> {
            final Button closeButton = new JFXButton("x");
            closeButton.setOnAction(e -> action.accept(this));
            this.setGraphic(closeButton);
        });

        this.onLoad = b.onLoad;

        final WebView webView = getWebView();
        final WebEngine engine = webView.getEngine();
        final Worker<Void> loadWorker = engine.getLoadWorker();
        loadWorker.stateProperty().addListener(
                (observable, prev, next) -> {
                    final String url = engine.getLocation();
                    //System.out.println(url + " " + observable.getValue() +" " + prev.name() + " " + next.name());

                    if (StringUtils.isEmpty(url) && !State.SUCCEEDED.equals(observable.getValue())) {
                        return;
                    }

                    if (State.SCHEDULED.equals(observable.getValue())) {
                        loadWorker.cancel();
                        openNewTab(url, b.onOpenNewArticle, b.onOpenUrl);
                        return;
                    }

                    if (State.SUCCEEDED.equals(observable.getValue())) {
                        final String title = engine.getTitle();
                        this.setText(StringUtils.isNotBlank(title)
                                ? title : article.title);
                        onLoad.run();
                        final int j = this.article.yOffset;
                        if (j == 0) {
                            return;
                        }
                        engine.executeScript(String.format("window.scrollTo(0, %d);", j));
                    }
                });

        this.setContent(webView);
        Optional.ofNullable(b.onContextMenuRequested).ifPresent(webView::setOnContextMenuRequested);

        // 新規タブで開く場合
        //Optional.ofNullable(b.createPopupHandler).ifPresent(engine::setCreatePopupHandler);
        //engine.setJavaScriptEnabled(true);
        engine.setOnAlert(e -> LOGGER.info(e.getData()));
        this.loadUrl(article.toInternalUrl());
    }

    /**
     *
     * @param url
     * @param onOpenNewArticle
     * @param onOpenUrl
     */
    private void openNewTab(
            final String url,
            final Consumer<Article> onOpenNewArticle,
            final BiConsumer<String, String> onOpenUrl
            ) {

        if (StringUtils.isBlank(url)) {
            return;
        }

        if (Article.isWikiArticleUrl(url)) {
            onOpenNewArticle.accept(Article.findFromUrl(url));
            return;
        }
        onOpenUrl.accept(LOADING, url);
    }

    @Override
    public void loadUrl(final String url) {
        loadUrl(url, false);
    }

    /**
     * Load URL.
     * @param url
     * @param isReload
     */
    public void loadUrl(final String url, final boolean isReload) {
        final WebEngine engine = getWebView().getEngine();
        int yOffset = 0;
        if (isReload) {
            final Object script = engine.executeScript("window.pageYOffset;");
            yOffset = script != null ? MathUtil.parseOrZero(script.toString()) : 0;
        }
        loadArticle(isReload, yOffset);
    }

    /**
     * Load specified article with options.
     * @param isReload
     * @param yOffset
     */
    private void loadArticle(
            final boolean isReload,
            final int yOffset
            ) {
        final PostProcessor post = new PostProcessor(Config.get(Key.ARTICLE_DIR));

        final File openTarget = this.article.file;
        if (!openTarget.exists()) {
            try {
                openTarget.createNewFile();
                Files.write(openTarget.toPath(), ArticleGenerator.makeNewContent(this.article));
            } catch (final IOException e) {
                LOGGER.error("Error", e);;
            }
            Controller.openFileByEditor(openTarget);
        }

        final String processed   = post.process(GENERATOR.wiki2Html(openTarget.getAbsolutePath()));
        final String subheading  = post.generateSubheadings();
        content = GENERATOR.convertHtml(this.article.title, processed, subheading);

        final WebEngine engine = getWebView().getEngine();
        engine.loadContent(content);
        if (isReload) {
            engine.executeScript(String.format("window.scrollTo(0, %d);", yOffset));
        }
    }

    @Override
    public void reload() {
        loadUrl("", true);
    }

    /**
     * Get this tab's WebView.
     * @return
     */
    @Override
    public WebView getWebView() {
        return super.getWebView();
    }

    /**
     * Get this tab's article.
     * @return
     */
    public Article getArticle() {
        return article;
    }

    /**
     * Get this tab's title.
     * @return title
     */
    @Override
    public String getTitle() {
        return article.title;
    }

    @Override
    public String htmlSource() {
        return content != null ? content : super.htmlSource();
    }

    @Override
    protected Callback<PopupFeatures, WebEngine> getHandler() {
        return this.handler;
    }

}
