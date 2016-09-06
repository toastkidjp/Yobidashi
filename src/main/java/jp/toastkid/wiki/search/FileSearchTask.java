package jp.toastkid.wiki.search;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.Interval;

import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.wiki.models.Defines;

/**
 * ファイル単位での検索をする.
 * @author Toast kid
 */
public final class FileSearchTask implements Runnable {

    /** 検索パターンのセット */
    private final Set<Pattern> targetPatterns;

    /** 検索結果 */
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
        return result.filePath;
    }

    /**
     * 各パラメータで初期化する.
     * @param pFilePath 検索対象ファイルのパス
     * @param pPatSet   検索パターンのセット
     */
    public FileSearchTask(
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
        final List<String> contents
            = FileUtil.readLines(result.filePath, Defines.ARTICLE_ENCODE);

        Interval.zeroTo(contents.size()).each(i -> {
            final String content = contents.get(i);
            result.length = result.length + content.length();
            targetPatterns.forEach(pat -> {
                final Matcher matcher = pat.matcher(content);
                while (matcher.find()){
                    final List<String> founds
                        = result.df.getOrDefault(pat.pattern(), Lists.mutable.empty());
                    founds.add(i + " : " + matcher.group(0));
                    result.df.put(pat.pattern(), founds);
                }
            });
        });
    }
}
