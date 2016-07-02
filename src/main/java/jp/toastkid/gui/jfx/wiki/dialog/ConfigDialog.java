package jp.toastkid.gui.jfx.wiki.dialog;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import jp.toastkid.libs.utils.FileUtil;

/**
 * WikiCliend の設定ダイアログ
 * @author Toast kid
 * @see <a href="http://d.hatena.ne.jp/aoe-tk/20130526/1369577773">
 * JavaFX2.2でダイアログを作る方法</a>
 *
 */
public final class ConfigDialog  extends Application {
    /** FXML ファイルのパス. */
    private static final String DIALOG_FXML = "public/scenes/ConfigDialog.fxml";
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
            = new FXMLLoader(FileUtil.getUrl(DIALOG_FXML));
        try {
            if (scene == null) {
                scene = new Scene(loader.load());
            }
        } catch (final IOException e) {
            e.printStackTrace();
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