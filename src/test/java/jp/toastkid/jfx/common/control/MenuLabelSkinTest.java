package jp.toastkid.jfx.common.control;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

/**
 * {@link MenuLabelSkin}'s test case.
 *
 * @author Toast kid
 *
 */
public class MenuLabelSkinTest extends ApplicationTest {

    /** Label. */
    private MenuLabel label;

    /**
     * Test empty skin.
     */
    @Test
    public void test_empty() {
        final MenuLabelSkin skin = new MenuLabelSkin(new MenuLabel());
        skin.handleControlPropertyChanged("LABEL_FOR");
    }

    /**
     * Test label with accelerator.
     */
    @Test
    public void test() {
        label.setAccelerator(KeyCombination.keyCombination("Ctrl+L"));
        new MenuLabelSkin(label);
        assertEquals("Test    |    Ctrl+L", label.getText());
    }

    @Override
    public void start(Stage stage) throws Exception {
        label = new MenuLabel("Test");
        stage.setScene(new Scene(label));
    }

}
