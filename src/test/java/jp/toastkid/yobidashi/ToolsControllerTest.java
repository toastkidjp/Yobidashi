package jp.toastkid.yobidashi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import com.jfoenix.controls.JFXSlider;

import io.reactivex.Observable;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jp.toastkid.article.control.UserAgent;
import jp.toastkid.yobidashi.message.UserAgentMessage;

/**
 * {@link ToolsController}'s test case.
 *
 * @author Toast kid
 *
 */
public class ToolsControllerTest extends ApplicationTest {

    /** Test object. */
    private ToolsController controller;

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
        controller.setFlux(Observable.just(new SimpleDoubleProperty(1.0d)));
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
     * Test of {@link ToolsController#changeUserAgent}.
     */
    @Test
    public void test_ua() {
        Platform.runLater(() -> {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            final ComboBox<UserAgent> draw = (ComboBox) lookup("#ua").query();
            controller.subscribe(m -> {
                final UserAgentMessage um = (UserAgentMessage) m;
                assertEquals(draw.getSelectionModel().getSelectedItem(), um.getUserAgent());
            });
            draw.getSelectionModel().select(1);
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

    /**
     * Test of {@link ToolsController#accelerators()}.
     */
    @Test
    public void test_accelerators() {
        assertNotNull(controller.accelerators());
        assertTrue(0 < controller.accelerators().size());
    }

    @Override
    public void start(final Stage stage) throws Exception {
        this.stage = stage;
        final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("scenes/Tools.fxml"));
        try {
            loader.load();
            controller = (ToolsController) loader.getController();
            stage.setScene(new Scene(controller.getRoot()));
        } catch (final IOException e) {
            e.printStackTrace();
            Platform.exit();
        }
    }

}
