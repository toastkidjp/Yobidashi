package jp.toastkid.article.control;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import jp.toastkid.article.ArticleGenerator;
import jp.toastkid.article.converter.PostProcessor;
import jp.toastkid.article.models.Article;
import jp.toastkid.article.models.Articles;
import jp.toastkid.jfx.common.transition.SplitterTransitionFactory;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.yobidashi.Config;
import jp.toastkid.yobidashi.Config.Key;
import jp.toastkid.yobidashi.Defines;

/**
 * Article tab.
 *
 * @author Toast kid
 */
public class ArticleTab extends BaseWebTab {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleTab.class);

    /** WebView - Editor y pos. */
    private static final double SCALE_FACTOR = 3.0d;

    /** Article HTML generator. */
    private static final ArticleGenerator GENERATOR = new ArticleGenerator();

    /** Article. */
    private final Article article;

    /** Load action. */
    private final Runnable onLoad;

    /** Content editor. */
    private final CodeArea editor;

    /** Splitter. */
    private final SplitPane split;

    /** Editor's scroll bar. */
    private final VirtualizedScrollPane<CodeArea> vsp;

    /** HTML content. */
    private String content;

    /** WebView's y position. */
    private int yOffset;

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
        this.onLoad = b.onLoad;

        final WebView webView = getWebView();
        initWebView(webView,b.onOpenNewArticle, b.onOpenUrl);

        this.editor = new CodeArea();
        initEditor();
        vsp = new VirtualizedScrollPane<>(editor);
        vsp.estimatedScrollYProperty()
            .addListener((value, prev, next) -> scrollTo(convertToWebViewY(value.getValue().doubleValue())));
        split = new SplitPane(webView, vsp);
        split.setDividerPositions(0.5);

        this.setContent(split);
        switchEditorVisible();

        Optional.ofNullable(b.onContextMenuRequested).ifPresent(webView::setOnContextMenuRequested);

        // 新規タブで開く場合
        this.loadUrl(article.toInternalUrl());
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
     * Convert to Editor y position.
     * @param value
     * @return
     */
    private double convertToEditorY(double value) {
        return value / SCALE_FACTOR;
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
        //engine.setCreatePopupHandler(handler);
        final Worker<Void> loadWorker = engine.getLoadWorker();
        loadWorker.stateProperty().addListener(
                (observable, prev, next) -> {
                    final String url = engine.getLocation();
                    System.out.println(getText() + " " + url + " " + observable.getValue());

                    if (StringUtils.isEmpty(url) && !State.SUCCEEDED.equals(observable.getValue())) {
                        return;
                    }

                    if (url.startsWith("http")) {
                        if (State.SCHEDULED.equals(observable.getValue())) {
                            System.out.println("loader stop ");
                            //loadWorker.cancel();
                            onOpenUrl.accept(LOADING, url);
                            //reload();
                            return;
                        }
                        return;
                    }

                    if (State.SCHEDULED.equals(observable.getValue())) {
                        loadWorker.cancel();
                        System.out.println(getText() + " open new tab " + url);
                        openNewTab(url, onOpenNewArticle, onOpenUrl);
                        return;
                    }

                    if (State.CANCELLED.equals(observable.getValue())) {
                        hideSpinner();
                    }

                    if (State.SUCCEEDED.equals(observable.getValue())) {
                        final String title = engine.getTitle();
                        this.setText(StringUtils.isNotBlank(title) ? title : article.title);
                        onLoad.run();
                        hideSpinner();
                        if (yOffset != 0) {
                            scrollTo(yOffset);
                        }
                    }
                });
        engine.setOnAlert(e -> LOGGER.info(e.getData()));
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

            if (event.isControlDown() || event.getCode().isArrowKey()) {
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
            return;
        }

        yOffset = getYPosition();
        SplitterTransitionFactory.makeHorizontalSlide(split, 1.0d, 1.0d).play();
        vsp.setVisible(false);
        vsp.setManaged(false);
        split.setDividerPositions(1.0);
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
        return 0.9 < split.getDividerPositions()[0];
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
        final PostProcessor post = new PostProcessor(Config.get(Key.ARTICLE_DIR));

        final Path openTarget = this.article.path;
        if (!Files.exists(openTarget)) {
            Articles.generateNewArticle(article);
        }

        final String processed  = post.process(GENERATOR.convertToHtml(this.article));
        final String subheading = post.generateSubheadings();
        content = GENERATOR.decorate(this.article.title, processed, subheading);

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
        final String absPath = article.path.toAbsolutePath().toString();
        if (Files.exists(openTarget)){
            editor.replaceText(FileUtil.getStrFromFile(absPath, Defines.ARTICLE_ENCODE));
            switchEditorVisible();
            return "";
        }

        // ファイルが存在しない場合は、ひな形を元に新規作成する。
        Articles.generateNewArticle(article);
        editor.replaceText(FileUtil.getStrFromFile(absPath, Defines.ARTICLE_ENCODE));
        switchEditorVisible();
        return "";
    }

    @Override
    public boolean isEditing() {
        return isEditorVisible();
    }

    @Override
    public String saveContent() {
        try {
            Files.write(article.path, editor.getText().getBytes(Defines.ARTICLE_ENCODE));
        } catch (final IOException e) {
            LOGGER.error("Error", e);
            return e.getMessage();
        }
        reload();
        return String.format("Save to file 「%s」", article.title);
    }

    @Override
    public String getUrl() {
        return article.toInternalUrl();
    }

}
