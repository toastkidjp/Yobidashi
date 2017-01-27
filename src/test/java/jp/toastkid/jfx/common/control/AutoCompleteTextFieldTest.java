package jp.toastkid.jfx.common.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.stage.Stage;

/**
 * {@link AutoCompleteTextField}'s test case.
 *
 * @author Toast kid
 *
 */
public class AutoCompleteTextFieldTest extends ApplicationTest {

    /** Test object. */
    private AutoCompleteTextField textField;

    /**
     * Test of {@link AutoCompleteTextField}.
     */
    @Test
    public void test() {
        textField.setText("0");
        textField.getEntries().addAll(Arrays.asList("Apple", "Bacon", "Cheeze"));
        textField.setText("App");
        textField.requestFocus();
        final ContextMenu entriesPopup
            = (ContextMenu) Whitebox.getInternalState(textField, "entriesPopup");
        entriesPopup.getItems().get(0).fire();
        assertEquals("Apple", textField.getText());
        textField.setText("");
        assertTrue(textField.getText().isEmpty());
    }

    @Override
    public void start(final Stage stage) throws Exception {
        textField = new AutoCompleteTextField();
        stage.setScene(new Scene(textField));
    }

}
