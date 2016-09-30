package jp.toastkid.yobidashi;

import java.awt.Desktop;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextArea;
import com.sun.javafx.scene.control.skin.ContextMenuContent;
import com.sun.javafx.scene.control.skin.ContextMenuContent.MenuItemContainer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;
import jp.toastkid.chart.ChartPane;
import jp.toastkid.dialog.AlertDialog;
import jp.toastkid.dialog.ProgressDialog;
import jp.toastkid.jfx.common.Style;
import jp.toastkid.jfx.common.control.AutoCompleteTextField;
import jp.toastkid.libs.WebServiceHelper;
import jp.toastkid.libs.archiver.ZipArchiver;
import jp.toastkid.libs.utils.AobunUtils;
import jp.toastkid.libs.utils.CalendarUtil;
import jp.toastkid.libs.utils.CollectionUtil;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.libs.utils.HtmlUtil;
import jp.toastkid.libs.utils.MathUtil;
import jp.toastkid.libs.utils.RuntimeUtil;
import jp.toastkid.libs.utils.Strings;
import jp.toastkid.libs.wiki.Wiki2Markdown;
import jp.toastkid.rss.RssFeeder;
import jp.toastkid.wiki.ApplicationState;
import jp.toastkid.wiki.Archiver;
import jp.toastkid.wiki.ArticleGenerator;
import jp.toastkid.wiki.EpubGenerator;
import jp.toastkid.wiki.FullScreen;
import jp.toastkid.wiki.control.ArticleListCell;
import jp.toastkid.wiki.dialog.ConfigDialog;
import jp.toastkid.wiki.jobs.FileWatcherJob;
import jp.toastkid.wiki.models.Article;
import jp.toastkid.wiki.models.Article.Extension;
import jp.toastkid.wiki.models.Config;
import jp.toastkid.wiki.models.Defines;
import jp.toastkid.wiki.search.FileSearcher;
import jp.toastkid.wiki.search.SearchResult;
import jp.toastkid.wordcloud.FxWordCloud;
import jp.toastkid.wordcloud.JFXMasonryPane2;
import reactor.core.publisher.Mono;

/**
 * JavaFX WikiClient's Controller.
 *
 * @author Toast kid
 *
 */
