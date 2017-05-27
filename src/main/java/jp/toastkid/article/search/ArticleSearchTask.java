package jp.toastkid.article.search;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import jp.toastkid.article.models.Article;
import jp.toastkid.libs.utils.FileUtil;

/**
 * ファイル単位での検索をする.
 *
 * @author Toast kid
 */
public final class ArticleSearchTask implements Runnable {

    /** Search pattern. */
    private final Set<Pattern> targetPatterns;

    /** Search result. */
    private final SearchResult result;

    /**
     * @return result
     */
    public SearchResult getResult() {
        return result;
    }

    /**
     * @return filePath
     */
    public String getFilePath() {
        return result.filePath();
    }

    /**
     * 各パラメータで初期化する.
     * @param pFilePath 検索対象ファイルのパス
     * @param pPatSet   検索パターンのセット
     */
    public ArticleSearchTask(
            final String       pFilePath,
            final Set<Pattern> pPatSet
            ) {
        this.targetPatterns = pPatSet;
        this.result = new SearchResult(pFilePath);
    }

    @Override
    public void run() {
        strSearchFromFile();
    }

    /**
     * ファイル単位で文字列を検索する.
     */
    private void strSearchFromFile() {
        final List<String> contents = FileUtil.readLines(result.filePath(), Article.ENCODE);

        IntStream.rangeClosed(0, contents.size()).forEach(i -> {
            final String content = contents.get(i);
            result.addLength(content.length());
            targetPatterns.forEach(pat -> {
                final Matcher matcher = pat.matcher(content);
                while (matcher.find()){
                    final List<String> founds = result.getOrEmpty(pat.pattern());
                    founds.add(i + " : " + matcher.group(0));
                    result.put(pat.pattern(), founds);
                }
            });
        });
    }
}
