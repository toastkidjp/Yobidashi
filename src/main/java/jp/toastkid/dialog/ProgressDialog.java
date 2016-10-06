package jp.toastkid.dialog;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jp.toastkid.wiki.ImageChooser;
import jp.toastkid.wiki.models.Defines;

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
    private static final String SPLASH_DIR     = Defines.ASSETS_DIR + "/splash/";

    /** path to user image directory. */
    private static final String USER_IMAGE_DIR = Defines.USER_DIR + "/res/images/background/";

    /** FXML ファイルのパス. */
    private static final String FXML_PATH      = Defines.SCENE_DIR + "/ProgressDialog.fxml";

    /** Scene. */
    private Scene scene = null;

    /** 表示中. */
    private boolean active = true;

    /** progress indicator. */
    private final AtomicInteger progress;

    /** message. */
    private String text;

    /** dialog stage. */
    private final Stage dialogStage;

    /** controller. */
    private final ProgressDialogController controller;

    /** splash image file chooser. */
    private final ImageChooser chooser;

    /**
     * Builder.
     *
     * @author Toast kid
     */
    public static class Builder {
        private Scene scene;
        private String text;
        private int progress;

        public Builder setScene(final Scene scene) {
            this.scene = scene;
            return this;
        }

        public Builder setText(final String text) {
            this.text = text;
            return this;
        }

        public Builder setProgress(final int progress) {
            this.progress = progress;
            return this;
        }

        public ProgressDialog build() {
            return new ProgressDialog(this);
        }
    }

    /**
     * Load scene file.
     */
    private ProgressDialog(final Builder b) {
        progress = new AtomicInteger(b.progress);

        final FXMLLoader loader
            = new FXMLLoader(getClass().getClassLoader().getResource(FXML_PATH));
        try {
            if (scene == null) {
                scene = new Scene(loader.load());
            }
        } catch (final IOException e) {
            LOGGER.error("Error", e);;
        }
        controller = loader.getController();
        dialogStage = new Stage(StageStyle.DECORATED);
        dialogStage.setScene(scene);
        dialogStage.setAlwaysOnTop(true);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setResizable(false);

        this.chooser = new ImageChooser(SPLASH_DIR, USER_IMAGE_DIR);
        // 画像をランダムで選択して設定.
        findStyle().ifPresent(style -> { controller.background.setStyle(style); });

        this.text = b.text;
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

    /**
     * set Progress indicator.
     * @param progres
     */
    public final void setProgress(final int progress) {
        if (progress < 0 || 100 <= progress) {
            return;
        }
        this.progress.set(progress);
    }

    /**
     * set Progress indicator.
     * @param progres
     */
    public final void addProgress(final int progress) {
        if (progress < 0 || 100 <= progress) {
            return;
        }
        this.progress.addAndGet(progress);
    }

    /**
     * get progress.
     * @return progress value.
     */
    public final int getProg() {
        return this.progress.get();
    }

    @Override
    public void start(final Stage stage) {
        Platform.runLater(() -> dialogStage.show());
        final Service<Integer> service = new Service<Integer>() {
            @Override
            protected Task<Integer> createTask() {
                return new Task<Integer>() {
                    @Override
                    public Integer call() {
                        // 長い時間のかかるタスク
                        try {
                            while (active && getProg() < 100) {
                                updateTitle(getProg() + "%");
                                updateProgress(getProg(), 100);
                                updateMessage(getText());
                                Thread.sleep(10L);
                            }
                        } catch (final Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            updateProgress(100, 100);
                        }
                        return progress.get();
                    }
                };
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

        activate(service);
    }

    /**
     * add text.
     * @param text
     */
    public void addText(final String text) {
        LOGGER.info(getProg() + " " + text);
        this.text = text;
    }

    /**
     * get text.
     * @return
     */
    private String getText() {
        return StringUtils.isNotEmpty(this.text) ? this.text : "";
    }

    /**
     * activate this instance.
     * @param service
     */
    public void activate(final Service<?> service)  {
        dialogStage.titleProperty().bind(service.titleProperty());
        controller.pb.progressProperty().bind(service.progressProperty());
        controller.label.textProperty().bind(service.messageProperty());

        service.restart();
        dialogStage.show();
    }

    @Override
    public void stop() {
        try {
            super.stop();
        } catch (final Exception e) {
            LOGGER.error("Error", e);;
        } finally {
            active = false;
        }
    }

    public static void main(final String... args) {
        Application.launch(ProgressDialog.class);
    }

    @Override
    public void close() throws Exception {
        this.stop();
    }
}
