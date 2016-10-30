package jp.toastkid.libs.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.factory.Lists;


/**
 * HTML を扱う便利なメソッドを収録したユーティリティクラス
 * <HR>
 * (130414) 作成<BR>
 * @author Toast kid
 *
 */
public final class HtmlUtil {

    /** RDFへのリンクを抜き出す正規表現. */
    private static final Pattern RDF_PATTERN
        = Pattern.compile("http://(.+?)\\.(rdf|xml)",  Pattern.DOTALL);

    /** body の中身を抜き出す正規表現. */
    private static final Pattern BODY_PATTERN
        = Pattern.compile("<[B|b]ody>(.+?)<[B|b]ody>", Pattern.DOTALL);

    /** HTML のタイトルを抜き出す正規表現. */
    private static final Pattern TITLE_PATTERN
        = Pattern.compile("<title>(.+?)</title>",      Pattern.DOTALL);

    /**
     * 表の HTML 表現を生成し返す。
     * <HR>
     * (130414) 作成<BR>
     * @param map
     * @return 表の HTML 表現
     */
    public static final String getTableHtml(final Map<String,?> map){
        return getTableHtml(map,null);
    }
    /**
     * 表の HTML 表現を生成し返す。
     * <HR>
     * (130414) 作成<BR>
     * @param map
     * @param header HTMLで記述のこと
     * @return 表の HTML 表現
     */
    public static final String getTableHtml(final Map<String,?> map, final String header) {
        return getTableHtml(map, header, map.size());
    }
    /**
     * 表の HTML 表現を生成し返す。
     * <HR>
     * (130414) 作成<BR>
     * @param map
     * @param header HTMLで記述のこと、ex) &lt;th>word&lt;/th>&lt;th>count&lt;/th>
     * @param limit 件数上限
     * @return 表の HTML 表現
     */
    public static final String getTableHtml(
            final Map<String,?> map,
            final String header,
            final int limit
            ) {
        final StringBuilder bld = new StringBuilder(map.size() * 500);
        bld.append("<table>");
        if (StringUtils.isNotEmpty(header)){
            bld.append(header);
        }
        Lists.immutable.ofAll(map.keySet())
            .subList(0, limit < 0 ? map.size() : limit)
            .each(key -> {
                bld.append("<tr>").append(td(key)).append(td(map.get(key).toString())).append("</tr>");
            });
        bld.append("</table>");
        return bld.toString();
    }
    /**
     * 文字列を&lt;td&gt;タグで囲って返す。
     * <HR>
     * (130414) 作成<BR>
     * @param element
     * @return &lt;td&gt;タグで囲った文字列
     */
    private static final String td(final String element){
        return "<td>" + element + "</td>";
    }
    /**
     * HTML のタイトルタグで囲って返す。
     * <HR>
     * (130414) 作成
     * @param title タイトル文字列
     * @return title タグで囲った文字列
     */
    public static final String toHtmlTitle(final String title) {
        return "<title>" + title + "</title>";
    }
    /**
     * ルビ対応HTMLを生成して返す.
     * @param appear 表示テキスト
     * @param ruby ルビ
     * @return ルビ対応HTML
     */
    public static final String getRuby(
            final String appear,
            final String ruby
            ) {
        return "<ruby><rb>" + appear + "</rb><rp></rp><rt>" + ruby + "</rt><rp></rp></ruby>";
    }
    /**
     * tooltip(独自実装)のHTMLを構築して返す.
     * @param appear 表示テキスト
     * @param content tooltip内テキスト
     * @return tooltipのHTML
     */
    public static final String getTooltip(
            final String appear,
            final String content
        ) {
        return new StringBuilder().append("<a href=\"#\" class=\"tooltip\">")
                .append(appear.trim())
                .append("<span class=\"tooltipBody\">")
                .append(content.trim())
                .append("<span class=\"tooltipAngle\">")
                .append("<span class=\"tooltipAngleInner\" />")
                .append("</span></span></a>")
                .toString();
    }
    /**
     * color対応のHTMLを返す.
     * @param color 文字色
     * @param text テキスト
     * @return color対応のHTML
     */
    public static final String getColor(
            final String color,
            final String text
        ) {
        return getColor(color, null, text);
    }
    /**
     * color対応のHTMLを返す.
     * @param color 文字色
     * @param bgColor 背景色
     * @param text テキスト
     * @return color対応のHTML
     */
    public static final String getColor(
            final String color,
            final String bgColor,
            final String text
        ) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<span style=\"");
        if (StringUtils.isNotBlank(color)) {
            sb.append("color: ").append(color).append("; ");
        }
        if (StringUtils.isNotBlank(bgColor)) {
            sb.append("background-color: ").append(bgColor);
        }
        sb.append("\">").append(text).append("</span>");
        return sb.toString();
    }
    /**
     * テキストのハイパーリンクを生成して返す.
     * @param url URL
     * @param text テキスト
     * @return ハイパーリンクの文字列表現
     */
    public static final String makeLink(
           final String url,
           final String text
           ) {
       return makeLink(url, text, false);
    }
    /**
     * テキストのハイパーリンクを生成して返す.
     * @param url URL
     * @param text テキスト
     * @param openBlank true の時は target=_brank でリンクを生成する.
     * @return ハイパーリンクの文字列表現
     */
    public static final String makeLink(
            final String url,
            final String text,
            final boolean openBlank
            ) {
        final StringBuilder link = new StringBuilder(300);
        link.append("<a href=\"");
        link.append(url);
        link.append("\"");
        if (openBlank) {
            link.append(" target='_blank' rel='noopener'");
        }
        link.append(">");
        link.append(text);
        link.append("</a>");
        return link.toString();
    }
    /**
     * 文字列中から RSS の URL だけを抜き出して Map で返す.
     * <HR>
     * <PRE>
     * String str = WebDocumentUtil.getWebDocs(
     * "http://www.itmedia.co.jp/info/rss/kw.html", "UTF-8").toString();
     * HashSet<String> resSet = StringUtil.getRSSURLs( str );
     * System.out.println( ColleUtil.getStringFromSet(resSet, StringUtil.lineSeparator));
     * </PRE>
     * <HR>
     * (120903) 作成<BR>
     * @param str
     */
    public static HashMap<String,String> getRssUrlsMap( final String str ) {
        final HashMap<String,String> resSet = new HashMap<String,String>(100);
        final Matcher matcher = RDF_PATTERN.matcher(str);
        while (matcher.find()) {
            final String matched = "http://" + matcher.group(1) + ".rdf";
            if(matched.length() < 100){
                //System.out.println("match : " +  matched);
                resSet.put("get",matched);
            }
        }
        return resSet;
    }
    /**
     * html から bodyタグで囲まれている部分を抜き出して返す.
     * @param html 文字列
     * @return bodyタグで囲まれている部分
     */
    public static String extractBody(final String html) {
        final Matcher matcher = BODY_PATTERN.matcher(html);
        return matcher.find() ? matcher.group(1) : html;
    }

    /**
     * 文字列中の HTML タグをすべて除去する.
     * @param html 文字列
     * @return  HTML タグを除去した文字列
     */
    public static String tagRemove(final String html) {
        return html.replaceAll("<.+?>", "");
    }

    /**
     * 文字列中の &lt; と &gt; をエスケープする.
     * @param str 文字列
     * @return 文字列中の &gt; と &lt; をエスケープした文字列
     */
    public static final String tagEscape(final String str) {
        return str.replace("<", "&lt;").replace(">", "&gt;");
    }

    /**
     * HTMLの文字列表現からScript, Style, Commentとタグを取り除いた文字列を返す.
     * @param str HTML 文字列
     * @return Script, Style, Commentとタグを取り除いた文字列
     */
    public static final String tagScriptRemove(final String str) {
        return str.replaceAll("<[S|s]cript(.+?)</[S|s]cript>", "")
                .replaceAll("<[S|s]tyle(.+?)</[S|s]tyle>", "")
                .replaceAll("<[C|c]omment(.+?)</[C|c]omment>", "")
                .replaceAll("<.+?>", "");
    }
    /**
     * 文字列中のタブをすべて除去する.
     * @param str 文字列
     * @return タブを除去した文字列
     */
    public static String tabRemove(final String str) {
        if (str.indexOf("\t") != -1) {
            return str.replaceAll("\t", "");
        } else {
            return str;
        }
    }

    /**
     * extract alt string from title tag.
     * @param titleTags
     * @return title string.
     */
    public static final String extractTitle(final String titleTags) {
        if (StringUtils.isBlank(titleTags)) {
            return titleTags;
        }
        final Matcher matcher = TITLE_PATTERN.matcher(titleTags);
        return matcher.find() ? matcher.group(1) : titleTags;
    }

    /**
     * convert to HTML under line form.
     * @param text string
     * @return HTML under line
     */
    public static String underLine(final String text) {
        return new StringBuilder().append("<u>").append(text).append("</u>").toString();
    }

    /**
     * convert to HTML in-line code block.
     * @param text string
     * @return HTML under line
     */
    public static String inLineCode(final String text) {
        return new StringBuilder(text.length() + 14)
                .append("<code>").append(text).append("</code>").toString();
    }

    /**
     * make hx tag.
     * @param x x
     * @param subheading
     * @return hx tagged string.
     */
    public static String makeHead(final int x, final String subheading) {
        return String.format("<h%d>%s</h%d>", x, subheading, x);
    }

}
