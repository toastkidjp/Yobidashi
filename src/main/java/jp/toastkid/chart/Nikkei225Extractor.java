package jp.toastkid.chart;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import jp.toastkid.article.ArticleGenerator;
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
public final class Nikkei225Extractor implements ChartDataExtractor {

    /** file list. */
    private static String[] list;

    private String prefix;

    /**
     *
     * @param pathToDir source file directory
     * @param pPrefix (例) 日記2012-0
     * @return ファイル名と日経平均株価を対応させた Map
     */
    @Override
    public Map<String, Number> extract(final String pathToDir, final String pPrefix) {

        this.prefix = pPrefix;

        final Map<String, Number> resultMap = new TreeMap<String, Number>();
        if(list == null){
            list = new File(pathToDir).list(new ArticleFileFilter(false));
        }

        final int length = list.length;
        for (int i = 0; i < length; i++) {
            final String name
                = ArticleGenerator.decodeBytedStr(FileUtil.removeExtension(list[i]), Defines.TITLE_ENCODE);
            if(name.startsWith(pPrefix)){
                final BufferedReader fileReader
                    = FileUtil.makeFileReader(pathToDir + "/" + list[i], Defines.ARTICLE_ENCODE);
                String str ="";
                try {
                    out:
                    while (str != null) {
                        if (str.endsWith("今日の日経平均株価終値")){
                            str = fileReader.readLine();
                            final String target = str.split("円")[0];
                            if (StringUtils.isNotEmpty(target)){
                                final String input = target.replace(",", "");
                                resultMap.put(
                                        name.replace("日記", ""),
                                        (int)Math.floor(Double.parseDouble(input))
                                        );
                                break out;
                            }
                        }
                        str = fileReader.readLine();
                    }
                    fileReader.close();
                } catch ( final IOException e) {
                    e.printStackTrace();
                }
            }
            if (200 < resultMap.size()){
                break;
            }
        }
        return resultMap;
    }

    @Override
    public String getTitle() {
        if (prefix == null) {
            throw new IllegalStateException("'prefix' is null.");
        }
        return ChartPane.NIKKEI225 + ": " + prefix.substring(2) + " ";
    }
}
