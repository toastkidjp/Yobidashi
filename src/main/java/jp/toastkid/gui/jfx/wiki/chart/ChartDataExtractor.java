package jp.toastkid.gui.jfx.wiki.chart;

import java.util.Map;

public interface ChartDataExtractor {
    public Map<String, Number> extract(final String pathToDir, final String pPrefix);
}
