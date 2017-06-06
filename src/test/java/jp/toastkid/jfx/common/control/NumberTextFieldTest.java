package jp.toastkid.jfx.common.control;

import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertEquals;

/**
 * Test of {@link NumberTextField}.
 *
 * @author Toast kid
 *
 */
public class NumberTextFieldTest extends ApplicationTest {

    /** test object. */
    private NumberTextField numberTextField;

    /**
     * Set up test object.
     */
    @Before
    public void setUp() {
        numberTextField = new NumberTextField();
    }

    /**
     * {@link NumberTextField#replaceSelection(String)}.
     */
    @Test
    public void test_replaceSelection() {
        numberTextField.replaceSelection("a12bc33");
        assertEquals("", numberTextField.getText());
        numberTextField.replaceSelection("1233");
        assertEquals("1233", numberTextField.getText());
    }

    /**
     * {@link NumberTextField#replaceText(javafx.scene.control.IndexRange, String)}.
     */
    @Test
    public void test_replaceText() {
        numberTextField.setText("1234567890");
        numberTextField.replaceText(2, 3, "tomato");
        assertEquals("1234567890", numberTextField.getText());
        numberTextField.replaceText(2, 3, "12345");
        assertEquals("12123454567890", numberTextField.getText());
    }

    /**
     * {@link NumberTextField#intValue()}.
     */
    @Test
    public void test_intValue() {
        numberTextField.setText(null);
        assertEquals(0, numberTextField.intValue());
        numberTextField.setText("");
        assertEquals(0, numberTextField.intValue());
        numberTextField.setText(" ");
        assertEquals(0, numberTextField.intValue());
        numberTextField.setText("0");
        assertEquals(0, numberTextField.intValue());
        numberTextField.setText("1");
        assertEquals(1, numberTextField.intValue());
        numberTextField.setText("-1");
        assertEquals(-1, numberTextField.intValue());
    }

    /**
     * {@link NumberTextField#longValue()}.
     */
    @Test
    public void test_longValue() {
        numberTextField.setText(null);
        assertEquals(0L, numberTextField.longValue());
        numberTextField.setText("");
        assertEquals(0L, numberTextField.longValue());
        numberTextField.setText(" ");
        assertEquals(0L, numberTextField.longValue());
        numberTextField.setText("0");
        assertEquals(0L, numberTextField.longValue());
        numberTextField.setText("1");
        assertEquals(1L, numberTextField.longValue());
        numberTextField.setText("-1");
        assertEquals(-1L, numberTextField.longValue());
    }

    /**
     * {@link NumberTextField#doubleValue()}.
     */
    @Test
    public void test_doubleValue() {
        numberTextField.setText(null);
        assertEquals(0, numberTextField.doubleValue(), 0.1d);
        numberTextField.setText("");
        assertEquals(0, numberTextField.doubleValue(), 0.1d);
        numberTextField.setText(" ");
        assertEquals(0, numberTextField.doubleValue(), 0.1d);
        numberTextField.setText("0");
        assertEquals(0, numberTextField.doubleValue(), 0.1d);
        numberTextField.setText("1");
        assertEquals(1, numberTextField.doubleValue(), 0.1d);
        numberTextField.setText("-1");
        assertEquals(-1, numberTextField.doubleValue(), 0.1d);
        numberTextField.setText("1.1");
        assertEquals(1.1, numberTextField.doubleValue(), 0.1d);
        numberTextField.setText("-1.1");
        assertEquals(-1.1, numberTextField.doubleValue(), 0.1d);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // NOP
    }

}
