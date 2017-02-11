package jp.toastkid.yobidashi;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.factory.Maps;
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
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;
import jp.toastkid.article.ArticleGenerator;
import jp.toastkid.article.EpubGenerator;
import jp.toastkid.article.control.ArticleListCell;
import jp.toastkid.article.control.BaseWebTab;
import jp.toastkid.article.control.ContentTab;
import jp.toastkid.article.control.ReloadableTab;
import jp.toastkid.article.control.WebTab;
import jp.toastkid.article.control.editor.ArticleTab;
import jp.toastkid.article.control.editor.Editable;
import jp.toastkid.article.control.editor.EditorTab;
import jp.toastkid.article.models.Article;
import jp.toastkid.article.models.Articles;
import jp.toastkid.article.models.ContentType;
import jp.toastkid.article.search.ArticleSearcher;
import jp.toastkid.dialog.AlertDialog;
import jp.toastkid.dialog.ProgressDialog;
import jp.toastkid.jfx.common.FontFactory;
import jp.toastkid.jfx.common.Style;
import jp.toastkid.jfx.common.control.AutoCompleteTextField;
import jp.toastkid.jfx.common.transition.SplitterTransitionFactory;
import jp.toastkid.jobs.FileWatcherJob;
import jp.toastkid.libs.WebServiceHelper;
import jp.toastkid.libs.utils.AobunUtils;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.libs.utils.RuntimeUtil;
import jp.toastkid.libs.utils.Strings;
import jp.toastkid.wordcloud.FxWordCloud;
import jp.toastkid.wordcloud.JFXMasonryPane2;
import jp.toastkid.yobidashi.message.ApplicationMessage;
import jp.toastkid.yobidashi.message.ArticleMessage;
import jp.toastkid.yobidashi.message.ArticleSearchMessage;
import jp.toastkid.yobidashi.message.ContentTabMessage;
import jp.toastkid.yobidashi.message.EditorTabMessage;
import jp.toastkid.yobidashi.message.FontMessage;
import jp.toastkid.yobidashi.message.Message;
import jp.toastkid.yobidashi.message.ShowSearchDialog;
import jp.toastkid.yobidashi.message.SnackbarMessage;
import jp.toastkid.yobidashi.message.TabMessage;
import jp.toastkid.yobidashi.message.ToolsDrawerMessage;
import jp.toastkid.yobidashi.message.WebSearchMessage;
import jp.toastkid.yobidashi.message.WebTabMessage;
import jp.toastkid.yobidashi.models.BookmarkManager;
import jp.toastkid.yobidashi.models.Config;
import jp.toastkid.yobidashi.models.Config.Key;
import jp.toastkid.yobidashi.models.Defines;
import reactor.core.Cancellation;
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

    /** default divider's position. */
    private static final double DEFAULT_DIVIDER_POSITION = 0.2;

    /** Speed Dial's title. */
    private static final String TITLE_SPEED_DIAL = "Speed Dial";

    /** WebView's highliting. */
    private static final String WINDOW_FIND_DOWN
        = "window.find(\"{0}\", false, false, true, false, true, false)";

    /** WebView's highliting. */
    private static final String WINDOW_FIND_UP
        = "window.find(\"{0}\", false, true, true, false, true, false)";

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

    /** Splitter of TabPane. */
    @FXML
    private SplitPane splitter;

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

    /** use for draw word-cloud. */
    private FxWordCloud wordCloud;

    /** Side Menu pane controller. */
    @FXML
    private SideMenuController sideMenuController;

    /** Tools pane controller. */
    @FXML
    private ToolsController toolsController;

    /** Config. */
    private Config conf;

    /** ePub generator. */
    private EpubGenerator ePubGenerator;

    @Override
    public final void initialize(final URL url, final ResourceBundle bundle) {

        // initialize parallel task.
        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        final ExecutorService es = Executors.newFixedThreadPool(
                availableProcessors + availableProcessors + availableProcessors);

        snackbar.registerSnackbarContainer(root);

        initDragAndDrop();

        conf = new Config(Defines.CONFIG);

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
                            articleGenerator = new ArticleGenerator(conf);
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
                        es.execute(Controller.this::initTabPane);

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
     * Initialize Drag and Drop Events.
     */
    private void initDragAndDrop() {
        root.setOnDragOver(event -> {
            final Dragboard board = event.getDragboard();
            if (!board.hasFiles()) {
                return;
            }
            event.acceptTransferModes(TransferMode.COPY);
        });
        root.setOnDragDropped(event -> {
            final Dragboard board = event.getDragboard();
            if (!board.hasFiles()) {
                event.setDropCompleted(false);
                return;
            }
            board.getFiles().stream().forEach(f -> {
                if (Article.Extension.MD.text().equals(FileUtil.findExtension(f.toPath()).get())) {
                    processEditorTabMessage(EditorTabMessage.make(Paths.get(f.getAbsolutePath())));
                    return;
                }
                openWebTabWithContent(
                        f.getAbsolutePath(),
                        FileUtil.readLines(f.toPath(), Article.ENCODE).makeString(System.lineSeparator()),
                        ContentType.TEXT
                    );
            });
            event.setDropCompleted(true);
        });
    }

    /**
     * Initialize tab pane.
     */
    private void initTabPane() {
        final long start = System.currentTimeMillis();
        tabPane.getSelectionModel().selectedItemProperty().addListener(
                (a, prevTab, nextTab) -> {
                    // (121224) タブ切り替え時の URL 表示の変更
                    final ReloadableTab tab = getCurrentTab();
                    if (tab == null) {
                        return;
                    }

                    setTitleOnToolbar(tab.getTitle());

                    final String tabUrl = tab.getUrl();
                    if (!StringUtils.isEmpty(tabUrl)){
                        urlText.setText(tabUrl);
                        focusOn();
                        return;
                    }

                    articleList.getSelectionModel().clearSelection();
                    historyList.getSelectionModel().clearSelection();
                    bookmarkList.getSelectionModel().clearSelection();
                }
            );
        final String message = Thread.currentThread().getName()
                + " Ended initialize right tabs. "
                + (System.currentTimeMillis() - start) + "ms";
        //TODO modify with rx : setProgress(message);
        LOGGER.info(message);
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
                ? conf.get(Config.Key.APP_TITLE)
                : titleStr + " - " + conf.get(Config.Key.APP_TITLE);
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
        final String stylesheet = conf.get(Config.Key.STYLESHEET);
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
    private final void callCalendar() {
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
    private final void showHtmlSource() {
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
    private final void reload() {
        getCurrentTab().reload();
    }

    /**
     * Open new tab having SpeedDial.
     */
    private void openSpeedDialTab() {
        try {
            final jp.toastkid.speed_dial.Controller controller = readSpeedDial();
            controller.setTitle(conf.get(Config.Key.APP_TITLE));
            controller.setZero();
            controller.setBackground(articleGenerator.getBackground());
            final Cancellation cancellation2 = controller.messenger().subscribe(this::processMessage);
            Runtime.getRuntime().addShutdownHook(new Thread(cancellation2::dispose));

            final Pane sdRoot = controller.getRoot();
            sdRoot.setPrefWidth(width * 0.8);
            sdRoot.setPrefHeight(tabPane.getHeight());
            openTab(makeContentTab(TITLE_SPEED_DIAL, sdRoot));
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
                getClass().getClassLoader().getResource(jp.toastkid.speed_dial.Controller.FXML));
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
        final ArticleTab articleTab = makeArticleTab(article);
        articleTab.setFont(readFont());
        openTab(articleTab);
    }

    /**
     * Make article tab.
     * @param article
     * @return ArticleTab
     */
    private ArticleTab makeArticleTab(final Article article) {
        return new ArticleTab.Builder()
                .setArticle(article)
                .setConfig(conf)
                .setOnClose(this::closeTab)
                .setOnContextMenuRequested(event -> showContextMenu())
                .setOnOpenNewArticle(this::openArticleTab)
                .setPopupHandler(param -> openWebTab("", "").getWebView().getEngine())
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
     * Open specified tab with title.
     * @param title tab's title
     * @param content tab's content(Pane)
     */
    private void openContentTab(final String title, final Pane content) {
        openTab(makeContentTab(title, content));
    }

    /**
     * Open new web tab.
     * @param title
     * @param url
     */
    private WebTab openWebTab(final String title, final String url) {
        final WebTab webTab = new WebTab.Builder()
                .setTitle(title)
                .setUrl(url)
                .setOnClose(this::closeTab)
                .build();
        openTab(webTab);
        return webTab;
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
     * Open new editor tab.
     * @param path
     */
    private void openEditorTab(final Path path) {
        final EditorTab tab = new EditorTab.Builder()
                .setPath(path)
                .setConfig(conf)
                .setOnClose(this::closeTab)
                .build();
        tab.setFont(readFont());
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
            final ObservableList<Node> itemContainer = cmc.getItemsContainer().getChildren();
            itemContainer.addAll(
                    makeContextMenuItemContainerWithAction(
                            cmc, "文字数計測", event -> processArticleMessage(ArticleMessage.makeLength())),
                    makeContextMenuItemContainerWithAction(
                            cmc, "Full Screen", event -> stage.setFullScreen(true)),
                    makeContextMenuItemContainerWithAction(
                            cmc, "Slide show", event -> slideShow()),
                    makeContextMenuItemContainerWithAction(
                            cmc, "Open new tab", event -> openSpeedDialTab()),
                    makeContextMenuItemContainerWithAction(
                            cmc, "Close current tab", event -> closeCurrentTab()),
                    makeContextMenuItemContainerWithAction(
                            cmc, "Close all tabs", event -> closeAllTabs()),
                    makeContextMenuItemContainerWithAction(
                            cmc, "Show HTML source", event -> showHtmlSource()),
                    makeContextMenuItemContainerWithAction(
                            cmc, "Find in page", event -> openSearcher()),
                    makeContextMenuItemContainerWithAction(
                            cmc, "Go to top of this page", event -> moveToTop()),
                    makeContextMenuItemContainerWithAction(
                            cmc, "Go to bottom of this page", event -> moveToBottom()),
                    makeContextMenuItemContainerWithAction(
                            cmc, "Search article", event -> showSearchDialog("", "")),
                    makeContextMenuItemContainerWithAction(
                            cmc,
                            isRightDrawerClosing() ? "Open tools" : "Close tools",
                            event -> switchRightDrawer()
                            ),
                    makeContextMenuItemContainerWithAction(
                            cmc, "Save article", event -> saveCurrentTab()),
                    cmc.new MenuItemContainer(
                            isHideLeftPane()
                            ? makeMenuItemWithAction("記事一覧を開く",   event -> showLeftPane())
                            : makeMenuItemWithAction("記事一覧を閉じる", event -> hideLeftPane())
                            )
                    );
            editableOr().ifPresent(editor -> itemContainer.add(cmc.new MenuItemContainer(
                    editor.isEditing()
                    ? makeMenuItemWithAction("Close editor",   event -> edit())
                    : makeMenuItemWithAction("Edit", event -> edit())
                    )
                ));

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
        SplitterTransitionFactory
            .makeHorizontalSlide(splitter, DEFAULT_DIVIDER_POSITION, DEFAULT_DIVIDER_POSITION).play();
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
     * Close current tab.
     */
    private final void closeCurrentTab() {
        Optional.ofNullable(getCurrentTab()).ifPresent(tab -> tab.close(this::closeTab));
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
     * Show search order dialog.
     * 再帰的に呼び出すためメソッドに切り出し.-
     * @param q クエリ
-    * @param f 記事名フィルタ文字列
     */
    private void showSearchDialog(final String q, final String f) {
        queryInput.setText(q);
        filterInput.setText(f);
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

                searchArticle(isAnd.isSelected(), query, filter);
            }).build().showAndWait();
    }

    /**
     * Do search article.
     * @param isAnd
     * @param query
     * @param filter
     */
    private void searchArticle(final boolean isAnd, final String query, final String filter) {
        final ArticleSearcher fileSearcher = new ArticleSearcher.Builder()
                .setHomeDirPath(conf.get(Key.ARTICLE_DIR))
                .setAnd(isAnd)
                .setSelectName(filter)
                .setEmptyAction(() -> {
                    showSnackbar(String.format("Not found article with '%s'.", query));
                    showSearchDialog(queryInput.getText(), filterInput.getText());
                })
                .setSuccessAction(this::setStatus)
                .setTabPane(leftTabs)
                .setListViewInitializer(this::initArticleList)
                .setHeight(articleList.getHeight())
                .build();
        fileSearcher.search(query);
    }

    /**
     * Show slide show.
     */
    private void slideShow() {
        final Optional<Article> articleOr = Optional.ofNullable(getCurrentArticle());
        if (!articleOr.isPresent()) {
            setStatus("This tab can't use slide show.");
            return;
        }
        new jp.toastkid.slideshow.Main().show(this.stage, articleOr.get().path.toAbsolutePath().toString());
    }

    /**
     * Open home.
     */
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
                tabPane.getSelectionModel().select(first.get());
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
        Flux.fromIterable(Articles.readAllArticleNames(conf.get(Key.ARTICLE_DIR)))
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
            .subscribe(
                empty -> new BookmarkManager(Defines.PATH_TO_BOOKMARK).readLines()
                    .collect(Articles::findByTitle)
                    .each(bookmarks::add)
                    );
    }

    /**
     * 現在選択中のファイルに ListView をフォーカスする.
     */
    private void focusOn() {
        final Optional<Article> articleOr = Optional.ofNullable(getCurrentArticle());
        if (!articleOr.isPresent()) {
            return;
        }

        articleOr.ifPresent(article -> Platform.runLater(() -> {
            article.focus(articleList);
            article.focus(historyList);
            article.focus(bookmarkList);
        }));

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
        FILE_WATCHER.add(article.path);
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
            .build().showAndWait();
    }

    /**
     * Make new Markdown file.
     */
    private final void makeMarkdown() {
        final TextField input = new JFXTextField();
        final String newArticleMessage = "Please could you input new article's title?";
        input.setPromptText("New article name");
        new AlertDialog.Builder(getParent())
                .setTitle("Make new article")
                .setMessage(newArticleMessage)
                .addControl(input)
                .build().showAndWait();
        final String newFileName = input.getText();
        if (StringUtils.isEmpty(newFileName)){
            return;
        }
        openArticleTab(Articles.findByTitle(newFileName));
    }

    /**
     * Call copy article。
     * <HR>
     * (130707) メッセージ変更<BR>
     * (130512) 作成<BR>
     */
    @FXML
    private final void copyArticle() {
        callRenameArticle(true);
    }

    /**
     * Call rename article。
     * <HR>
     * (130707) メッセージ変更<BR>
     * (130512) 作成<BR>
     */
    @FXML
    private final void renameArticle() {
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

        final TextField input = new JFXTextField(prefix.concat(currentTitle));

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
                final Path dest = Paths.get(
                        conf.get(Config.Key.ARTICLE_DIR),
                        Articles.titleToFileName(newTitle) + article.extention()
                        );
                if (Files.exists(dest)){
                    AlertDialog.showMessage(parent, "変更失敗", "そのファイル名はすでに存在します。");
                }
                final Path path = article.path;
                boolean success = false;
                try {
                    success = isCopy
                            ? Files.copy(path, dest) != null
                            : Files.move(path, dest) != null;
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
                    removeHistory(new Article(path));
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
            }).build().showAndWait();;
    }

    /**
     * 現在選択している記事を削除する。
     * <HR>
     * (130317) ファイルが存在しない場合はダイアログを出して戻る<BR>
     * (130309) 1つファイルを消すたびにファイル一覧を読み直すのは非効率的なので、
     * ファイル一覧から削除するよう処理変更<BR>
     * (130305) 作成<BR>
     */
    private final void deleteArticle() {
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
        if (!Files.exists(article.path)){
            AlertDialog.showMessage(parent,
                    "ファイルがありません", deleteTarget + " というファイルは存在しません。");
            return;
        }
        new AlertDialog.Builder(parent)
            .setTitle("Delete file")
            .setMessage(deleteTarget + " を削除しますか？")
            .setOnPositive("OK", () -> {
                try {
                    FILE_WATCHER.remove(article.path);
                    Files.delete(article.path);
                    AlertDialog.showMessage(parent, "削除完了", deleteTarget + " を削除しました。");
                    closeCurrentTab();
                    final Optional<Tab> first = tabPane.getTabs().stream()
                            .filter(tab -> TITLE_SPEED_DIAL.equals(tab.getText()))
                            .findFirst();
                    if (first.isPresent()) {
                        tabPane.getSelectionModel().select(first.get());
                    } else {
                        openSpeedDialTab();
                    }
                    removeHistory(article);
                } catch (final Exception e) {
                    e.printStackTrace();
                    AlertDialog.showMessage(parent, "Failed!", deleteTarget + " を削除できませんでした。");
                }
            }).build().showAndWait();
    }

    /**
     * 履歴リストから指定した記事名を削除する.
     * @param deleteTarget 削除する Article
     */
    private final void removeHistory(final Article deleteTarget) {
        removeItem(deleteTarget, articleList);
        removeItem(deleteTarget, historyList);
        removeItem(deleteTarget, bookmarkList);
    }

    /**
     * Remove Item from passed ListView.
     * @param deleteTarget
     * @param listView
     */
    private final void removeItem(final Article deleteTarget, final ListView<Article> listView) {
        final ObservableList<Article> items = listView.getItems();
        if (items.indexOf(deleteTarget) != -1) {
            items.remove(deleteTarget);
        }
    }

    /**
     * Save current tab's content.
     */
    private void saveCurrentTab() {
        editableOr().map(Editable::saveContent).ifPresent(this::showSnackbar);
    }

    /**
     * Call editor.
     */
    private final void edit() {
        editableOr().ifPresent(Editable::edit);
    }

    /**
     * Return Optional&lt;Editable&gt;.
     * @return Optional&lt;Editable&gt;.
     */
    private final Optional<Editable> editableOr() {
        final ReloadableTab tab = getCurrentTab();
        return Optional.ofNullable(tab).filter(t -> t instanceof Editable).map(Editable.class::cast);
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
    private final void readUrlText() {
        openWebTab("", urlText.getText());
    }

    /**
     * Call PrinterJob.
     *
     * @see <a href="http://d.hatena.ne.jp/tatsu-no-toshigo/20141208/1417957734">
     * 続・JavaFX8の印刷機能によるHTMLのPDF変換</a>
     */
    @FXML
    private final void callPrinterJob() {
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
                job.getJobSettings().setJobName(conf.get(Config.Key.APP_TITLE));
                LOGGER.info("jobName [{}]\n", job.getJobSettings().getJobName());
                tab.print(job);
                job.endJob();
                AlertDialog.showMessage(getParent(), "Complete print to PDF", "PDF印刷が正常に完了しました。");
            }).build().showAndWait();
    }

    /**
     * apply stylesheet.
     */
    @FXML
    private void callApplyStyle() {
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
        conf.store(Config.Key.STYLESHEET, styleName);
    }

    /**
     * Set passed stage on this object.
     * @param stage
     */
    protected final void setStage(final Stage stage) {
        this.stage = stage;
        stage.setTitle(conf.get(Config.Key.APP_TITLE));
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
    protected final void setSize(final double width, final double height) {
        this.width  = width;
        this.height = height;
    }

    /**
     * Set stage to SideMenuController.
     */
    protected void setupSideMenu() {
        sideMenuController.setStage(this.stage);
        final Cancellation cancellation
            = sideMenuController.getMessenger().subscribe(this::processMessage);
        Runtime.getRuntime().addShutdownHook(new Thread(cancellation::dispose));
        sideMenuController.setConfig(conf);
    }

    /**
     * Set up Tool Menu.
     */
    protected void setupToolMenu() {
        toolsController.init(this.stage);
        final Cancellation cancellation
            = toolsController.getMessenger().subscribe(this::processMessage);
        Runtime.getRuntime().addShutdownHook(new Thread(cancellation::dispose));
        toolsController.setConfig(conf);
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
     * Process processor's event.
     * @param message Processor's event
     */
    private void processMessage(final Message message) {

        if (message instanceof ToolsDrawerMessage) {
            Platform.runLater(this::switchRightDrawer);
            return;
        }

        if (message instanceof ArticleMessage) {
            processArticleMessage((ArticleMessage) message);
            return;
        }

        if (message instanceof TabMessage) {
            processTabMessage((TabMessage) message);
            return;
        }

        if (message instanceof ContentTabMessage) {
            processContentTabMessage((ContentTabMessage) message);
            return;
        }

        if (message instanceof WebTabMessage) {
            processWebTabMessage((WebTabMessage) message);
            return;
        }

        if (message instanceof EditorTabMessage) {
            processEditorTabMessage((EditorTabMessage) message);
            return;
        }

        if (message instanceof SnackbarMessage) {
            setStatus(((SnackbarMessage) message).getText());
            return;
        }

        if (message instanceof ApplicationMessage) {
            processApplicationMessage((ApplicationMessage) message);
            return;
        }

        if (message instanceof ArticleSearchMessage) {
            final ArticleSearchMessage asm = (ArticleSearchMessage) message;
            Platform.runLater(() -> searchArticle(true, asm.query(), asm.filter()));
            return;
        }

        if (message instanceof WebSearchMessage) {
            final WebSearchMessage wsm = (WebSearchMessage) message;
            Platform.runLater(() ->
                openWebTab("Loading...", WebServiceHelper.buildRequestUrl(wsm.query(), wsm.type())));
            return;
        }

        if (message instanceof ShowSearchDialog) {
            Platform.runLater(() -> showSearchDialog("", ""));
            return;
        }

        if (message instanceof FontMessage) {
            final FontMessage fontEvent = (FontMessage) message;
            final int size = fontEvent.getSize();
            conf.store(Maps.mutable.of(
                    Key.FONT_SIZE.text(),   Integer.toString(size),
                    Key.FONT_FAMILY.text(), fontEvent.getFont().getFamily()
                    ));
            applyFontToEditor();
            return;
        }
    }

    /**
     * Apply current font to editor tab.
     */
    private void applyFontToEditor() {
        final Font font = readFont();
        tabPane.getTabs().stream()
            .filter(Editable.class::isInstance)
            .map(Editable.class::cast)
            .forEach(tab -> Platform.runLater(() -> tab.setFont(font)));
    }

    /**
     * Read font form config.
     * @return Font
     */
    private Font readFont() {
        return FontFactory.make(conf.get(Key.FONT_FAMILY), conf.getInt(Key.FONT_SIZE, 16));
    }

    /**
     * Process {@link EditorTabMessage}'s object.
     * @param message {@link EditorTabMessage}
     */
    private void processEditorTabMessage(final EditorTabMessage message) {
        Platform.runLater(() -> openEditorTab(message.getPath()));
    }

    /**
     * Open WebTabMessage with passed title and content.
     * @param message WebTabMessage
     */
    private void processWebTabMessage(final WebTabMessage message) {
        Platform.runLater(() -> openWebTabWithContent(
                message.getTitle(), message.getContent(), message.getContentType()));
    }

    /**
     * Open ContentTab with passed title and content.
     * @param message ContentTabMessage
     */
    private void processContentTabMessage(final ContentTabMessage message) {
        Platform.runLater(() -> openContentTab(message.getTitle(), message.getContent()));
    }

    /**
     * Process {@link ApplicationMessage}.
     * @param message
     */
    private void processApplicationMessage(ApplicationMessage message) {
        switch (message.getCommand()) {
            case QUIT:
                Platform.runLater(stage::close);
                System.exit(0);
                return;
        }
    }

    /**
     * Process tab's message.
     * @param message
     */
    private void processTabMessage(final TabMessage message) {
        switch (message.getCommand()) {
            case EDIT:
                Platform.runLater(this::edit);
                return;
            case CLOSE:
                Platform.runLater(this::closeCurrentTab);
                return;
            case CLOSE_ALL:
                Platform.runLater(this::closeAllTabs);
                return;
            case PREVIEW:
                Platform.runLater(this::showHtmlSource);
                return;
            case SAVE:
                Platform.runLater(this::saveCurrentTab);
                return;
            case RELOAD:
                Platform.runLater(this::reload);
                return;
            case OPEN:
                Platform.runLater(this::openSpeedDialTab);
                return;
            default:
                return;
        }
    }

    /**
     * Process article's message.
     * @param message {@link ArticleMessage}'s instance
     */
    private void processArticleMessage(final ArticleMessage message) {
        final Optional<Article> optional = getCurrentArticleOfNullable();
        switch (message.getCommand()) {
            case SLIDE_SHOW:
                Platform.runLater(this::slideShow);
                return;
            case MAKE:
                Platform.runLater(this::makeMarkdown);
                return;
            case LENGTH:
                if (!optional.isPresent()) {
                    snackbar.fireEvent(new SnackbarEvent("現在表示できません。"));
                    return;
                }
                Platform.runLater(() -> AlertDialog.showMessage(
                        getParent(), "文字数計測", optional.get().makeCharCountResult()));
                return;
            case COPY:
                Platform.runLater(this::copyArticle);
                return;
            case DELETE:
                Platform.runLater(this::deleteArticle);
                return;
            case RENAME:
                Platform.runLater(this::renameArticle);
                return;
            case CONVERT_AOBUN:
                if (!optional.isPresent()) {
                    setStatus("This tab's content can't convert Aozora bunko file.");
                    return;
                }
                final String absolutePath = optional.get().path.toAbsolutePath().toString();
                AobunUtils.docToTxt(optional.get().path, Paths.get(Defines.findInstallDir()));
                AlertDialog.showMessage(
                        getParent(),
                        "Complete Converting",
                        Strings.join("変換が完了しました。", System.lineSeparator(), absolutePath)
                );
                return;
            case CONVERT_EPUB:
                Platform.runLater(this::convertEpub);
                return;
            case WORD_CLOUD:
                final JFXMasonryPane2 pane = new JFXMasonryPane2();
                final ScrollPane value = new ScrollPane(pane);
                value.setFitToHeight(true);
                value.setFitToWidth(true);

                if (!optional.isPresent()) {
                    setStatus("This tab's content can't generate word cloud.");
                    return;
                }

                wordCloud = new FxWordCloud.Builder().setNumOfWords(200).setMaxFontSize(120.0)
                                .setMinFontSize(8.0).build();
                final Article article = optional.get();
                wordCloud.draw(pane, article.path.toString());
                Platform.runLater(() -> openContentTab(article.title + "'s word cloud", pane));
                return;
            default:
                return;
        }
    }

    /**
     * Convert current article to ePub.
     */
    private void convertEpub() {
        final RadioButton vertically   = new RadioButton("vertically");
        final RadioButton horizontally = new RadioButton("horizontally");

        new ToggleGroup() {{
            getToggles().addAll(vertically, horizontally);
            vertically.setSelected(true);
        }};

        if (ePubGenerator == null) {
            ePubGenerator = new EpubGenerator(conf);
        }

        final Window parent = getParent();
        new AlertDialog.Builder(parent)
            .setTitle("ePub").setMessage("Current Article convert to ePub.")
            .addControl(vertically, horizontally)
            .setOnPositive("OK", () -> {
                final ProgressDialog pd = new ProgressDialog.Builder()
                        .setScene(parent.getScene())
                        .setCommand(new Task<Integer>() {
                            @Override
                            protected Integer call() throws Exception {
                                final Optional<Article> optional = getCurrentArticleOfNullable();
                                optional.ifPresent(article ->
                                    ePubGenerator.toEpub(article, vertically.isSelected()));
                                return 100;
                            }
                        })
                        .build();
                pd.start(stage);
            }).build().showAndWait();;
    }

    /**
     * get parent window.
     * @return parent window.
     */
    private Window getParent() {
        return stage.getScene().getWindow();
    }

}
