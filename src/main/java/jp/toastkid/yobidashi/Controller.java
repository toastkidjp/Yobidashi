package jp.toastkid.yobidashi;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;
import com.sun.javafx.scene.control.skin.ContextMenuContent;
import com.sun.javafx.scene.control.skin.ContextMenuContent.MenuItemContainer;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import jp.toastkid.dialog.AlertDialog;
import jp.toastkid.dialog.ProgressDialog;
import jp.toastkid.jfx.common.Style;
import jp.toastkid.jfx.common.control.AutoCompleteTextField;
import jp.toastkid.jobs.FileWatcherJob;
import jp.toastkid.libs.WebServiceHelper;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.libs.utils.RuntimeUtil;
import jp.toastkid.wiki.ArticleGenerator;
import jp.toastkid.wiki.FullScreen;
import jp.toastkid.wiki.control.ArticleListCell;
import jp.toastkid.wiki.control.ArticleTab;
import jp.toastkid.wiki.control.BaseWebTab;
import jp.toastkid.wiki.control.ContentTab;
import jp.toastkid.wiki.control.ReloadableTab;
import jp.toastkid.wiki.control.WebTab;
import jp.toastkid.wiki.models.Article;
import jp.toastkid.wiki.models.Article.Extension;
import jp.toastkid.wiki.models.Config;
import jp.toastkid.wiki.models.Defines;
import jp.toastkid.wiki.search.ArticleSearcher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * JavaFX WikiClient's Controller.
 *
 * @author Toast kid
 */
public final class Controller implements Initializable {

    /** Speed dial's scene graph file. */
    private static final String SPEED_DIAL_FXML = Defines.SCENE_DIR + "/SpeedDial.fxml";

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    /** /path/to/log file. */
    private static final String PATH_APP_LOG     = Defines.LOG_DIR    + "/app.log";

    /** /path/to/path to about file. */
    private static final String PATH_ABOUT_APP   = "README.md";

    /** /path/to/path to bookmark file. */
    private static final String PATH_BOOKMARK    = Defines.USER_DIR + "/bookmark.txt";

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

    /** root pane. */
    @FXML
    protected Pane root;

    /** header. */
    @FXML
    private Pane header;

    /** footer. */
    @FXML
    private Pane footer;

    /** title label. */
    @FXML
    private Label title;

    /** title's tooltip. */
    @FXML
    private Tooltip titleTooltip;

    /** URL Input Area. */
    @FXML
    private TextField urlText;

    /** Left-side tabs area(Articles). */
    @FXML
    private TabPane leftTabs;

    /** Right-side tabs area(WebView). */
    @FXML
    private TabPane tabPane;

    /** List of articles. */
    @FXML
    private ListView<Article> articleList;

    /** List of histories. */
    @FXML
    private ListView<Article> historyList;

    /** List of Bookmark article. */
    @FXML
    private ListView<Article> bookmarkList;

    /** Status message. */
    @FXML
    private Label status;

    /** Search category selector. */
    @SuppressWarnings("rawtypes")
    @FXML
    private ComboBox searchCategory;

    /** Web search query. */
    @FXML
    private TextField webQuery;

    /** Splitter of TabPane. */
    @FXML
    private SplitPane splitter;

    /** Reload button. */
    @FXML
    private Button reload;

    /** Web search button. */
    @FXML
    private Button webSearch;

    /** Stylesheet selector. */
    @FXML
    private ComboBox<String> style;

    /** in article searcher area. */
    @FXML
    private HBox searcherArea;

    /** in article searcher input box. */
    @FXML
    private TextField searcherInput;

    /** calendar. */
    @FXML
    private DatePicker calendar;

    /** for desktop control. */
    private static Desktop desktop;

    /** functions class. */
    private ArticleGenerator articleGenerator;

    /** Stage. */
    private Stage stage;

    /** width. */
    private double width;

    /** height. */
    private double height;

    /** BMI area's controller. */
    @FXML
    private jp.toastkid.bmi.Controller bmiController;

    /** search history. */
    private final TextField queryInput
        = new AutoCompleteTextField(){{setPromptText("Input search keyword.");}};

    /** filter input. */
    private final TextField filterInput
        = new AutoCompleteTextField(){{setPromptText("Input part of article name.");}};

    /** for auto backup. */
    private static final ExecutorService BACKUP = Executors.newSingleThreadExecutor();

    /** file watcher. */
    private static final FileWatcherJob FILE_WATCHER = new FileWatcherJob();

    /** use for full screen. */
    private FullScreen fs;

    /** splitter closing transition. */
    private TranslateTransition splitterClose;

    /** splitter opening transition. */
    private TranslateTransition splitterOpen;

    /** Snackbar. */
    @FXML
    private JFXSnackbar snackbar;

