package jp.toastkid.rss;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.yobidashi.Defines;

/**
 * RSS Feeder.
 *
 * @author Toast kid
 *
 */
public final class RssFeeder {

    /** RSS取得対象のURLリスト. */
    private static final String PATH_RSS_TARGETS = Defines.USER_DIR + "/res/rss";

    /** line separator. */
    private static final String LINE_SEPARATOR   = System.lineSeparator();

    /** RSS headings. */
    private final List<String> headings;

    /**
     * Initialize.
     */
    public RssFeeder() {
        headings = new ArrayList<>(10);
    }

    /**
     * RSS を取得し、その結果をHTMLで返す．
     * @return RSS取得結果.
     */
    public String run() {
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
     * 指定したURL1件のフィードを取得.
     * @param url URL(文字列)
     * @return URL1件単位でのRSS取得結果.
     */
    private String getFeed(final String url) {
        try {
            return makeTable(new RssParser().parse(new URI(url)));
        } catch (final URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * RSS クラスを 簡単なtableに変換して返す.
     * @param rss
     * @return
     */
    private String makeTable(final Rss rss) {
        final StringBuilder table = new StringBuilder();
        // サイトのタイトル
        final String expanderTitle = rss.expandTitle();
        headings.add(expanderTitle);
        table.append("<h1><a href=\"").append(rss.getLink()).append("\">")
            .append(rss.getTitle()).append("</a></h2>");
        table.append("<a href=\"JavaScript:open('").append(expanderTitle)
            .append("')\">ここをクリックすると開きます。</a>");
        table.append("<div class=\"expander\" id=\"").append(expanderTitle).append("\">");
        table.append("<table>");
        table.append("<tr>");
        table.append("<th>Article</th><th>Description</th>");
        table.append("</tr>");
        for (final Rss.Item item : rss.items()) {
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
