package jp.toastkid.yobidashi;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jp.toastkid.yobidashi.models.Defines;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;
import reactor.core.scheduler.Schedulers;

/**
 * JavaFX Wiki-Client.
 * @author Toast kid
 * @version 0.0.1
 */
public final class Main extends Application {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /** Path to icon of this app. */
    private static final String PATH_IMG_ICON = "images/Icon.png";

    /** fxml file. */
    private static final String FXML_PATH = Defines.SCENE_DIR + "/YobidashiMain.fxml";

    /** Controller. */
    private Controller controller;

    /** Starting ms. */
    private long start;

    /** Stage initializer disposable. */
    private Disposable stageDisposable;

    /** Controller initializer disposable. */
    private MonoProcessor<Object> controllerDisposable;

    @Override
    public void start(final Stage stage) {
        start = System.currentTimeMillis();

        try {
            launch(stage);
        } catch (final Throwable e) {
            LOGGER.error("Caught error.", e);
        } finally {
            stageDisposable.dispose();
            controllerDisposable.dispose();
        }
    }

    /**
     * initialize method.
     * @param stage
     */
    private void launch(final Stage stage) {

        stageDisposable = readScene().subscribeOn(Schedulers.immediate()).subscribe(scene -> {
            controller.setStage(stage);
            stage.getIcons()
                .add(new Image(getClass().getClassLoader().getResourceAsStream(PATH_IMG_ICON)));
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> this.closeApplication(stage));
            stage.centerOnScreen();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setMaximized(true);
            stage.show();
            LOGGER.info("{} Ended set stage size. {}[ms]",
                    Thread.currentThread().getName(),
                    (System.currentTimeMillis() - start)
                    );
        });

        controllerDisposable = Mono.create(emitter -> {
            final Rectangle2D d = Screen.getPrimary().getVisualBounds();
            stage.setWidth(d.getWidth());
            stage.setHeight(d.getHeight());
            controller.setSize(d.getWidth(), d.getHeight());
            // setup searcher.
            controller.setupExpandables();
            controller.setupSideMenu();
            controller.setStatus(" Complete - " + (System.currentTimeMillis() - start) + "[ms]");
            emitter.success();
        }).subscribeOn(Schedulers.elastic()).subscribe();
    }

    /**
     * コントローラに stage を渡し、シーンファイルを読み込む.
     * @return Scene オブジェクト
     */
    private final Mono<Scene> readScene() {
        final long start = System.currentTimeMillis();
        final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FXML_PATH));
        try {
            loader.load();
            controller = (Controller) loader.getController();
        } catch (final IOException e) {
            LOGGER.error("Caught error.", e);
            Platform.exit();
        }
        LOGGER.info("{} Ended loading scene graph. {}[ms]",
                Thread.currentThread().getName(),
                System.currentTimeMillis() - start);
        return Mono.just(new Scene(controller.root));
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