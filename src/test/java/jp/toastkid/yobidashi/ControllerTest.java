package jp.toastkid.yobidashi;

import java.io.IOException;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * {@link Controller}'s test.
 *
 * @author Toast kid
 *
 */
public class ControllerTest extends ApplicationTest {

    /** FXML. */
    private static final String FXML = "scenes/YobidashiMain.fxml";

    /** Test stage. */
    private Stage stage;

    /** Test object. */
    private Controller controller;

    @Test
    public void test() {
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        final FXMLLoader loader
            = new FXMLLoader(getClass().getClassLoader().getResource(FXML));
        try {
            final Pane root = loader.load();
            controller = (Controller) loader.getController();
            stage.setScene(new Scene(root));
            controller.setStage(stage);
        } catch (final IOException e) {
            e.printStackTrace();
            Platform.exit();
        }
        stage.show();
    }

}
