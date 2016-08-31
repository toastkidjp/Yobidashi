package jp.toastkid.dialog;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.wiki.models.Defines;

/**
 * 簡単な確認ダイアログ.
 * JavaFX 8u40以前でも使用可能.
 *
 * @author Toast kid
 * @see <a href="http://d.hatena.ne.jp/aoe-tk/20130526/1369577773">
 * JavaFX2.2でダイアログを作る方法</a>
 */
public final class AlertDialog extends Application {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AlertDialog.class);

    /** FXML ファイルのパス. */
    private static final String DIALOG_FXML = Defines.SCENE_DIR + "/AlertDialog.fxml";

    /** コントローラオブジェクト. */
    private AlertDialogController controller;

    /** Stage. */
    private Stage stage;

    /** Scene. */
    private Scene scene = null;

    /**
     * {@link AlertDialog}'s builder.
     * @author Toast kid
     *
     */
    public static class Builder {
        private final Window parent;
        private String title;
        private String message;

        private String negaText;
        private Runnable negaAction;

        private String posiText;
        private Runnable posiAction;

        private String neutralText;
        private Runnable neutralAction;

        private final MutableList<Node> cntrs;

        public Builder() {
            this(null);
        }

        public Builder(final Window parent) {
            this.parent = parent;
            cntrs = Lists.mutable.empty();
        }

        public Builder setTitle(final String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(final String message) {
            this.message = message;
            return this;
        }

        public Builder setOnPositive(final String title, final Runnable act) {
            this.posiText   = title;
            this.posiAction = act;
            return this;
        }

        public Builder setOnNeutral(final String title, final Runnable act) {
            this.neutralText   = title;
            this.neutralAction = act;
            return this;
        }

        public Builder setOnNegative(final String title, final Runnable act) {
            this.negaText   = title;
            this.negaAction = act;
            return this;
        }

        public Builder addControl(final Node... cntrs) {
            this.cntrs.withAll(ArrayAdapter.adapt(cntrs));
            return this;
        }

        public AlertDialog build() {
            return new AlertDialog(this);
        }
    }

    /**
     * Constructor.
     * @param parent
     */
    public AlertDialog(final Builder b) {
        loadDialog(b.parent);

        // set Builder's parameter on Controller.
        controller.setOnPositive(b.posiText, b.posiAction);
        controller.setOnNegative(b.negaText, b.negaAction);
        controller.setOnNeutral(b.neutralText, b.neutralAction);

        if (StringUtils.isNotBlank(b.message)) {
            controller.setMessage(b.message);
        }

        if (b.cntrs != null && !b.cntrs.isEmpty()) {
            controller.addAll(b.cntrs);
        }

        stage.setTitle(b.title);
        controller.setTitle(b.title);

        if (b.parent == null || b.parent.getScene().getStylesheets() == null) {
            return;
        }
        // StyleSheet をコピーする.
        this.scene.getStylesheets().addAll(b.parent.getScene().getStylesheets());
    }

    /**
     * FXML からロードする.
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
            LOGGER.error("Error", e);;
        }
        controller = loader.getController();
        stage = new Stage(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        if (window != null) {
            stage.initOwner(window);
        }
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
    }

    public void show() {
        stage.showAndWait();
    }

    @Override
    public void start(final Stage arg0) throws Exception {
        /*
        showMessageDialog("title", "message", "test");
        showInputDialog("title", "message", "test", null);
        System.out.println("input - " + getInput());
        showInputDialog("title", "message", null, "checksi");
        System.out.println("check - " + isChecked());
        showInputDialog("title", "message", null, null);
        System.out.println("check - " + isChecked());
        //*/
    }

    /**
     * 渡されたコントロールをセットする。
     * @param control
     */
    public AlertDialog setControl(final Node control) {
        controller.add(control);
        return this;
    }

    /**
     * main method.
     * @param args
     */
    public static void main(final String[] args) {
        Application.launch(AlertDialog.class);
    }

    /**
     * show simple message dialog.
     * @param parent
     * @param title
     * @param message
     */
    public static void showMessage(final Window parent, final String title, final String message) {
        showMessage(parent, title, message, null);
    }

    /**
     * show simple message dialog.
     * @param parent
     * @param title
     * @param message
     * @param detail
     */
    public static void showMessage(
            final Window parent, final String title, final String message, final String detail) {
        final Builder builder = new AlertDialog.Builder(parent)
                .setTitle(title)
                .setMessage(message);
        if (StringUtils.isNotEmpty(detail)) {
            final TextArea textArea = new TextArea(){{
                setEditable(false);
                setText(detail);
            }};
            builder.addControl(textArea);
        }
        builder.build().show();
    }
}