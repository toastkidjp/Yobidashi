package jp.toastkid.article.control.editor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.text.Font;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import jp.toastkid.article.ArticleGenerator;
import jp.toastkid.article.control.BaseWebTab;
import jp.toastkid.article.converter.PostProcessor;
import jp.toastkid.article.models.Article;
import jp.toastkid.article.models.Articles;
import jp.toastkid.libs.utils.Strings;

/**
 * Article tab.
 *
 * @author Toast kid
 */
public class ArticleTab extends BaseWebTab implements Editable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleTab.class);

    /** Article generator. */
    private final ArticleGenerator generator;

    /** Article. */
    private final Article article;

    /** Load action. */
    private final Runnable onLoad;

    /** Content editor. */
    private final Editor editor;

    /** HTML content. */
    private String content;

    /** WebView's y position. */
    private int yOffset;

    /** Messenger, */
    private Subject<String> messenger;

    /** Disposable of subscription. */
    private Disposable disposable;

    /** Article post processor. */
    private PostProcessor post;

    /**
     * {@link ArticleTab}'s builder.
     *
     * @author Toast kid
     */
    public static class Builder {

        private Article article;

        private EventHandler<ContextMenuEvent> onContextMenuRequested;

        private Consumer<Tab> closeAction;

        private Runnable onLoad;

        private io.reactivex.functions.Consumer<String> onOpenNewArticle;

        private ArticleGenerator generator;

        private Callback<PopupFeatures, WebEngine> popupHandler;

        private PostProcessor postProcessor;

        public Node makeContent() {
            return null;
        }

        public Builder setArticleGenerator(final ArticleGenerator generator) {
            this.generator = generator;
            return this;
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

        public Builder setOnLoad(final Runnable onLoad) {
            this.onLoad = onLoad;
            return this;
        }

        public Builder setOnOpenNewArticle(final io.reactivex.functions.Consumer<String> onOpenNewArticle) {
            this.onOpenNewArticle = onOpenNewArticle;
            return this;
        }

        public Builder serPostProcessor(final PostProcessor postProcessor) {
            this.postProcessor = postProcessor;
            return this;
        }

        public Builder setPopupHandler(final Callback<PopupFeatures, WebEngine> popupHandler) {
            this.popupHandler = popupHandler;
            return this;
        }

        public ArticleTab build() {
            return new ArticleTab(this);
        }

    }

    /**
     * Call only internal.
     * @param b Builder
     */
    private ArticleTab(final Builder b) {
        super(b.article.title, b.makeContent(), b.closeAction);
        this.article    = b.article;
        this.onLoad     = b.onLoad;
        this.generator  = b.generator;
        this.post       = b.postProcessor;
        this.messenger  = PublishSubject.create();

        final WebView webView = getWebView();
        initWebView(webView, b.popupHandler);

        this.editor = new Editor(this.article.path, webView);
        editor.setModifiedListener((v, prev, next) -> {
            if (v.getValue()) {
                setText("* " + getTitle());
                return;
            }
            setText(getTitle());
        });

        this.setContent(editor.getNode());
        editor.switchEditorVisible();

        Optional.ofNullable(b.onContextMenuRequested).ifPresent(webView::setOnContextMenuRequested);

        // 新規タブで開く場合
        this.loadUrl(article.toInternalUrl());

        disposable = messenger
            .filter(StringUtils::isNotBlank)
            .filter(Articles::isInternalLink)
            .subscribe(b.onOpenNewArticle);
    }

    /**
     * Initialize WebView.
     * @param webView
     * @param onOpenNewArticle
     * @param popupHandler
     */
    private void initWebView(
            final WebView webView,
            final Callback<PopupFeatures, WebEngine> popupHandler
            ) {
        final WebEngine engine = webView.getEngine();
        engine.setCreatePopupHandler(popupHandler);
        final Worker<Void> loadWorker = engine.getLoadWorker();
        loadWorker.stateProperty().addListener((observable, prev, next) -> {
                    if (!State.SUCCEEDED.equals(observable.getValue())) {
                        return;
                    }
                    onLoad.run();
                    if (yOffset != 0) {
                        editor.scrollTo(yOffset);
                    }
                });
        engine.locationProperty().addListener((value, prev, next) -> {
            final String url = value.getValue();
            messenger.onNext(url);
            setText(article.title);
        });
        engine.setOnAlert(e -> LOGGER.info(e.getData()));
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
        loadUrl(url, false);
    }

    /**
     * Load URL.
     * @param url
     * @param isReload
     */
    public void loadUrl(final String url, final boolean isReload) {
        yOffset = isReload ? editor.getYPosition() : 0;
        loadArticle();
    }

    /**
     * Load specified article with options.
     */
    private void loadArticle() {
        final String processed  = post.process(generator.convertToHtml(this.article));
        final String subheading = post.generateSubheadings();
        content = generator.decorate(getTitle(), processed, subheading);

        final WebEngine engine = getWebView().getEngine();
        engine.loadContent(content);
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
    public void setFont(final Font font) {
        editor.setFont(font);
    }

    @Override
    public String edit() {
        final Path openTarget = article.path;

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
        return String.format("Save to file 「%s」", article.title);
    }

    @Override
    public String getUrl() {
        return article.toInternalUrl();
    }

    /**
     * Dispose messenger's subscription.
     */
    public void close() {
        disposable.dispose();
    }

}
