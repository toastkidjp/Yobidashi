package jp.toastkid.gui.jfx.wiki;

import java.util.Iterator;

import com.jfoenix.controls.JFXButton;
import com.sun.javafx.scene.control.skin.ContextMenuContent;
import com.sun.javafx.scene.control.skin.ContextMenuContent.MenuItemContainer;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;
import jp.toastkid.gui.jfx.common.control.NumberTextField;
import jp.toastkid.gui.jfx.common.control.Stopwatch;
import jp.toastkid.gui.jfx.dialog.AlertDialog;
import jp.toastkid.libs.utils.MathUtil;

/**
 * タブの全画面表示用.
 * @author Toast kid
 *
 */
public class FullScreen {

    /** stage. */
    private final Stage stage = new Stage();

    /** WebView. */
    private WebView webView;

    /** Width. */
    private final double width;
    /** Height. */
    private final double height;

    /**
     * width & height.
     * @param width
     * @param height
     */
    public FullScreen(final double width, final double height) {
        this.width = width;
        this.height = height;
    }

    /**
     * show full screen.
     */
    public void show(final String url) {

        initWebView();

        webView.getEngine().load(url);
        final AnchorPane ap = new AnchorPane();
        ap.getChildren().add(webView);
        AnchorPane.setTopAnchor(ap, 0.0);
        AnchorPane.setBottomAnchor(ap, 0.0);
        AnchorPane.setRightAnchor(ap, 0.0);
        AnchorPane.setLeftAnchor(ap, 0.0);

        final NumberTextField page = new NumberTextField();
        page.setOnAction(e -> {jump(page.getText());});

        final HBox timer = initTimer();
        final VBox vb = new VBox();
        vb.getChildren().addAll(ap, new HBox(){{getChildren().addAll(page, timer);}});
        final Scene scene = new Scene(vb);
        scene.setOnKeyTyped(e -> {
            if (e.getCode().equals(KeyCode.J)) {
                callJump();
            }
        });
        stage.setFullScreen(true);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * initialize timer components.
     * @return HBox.
     */
    private HBox initTimer() {
        final Stopwatch stopwatch = new Stopwatch();
        stopwatch.setTextFill(Color.NAVY);
        stopwatch.setStyle("-fx-font-size: 2em;");

        final Button start = new JFXButton("Start");
        start.setOnAction(eve -> {
            start.setText(stopwatch.isActive() ? "Start" : "Stop");
            stopwatch.start();
        });

        final Button reset = new JFXButton("Reset");
        reset.setOnAction(eve -> {stopwatch.reset();});

        return new HBox(stopwatch, start, reset);
    }

    /**
     * WebView を初期化.
     * @param url URL
     * @return WebView オブジェクト
     */
    private void initWebView() {
        webView = new WebView();
        webView.setPrefHeight(height);
        webView.setPrefWidth(width);
        webView.setOnContextMenuRequested(event -> {showContextMenu();});
        webView.setOnKeyTyped(value -> {
            if (value.getCode().equals(KeyCode.UNDEFINED)) {
                close();
            }
        });
    }

    /**
     * ジャンプするページ数を入力させる.
     * @param webView
     */
    private void callJump() {
        final NumberTextField num = new NumberTextField();
        new AlertDialog.Builder().setParent(stage.getScene().getWindow())
            .setTitle("ジャンプ")
            .setMessage("何ページ目に移動しますか？")
            .addControl(num)
            .setOnPositive("Jump", () -> { jump(num.getText());})
            .build().show();
    }

    /**
     * 指定したページにジャンプする.
     * @param webView WebView
     * @param num ページ番号(必ず数字になっているはず)
     */
    private void jump(final String num) {
        final WebEngine engine = webView.getEngine();
        engine.load(deleteFragment(engine.getLocation()) + "#/" + (MathUtil.parseOrZero(num) - 1));
    }

    /**
     * # 以下を消す.
     * @param url URL
     * @return # 以下を消したURL
     */
    private String deleteFragment(String url) {
        return url.contains("#/") ? url.substring(0, url.lastIndexOf("#/")) : url;
    }

    /**
     * show Context(Popup) menu.
     * @see <a href="http://stackoverflow.com/questions/27047447/
     *customized-context-menu-on-javafx-webview-webengine">
     *customized-context-menu-on-javafx-webview-webengine</a>
     */
    private PopupWindow showContextMenu() {
        @SuppressWarnings("deprecation")
        final Iterator<Window> windows = Window.impl_getWindows();

        while (windows.hasNext()) {
            final Window window = windows.next();

            if (!(window instanceof ContextMenu
                    && window.getScene() != null && window.getScene().getRoot() != null)) {
                return null;
            }

            final Parent root = window.getScene().getRoot();

            // access to context menu content
            if(root.getChildrenUnmodifiable().size() <= 0) {
                return null;
            }
            final Node popup = root.getChildrenUnmodifiable().get(0);
            if(popup.lookup(".context-menu") == null) {
                return null;
            }
            final Node bridge = popup.lookup(".context-menu");
            final ContextMenuContent cmc
                = (ContextMenuContent)((Parent)bridge).getChildrenUnmodifiable().get(0);

            final VBox itemsContainer = cmc.getItemsContainer();
            for (final Node n: itemsContainer.getChildren()) {
                final MenuItemContainer item = (MenuItemContainer) n;
                item.getItem().setText(item.getItem().getText());
            }

            // adding new item:
            final MenuItem jump = new MenuItem("ジャンプ"){{
                setOnAction(event -> {callJump();});
            }};

            final MenuItem length = new MenuItem("終了"){{
                setOnAction(event -> {close();});
            }};

            // add new item:
            cmc.getItemsContainer().getChildren().addAll(
                    cmc.new MenuItemContainer(jump),
                    cmc.new MenuItemContainer(length)
                );

            return (PopupWindow)window;
        }
        return null;
    }

    /**
     * close this window.
     */
    private void close() {
        this.stage.setFullScreen(false);
        this.stage.close();
    }
}
