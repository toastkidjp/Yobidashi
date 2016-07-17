package jp.toastkid.gui.jfx.wordcloud;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;

/**
 * コントローラ.
 * @author Toast kid
 */
public final class Controller implements Initializable {

    /** parent view for use JFXMasonryPane2. */
    @FXML
    public ScrollPane parent;

    /** 表示領域. */
    @FXML
    public JFXMasonryPane2 canvas;

    /**
     * close this dialog.
     */
    @FXML
    private void close() {
        canvas.getScene().getWindow().hide();
    }

    @Override
    public void initialize(final URL arg0, final ResourceBundle arg1) {
        canvas.setOnMouseClicked(eve -> {Controller.this.close();});
    }

}