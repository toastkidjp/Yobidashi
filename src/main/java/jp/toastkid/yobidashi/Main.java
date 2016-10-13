package jp.toastkid.yobidashi;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jp.toastkid.dialog.AlertDialog;
import jp.toastkid.wiki.models.Config;
import jp.toastkid.wiki.models.Defines;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * JavaFX による WikiClient.
 * @author Toast kid
 * @version 0.0.1
 */
public final class Main extends Application {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /** アイコン画像ファイルへのパス */
    private static final String PATH_IMG_ICON = "images/Icon.png";

    /** 二重起動している際のメッセージ. */
    private static final String MESSAGE_ALERT_PROCESS_DUPLICATE
        = "すでに別プロセスで起動しています。"
                + "\n心当たりのない場合は「temp.html」というファイルを削除してください。";

    /** fxml ファイル. */
    private static final String FXML_PATH = Defines.SCENE_DIR + "/YobidashiMain.fxml";

    /** process lock file. */
    private static final File   LOCK_FILE = new File(Defines.TEMP_FILE_NAME);

    /** コントローラ. */
    private Controller controller;

    /** starting ms. */
    private long start;

    @Override
    public void start(final Stage stage) {
        start = System.currentTimeMillis();

        // It must delete lock file when this process was shutdown.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (LOCK_FILE.exists()) {
                LOCK_FILE.delete();
                LOGGER.info("Lock file was deleted.");
            }
        }));
        try {
            launch(stage);
        } catch (final Throwable e) {
            LOGGER.error("Caught error.", e);
        }
    }

    /**
     * initialize method.
     * @param stage
     */
    private void launch(final Stage stage) {
        // (130615) 二重起動防止機能
        if (LOCK_FILE.exists()) {
            new AlertDialog.Builder()
                .setTitle("二重起動防止")
                .setMessage(MESSAGE_ALERT_PROCESS_DUPLICATE)
                .setOnPositive("OK",     () -> System.exit(-1))
                .setOnNegative("Delete", () -> {})
                .build().show();
        }

        // ファイルがない場合は作成.
        try {
            LOCK_FILE.createNewFile();
        } catch (final IOException e) {
            LOGGER.error("Caught error.", e);
        }

        Mono.create(emitter -> {
            stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream(PATH_IMG_ICON)));
            emitter.success();
            LOGGER.info("Ended set stage size.");
        })
        .subscribeOn(Schedulers.elastic()).subscribe();

        readScene(stage).subscribeOn(Schedulers.immediate()).subscribe(scene -> {
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> this.closeApplication(stage));
            stage.centerOnScreen();
            final Screen screen = Screen.getScreens().get(0);
            final Rectangle2D bounds = screen.getVisualBounds();
            final BoundingBox maximizedBox = new BoundingBox(
                    bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
            // maximized the stage
            stage.setX(maximizedBox.getMinX());
            stage.setY(maximizedBox.getMinY());
            stage.setWidth(maximizedBox.getWidth());
            stage.setHeight(maximizedBox.getHeight());
            stage.show();
            controller.setStatus("Complete - " + (System.currentTimeMillis() - start) + "[ms]");
        });

        Mono.create(emitter -> {
            final Rectangle2D d = Screen.getPrimary().getVisualBounds();
            stage.setWidth(d.getWidth());
            stage.setHeight(d.getHeight());
            controller.setSize(d.getWidth(), d.getHeight());
            // setup searcher.
            controller.setupExpandables();
            controller.setupSideMenu();
            emitter.success();
            LOGGER.info("Ended set stage size.");
        }).subscribeOn(Schedulers.elastic()).subscribe();

        Mono.create(emitter -> {
            stage.initStyle(StageStyle.DECORATED);
            //scene.setFill(Color.TRANSPARENT);
            emitter.success();
            LOGGER.info("Ended set stage size.");
        }).subscribeOn(Schedulers.elastic()).subscribe();
    }

    /**
     * コントローラに stage を渡し、シーンファイルを読み込む.
     * @param stage Stage オブジェクト
     * @return Scene オブジェクト
     */
    private final Mono<Scene> readScene(final Stage stage) {
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FXML_PATH));
            final VBox loaded = (VBox) loader.load();
            controller = (Controller) loader.getController();
            stage.setTitle(Config.get(Config.Key.WIKI_TITLE));
            controller.setStage(stage);
            /*final JFXDecorator decorator = new JFXDecorator(stage, loaded);
            decorator.setCustomMaximize(true);
            decorator.setOnCloseButtonAction(() -> this.closeApplication(stage));
            decorator.setMaximized(true);*/
            return Mono.just(new Scene(controller.root));
        } catch (final IOException e) {
            LOGGER.error("Caught error.", e);
            Platform.exit();
        }
        return Mono.empty();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        controller = null;
    }

    /**
     * Close this application.
     * @param stage
     */
    public final void closeApplication(final Stage stage) {
        stage.close();
        Platform.exit();
        System.exit(0);
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