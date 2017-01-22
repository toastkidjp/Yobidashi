package jp.toastkid.yobidashi;

import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;

import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import jp.toastkid.article.Archiver;
import jp.toastkid.article.ArticleGenerator;
import jp.toastkid.article.EpubGenerator;
import jp.toastkid.article.models.ContentType;
import jp.toastkid.dialog.AlertDialog;
import jp.toastkid.dialog.ProgressDialog;
import jp.toastkid.jfx.common.control.MenuLabel;
import jp.toastkid.libs.archiver.ZipArchiver;
import jp.toastkid.libs.lambda.Filters;
import jp.toastkid.libs.utils.CalendarUtil;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.libs.utils.RuntimeUtil;
import jp.toastkid.libs.utils.Strings;
import jp.toastkid.rss.RssFeeder;
import jp.toastkid.yobidashi.dialog.ConfigDialog;
import jp.toastkid.yobidashi.message.ApplicationMessage;
import jp.toastkid.yobidashi.message.ArticleMessage;
import jp.toastkid.yobidashi.message.ContentTabMessage;
import jp.toastkid.yobidashi.message.Message;
import jp.toastkid.yobidashi.message.ShowSearchDialog;
import jp.toastkid.yobidashi.message.SnackbarMessage;
import jp.toastkid.yobidashi.message.TabMessage;
import jp.toastkid.yobidashi.message.ToolsDrawerMessage;
import jp.toastkid.yobidashi.message.WebTabMessage;
import jp.toastkid.yobidashi.models.ApplicationState;
import jp.toastkid.yobidashi.models.BookmarkManager;
import jp.toastkid.yobidashi.models.Config;
import jp.toastkid.yobidashi.models.Defines;
import reactor.core.publisher.TopicProcessor;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

/**
 * Side menu's controller.
 *
 * @author Toast kid
 */
