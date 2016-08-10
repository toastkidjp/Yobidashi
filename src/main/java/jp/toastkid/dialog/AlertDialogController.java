package jp.toastkid.dialog;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * 簡単な確認ダイアログのコントローラ.
 * @author Toast kid
 * @see <a href="http://d.hatena.ne.jp/aoe-tk/20130526/1369577773">
 * JavaFX2.2でダイアログを作る方法</a>
 */
public final class AlertDialogController implements Initializable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AlertDialogController.class);

    /** メッセージラベル. */
    @FXML
    public Label message;

    /** Control を入れる VBox. */
    @FXML
    public VBox inputBox;

    /** OK button. */
    @FXML
    private Button posi;

    /** Cancel button. */
    @FXML
    private Button neutral;

    /** No button. */
    @FXML
    private Button nega;

    /**
     * OK ボタンを押した時の動作を指定.
     * @param text
     * @param action
     */
    public void setOnPositive(final String text, final Action action) {
        setOnActionToButton(posi, text, action);
    }

    /**
     * Neutral ボタンを押した時の動作を指定.
     * @param text
     * @param action
     */
    public void setOnNeutral(final String text, final Action action) {
        if (StringUtils.isEmpty(text)) {
            return;
        }
        neutral.setVisible(true);
        setOnActionToButton(neutral, text, action);
    }

    /**
     * NO ボタンを押した時の動作を指定.
     * @param text
     * @param action
     */
    public void setOnNegative(final String text, final Action action) {
        setOnActionToButton(nega, text, action);
    }

    /**
     * Button に Action を登録.
     * @param b Button object
     * @param text text
     * @param action action
     */
    private void setOnActionToButton(final Button b, final String text, final Action action) {
        if (StringUtils.isNotBlank(text)) {
            b.setText(text);
        }

        EventHandler<ActionEvent> eventHandler = null;
        if (action != null) {
            eventHandler = eve -> {
                try {
                    action.doAction();
                    this.close();
                } catch (final RuntimeException e) {
                    AlertDialog.showMessage(message.getScene().getWindow(), "Error!!!", e.getMessage());
                    LOGGER.error("Caught error.", e);
                }
            };
        } else {
            eventHandler = eve -> {this.close();};
        }
        b.setOnAction(eventHandler);
    }

    /**
     * close this dialog.
     */
    @FXML
    private void close() {
        message.getScene().getWindow().hide();
    }

    @Override
    public void initialize(final URL arg0, final ResourceBundle arg1) {
        neutral.setOnMouseClicked(eve -> {this.close();});
    }
}