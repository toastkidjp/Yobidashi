package jp.toastkid.wiki.dialog;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import jp.toastkid.wiki.models.Defines;

/**
 * WikiCliend の設定ダイアログ
 * @author Toast kid
 * @see <a href="http://d.hatena.ne.jp/aoe-tk/20130526/1369577773">
 * JavaFX2.2でダイアログを作る方法</a>
 *
 */
public final class ConfigDialog  extends Application {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigDialog.class);

    /** FXML ファイルのパス. */
    private static final String FXML_PATH = Defines.SCENE_DIR + "/ConfigDialog.fxml";

    /** コントローラオブジェクト. */
    private ConfigDialogController controller;

    /** Stage. */
    private Stage dialog;

    /** Scene. */
    private Scene scene = null;

    /**
     * シーンファイルをロードしておく.
     */
    public ConfigDialog(final Window window) {
        loadDialog(window);
        controller.loadConfig();
        // copy StyleSheet.
        this.scene.getStylesheets().addAll(window.getScene().getStylesheets());
    }

    /**
     * Load scene from FXML.
     * @return Parent オブジェクト
     */
    private final void loadDialog(final Window window) {
        final FXMLLoader loader
            = new FXMLLoader(getClass().getClassLoader().getResource(FXML_PATH));
        try {
            if (scene == null) {
                scene = new Scene(loader.load());
            }
        } catch (final IOException e) {
            LOGGER.error("Caught Error.", e);
        }
        controller = loader.getController();
        dialog = new Stage(StageStyle.UTILITY);
        dialog.setScene(scene);
        dialog.initOwner(window);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setResizable(false);
        dialog.setTitle("Config");
    }

    /**
     * 入力を受け付けるダイアログを表示する.
     * This method is stopping main thread until when the dialog close.
     * @param title  ダイアログのタイトル
     * @param msg    ダイアログのメッセージ
     * @param defaultInput デフォルト入力、null を指定した場合はテキストフィールドを表示しない
     * @param checkText チェックボックスの文字列、null か空白を指定した時は表示しない
     * @return input 入力文字列
     */
    public final void showConfigDialog() {
        dialog.showAndWait();
    }

    @Override
    public void start(final Stage arg0) throws Exception {
        showConfigDialog();
    }

    /**
     *
     * @param args
     */
    public static void main(final String[] args) {
        Application.launch(ConfigDialog.class);
    }
}