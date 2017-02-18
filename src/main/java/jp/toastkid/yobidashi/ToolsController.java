package jp.toastkid.yobidashi;

import org.eclipse.collections.impl.utility.ArrayIterate;

import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import jp.toastkid.article.control.UserAgent;
import jp.toastkid.chart.ChartPane;
import jp.toastkid.chart.ChartPane.Category;
import jp.toastkid.jfx.common.control.NumberTextField;
import jp.toastkid.yobidashi.message.ContentTabMessage;
import jp.toastkid.yobidashi.message.FontMessage;
import jp.toastkid.yobidashi.message.Message;
import jp.toastkid.yobidashi.message.UserAgentMessage;
import jp.toastkid.yobidashi.models.Config;
import jp.toastkid.yobidashi.models.Config.Key;
import reactor.core.Cancellation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.TopicProcessor;
import reactor.core.scheduler.Schedulers;

/**
 * Right side tools controller.
 * @author Toast kid
 *
 */
public class ToolsController {

    /** Zoom increment keyboard shortcut. */
    private static final KeyCodeCombination ZOOM_INCREMENT
        = new KeyCodeCombination(KeyCode.SEMICOLON, KeyCombination.CONTROL_DOWN);

    /** Zoom decrement keyboard shortcut. */
    private static final KeyCodeCombination ZOOM_DECREMENT
        = new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN);

    /** Draw chart shortcut. */
    private static final KeyCombination DRAW_CHART
        = new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN);

    /** Root pane. */
    @FXML
    private Pane root;

    /** グラフ種別セレクタ. */
    @SuppressWarnings("rawtypes")
    @FXML
    private ComboBox graphKind;

    /** 月セレクタ. */
    @FXML
    private ComboBox<String> month;

    /** Zoom Controller. */
    @FXML
    private Slider zoom;

    /** Specify zoom rate. */
    @FXML
    private TextField zoomInput;

    /** Font family */
    @FXML
    private ComboBox<String> fontFamily;

    /** Font size. */
    @FXML
    private NumberTextField fontSize;

    /** UserAgent's selector. */
    @FXML
    private ComboBox<UserAgent> ua;

    /** Message sender. */
    private final TopicProcessor<Message> messenger = TopicProcessor.create();

    /** Config. */
    private Config conf;

    /**
     * Draw chart.
     */
    @FXML
    private final void drawChart() {
        final String title = graphKind.getSelectionModel().getSelectedItem().toString();
        final Pane content = ChartPane.make(
                conf.get(Key.ARTICLE_DIR),
                Category.findByText(title),
                "日記" + month.getSelectionModel().getSelectedItem().toString()
                );
        messenger.onNext(ContentTabMessage.make(title, content));
    }

    /**
     * Set zoom rate 1.
     */
    @FXML
    private final void callDefaultZoom() {
        zoom.setValue(1.0);;
    }

    /**
     * Initialize chart tool.
     */
    private final void initChartTool() {
        @SuppressWarnings("unused")
        final ObservableList<String> items = month.<String>getItems();
        items.addAll(ChartPane.readMonths());
        graphKind.getSelectionModel().select(0);
        month.getSelectionModel().select(items.size() - 1);
    }

    /**
     * Initialize zoom.
     */
    private void initZoom() {
        zoomInput.textProperty().bind(zoom.valueProperty().asString());
    }

    /**
     * Set stage for controlling window.
     * @param stage
     * @param controller
     */
    protected void init(final Stage stage) {
        final ObservableMap<KeyCombination, Runnable> accelerators
            = stage.getScene().getAccelerators();
        accelerators.put(DRAW_CHART,     this::drawChart);
        accelerators.put(ZOOM_INCREMENT, zoom::increment);
        accelerators.put(ZOOM_DECREMENT, zoom::decrement);
        initChartTool();
        initZoom();
    }

    /**
     * Change current WebView's user agent.
     */
    @FXML
    private void changeUserAgent() {
        if (ua == null || ua.getItems().isEmpty()) {
            ArrayIterate.forEach(UserAgent.values(), ua.getItems()::add);
            ua.getSelectionModel().select(0);
        }
        messenger.onNext(UserAgentMessage.make(ua.getValue()));
    }

    /**
     * Apply font settings.
     */
    @FXML
    private void applyFontSettings() {
        final int size = fontSize.intValue();
        if (size < 0) {
            return;
        }
        final String item = fontFamily.getSelectionModel().getSelectedItem();
        messenger.onNext(FontMessage.make(Font.font(item), size));
    }

    /**
     * Set current WebView publisher.
     * @param zoomPublisher
     */
    public void setFlux(final Flux<DoubleProperty> zoomPublisher) {
        final Cancellation subscribe = zoomPublisher.subscribeOn(Schedulers.elastic())
            .subscribe(z -> {
                zoom.setValue(z.get());
                zoom.valueProperty().bindBidirectional(z);
            });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> subscribe.dispose()));
    }

    /**
     * Return message senger.
     * @return {@link TopicProcessor}
     */
    public TopicProcessor<Message> getMessenger() {
        return messenger;
    }

    /**
     * Pass {@link Config} object.
     * @param conf
     */
    public void setConfig(Config conf) {
        this.conf = conf;
        this.fontSize.setText(conf.get(Key.FONT_SIZE));
        this.fontFamily.getItems().addAll(Font.getFamilies());
        final int index = this.fontFamily.getItems().indexOf(conf.get(Key.FONT_FAMILY));
        this.fontFamily.getSelectionModel().select(index == -1 ? 0 : index);

        ArrayIterate.forEach(UserAgent.values(), ua.getItems()::add);
        ua.getSelectionModel().select(0);
    }

    /**
     * Return root pane,
     * @return
     */
    protected Pane getRoot() {
        return root;
    }

}