    /** Container of title burger. */
    @FXML
    private StackPane titleBurgerContainer;

    /** title hamburger. */
    @FXML
    private JFXHamburger titleBurger;

    /** Option menu container. */
    @FXML
    private StackPane optionsBurger;

    /** Left-side drawer. */
    @FXML
    private JFXDrawer leftDrawer;

    /** Right-side drawer. */
    @FXML
    private JFXDrawer rightDrawer;

    /** Side Menu pane controller. */
    @FXML
    private SideMenuController sideMenuController;

    /** Tools pane controller. */
    @FXML
    private ToolsController toolsController;

    /** Speed dial's controller. */
    private jp.toastkid.speed_dial.Controller speedDialController;

    @Override
    public final void initialize(final URL url, final ResourceBundle bundle) {

        // initialize parallel task.
        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        final ExecutorService es = Executors.newFixedThreadPool(
                availableProcessors + availableProcessors + availableProcessors);

        snackbar.registerSnackbarContainer(root);
        Mono.create(emitter -> emitter.success(
                String.format("Memory: %,3d[MB]", RuntimeUtil.calcUsedMemorySize() / 1_000_000L)
                )
            )
            .delaySubscriptionMillis(5000L)
            .repeat()
            .map(Object::toString)
            .subscribeOn(Schedulers.newElastic("MemoryWatcherDelay"))
            .subscribe(message -> setStatus(message, false));

        final ProgressDialog pd = new ProgressDialog.Builder()
            .setCommand(new Task<Integer>() {
                final int tasks = 9;
                final AtomicInteger done = new AtomicInteger(0);

                @Override
                public Integer call() {
                    // 長い時間のかかるタスク
                    try {
                        setTitleOnToolbar("");
                        if (Desktop.isDesktopSupported()) {
                            desktop = Desktop.getDesktop();
                        }
                        setProgress("availableProcessors = " + availableProcessors);

                        es.execute(() -> {
                            final long start = System.currentTimeMillis();
                            articleGenerator = new ArticleGenerator();
                            Platform.runLater(Controller.this::callHome);
                            final String message = Thread.currentThread().getName()
                                    + " Ended initialize ArticleGenerator. "
                                    + (System.currentTimeMillis() - start) + "ms";
                            setProgress(message);
                            LOGGER.info(message);
                        });

                        es.execute(() -> {
                            final long start = System.currentTimeMillis();
                            prepareArticleList();
                            prepareBookmarks();
                            leftTabs.getSelectionModel().select(2);

                            final String message = Thread.currentThread().getName()
                                    + " Ended read article names. "
                                    + (System.currentTimeMillis() - start) + "ms";
                            setProgress(message);
                            LOGGER.info(message);
                        });

                        es.execute(() -> {
                            final long start = System.currentTimeMillis();
                            Platform.runLater( () -> {
                                readStyleSheets();
                                setStylesheet();
                                splitter.setDividerPosition(0, DEFAULT_DIVIDER_POSITION);
                            });
                            final String message = Thread.currentThread().getName()
                                    + " Ended initialize stylesheets. "
                                    + (System.currentTimeMillis() - start) + "ms";
                            setProgress(message);
                            LOGGER.info(message);
                        });

                        // insert WebView to tabPane.
                        es.execute(() -> {
                            final long start = System.currentTimeMillis();
                            tabPane.getSelectionModel().selectedItemProperty().addListener(
                                    (a, prevTab, nextTab) -> {
                                        // (121224) タブ切り替え時の URL 表示の変更
                                        final String tabUrl = getCurrentTab().getUrl();
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
                                                ArticleGenerator.titleToFileName(nextTab.getText()) + Article.Extension.WIKI.text()
                                                );
                                        if (selected.exists()){
                                            Config.article = new Article(selected);
                                            urlText.setText(Config.article.toInternalUrl());
                                            focusOn();
                                        }
                                    }
                                    );
                            final String message = Thread.currentThread().getName()
                                    + " Ended initialize right tabs. "
                                    + (System.currentTimeMillis() - start) + "ms";
                            setProgress(message);
                            LOGGER.info(message);
                        });

                        es.execute(() -> {
                            final long start = System.currentTimeMillis();
                            searcherInput.textProperty().addListener((observable, oldValue, newValue) ->
                            getCurrentTab().highlight(Optional.ofNullable(newValue), WINDOW_FIND_DOWN));
                            final String message = Thread.currentThread().getName()
                                    + " Ended initialize tools. "
                                    + (System.currentTimeMillis() - start) + "ms";
                            setProgress(message);
                            LOGGER.info(message);
                        });

                        es.execute(() -> {
                            final long start = System.currentTimeMillis();
                            // init the title hamburger icon
                            leftDrawer.setOnDrawerOpening(e -> {
                                titleBurger.getAnimation().setRate(1);
                                titleBurger.getAnimation().play();
                            });
                            leftDrawer.setOnDrawerClosing(e -> {
                                titleBurger.getAnimation().setRate(-1);
                                titleBurger.getAnimation().play();
                            });
                            titleBurgerContainer.setOnMouseClicked(e->{
                                if (leftDrawer.isHidden() || leftDrawer.isHidding()) {
                                    leftDrawer.open();
                                } else {
                                    leftDrawer.close();
                                }
                            });
                            optionsBurger.setOnMouseClicked(e->switchRightDrawer());
                            final String message = Thread.currentThread().getName()
                                    + " Ended initialize drawer. "
                                    + (System.currentTimeMillis() - start) + "ms";
                            setProgress(message);
                            LOGGER.info(message);
                        });

                        es.execute(() -> {
                            footer.setOnMousePressed(event -> moveToBottom());
                            setProgress("Ended set footer action.");
                        });

                        es.execute(() -> {
                            BACKUP.submit(FILE_WATCHER);
                            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                                if (BACKUP != null) {
                                    BACKUP.shutdownNow();
                                }
                            }));
                            setProgress("Ended launching backup job.");
                        });
                        es.shutdown();
                    } catch (final Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        updateProgress(100, 100);
                    }
                    return 0;
                }

                private void setProgress(final String message) {
                    Platform.runLater(() -> {
                        final int i = done.incrementAndGet();
                        updateProgress(i, tasks);
                        updateMessage(message);
                        LOGGER.info("Progress {}, {}/{}, {}", getProgress(), i, tasks, message);
                    });
                }

            })
            .build();
        pd.start(stage);
    }

    /**
     * Switch right drawer's state.
     */
    private void switchRightDrawer() {
        if (isRightDrawerClosing()) {
            rightDrawer.open();
            return;
        }
        rightDrawer.close();
    }

    /**
     * Return drawer hiding.
     * @return
     */
    private boolean isRightDrawerClosing() {
        return rightDrawer.isHidden() || rightDrawer.isHidding();
    }

    /**
     * Set title.
     * @param titleStr
     */
    private void setTitleOnToolbar(final String titleStr) {
        final String text = titleStr == null
                ? Config.get(Config.Key.APP_TITLE)
                : titleStr + " - " + Config.get(Config.Key.APP_TITLE);
        title.setText(text);
        titleTooltip.setText(text);
    }

    /**
     * Set up searcher and scripter. this method call by FXWikiClient.
     */
    protected void setupExpandables() {
        hideSearcher();
        final ObservableMap<KeyCombination, Runnable> accelerators
            = stage.getScene().getAccelerators();
        accelerators.put(FIRST_TAB,   () -> selectTab(0));
        accelerators.put(SECOND_TAB,  () -> selectTab(1));
        accelerators.put(THIRD_TAB,   () -> selectTab(2));
        accelerators.put(FOURTH_TAB,  () -> selectTab(3));
        accelerators.put(FIFTH_TAB,   () -> selectTab(4));
        accelerators.put(SIXTH_TAB,   () -> selectTab(5));
        accelerators.put(SEVENTH_TAB, () -> selectTab(6));
        accelerators.put(EIGHTH_TAB,  () -> selectTab(7));
        accelerators.put(NINTH_TAB,   () -> selectTab(8));
        accelerators.put(SHOW_LEFT_PANE, this::showLeftPane);
        accelerators.put(HIDE_LEFT_PANE, this::hideLeftPane);
        accelerators.put(APPEAR_SEARCHER, () -> {
            if (searcherArea.visibleProperty().getValue()) {
                hideSearcher();
            } else {
                openSearcher();
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
        getCurrentTab().highlight(Optional.ofNullable(searcherInput.getText()), WINDOW_FIND_UP);
    }

    /**
     * search forward.
     */
    @FXML
    protected void searchDown() {
        getCurrentTab().highlight(Optional.ofNullable(searcherInput.getText()), WINDOW_FIND_DOWN);
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
     * move to top of current page.
     * @see <a href="https://community.oracle.com/thread/2595743">
     * How to auto-scroll to the end in WebView?</a>
     */
    @FXML
    private final void moveToTop() {
        getCurrentTab().moveToTop();
    }

    /**
     * move to bottom of current page.
     * @see <a href="https://community.oracle.com/thread/2595743">
     * How to auto-scroll to the end in WebView?</a>
     */
    private final void moveToBottom() {
        getCurrentTab().moveToBottom();
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
     * Load diary specified LocalDate.
     * @param date
     */
    private void loadDiary(final LocalDate date) {
        try {
            final String prefix = "日記" + date.toString();
            final Optional<Article> opt = articleList.getItems().stream()
                    .filter(item -> item.title.startsWith(prefix))
                    .findFirst();
            if (!opt.isPresent()) {
                new JFXSnackbar().show(prefix + "'s diary is not exist.", 4000L);
                return;
            }
            opt.ifPresent(article -> getCurrentTab().loadUrl(article.toInternalUrl()));
        } catch (final Exception e) {
            LOGGER.error("no such element", e);
        }
    }

    /**
     * Show HTML source.
     */
    @FXML
    private final void callHtmlSource() {
        final ReloadableTab tab = getCurrentTab();

        if (!(tab instanceof BaseWebTab)) {
            setStatus("This tab can't show source.");
            return;
        }

        final String htmlSource = ((BaseWebTab) tab).htmlSource();
        openWebTabWithContent(tab.getTitle().concat("'s HTML Source"), htmlSource, "text/plain");
    }

    /**
     * Set "home"(it means appearing when start-up) article.
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
     * Reload tab content.
     */
    @FXML
    private final void reload() {
        getCurrentTab().reload();
    }

    /**
     * Search Web with query.
     * @param event ActionEvent
     */
    @FXML
    private final void webSearch() {
        final String kind  = searchCategory.getItems()
                .get(searchCategory.getSelectionModel().getSelectedIndex())
                .toString();
        final String query = webQuery.getText();
        if (StringUtils.isEmpty(query)){
            return ;
        }
        final String url = WebServiceHelper.buildRequestUrl(query, kind);
        if (StringUtils.isEmpty(url)){
            return;
        }
        openWebTab("Loading...", url);
    }

    /**
     * Open new tab having SpeedDial.
     */
    @FXML
    private void openSpeedDialTab() {
        if (speedDialController == null) {
            speedDialController = readSpeedDial();
            speedDialController.setTitle(Config.get(Config.Key.APP_TITLE));
            speedDialController.setZero();
            speedDialController.setBackground(articleGenerator.getBackground());
            speedDialController.setOnWebSearch((query, type) ->
                openWebTab("Loading...", WebServiceHelper.buildRequestUrl(query, type))
            );
            speedDialController.setOnEmptyAction(() -> showSnackbar("You have to input any query."));
        }

        final Pane sdRoot = speedDialController.getRoot();
        sdRoot.setPrefWidth(width * 0.8);
        openTab(new ContentTab.Builder().setTitle("Speed Dial")
                .setContent(sdRoot).setOnClose(this::closeTab).build());
    }

    /**
     * Init speed dial's controller.
     * @return Controller
     */
    private final jp.toastkid.speed_dial.Controller readSpeedDial() {
        try {
            final FXMLLoader loader = new FXMLLoader(
                    getClass().getClassLoader().getResource(SPEED_DIAL_FXML));
            loader.load();
            return (jp.toastkid.speed_dial.Controller) loader.getController();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
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
     * Open new tab having WebView with title.
     * @param article tab's article
     */
    private void openArticleTab(final Article article) {
        LOGGER.info("call open atab " + article.title);
        openTab(makeArticleTab(article));
    }

    /**
     * Make article tab.
     * @param article
     * @return ArticleTab
     */
    private ArticleTab makeArticleTab(final Article article) {
        return new ArticleTab.Builder()
                .setArticle(article)
                .setOnClose(this::closeTab)
                .setOnContextMenuRequested(event -> showContextMenu())
                .setOnOpenNewArticle(this::openArticleTab)
                .setOnOpenUrl(this::openWebTab)
                .setOnLoad(() -> {
                    // 読み込んだ内容を HTML 変換し、一時ファイルに書き出し、さらにそれを読み込んで表示
                    urlText.setText(Config.article.toInternalUrl());
                    Platform.runLater(() -> setTitleOnToolbar(article.title));
                    // deep copy を渡す.
                    addHistory(Config.article.clone());
                    focusOn();
                })
                .setCreatePopupHandler(this::handleCreatePopup)//TODO WebViewのあたり出来上がってから実装
                .build();
    }

    /**
     *
     * @param popupFeature
     * @return
     */
    private WebEngine handleCreatePopup(final PopupFeatures popupFeature) {
        final ArticleTab tab = makeArticleTab(new Article(new File("")));
        tabPane.getSelectionModel().selectLast();
        return tab.getWebView().getEngine();
    }

    /**
     * Open new web tab.
     * @param title
     * @param url
     */
    private void openWebTab(final String title, final String url) {
        openTab(new WebTab.Builder().setTitle(title).setUrl(url).setOnClose(this::closeTab).build());
    }

    /**
     * Open new web tab for view html content.
     * @param title
     * @param content
     * @param mimetype
     */
    private void openWebTabWithContent(
            final String title,
            final String content,
            final String mimetype
            ) {
        final WebTab tab = new WebTab.Builder()
                .setTitle(title)
                .setContent(content)
                .setMimetype(mimetype)
                .setOnClose(this::closeTab)
                .build();
        openTab(tab);
    }

    /**
     * Show Context(Popup) menu.
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

            // add new item:
            cmc.getItemsContainer().getChildren().addAll(
                    makeContextMenuItemContainerWithAction(
                            cmc, "文字数計測", event -> sideMenuController.callFileLength()),
                    makeContextMenuItemContainerWithAction(
                            cmc, "Full Screen", event -> callTabFullScreen()),
                    makeContextMenuItemContainerWithAction(
                            cmc, "Slide show", event -> slideShow()),
                    makeContextMenuItemContainerWithAction(
                            cmc, "Open new tab", event -> openSpeedDialTab()),
                    makeContextMenuItemContainerWithAction(
                            cmc, "Show HTML source", event -> callHtmlSource()),
                    makeContextMenuItemContainerWithAction(
                            cmc, "Find in page", event -> openSearcher()),
                    makeContextMenuItemContainerWithAction(
                            cmc, "Go to top of this page", event -> moveToTop()),
                    makeContextMenuItemContainerWithAction(
                            cmc, "Go to bottom of this page", event -> moveToBottom()),
                    makeContextMenuItemContainerWithAction(
                            cmc, "Search all article", event -> searchArticle("", "")),
                    makeContextMenuItemContainerWithAction(
                            cmc,
                            isRightDrawerClosing() ? "Open tools" : "Close tools",
                            event -> switchRightDrawer()
                            ),
                    cmc.new MenuItemContainer(
                            isHideLeftPane()
                            ? makeMenuItemWithAction("記事一覧を開く",   event -> showLeftPane())
                            : makeMenuItemWithAction("記事一覧を閉じる", event -> hideLeftPane())
                            )
                    );

            return (PopupWindow)window;
        }
        return null;
    }

    /**
     * Make MenuItemContainer with ActionEvent's EventHandler.
     * @param cmc ContextMenuContent
     * @param labelText label text.
     * @param action event
     * @return MenuItem
     */
    private MenuItemContainer makeContextMenuItemContainerWithAction(
            final ContextMenuContent cmc,
            final String labelText,
            final EventHandler<ActionEvent> action
            ) {
        return cmc.new MenuItemContainer(makeMenuItemWithAction(labelText, action));
    }

    /**
     * Make MenuItem with ActionEvent's EventHandler.
     * @param labelText label text.
     * @param action event
     * @return MenuItem
     */
    private MenuItem makeMenuItemWithAction(
            final String labelText,
            final EventHandler<ActionEvent> action
            ) {
        return new MenuItem(labelText){{ setOnAction(action);}};
    }

    /**
     * Show left panel.
     */
    private void showLeftPane() {
        if (splitterOpen == null) {
            splitterOpen = new TranslateTransition(Duration.seconds(0.25d), tabPane);
            splitterOpen.setFromX(-200);
            splitterOpen.setToX(0);
            splitterOpen.setInterpolator(Interpolator.LINEAR);
            splitterOpen.setCycleCount(1);
        }
        splitterOpen.play();
        splitter.setDividerPosition(0, DEFAULT_DIVIDER_POSITION);
    }

    /**
     * 左側を隠す.
     */
    private void hideLeftPane() {
        if (splitterClose == null) {
            splitterClose = new TranslateTransition(Duration.seconds(0.25d), tabPane);
            splitterClose.setFromX(200);
            splitterClose.setToX(0);
            splitterClose.setInterpolator(Interpolator.LINEAR);
            splitterClose.setCycleCount(1);
        }
        splitterClose.play();
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
     * 現在選択しているタブを閉じる. 1つしか開いていない時は閉じない.
     */
    @FXML
    private final void closeTab() {
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
            return;
        }
        showSnackbar("Can't close tab when current tabs count 1.");
    }

    /**
     * Stop loading.
     * TODO 動作未検証
     * @param event
     */
    @FXML
    private final void stop() {
        final ReloadableTab tab = getCurrentTab();
        if (!(tab instanceof ArticleTab)) {
            return;
        }
        Platform.runLater(tab::stop);
    }

    /**
     * Alias for using method reference.
     */
    private void searchArticle() {
        searchArticle("", "");
    }

    /**
     * 再帰的に呼び出すためメソッドに切り出し.
     * @param q クエリ
     * @param f 記事名フィルタ文字列
     */
    protected void searchArticle(final String q, final String f) {
        final CheckBox isAnd       = new JFXCheckBox("AND Search"){{setSelected(true);}};
        new AlertDialog.Builder(getParent())
            .setTitle("All article search").setMessage("この操作の実行には時間がかかります。")
            //"記事名のみを対象に検索"
            .addControl(queryInput, new Label("Filter article name"), filterInput, isAnd)
            .setOnPositive("OK", () -> {
                final String query = queryInput.getText().trim();
                ((AutoCompleteTextField) queryInput).getEntries().add(query);

                // 入っていない時もあるので.
                final String filter = filterInput.getText();
                if (StringUtils.isNotBlank(filter)) {
                    ((AutoCompleteTextField) filterInput).getEntries().add(filter);
                }

                final ArticleSearcher fileSearcher = new ArticleSearcher.Builder()
                        .setHomeDirPath(Config.get("articleDir"))
                        .setAnd(isAnd.isSelected())
                        .setSelectName(filter)
                        .setEmptyAction(() -> {
                            showSnackbar(String.format("Not found article with '%s'.", query));
                            searchArticle(queryInput.getText(), filterInput.getText());
                        })
                        .setSuccessAction(this::setStatus)
                        .setTabPane(leftTabs)
                        .setListViewInitializer(this::initArticleList)
                        .setHeight(articleList.getHeight())
                        .build();
                fileSearcher.search(query);
            }).build().show();
    }

    /**
     * Call RSS Feeder．
     */
    @FXML
    private final void callRssFeeder() {
        final long start = System.currentTimeMillis();
       /* TODO impl
        * openArticleTab("RSS Feeder");
        String content = RssFeeder.run();
        if (StringUtils.isEmpty(content)) {
            content = "コンテンツを取得できませんでした.";
        }
        articleGenerator.generateHtml(content, "RSS Feeder");
        loadDefaultFile();*/
        setStatus("Done：" + (System.currentTimeMillis() - start) + "[ms]");
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
     * Show slide show.
     */
    @FXML
    private void slideShow() {
        new jp.toastkid.slideshow.Main().show(this.stage, Config.article.file.getAbsolutePath());
    }

    /**
     * Open folder.
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
     * Open home.
     */
    @FXML
    private final void callHome() {
        //loadUrl(Config.get(Config.Key.HOME));
        openSpeedDialTab();
    }

    /**
     * Prepare common article list.
     */
    private void prepareArticleList() {
        Mono.create(emitter -> {
            initArticleList(articleList);
            emitter.success("");
        })
        .subscribeOn(Schedulers.newSingle("I/O"))
        .subscribe(empty -> {
            final long start = System.currentTimeMillis();
            loadArticleList();
            LOGGER.info("Ended init loadArticleList. {}[ms]", System.currentTimeMillis() - start);
            });
        initArticleList(historyList);
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

            if (newVal == null) {
                return;
            }

            final Optional<Tab> first = tabPane.getTabs().stream()
                    .filter(tab -> (tab instanceof ArticleTab)
                            && ((ArticleTab) tab).getArticle().equals(newVal))
                    .findFirst();
            if (first.isPresent()) {
                first.ifPresent(tab -> tabPane.getSelectionModel().select(tab));
                return;
            }
            openArticleTab(newVal);
        });
    }

    /**
     * Load all articles list.
     */
    @FXML
    private void loadArticleList() {
        final ObservableList<Article> items = articleList.getItems();
        items.removeAll();
        Flux.fromIterable(Article.readAllArticleNames(Config.get("articleDir")))
            .subscribeOn(Schedulers.newSingle("I/O"))
            .doOnTerminate(this::focusOn)
            .subscribe(items::add);
    }

    /**
     * Prepare bookmark list.
     */
    private void prepareBookmarks() {
        final ObservableList<Article> bookmarks = bookmarkList.getItems();
        Mono.create(emitter -> {
            initArticleList(bookmarkList);
            emitter.success("");
            })
            .publishOn(Schedulers.elastic())
            .subscribeOn(Schedulers.elastic())
            .subscribe(
                empty -> FileUtil.readLines(PATH_BOOKMARK, "UTF-8")
                    .collect(ArticleGenerator::titleToFileName)
                    .collect(fileName -> fileName + ".txt")
                    .collect(Article::find)
                    .each(bookmarks::add)
                    );
    }

    /**
     * 現在選択中のファイルに ListView をフォーカスする.
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
     * 記事名をリストの先頭に追加する。
     * @param article Article Object
     */
    private final void addHistory(final Article article) {
        final ObservableList<Article> items = historyList.getItems();
        if (!items.contains(article)) {
            items.add(0, article);
        }
        FILE_WATCHER.add(article.file);
    }

    /**
     * Clear view history.
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
     * Make new Markdown.
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
                .setTitle("Make new article")
                .setMessage(newArticleMessage)
                .addControl(input)
                .build().show();
        final String newFileName = input.getText();
        if (!StringUtils.isEmpty(newFileName)){
            Config.article = Article.find(ArticleGenerator.titleToFileName(newFileName) + ext.text());
            callEditor();
        }
    }

    /**
     * Call copy article。
     * <HR>
     * (130707) メッセージ変更<BR>
     * (130512) 作成<BR>
     */
    @FXML
    private final void callCopy() {
        callRenameArticle(true);
    }

    /**
     * Call rename article。
     * <HR>
     * (130707) メッセージ変更<BR>
     * (130512) 作成<BR>
     */
    @FXML
    private final void callRename() {
        callRenameArticle(false);
    }

    /**
     * Rename article。
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
            .setTitle("Rename article").setMessage(renameMessage)
            .addControl(input)
            .setOnPositive("OK", () ->{
                final String newTitle = input.getText();
                if (StringUtils.isBlank(newTitle)) {
                    return;
                }
                final File dest = new File(
                        Config.get(Config.Key.ARTICLE_DIR),
                        ArticleGenerator.titleToFileName(newTitle) + Config.article.extention()
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
                    getCurrentTab().loadUrl(Config.article.toInternalUrl());
                    removeHistory(new Article(file));
                    loadArticleList();
                    return;
                }
                if (isCopy) {
                    title   = "Copy failed";
                    message = "ファイルのコピーに失敗しました。";
                } else {
                    title   = "Rename failed";
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
    private final void callDelete() {
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
            .setTitle("Delete file")
            .setMessage(deleteTarget + " を削除しますか？")
            .setOnPositive("OK", () -> {
                article.file.delete();
                AlertDialog.showMessage(parent, "削除完了", deleteTarget + " を削除しました。");
                // (130317)
                final String homePath = Config.get(Config.Key.HOME);
                // 削除後はホーム画面に戻す
                getCurrentTab().loadUrl(homePath);
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
     * Open file by editor.
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
     * Return current tab.
     * @return current tab.
     */
    private final ReloadableTab getCurrentTab() {
        final Tab tab = tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex());
        if (!(tab instanceof ReloadableTab)) {
            LOGGER.warn("Not instance Tab");
            return null;
        }
        return (ReloadableTab) tab;
    }

    /**
     * Open urlText's value.
     */
    @FXML
    public final void readUrlText(final ActionEvent event) {
        getCurrentTab().loadUrl(urlText.getText());
    }

    /**
     * Call PrinterJob.
     *
     * @see <a href="http://d.hatena.ne.jp/tatsu-no-toshigo/20141208/1417957734">
     * 続・JavaFX8の印刷機能によるHTMLのPDF変換</a>
     */
    @FXML
    public final void callPrinterJob() {
        new AlertDialog.Builder(getParent())
            .setTitle("PDF印刷").setMessage("PDFへの印刷を実行しますか？")
            .setOnPositive("OK", () -> {
                final ReloadableTab tab = getCurrentTab();
                if (!tab.canLoadUrl()) {
                    return;
                }
                final Printer printer = Printer.getDefaultPrinter();
                if (printer == null) {
                    LOGGER.warn("printe is null");
                    return;
                } else {
                    LOGGER.info("print name:" + printer.getName());
                }

                printer.createPageLayout(
                        Paper.A4,
                        PageOrientation.PORTRAIT,
                        Printer.MarginType.DEFAULT
                        );

                final PrinterJob job = PrinterJob.createPrinterJob(printer);
                if (job == null) {
                    return;
                }
                job.getJobSettings().setJobName(Config.get(Config.Key.APP_TITLE));
                LOGGER.info("jobName [{}]\n", job.getJobSettings().getJobName());
                tab.print(job);
                job.endJob();
                AlertDialog.showMessage(getParent(), "Complete print to PDF", "PDF印刷が正常に完了しました。");
            }).build().show();
    }

    /**
     * apply stylesheet.
     */
    @FXML
    public void callApplyStyle() {
        final String styleName = style.getItems().get(style.getSelectionModel().getSelectedIndex())
                .toString();
        if (StringUtils.isEmpty(styleName)) {
            return;
        }
        final ObservableList<String> stylesheets = stage.getScene().getStylesheets();
        if (stylesheets != null) {
            stylesheets.clear();
        }
        final String commonStyle = getClass().getResource("/css/common").toString();
        if ("MODENA".equals(styleName) || "CASPIAN".equals(styleName)) {
            Application.setUserAgentStylesheet(styleName);
            stylesheets.add(commonStyle);
        } else {
            Application.setUserAgentStylesheet("MODENA");
            stylesheets.addAll(commonStyle, Style.getPath(styleName));
        }

        // for highlighting script area.
        stylesheets.add(getClass().getClassLoader().getResource("keywords.css").toExternalForm());
        Config.store(Config.Key.STYLESHEET, styleName);
    }

    /**
     * Set passed stage on this object.
     * @param stage
     */
    public final void setStage(final Stage stage) {
        this.stage = stage;
    }

    /**
     * Show passed message on status label and snackbar.
     * @param message message string
     */
    protected final void setStatus(final String message) {
        setStatus(message, true);
    }

    /**
     * Show passed message on status label.
     * @param message message string
     * @param showLog logging and show snackbar
     */
    private final void setStatus(final String message, final boolean showLog) {
        Platform.runLater(() -> status.setText(message));
        if (showLog) {
            LOGGER.info(message);
            showSnackbar(message);
        }
    }

    /**
     * Show message with Snackbar.
     * @param message
     */
    private void showSnackbar(final String message) {
        snackbar.fireEvent(new SnackbarEvent(message));
    }

    /**
     * Set width and height to Controller.
     * @param height 縦幅
     */
    public final void setSize(final double width, final double height) {
        this.width  = width;
        this.height = height;
    }

    /**
     * Call LogViewer.
     */
    private final void openLogViewer() {
        if (!new File(PATH_APP_LOG).exists()) {
            LOGGER.warn(new File(PATH_APP_LOG).getAbsolutePath() + " is not exists.");
            return;
        }
        final String log = String.format(
                "<pre>%s</pre>",
                FileUtil.getStrFromFile(PATH_APP_LOG, StandardCharsets.UTF_8.name())
                );
        articleGenerator.generateHtml(log, "LogViewer");
        openWebTab("LogViewer", Defines.findInstallDir() + Defines.TEMP_FILE_NAME);
    }

    /**
     * Set stage to SideMenuController.
     */
    protected void setupSideMenu() {
        sideMenuController.setStage(this.stage);
        sideMenuController.setOnSearch(this::searchArticle);
        sideMenuController.setOnEdit(this::callEditor);
        sideMenuController.setOnNewTab(this::openSpeedDialTab);
        sideMenuController.setOnCloseTab(this::closeTab);
        sideMenuController.setOnSlideShow(this::slideShow);
        sideMenuController.setOnReload(this::reload);
        sideMenuController.setOnPreviewSource(this::callHtmlSource);
        sideMenuController.setOnWordCloud(this::openContentTab);
        sideMenuController.setOnOpenExternalFile(this::openExternalWebContent);
        sideMenuController.setOnConvertMd(this::openContentTab);
        sideMenuController.setOnOpenLogViewer(this::openLogViewer);
        sideMenuController.setOnMakeArticle(this::makeContent);
        sideMenuController.setOnCopy(this::callCopy);
        sideMenuController.setOnRename(this::callRename);
        sideMenuController.setOnDelete(this::callDelete);
        sideMenuController.setOnAbout(this::about);
        sideMenuController.setOnQuit(() -> {
            this.stage.close();
            System.exit(0);
        });
        sideMenuController.setOnOpenScriptRunner(this::openContentTab);
        sideMenuController.setOnOpenTools(this::switchRightDrawer);
        sideMenuController.setOnPopup(this::setStatus);
    }

    /**
     * Set up Tool Menu.
     */
    protected void setupToolMenu() {
        toolsController.init(this.stage);
        toolsController.setOnDrawChart(this::openContentTab);
        toolsController.setFlux(Flux.<DoubleProperty>create(emitter ->
            tabPane.getSelectionModel().selectedItemProperty()
                .addListener((a, prevTab, nextTab) -> {
                    if (prevTab != null && prevTab.getContent() instanceof WebView) {
                        final WebView prev = (WebView) prevTab.getContent();
                        prev.zoomProperty().unbind();
                    }

                    emitter.next(getCurrentTab().zoomProperty());
                })
        ));
    }

    /**
     * Show about this app.
     */
    private void about() {
        if (!new File(PATH_ABOUT_APP).exists()) {
            LOGGER.warn(new File(PATH_ABOUT_APP).getAbsolutePath() + " is not exists.");
            return;
        }

        final String title = "About";
        articleGenerator.generateHtml(articleGenerator.md2Html(PATH_ABOUT_APP), title);
        openWebTab(title, Defines.findInstallDir() + Defines.TEMP_FILE_NAME);
    }

    /**
     * Open specified tab with title.
     * @param title tab's title
     * @param content tab's content(Pane)
     */
    private void openContentTab(final String title, final Pane content) {
        openTab(makeContentTab(title, content));
    }

    /**
     * Make ContentTab.
     * @param title
     * @param content
     * @return ContentTab
     */
    private ContentTab makeContentTab(final String title, final Pane content) {
        return new ContentTab.Builder()
                .setTitle(title).setContent(content).setOnClose(this::closeTab).build();
    }

    /**
     * Open external content on tab with title.
     * @param title tab's title
     */
    private void openExternalWebContent(final String title) {
        openWebTab(title, Defines.findInstallDir() + Defines.TEMP_FILE_NAME);
    }

    /**
     * get parent window.
     * @return parent window.
     */
    private Window getParent() {
        return stage.getScene().getWindow();
    }

}
