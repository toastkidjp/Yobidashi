/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi;

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
import jp.toastkid.libs.utils.CalendarUtil;
import jp.toastkid.yobidashi.message.ContentTabMessage;
import jp.toastkid.yobidashi.message.Message;
import jp.toastkid.yobidashi.message.UserAgentMessage;
import jp.toastkid.yobidashi.models.Config;
import jp.toastkid.yobidashi.models.Config.Key;
import jp.toastkid.yobidashi.models.Defines;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 * Right side tools controller.
 *
 * @author Toast kid
 */
public final class ToolsController implements Initializable {

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
    private void drawChart() {
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
    private void callDefaultZoom() {
        zoom.setValue(1.0);;
    }

    /**
     * Initialize chart tool.
     */
    private void initChartTool() {
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
            Stream.of(UserAgent.values()).forEach(ua.getItems()::add);
            ua.getSelectionModel().select(0);
        }
        messenger.onNext(UserAgentMessage.make(ua.getValue()));
    }

    /**
     * Set current WebView publisher.
     * @param zoomPublisher
     */
    void setFlux(final Observable<DoubleProperty> zoomPublisher) {
        final Disposable subscribe = zoomPublisher.subscribeOn(Schedulers.newThread())
            .subscribe(z -> {
                zoom.setValue(z.get());
                zoom.valueProperty().bindBidirectional(z);
            });
        Runtime.getRuntime().addShutdownHook(new Thread(subscribe::dispose));
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
        final Map<KeyCombination, Runnable> map = new HashMap<>();
        map.put(DRAW_CHART,     this::drawChart);
        map.put(ZOOM_INCREMENT, zoom::increment);
        map.put(ZOOM_DECREMENT, zoom::decrement);
        return map;
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

    @FXML
    private NumberTextField timestamp;

    @FXML
    private TextField date;

    @FXML
    private void timestampToDate() {
        date.setText(CalendarUtil.longToStr(timestamp.longValue(), "yyyy/MM/dd HH:mm:ss"));
    }

    @FXML
    private void dateToTimestamp() {
        final String dateText = date.getText();
        if (StringUtils.isEmpty(dateText)) {
            return;
        }
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        final String cleaned = dateText
                .replace("/", "")
                .replace("-", "")
                .replace(" ", "")
                .replace(":", "");
        final TemporalAccessor accessor = dateTimeFormatter.parse(cleaned);
        timestamp.setText(Long.toString(LocalDateTime.from(accessor).toEpochSecond(ZoneOffset.ofHours(9)) * 1000L));
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        initChartTool();
        initZoom();
        Stream.of(UserAgent.values()).forEach(ua.getItems()::add);
        ua.getSelectionModel().select(0);
    }

}
