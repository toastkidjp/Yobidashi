package jp.toastkid.article.control.editor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import jp.toastkid.article.ArticleGenerator;
import jp.toastkid.article.control.BaseWebTab;
import jp.toastkid.article.converter.PostProcessor;
import jp.toastkid.article.models.Article;
import jp.toastkid.article.models.Articles;
import jp.toastkid.jfx.common.transition.SplitterTransitionFactory;
import jp.toastkid.libs.utils.Strings;
import jp.toastkid.yobidashi.models.Config;
import jp.toastkid.yobidashi.models.Config.Key;

/**
 * Article tab.
 *
 * @author Toast kid
 */
public class ArticleTab extends BaseWebTab implements Editable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleTab.class);

    /** Article HTML generator. */
    private final ArticleGenerator generator;

    /** Article. */
    private final Article article;

    /** Load action. */
    private final Runnable onLoad;

    /** Content editor. */
    private final Editor editor;

    /** Splitter. */
    private final SplitPane split;

    /** HTML content. */
    private String content;

    /** WebView's y position. */
    private int yOffset;

    /** Dir of article. */
    private final String articleDir;

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

        private Consumer<Article> onOpenNewArticle;

        private BiConsumer<String, String> onOpenUrl;

        private Config conf;

        public Node makeContent() {
            return null;
        }

        public Builder setConfig(final Config conf) {
            this.conf = conf;
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
        this.generator = new ArticleGenerator(b.conf);
        this.articleDir = b.conf.get(Key.ARTICLE_DIR);
        this.onLoad = b.onLoad;

        final WebView webView = getWebView();
        initWebView(webView,b.onOpenNewArticle, b.onOpenUrl);

        this.editor = new Editor(this.article.path);
        editor.getScrollEmitter().subscribe(this::scrollTo);
        editor.setModifiedListener((v, prev, next) -> {
            if (v.getValue()) {
                setText("* " + getTitle());
                return;
            }
            setText(getTitle());
        });

        split = new SplitPane(editor.getNode(), webView);
        split.setDividerPositions(0.5);

        this.setContent(split);
        switchEditorVisible();

        Optional.ofNullable(b.onContextMenuRequested).ifPresent(webView::setOnContextMenuRequested);

        // 新規タブで開く場合
        this.loadUrl(article.toInternalUrl());
    }

    /**
     * Initialize WebView.
     * @param webView
     * @param onOpenNewArticle
     * @param onOpenUrl
     */
    private void initWebView(
            final WebView webView,
            final Consumer<Article> onOpenNewArticle,
            final BiConsumer<String, String> onOpenUrl
            ) {
        final WebEngine engine = webView.getEngine();
        final Worker<Void> loadWorker = engine.getLoadWorker();
        loadWorker.stateProperty().addListener(
                (observable, prev, next) -> {
                    final String url = engine.getLocation();

                    if (StringUtils.isEmpty(url) && !State.SUCCEEDED.equals(observable.getValue())) {
                        return;
                    }

                    if (url.startsWith("http")) {
                        if (State.SCHEDULED.equals(observable.getValue())) {
                            onOpenUrl.accept(LOADING, url);
                            reload();
                            return;
                        }
                        return;
                    }

                    if (State.SCHEDULED.equals(observable.getValue())) {
                        loadWorker.cancel();
                        openNewTab(url, onOpenNewArticle, onOpenUrl);
                        setText(article.title);
                        return;
                    }

                    if (State.SUCCEEDED.equals(observable.getValue())) {
                        onLoad.run();
                        if (yOffset != 0) {
                            scrollTo(yOffset);
                        }
                        return;
                    }
                });
        engine.setOnAlert(e -> LOGGER.info(e.getData()));
    }

    @Override
    public void setFont(final Font font) {
        editor.setFont(font);
    }

    /**
     * Switch editor's visibility.
     */
    private void switchEditorVisible() {
        if (isNotEditorVisible()) {
            SplitterTransitionFactory.makeHorizontalSlide(split, 0.5d, 1.0d).play();
            split.setDividerPositions(0.5);
            editor.show();
            return;
        }

        yOffset = getYPosition();
        SplitterTransitionFactory.makeHorizontalSlide(split, 0.0d, 1.0d).play();
        editor.hide();
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

        if (Articles.isInternalLink(url)) {
            onOpenNewArticle.accept(Articles.findByUrl(url));
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
        yOffset = isReload ? getYPosition() : 0;
        loadArticle();
    }

    /**
     * Load specified article with options.
     */
    private void loadArticle() {
        final PostProcessor post = new PostProcessor(articleDir);

        final Path openTarget = this.article.path;
        if (!Files.exists(openTarget)) {
            Articles.generateNewArticle(article);
        }

        final String processed  = post.process(generator.convertToHtml(this.article));
        final String subheading = post.generateSubheadings();
        content = generator.decorate(this.article.title, processed, subheading);

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
    public String edit() {
        final Path openTarget = article.path;
        if (!Files.exists(openTarget)){
            // ファイルが存在しない場合は、ひな形を元に新規作成する。
            Articles.generateNewArticle(article);
        }

        try {
            final String content = Files.readAllLines(openTarget)
                                        .stream()
                                        .collect(Collectors.joining(Strings.LINE_SEPARATOR));
            editor.setContent(content);
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

}
