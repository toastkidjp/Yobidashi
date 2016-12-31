package jp.toastkid.chart;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.factory.Sets;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import jp.toastkid.yobidashi.Config;
import jp.toastkid.yobidashi.Config.Key;

/**
 * Chart controller.
 * @author Toast kid
 *
 */
public class ChartPane extends VBox {

    /** default width. */
    private static final double DEFAULT_WIDTH = 1200.0;

    /** 「日記の文字数」.  (130406)*/
    public static final String DIARY     = "日記の文字数";

    /** 「出費」.(130406)*/
    public static final String OUTGO     = "出費";

    /** 「日経平均株価」.  (130406)*/
    public static final String NIKKEI225 = "日経平均株価";

    /**
     * call super constructor.
     * @param content.
     */
    public ChartPane(final Node... content) {
        super(content);
    }

    /**
     * make chart view.
     * @param category
     * @param prefix ex: "日記2012-11".
     * @return pane contains chart.
     */
    public static ChartPane make(final String category, final String prefix) {

        final ChartDataExtractor extractor = findExtractor(category);
        final Map<String, Number> dataMap
            = extractor.extract(Config.get(Key.ARTICLE_DIR), prefix);
        final String title = extractor.getTitle();

        final LineChart<String,Number> chart = makeChart(title, dataMap, findThreshold(category));
        final Label dataLabel = new Label();
        initBackground(chart, dataLabel);

        final Pane mainContent = new VBox(dataLabel, new ScrollPane(chart));
        final List<KeyValue> tableValues = extractor.getTableValues();
        if (!tableValues.isEmpty()) {
            final SplitPane content = new SplitPane();
            content.setOrientation(Orientation.VERTICAL);
            content.getItems().addAll(mainContent, makeTable(tableValues));
            content.setDividerPosition(0, 0.8);
            return new ChartPane(content);
        }
        return new ChartPane(new SplitPane(){{getItems().addAll(mainContent);}});
    }

    /**
     * outgo chart has detail table.
     * @param tableValues
     * @return TableView.
     */
    @SuppressWarnings("unchecked")
    private static TableView<KeyValue> makeTable(final Collection<KeyValue> tableValues) {
        final TableView<KeyValue> table = new TableView<>();
        final TableColumn<KeyValue, String> keys = new TableColumn<>("Date");
        keys.setCellValueFactory(new PropertyValueFactory<KeyValue, String>("key"));
        keys.setPrefWidth(300.0);
        final TableColumn<KeyValue, String> items = new TableColumn<>("Item");
        items.setCellValueFactory(new PropertyValueFactory<KeyValue, String>("middle"));
        items.setPrefWidth(500.0);
        final TableColumn<KeyValue, String> costs = new TableColumn<>("Cost");
        costs.setCellValueFactory(new PropertyValueFactory<KeyValue, String>("value"));
        costs.setPrefWidth(100.0);

        table.getColumns().addAll(keys, items, costs);
        table.getItems().addAll(tableValues);
        return table;
    }

    /**
     *
     * @param title chart value.
     * @param dataMap map contains data.
     * @param threshold If value is less than threshold, value's color red.
     * @return
     */
    private static LineChart<String, Number> makeChart(
            final String title,
            final Map<String, Number> dataMap,
            final Number threshold
            ) {

        // Prepare drawing chart.
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(true);

        final XYChart.Series<String, Number> series = new Series<>();
        series.setName(title);

        dataMap.entrySet().stream()
            .filter( entry -> {return !entry.getKey().equals(FilesLengthExtractor.TOTAL_KEY);})
            .forEach(entry -> {
                final Data<String, Number> d = new Data<String, Number>(entry.getKey(), entry.getValue());
                final String text = entry.getKey() + ": " + entry.getValue().intValue();
                d.setNode(new HoveredThresholdNode(threshold, entry.getValue().intValue(), text));
                series.getData().add(d);
            });

        final ObservableList<Series<String, Number>> seriesList
            = FXCollections.observableArrayList();
        seriesList.add(series);

        final double width = Math.max(series.getData().size() * 50.0, DEFAULT_WIDTH);
        final LineChart<String, Number> chart = initChartArea(title, yAxis, width);
        chart.setData(seriesList);

        yAxis.setForceZeroInRange(false);
        yAxis.setLowerBound(Sets.immutable.withAll(dataMap.values()).min().doubleValue());
        return chart;
    }

