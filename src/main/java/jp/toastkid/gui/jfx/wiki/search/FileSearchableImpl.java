package jp.toastkid.gui.jfx.wiki.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.collections.impl.list.Interval;

import jp.toastkid.gui.jfx.wiki.models.Defines;
import jp.toastkid.libs.utils.FileUtil;

/**
 * ファイル単位での検索をする.
 * @author Toast kid
 */
public final class FileSearchableImpl implements Runnable {

    /** AND 検索をするか否か */
    private final boolean isAnd;
    /** 検索パターンのセット */
    private final Set<Pattern> patSet;
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
     * @param pIsAnd    AND 検索をするか否か
     */
    public FileSearchableImpl(
            final String       pFilePath,
            final Set<Pattern> pPatSet,
            final boolean      pIsAnd
            ) {
        this.patSet = pPatSet;
        this.isAnd  = pIsAnd;
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
        final List<String> contentList
            = FileUtil.readLines(result.filePath, Defines.ARTICLE_ENCODE);

        final List<String> founds = new ArrayList<String>();

        Interval.zeroTo(contentList.size()).each((i) -> {
            final String content = contentList.get(i);
            result.length = result.length + content.length();
            for (final Pattern pat : patSet) {
                final Matcher matcher = pat.matcher(content);
                while (matcher.find()){
                    founds.add(i + " : " + matcher.group(0));
                    result.df.put(pat.pattern(), founds);
                }
            }
        });

        // すべてのクエリを含んでいない文書は不適合とする.
        if (isAnd && result.df.size() < patSet.size()) {
            result.df.clear();
        }
    }
}
