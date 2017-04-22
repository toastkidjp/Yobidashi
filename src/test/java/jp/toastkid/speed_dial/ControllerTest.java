package jp.toastkid.speed_dial;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jp.toastkid.libs.utils.Whitebox;

/**
 * {@link Controller}'s test.
 *
 * @author Toast kid
 *
 */
public class ControllerTest extends ApplicationTest {

    /** target object. */
    private Controller controller;

    /**
     * Set up target object.
     * @throws IOException
     */
    @Before
    public void setUp() throws IOException {
        if (controller != null) {
            return;
        }
        final FXMLLoader loader = new FXMLLoader(
                ControllerTest.class.getClassLoader().getResource(Controller.FXML));
        loader.load();
        controller = loader.getController();
    }

    /**
     * Check {@link Controller#setTitle(String)}.
     */
    @Test
    public void testSetTitle() {
        controller.setTitle("title");
        final Label titleField = (Label) Whitebox.getInternalState(controller, "title");
        assertEquals("title", titleField.getText());
    }

    /**
     * Check {@link Controller#setTitle(String)} empty case.
     */
    @Test
    public void testSetEmptyTitle() {
        final Label titleField = (Label) Whitebox.getInternalState(controller, "title");
        final String text = titleField.getText();
        controller.setTitle(null);
        assertEquals(text, titleField.getText());
    }

    /**
     * Check {@link Controller#setZero()}.
     */
    @Test
    public void testSetZero() {
        controller.setZero();
        @SuppressWarnings("rawtypes")
        final ComboBox cb = (ComboBox) Whitebox.getInternalState(controller, "type");
        assertEquals(0, cb.getSelectionModel().getSelectedIndex());
    }

    /**
     * Check {@link Controller#setBackground(String)}.
     */
    @Test
    public void testSetBackground() {
        controller.setBackground("bg");
        assertEquals(
                "-fx-background-image: url('bg'); -fx-background-position: center center;"
                + " -fx-background-size: cover;-fx-background-repeat: stretch;",
                controller.getRoot().getStyle()
                );
    }

    /**
     * Only fire search action.
     */
    @Test
    public void testArticleSearch() {
        @SuppressWarnings("rawtypes")
        final ComboBox cb = (ComboBox) Whitebox.getInternalState(controller, "type");
        cb.getSelectionModel().select(0);
        final TextField input = (TextField) Whitebox.getInternalState(controller, "input");
        input.setText("Tomato");
        input.fireEvent(new ActionEvent());
    }

    /**
     * Only fire search action.
     */
    @Test
    public void testWebSearch() {
        @SuppressWarnings("rawtypes")
        final ComboBox cb = (ComboBox) Whitebox.getInternalState(controller, "type");
        cb.getSelectionModel().select(1);
        final TextField input = (TextField) Whitebox.getInternalState(controller, "input");
        input.setText("Tomato");
        input.fireEvent(new ActionEvent());
    }

    /**
     * Only fire search action.
     */
    @Test
    public void testSearchEmptyCase() {
        final TextField input = (TextField) Whitebox.getInternalState(controller, "input");
        input.setText(null);
        input.fireEvent(new ActionEvent());
    }

    @Override
    public void start(Stage stage) throws Exception {
        // NOP.
    }

}
