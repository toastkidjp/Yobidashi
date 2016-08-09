package jp.toastkid.gui.jfx.chart;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.factory.Lists;

import jp.toastkid.gui.jfx.wiki.ArticleGenerator;
import jp.toastkid.gui.jfx.wiki.models.Article;
import jp.toastkid.gui.jfx.wiki.models.Defines;
import jp.toastkid.libs.utils.FileUtil;

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

        final String prefix = ArticleGenerator.toBytedString_EUC_JP(pPrefix);
        final List<String> articleTitles
            = Arrays.asList(new File(pathToDir).list())
                .stream()
                .filter((item) -> item.startsWith(prefix))
                .collect(Collectors.toList());

        if (articleTitles.size() < 1) {
            return Collections.emptyMap();
        }

        final Map<String, Number> map   = new TreeMap<>();
        final Map<String, Number> daily = new TreeMap<>();
        final AtomicInteger gross = new AtomicInteger(0);
        articleTitles.forEach(item -> {
            final String readTarget = pathToDir + "/" + item;

            boolean isCashFlowArea = false;
            final String date = Article.convertTitle(item).replace("日記", "");
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
                            final String priceStr
                                = target.substring(0, target.indexOf("円")).trim();
                            // 120701 修正
                            if (!"".equals(priceStr)) {
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
     * @return
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
        return String.format("%s: %s %,3d円", ChartPane.DIARY, prefix.substring(2), this.overall);
    }
}
