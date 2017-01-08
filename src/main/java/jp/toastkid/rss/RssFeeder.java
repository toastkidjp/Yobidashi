package jp.toastkid.rss;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.yobidashi.Defines;

/**
 * RSS リーダ.
 * TODO template 分離.
 * @author Toast kid
 *
 */
public final class RssFeeder {

    /** RSS取得対象のURLリスト. */
    private static final String PATH_RSS_TARGETS = Defines.USER_DIR + "/res/rss";

    /** line separator. */
    private static final String LINE_SEPARATOR   = System.lineSeparator();

    /**
     * RSS を取得し、その結果をHTMLで返す．
     * @return RSS取得結果.
     */
    public static String run() {
        if (!Files.exists(Paths.get(PATH_RSS_TARGETS))) {
            return null;
        }
        final StringBuilder content = new StringBuilder();
        final List<String> urls = FileUtil.readDirLines(PATH_RSS_TARGETS);
        urls.parallelStream()
            .filter((url) ->  {return StringUtils.isNotBlank(url) && url.startsWith("http");})
            .forEach((url) -> {content.append(getFeed(url)).append(LINE_SEPARATOR);}
            );
        return content.toString();
    }

    /**
     * 指定したURL1件のフィードを取得し、簡単なtableに変換して返す．
     * @param url URL(文字列)
     * @return URL1件単位でのRSS取得結果.
     */
    private static String getFeed(final String url) {
        final Rss rss = new Rss(url);
        rss.parse();
        final StringBuilder table = new StringBuilder();
        // サイトのタイトル
        final String expanderTitle = rss.expandTitle();
        table.append("<h2><a href=\"").append(rss.getLink()).append("\">")
            .append(rss.getTitle()).append("</a></h2>");
        table.append("<a href=\"JavaScript:open('").append(expanderTitle)
            .append("')\">ここをクリックすると開きます。</a>");
        table.append("<div class=\"expander\" id=\"").append(expanderTitle).append("\">");
        table.append("<table>");
        table.append("<tr>");
        table.append("<th>Article</th><th>Description</th>");
        table.append("</tr>");
        for (final Rss.Item item : rss.getItems()) {
            //System.out.println(entry.toString());
            table.append("<tr>");
            table.append("<td><a href=\"").append(item.link).append("\">")
                    .append(item.title).append("</a>");
            table.append("<br/>");
            // 記事の詳細
            table.append(item.date).append("</td>");
            // 記事の詳細
            table.append("<td>").append(item.description).append("</td>");
            table.append("</tr>");
        }
        table.append("</tr>");
        table.append("</table>");
        table.append("</div><script>close(\"").append(expanderTitle).append("\");</script>");
        return table.toString();
    }
}
