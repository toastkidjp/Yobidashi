package jp.toastkid.yobidashi;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * {@link Main}'s test case.
 *
 * @author Toast kid
 *
 */
public class MainTest extends ApplicationTest {

    /** Main. */
    private Main main;

    /**
     * Test of {@link Main#closeApplication(Stage)}.
     */
    @Test
    public void test() {
        Platform.runLater(() -> {
            try {
                main.stop();
            } catch (final Exception e) {
                e.printStackTrace();
                fail();
            }
        });
    }

    @Override
    public void start(final Stage stage) throws Exception {
        main = new Main();
        main.start(stage);
    }

}
