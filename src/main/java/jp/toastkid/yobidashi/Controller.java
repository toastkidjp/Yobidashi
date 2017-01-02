package jp.toastkid.yobidashi;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
import com.jfoenix.controls.JFXTextField;
import com.sun.javafx.scene.control.skin.ContextMenuContent;
import com.sun.javafx.scene.control.skin.ContextMenuContent.MenuItemContainer;

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
import javafx.scene.web.WebView;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;
import jp.toastkid.article.ArticleGenerator;
import jp.toastkid.article.control.ArticleListCell;
import jp.toastkid.article.control.ArticleTab;
import jp.toastkid.article.control.BaseWebTab;
import jp.toastkid.article.control.ContentTab;
import jp.toastkid.article.control.ReloadableTab;
import jp.toastkid.article.control.WebTab;
import jp.toastkid.article.models.Article;
import jp.toastkid.article.models.Articles;
import jp.toastkid.article.models.ContentType;
import jp.toastkid.article.search.ArticleSearcher;
import jp.toastkid.dialog.AlertDialog;
import jp.toastkid.dialog.ProgressDialog;
import jp.toastkid.jfx.common.Style;
import jp.toastkid.jfx.common.control.AutoCompleteTextField;
import jp.toastkid.jfx.common.transition.SplitterTransitionFactory;
import jp.toastkid.jobs.FileWatcherJob;
import jp.toastkid.libs.WebServiceHelper;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.libs.utils.RuntimeUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Yobidashi's primary Controller.
 *
 * @author Toast kid
 */
