package jp.toastkid.gui.jfx.wordcloud;

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
 * Word cloud.
 *
 * @author Toast kid
 */
public final class Main extends Application {
    /** FXML ファイルのパス. */
    private static final String FXML = "public/scenes/WordCloud.fxml";
    /** コントローラオブジェクト. */
    private Controller controller;
    /** Stage. */
    private Stage stage;
    /** Scene. */
    private Scene scene = null;

    /**
     * Constructor.
     * @param parent
     */
    public Main() {
        loadDialog(null);
    }

    /**
     * FXML からロードする.
     * @return Parent オブジェクト
     */
    private final void loadDialog(final Window window) {
        final FXMLLoader loader
            = new FXMLLoader(FileUtil.getUrl(FXML));
        try {
            if (scene == null) {
                scene = new Scene(loader.load());
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        controller = loader.getController();
        stage = new Stage(StageStyle.UTILITY);
        stage.setScene(scene);
        if (window != null) {
            stage.initOwner(window);
        }
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        FxWordCloud.draw(controller.canvas, "sample");
    }

    public void show() {
        stage.showAndWait();
    }

    @Override
    public void start(final Stage arg0) throws Exception {
        show();
    }
    /**
     *
     * @param args
     */
    public static void main(final String[] args) {
        Application.launch(Main.class);
    }
}