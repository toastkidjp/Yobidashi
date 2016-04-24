package jp.toastkid.gui.jfx.wordcloud;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import jp.toastkid.gui.jfx.wiki.models.Defines;
import jp.toastkid.libs.tinysegmenter.TinySegmenter;
import jp.toastkid.libs.utils.FileUtil;

/**
 * Utility of word cloud for JavaFX.
 * @author Toast kid
 * @see <a href="http://stackoverflow.com/questions/11481482/
 *how-can-i-generate-a-tag-cloud-in-java-with-opencloud">
 * How can I generate a tag cloud in Java, with OpenCloud?</a>
 *
 */
public class FxWordCloud {

    /** 最小保証サイズ./ */
    private static final double MINIMUM_SIZE  = 10.0;
    /** 最大のフォントサイズ, */
    private static final double MAX_FONT_SIZE = 500.0;

    /**
     * draw Word Cloud on passed Pane.
     * @param canvas
     * @param pathToFile path/to/file
     */
    public static void draw(final Pane canvas, final String pathToFile) {
        draw(canvas, new File(pathToFile));
    }

    /**
     * draw Word Cloud on passed Pane.
     * @param canvas
     * @param file file
     */
    public static void draw(final Pane canvas, final File file) {
        final TinySegmenter ts = TinySegmenter.getInstance();
        final MutableMap<String, Integer> map = Maps.mutable.empty();
        FileUtil.readLines(file, Defines.ARTICLE_ENCODE)
            .stream()
            .filter(str  -> {return StringUtils.isNotEmpty(str);})
            .forEach(str -> {
                ts.segment(str).stream()
                    .map(seg     -> {return seg.replace("\"", "");})
                    .filter(seg  -> {return StringUtils.isNotEmpty(seg);})
                    .forEach(seg -> {map.put(seg, map.getIfAbsentValue(seg, 0) + 1);});
        });
        placeTexts(canvas, map);
    }

    /**
     * draw Word Cloud on passed Pane.
     * @param canvas
     * @param file file
     */
    public static void draw(final Pane canvas, final Map<String, Integer> map) {
        final MutableMap<String, Integer> m = Maps.mutable.empty();
        m.putAll(map);
        placeTexts(canvas, m);
    }

    /**
     * place texts to passed pane.
     * @param canvas pane
     * @param map map
     */
    private static void placeTexts(final Pane canvas, final MutableMap<String, Integer> map) {
        final int max = map.max();
        map.forEach((k, v) -> {
            if (50 < map.size() && v < 2) {
                return;
            }
            final Text text = new Text(k);
            text.setFont(new Font(MAX_FONT_SIZE * ((double) v / (double) max) + MINIMUM_SIZE));
            text.setFill(Color.color(Math.random(), Math.random(), Math.random()));
            Tooltip.install(text, new Tooltip(k + ": " + v));
            canvas.getChildren().add(text);
        });
    }
}
