package jp.toastkid.gui.jfx.wiki;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import jp.toastkid.gui.jfx.dialog.AlertDialog;
import jp.toastkid.gui.jfx.wiki.models.Config;
import jp.toastkid.gui.jfx.wiki.models.Defines;
import jp.toastkid.gui.jfx.wiki.models.Resources;
import jp.toastkid.libs.utils.FileUtil;

/**
 * JavaFX による WikiClient.
 * @author Toast kid
 * @version 0.0.1
 */
public final class Main extends Application {

    /** 二重起動している際のメッセージ. */
    private static final String MESSAGE_ALERT_PROCESS_DUPLICATE
        = "すでに別プロセスで起動しています。"
                + "\n心当たりのない場合は「temp.html」というファイルを削除してください。";

    /** fxml ファイル. */
    private static final String FXML_PATH = "public/scenes/Main.fxml";

    /** process lock file. */
    private static final File LOCK_FILE = new File(Defines.TEMP_FILE_NAME);

    /** コントローラ. */
    private Controller controller;

    @Override
    public void start(final Stage stage) {
        final long start = System.currentTimeMillis();

        // It must delete lock file when this process was shutdown.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (LOCK_FILE.exists()) {
                LOCK_FILE.delete();
                System.out.println("Lock file was deleted.");
            }
        }));
        try {
            initialize(stage);
        } catch (final Throwable e) {
            e.printStackTrace();
        }
        controller.setStatus("完了 - " + (System.currentTimeMillis() - start) + "[ms]");
    }

    /**
     * initialize method.
     * @param stage
     */
    private void initialize(final Stage stage) {
        // (130615) 二重起動防止機能
        if (LOCK_FILE.exists()) {
            new AlertDialog.Builder().setTitle("二重起動防止")
                .setMessage(MESSAGE_ALERT_PROCESS_DUPLICATE)
                .setOnPositive("OK",     () -> System.exit(-1))
                .setOnNegative("Delete", () -> {})
                .build().show();
        }

        // ファイルがない場合は作成.
        try {
            LOCK_FILE.createNewFile();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        stage.getIcons().add(new Image(FileUtil.getUrl(Resources.PATH_IMG_ICON).toString()));
        final Scene scene = readScene(stage);
        stage.setScene(scene);
        final Rectangle2D d = Screen.getPrimary().getVisualBounds();
        stage.setWidth(d.getWidth());
        stage.setHeight(d.getHeight());
        controller.setSize(d.getWidth(), d.getHeight());
        // setup searcher.
        controller.setupExpandables();

        stage.initStyle(StageStyle.DECORATED);
        //stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        stage.setOnCloseRequest( (event) -> {
            if (event.getEventType().equals(WindowEvent.WINDOW_CLOSE_REQUEST)) {
                controller.closeApplication();
            }
        });

        stage.show();
    }

    /**
     * コントローラに stage を渡し、シーンファイルを読み込む.
     * @param stage Stage オブジェクト
     * @return Scene オブジェクト
     */
    private final Scene readScene(final Stage stage) {
        try {
            final FXMLLoader loader = new FXMLLoader(FileUtil.getUrl(FXML_PATH));
            final VBox loaded = (VBox) loader.load();
            controller = (Controller) loader.getController();
            stage.setTitle(Config.get(Config.Key.WIKI_TITLE));
            controller.setStage(stage);
            return new Scene(loaded);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        controller = null;
    }

    /**
     *
     * @param args
     *
     */
    public static void main(final String[] args) {
        Application.launch(Main.class);
    }
}