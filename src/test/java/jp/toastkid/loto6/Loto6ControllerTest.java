package jp.toastkid.loto6;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jp.toastkid.libs.utils.Whitebox;

/**
 * {@link Loto6Controller}'s test cases.
 *
 * @author Toast kid
 *
 */
public class Loto6ControllerTest extends ApplicationTest {

    /**
     * Check {@link Loto6Controller#initialize(URL, ResourceBundle)}.
     * @throws MalformedURLException
     */
    @Test
    public void testInitialize() throws MalformedURLException {
        final Loto6Controller loto6Controller = new Loto6Controller();
        loto6Controller.initialize(new URL("https://localhost:8080/"), null);
    }

    /**
     * Check {@link Loto6Controller#generate()}.
     */
    @Test
    public void testGenerate() {
        final Loto6Controller loto6Controller = new Loto6Controller();
        final Label result = setUpTarget(loto6Controller);
        loto6Controller.generate();
        verify(result).setText(anyString());
    }

    /**
     * Set uo target controller.
     * @param loto6Controller
     * @return
     */
    private Label setUpTarget(final Loto6Controller loto6Controller) {
        final Label result = mock(Label.class);
        Whitebox.setInternalState(loto6Controller, "result", result);
        Whitebox.setInternalState(loto6Controller, "inputWord", new TextField());
        return result;
    }

    @Override
    public void start(Stage stage) throws Exception {
        // NOP.
    }

}
