package jp.toastkid.gui.jfx.wiki.chart;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Chart data extractor from articles.
 * @author Toast kid
 *
 */
public interface ChartDataExtractor {

    /**
     * extract data and return map.
     * @param pathToDir
     * @param pPrefix
     * @return
     */
    public Map<String, Number> extract(final String pathToDir, final String pPrefix);

    /**
     * default imple return empty list.
     * @return empty list.
     */
    public default List<KeyValue> getTableValues() {
        return Collections.emptyList();
    }

    /**
     * return this chart's title.
     * If insufficient parameter, you should throw {@link IllegalStateException}.
     * @return title
     * @throws IllegalStateException you should throw {@link IllegalStateException}.
     */
    public String getTitle();
}
