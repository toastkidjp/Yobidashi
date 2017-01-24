package jp.toastkid.chart;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.factory.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.toastkid.article.models.Articles;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.yobidashi.models.Defines;

/**
 * 日記から家計簿の部分を取り出し、計算する。
 * <HR>
 * (130319) "|" での表に対応<BR>
 * (121111) Play に移植<BR>
 * (120701) バグ修正<BR>
 * (120625) Android に移植<BR>
 * (120623) 作成<BR>
 * @author Toast kid
 *
 */
public final class CashFlowExtractor implements ChartDataExtractor {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CashFlowExtractor.class);

    /** key value pair's list. */
    private final List<KeyValue> values;

    /** use for make title. */
    private String prefix;

    /** use for make title. */
    private int overall;

    /**
     * init with empty list.
     */
    public CashFlowExtractor() {
        values = Lists.mutable.empty();
    }


    /**
     * 月間の消費額を計算する.
     * @param pathToDir dir.
     * @param pPrefix (例) 日記2012-0
     * @return 月間の消費額
     * @throws IOException ファイル読み込みの異常で発生
     * @throws NumberFormatException 数値パースで発生
     */
    @Override
    public Map<String, Number> extract(final String pathToDir, final String pPrefix) {

        this.prefix = pPrefix;

        if (StringUtils.isEmpty(pathToDir)) {
            return Collections.emptyMap();
        }

        final String prefix = Articles.titleToFileName(pPrefix);

        final List<String> articleTitles = new ArrayList<>();
        try {
            Files.find(Paths.get(pathToDir), 1, (p, attr) -> p.getFileName().toString().startsWith(prefix))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .forEach(articleTitles::add);
        } catch (final IOException e1) {
            LOGGER.info("Error!", e1);
        }

        if (articleTitles.size() < 1) {
            return Collections.emptyMap();
        }

        final Map<String, Number> map   = new TreeMap<>();
        final Map<String, Number> daily = new TreeMap<>();
        final AtomicInteger gross = new AtomicInteger(0);
        articleTitles.forEach(item -> {
            final String readTarget = pathToDir + "/" + item;

            boolean isCashFlowArea = false;
            final String date = Articles.convertTitle(item).replace("日記", "");
            String str = "";
            try (final BufferedReader fileReader
                   = FileUtil.makeFileReader(readTarget, Defines.ARTICLE_ENCODE); ) {
                str = fileReader.readLine();
                while (str != null) {
                    if ((str.startsWith("*") || str.startsWith("#")) && str.endsWith("家計簿")) {
                        isCashFlowArea = true;
                    }
                    if (isCashFlowArea && str.startsWith("|")) {
                        final String[] line = str.split("[,\\|]");
                        final String target = line[2];
                        int price = 0;
                        if (target.endsWith("円")) {
                            final String priceStr = target.substring(0, target.indexOf("円")).trim();
                            // 120701 修正
                            if (StringUtils.isNotBlank(priceStr)) {
                                price = Integer.parseInt(priceStr);
                            }
                            map.put(date +  line[1].trim(), price);
                            values.add(new KeyValue.Builder().setKey(date)
                                    .setMiddle(line[1].trim()).setValue(price).build());
                        }
                        gross.addAndGet(price);
                    }
                    str = fileReader.readLine();
                }
                daily.put(date.substring(0, 10), gross.get());
                isCashFlowArea = false;
            } catch (final IOException e) {
                e.printStackTrace();
            }
        });
        this.overall = gross.get();
        return daily;
    }

    /**
     * return KeyValues.
     * @return current KeyValues
     */
    @Override
    public List<KeyValue> getTableValues() {
        return values;
    }

    @Override
    public String getTitle() {
        if (prefix == null) {
            throw new IllegalStateException("'prefix' is null.");
        }
        if (prefix.isEmpty()) {
            return "";
        }
        return String.format("%s: %s %,3d円",
                ChartPane.Category.OUTGO.text(), prefix.substring(2), this.overall);
    }
}