public final class Controller implements Initializable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    /** log file. */
    private static final String PATH_APP_LOG     = Defines.LOG_DIR    + "/app.log";

    /** about file. */
    private static final String PATH_ABOUT_APP   = "README.md";

    /** 「リロード」ボタンの画像ファイルへのパス */
    private static final String PATH_IMG_RELOAD  = Defines.ASSETS_DIR + "/images/reload.png";

    /** 「検索」画像ファイルへのパス. */
    private static final String PATH_IMG_SEARCH  = Defines.ASSETS_DIR + "/images/search.png";

    /** default divider's position. */
    private static final double DEFAULT_DIVIDER_POSITION = 0.2;

    /** WebView's highliting. */
    private static final String WINDOW_FIND_DOWN
        = "window.find(\"{0}\", false, false, true, false, true, false)";

    /** WebView's highliting. */
    private static final String WINDOW_FIND_UP
        = "window.find(\"{0}\", false, true, true, false, true, false)";

    /** 左のリストで中心をいくつずらすか. */
    private static final int FOCUS_MARGIN = 10;

    /** searcher appear keyboard shortcut. */
    private static final KeyCodeCombination APPEAR_SEARCHER
        = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);

    /** scripter appear keyboard shortcut. */
    //private static final KeyCodeCombination APPEAR_SCRIPTER
    //    = new KeyCodeCombination(KeyCode.K, KeyCombination.CONTROL_DOWN);

    /** run script keyboard shortcut. */
    //private static final KeyCodeCombination RUN_SCRIPT
    //    = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);

    /** Zoom increment keyboard shortcut. */
    private static final KeyCodeCombination ZOOM_INCREMENT
        = new KeyCodeCombination(KeyCode.SEMICOLON, KeyCombination.CONTROL_DOWN);

    /** Zoom decrement keyboard shortcut. */
    private static final KeyCodeCombination ZOOM_DECREMENT
        = new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN);

    /** Show left pane. */
    private static final KeyCodeCombination SHOW_LEFT_PANE
        = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN);

    /** Hide left pane. */
    private static final KeyCodeCombination HIDE_LEFT_PANE
        = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN);

    /** select tab. */
    private static final KeyCodeCombination FIRST_TAB
        = new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.CONTROL_DOWN);

    /** select tab. */
    private static final KeyCodeCombination SECOND_TAB
        = new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.CONTROL_DOWN);

    /** select tab. */
    private static final KeyCodeCombination THIRD_TAB
        = new KeyCodeCombination(KeyCode.DIGIT3, KeyCombination.CONTROL_DOWN);

    /** select tab. */
    private static final KeyCodeCombination FOURTH_TAB
        = new KeyCodeCombination(KeyCode.DIGIT4, KeyCombination.CONTROL_DOWN);

    /** select tab. */
    private static final KeyCodeCombination FIFTH_TAB
        = new KeyCodeCombination(KeyCode.DIGIT5, KeyCombination.CONTROL_DOWN);

    /** select tab. */
    private static final KeyCodeCombination SIXTH_TAB
        = new KeyCodeCombination(KeyCode.DIGIT6, KeyCombination.CONTROL_DOWN);

    /** select tab. */
    private static final KeyCodeCombination SEVENTH_TAB
        = new KeyCodeCombination(KeyCode.DIGIT7, KeyCombination.CONTROL_DOWN);

    /** select tab. */
    private static final KeyCodeCombination EIGHTH_TAB
        = new KeyCodeCombination(KeyCode.DIGIT8, KeyCombination.CONTROL_DOWN);

    /** select tab. */
    private static final KeyCodeCombination NINTH_TAB
        = new KeyCodeCombination(KeyCode.DIGIT9, KeyCombination.CONTROL_DOWN);

    /** header. */
    @FXML
    public HBox header;

    /** footer. */
    @FXML
    public HBox footer;

    /** URL 入力エリア. */
    @FXML
    public TextField urlText;
    /** 左側のタブ(記事一覧/履歴ほか). */
    @FXML
    public TabPane leftTabs;
    /** 右側のタブ(WebView). */
    @FXML
    public TabPane tabPane;
    /** 記事一覧. */
    @FXML
    public ListView<Article> articleList;
    /** 履歴一覧. */
    @FXML
    public ListView<Article> historyList;
    /** 画面下部のステータスラベル. */
    @FXML
    public Label status;
    /** 検索種別セレクタ. */
    @SuppressWarnings("rawtypes")
    @FXML
    public ComboBox searchKind;
    /** Web 検索のクエリを記入する部分. */
    @FXML
    public TextField webQuery;
    /** グラフ種別セレクタ. */
    @SuppressWarnings("rawtypes")
    @FXML
    public ComboBox graphKind;
    /** 月セレクタ. */
    @FXML
    public ComboBox<String> month;
    /** スプリッタ―. */
    @FXML
    public SplitPane splitter;

    /** リロードボタン. */
    @FXML
    public Button reload;
    /** Web search button. */
    @FXML
    public Button webSearch;

    /** Zoom Controller. */
    @FXML
    public Slider zoom;
    /** Specify zoom rate. */
    @FXML
    public TextField zoomInput;
    /** Stylesheet selector. */
    @FXML
    public ComboBox<String> style;

    /** in article searcher area. */
    @FXML
    public HBox searcherArea;
    /** in article searcher input box. */
    @FXML
    public TextField searcherInput;

    /** calendar. */
    @FXML
    public DatePicker calendar;

    /** 主要エリア. */
    @FXML
    public VBox mainArea;

    /** for desktop control. */
    private static Desktop desktop;
    /** functions class. */
    private ArticleGenerator func;

    /** Stage. */
    private Stage stage;

    /** width. */
    private double width;
    /** height. */
    private double height;

    /** Music Player's controller. */
    @FXML
    private jp.toastkid.music.Controller  musicController;

    /** NameMaker's controller. */
    @FXML
    private jp.toastkid.name.Controller   nameController;

    /** BMI area's controller. */
    @FXML
    private jp.toastkid.bmi.Controller bmiController;

    /** search history. */
    private final TextField queryInput
        = new AutoCompleteTextField(){{setPromptText("検索キーワードを入力");}};

    /** filter input. */
    private final TextField filterInput
        = new AutoCompleteTextField(){{setPromptText("記事名の一部を入力");}};

    /** for auto backup. */
    private static final ExecutorService BACKUP = Executors.newSingleThreadExecutor();

    /** file watcher. */
    private static final FileWatcherJob FILE_WATCHER = new FileWatcherJob();

    /** use for full screen. */
    private FullScreen fs;

    /** use for draw word-cloud. */
    private FxWordCloud wordCloud;

    @Override
    public final void initialize(final URL url, final ResourceBundle bundle) {

        // initialize parallel task.
        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        final ExecutorService es = Executors.newFixedThreadPool(
                availableProcessors + availableProcessors + availableProcessors);

        es.submit(() -> {
            while (true) {
                final String text = String.format(
                        "Memory using: (%,3d[Byte])",
                        RuntimeUtil.calcUsedMemorySize()
                        );
                Platform.runLater(() -> status.setText(text));
                Thread.sleep(5000L);
            }
        });

        final ProgressDialog pd
            = new ProgressDialog.Builder().setText("Activation in progress...").build();
        pd.start(stage);

        urlText.setText(Defines.DEFAULT_HOME);
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            pd.addProgress(1);
        }

        pd.addText("availableProcessors = " + availableProcessors);

        es.execute(() -> {
            final long start = System.currentTimeMillis();
            prepareArticleList();
            pd.addProgress(11);
            pd.addText(Thread.currentThread().getName() + " Ended read article names. "
                    + (System.currentTimeMillis() - start) + "ms");
        });

        es.execute(() -> {
            final long start = System.currentTimeMillis();
            func = new ArticleGenerator();
            pd.addProgress(11);
            pd.addText(Thread.currentThread().getName() + " Ended initialize Functions class. "
                    + (System.currentTimeMillis() - start) + "ms");
        });

        es.execute(() -> {
            final long start = System.currentTimeMillis();
            initGraphTool();
            pd.addProgress(11);
            pd.addText(Thread.currentThread().getName() + " Ended initialize graph tool. "
                    + (System.currentTimeMillis() - start) + "ms");
        });

        es.execute(() -> {
            final long start = System.currentTimeMillis();
            Platform.runLater( () -> {
                readStyleSheets();
                setStylesheet();
                splitter.setDividerPosition(0, DEFAULT_DIVIDER_POSITION);
            });
            pd.addProgress(11);
            pd.addText(Thread.currentThread().getName() + " Ended initialize stylesheets. "
                    + (System.currentTimeMillis() - start) + "ms");
        });

        // insert WebView to tabPane.
        es.execute(() -> {
            final long start = System.currentTimeMillis();
            tabPane.getSelectionModel().selectedItemProperty().addListener(
                    (a, prevTab, nextTab) -> {
                        // (121224) タブ切り替え時の URL 表示の変更
                        final Optional<WebView> opt = getCurrentWebView();
                        if (!opt.isPresent()) {
                            return;
                        }
                        final WebEngine engine = opt.get().getEngine();
                        final String tabUrl = engine.getLocation();
                        if (!StringUtils.isEmpty(tabUrl)
                                && !tabUrl.startsWith("about")
                                && !tabUrl.endsWith(Defines.TEMP_FILE_NAME)
                                ){
                            urlText.setText(tabUrl);
                            return;
                        }

                        final String text = nextTab.getText();
                        if (StringUtils.isEmpty(text)) {
                            return;
                        }

                        // (130317) 「現在選択中のファイル名」にセット
                        final File selected = new File(
                                Config.get(Config.Key.ARTICLE_DIR),
                                ArticleGenerator.toBytedString_EUC_JP(nextTab.getText()) + Article.Extension.WIKI.text()
                                );
                        if (selected.exists()){
                            Config.article = new Article(selected);
                            urlText.setText(Config.article.toInternalUrl());
                            focusOn();
                        }
                    }
                    );
            wordCloud = new FxWordCloud.Builder().setNumOfWords(200).setMaxFontSize(120.0)
                            .setMinFontSize(8.0).build();
            Platform.runLater( () -> {
                openWebTab();
                callHome();
            });
            pd.addProgress(11);
            pd.addText(Thread.currentThread().getName() + " Ended initialize right tabs. "
                    + (System.currentTimeMillis() - start) + "ms");
        });

        es.execute(() -> {
            final long start = System.currentTimeMillis();
            searcherInput.textProperty().addListener((observable, oldValue, newValue) ->
            highlight(Optional.ofNullable(newValue), WINDOW_FIND_DOWN)
                    );
            final DoubleProperty valueProperty = zoom.valueProperty();
            valueProperty.addListener( (value, arg1, arg2) -> {
                getCurrentWebView().ifPresent(wv -> {wv.zoomProperty().bindBidirectional(valueProperty);});
                zoomInput.setText(Double.toString(valueProperty.get()));
            });
            pd.addProgress(11);
            pd.addText(Thread.currentThread().getName() + " Ended initialize tools. "
                    + (System.currentTimeMillis() - start) + "ms");
        });

        es.shutdown();

        searchKind.getSelectionModel().select(0);

        // move to top.
        header.setOnMousePressed((event) -> moveToTop());
        footer.setOnMousePressed((event) -> moveToBottom());
        pd.addProgress(11);

        reload.setGraphic(   new ImageView(FileUtil.getUrl(PATH_IMG_RELOAD).toString()));
        webSearch.setGraphic(new ImageView(FileUtil.getUrl(PATH_IMG_SEARCH).toString()));
        //initReloadButton();

        BACKUP.submit(FILE_WATCHER);
        pd.addProgress(11);
        pd.stop();
    }

    /**
     * setup searcher and scripter. this method call by FXWikiClient.
     */
    protected void setupExpandables() {
        hideSearcher();
        stage.getScene().setOnKeyPressed(e -> {
            // select tab.
            if (FIRST_TAB.match(e)) {
                selectTab(0);
            } else if (SECOND_TAB.match(e)) {
                selectTab(1);
            } else if (THIRD_TAB.match(e)) {
                selectTab(2);
            } else if (FOURTH_TAB.match(e)) {
                selectTab(3);
            } else if (FIFTH_TAB.match(e)) {
                selectTab(4);
            } else if (SIXTH_TAB.match(e)) {
                selectTab(5);
            } else if (SEVENTH_TAB.match(e)) {
                selectTab(6);
            } else if (EIGHTH_TAB.match(e)) {
                selectTab(7);
            } else if (NINTH_TAB.match(e)) {
                selectTab(8);
            }

            if (APPEAR_SEARCHER.match(e)) {
                if (searcherArea.visibleProperty().getValue()) {
                    hideSearcher();
                } else {
                    openSearcher();
                }
            /*} else if (APPEAR_SCRIPTER.match(e)) {
                if (scriptController.scripterArea.visibleProperty().getValue()) {
                    scriptController.hideScripter();
                } else {
                    scriptController.openScripter();
                }*/
            } else if (ZOOM_INCREMENT.match(e)) {
                zoom.increment();
            } else if (ZOOM_DECREMENT.match(e)) {
                zoom.decrement();
            } else if (SHOW_LEFT_PANE.match(e)) {
                showLeftPane();
            } else if (HIDE_LEFT_PANE.match(e)) {
                hideLeftPane();
            }
        });
        // 特殊な使い方をしているので、ここでこのメソッドを呼んでタブ内のサイズ調整をする.
        tabPane.setPrefHeight(height);
    }

    /**
     * select specified index tab.
     * @param index
     */
    private void selectTab(final int index) {

        if (index < 0 || tabPane.getTabs().size() < index ) {
            LOGGER.info(index + " " + tabPane.getTabs().size());
            return;
        }
        tabPane.getSelectionModel().select(index);
    }

    /**
     * search backward.
     */
    @FXML
    protected void searchUp() {
        highlight(Optional.ofNullable(searcherInput.getText()), WINDOW_FIND_UP);
    }

    /**
     * search forward.
     */
    @FXML
    protected void searchDown() {
        highlight(Optional.ofNullable(searcherInput.getText()), WINDOW_FIND_DOWN);
    }

    /**
     * キーワードをハイライトする.
     * @param word キーワード
     * @see <a href="http://aoe-tk.hatenablog.com/entry/2015/06/15/001217">
     * JavaFXのWebViewの検索を実現するのにもっと簡単な方法がありました</a>
     */
    private final void highlight(
            final Optional<String> word, final String script) {
        word.ifPresent(keyword ->
            getCurrentWebView().ifPresent(
                    wv -> wv.getEngine().executeScript(MessageFormat.format(script, keyword)))
        );
    }

    /**
     * hide article search box area.
     * @see <a href="http://stackoverflow.com/questions/19923443/
     *javafx-fill-empty-space-when-component-is-not-visible">
     * JavaFX Fill empty space when component is not visible?</a>
     */
    @FXML
    protected void hideSearcher() {
        searcherArea.visibleProperty().setValue(false);
        searcherArea.setManaged(false);
    }

    /**
     * hide article search box area.
     */
    private void openSearcher() {
        searcherArea.setManaged(true);
        searcherArea.visibleProperty().setValue(true);
        searcherInput.requestFocus();
    }

    /**
     * read stylesheets.
     */
    private void readStyleSheets() {
        style.getItems().addAll(Style.findFileNamesFromDir());
    }

    /**
     * set stylesheet name in combobox.
     */
    private void setStylesheet() {
        final String stylesheet = Config.get(Config.Key.STYLESHEET);
        style.getSelectionModel().select(StringUtils.isEmpty(stylesheet)
                ? 0 : style.getItems().indexOf(stylesheet));
    }

    /**
     * ズーム率を指定値に変更する.
     */
    @FXML
    private final void setZoom() {
        try {
            final double ratio = Double.parseDouble(zoomInput.getText());
            zoom.setValue(ratio);
        } catch (final Exception e) {
            LOGGER.error("Error", e);
        }
    }

    /**
     * ズーム率をデフォルト(=1)に戻す.
     */
    @FXML
    private final void callDefaultZoom() {
        zoom.setValue(1.0);;
    }

    /**
     * move to top of current page.
     * @see <a href="https://community.oracle.com/thread/2595743">
     * How to auto-scroll to the end in WebView?</a>
     */
    private final void moveToTop() {
        getCurrentWebView().ifPresent(wv ->
            wv.getEngine().executeScript(findScrollTop(wv.getEngine().getLocation()))
        );
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
     * move to bottom of current page.
     * @see <a href="https://community.oracle.com/thread/2595743">
     * How to auto-scroll to the end in WebView?</a>
     */
    private final void moveToBottom() {
        getCurrentWebView().ifPresent(wv ->
            wv.getEngine().executeScript(findScrollBottom(wv.getEngine().getLocation()))
        );
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
     * ギャラリーを開く.
     */
    @FXML
    public final void callGallery() {
        func.generateGallery();
        loadUrl(Defines.findInstallDir() + Defines.TEMP_FILE_NAME);
    }

    /**
     * カレンダーを呼び出し、選択された日付の日記を表示する.
     */
    @FXML
    public final void callCalendar() {
        calendar.show();
        final LocalDate value = calendar.getValue();
        if (value == null) {
            return;
        }
        loadDiary(value);
    }

    /**
     * load diary specified LocalDate.
     * @param date
     */
    private void loadDiary(final LocalDate date) {
        try {
            final String prefix = "日記" + date.toString();
            final Optional<Article> opt = articleList.getItems().stream()
                    .filter(item -> item.title.startsWith(prefix))
                    .findFirst();
            if (!opt.isPresent()) {
                new JFXSnackbar(mainArea).show(prefix + "'s diary is not exist.", 4000L);
                return;
            }
            opt.ifPresent(article -> loadUrl(article.toInternalUrl()));
        } catch (final Exception e) {
            LOGGER.error("no such element", e);
        }
    }

    /**
     * グラフツールを初期化する.
     */
    private final void initGraphTool() {
        @SuppressWarnings("unused")
        final ObservableList<String> items = month.<String>getItems();
        items.addAll(ChartPane.getMonthsList());
        // 初期値セット
        graphKind.getSelectionModel().select(0);
        month.getSelectionModel().select(items.size() - 1);
    }

    /**
     * 青空文庫変換を呼ぶ.
     */
    @FXML
    public final void callConvertAobun() {
        final String absolutePath = Config.article.file.getAbsolutePath();
        AobunUtils.docToTxt(absolutePath);
        AlertDialog.showMessage(getParent(), "変換完了",
                Strings.join("変換が完了しました。", System.lineSeparator(), absolutePath));
    }

    /**
     * ePub を生成するメソッドを呼び出す.
     */
    @FXML
    public final void callConvertEpub() {
        final RadioButton vertically   = new RadioButton("vertically");
        final RadioButton horizontally = new RadioButton("horizontally");

        final ToggleGroup radios = new ToggleGroup() {{
            getToggles().addAll(vertically, horizontally);
            vertically.setSelected(true);
        }};

        new AlertDialog.Builder(getParent())
            .setTitle("ePub").setMessage("OK を押すと ePub を生成します。")
            .addControl(vertically, horizontally)
            .setOnPositive("OK", () -> {
                final ProgressDialog pd = new ProgressDialog.Builder()
                        .setScene(this.getParent().getScene())
                        .setText("ePub 生成中……").build();
                pd.start(stage);
                new EpubGenerator().toEpub(vertically.isSelected());
                pd.stop();
            }).build().show();
    }

    /**
     * Jsonの設定値を基に ePub を生成するメソッドを呼び出す.
     */
    @FXML
    public final void callGenerateEpubs() {
        new AlertDialog.Builder(getParent())
        .setTitle("ePub").setMessage("OK を押すと ePub を生成します。")
        .setOnPositive("OK", () -> {
            final ProgressDialog pd = new ProgressDialog.Builder()
                    .setScene(this.getParent().getScene())
                    .setText("ePub 生成中……").build();
            pd.start(stage);
            new EpubGenerator().runEpubGenerator();
            pd.stop();
        }).build().show();
    }

    /**
     * call simple backup.
     */
    @FXML
    public final void callSimpleBachup() {
        final DatePicker datePicker = new JFXDatePicker();
        datePicker.show();
        datePicker.setShowWeekNumbers(true);
        new AlertDialog.Builder(getParent()).addControl(datePicker)
            .setTitle("日付選択")
            .setMessage("バックアップする最初の日を選択してください。")
            .setOnPositive("Backup", () -> {
                final LocalDate value = datePicker.getValue();
                if (value == null) {
                    return;
                }
                final long epochDay = CalendarUtil.zoneDateTime2long(
                        value.atStartOfDay().atZone(ZoneId.systemDefault()));
                new Archiver().simpleBackup(Config.get(Config.Key.ARTICLE_DIR), epochDay);
            }).build().show();
    }

    /**
     * HTML ソースを表示する.
     */
    @FXML
    public final void callHtmlSource() {
        final Mono<String> source = Mono.<String>create(emitter -> {
            getCurrentWebView().ifPresent(wv -> {
                emitter.success(wv.getEngine()
                        .executeScript(
                                "document.getElementsByTagName('html')[0].innerHTML;"
                                )
                        .toString()
                        .replace("<", "&lt;")
                        .replace(">", "&gt;")
                        );
            });
        });

        final Mono<WebView> browser = Mono.<WebView>create(emitter -> {
            final String title = tabPane.getSelectionModel().getSelectedItem().getText();
            openWebTab(title.concat("'s HTML Source"));
            getCurrentWebView().ifPresent(wv -> emitter.success(wv));
        });

        source.and(browser).subscribe(tuple ->
            tuple.getT2().getEngine().loadContent(tuple.getT1().replace("\n", "<br/>"))
        );
    }

    /**
     * アプリケーションの状態を取得し、HTMLで表示する.
     */
    @FXML
    private final void callApplicationState() {
        final Map<String, String> map = ApplicationState.getConfigMap();
        final StringBuilder bld = new StringBuilder();
        final String lineSeparator = System.lineSeparator();
        map.forEach((key, value) ->
            bld.append(key).append("\t").append(value).append(lineSeparator)
        );
        AlertDialog.showMessage(getParent(), "状態", bld.toString());
    }

    /**
     * call LogViewer.
     */
    @FXML
    private final void callLogViewer() {
        if (!new File(PATH_APP_LOG).exists()) {
            LOGGER.warn(new File(PATH_APP_LOG).getAbsolutePath() + " is not exists.");
            return;
        }
        final String log = String.format(
                "<pre>%s</pre>",
                FileUtil.getStrFromFile(PATH_APP_LOG, StandardCharsets.UTF_8.name())
                );
        func.generateHtml(log, "LogViewer");
        openWebTab();
        loadDefaultFile();
    }

    /**
     * call About.
     */
    @FXML
    private final void callAbout() {
        if (!new File(PATH_ABOUT_APP).exists()) {
            LOGGER.warn(new File(PATH_ABOUT_APP).getAbsolutePath() + " is not exists.");
            return;
        }

        func.generateHtml(
                new ArticleGenerator().md2Html(PATH_ABOUT_APP),
                "About"
                );
        openWebTab();
        loadDefaultFile();
    }

    /**
     * バックアップ機能を呼び出す。
     */
    @FXML
    private final void callBackUp(final ActionEvent event) {
        final Window parent = getParent();
        new AlertDialog.Builder(parent)
            .setTitle("バックアップ")
            .setMessage("この処理には時間がかかります。")
            .setOnPositive("OK", () -> {
                final ProgressDialog pd = new ProgressDialog.Builder()
                        .setScene(this.getParent().getScene())
                        .setText("バックアップ中……").build();
                final long start = System.currentTimeMillis();
                String sArchivePath = Config.get(Config.Key.ARTICLE_DIR);
                try {
                    new ZipArchiver().doDirectory(sArchivePath);
                    //new ZipArchiver().doDirectory(iArchivePath);
                } catch (final IOException e) {
                    LOGGER.error("Error", e);;
                }
                final long end = System.currentTimeMillis() - start;
                pd.stop();
                sArchivePath = sArchivePath.substring(0, sArchivePath.length() - 1)
                        .concat(ZipArchiver.EXTENSION_ZIP);
                final String message = String.format("バックアップを完了しました。：%s%s%d[ms]",
                        sArchivePath, System.lineSeparator(), end);
                AlertDialog.showMessage(parent, "バックアップ完了", message);
            }).build().show();
    }

    /**
     * get parent window.
     * @return parent window.
     */
    private Window getParent() {
        return stage.getScene().getWindow();
    }

    /**
     * ホーム記事を登録する.
     */
    @FXML
    private final void setHome() {
        final String currentURL = urlText.getText();
        final Window parent = getParent();
        if (currentURL.startsWith("/")
                || currentURL.startsWith("http://")
                || currentURL.startsWith("https://")
                ){
            final String homeTitle = tabPane.getSelectionModel().getSelectedItem().getText();
            new AlertDialog.Builder(parent).setTitle("ホーム設定")
                .setMessage(homeTitle + "をホームに設定しますか？")
                .setOnPositive("YES", () -> {
                    Config.store(Config.Key.HOME, currentURL);
                    Config.reload();
                }).build().show();
            return ;
        }
        AlertDialog.showMessage(parent, "設定不可", "現在のページはホームに設定できません。");
    }

    /**
     * 現在のタブをリロードする.
     * Wiki 記事の場合は再読み込み(というより一時HTMLの再生成)を実施する.
     */
    @FXML
    private final void reload() {
        final Node node = getCurrentTab().getContent();
        if (node == null) {
            return;
        }

        if (node instanceof WebView) {
            final WebEngine engine = ((WebView) node).getEngine();
            final String url = engine.getLocation();
            if (!url.contains(Defines.TEMP_FILE_NAME)
                    || StringUtils.isEmpty(Config.article.file.getName())) {
                engine.reload();
                return;
            }
            loadUrl(Config.article.toInternalUrl(), true);
            return;
        }
        // webView でなければそれぞれ reload.
        LOGGER.info(node.getClass().equals(ChartPane.class) + " " + node.getClass().getName() + " " + ToStringBuilder.reflectionToString(node));
        if (node instanceof ChartPane) {
            drawChart(false);
        }
    }

    /**
     * Web 検索機能.
     * @param event ActionEvent
     */
    @FXML
    private final void webSearch(final ActionEvent event) {
        final String kind  = searchKind.getItems()
                .get(searchKind.getSelectionModel().getSelectedIndex())
                .toString();
        final String query = webQuery.getText();
        if (StringUtils.isEmpty(query)){
            return ;
        }
        final String url = WebServiceHelper.buildRequestUrl(query, kind);
        if (StringUtils.isEmpty(url)){
            return;
        }
        openWebTab();
        loadUrl(url);
    }

    /**
     * 設定ダイアログを呼び出す.
     * @param event ActionEvent
     */
    @FXML
    private final void callConfig(final ActionEvent event) {
        final String current = Config.get(Config.Key.VIEW_TEMPLATE);
        new ConfigDialog(getParent()).showConfigDialog();
        Config.reload();
        musicController.reload();
        if (!current.equals(Config.get(Config.Key.VIEW_TEMPLATE))) {
            reload();
        }
    }

    /**
     * open CSS Generator.
     */
    @FXML
    private final void openCssGenerator() {
        try {
            new jp.toastkid.gui.jfx.cssgen.Main().start(this.stage);
        } catch (final Exception e) {
            LOGGER.error("Error", e);;
        }
    }

    /**
     * open Noodle Timer.
     */
    @FXML
    private final void openNoodleTimer() {
        try {
            new jp.toastkid.gui.jfx.noodle_timer.Main().start(this.stage);
        } catch (final Exception e) {
            LOGGER.error("Error", e);;
        }
    }

    /**
     * グラフを描画する.
     */
    @FXML
    private final void drawChart() {
        drawChart(true);
    }

    /**
     * グラフを描画する.
     */
    @FXML
    private final void drawChart(final boolean openNew) {
        final String graphTitle = graphKind.getSelectionModel().getSelectedItem().toString();
        final Pane content = ChartPane.make(graphTitle,
                "日記" + month.getSelectionModel().getSelectedItem().toString());

        if (openNew) {
            final Tab tab = makeClosableTab(graphTitle);
            tab.setContent(content);
            openTab(tab);
            return;
        }

        getCurrentTab().setContent(content);
    }

    /**
     * 渡されたタブを新規に開いて、そのタブを current にする.
     * @param tab Tab.
     */
    private void openTab(final Tab tab) {
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    /**
     * 新しい WebView のタブを開く.
     * @param e
     */
    @FXML
    private void openWebTab() {
        openWebTab("");
    }

    /**
     * 新しい WebView のタブを開く.
     * @param title タブのタイトル
     */
    private void openWebTab(final String title) {

        // tab 切り替え→ URL 開くと変な位置にスクロールされるのを防止
        if (Config.article != null) {
            Config.article.yOffset = 0;
        }

        final Tab tab = makeClosableTab("new tab");
        final WebView wv = new WebView();
        wv.setOnContextMenuRequested(event -> showContextMenu());
        tab.setContent(wv);
        final WebEngine engine = wv.getEngine();
        // 新規タブで開く場合
        engine.setCreatePopupHandler(
                popupFeature -> {
                    openWebTab();
                    tabPane.getSelectionModel().selectLast();
                    return getCurrentWebView().get().getEngine();
                }
                );
        //engine.setJavaScriptEnabled(true);
        engine.setOnAlert(e -> LOGGER.info(e.getData()));
        final Worker<Void> loadWorker = engine.getLoadWorker();
        loadWorker.stateProperty().addListener(
                (arg0, prev, next) -> {
                    final String url = engine.getLocation();

                    if (State.READY.equals(prev)) {
                        tab.setText("loading...");
                    }

                    if (Article.isWikiArticleUrl(url)) {
                        if (State.SCHEDULED.equals(arg0.getValue())) {
                            loadWorker.cancel();
                            Platform.runLater(() -> loadUrl(url));
                        }
                    }

                    if (next == Worker.State.SUCCEEDED) {
                        tab.setText(StringUtils.isNotBlank(engine.getTitle())
                                ? engine.getTitle() : title);
                        final int j = Config.article.yOffset;
                        if (j == 0) {
                            return;
                        }
                        engine.executeScript(String.format("window.scrollTo(0, %d);", j));
                    }
                }
                );
        openTab(tab);
    }

    /**
     * make closable tab.
     * @param title tab's title
     * @return
     */
    private Tab makeClosableTab(final String title) {
        final Tab tab = new Tab(title);
        final Button closeButton = new JFXButton("x");
        closeButton.setOnAction(e -> closeTab(tab));
        tab.setGraphic(closeButton);
        return tab;
    }

    /**
     * show Context(Popup) menu.
     * @see <a href="http://stackoverflow.com/questions/27047447/
     *customized-context-menu-on-javafx-webview-webengine">
     *customized-context-menu-on-javafx-webview-webengine</a>
     */
    private PopupWindow showContextMenu() {
        @SuppressWarnings("deprecation")
        final Iterator<Window> windows = Window.impl_getWindows();

        while (windows.hasNext()) {
            final Window window = windows.next();

            if (!(window instanceof ContextMenu
                    && window.getScene() != null && window.getScene().getRoot() != null)) {
                return null;
            }

            final Parent root = window.getScene().getRoot();

            // access to context menu content
            if(root.getChildrenUnmodifiable().size() <= 0) {
                return null;
            }
            final Node popup = root.getChildrenUnmodifiable().get(0);
            if(popup.lookup(".context-menu") == null) {
                return null;
            }
            final Node bridge = popup.lookup(".context-menu");
            final ContextMenuContent cmc
            = (ContextMenuContent)((Parent)bridge).getChildrenUnmodifiable().get(0);

            final VBox itemsContainer = cmc.getItemsContainer();
            for (final Node n: itemsContainer.getChildren()) {
                final MenuItemContainer item = (MenuItemContainer) n;
                item.getItem().setText(item.getItem().getText());
            }

            // adding new item:
            final MenuItem length = new MenuItem("文字数計測"){{
                setOnAction(event -> callFileLength());
            }};
            final MenuItem fullScreen = new MenuItem("Full Screen"){{
                setOnAction(event -> callTabFullScreen());
            }};
            final MenuItem slideShow = new MenuItem("スライドショー"){{
                setOnAction(event -> slideShow());
            }};
            final MenuItem openTab = new MenuItem("新しいタブを開く"){{
                setOnAction(event -> openWebTab());
            }};
            final MenuItem source = new MenuItem("ソースを表示"){{
                setOnAction(event -> callHtmlSource());
            }};
            final MenuItem search = new MenuItem("ページ内検索"){{
                setOnAction(event -> openSearcher());
            }};
            final MenuItem moveToTop = new MenuItem("ページの先頭に移動"){{
                setOnAction(event -> moveToTop());
            }};
            final MenuItem moveToBottom = new MenuItem("ページの最後に移動"){{
                setOnAction(event -> moveToBottom());
            }};
            final MenuItem searchAll = new MenuItem("全記事検索"){{
                setOnAction(event -> callSearch());
            }};
            final MenuItem showLeft = new MenuItem("記事一覧を開く"){{
                setOnAction(event -> showLeftPane());
            }};
            final MenuItem hideLeft = new MenuItem("記事一覧を閉じる"){{
                setOnAction(event -> hideLeftPane());
            }};
            final MenuItem wordCloud = new MenuItem("Word cloud"){{
                setOnAction(event -> callWordCloud());
            }};

            // add new item:
            cmc.getItemsContainer().getChildren().addAll(
                    cmc.new MenuItemContainer(length),
                    cmc.new MenuItemContainer(fullScreen),
                    cmc.new MenuItemContainer(slideShow),
                    cmc.new MenuItemContainer(openTab),
                    cmc.new MenuItemContainer(source),
                    cmc.new MenuItemContainer(search),
                    cmc.new MenuItemContainer(moveToTop),
                    cmc.new MenuItemContainer(moveToBottom),
                    cmc.new MenuItemContainer(searchAll),
                    cmc.new MenuItemContainer(isHideLeftPane() ? showLeft : hideLeft),
                    cmc.new MenuItemContainer(wordCloud)
                    );

            return (PopupWindow)window;
        }
        return null;
    }

    /**
     * 左側を隠すか表示する.
     */
    private void showLeftPane() {
        splitter.setDividerPosition(0, DEFAULT_DIVIDER_POSITION);
    }

    /**
     * 左側を隠す.
     */
    private void hideLeftPane() {
        splitter.setDividerPosition(0, 0.0d);
    }

    /**
     * 左側が隠れている場合は true.
     * @return 左側が隠れている場合は true.
     */
    private boolean isHideLeftPane() {
        return splitter.getDividerPositions()[0] < 0.01d;
    }

    /**
     * 現在選択しているタブを閉じる.1つしか開いていない時は閉じない。
     * @param e
     */
    @FXML
    private final void closeTab(final ActionEvent event) {
        closeTab(tabPane.getSelectionModel().getSelectedItem());
    }

    /**
     * 引数で渡されたタブを閉じる.
     * @param tab
     */
    private final void closeTab(final Tab tab) {
        final ObservableList<Tab> tabs = tabPane.getTabs();
        if (1 < tabs.size()) {
            tabs.remove(tab);
        }
    }

    /**
     * 1つ前のページに戻る.
     */
    @FXML
    private final void back() {
        final Optional<WebView> opt = getCurrentWebView();
        opt.ifPresent(wv -> {
            final WebHistory history = wv.getEngine().getHistory();
            final int index = history.getCurrentIndex();
            if (0 < index) {
                history.go(index - 1);
            }
        });
    }

    /**
     * 1つ先のページに進む.
     */
    @FXML
    private final void forward() {
        final Optional<WebView> opt = getCurrentWebView();
        opt.ifPresent(wv -> {
            final WebHistory history = wv.getEngine().getHistory();
            final int index = history.getCurrentIndex();
            if (index < history.getMaxSize()) {
                history.go(index + 1);
            }
        });
    }

    /**
     * 読み込みを中止する.
     * TODO 動作未検証
     * @param event
     */
    @FXML
    private final void stop() {
        Platform.runLater( () ->
            getCurrentWebView().ifPresent(wv -> wv.getEngine().getLoadWorker().cancel()));
    }

    /**
     * ファイルの文字数を計測して表示する.
     * @param event
     */
    @FXML
    private final void callFileLength() {
        AlertDialog.showMessage(
                getParent(),
                "文字数計測",
                Config.article.makeCharCountResult()
                );
    }

    /**
     * 全記事検索を呼び出す.
     */
    @FXML
    private final void callSearch() {
        searchArticle("", "");
    }

    /**
     * 再帰的に呼び出すためメソッドに切り出し.
     * @param q クエリ
     * @param f 記事名フィルタ文字列
     */
    private void searchArticle(final String q, final String f) {
        final CheckBox isTitleOnly = new JFXCheckBox("記事名で検索");
        final CheckBox isAnd       = new JFXCheckBox("AND 検索"){{setSelected(true);}};
        new AlertDialog.Builder(getParent())
            .setTitle("全記事検索").setMessage("この操作の実行には時間がかかります。")
            //"記事名のみを対象に検索"
            .addControl(queryInput, new Label("記事名でフィルタ"), filterInput, isTitleOnly, isAnd)
            .setOnPositive("OK", () -> {
                final String query = queryInput.getText().trim();
                if (StringUtils.isEmpty(query)) {
                    return;
                }
                ((AutoCompleteTextField) queryInput).getEntries().add(query);

                // 入っていない時もあるので.
                final String filter = filterInput.getText();
                if (StringUtils.isNotBlank(filter)) {
                    ((AutoCompleteTextField) filterInput).getEntries().add(filter);
                }

                final long start = System.currentTimeMillis();

                final FileSearcher fileSearcher = new FileSearcher.Builder()
                        .setHomeDirPath(Config.get("articleDir"))
                        .setAnd(isAnd.isSelected())
                        .setTitleOnly(isTitleOnly.isSelected())
                        .setSelectName(filter)
                        .build();

                final Map<String, SearchResult> map = fileSearcher.search(query);
                if (map.isEmpty()){
                    AlertDialog.showMessage(
                            getParent(), "見つかりませんでした。", "見つかりませんでした。");
                    searchArticle(queryInput.getText(), filterInput.getText());
                    return;
                }

                final Tab tab = makeClosableTab(
                        String.format("「%s」の%s検索結果", query, isAnd.isSelected() ? "AND" : "OR"),
                        leftTabs
                        );
                // prepare tab's content.
                final VBox box = new VBox();
                leftTabs.getTabs().add(tab);
                leftTabs.getSelectionModel().select(tab);
                final ObservableList<Node> children = box.getChildren();
                children.add(new Label(
                        String.format("実行時間: %d[ms]", fileSearcher.getLastSearchTime())));
                children.add(new Label(String.format("%dファイル / %dファイル中",
                        map.size(), fileSearcher.getLastFilenum())));
                // set up ListView.
                final ListView<Article> listView = new ListView<>();
                initArticleList(listView);
                listView.getItems().addAll(
                        map.entrySet().stream()
                            .map(entry -> new Article(new File(entry.getValue().filePath)))
                            .sorted()
                            .collect(Collectors.toList())
                        );
                listView.setMinHeight(articleList.getHeight());

                children.add(listView);
                tab.setContent(box);
                setStatus("検索完了：" + (System.currentTimeMillis() - start) + "[ms]");
            }).build().show();
    }

    /**
     * make empty closable tab.
     * @param title Tab's title
     * @param parent Parent TabPane
     * @return 空の Tab
     */
    public static Tab makeClosableTab(final String title, final TabPane parent) {
        final Tab tab = new Tab(title);
        tab.setClosable(true);

        final Button closeButton = new Button("x");
        closeButton.setOnAction(e -> parent.getTabs().remove(tab));
        tab.setGraphic(closeButton);
        return tab;
    }

    /**
     * ワードクラウド表示機能を呼び出す.
     */
    @FXML
    private final void callWordCloud() {
        final Tab tab = makeClosableTab(Config.article.title + "のワードクラウド");
        final JFXMasonryPane2 pane = new JFXMasonryPane2();
        final ScrollPane value = new ScrollPane(pane);
        value.setFitToHeight(true);
        value.setFitToWidth(true);
        tab.setContent(value);
        wordCloud.draw(pane, Config.article.file);
        Platform.runLater(()-> value.requestLayout());
        openTab(tab);
    }

    /**
     * Markdown に変換する.
     */
    @FXML
    private void callConvertMd() {
        final Tab tab = makeClosableTab("(MD)" + Config.article.title);
        final TextArea pane = new JFXTextArea();
        final double prefWidth = tabPane.getWidth();
        pane.setPrefWidth(prefWidth);
        pane.setPrefHeight(tabPane.getPrefHeight());
        try {
            pane.setText(CollectionUtil.implode(
                    Wiki2Markdown.convert(Files.readAllLines(Config.article.file.toPath()))
                    ));
        } catch (final IOException e) {
            LOGGER.error("Error", e);;
            AlertDialog.showMessage(getParent(), "IOException", e.getMessage());
            return;
        }
        tab.setContent(new ScrollPane(pane));
        openTab(tab);
    }

    /**
     * call RSS Feeder．
     */
    @FXML
    private final void callRssFeeder() {
        final long start = System.currentTimeMillis();
        openWebTab("RSS Feeder");
        String content = RssFeeder.run();
        if (StringUtils.isEmpty(content)) {
            content = "コンテンツを取得できませんでした.";
        }
        func.generateHtml(content, "RSS Feeder");
        loadDefaultFile();
        setStatus("取得完了：" + (System.currentTimeMillis() - start) + "[ms]");
    }

    /**
     * Load temporary HTML.
     */
    private void loadDefaultFile() {
        loadUrl(Defines.findInstallDir() + Defines.TEMP_FILE_NAME);
    }

    /**
     * Call system calculator.
     */
    @FXML
    private final void callCalc() {
        RuntimeUtil.callCalculator();
    }

    /**
     * Call command prompt.
     */
    @FXML
    private final void callCmd() {
        RuntimeUtil.callCmd();
    }

    /**
     * switch Full screen mode.
     */
    @FXML
    private final void fullScreen() {
        stage.setFullScreen(!stage.fullScreenProperty().get());
    }

    /**
     * Show current tab to full screen.
     */
    @FXML
    private void callTabFullScreen() {
        if (fs == null) {
            fs = new FullScreen(this.width, this.height);
        }

        if (!StringUtils.equals(fs.getTitle(), Config.article.title)) {
            fs.setTitle(Config.article.title);
            fs.show(Defines.findInstallDir() + Defines.TEMP_FILE_NAME);
            return;
        }
        fs.show();
    }

    /**
     * show slide show.
     */
    @FXML
    private void slideShow() {
        func.generateSlide(
                Config.article.title,
                func.convertArticle2Slide(),
                Config.get(Config.Key.SLIDE_THEME, "white")
                );
        callTabFullScreen();
    }

    /**
     * フォルダを開く.
     * @param event 開くフォルダを決めるのに使う.
     */
    @FXML
    private final void openFolder(final ActionEvent event) {
        final MenuItem source = (MenuItem) event.getSource();
        final String text = source.getText();
        String[] dirs;
        switch (text) {
            case "現在のフォルダ":
                dirs = new String[]{ Defines.findInstallDir() };
                break;
            case "記事":
                dirs = new String[]{ Config.get(Config.Key.ARTICLE_DIR) };
                break;
            case "画像":
                dirs = new String[]{ Config.get(Config.Key.IMAGE_DIR) };
                break;
            case "音楽":
                dirs = Config.get(Config.Key.MUSIC_DIR).split(",");
                break;
            default:
                dirs = new String[]{ Defines.findInstallDir() };
                break;
        }
        for (final String dir : dirs) {
            if (StringUtils.isBlank(dir)) {
                continue;
            }
            final String openPath = dir.startsWith(FileUtil.FILE_PROTOCOL)
                    ? dir
                    : FileUtil.FILE_PROTOCOL + dir;
            RuntimeUtil.callExplorer(openPath);
        }
    }

    /**
     * ホーム画面を呼び出す.
     */
    @FXML
    private final void callHome() {
        loadUrl(Config.get(Config.Key.HOME));
    }

    /**
     * 記事一覧リストの準備.
     */
    private void prepareArticleList() {
        initArticleList(articleList);
        initArticleList(historyList);
        loadArticleList();
    }

    /**
     * ListView 共通の処理を返す.
     * @param listView ListView
     */
    private void initArticleList(final ListView<Article> listView) {
        listView.setCellFactory((lv) -> new ArticleListCell());
        final MultipleSelectionModel<Article> selectionModel = listView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        selectionModel.selectedItemProperty().addListener((property, oldVal, newVal) -> {
            if (property.getValue() == null) {
                return;
            }
            loadUrl(property.getValue().toInternalUrl());
        });
    }

    /**
     * ファイル一覧をロードする.
     */
    @FXML
    private void loadArticleList() {
        final ObservableList<Article> items = articleList.getItems();
        items.removeAll();
        final List<Article> readArticleNames = Article.readArticleNames(Config.get("articleDir"));
        items.addAll(readArticleNames);
        articleList.requestLayout();
        focusOn();
    }

    /**
     * 現在選択中のファイルに ListView をフォーカスする.
     * @param items ObservableList
     */
    private void focusOn() {

        Platform.runLater(() -> {
            final int indexOf = articleList.getItems().indexOf(Config.article);
            if (indexOf != -1){
                articleList.getSelectionModel().select(indexOf);
                articleList.scrollTo(indexOf - FOCUS_MARGIN);
            }
            final int indexOfHistory = historyList.getItems().indexOf(Config.article);
            if (indexOfHistory != -1){
                historyList.getSelectionModel().select(indexOfHistory);
                historyList.scrollTo(indexOfHistory - FOCUS_MARGIN);
            }
        });
    }

    /**
     * ページをロードする.
     * @param url ロードする URL
     * @return yOffset.
     */
    private void loadUrl(final String url) {
        loadUrl(url, false);
    }

    /**
     * ページをロードする.
     * @param url ロードする URL
     * @param isReload リロードの場合、yOffsetを保持.
     * @return yOffset.
     */
    private void loadUrl(final String url, final boolean isReload) {
        final Optional<WebView> currentWebView = getCurrentWebView();
        if (!currentWebView.isPresent()) {
            if (StringUtils.isNotBlank(url)) {
                openWebTab();
                loadUrl(url, isReload);
            }
            return;
        }

        // Web ページならそのまま表示.
        if (!StringUtils.isEmpty(url) && url.startsWith("http")) {
            urlText.setText(url);
            currentWebView.ifPresent(wv -> wv.getEngine().load(url));
            return;
        }

        // HTMLならそのまま表示.
        if (!StringUtils.isEmpty(url) && url.endsWith(".html")) {
            urlText.setText(url);
            currentWebView.ifPresent(wv -> wv.getEngine().load(url));
            return;
        }

        // (121229) ファイルパスを取得するための処理
        String fileName  = Article.findFileNameFromUrl(url);
        final int lastIndexOf = fileName.lastIndexOf("#");
        final String innerLink = lastIndexOf != -1
                ? HtmlUtil.tagEscape(fileName.substring(lastIndexOf)) : "";
        // (140112) 内部リンク追加
        if (lastIndexOf != -1) {
            fileName  = fileName.substring(0, lastIndexOf);
        }
        final File file = new File(Config.get(Config.Key.ARTICLE_DIR), fileName);
        if (!fileName.endsWith(".html")
                && !FileUtil.isImageFile(fileName)
                //&& (url.startsWith("file://") || url.startsWith(Defines.ARTICLE_URL_PREFIX) )
                ){
            if (Config.article == null) {
                Config.article = new Article(file);
            } else {
                Config.article.replace(file);
            }

            if (!file.exists()){
                // 存在しないファイルの場合はエディタを呼び出す。
                callEditor();
            }

            if (func == null) {
                return;
            }
            // 読み込んだ内容を HTML 変換し、一時ファイルに書き出し、さらにそれを読み込んで表示
            func.generateArticleFile();
            urlText.setText(Config.article.toInternalUrl());
            // タブが入れ替わった可能性があるので、もう1回取得.
            final WebEngine engine = getCurrentWebView().get().getEngine();
            Platform.runLater(() -> {
                final Object script = engine.executeScript("window.pageYOffset;");
                final int yOffset
                    = script != null ? MathUtil.parseOrZero(script.toString()) : 0;
                engine.load(Defines.findInstallDir() + Defines.TEMP_FILE_NAME + innerLink);
                Config.article.yOffset = isReload ? yOffset : 0;
            });
            // deep copy を渡す.
            addHistory(Config.article.clone());
            focusOn();
        }
    }

    /**
     * 記事名をリストの先頭に追加する。
     * @param article Article Object
     */
    private final void addHistory(final Article article) {
        final ObservableList<Article> items = historyList.getItems();
        if (!items.contains(article)) {
            items.add(0, article);
        }
        FILE_WATCHER.add(article.file);
        historyList.requestLayout();
    }

    /**
     * バックアップファイルをすべて削除する.
     */
    @FXML
    private final void clearBackup() {
        new AlertDialog.Builder(stage)
            .setTitle("Clear History").setMessage("バックアップを削除します。")
            .setOnPositive("OK", () -> {
                try {
                    Files.list(Paths.get("backup/")).parallel()
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (final Exception e) {
                            LOGGER.error("Error", e);;
                        }
                    });
                } catch (final Exception e) {
                    LOGGER.error("Error", e);;
                }
            })
            .build().show();
    }

    /**
     * 閲覧履歴をクリアする.
     */
    @FXML
    private final void clearHistory() {
        new AlertDialog.Builder(stage)
            .setTitle("Clear History").setMessage("閲覧履歴を削除します。")
            .setOnPositive("OK", () -> {
                historyList.getItems().clear();
                FILE_WATCHER.clear();
            })
            .build().show();
    }

    /**
     * 新規に記事を作成する.
     * <HR>
     * (130304) 作成<BR>
     */
    @FXML
    private final void makeArticle() {
        makeContent(Article.Extension.WIKI);
    }

    /**
     * make new Markdown.
     * .
     */
    @FXML
    private final void makeMarkdown() {
        makeContent(Article.Extension.MD);
    }

    /**
     * make content file.
     * @param ext.
     */
    private final void makeContent(final Extension ext) {
        final TextField input = new TextField();
        final String newArticleMessage = "新しい記事の名前を入力して下さい。";
        input.setPromptText(newArticleMessage);
        new AlertDialog.Builder(getParent())
                .setTitle("新記事作成")
                .setMessage(newArticleMessage)
                .addControl(input)
                .build().show();
        final String newFileName = input.getText();
        if (!StringUtils.isEmpty(newFileName)){
            Config.article = Article.find(ArticleGenerator.toBytedString_EUC_JP(newFileName) + ext.text());
            callEditor();
        }
    }

    /**
     * 記事のコピー処理を呼び出す。
     * <HR>
     * (130707) メッセージ変更<BR>
     * (130512) 作成<BR>
     */
    @FXML
    private final void callCopy(final ActionEvent event) {
        callRenameArticle(true);
    }

    /**
     * 記事のリネーム処理を呼び出す。
     * <HR>
     * (130707) メッセージ変更<BR>
     * (130512) 作成<BR>
     */
    @FXML
    private final void callRename(final ActionEvent event) {
        callRenameArticle(false);
    }

    /**
     * 記事のリネーム処理を呼び出す。
     * <HR>
     * @param isCopy コピーをするか
     * (130707) メッセージ変更<BR>
     * (130512) 作成<BR>
     */
    private final void callRenameArticle(final boolean isCopy) {
        String prefix = "";
        if (isCopy) {
            prefix = "Copy_";
        }
        final Window parent = getParent();
        final String currentTitle = Config.article.title;

        final TextField input = new TextField(prefix.concat(currentTitle));

        final String renameMessage = "新しいファイル名を入力して下さい。";
        input.setPromptText(renameMessage);
        new AlertDialog.Builder(parent)
            .setTitle("記事名変更").setMessage(renameMessage)
            .addControl(input)
            .setOnPositive("OK", () ->{
                final String newTitle = input.getText();
                if (StringUtils.isBlank(newTitle)) {
                    return;
                }
                final File dest = new File(
                        Config.get(Config.Key.ARTICLE_DIR),
                        ArticleGenerator.toBytedString_EUC_JP(newTitle) + Config.article.extention()
                        );
                if (dest.exists()){
                    AlertDialog.showMessage(parent, "変更失敗", "そのファイル名はすでに存在します。");
                }
                final File file = Config.article.file;
                boolean success = false;
                try {
                    success = isCopy
                            ? FileUtil.copyTransfer(file.getAbsolutePath(), dest.getAbsolutePath())
                                    : file.renameTo(dest);
                } catch (final IOException e) {
                    LOGGER.error("Error", e);;
                }
                final String title;
                final String message;
                if (success){
                    if (isCopy) {
                        title   = "コピー";
                        message = "新規ファイル「" + newTitle  + "」を作成しました。";
                    } else {
                        title   = "変更";
                        message = "ファイル名を「" + newTitle  + "」に変更しました。";
                    }
                    AlertDialog.showMessage(parent, title, message);
                    Config.article.replace(dest);
                    loadUrl(Config.article.toInternalUrl());
                    removeHistory(new Article(file));
                    loadArticleList();
                    return;
                }
                if (isCopy) {
                    title   = "コピー失敗";
                    message = "ファイルのコピーに失敗しました。";
                } else {
                    title   = "変更失敗";
                    message = "ファイル名の変更に失敗しました。";
                }
                AlertDialog.showMessage(parent, title, message);
            }).build().show();;
    }

    /**
     * 現在選択している記事を削除する。
     * <HR>
     * (130317) ファイルが存在しない場合はダイアログを出して戻る<BR>
     * (130309) 1つファイルを消すたびにファイル一覧を読み直すのは非効率的なので、
     * ファイル一覧から削除するよう処理変更<BR>
     * (130305) 作成<BR>
     */
    @FXML
    public final void callDelete(final ActionEvent event) {
        // 削除対象のファイルオブジェクト
        final Article article = Config.article;
        final String deleteTarget = article.title;
        final Window parent = getParent();
        // (130317) ファイルが存在しない場合はダイアログを出して戻る。
        if (!article.file.exists()){
            AlertDialog.showMessage(parent,
                    "ファイルがありません", deleteTarget + " というファイルは存在しません。");
            return;
        }
        new AlertDialog.Builder(parent)
            .setTitle("ファイルの削除")
            .setMessage(deleteTarget + " を削除しますか？")
            .setOnPositive("OK", () -> {
                article.file.delete();
                AlertDialog.showMessage(parent, "削除完了", deleteTarget + " を削除しました。");
                // (130317)
                final String homePath = Config.get(Config.Key.HOME);
                // 削除後はホーム画面に戻す
                loadUrl(homePath);
                // (130309) そしてファイル一覧から削除
                articleList.getItems().remove(deleteTarget);
                removeHistory(article);
            }).build().show();
    }

    /**
     * 履歴リストから指定した記事名を削除する.
     * @param deleteTarget 削除する記事名
     */
    private final void removeHistory(final Article deleteTarget) {
        final ObservableList<Article> items = historyList.getItems();
        if (items.indexOf(deleteTarget) != -1) {
            items.remove(deleteTarget);
        }
    }

    /**
     * エディタを呼び出す.
     */
    @FXML
    public final void callEditor() {
        final File openTarget = Config.article.file;
        if (openTarget.exists()){
            openFileByEditor(openTarget);
            return;
        }

        // (130302) ファイルが存在しない場合は、ひな形を元に新規作成する。
        try {
            openTarget.createNewFile();
            Files.write(openTarget.toPath(), ArticleGenerator.makeNewContent());
        } catch (final IOException e) {
            LOGGER.error("Error", e);;
        }
        loadArticleList();
        openFileByEditor(openTarget);
    }

    /**
     * open file by editor.
     *
     * @param openTarget
     */
    public static void openFileByEditor(final File openTarget) {
        if (!openTarget.exists() || desktop == null){
            return;
        }
        try {
            desktop.open(openTarget);
        } catch (final IOException e) {
            LOGGER.error("Error", e);;
        }
    }

    /**
     * return current tab.
     * @return current tab.
     */
    private final Tab getCurrentTab() {
        return tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex());
    }

    /**
     * 現在選択されているタブの WebView を返す。
     * @return WebView オブジェクトか empty
     */
    private final Optional<WebView> getCurrentWebView() {
        final ObservableList<Tab> tabs = tabPane.getTabs();
        final Node content = tabs.get(tabPane.getSelectionModel()
                .getSelectedIndex()).getContent();
        return content instanceof WebView
                ? Optional.of((WebView) content)
                : Optional.empty();
    }

    /**
     * urlText の入力値を開く.
     */
    @FXML
    public final void readUrlText(final ActionEvent event) {
        loadUrl(urlText.getText());
    }

    /**
     * PrinterJob を呼ぶ.
     *
     * @see <a href="http://d.hatena.ne.jp/tatsu-no-toshigo/20141208/1417957734">
     * 続・JavaFX8の印刷機能によるHTMLのPDF変換</a>
     */
    @FXML
    public final void callPrinterJob() {
        new AlertDialog.Builder(getParent())
            .setTitle("PDF印刷").setMessage("PDFへの印刷を実行しますか？")
            .setOnPositive("OK", () -> {
                final Optional<WebView> wv = getCurrentWebView();
                if (!wv.isPresent()) {
                    return;
                }
                final Printer printer = Printer.getDefaultPrinter();
                if(printer == null) {
                    LOGGER.warn("printe is null");
                    return;
                } else {
                    LOGGER.info("print name:" + printer.getName());
                }

                final PageLayout pageLayout = printer.createPageLayout(
                        Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);

                final PrinterJob job = PrinterJob.createPrinterJob(printer);
                if (job == null) {
                    return;
                }
                job.getJobSettings().setJobName(Config.get(Config.Key.WIKI_TITLE));
                LOGGER.info("jobName [{}]\n", job.getJobSettings().getJobName());
                wv.get().getEngine().print(job);
                job.endJob();
                AlertDialog.showMessage(getParent(), "PDF印刷完了", "PDF印刷が正常に完了しました。");
            }).build().show();
    }

    /**
     * call capture.
     * @param filename
     */
    @FXML
    public void callCapture() {
        FileUtil.capture(Long.toString(System.nanoTime()), getCurrentRectangle());
    }

    /**
     * set stylesheet.
     */
    @FXML
    public void callSetOnStyle() {
        final String styleName = style.getItems().get(style.getSelectionModel().getSelectedIndex())
                .toString();
        if (StringUtils.isEmpty(styleName)) {
            return;
        }
        final ObservableList<String> stylesheets = stage.getScene().getStylesheets();
        if (stylesheets != null) {
            stylesheets.clear();
        }
        if ("MODENA".equals(styleName) || "CASPIAN".equals(styleName)) {
            Application.setUserAgentStylesheet(styleName);
        } else {
            Application.setUserAgentStylesheet("MODENA");
            stylesheets.add(Style.getPath(styleName));
        }

        // for highlighting script area.
        //stylesheets.add(getClass().getResource("css/highlights/java-keywords.css").toExternalForm());
        Config.store(Config.Key.STYLESHEET, styleName);
    }

    /**
     * get current window size rectangle.
     * @return
     */
    private Rectangle getCurrentRectangle() {
        return new Rectangle(
                (int) stage.getX(),
                (int) stage.getY(),
                (int) stage.getWidth(),
                (int) stage.getHeight());
    }

    /**
     * アプリケーションを終了する.
     * <ol>
     * <li>生成した一時 HTML ファイルを削除.
     * </ol>
     */
    @FXML
    public final void closeApplication() {
        try {
            if (BACKUP != null) {
                BACKUP.shutdownNow();
            }
        } finally {
            this.stage.close();
            System.exit(0);
        }
    }

    /**
     * stage をセットする.
     * @param stage
     */
    public final void setStage(final Stage stage) {
        this.stage = stage;
    }

    /**
     * only call child method.
     */
    @FXML
    protected void openScripter() {
        new jp.toastkid.script.Main().show(stage);
    }

    /**
     * 引数で渡された文字列を画面下部のステータスラベルに表示する.
     * @param message 文字列
     */
    protected final void setStatus(final String message) {
        status.setText(message);
        LOGGER.info(message);
    }

    /**
     * height をセットする.
     * @param height 縦幅
     */
    public final void setSize(final double width, final double height) {
        this.width  = width;
        this.height = height;
    }
}
