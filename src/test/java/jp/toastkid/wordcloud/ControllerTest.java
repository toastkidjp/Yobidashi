package jp.toastkid.wordcloud;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jp.toastkid.yobidashi.models.Defines;

/**
 * {@link Controller}'s test case.
 *
 * @author Toast kid
 *
 */
public class ControllerTest extends ApplicationTest {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /** Sample file path. */
    private static final String SAMPLE_PATH = "wordcloud/sample.txt";

    /** path to scene file. */
    private static final String FXML_PATH = Defines.SCENE_DIR + "/WordCloud.fxml";

    /** Controller. */
    private Controller controller;

    /** Scene. */
    private Scene scene;

    /**
     * Set up controller.
     */
    @Override
	public void init() {

        final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FXML_PATH));
        try {
            scene = new Scene(loader.load());
        } catch (final IOException e) {
            LOGGER.error("Caught Error.", e);
        }
        controller = loader.getController();
        System.out.println("inir");
    }

    /**
     * For test coverage.
     */
    @Test
    public void testInitialize() {
        controller.initialize(null, null);
    }

    @Override
    public void start(Stage stage) throws Exception {
        init();
        stage = new Stage(StageStyle.DECORATED);
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        final Path path = Paths.get(getClass().getClassLoader().getResource(SAMPLE_PATH).toURI());
        //System.out.println(new String(Files.readAllBytes(path)));
        new FxWordCloud.Builder().build().draw(controller.canvas, path.toString());
        Platform.runLater(()-> controller.parent.requestLayout());
        stage.show();
    }

}
