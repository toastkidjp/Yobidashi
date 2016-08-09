package jp.toastkid.gui.jfx.wiki;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;

import com.jfoenix.controls.JFXButton;
import com.sun.javafx.scene.control.skin.ContextMenuContent;
import com.sun.javafx.scene.control.skin.ContextMenuContent.MenuItemContainer;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
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
import jp.toastkid.gui.jfx.wiki.models.Config;
import jp.toastkid.gui.jfx.wiki.models.Defines;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.libs.utils.MathUtil;

/**
 * For appear article full screen.
 * @author Toast kid
 *
 */
public class FullScreen {

    /** Full screen key. */
    private static final KeyCodeCombination FULL_SCREEN_KEY = new KeyCodeCombination(KeyCode.F11);

    /** Reload key. */
    private static final KeyCodeCombination RELOAD_KEY = new KeyCodeCombination(KeyCode.F5);

    /** Jump key. */
    private static final KeyCodeCombination JUMP_KEY = new KeyCodeCombination(KeyCode.J, KeyCombination.CONTROL_DOWN);

    /** path to theme's css dir. */
    private static final String THEME_DIR = Defines.ASSETS_DIR + "/javascripts/reveal/css/theme/";

    /** css file name filter. */
    private static final FilenameFilter CSS_FILTER = (dir, name) -> name.endsWith(".css");

    /** stage. */
    private final Stage stage = new Stage();

    /** WebView. */
    private final WebView webView;

    /** Style sheets. */
    private final ComboBox<String> styles;

    /** use for reloading. */
    private String title;

    /**
     * width & height.
     * @param width
     * @param height
     */
    public FullScreen(final double width, final double height) {
        webView = new WebView();
        styles  = new ComboBox<>();
        initWebView();

        final AnchorPane ap = new AnchorPane();
        ap.getChildren().add(webView);
        AnchorPane.setTopAnchor(ap, 0.0);
        AnchorPane.setBottomAnchor(ap, 0.0);
        AnchorPane.setRightAnchor(ap, 0.0);
        AnchorPane.setLeftAnchor(ap, 0.0);
        webView.setPrefWidth(width);
        webView.setPrefHeight(height);

        final NumberTextField page = new NumberTextField();
        page.setOnAction(e -> jump(page.getText()));

        // 初期値セット
        final String theme = Config.get(Config.Key.SLIDE_THEME);
        styles.getSelectionModel().select(StringUtils.isNotBlank(theme) ? theme : "white");

        final Scene scene = new Scene(new VBox(ap, new HBox(page, makeFooter())));
        scene.setOnKeyTyped(e -> {
            if (e.getCode().equals(KeyCode.J)) {
                callJump();
            }
        });
        final ObservableMap<KeyCombination, Runnable> accelerators = scene.getAccelerators();
        accelerators.put(JUMP_KEY,        () -> callJump());
        accelerators.put(RELOAD_KEY,      () -> webView.getEngine().reload());
        accelerators.put(FULL_SCREEN_KEY, () -> stage.setFullScreen(true));
        stage.setScene(scene);
    }

    /**
     * show full screen with url.
     * @param url
     */
    public void show(final String url) {
        webView.getEngine().load(url);
        show();
    }

    /**
     * show full screen.
     */
    public void show() {
        stage.setFullScreen(true);
        stage.show();
    }

    /**
     * initialize timer components.
     * @return HBox.
     */
    private HBox makeFooter() {
        final Stopwatch stopwatch = new Stopwatch();
        stopwatch.setTextFill(Color.NAVY);
        stopwatch.setStyle("-fx-font-size: 2em;");

        final Button start = new JFXButton("Start");
        start.setOnAction(eve -> {
            start.setText(stopwatch.isActive() ? "Start" : "Stop");
            stopwatch.start();
        });

        final Button reset = new JFXButton("Reset");
        reset.setOnAction(eve -> stopwatch.reset());

        return new HBox(stopwatch, start, reset, makeThemeChooser());
    }

    /**
     * make theme chooser.
     * @return ComboBox
     */
    private ComboBox<String> makeThemeChooser() {
        final ObservableList<String> items = styles.getItems();
        items.addAll(
                ArrayAdapter.adapt(new File(THEME_DIR).list(CSS_FILTER))
                    .collect(name -> FileUtil.removeExtension(name))
                    );
        styles.setOnAction(event -> setTheme(styles.getSelectionModel().getSelectedItem()));
        return styles;
    }

    /**
     * set theme css.
     * @param theme css name without extension.
     */
    private void setTheme(final String theme) {
        webView.getEngine().executeScript(
                String.format("document.getElementById('theme').href = '%s%s.css';", THEME_DIR, theme));
        Config.store(Config.Key.SLIDE_THEME, theme);
    }

    /**
     * Init WebView.
     * @param url URL
     * @return WebView Obbject
     */
    private void initWebView() {
        webView.setOnContextMenuRequested(event -> showContextMenu());
    }

    /**
     * Specify target page.
     * @param webView
     */
    private void callJump() {
        final NumberTextField num = new NumberTextField();
        new AlertDialog.Builder().setParent(stage.getScene().getWindow())
            .setTitle("Jump to")
            .setMessage("何ページ目に移動しますか？")
            .addControl(num)
            .setOnPositive("Jump", () -> jump(num.getText()))
            .build().show();
    }

    /**
     * Jump to specified page.
     * @param webView WebView
     * @param num ページ番号(必ず数字になっているはず)
     */
    private void jump(final String num) {
        final WebEngine engine = webView.getEngine();
        engine.load(deleteFragment(engine.getLocation()) + "#/" + (MathUtil.parseOrZero(num) - 1));
    }

    /**
     * Remove under #.
     * @param url URL
     * @return # 以下を消したURL
     */
    private String deleteFragment(final String url) {
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
            final MenuItem jump = new MenuItem("Jump"){{
                setOnAction(event -> callJump());
            }};

            // jump
            cmc.getItemsContainer().getChildren().add(
                    cmc.new MenuItemContainer(jump)
                    );

            // quit full screen.
            if (!stage.isFullScreen()) {
                final MenuItem fs = new MenuItem("Full Screen"){{
                    setOnAction(event -> stage.setFullScreen(true));
                }};
                cmc.getItemsContainer().getChildren().add(cmc.new MenuItemContainer(fs));
            } else {
                final MenuItem fs = new MenuItem("Quit Full Screen"){{
                    setOnAction(event -> stage.setFullScreen(false));
                }};
                cmc.getItemsContainer().getChildren().add(cmc.new MenuItemContainer(fs));
            }

            // Close window.
            final MenuItem length = new MenuItem("Close"){{
                setOnAction(event -> close());
            }};
            cmc.getItemsContainer().getChildren().add(
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

    /**
     * set to this title.
     * @param title
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * get this title.
     * @return title
     */
    public CharSequence getTitle() {
        return this.title;
    }
}
