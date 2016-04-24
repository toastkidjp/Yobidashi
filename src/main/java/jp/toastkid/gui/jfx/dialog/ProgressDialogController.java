package jp.toastkid.gui.jfx.dialog;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

/**
 *
 * @author Toast kid
 *
 */
public class ProgressDialogController implements Initializable {

    @FXML
    protected ProgressBar pb;
    @FXML
    protected ProgressIndicator pin;
    @FXML
    protected Label label;
    @FXML
    protected VBox background;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pb.setProgress(0.0d);
        pin.setProgress(0.0d);
    }


}
