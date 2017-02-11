package jp.toastkid.yobidashi;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import com.jfoenix.controls.JFXSlider;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jp.toastkid.yobidashi.message.Message;
import jp.toastkid.yobidashi.models.Config;
import reactor.core.publisher.Flux;
import reactor.core.publisher.TopicProcessor;

/**
 * {@link ToolsController}'s test case.
 *
 * @author Toast kid
 *
 */
public class ToolsControllerTest extends ApplicationTest {

    /** Test object. */
    private ToolsController controller;

    /** Test messenger. */
    private TopicProcessor<Message> messenger;

    /** Stage. */
    private Stage stage;

    /**
     * Show stage.
     */
    @Before
    public void setUp() {
        Platform.runLater(stage::show);
    }

    /**
     * Close stage.
     */
    @After
    public void tearDown() {
        Platform.runLater(stage::close);
    }

    /**
     * Test of {@link ToolsController#setFlux(Flux)}.
     */
    @Test
    public void testSetFlux() {
        controller.setFlux(Flux.just(new SimpleDoubleProperty(1.0d)));
    }

    /**
     * Test of {@link ToolsController} draw button.
     */
    @Test
    public void test_draw() {
        Platform.runLater(() -> {
            final Button draw = (Button) lookup("Draw").query();
            draw.fire();
        });
    }

    /**
     * Test of {@link ToolsController#applyFontSetting}.
     */
    @Test
    public void test_font() {
        Platform.runLater(() -> {
            final Button draw = (Button) lookup("Apply").query();
            draw.fire();
        });
    }

    /**
     * Test of {@link ToolsController} zoom.
     */
    @Test
    public void test_setZoom() {
        Platform.runLater(() -> {
            final JFXSlider zoom = (JFXSlider) lookup("#zoom").query();
            zoom.setValue(1.1d);
            final TextField zoomInput = (TextField) lookup("#zoomInput").query();
            assertEquals("1.1" , zoomInput.getText());
        });
    }

    /**
     * Test of {@link ToolsController#callDefaultZoom()}.
     */
    @Test
    public void test_callDefaultZoom() {
        Platform.runLater(() -> lookup("Default").query().fireEvent(new ActionEvent()));
    }

    @Override
    public void start(final Stage stage) throws Exception {
        this.stage = stage;
        final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("scenes/Tools.fxml"));
        try {
            loader.load();
            controller = (ToolsController) loader.getController();
            stage.setScene(new Scene(controller.getRoot()));
            controller.init(stage);
            controller.setConfig(makeConfig());
        } catch (final IOException e) {
            e.printStackTrace();
            Platform.exit();
        }
        messenger = controller.getMessenger();
    }

    /**
     * Initialize {@link Config}.
     * @return
     * @throws URISyntaxException
     */
    public Config makeConfig() throws URISyntaxException {
        final Path path = Paths.get(getClass().getClassLoader()
                .getResource("conf/conf.properties").toURI());
        return new Config(path);
    }
}
