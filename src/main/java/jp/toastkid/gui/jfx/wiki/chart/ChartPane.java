package jp.toastkid.gui.jfx.wiki.chart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.factory.Sets;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import jp.toastkid.gui.jfx.wiki.models.Config;
import jp.toastkid.gui.jfx.wiki.models.Config.Key;
import jp.toastkid.libs.utils.CalendarUtil;

/**
 * Chart controller.
 * @author Toast kid
 *
 */
public class ChartPane extends AnchorPane {

    /** 「日記の文字数」.  (130406)*/
    public static final String DIARY     = "日記の文字数";

    /** 「出費」.(130406)*/
    public static final String OUTGO     = "出費";

    /** 「日経平均株価」.  (130406)*/
    public static final String NIKKEI225 = "日経平均株価";

    /**
     * make chart view.
     * @param category
     * @param prefix ex: "日記2012-11".
     * @return pane contains chart.
     */
    public static Pane makeChart(final String category, final String prefix) {

        final LineChart<String,Number> chart = drawChart(category, prefix);
        final Label dataLabel = new Label();
        initBackground(chart, dataLabel);
        return new ChartPane(){{getChildren().add(new VBox(dataLabel, chart));}};
    }

    private static LineChart<String, Number> drawChart(String category, String prefix) {
        final String title = category + ": " + prefix.substring(2);

        // Prepare drawing chart.
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(true);
        final LineChart<String, Number> chart = initChartArea(title, yAxis);

        final XYChart.Series<String, Number> series = new Series<>();
        series.setName(title);

        final Map<String, Number> dataMap
            = findExtractor(category).extract(Config.get(Key.ARTICLE_DIR), prefix);
        dataMap.entrySet().stream()
            .filter( entry -> {return !entry.getKey().equals(FilesLengthExtractor.TOTAL_KEY);})
            .forEach(entry -> {
                final Data<String, Number> d = new Data<String, Number>(entry.getKey(), entry.getValue());
                final String text = entry.getKey() + ": " + entry.getValue().intValue();
                d.setNode(new HoveredThresholdNode(findThreshold(category), entry.getValue().intValue(), text));
                series.getData().add(d);
            });

        final ObservableList<Series<String, Number>> seriesList
            = FXCollections.observableArrayList();
        seriesList.add(series);

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
     * @return
     */
    private static LineChart<String, Number> initChartArea(final String title, final NumberAxis yAxis) {
        final LineChart<String, Number> chart = new LineChart<>(new CategoryAxis(), yAxis);
        chart.setPrefHeight(600.0);
        chart.setPrefWidth(1200.0);
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
    private static GraphDataExtractor findExtractor(final String category) {
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
        //monthSelector.add(TAG_TOTAL);
        final String now = CalendarUtil.calendarToFormated(Calendar.getInstance()).substring(0, 7);
        final Calendar cal = new GregorianCalendar(2011, 0, 1);
        int i = 0;
        while (true){
            final String month = CalendarUtil.calendarToFormated(cal).substring(0, 7);
            monthSelector.add(month);
            cal.add(Calendar.MONTH,1);
            i++;
            if (now.equals(month) || i == 100){
                break;
            }
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
