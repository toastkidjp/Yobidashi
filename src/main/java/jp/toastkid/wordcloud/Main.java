package jp.toastkid.wordcloud;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import jp.toastkid.article.models.Defines;

/**
 * Word cloud.
 *
 * @author Toast kid
 */
public final class Main extends Application {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /** path to scene file. */
    private static final String FXML_PATH = Defines.SCENE_DIR + "/WordCloud.fxml";

    /** Controller object. */
    private Controller controller;

    /** Stage. */
    private Stage stage;

    /** Scene. */
    private Scene scene = null;

    /**
     * Constructor.
     * @param parent
     */
    public Main() {
        loadDialog(null);
    }

    /**
     * FXML からロードする.
     * @return Parent オブジェクト
     */
    private final void loadDialog(final Window window) {
        final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FXML_PATH));
        try {
            if (scene == null) {
                scene = new Scene(loader.load());
            }
        } catch (final IOException e) {
            LOGGER.error("Caught Error.", e);
        }
        controller = loader.getController();
        stage = new Stage(StageStyle.DECORATED);
        stage.setScene(scene);
        if (window != null) {
            stage.initOwner(window);
        }
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        new FxWordCloud.Builder().build().draw(controller.canvas, "sample");
        Platform.runLater(()-> controller.parent.requestLayout());
    }

    public void show() {
        stage.showAndWait();
    }

    @Override
    public void start(final Stage arg0) throws Exception {
        show();
    }
    /**
     *
     * @param args
     */
    public static void main(final String[] args) {
        Application.launch(Main.class);
    }
}