public class SideMenuController implements Initializable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SideMenuController.class);

    /** /path/to/about_file. */
    private static final String PATH_ABOUT_APP = "README.md";

    /** /path/to/license */
    private static final String PATH_LICENSE   = "LICENSE";

    /** /path/to/log file. */
    private static final String PATH_APP_LOG     = Defines.LOG_DIR    + "/app.log";

    /** RSS取得対象のURLリスト. */
    private static final String PATH_RSS_TARGETS = Defines.USER_DIR + "/res/rss";

    /** Article Generator. */
    private ArticleGenerator articleGenerator;

    /** ePub Generator. */
    private EpubGenerator ePubGenerator;

    /** JVM Language Script Runner. */
    private jp.toastkid.script.Main scriptRunner;

    /** Name Generator. */
    private jp.toastkid.name.Main nameGenerator;

    /** for controlling window. */
    private Stage stage;

    /** menu tabs. */
    @FXML
    private TabPane menuTabs;

    /** Tool Drawer event processor. */
    private TopicProcessor<Message> messenger;

    private Config conf;

    /**
     * Call back up method.
     */
    @FXML
    private final void callBackUp() {
        final Window parent = getParent().get();
        new AlertDialog.Builder(parent)
            .setTitle("Backup")
            .setMessage("この処理には時間がかかります。")
            .setOnPositive("OK", () -> {
                new ProgressDialog.Builder()
                        .setScene(parent.getScene())
                        .setCommand(new Task<Integer>() {
                            private String sArchivePath;
                            private long end;
                            @Override
                            protected Integer call() throws Exception {
                                final long start = System.currentTimeMillis();
                                sArchivePath = conf.get(Config.Key.ARTICLE_DIR);
                                try {
                                    new ZipArchiver().doDirectory(sArchivePath);
                                    //new ZipArchiver().doDirectory(iArchivePath);
                                } catch (final IOException e) {
                                    LOGGER.error("Error", e);;
                                }
                                end = System.currentTimeMillis() - start;
                                return 100;
                            }
                            @Override
                            protected void succeeded() {
                                sArchivePath = sArchivePath.substring(0, sArchivePath.length() - 1)
                                        .concat(ZipArchiver.EXTENSION_ZIP);
                                final String message
                                    = String.format("バックアップを完了しました。：%s%s%d[ms]",
                                        sArchivePath, System.lineSeparator(), end);
                                AlertDialog.showMessage(parent, "バックアップ完了", message);
                            }
                        })
                        .build();
            }).build().show();
    }

    /**
     * Delete backup files.
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
     * Show file length.
     */
    @FXML
    protected final void callFileLength() {
        messenger.onNext(ArticleMessage.makeLength());
    }

    /**
     * Switch Full screen mode.
     */
    @FXML
    private final void fullScreen() {
        if (stage == null) {
            return;
        }
        stage.setFullScreen(!stage.fullScreenProperty().get());
    }

    /**
     * Call configuration dialog.
     * @param event ActionEvent
     */
    @FXML
    private final void callConfig(final ActionEvent event) {
        final Optional<Window> parent = getParent();
        if (!parent.isPresent()) {
            return;
        }

        new ConfigDialog(parent.get()).showConfigDialog();
        conf.reload();
        messenger.onNext(TabMessage.makeReload());
    }

    /**
     * Show current application state.
     */
    @FXML
    private final void callApplicationState() {
        final Map<String, String> map = ApplicationState.getConfigMap();
        final StringBuilder bld = new StringBuilder();
        final String lineSeparator = System.lineSeparator();
        map.forEach((key, value) ->
            bld.append(key).append("\t").append(value).append(lineSeparator)
        );
        showMessageDialog("Application State", bld.toString());
    }

    /**
     * Call method of converting to Aozora-Bunko style text.
     */
    @FXML
    public final void callConvertAobun() {
        messenger.onNext(ArticleMessage.makeConvertAobun());
    }

    /**
     * Call word cloud.
     */
    @FXML
    private final void callWordCloud() {
        messenger.onNext(ArticleMessage.makeWordCloud());
    }

    /**
     * Open RSS Feeder．
     */
    @FXML
    private final void callRssFeeder() {
        if (!Files.exists(Paths.get(PATH_RSS_TARGETS))) {
            messenger.onNext(SnackbarMessage.make("Can't read RSS targets."));
            return;
        }
        final long start = System.currentTimeMillis();
        final RssFeeder feeder = new RssFeeder();
        final String rss = feeder.run(Paths.get(PATH_RSS_TARGETS));
        final String content = articleGenerator.decorate("RSS Feeder", rss, null);
        if (StringUtils.isEmpty(content)) {
            messenger.onNext(SnackbarMessage.make("Can't fetch RSS content."));
            return;
        }
        messenger.onNext(WebTabMessage.make("RSS Feeder", content, ContentType.HTML));
        messenger.onNext(SnackbarMessage.make("Done：" + (System.currentTimeMillis() - start) + "[ms]"));
    }

    /**
     * Call BookmarkManager.
     */
    @FXML
    private final void editBookmark() {
        new BookmarkManager().edit(stage);
    }

    /**
     * Call About.
     */
    @FXML
    private final void about() {
        openStaticFileWithConverting(PATH_ABOUT_APP, "About");
    }

    /**
     * Call License.
     */
    @FXML
    private final void license() {
        messenger.onNext(
            WebTabMessage.make(
                "License",
                FileUtil.readLines(PATH_LICENSE, "UTF-8").makeString(Strings.LINE_SEPARATOR),
                ContentType.TEXT
                )
            );
    }

    /**
     * Open static file with convert to html.
     * @param pathToFile
     * @param title
     */
    private void openStaticFileWithConverting(final String pathToFile, final String title) {
        final Path path = Paths.get(pathToFile);
        if (!Files.exists(path)) {
            LOGGER.warn(path.toAbsolutePath().toString() + " is not exists.");
            return;
        }
        messenger.onNext(
                WebTabMessage.make(title, articleGenerator.decorate(title, path), ContentType.HTML)
            );
    }

    /**
     * Get parent window. implement for lazy initialization.
     * @return Optional&lt;Window&gt;.
     */
    private Optional<Window> getParent() {
        return Optional.of(
                Optional.of(stage.getScene()).map(scene -> scene.getWindow()).orElse(null)
                );
    }

    /**
     * only call child method.
     */
    @FXML
    private void openScripter() {
        if (scriptRunner == null) {
            scriptRunner = new jp.toastkid.script.Main();
        }
        messenger.onNext(ContentTabMessage.make("Script Runner", scriptRunner.getRoot()));
    }

    /**
     * only call child method.
     */
    @FXML
    private void openNameGenerator() {
        if (nameGenerator == null) {
            nameGenerator = new jp.toastkid.name.Main();
        }
        nameGenerator.show(stage);
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
     * call capture.
     * @param filename
     */
    @FXML
    private void callCapture() {
        FileUtil.capture(Long.toString(System.nanoTime()), getCurrentRectangle());
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
                (int) stage.getHeight()
                );
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
     * Call article search.
     */
    @FXML
    private final void callSearch() {
        messenger.onNext(ShowSearchDialog.make());
    }

    /**
     * Show slide show.
     */
    @FXML
    private final void slideShow() {
        messenger.onNext(ArticleMessage.makeSlideShow());
    }

    /**
     * Call editor.
     */
    @FXML
    private final void callEditor() {
        messenger.onNext(TabMessage.makeEdit());
    }

    /**
     * Open new tab.
     */
    @FXML
    private final void openSpeedDialTab() {
        messenger.onNext(TabMessage.makeOpen());
    }

    /**
     * Close current tab.
     */
    @FXML
    private final void closeTab() {
        messenger.onNext(TabMessage.makeClose());
    }

    /**
     * Close all tabs.
     */
    @FXML
    private final void closeAllTabs() {
        messenger.onNext(TabMessage.makeCloseAll());
    }

    /**
     * Reload current tab.
     */
    @FXML
    private final void reload() {
        messenger.onNext(TabMessage.makeReload());
    }

    /**
     * Preview HTML source.
     */
    @FXML
    private final void callHtmlSource() {
        messenger.onNext(TabMessage.makePreview());
    }

    /**
     * Show log viewer.
     */
    @FXML
    private void callLogViewer() {
        final Path path = Paths.get(PATH_APP_LOG);
        if (!Files.exists(path)) {
            LOGGER.warn(path.toAbsolutePath().toString() + " is not exists.");
            return;
        }

        try {
            final String content = Files.readAllLines(path)
                                        .stream()
                                        .collect(Collectors.joining(Strings.LINE_SEPARATOR))
                                        .replace("$", "\\$");
            final String log = String.format("<pre>%s</pre>", content);
            final String title = "LogViewer";
            messenger.onNext(
                    WebTabMessage.make(
                            title,
                            articleGenerator.decorate(title, log, ""),
                            ContentType.HTML
                            )
                    );
        } catch (final IOException e) {
            LOGGER.error("ERROR!", e);
        }
    }

    /**
     * call simple backup.
     */
    @FXML
    private final void callSimpleBachup() {
        final DatePicker datePicker = new JFXDatePicker();
        datePicker.show();
        datePicker.setShowWeekNumbers(true);
        getParent().ifPresent(parent -> new AlertDialog.Builder(parent)
            .addControl(datePicker)
            .setTitle("Select date")
            .setMessage("バックアップする最初の日を選択してください。")
            .setOnPositive("Backup", () -> {
                final LocalDate value = datePicker.getValue();
                if (value == null) {
                    return;
                }
                final long epochDay = CalendarUtil.zoneDateTime2long(
                        value.atStartOfDay().atZone(ZoneId.systemDefault()));
                new Archiver().simpleBackup(conf.get(Config.Key.ARTICLE_DIR), epochDay);
            }).build().show()
        );
    }

    /**
     * Jsonの設定値を基に ePub を生成するメソッドを呼び出す.
     */
    @FXML
    private final void callGenerateEpubs() {
        getParent().ifPresent(parent -> new AlertDialog.Builder(parent)
                .setTitle("ePub").setMessage("OK を押すと ePub を生成します。")
                .setOnPositive("OK", () -> {
                    final ProgressDialog pd = new ProgressDialog.Builder()
                            .setScene(parent.getScene())
                            .setCommand(new Task<Integer>() {
                                @Override
                                protected Integer call() throws Exception {
                                    ePubGenerator.runEpubGenerator();
                                    return 100;
                                }
                            })
                            .build();
                    pd.start(stage);
                }).build().show());
    }

    /**
     * Call method of converting to ePub.
     */
    @FXML
    public final void callConvertEpub() {
        messenger.onNext(ArticleMessage.makeConvertEpub());
    }

    /**
     * Quit this app.
     */
    @FXML
    private void quit() {
        messenger.onNext(ApplicationMessage.makeQuit());
    }

    /**
     * Make new article.
     */
    @FXML
    private final void saveArticle() {
        messenger.onNext(TabMessage.makeSave());
    }

    /**
     * Make new article.
     */
    @FXML
    private final void makeArticle() {
        messenger.onNext(ArticleMessage.makeNew());
    }

    /**
     * Copy article.
     */
    @FXML
    private void callCopy() {
        messenger.onNext(ArticleMessage.makeCopy());
    }

    /**
     * rename article.
     */
    @FXML
    private void callRename() {
        messenger.onNext(ArticleMessage.makeRename());
    }

    /**
     * Delete article.
     */
    @FXML
    private void callDelete() {
        messenger.onNext(ArticleMessage.makeDelete());
    }

    /**
     * Open right Drawer.
     */
    @FXML
    private void openTools() {
        getProcessor().onNext(ToolsDrawerMessage.make());
    }

    /**
     * Open current folder.
     */
    @FXML
    private void openCurrentFolder() {
        openFolder(Defines.findInstallDir());
    }

    /**
     * Open current folder.
     */
    @FXML
    private void openArticleFolder() {
        openFolder(conf.get(Config.Key.ARTICLE_DIR));
    }

    /**
     * Open current folder.
     */
    @FXML
    private void openImageFolder() {
        openFolder(conf.get(Config.Key.IMAGE_DIR));
    }

    /**
     * Open specified folder.
     * @param dir
     */
    private void openFolder(final String dir) {
        final String openPath = dir.startsWith(FileUtil.FILE_PROTOCOL)
                ? dir
                : FileUtil.FILE_PROTOCOL + dir;
        RuntimeUtil.callExplorer(openPath);
    }

    /**
     * Open external file.
     */
    @FXML
    private void openExternalFile() {
        final FileChooser fc = new FileChooser();
        fc.setInitialDirectory(Paths.get(".").toFile());
        final Path result = fc.showOpenDialog(stage.getScene().getWindow()).toPath();
        if (!Files.exists(result) || !Files.isReadable(result)) {
            return;
        }

        final StringBuilder content = new StringBuilder();
        FileUtil.findExtension(result).ifPresent(ext -> {
            switch (ext) {
                case ".txt":
                    messenger.onNext(
                            WebTabMessage.make(
                                    result.toAbsolutePath().toString(),
                                    FileUtil.readLines(result, "UTF-8").makeString(Strings.LINE_SEPARATOR),
                                    ContentType.TEXT
                                )
                            );
                    break;
                case ".md":
                    content.append(articleGenerator.convertToHtml(result));
                    if (content.length() == 0) {
                        return;
                    }
                    messenger.onNext(
                            WebTabMessage.make(
                                    result.toAbsolutePath().toString(),
                                    content.toString(),
                                    ContentType.HTML
                                )
                            );
                    break;
            }
        });
    }

    /**
     * Call Garbage Collection.
     */
    @FXML
    private void callGC() {
        System.gc();
        messenger.onNext(SnackbarMessage.make("Called garbage collection."));
    }

    /**
     * for use shortcut when start-up.
     */
    private void putAccerelator() {
        final ObservableMap<KeyCombination, Runnable> accelerators
            = this.stage.getScene().getAccelerators();
        menuTabs.getTabs().forEach(tab -> {
            @SuppressWarnings("unchecked")
            final JFXListView<Node> items = (JFXListView<Node>) tab.getContent();
            items.getItems().stream()
                 .flatMap(this::flattenListToLabel)
                 .map(this::extractAcceleratorPair)
                 .filter(Filters::isNotNull)
                 .forEach(item -> accelerators.put(
                         item.getT1(), () -> item.getT2().handle(new ActionEvent())));
        });
    }

    /**
     * Flatten list to Label.
     * @param item Node
     * @return new Node's Stream.
     */
    @SuppressWarnings("unchecked")
    private Stream<Node> flattenListToLabel(final Node item) {
        if (item instanceof ListView) {
            return ((ListView<Node>) item).getItems().stream();
        }
        return Stream.of(item);
    }

    /**
     * Extract accelerator and action from node.
     * @param node
     * @return accelerator and action
     */
    private Tuple2<KeyCombination, EventHandler<ActionEvent>> extractAcceleratorPair(final Node node) {
        if (!(node instanceof MenuLabel)) {
            return null;
        }
        final MenuLabel label = (MenuLabel) node;
        if (label.getAccelerator() == null) {
            return null;
        }
        return Tuples.of(label.getAccelerator(), label.getOnAction());
    }

    /**
     * Set stage for controlling window.
     * @param stage
     */
    protected void setStage(final Stage stage) {
        this.stage  = stage;
        putAccerelator();
    }

    /**
     * Show simple message dialog.
     * @param title title
     * @param message message
     */
    private void showMessageDialog(final String title, final String message) {
        getParent().ifPresent(parent -> AlertDialog.showMessage(parent, title, message));
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        this.messenger = TopicProcessor.create();
    }

    /**
     * Getter of messenger.
     * @return messenger
     */
    public TopicProcessor<Message> getProcessor() {
        return messenger;
    }

    /**
     * Pass {@link Config} object.
     * @param conf
     */
    public void setConfig(final Config conf) {
        this.articleGenerator = new ArticleGenerator(conf);
        this.ePubGenerator    = new EpubGenerator(conf);
    }

}
