package jp.toastkid.dialog;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * {@link ProgressDialog}'s test case.
 *
 * @author Toast kid
 *
 */
public class ProgressDialogTest extends ApplicationTest {

    /** ProgressDialog. */
    private ProgressDialog dialog;

    /**
     * Test of {@link ProgressDialog#start(Stage)}.
     * @throws Exception
     */
    @Test
    public void testStartStage() throws Exception {
        dialog.stop();
        dialog.close();
    }

    @Override
    public void start(Stage stage) throws Exception {
        dialog = new ProgressDialog.Builder().setScene(new Scene(new HBox()))
                .setCommand(new Task<Integer>(){
                    @Override
                    protected Integer call() throws Exception {
                        assertTrue(true);
                        return 100;
                    }})
                .build();
        dialog.start(stage);
    }

}
