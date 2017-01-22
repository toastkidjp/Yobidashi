package jp.toastkid.dialog;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jp.toastkid.article.ImageChooser;
import jp.toastkid.yobidashi.models.Defines;

/**
 * Progress dialog.
 *
 * @author Toast kid
 * @see <a href="http://stackoverflow.com/questions/27866432/
 *controls-fx-progress-dialog-modality-alert-modality">Controls FX Progress Dialog Modality
 * &amp; Alert Modality</a>
 * @see
 * <a href="http://stackoverflow.com/questions/29625170/display-popup-with-progressbar-in-javafx">
 * Display Popup with ProgressBar in JavaFX</a>
 */
public final class ProgressDialog extends Application implements AutoCloseable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProgressDialog.class);

    /** path to user image directory. */
    private static final String SPLASH_DIR     = "images/splash/default.jpg";

    /** path to user image directory. */
    private static final String USER_IMAGE_DIR = Defines.USER_DIR + "/res/images/background/";

    /** FXML ファイルのパス. */
    private static final String FXML_PATH      = Defines.SCENE_DIR + "/ProgressDialog.fxml";

    /** Scene. */
    private Scene scene = null;

    /** dialog stage. */
    private final Stage dialogStage;

    /** controller. */
    private final ProgressDialogController controller;

    /** splash image file chooser. */
    private final ImageChooser chooser;

    /** command with long time. */
    private Task<Integer> command;

    /**
     * Builder.
     *
     * @author Toast kid
     */
    public static class Builder {
        private Scene scene;
        private Task<Integer> command;

        public Builder setScene(final Scene scene) {
            this.scene = scene;
            return this;
        }

        public Builder setCommand(final Task<Integer> command) {
            this.command = command;
            return this;
        }

        public ProgressDialog build() {
            return new ProgressDialog(this);
        }
    }

    public ProgressDialog() {
        this(new Builder().setCommand(new Task<Integer>(){
            @Override
            protected Integer call() throws Exception {
                updateMessage("Please wait 5s...");
                Thread.sleep(5000L);
                return 0;
            }
        }));
    }

    /**
     * Load scene file.
     */
    private ProgressDialog(final Builder b) {
        final FXMLLoader loader
            = new FXMLLoader(getClass().getClassLoader().getResource(FXML_PATH));
        try {
            if (scene == null) {
                scene = new Scene(loader.load());
            }
        } catch (final IOException e) {
            LOGGER.error("Error", e);
        }
        controller = loader.getController();
        dialogStage = new Stage(StageStyle.DECORATED);
        dialogStage.setScene(scene);
        dialogStage.setAlwaysOnTop(true);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setResizable(false);

        this.command = b.command;

        this.chooser = new ImageChooser(USER_IMAGE_DIR);
        try {
            chooser.add(ProgressDialog.class.getClassLoader().getResource(SPLASH_DIR).toURI());
        } catch (final URISyntaxException e) {
            LOGGER.error("Error", e);
        }
        // 画像をランダムで選択して設定.
        findStyle().ifPresent(controller.background::setStyle);

        if (b.scene == null || b.scene.getStylesheets() == null) {
            return;
        }
        // StyleSheet をコピーする.
        this.scene.getStylesheets().addAll(b.scene.getStylesheets());
    }

    /**
     * find image and make style string.
     * @return optional string.
     */
    private Optional<String> findStyle() {
        final String choose = chooser.choose();
        if (choose == null || choose.isEmpty()) {
            return Optional.empty();
        }
        final String style = "-fx-background-image: url('" + choose + "'); "
                + "-fx-background-position: center center; "
                + "-fx-background-size: cover;"
                + "-fx-background-repeat: stretch;";
        return Optional.of(style);
    }

    @Override
    public void start(final Stage stage) {
        final Service<Integer> service = new Service<Integer>() {
            @Override
            protected Task<Integer> createTask() {
                return ProgressDialog.this.command;
            }

            /**
             * タスクの終了時処理.
             * JavaFX Application Threadで実行.
             */
            @Override
            protected void succeeded() {
                dialogStage.setAlwaysOnTop(false);
                dialogStage.close();
            }
        };

        dialogStage.titleProperty().bind(service.titleProperty());
        controller.pb.progressProperty().bind(service.progressProperty());
        controller.label.textProperty().bind(service.messageProperty());

        if (service != null) {
            service.restart();
        }
        dialogStage.show();
    }

    @Override
    public void stop() {
        try {
            super.stop();
        } catch (final Exception e) {
            LOGGER.error("Error", e);;
        }
    }

    @Override
    public void close() throws Exception {
        this.stop();
    }

    /**
     * Main method.
     * @param args
     */
    public static void main(final String[] args) {
        Application.launch(ProgressDialog.class);
    }
}
