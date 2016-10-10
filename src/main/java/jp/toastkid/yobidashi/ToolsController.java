package jp.toastkid.yobidashi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
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

/**
 * Right side tools controller.
 * @author Toast kid
 *
 */
public class ToolsController {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    /** Zoom increment keyboard shortcut. */
    private static final KeyCodeCombination ZOOM_INCREMENT
        = new KeyCodeCombination(KeyCode.SEMICOLON, KeyCombination.CONTROL_DOWN);

    /** Zoom decrement keyboard shortcut. */
    private static final KeyCodeCombination ZOOM_DECREMENT
        = new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN);

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

    /** for controlling window. */
    private Stage stage;

    /**
     * Draw chart.
     */
    @FXML
    private final void drawChart() {
        drawChart(true);
    }

    /**
     * Draw chart.
     */
    @FXML
    private final void drawChart(final boolean openNew) {
        final String graphTitle = graphKind.getSelectionModel().getSelectedItem().toString();
        final Pane content = ChartPane.make(graphTitle,
                "日記" + month.getSelectionModel().getSelectedItem().toString());

        /*if (openNew) {
            final Tab tab = makeClosableTab(graphTitle);
            tab.setContent(content);
            openTab(tab);
            return;
        }

        getCurrentTab().setContent(content);*/
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
        final DoubleProperty valueProperty = zoom.valueProperty();
        valueProperty.addListener( (value, arg1, arg2) -> {
            //getCurrentWebView().ifPresent(wv -> {wv.zoomProperty().bindBidirectional(valueProperty);});
            zoomInput.setText(Double.toString(valueProperty.get()));
        });
    }

    /**
     * Set stage for controlling window.
     * @param stage
     */
    protected void init(final Stage stage) {
        this.stage  = stage;
        stage.getScene().setOnKeyPressed(e -> {
            if (ZOOM_INCREMENT.match(e)) {
                zoom.increment();
            } else if (ZOOM_DECREMENT.match(e)) {
                zoom.decrement();
            }
        });
        initChartTool();
        initZoom();
    }

}
