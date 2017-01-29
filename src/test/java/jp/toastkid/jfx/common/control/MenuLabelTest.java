/**
 *
 */
package jp.toastkid.jfx.common.control;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * {@link MenuLabel}'s test cases.
 *
 * @author Toast kid
 *
 */
public class MenuLabelTest extends ApplicationTest {

    /** Test object. */
    private MenuLabelTestController controller;

    /**
     * {@link MenuLabel#getAccelerator()} 's test method.
     */
    @Test
    public void test_getAccelerator() {
        assertEquals("Ctrl+T", controller.label.getAccelerator().getDisplayText());
    }

    /**
     * {@link MenuLabel#getOnAction()} 's test method.
     */
    @Test
    public void test_getOnAction() {
        controller.label.getOnAction().handle(new ActionEvent());
    }

    @Override
    public void start(final Stage stage) throws Exception {
        final FXMLLoader loader
            = new FXMLLoader(getClass().getClassLoader().getResource("scenes/MenuLabel.fxml"));
        stage.setScene(new Scene(loader.load()));;
        controller = loader.getController();
    }

}
