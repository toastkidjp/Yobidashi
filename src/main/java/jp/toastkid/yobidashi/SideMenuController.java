package jp.toastkid.yobidashi;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.reactfx.util.TriConsumer;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import jp.toastkid.article.ApplicationState;
import jp.toastkid.article.Archiver;
import jp.toastkid.article.ArticleGenerator;
import jp.toastkid.article.EpubGenerator;
import jp.toastkid.article.models.Article;
import jp.toastkid.article.models.Config;
import jp.toastkid.article.models.ContentType;
import jp.toastkid.article.models.Defines;
import jp.toastkid.dialog.AlertDialog;
import jp.toastkid.dialog.ProgressDialog;
import jp.toastkid.jfx.common.control.MenuLabel;
import jp.toastkid.libs.archiver.ZipArchiver;
import jp.toastkid.libs.lambda.Filters;
import jp.toastkid.libs.utils.AobunUtils;
import jp.toastkid.libs.utils.CalendarUtil;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.libs.utils.RuntimeUtil;
import jp.toastkid.libs.utils.Strings;
import jp.toastkid.wordcloud.FxWordCloud;
import jp.toastkid.wordcloud.JFXMasonryPane2;
import jp.toastkid.yobidashi.dialog.ConfigDialog;
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

    /** Article Generator. */
    private ArticleGenerator articleGenerator;

    /** menu tabs. */
    @FXML
    private TabPane menuTabs;

    /** use for draw word-cloud. */
    private FxWordCloud wordCloud;

    /** for controlling window. */
    private Stage stage;

    /** search command. */
    private Runnable search;

    /** edit article command. */
    private Runnable edit;

    /** open tab command. */
    private Runnable open;

    /** close tab command. */
    private Runnable close;

    /** slide show command. */
    private Runnable slide;

    /** tab action/ */
    private OpenTabAction tabAction;

    /** Command of reload. */
    private Runnable reload;

    /** Command of preview. */
    private Runnable preview;

    /** Command of quit this app. */
    private Runnable onQuit;

    /** Action of make new article. */
    private Consumer<Article.Extension> onMakeArticle;

    /** Command of copy article. */
    private Runnable onCopy;

    /** Command of rename article. */
    private Runnable onRename;

    /** Command of delete article. */
    private Runnable onDelete;

    /** Action of launch Script runner tab. */
    private OpenTabAction scriptOpener;

    /** JVM Language Script Runner. */
    private jp.toastkid.script.Main scriptRunner;

    /** Name Generator. */
    private jp.toastkid.name.Main nameGenerator;

    /** Action of open external file. */
    private Consumer<String> openExternal;

    /** Action of open drawer. */
    private Runnable switchRightDrawer;

    /** Action of popup text. */
    private Consumer<String> onPopup;

    /** Article's getter. */
    private Supplier<Optional<Article>> articleGetter;

    /** Action open tab with html content. */
    private TriConsumer<String, String, ContentType> openTabWithHtmlContent;

    private Runnable closeAll;

    /**
     * Call back up method.
     */
    @FXML
    private final void callBackUp(final ActionEvent event) {
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
                                sArchivePath = Config.get(Config.Key.ARTICLE_DIR);
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
        final Optional<Article> optional = articleGetter.get();
        if (!optional.isPresent()) {
            showMessagePopup("現在表示できません。");
            return;
        }
        showMessageDialog("文字数計測", optional.get().makeCharCountResult());
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
        Config.reload();
        reload.run();
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
        final Optional<Article> optional = articleGetter.get();
        if (!optional.isPresent()) {
            showMessagePopup("This tab's content can't convert Aozora bunko file.");
            return;
        }
        final String absolutePath = optional.get().file.getAbsolutePath();
        AobunUtils.docToTxt(absolutePath);
        showMessageDialog(
                "Complete Converting",
                Strings.join("変換が完了しました。", System.lineSeparator(), absolutePath)
        );
    }

    /**
     * Call word cloud.
     */
    @FXML
    private final void callWordCloud() {
        final JFXMasonryPane2 pane = new JFXMasonryPane2();
        final ScrollPane value = new ScrollPane(pane);
        value.setFitToHeight(true);
        value.setFitToWidth(true);

        final Optional<Article> optional = articleGetter.get();
        if (!optional.isPresent()) {
            showMessagePopup("This tab's content can't generate word cloud.");
            return;
        }

        wordCloud = new FxWordCloud.Builder().setNumOfWords(200).setMaxFontSize(120.0)
                        .setMinFontSize(8.0).build();
        final Article article = optional.get();
        wordCloud.draw(pane, article.file);
        tabAction.open(article.title + "のワードクラウド", pane);
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
        openTabWithHtmlContent.accept(
                "License",
                FileUtil.readLines(PATH_LICENSE, "UTF-8").makeString(Strings.LINE_SEPARATOR),
                ContentType.TEXT
                );
    }

    /**
     * Open static file with convert to html.
     * @param pathToFile
     * @param title
     */
    private void openStaticFileWithConverting(final String pathToFile, final String title) {
        final File file = new File(pathToFile);
        if (!file.exists()) {
            LOGGER.warn(file.getAbsolutePath() + " is not exists.");
            return;
        }
        this.openTabWithHtmlContent.accept(
                title,
                articleGenerator.decorate(title, file),
                ContentType.HTML
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
        scriptOpener.open("Script Runner", scriptRunner.getRoot());
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
        search.run();
    }

    /**
     * Call editor.
     */
    @FXML
    private final void callEditor() {
        edit.run();
    }

    /**
     * Open new tab.
     */
    @FXML
    private final void openSpeedDialTab() {
        open.run();
    }

    /**
     * Close current tab.
     */
    @FXML
    private final void closeTab() {
        close.run();
    }

    /**
     * Close all tabs.
     */
    @FXML
    private final void closeAllTabs() {
        closeAll.run();
    }

    /**
     * Show slide show.
     */
    @FXML
    private final void slideShow() {
        slide.run();
    }

    /**
     * Reload current tab.
     */
    @FXML
    private final void reload() {
        reload.run();
    }

    /**
     * Preview HTML source.
     */
    @FXML
    private final void callHtmlSource() {
        preview.run();
    }

    /**
     * Show log viewer.
     */
    @FXML
    private void callLogViewer() {
        if (!new File(PATH_APP_LOG).exists()) {
            LOGGER.warn(new File(PATH_APP_LOG).getAbsolutePath() + " is not exists.");
            return;
        }
        final String log = String.format(
                "<pre>%s</pre>",
                FileUtil.getStrFromFile(PATH_APP_LOG, StandardCharsets.UTF_8.name())
                );

        final String title = "LogViewer";
        openTabWithHtmlContent.accept(
                title, articleGenerator.decorate(title, log, ""), ContentType.HTML);
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
                new Archiver().simpleBackup(Config.get(Config.Key.ARTICLE_DIR), epochDay);
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
                                    new EpubGenerator().runEpubGenerator();
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
        final RadioButton vertically   = new RadioButton("vertically");
        final RadioButton horizontally = new RadioButton("horizontally");

        new ToggleGroup() {{
            getToggles().addAll(vertically, horizontally);
            vertically.setSelected(true);
        }};

        getParent().ifPresent(parent -> new AlertDialog.Builder(parent)
                .setTitle("ePub").setMessage("Current Article convert to ePub.")
                .addControl(vertically, horizontally)
                .setOnPositive("OK", () -> {
                    final ProgressDialog pd = new ProgressDialog.Builder()
                            .setScene(parent.getScene())
                            .setCommand(new Task<Integer>() {
                                @Override
                                protected Integer call() throws Exception {
                                    articleGetter.get().ifPresent(article ->
                                        new EpubGenerator().toEpub(article, vertically.isSelected()));
                                    return 100;
                                }
                            })
                            .build();
                    pd.start(stage);
                }).build().show()
            );
    }

    /**
     * Quit this app.
     */
    @FXML
    private void quit() {
        onQuit.run();
    }

    /**
     * Copy article.
     */
    @FXML
    private void callCopy() {
        onCopy.run();
    }

    /**
     * Make new article.
     */
    @FXML
    private final void makeArticle() {
        onMakeArticle.accept(Article.Extension.MD);
    }

    /**
     * rename article.
     */
    @FXML
    private void callRename() {
        onRename.run();
    }

    /**
     * Delete article.
     */
    @FXML
    private void callDelete() {
        onDelete.run();
    }

    /**
     * Open right Drawer.
     */
    @FXML
    private void openTools() {
        switchRightDrawer.run();
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
        openFolder(Config.get(Config.Key.ARTICLE_DIR));
    }

    /**
     * Open current folder.
     */
    @FXML
    private void openImageFolder() {
        openFolder(Config.get(Config.Key.IMAGE_DIR));
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
        fc.setInitialDirectory(new File("."));
        final File result = fc.showOpenDialog(stage.getScene().getWindow());
        if (!result.exists() || !result.canRead()) {
            return;
        }

        final StringBuilder content = new StringBuilder();
        FileUtil.findExtension(result).ifPresent(ext -> {
            switch (ext) {
                case ".txt":
                    openTabWithHtmlContent.accept(
                            result.getAbsolutePath(),
                            FileUtil.readLines(result, "UTF-8").makeString(Strings.LINE_SEPARATOR),
                            ContentType.TEXT
                            );
                    break;
                case ".md":
                    content.append(articleGenerator.convertToHtml(result));
                    if (content.length() == 0) {
                        return;
                    }
                    articleGenerator.generateHtml(content.toString(), result.getAbsolutePath());
                    openExternal.accept(result.getAbsolutePath());
                    break;
            }
        });
    }

    /**
     * Set edit command.
     * @param edit Command
     */
    protected void setOnEdit(final Runnable command) {
        this.edit = command;
    }

    /**
     * Set open tab command.
     * @param open tab Command
     */
    protected void setOnNewTab(final Runnable command) {
        this.open = command;
    }

    /**
     * Set close tab command.
     * @param close tab Command
     */
    protected void setOnCloseTab(final Runnable command) {
        this.close = command;
    }

    /**
     * Set action of close all tabs.
     * @param object
     */
    protected void setOnCloseAllTabs(final Runnable command) {
        this.closeAll = command;
    }

    /**
     * Set search command.
     * @param command
     */
    protected void setOnSearch(final Runnable command) {
        this.search = command;
    }

    /**
     * Set show slide show command.
     * @param command
     */
    protected void setOnSlideShow(final Runnable command) {
        this.slide = command;
    }

    /**
     * Set WordCloud Action.
     * @param tabAction
     */
    protected void setOnWordCloud(final OpenTabAction tabAction) {
        this.tabAction = tabAction;
    }

    /**
     * Set reloading command.
     * @param reload
     */
    protected void setOnReload(final Runnable reload) {
        this.reload = reload;
    }

    /**
     * Set preview HTML source.
     * @param preview
     */
    protected void setOnPreviewSource(final Runnable preview) {
        this.preview = preview;
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
     * Set on quit command.
     * @param onQuit
     */
    protected void setOnQuit(final Runnable onQuit) {
        this.onQuit = onQuit;
    }

    /**
     * Set on make article action.
     * @param c
     */
    public void setOnMakeArticle(final Consumer<Article.Extension> onMakeArticle) {
        this.onMakeArticle = onMakeArticle;
    }

    /**
     * Set on copy command.
     * @param onCopy
     */
    protected void setOnCopy(final Runnable onCopy) {
        this.onCopy = onCopy;
    }

    /**
     * Set on rename command.
     * @param onRename
     */
    protected void setOnRename(final Runnable onRename) {
        this.onRename = onRename;
    }

    /**
     * Set on delete command.
     * @param onDelete
     */
    protected void setOnDelete(final Runnable onDelete) {
        this.onDelete = onDelete;
    }

    /**
     * Set on opening Script Runner's action.
     * @param scriptOpener
     */
    protected void setOnOpenScriptRunner(final OpenTabAction scriptOpener) {
        this.scriptOpener = scriptOpener;
    }

    /**
     * Set on opening external content action.
     * @param openExternal
     */
    protected void setOnOpenExternalFile(final Consumer<String> openExternal) {
        this.openExternal = openExternal;
    }

    /**
     * Show simple message dialog.
     * @param title title
     * @param message message
     */
    private void showMessageDialog(final String title, final String message) {
        getParent().ifPresent(parent -> AlertDialog.showMessage(parent, title, message));
    }

    /**
     * Show simple message popup.
     * @param message message
     */
    private void showMessagePopup(final String message) {
        onPopup.accept(message);
    }

    /**
     * Set on OpenTools action.
     * @param switchRightDrawer
     */
    public void setOnOpenTools(final Runnable switchRightDrawer) {
        this.switchRightDrawer = switchRightDrawer;
    }

    /**
     * Set on popup.
     * @param onPopup
     */
    public void setOnPopup(final Consumer<String> onPopup) {
        this.onPopup = onPopup;
    }

    /**
     * Set current article getter.
     * @param articleGetter
     */
    public void setCurrentArticleGetter(final Supplier<Optional<Article>> articleGetter) {
        this.articleGetter = articleGetter;
    }

    /**
     * Set openTabWithHtmlContent.
     * @param openTabWithHtmlContent
     */
    public void setOpenTabWithHtmlContent(
            final TriConsumer<String, String, ContentType> openTabWithHtmlContent) {
        this.openTabWithHtmlContent = openTabWithHtmlContent;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        articleGenerator = new ArticleGenerator();
    }

}
