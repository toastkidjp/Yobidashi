package jp.toastkid.yobidashi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import javafx.stage.Stage;
import jp.toastkid.chart.ChartPane;
import jp.toastkid.yobidashi.message.ContentTabMessage;
import jp.toastkid.yobidashi.message.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.TopicProcessor;
import reactor.core.scheduler.Schedulers;

/**
 * Right side tools controller.
 * @author Toast kid
 *
 */
public class ToolsController {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ToolsController.class);

    /** Zoom increment keyboard shortcut. */
    private static final KeyCodeCombination ZOOM_INCREMENT
        = new KeyCodeCombination(KeyCode.SEMICOLON, KeyCombination.CONTROL_DOWN);

    /** Zoom decrement keyboard shortcut. */
    private static final KeyCodeCombination ZOOM_DECREMENT
        = new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN);

    /** Draw chart shortcut. */
    private static final KeyCombination DRAW_CHART
        = new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN);

    /** グラフ種別セレクタ. */
    @SuppressWarnings("rawtypes")
    @FXML
    public ComboBox graphKind;

    /** 月セレクタ. */
    @FXML
    public ComboBox<String> month;

    /** Zoom Controller. */
    @FXML
    public Slider zoom;

    /** Specify zoom rate. */
    @FXML
    public TextField zoomInput;

    /** Message sender. */
    private final TopicProcessor<Message> messenger = TopicProcessor.create();

    /**
     * Draw chart.
     */
    @FXML
    private final void drawChart() {
        final String title = graphKind.getSelectionModel().getSelectedItem().toString();
        final Pane content = ChartPane.make(title,
                "日記" + month.getSelectionModel().getSelectedItem().toString());
        messenger.onNext(ContentTabMessage.make(title, content));
    }

    /**
     * Set zoom rate with specified value.
     */
    @FXML
    private final void setZoom() {
        try {
            final double ratio = Double.parseDouble(zoomInput.getText());
            zoom.setValue(ratio);
        } catch (final Exception e) {
            LOGGER.error("Error", e);
        }
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
        items.addAll(ChartPane.getMonthsList());
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
     * Set current WebView publisher.
     * @param zoomPublisher
     */
    public void setFlux(final Flux<DoubleProperty> zoomPublisher) {
        zoomPublisher.subscribeOn(Schedulers.elastic())
            .subscribe(z -> {
                zoom.setValue(z.get());
                zoom.valueProperty().bindBidirectional(z);
            });
    }

    /**
     * Return message senger.
     * @return {@link TopicProcessor}
     */
    public TopicProcessor<Message> getMessenger() {
        return messenger;
    }

}
