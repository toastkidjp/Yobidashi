package jp.toastkid.chart;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import jp.toastkid.article.models.Articles;
import jp.toastkid.libs.fileFilter.ArticleFileFilter;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.yobidashi.Defines;

/**
 * ファイルの文字数を計算する。
 * <HR>
 * (121113) 作成<BR>
 * @author Toast kid
 *
 */
public final class FilesLengthExtractor implements ChartDataExtractor {

    /** 「月合計」のキー */
    public static final String TOTAL_KEY = "合計";

    private static String[] list;

    private String prefix;

    private int overall;

    /**
     *
     * @param pathToDir ソースファイルフォルダ
     * @param pPrefix (例) 日記2012-0
     * @return ファイル名とファイル内容の長さを対応させた Map
     */
    @Override
    public Map<String, Number> extract(final String pathToDir, final String pPrefix) {
        this.prefix = pPrefix;
        final Map<String, Number> resultMap = new TreeMap<>();
        if (list == null) {
            list = new File(pathToDir).list(new ArticleFileFilter(false));
        }
        int overall = 0;
        final int length = list.length;
        for (int i = 0; i < length; i++) {
            final String name = Articles.decodeBytedStr(FileUtil.removeExtension(list[i]), Defines.TITLE_ENCODE);
            if (name.startsWith(pPrefix)){
                String putKey = name;
                // (130503) 修正
                if (StringUtils.isNotEmpty(pPrefix)){
                    putKey = name.substring(2);
                }
                // 文字数集計.
                final int fileCharacterValue
                    = FileUtil.countCharacters(pathToDir + "/" + list[i], Defines.ARTICLE_ENCODE);
                resultMap.put(putKey, fileCharacterValue);
                overall = overall + fileCharacterValue;
            }
        }
        this.overall = overall;
        resultMap.put(TOTAL_KEY, overall);
        return resultMap;
    }

    @Override
    public String getTitle() {
        if (prefix == null) {
            throw new IllegalStateException("'prefix' is null.");
        }
        return String.format("%s: %s %,3d字", ChartPane.DIARY, prefix.substring(2), this.overall);
    }
}
