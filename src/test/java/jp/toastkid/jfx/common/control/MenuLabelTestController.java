package jp.toastkid.jfx.common.control;

import static org.junit.Assert.assertTrue;

import javafx.fxml.FXML;

/**
 * Test Controller.
 * @author Toast kid
 *
 */
public class MenuLabelTestController {

    /** Label. */
    @FXML
    public MenuLabel label;

    /**
     * OK.
     */
    @FXML
    private void test() {
        assertTrue(true);
    }
}
