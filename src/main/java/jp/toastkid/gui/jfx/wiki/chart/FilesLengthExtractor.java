package jp.toastkid.gui.jfx.wiki.chart;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import jp.toastkid.gui.jfx.wiki.Functions;
import jp.toastkid.gui.jfx.wiki.models.Article;
import jp.toastkid.gui.jfx.wiki.models.Defines;
import jp.toastkid.libs.fileFilter.TextFileFilter;
import jp.toastkid.libs.utils.FileUtil;

/**
 * ファイルの文字数を計算する。
 * <HR>
 * (121113) 作成<BR>
 * @author Toast kid
 *
 */
public final class FilesLengthExtractor implements GraphDataExtractor {

    /** 「月合計」のキー */
    public static final String TOTAL_KEY = "合計";

    private static String[] list;

    /**
     *
     * @param pathToDir ソースファイルフォルダ
     * @param pPrefix (例) 日記2012-0
     * @return ファイル名とファイル内容の長さを対応させた Map
     */
    @Override
    public Map<String, Number> extract(final String pathToDir, final String pPrefix) {
        final Map<String, Number> resultMap = new TreeMap<String, Number>();
        if (list == null) {
            list = new File(pathToDir).list(new TextFileFilter(false));
        }
        int overall = 0;
        final int length = list.length;
        for (int i = 0; i < length; i++) {
            final String name = Functions.decodeBytedStr(
                    list[i].replace(Article.Extension.WIKI.text(),""), Defines.TITLE_ENCODE);
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
        resultMap.put(TOTAL_KEY, overall);
        return resultMap;
    }
}