public final class Controller implements Initializable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    /** Speed dial's scene graph file. */
    private static final String SPEED_DIAL_FXML = Defines.SCENE_DIR + "/SpeedDial.fxml";

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
                        setProgress("availableProcessors = " + availableProcessors);

                        es.execute(() -> {
                            final long start = System.currentTimeMillis();
                            articleGenerator = new ArticleGenerator();
                            Platform.runLater(Controller.this::callHome);
                            final String message = Thread.currentThread().getName()
                                    + " Ended initialize Articles. "
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
                                        final ReloadableTab tab = getCurrentTab();
                                        if (tab == null) {
                                            return;
                                        }
                                        final String tabUrl = tab.getUrl();
                                        if (!StringUtils.isEmpty(tabUrl) && !tabUrl.startsWith("about")){
                                            urlText.setText(tabUrl);
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
            opt.ifPresent(this::openArticleTab);
        } catch (final Exception e) {
            LOGGER.error("no such element", e);
        }
    }

    /**
     * Show HTML source.
     */
    private final void callHtmlSource() {
        final ReloadableTab tab = getCurrentTab();

        if (!(tab instanceof BaseWebTab)) {
            setStatus("This tab can't show source.");
            return;
        }

        final String htmlSource = ((BaseWebTab) tab).htmlSource();
        openWebTabWithContent(tab.getTitle().concat("'s HTML Source"), htmlSource, ContentType.TEXT);
    }

    /**
     * Reload tab content.
     */
    @FXML
    private final void reload() {
        getCurrentTab().reload();
    }

    /**
     * Open new tab having SpeedDial.
     */
    private void openSpeedDialTab() {
        try {
            final jp.toastkid.speed_dial.Controller controller = readSpeedDial();
            controller.setTitle(Config.get(Config.Key.APP_TITLE));
            controller.setZero();
            controller.setBackground(articleGenerator.getBackground());
            controller.setOnArticleSearch(this::doSearch);
            controller.setOnWebSearch((query, type) ->
                openWebTab("Loading...", WebServiceHelper.buildRequestUrl(query, type))
            );
            controller.setOnEmptyAction(() -> showSnackbar("You have to input any query."));

            final Pane sdRoot = controller.getRoot();
            sdRoot.setPrefWidth(width * 0.8);
            sdRoot.setPrefHeight(tabPane.getHeight());
            openTab(makeContentTab("Speed Dial", sdRoot));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Init speed dial's controller.
     * @return Controller
     * @throws IOException
     */
    private final jp.toastkid.speed_dial.Controller readSpeedDial() throws IOException {
        final FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource(SPEED_DIAL_FXML));
        loader.load();
        return (jp.toastkid.speed_dial.Controller) loader.getController();
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
                    urlText.setText(article.toInternalUrl());
                    Platform.runLater(() -> setTitleOnToolbar(article.title));
                    // deep copy を渡す.
                    addHistory(article.clone());
                    focusOn();
                })
                .build();
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
     * @param contentType
     */
    private void openWebTabWithContent(
            final String title,
            final String content,
            final ContentType contentType
            ) {
        final WebTab tab = new WebTab.Builder()
                .setTitle(title)
                .setContent(content)
                .setContentType(contentType)
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
                            cmc, "Full Screen", event -> stage.setFullScreen(true)),
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
        SplitterTransitionFactory.makeHorizontalSlide(splitter, DEFAULT_DIVIDER_POSITION, DEFAULT_DIVIDER_POSITION).play();
        splitter.setDividerPosition(0, DEFAULT_DIVIDER_POSITION);
    }

    /**
     * 左側を隠す.
     */
    private void hideLeftPane() {
        SplitterTransitionFactory.makeHorizontalSlide(splitter, 0.0d, DEFAULT_DIVIDER_POSITION).play();
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
     * Close all tabs.
     */
    private final void closeAllTabs() {
        tabPane.getTabs().removeAll(tabPane.getTabs());
        setStatus("Close all tabs.");
        openSpeedDialTab();
    }

    /**
     * Stop loading.
     * TODO 動作未検証
     * @param event
     */
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

                doSearch(isAnd.isSelected(), query, filter);
            }).build().show();
    }

    /**
     * Do search article.
     * @param isAnd
     * @param query
     * @param filter
     */
    private void doSearch(final boolean isAnd, final String query, final String filter) {
        final ArticleSearcher fileSearcher = new ArticleSearcher.Builder()
                .setHomeDirPath(Config.get("articleDir"))
                .setAnd(isAnd)
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
     * Show slide show.
     */
    @FXML
    private void slideShow() {
        final Optional<Article> articleOr = Optional.ofNullable(getCurrentArticle());
        if (!articleOr.isPresent()) {
            setStatus("This tab can't use slide show.");
            return;
        }
        new jp.toastkid.slideshow.Main().show(this.stage, articleOr.get().file.getAbsolutePath());
    }

    /**
     * Open home.
     */
    @FXML
    private final void callHome() {
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

            final Optional<Tab> first = tabPane.getTabs().stream()
                    .filter(tab -> (tab instanceof ArticleTab)
                            && ((ArticleTab) tab).getArticle().equals(property.getValue()))
                    .findFirst();
            if (first.isPresent()) {
                first.ifPresent(tab -> tabPane.getSelectionModel().select(tab));
                return;
            }
            openArticleTab(property.getValue());
        });
    }

    /**
     * Load all articles list.
     */
    @FXML
    private void loadArticleList() {
        final ObservableList<Article> items = articleList.getItems();
        items.removeAll();
        Flux.fromIterable(Articles.readAllArticleNames(Config.get("articleDir")))
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
                empty -> new BookmarkManager().readLines()
                    .collect(Articles::findFromTitle)
                    .each(bookmarks::add)
                    );
    }

    /**
     * 現在選択中のファイルに ListView をフォーカスする.
     */
    private void focusOn() {
        Platform.runLater(() ->
            Optional.ofNullable(getCurrentArticle()).ifPresent(article -> {
                final int indexOf = articleList.getItems().indexOf(article);
                if (indexOf != -1){
                    articleList.getSelectionModel().select(indexOf);
                    articleList.scrollTo(indexOf - FOCUS_MARGIN);
                }

                final int indexOfHistory = historyList.getItems().indexOf(article);
                if (indexOfHistory != -1){
                    historyList.getSelectionModel().select(indexOfHistory);
                    historyList.scrollTo(indexOfHistory - FOCUS_MARGIN);
                }

                final int indexOfBookmark = bookmarkList.getItems().indexOf(article);
                if (indexOfBookmark != -1){
                    bookmarkList.getSelectionModel().select(indexOfBookmark);
                    bookmarkList.scrollTo(indexOfBookmark - FOCUS_MARGIN);
                }
            })
        );
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
            .setTitle("Clear History")
            .setMessage("Does it delete all histories?")
            .setOnPositive("OK", () -> {
                historyList.getItems().clear();
                FILE_WATCHER.clear();
            })
            .build().show();
    }

    /**
     * Make new Markdown file.
     */
    private final void makeMarkdown() {
        final TextField input = new JFXTextField();
        final String newArticleMessage = "Please could you input new article's title?";
        input.setPromptText(newArticleMessage);
        new AlertDialog.Builder(getParent())
                .setTitle("Make new article")
                .setMessage(newArticleMessage)
                .addControl(input)
                .build().show();
        final String newFileName = input.getText();
        if (StringUtils.isEmpty(newFileName)){
            return;
        }
        openArticleTab(Articles.findFromTitle(newFileName));
    }

    /**
     * Save current tab's content.
     */
    private void saveCurrentTab() {
        final String message = getCurrentTab().saveContent();
        if (StringUtils.isNotBlank(message)) {
            setStatus(message);
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
        final Optional<Article> articleOr = Optional.ofNullable(getCurrentArticle());
        if (!articleOr.isPresent()) {
            setStatus("This tab can't rename.");
            return;
        }

        final Article article = articleOr.get();
        final String currentTitle = article.title;

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
                        Articles.titleToFileName(newTitle) + article.extention()
                        );
                if (dest.exists()){
                    AlertDialog.showMessage(parent, "変更失敗", "そのファイル名はすでに存在します。");
                }
                final File file = article.file;
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
                    article.replace(dest);
                    getCurrentTab().loadUrl(article.toInternalUrl());
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
        final Optional<Article> ofNullable = getCurrentArticleOfNullable();
        if (!ofNullable.isPresent()) {
            setStatus("This tab's content can't delete.");
            return;
        }
        final Article article = ofNullable.get();
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
                openSpeedDialTab();
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
    private final void callEditor() {

        final String edit = getCurrentTab().edit();

        if (StringUtils.isNotEmpty(edit)) {
            setStatus(edit);
        }
    }

    /**
     * Return current tab.
     * @return current tab.
     */
    private final ReloadableTab getCurrentTab() {
        final int currentIndex = tabPane.getSelectionModel().getSelectedIndex();
        if (currentIndex == -1) {
            return null;
        }
        final Tab tab = tabPane.getTabs().get(currentIndex);
        if (!(tab instanceof ReloadableTab)) {
            LOGGER.warn("Not instance Tab");
            return null;
        }
        return (ReloadableTab) tab;
    }

    /**
     *
     * @return
     */
    private final Optional<Article> getCurrentArticleOfNullable() {
        return Optional.ofNullable(getCurrentArticle());
    }

    /**
     * Get current tab's article.
     * @return If tab contains article, its article. else null.
     */
    private final Article getCurrentArticle() {
        final ReloadableTab tab = getCurrentTab();
        return tab instanceof ArticleTab ? ((ArticleTab) tab).getArticle() : null;
    }

    /**
     * Open urlText's value.
     */
    @FXML
    public final void readUrlText() {
        openWebTab("", urlText.getText());
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
                }

                LOGGER.info("print name:" + printer.getName());

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
     * Set stage to SideMenuController.
     */
    protected void setupSideMenu() {
        sideMenuController.setStage(this.stage);
        sideMenuController.setOnSearch(this::searchArticle);
        sideMenuController.setOnEdit(this::callEditor);
        sideMenuController.setOnNewTab(this::openSpeedDialTab);
        sideMenuController.setOnCloseTab(this::closeTab);
        sideMenuController.setOnCloseAllTabs(this::closeAllTabs);
        sideMenuController.setOnSlideShow(this::slideShow);
        sideMenuController.setOnReload(this::reload);
        sideMenuController.setOnPreviewSource(this::callHtmlSource);
        sideMenuController.setOnWordCloud(this::openContentTab);
        sideMenuController.setOnMakeArticle(this::makeMarkdown);
        sideMenuController.setOnCopy(this::callCopy);
        sideMenuController.setOnRename(this::callRename);
        sideMenuController.setOnDelete(this::callDelete);
        sideMenuController.setOnQuit(() -> {
            this.stage.close();
            System.exit(0);
        });
        sideMenuController.setOnOpenScriptRunner(this::openContentTab);
        sideMenuController.setOnOpenTools(this::switchRightDrawer);
        sideMenuController.setOnPopup(this::setStatus);
        sideMenuController.setCurrentArticleGetter(this::getCurrentArticleOfNullable);
        sideMenuController.setOpenTabWithHtmlContent(this::openWebTabWithContent);
        sideMenuController.setOnSaveArticle(this::saveCurrentTab);
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
                    Optional.ofNullable(getCurrentTab())
                            .ifPresent(tab -> emitter.next(tab.zoomProperty()));
                })
        ));
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
     * get parent window.
     * @return parent window.
     */
    private Window getParent() {
        return stage.getScene().getWindow();
    }

}
