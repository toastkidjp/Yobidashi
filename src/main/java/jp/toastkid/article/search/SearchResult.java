package jp.toastkid.article.search;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 記事1件の検索結果.
 *
 * @author Toast kid
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
     * Constructor.
     * @param pFilePath /path/to/file
     */
    public SearchResult(final String pFilePath) {
        this.df           = new HashMap<>(20);
        this.filePath     = pFilePath;
        try {
            this.lastModified = Files.getLastModifiedTime(Paths.get(pFilePath)).toMillis();
        } catch (final IOException e) {
            e.printStackTrace();
        }
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
