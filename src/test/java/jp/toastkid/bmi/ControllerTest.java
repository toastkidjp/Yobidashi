package jp.toastkid.bmi;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import org.testfx.framework.junit.ApplicationTest;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * BMI calculator's test.
 *
 * @author Toast kid
 *
 */
public class ControllerTest extends ApplicationTest {

    /** Controller. */
    private Controller controller;

    /** Height input. */
    private TextField height;

    /** Weight input. */
    private TextField weight;

    /** Result. */
    private TextArea result;

    /**
     * Set up controller.
     * @throws IOException
     */
    @Before
    public void setUp() throws IOException {
        if (controller != null) {
            result.clear();
            return;
        }
        final FXMLLoader loader = new FXMLLoader(
                ControllerTest.class.getClassLoader().getResource("scenes/Bmi.fxml"));
        loader.load();
        controller = loader.getController();
        height = (TextField) Whitebox.getInternalState(controller, "height");
        weight = (TextField) Whitebox.getInternalState(controller, "weight");
        result = (TextArea) Whitebox.getInternalState(controller, "result");
    }

    /**
     * Simple running.
     */
    @Test
    public void test() {
        height.setText("175");
        weight.setText("58");
        weight.fireEvent(new ActionEvent());

        final String lineSeparator = "\n";
        assertEquals(
                "BMI値 = 18.9" + lineSeparator + "標準体重 = 67.4" + lineSeparator + "標準です。",
                result.getText()
                );
    }

    /**
     * Check height empty case.
     */
    @Test
    public void testNullHeight() {
        height.setText(null);
        weight.setText("58");
        weight.fireEvent(new ActionEvent());

        checkDefaultResult();
    }

    /**
     * Check weight empty case.
     */
    @Test
    public void testNullWeight() {
        height.setText("175");
        weight.setText(null);
        weight.fireEvent(new ActionEvent());

        checkDefaultResult();
    }

    /**
     * Check height empty case.
     */
    @Test
    public void testEmptyHeight() {
        height.setText("");
        weight.setText("58");
        weight.fireEvent(new ActionEvent());

        checkDefaultResult();
    }

    /**
     * Check weight empty case.
     */
    @Test
    public void testIllegularWeight() {
        height.setText("175");
        weight.setText("test");
        weight.fireEvent(new ActionEvent());

        checkDefaultResult();
    }


    /**
     * Check height empty case.
     */
    @Test
    public void testIllegularHeight() {
        height.setText("test");
        weight.setText("58");
        weight.fireEvent(new ActionEvent());

        checkDefaultResult();
    }

    /**
     * Check weight empty case.
     */
    @Test
    public void testEmptyWeight() {
        height.setText("175");
        weight.setText("");
        weight.fireEvent(new ActionEvent());

        checkDefaultResult();
    }

    /**
     * Check is default result.
     */
    private void checkDefaultResult() {
        assertEquals("標準値は22です。", result.getText());
    }

    /**
     * NOP.
     */
    @Test
    public void test_initialize() {
        controller.initialize(null, null);;
    }

    @Override
    public void start(Stage stage) throws Exception {
        // TNOP.
    }

}
