package jp.toastkid.article.control;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javafx.PlatformUtil;

import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.IndexRange;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.InputMethodTextRun;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Window;
import jp.toastkid.article.ArticleGenerator;
import jp.toastkid.article.converter.PostProcessor;
import jp.toastkid.article.models.Article;
import jp.toastkid.article.models.Articles;
import jp.toastkid.jfx.common.transition.SplitterTransitionFactory;
import jp.toastkid.libs.utils.Strings;
import jp.toastkid.yobidashi.models.Config;
import jp.toastkid.yobidashi.models.Config.Key;
import jp.toastkid.yobidashi.models.Defines;

/**
 * Article tab.
 *
 * @author Toast kid
 */
public class ArticleTab extends BaseWebTab implements Editable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleTab.class);

    /** WebView - Editor y pos. */
    private static final double SCALE_FACTOR = 3.0d;

    /** Article HTML generator. */
    private final ArticleGenerator generator;

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

        this.editor = new CodeArea();
        initEditor();
        vsp = new VirtualizedScrollPane<>(editor);
        vsp.estimatedScrollYProperty()
            .addListener((value, prev, next) -> scrollTo(convertToWebViewY(value.getValue().doubleValue())));
        vsp.setPrefHeight(700.0d);
        split = new SplitPane(vsp, webView);
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
                    //System.out.println(getText() + " " + url + " " + observable.getValue());

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

            if (event.isControlDown()) {
                return;
            }

            if (!event.getCode().isLetterKey()
                    && !event.getCode().isDigitKey()
                    && !event.getCode().isWhitespaceKey()) {
                return;
            }
            setText("* " + getText());
        });

        if (editor.getOnInputMethodTextChanged() == null) {
            editor.setOnInputMethodTextChanged(event -> {
                handleInputMethodEvent(event);
            });
}

        editor.setInputMethodRequests(new InputMethodRequests() {
            @Override public Point2D getTextLocation(int offset) {
                final Scene scene = editor.getScene();
                final Window window = scene.getWindow();
                // Don't use imstart here because it isn't initialized yet.
               // Rectangle2D characterBounds = getCharacterBounds(editor.getSelection().getStart() + offset);
                //Point2D p = editor.localToScene(characterBounds.getMinX(), characterBounds.getMaxY());
                final Point2D location = new Point2D(window.getX() + scene.getX() ,
                                               window.getY() + scene.getY() );
                return location;
            }

            @Override
            public int getLocationOffset(int x, int y) {
                return 0;
            }

            @Override
            public void cancelLatestCommittedText() {
                // TODO
            }

            @Override
            public String getSelectedText() {
                final IndexRange selection = editor.getSelection();

                return editor.getText(selection.getStart(), selection.getEnd());
            }
        });
    }

    // Start/Length of the text under input method composition
    private int imstart;
    private int imlength;
    // Holds concrete attributes for the composition runs
    private final List<Shape> imattrs = new java.util.ArrayList<Shape>();

    protected void handleInputMethodEvent(InputMethodEvent event) {
        if (editor.isEditable() && !editor.isDisabled()) {

            // just replace the text on iOS
            if (PlatformUtil.isIOS()) {
               editor.replaceText(event.getCommitted());
               return;
            }

            // remove previous input method text (if any) or selected text
            if (imlength != 0) {
                //removeHighlight(imattrs);
                imattrs.clear();
                editor.selectRange(imstart, imstart + imlength);
            }

            // Insert committed text
            if (event.getCommitted().length() != 0) {
                final String committed = event.getCommitted();
                editor.replaceText(editor.getSelection(), committed);
            }

            // Replace composed text
            imstart = editor.getSelection().getStart();
            final StringBuilder composed = new StringBuilder();
            for (final InputMethodTextRun run : event.getComposed()) {
                composed.append(run.getText());
            }
            editor.replaceText(editor.getSelection(), composed.toString());
            imlength = composed.length();
            if (imlength != 0) {
                int pos = imstart;
                for (final InputMethodTextRun run : event.getComposed()) {
                    final int endPos = pos + run.getText().length();
                    //createInputMethodAttributes(run.getHighlight(), pos, endPos);
                    pos = endPos;
                }
                //addHighlight(imattrs, imstart);

                // Set caret position in composed text
                final int caretPos = event.getCaretPosition();
                if (caretPos >= 0 && caretPos < imlength) {
                    editor.selectRange(imstart + caretPos, imstart + caretPos);
                }
            }
        }
    }

    @Override
    public void setFont(final Font font) {
        editor.setStyle(String.format("-fx-font-family: %s; -fx-font-size: %f;",
                font.getFamily(), font.getSize()));
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
        SplitterTransitionFactory.makeHorizontalSlide(split, 0.0d, 1.0d).play();
        vsp.setVisible(false);
        vsp.setManaged(false);
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
            editor.replaceText(content);
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
