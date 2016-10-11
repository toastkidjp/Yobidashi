package jp.toastkid.yobidashi;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.controls.JFXListView;

import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.Window;
import jp.toastkid.dialog.AlertDialog;
import jp.toastkid.dialog.ProgressDialog;
import jp.toastkid.jfx.common.control.ActionLabel;
import jp.toastkid.libs.archiver.ZipArchiver;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.libs.utils.RuntimeUtil;
import jp.toastkid.wiki.ApplicationState;
import jp.toastkid.wiki.dialog.ConfigDialog;
import jp.toastkid.wiki.models.Config;

/**
 * Side menu's controller.
 *
 * @author Toast kid
 *
 */
public class SideMenuController {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SideMenuController.class);

    /** about file. */
    private static final String PATH_ABOUT_APP   = "README.md";

    @FXML
    private TabPane menuTabs;

    /** for controlling window. */
    private Stage stage;

    /** search command. */
    private Runnable search;

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
                final ProgressDialog pd = new ProgressDialog.Builder()
                        .setScene(parent.getScene())
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
        getParent().ifPresent(parent -> AlertDialog.showMessage(
                parent,
                "文字数計測",
                Config.article.makeCharCountResult()
                )
            );
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
            //reload();
        }
    }

    /**
     * Show current application state.
     */
    @FXML
    private final void callApplicationState() {
        final Optional<Window> parent = getParent();
        if (!parent.isPresent()) {
            return;
        }

        final Map<String, String> map = ApplicationState.getConfigMap();
        final StringBuilder bld = new StringBuilder();
        final String lineSeparator = System.lineSeparator();
        map.forEach((key, value) ->
            bld.append(key).append("\t").append(value).append(lineSeparator)
        );
        AlertDialog.showMessage(parent.get(), "状態", bld.toString());
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

        // TODO implement
        /*func.generateHtml(
                new ArticleGenerator().md2Html(PATH_ABOUT_APP),
                "About"
                );
        openWebTab();
        loadDefaultFile();*/
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
    protected void openScripter() {
        new jp.toastkid.script.Main().show(stage);
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
    public void callCapture() {
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
     * Set search command.
     * @param command
     */
    protected void setOnSearch(final Runnable command) {
        this.search = command;
    }

    private void putAccerelator() {
        final ObservableMap<KeyCombination, Runnable> accelerators
            = this.stage.getScene().getAccelerators();
        menuTabs.getTabs().forEach(tab -> {
            @SuppressWarnings("unchecked")
            final JFXListView<ActionLabel> labels = (JFXListView<ActionLabel>) tab.getContent();
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
    public void setStage(final Stage stage) {
        this.stage  = stage;
        putAccerelator();
    }

}
