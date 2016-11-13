package jp.toastkid.yobidashi;

import java.awt.Rectangle;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;

import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import jp.toastkid.dialog.AlertDialog;
import jp.toastkid.dialog.ProgressDialog;
import jp.toastkid.jfx.common.control.MenuLabel;
import jp.toastkid.libs.archiver.ZipArchiver;
import jp.toastkid.libs.utils.AobunUtils;
import jp.toastkid.libs.utils.CalendarUtil;
import jp.toastkid.libs.utils.CollectionUtil;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.libs.utils.RuntimeUtil;
import jp.toastkid.libs.utils.Strings;
import jp.toastkid.wiki.ApplicationState;
import jp.toastkid.wiki.Archiver;
import jp.toastkid.wiki.EpubGenerator;
import jp.toastkid.wiki.dialog.ConfigDialog;
import jp.toastkid.wiki.lib.Wiki2Markdown;
import jp.toastkid.wiki.models.Config;
import jp.toastkid.wordcloud.FxWordCloud;
import jp.toastkid.wordcloud.JFXMasonryPane2;

/**
 * Side menu's controller.
 *
 * @author Toast kid
 */
public class SideMenuController {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SideMenuController.class);

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

    /** Command of converting to Markdown. */
    private OpenTabAction convert2Md;

    /** Command of showing about page. */
    private Runnable about;

    /** Command of showing log viewer. */
    private Runnable log;

    /** Command of quit this app. */
    private Runnable onQuit;

    /** Command of copy article. */
    private Runnable onCopy;

    /** Command of rename article. */
    private Runnable onRename;

    /** Command of delete article. */
    private Runnable onDelete;

    /** Action of launch Script runner tab. */
    private OpenTabAction scriptOpener;

    private jp.toastkid.script.Main scriptRunner;

    private jp.toastkid.name.Main nameGenerator;

    /**
     * バックアップ機能を呼び出す。
     */
    @FXML
    private final void callBackUp(final ActionEvent event) {
        final Window parent = getParent().get();
        new AlertDialog.Builder(parent)
            .setTitle("バックアップ")
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
        showMessageDialog("文字数計測", Config.article.makeCharCountResult());
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

        final String current = Config.get(Config.Key.VIEW_TEMPLATE);
        new ConfigDialog(parent.get()).showConfigDialog();
        Config.reload();
        if (!current.equals(Config.get(Config.Key.VIEW_TEMPLATE))) {
            reload.run();
        }
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
        final String absolutePath = Config.article.file.getAbsolutePath();
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

        wordCloud = new FxWordCloud.Builder().setNumOfWords(200).setMaxFontSize(120.0)
                        .setMinFontSize(8.0).build();
        wordCloud.draw(pane, Config.article.file);
        tabAction.open(Config.article.title + "のワードクラウド", pane);
    }
    /**
     * call About.
     */
    @FXML
    private final void callAbout() {
        about.run();
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
    private final void openWebTab() {
        open.run();
    }

    /**
     * Close new tab.
     */
    @FXML
    private final void closeTab() {
        close.run();
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
     * Convert current article to Markdown.
     */
    @FXML
    private void callConvertMd() {
        final CodeArea pane = new CodeArea();
        pane.setParagraphGraphicFactory(LineNumberFactory.get(pane));

        final double prefWidth = stage.getWidth() * 0.8;
        pane.setPrefWidth(prefWidth);
        pane.setPrefHeight(stage.getHeight());

        try {
            pane.replaceText(CollectionUtil.implode(
                    Wiki2Markdown.convert(Files.readAllLines(Config.article.file.toPath()))
                    ));
        } catch (final IOException e) {
            LOGGER.error("Error", e);
            showMessageDialog("IOException", e.getMessage());
            return;
        }
        convert2Md.open("[MD] " + Config.article.title, new AnchorPane(pane));
    }

    /**
     * Show log viewer.
     */
    @FXML
    private void callLogViewer() {
        log.run();
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
                                    new EpubGenerator().toEpub(vertically.isSelected());
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
     * Set edit command.
     * @param edit Command
     */
    protected void setOnEdit(final Runnable command) {
        this.edit = command;
    }

    /**
     * Set open tab command.
     * @param edit Command
     */
    protected void setOnNewTab(final Runnable command) {
        this.open = command;
    }

    /**
     * Set close tab command.
     * @param edit Command
     */
    protected void setOnCloseTab(final Runnable command) {
        this.close = command;
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
     * Set convert command.
     * @param convert2Md command.
     */
    protected void setOnConvertMd(final OpenTabAction convert2Md) {
        this.convert2Md = convert2Md;
    }

    /**
     * for use shortcut when start-up.
     */
    private void putAccerelator() {
        final ObservableMap<KeyCombination, Runnable> accelerators
            = this.stage.getScene().getAccelerators();
        menuTabs.getTabs().forEach(tab -> {
            @SuppressWarnings("unchecked")
            final JFXListView<MenuLabel> labels = (JFXListView<MenuLabel>) tab.getContent();
            labels.getItems().forEach(l -> {
                if (l.getAccelerator() == null) {
                    return;
                }
                accelerators.put(
                        l.getAccelerator(), () -> l.getOnAction().handle(new ActionEvent()));
            });
        });
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
     * Set "About" command.
     * @param about command
     */
    protected void setOnAbout(final Runnable about) {
        this.about = about;
    }

    /**
     * Set "LogViewer" command.
     * @param log command
     */
    protected void setOnOpenLogViewer(final Runnable log) {
        this.log = log;
    }

    /**
     * Set on quit command.
     * @param onQuit
     */
    protected void setOnQuit(final Runnable onQuit) {
        this.onQuit = onQuit;
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
     * Show simple message dialog.
     * @param title title
     * @param message message
     */
    private void showMessageDialog(final String title, final String message) {
        getParent().ifPresent(parent -> AlertDialog.showMessage(parent, title, message));
    }

}
