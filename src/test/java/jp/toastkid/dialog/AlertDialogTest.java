package jp.toastkid.dialog;

import java.lang.reflect.Method;

import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * {@link AlertDialog}'s test
 *
 * @author Toast kid
 *
 */
public class AlertDialogTest extends ApplicationTest {

    /** Stage. */
    private Stage stage;

    /** Test target dialog. */
    private AlertDialog dialog;

    /** Text field. */
    private TextField textField;

    /**
     * {@link AlertDialog.Builder#build()}'s test.
     */
    @Test
    public void test_build() {
        Platform.runLater(() -> {
            textField = new TextField();
            dialog = new AlertDialog.Builder(stage.getOwner())
                    .setTitle("Title")
                    .setMessage("Message")
                    .setOnPositive("OK", () -> {})
                    .setOnNeutral("Nomal", () -> {})
                    .setOnNegative("No", () -> {})
                    .addControl(textField)
                    .build();
            dialog.show();
            final AlertDialogController controller
                = (AlertDialogController) Whitebox.getInternalState(dialog, "controller");
            try {
                final Method m = controller.getClass().getMethod("close");
                m.invoke(null);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Test of {@link AlertDialog#showMessage(javafx.stage.Window, String, String)}.
     */
    @Test
    public void testShowMessageWindowStringString() {
        Platform.runLater(() -> {
            AlertDialog.showMessage(stage.getOwner(), "Title", "message");
        });
    }

    /**
     * Test of {@link AlertDialog#showMessage(javafx.stage.Window, String, String, String)}.
     */
    @Test
    public void testShowMessageWindowStringStringString() {
        Platform.runLater(() ->
            AlertDialog.showMessage(stage.getOwner(), "Title", "message", "detail"));
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
    }

}
