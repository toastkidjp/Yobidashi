package jp.toastkid.dialog;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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
    protected JFXSpinner pin;
    @FXML
    protected Label label;
    @FXML
    protected VBox background;
    @FXML
    protected JFXSnackbar snackbar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pb.setProgress(0.0d);
    }


}