    /**
     * find threshold.
     * @param category
     * @return
     */
    private static int findThreshold(final String category) {

        if (StringUtils.isEmpty(category)) {
            return 0;
        }

        switch (category) {
            case DIARY:
                return 3_000;
            case OUTGO:
                return 20_000;
            case NIKKEI225:
                return 10_000;
            default:
                return 0;
        }
    }

    /**
     * init chart.
     * @param title
     * @param yAxis
     * @param width
     * @return
     */
    private static LineChart<String, Number> initChartArea(
            final String title, final NumberAxis yAxis, final double width) {
        final LineChart<String, Number> chart = new LineChart<>(new CategoryAxis(), yAxis);
        chart.setPrefHeight(600.0);
        chart.setPrefWidth(width);
        chart.setStyle("-fx-background-color: #FCFCFC;");
        chart.setTitle(title);
        return chart;
    }

    /**
     * init chart background.
     * @param chart
     * @param dataLabel
     */
    private static void initBackground(final LineChart<String, Number> chart, final Label dataLabel) {
        final Node chartBackground = chart.lookup(".chart-plot-background");
        chartBackground.setOnMouseMoved(ev -> {
            final String pointXAxis = chart.getXAxis().getValueForDisplay(ev.getX());
            if (pointXAxis == null) {
                return;
            }
            final Number pointYAxis = chart.getYAxis().getValueForDisplay(ev.getY());
            dataLabel.setText(String.format("  %s: %.2f", pointXAxis, pointYAxis));
        });
        chartBackground.setCursor(Cursor.CROSSHAIR);
        chartBackground.getParent().getChildrenUnmodifiable().stream()
            .filter( n -> n != chartBackground)
            .forEach(n -> n.setMouseTransparent(true));
    }

    /**
     * category に 適合する Extractor を返す.
     * @param category
     * @return Extractor.
     */
    private static ChartDataExtractor findExtractor(final String category) {
        switch (category) {
            case DIARY:
                return new FilesLengthExtractor();
            case NIKKEI225:
                return new Nikkei225Extractor();
            case OUTGO:
                return new CashFlowExtractor();
        }
        return null;
    }

    /**
     * 月のリストを取得する.
     * @return 月の文字列表現を要素に持つ List
     */
    public static final List<String> getMonthsList() {
        final List<String> monthSelector = new ArrayList<String>();
        final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM");
        final String now = LocalDateTime.now().format(pattern);
        LocalDateTime ldt = LocalDateTime.of(2011, 1, 1, 0, 0);
        String month = ldt.format(pattern);
        while (!now.equals(month) && monthSelector.size() <= 100){
            ldt = ldt.plusMonths(1);
            month = ldt.format(pattern);
            monthSelector.add(month);
        }
        return monthSelector;
    }

    /**
     * a node which displays a value on hover, but is otherwise empty.
     * @author Toast kid.
     * @see <a href="https://gist.github.com/jewelsea/4681797">
     * @jewelsea jewelsea/LineChartWithHover.java</a>
     */
    private static class HoveredThresholdNode extends StackPane {

        /**
         *
         * @param priorValue
         * @param value
         * @param text
         */
        protected HoveredThresholdNode(final Number priorValue, final Number value, final String text) {
            setPrefSize(15, 15);

            final Label label = makeDataThresholdLabel(priorValue, value);
            getChildren().setAll(label);
            Tooltip.install(this, new Tooltip(text));
        }

        /**
         * set data label with color.
         * @param priorValue threshold.
         * @param value value.
         * @return colored label.
         */
        private Label makeDataThresholdLabel(final Number priorValue, final Number value) {
            final Label label = new Label(value.toString());
            label.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
            label.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

            if (priorValue.intValue() == 0) {
                label.setTextFill(Color.DARKGRAY);
            } else if (value.doubleValue() > priorValue.doubleValue()) {
                label.setTextFill(Color.FORESTGREEN);
            } else {
                label.setTextFill(Color.FIREBRICK);
            }

            label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
            return label;
        }
    }
}
