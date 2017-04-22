package jp.toastkid.yobidashi;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.utility.ArrayIterate;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import jp.toastkid.article.control.UserAgent;
import jp.toastkid.chart.ChartPane;
import jp.toastkid.chart.ChartPane.Category;
import jp.toastkid.jfx.common.control.NumberTextField;
import jp.toastkid.libs.temperature.TemperatureConverter;
import jp.toastkid.yobidashi.message.ContentTabMessage;
import jp.toastkid.yobidashi.message.Message;
import jp.toastkid.yobidashi.message.UserAgentMessage;
import jp.toastkid.yobidashi.models.Config;
import jp.toastkid.yobidashi.models.Config.Key;
import jp.toastkid.yobidashi.models.Defines;

/**
 * Right side tools controller.
 * @author Toast kid
 *
 */
public class ToolsController implements Initializable {

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

    /** UserAgent's selector. */
    @FXML
    private ComboBox<UserAgent> ua;

    /** Fahrenheit input. */
    @FXML
    private NumberTextField fahrenheit;

    /** Celsius input. */
    @FXML
    private NumberTextField celsius;

    /** Message sender. */
    private final Subject<Message> messenger = PublishSubject.create();


    /**
     * Draw chart.
     */
    @FXML
    private final void drawChart() {
        final String title = graphKind.getSelectionModel().getSelectedItem().toString();
        final Config conf = new Config(Defines.CONFIG);
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
     * Set current WebView publisher.
     * @param zoomPublisher
     */
    public void setFlux(final Observable<DoubleProperty> zoomPublisher) {
        final Disposable subscribe = zoomPublisher.subscribeOn(Schedulers.newThread())
            .subscribe(z -> {
                zoom.setValue(z.get());
                zoom.valueProperty().bindBidirectional(z);
            });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> subscribe.dispose()));
    }

    /**
     * Subscribe this messenger.
     * @return {@link Disposable}
     */
    public Disposable subscribe(final Consumer<Message> c) {
        return messenger.subscribe(c);
    }

    /**
     * Return root pane,
     * @return
     */
    protected Pane getRoot() {
        return root;
    }

    /**
     * Return accelerators.
     * @return accelerators
     */
    Map<KeyCombination, Runnable> accelerators() {
        return Maps.fixedSize.of(
                DRAW_CHART,     this::drawChart,
                ZOOM_INCREMENT, zoom::increment,
                ZOOM_DECREMENT, zoom::decrement
                );
    }

    /**
     * Convert celsius to fahrenheit.
     */
    @FXML
    private void cToF() {
        fahrenheit.setText(Double.toString(TemperatureConverter.cToF(celsius.doubleValue())));
    }

    /**
     * Convert fahrenheit to celsius.
     */
    @FXML
    private void fToC() {
        celsius.setText(Double.toString(TemperatureConverter.fToC(fahrenheit.doubleValue())));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initChartTool();
        initZoom();
        ArrayIterate.forEach(UserAgent.values(), ua.getItems()::add);
        ua.getSelectionModel().select(0);
    }

}
