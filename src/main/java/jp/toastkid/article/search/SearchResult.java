package jp.toastkid.article.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.eclipse.collections.impl.factory.Maps;

import jp.toastkid.article.ArticleGenerator;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.libs.utils.Strings;
import jp.toastkid.yobidashi.Defines;

/**
 * 記事1件の検索結果.
 * @author Toast kid
 *
 */
public final class SearchResult {

    /** ファイルパス. */
    public String filePath;

    /** 記事名. */
    public String title;

    /** 記事の文字数. */
    public int length;

    /** 記事の最終更新. */
    public long lastModified;

    /** 単語頻度マップ. */
    public Map<String, List<String>> df;

    /**
     * constructor.
     * @param pFilePath /path/to/file
     */
    public SearchResult(final String pFilePath) {
        this.df           = Maps.mutable.withInitialCapacity(20);
        this.filePath     = pFilePath;
        this.lastModified = FileUtil.lastModified(pFilePath);
    }

    /**
     * 仮の検索結果オブジェクトを返す.
     * @return 仮の検索結果オブジェクト
     */
    public static final SearchResult makeSimple(final String pFilePath) {
        final SearchResult result = new SearchResult(pFilePath);
        result.df.put("simple", new ArrayList<String>());
        return result;
    }

    /**
     * ファイルパスから記事名を生成し、内部リンクを返す.
     * @param filePath ファイルパス
     * @return 内部リンク
     */
    public final String getLink() {
        final String fileName = new File(this.filePath).getName();
        final String title = ArticleGenerator.decodeBytedStr(
                FileUtil.removeExtension(fileName),
                Defines.TITLE_ENCODE
        );
        return Strings.join(
                "<a href=\"/wiki/", fileName, "\" ", "target=\"_blank\">", title, "</a>");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
