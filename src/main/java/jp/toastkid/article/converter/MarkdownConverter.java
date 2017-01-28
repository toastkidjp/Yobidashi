package jp.toastkid.article.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.libs.utils.HtmlUtil;
import jp.toastkid.libs.utils.Strings;
import jp.toastkid.yobidashi.models.Config;
import jp.toastkid.yobidashi.models.Config.Key;

/**
 * This class convert Markdown file to HTML.
 * <HR>
 * <PRE>
 * generateConvertedHTML(
 * "D:/SRC/Stock/MyWikiData/C6FCB5AD323031322D30352D303228BFE529.txt",
 * "EUC-JP",
 * "D:/Study/CitationsTest"
 * );
 * </PRE>
 * @author Toast kid
 */
public final class MarkdownConverter {

    /** logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MarkdownConverter.class);

    /** value separator. */
    private static final String VALUE_SEPARATOR = "\\|";

    /** 既定の日付フォーマット. */
    public  static final String STANDARD_DATE_FORMAT = "yyyy-MM-dd (E) HH:mm:ss.SS";

    /** 既定の日付フォーマットから秒以下を除いたもの. */
    public  static final String WITHOUT_SECONDS      = "yyyy-MM-dd (E) HH:mm";

    /** 折り畳みのデフォルト表示文字列 */
    private static final String MESSAGE_DEFAULT_EXPAND = "ここをクリックすると開きます.";

    /** color pattern. */
    private static final Pattern COLOR_PATTERN
        = Pattern.compile("\\{color:(.+?)\\}(.+?)\\{color\\}", Pattern.DOTALL);

    /**
     * Markdown での取り消し線検出用正規表現.
     */
    private static final Pattern DELETE_MARKDOWN_PATTERN
        = Pattern.compile("~~(.+?)~~", Pattern.DOTALL);

    /**
     * ひとりWiki の&ruby()プラグインを再現するための検出用正規表現.
     * ひとりWiki プラグインとの互換性を持たせるためのもの
     */
    private static final Pattern RUBY_PAT = Pattern.compile("&ruby\\((.+?)\\)", Pattern.DOTALL);

    /**
     * RegEx of hyper link.
     */
    private static final Pattern HYPER_LINK_PATTERN
        = Pattern.compile("\\[(.+?)\\]\\((.+?)\\)", Pattern.DOTALL);

    /**
     * RegEx of Image.
     */
    private static final Pattern IMAGE_LINK_PATTERN
        = Pattern.compile("\\!\\[(.+?)\\]\\((.+?)\\)", Pattern.DOTALL);

    /** Github widget 記述を検出する正規表現. */
    private static final Pattern GITHUB_PATTERN  = Pattern.compile("\\{[g|G]it[h|H]ub:(.+?)\\}", Pattern.DOTALL);

    /** Wikipedia リンク記述を検出する正規表現. */
    private static final Pattern WIKIPEDIA_PATTERN
        = Pattern.compile("\\{[W|w]ikipedia\\:(.+?)\\}", Pattern.DOTALL);

    /** Twitter リンク記述を検出する正規表現. */
    private static final Pattern TWITTER_PATTERN
        = Pattern.compile("\\{[T|t]witter\\:(.+?)\\}", Pattern.DOTALL);

    /** tooltip の正規表現. */
    private static final Pattern TOOLTIP_PATTERN
        = Pattern.compile("\\{tooltip\\:(.+?)\\}", Pattern.DOTALL);

    /** 数値付きリストを検出する正規表現. */
    private static final Pattern ORDERED_LIST_PATTERN
        = Pattern.compile("^[\\d+]\\. ", Pattern.DOTALL);

    /** inline code の正規表現. */
    private static final Pattern INLINE_CODE_PATTERN
        = Pattern.compile("```(.+?)```", Pattern.DOTALL);

    /** yukiwiki's header pattern. */
    private static final Pattern HEADER_PATTERN = Pattern.compile(".\\#*");

    /** 段落の頭に p タグを許容するタグ. (130727) */
    private static final String[] ALLOW_P_TAGS = {
        "<a ",
        "<img ",
        "<del",
        "<ruby"
    };

    /** td's dummy. */
    private static final String DUMMY_TD    = "≪";

    /** th's dummy. */
    private static final String DUMMY_TH    = "≫";

    /** regex replacing dummy. */
    private static final String SPLIT_REGEX = String.format("[%s|%s]+", DUMMY_TD, DUMMY_TH);

    /** Matcher. */
    private Matcher matcher;

    /** メニューバーを含めるか否か. */
    public boolean containsMenubar = true;

    /** リンクを target=_brank で生成するか. */
    public boolean openLinkBrank   = false;

    /** 前回の処理で回収した画像のパス一覧. DocToEpubで参照. */
    public Set<String> latestImagePaths;

    /** Config. */
    private final Config config;

    /**
     * 指定されたパスで変換器を初期化する.
     * @param imgDir 画像のフォルダ
     */
    public MarkdownConverter(final Config config) {
        this.config = config;
    }

    /**
     * .txt ファイルを読み込み、Wiki 変換した文字列を返す.
     * @param filePath 変換するソースのテキストファイルパス
     * @param fileEncode 変換するソースのテキストファイル文字コード
     * @param isContainsMenubar メニューバーを含めるか否か
     * @return txtFilePath の中身を Wiki 変換した文字列
     */
    public String convert(final String filePath, final String fileEncode) {
        return convertToLines(filePath, fileEncode).makeString(Strings.LINE_SEPARATOR);
    }

    /**
     * テキストファイルの中身を読み込み Wiki 変換する.
     * @param filePath テキストファイルのパス
     * @param fileEncode テキストファイルの文字コード
     * @return Wiki 変換された行を入れた List
     */
    public MutableList<String> convertToLines(final String filePath, final String fileEncode) {
        this.latestImagePaths = Sets.mutable.empty();
        MutableList<String> strs = FileUtil.readLines(filePath, fileEncode);

        // ソースディレクトリパスの取り出し
        final StringBuilder dirBuf = new StringBuilder(filePath.length());
        final String dirSeparator = "/";
        // (111229) 以下、フォルダ区切り文字により柔軟な対応を実装
        String temp = filePath;
        if (temp.indexOf("\\") != -1) {
            temp = temp.replace("\\", dirSeparator);
        }
        final String[] splited = temp.split(dirSeparator);
        for (int i = 0; i < splited.length - 1; i++) {
            dirBuf.append(splited[i]);
            dirBuf.append(dirSeparator);
        }

        try {
            strs = wikiConvert(strs, true);
        } catch (final Exception e) {
            LOGGER.error("Caught error!", e);
        }
        return strs;
    }

    /**
     * .txt ファイルを HTML ファイルへ変換する、実際の処理をする部分<BR>
     * ここでは複数行に渡る処理をし、行単位の処理は下位メソッドに投げる.<BR>
     * フッタ・ヘッダが要らない場合はこのメソッドを使うこと.
     * <HR>
     * (121118) pタグの挙動を修正<BR>
     * (121111) 文字数計測機能を追加<BR>
     * (121028) リスト関連の変換のバグに応急処置<BR>
     * (121012) テーブルの閉じ忘れを修正<BR>
     * (120503) テーブル変換処理追加<BR>
     * (120119) &lt;p>タグ関連を修正<BR>
     * (111230) &lt;Pre>タグ変換を修正<BR>
     * (111229) ヘッダ・フッタ処理を上位の convert メソッドに移動<BR>
     * @param sources .txt ファイルを読み込んだ List
     * @param isCount 文字数計測に含めるか否か
     */
    public MutableList<String> wikiConvert(
            final MutableList<String> sources,
            final boolean isCount
            ) {
        final MutableList<String> contents = Lists.mutable.empty();
        boolean isInBlockQuote = false;
        boolean isInP          = false;
        boolean isInTable      = false;
        boolean isInExpand     = false;
        boolean isInFormation  = false;
        boolean isInAA         = false;
        boolean isInCodeBlock  = false;
        boolean isInMap        = false;

        YolpMapBuilder map = null;

        Formation formation = null;
        List<Footballer> team = new ArrayList<>(11);
        int uLTagDepth  = 0;
        int oLTagDepth  = 0;
        /** 何番目の expand か */
        int expanderCount = 0;
        for (int i = 0; i < sources.size(); i++) {
            String str = sources.get(i);
            // 変換前の行を保存しておく
            final String source = str;
            // AA area のところは変換処理をしない.
            str = (isInAA || isInCodeBlock) ? str : convertLine(str);

            final String lineSep = Strings.LINE_SEPARATOR;

            if (str.startsWith("===") && 0 <= (i - 1)) {
                contents.set(i - 1, HtmlUtil.makeHead(1, sources.get(i - 1)));
                contents.set(i, "<p>");
                continue;
            }

            // (130713)
            //isInQuote
            if (str.startsWith(">") && !isInBlockQuote) {
                if (isInP) {
                    contents.add("</p>");
                    isInP = false;
                }
                str = str.length() != 0 ? str.substring(1).trim() : str;
                str = "<blockquote>" + lineSep + str;
                isInBlockQuote = true;
            }
            if (isInTable && !str.startsWith("|")) {
                str = "</table>" + lineSep + str;
                isInTable = false;
            }
            if (!isInTable && str.startsWith("|")) {
                if (isInP) {
                    contents.add( "</p>" );
                    isInP = false;
                }
                contents.add(getTableTag());
                isInTable = true;
            }
            if (isInTable) {
                //120503 追加
                final StringBuffer buf = new StringBuffer(500);
                buf.append("<tr>");
                buf.append(lineSep);
                buf.append("\t");

                if (str.contains("|:---") || str.contains("|---:|")) {
                    continue;
                }

                str = str.replace("|", DUMMY_TD).replace(DUMMY_TD + DUMMY_TD, DUMMY_TH);
                if (str.contains(DUMMY_TH) && str.contains(DUMMY_TD)) {
                    final String[] tableElems = str.split(SPLIT_REGEX);
                    buf.append("<th>");
                    buf.append(tableElems[1]);
                    buf.append("</th>");
                    for (int j = 2; j < tableElems.length; j++) {
                        buf.append("<td>");
                        buf.append(tableElems[j]);
                        buf.append("</td>");
                    }
                } else if (str.contains(DUMMY_TH)) {
                    final String[] tableElems = str.split(DUMMY_TH);
                    for (int j = 1; j < tableElems.length; j++) {
                        buf.append("<th>");
                        buf.append(tableElems[j]);
                        buf.append("</th>");
                    }
                } else {
                    final String[] tableElems = str.split(DUMMY_TD);
                    for (int j = 1; j < tableElems.length; j++) {
                        buf.append("<td>");
                        buf.append(tableElems[j]);
                        buf.append("</td>");
                    }
                }
                buf.append(lineSep);
                buf.append("</tr>");
                buf.append(lineSep);
                str = buf.toString();
            }
            // 箇条書きリスト
            if (!isInCodeBlock && str.startsWith("-") ) {
                if (source.startsWith("--") && uLTagDepth < 2) {
                    if (isInP) {
                        contents.add("</p>");
                        isInP = false;
                    }
                    contents.add("<ul>");
                    if (uLTagDepth == 0) {
                        contents.add("<ul>");
                    }
                    uLTagDepth = 2;
                } else if (source.startsWith("-") && !source.startsWith("--") ) {
                    if (isInP) {
                        contents.add("</p>");
                        isInP = false;
                    }
                    //str = "<ul>" + lineSep + str;
                    if (uLTagDepth == 2) {
                        contents.add("</ul>");
                    }
                    if (uLTagDepth == 0) {
                        contents.add("<ul>");
                    }
                    uLTagDepth = 1;
                }
            }
            // 数字付きリスト
            if (!isInCodeBlock && ORDERED_LIST_PATTERN.matcher(str).find()) {
                if (isInP) {
                    contents.add("</p>");
                    isInP = false;
                }
                if (oLTagDepth == 2) {
                    contents.add("</ol>");
                }
                if (oLTagDepth == 0) {
                    contents.add("<ol>");
                }
                oLTagDepth = 1;
            }
            if (!isInCodeBlock
                    && !isInBlockQuote
                    && uLTagDepth == 0
                    && oLTagDepth == 0
                    && !isInAA
                    ) {
                if ("".equals(source) && isInP) {
                    contents.add("</p>");
                    //str = "</p>";
                    isInP = false;
                } else {
                    // (121118) 追加
                    if (isInP && !allowP(str)) {
                        contents.add("</p>");
                        isInP = false;
                    }
                    if (!isInP && allowP(str)) {
                        contents.add("<p>");
                        isInP = true;
                    }
                }
            }

            if (isInBlockQuote) {
                if (!str.startsWith(">") && !str.startsWith("<block")) {
                    isInBlockQuote = false;
                    final boolean isNotEmpty = str.length() != 0;
                    str = new StringBuilder().append("</blockquote>").append(lineSep)
                            .append(isNotEmpty ? "<p>" + str : "").toString();
                    isInP = isNotEmpty;
                }
                if (str.startsWith(">")) {
                    str = str.replaceFirst("^>", "");
                }
            }
            if (uLTagDepth != 0) {
                if (source.startsWith("-") ) {
                    str =  str.replaceFirst("-*-", "<li>") + "</li>";
                }
                if (!source.startsWith("-") ) {
                    StringBuffer buf = new StringBuffer(uLTagDepth * 5);
                    for(int j = 0; j < uLTagDepth; j++) {
                        buf.append("</ul>");
                        uLTagDepth--;
                        buf.append(lineSep);
                    }
                    str = str + lineSep + buf.toString();
                    buf = null;
                }
            }
            if (oLTagDepth != 0) {
                final boolean find = ORDERED_LIST_PATTERN.matcher(source).find();
                if (source.startsWith("#")) {
                    str = str.replaceFirst("#*#", "<li>") + "</li>";
                }
                if (find) {
                    str = str.replaceFirst(ORDERED_LIST_PATTERN.pattern(), "<li>") + "</li>";
                }
                if (!source.startsWith("#") && !find) {
                    final StringBuffer buf = new StringBuffer(oLTagDepth * 5);
                    for (int j = 0; j < oLTagDepth; j++) {
                        buf.append("</ol>");
                        oLTagDepth--;
                        buf.append(lineSep);
                    }
                    str = str + lineSep + buf.toString();
                }
            }

            // expander
            if (!isInExpand && str.trim().startsWith("{expand") ) {
                isInExpand = true;
                final StringBuilder expandStart = new StringBuilder();
                expandStart.append("<a href=\"JavaScript:open('expander").append(expanderCount)
                    .append("')\">");
                if (str.indexOf(":") != -1) {
                    expandStart.append(str.split(":")[1].replaceFirst("}$", ""));
                } else {
                    expandStart.append(MESSAGE_DEFAULT_EXPAND);
                }
                expandStart.append("</a>");
                contents.add(expandStart.toString());
                expandStart.delete(0, expandStart.length());
                str = "<div class=\"expander\" id=\"expander" + expanderCount  + "\"><p>";
                isInP = true;
            }

            if (isInExpand && str.trim().startsWith("{expand}") ) {
                str = "</div><Script>close(\"expander" + expanderCount + "\");</Script>";
                isInExpand = false;
                expanderCount++;
            }

            // Markdown code block
            if (!isInCodeBlock && str.trim().startsWith("```")) {

                if (isInP) {
                    contents.add("</p>");
                    isInP = false;
                }

                String title = null;
                String type = null;
                final String substr = str.substring(3, str.length());
                if (1 < substr.length()) {
                    final String[] properties = substr.split("\\:");
                    if (0 < properties.length) {
                        type = properties[0];
                    }
                    if (1 < properties.length) {
                        title = "<span class=\"code-title\">"+ properties[1] + "</span>";
                    }
                }//title
                str = new StringBuilder("<pre>")
                        .append(StringUtils.isNotBlank(title) ? title : "").append("<code")
                        .append(StringUtils.isNotBlank(type) ? String.format(" class=\"%s\"", type) : "")
                        .append(">").append(sources.get(++i)).toString();
                isInCodeBlock = true;
            }
            if (isInCodeBlock && (str.trim().startsWith("{code}") || str.trim().startsWith("```")) ) {
                str = "</code></pre>";
                isInCodeBlock = false;
            }


            // map.
            if (str.startsWith("{map:") && !isInMap) {
                /*
                 *  {map:width=600,height=400}
                 *  35.748807,139.80978,レストラン三幸,blue
                 *  35.747296,139.800864,キッチンフライパン,blue
                 *  35.746024,139.802037,いな穂,blue
                 *  {map}
                 */
                isInMap = true;
                map = new YolpMapBuilder();
                map.setAppId(config.get(Key.YID, ""));
                final String[] content = str.substring(5, str.length() - 1).split(VALUE_SEPARATOR);
                for (final String s : content) {
                    //System.out.println(s);
                    final String[] pair = s.split("=");
                    if ("width".equals(pair[0])) {
                        map.setWidth(Integer.parseInt(pair[1]));
                    }
                    if ("height".equals(pair[0])) {
                        map.setHeight(Integer.parseInt(pair[1]));
                    }
                }
                str = "";
            }
            if (isInMap) {
                if (str.contains(",")) {
                    map.pins.add(str);
                    str = "";
                }
                if (str.startsWith("{map}")) {
                    str = map.toString();
                    isInMap = false;
                }
            }

            // Ascii Art.
            if (!isInAA && source.startsWith("{aa}") ) {
                isInAA = true;
                str = "<div class=\"aaArea\">";
                isInP = false;
            }
            if (isInAA) {
                if (!str.contains("<div")) {
                    str = Strings.replace(str, ' ', "&nbsp;").concat("<br/>");
                }
                if (str.startsWith("{aa}")) {
                    str = "</div>";
                    isInAA = false;
                }
            }

            // popupするtooltip
            if (str.indexOf("{tooltip:") != -1) {
                matcher = TOOLTIP_PATTERN.matcher(str);
                while (matcher.find()){
                    final String matches = matcher.group(1);
                    if (matches.indexOf("|") != -1) {
                        final String[] split = matches.split("\\|");
                        if (1 < split.length) {
                            str = str.replaceFirst(
                                    TOOLTIP_PATTERN.pattern(),
                                    HtmlUtil.getTooltip(split[0], split[1])
                                );
                        }
                    }
                }
            }
            // (130917) 実装中
            if (!isInFormation && str.startsWith("{formation")) {
                isInFormation = true;
                formation     = Formation.parseFormation(str);
            }
            if (isInFormation) {
                if (str.indexOf("|") != -1) {
                    team.add(Footballer.getFootballer(str));
                }
                if ("{formation}".equals(str)) {
                    contents.add(Formation.getPitch(team, formation));
                    isInFormation = false;
                    formation   = null;
                    team = new ArrayList<>(11);
                }
                str = "";
            }
            if (str.indexOf("<p></p>") != -1) {
                str = str.replaceAll("<p></p>", "<p>");
                isInP = true;
            }
            contents.add(str);
        }
        // (121028) 以下、後始末
        // (121012) テーブルの閉じ忘れを修正
        if (isInTable) {
            contents.add("</tbody></table>");
        }
        if (0 < uLTagDepth) {
            for (int j = 0; j < uLTagDepth; j++) {
                contents.add("</ul>");
            }
        }
        if (0 < oLTagDepth) {
            for (int j = 0; j < oLTagDepth; j++) {
                contents.add("</ol>");
            }
        }
        if (isInP) {
            contents.add("</p>");
            isInP = false;
        }
        return contents;
    }

    /**
     * return table tag.
     * @return table tag
     */
    private String getTableTag() {
        return "<table class=\"table table-hover table-stripe\">";
    }

    /**
     * p タグを許容するか否かを判定する.
     * @param str
     * @return p タグを許容する場合は true
     */
    private final boolean allowP(final String str) {
        if (!str.startsWith("<")) {
            return true;
        }
        for (int i = 0; i < ALLOW_P_TAGS.length; i++) {
            final String allowTag = ALLOW_P_TAGS[i];
            if (str.startsWith(allowTag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 行単位の変換をする.
     * <HR>
     * (130629) 見出し中にリンクがあるとおかしくなる点を修正中<BR>
     * (130525) name → id に修正し、文中リンクに対応中<BR>
     * (111228) &lastmodify() 追加<BR>
     * @param str
     * @return Wiki 変換された行
     */
    private final String convertLine(String str) {

        if (str.startsWith("#")) {
            final Matcher matcher = HEADER_PATTERN.matcher(str);
            if (matcher.find()) {
                str = HtmlUtil.makeHead(matcher.group(0).length(), matcher.replaceFirst(""));
            }
        }

        if (str.indexOf("---") != -1) {
            str = str.replaceFirst("---*-", "<hr/>");
        }


        if (str.contains("~~")) {
            matcher = DELETE_MARKDOWN_PATTERN.matcher(str);
            while (matcher.find()) {
                str = str.replaceAll(DELETE_MARKDOWN_PATTERN.pattern(),
                        "<del>" + matcher.group(1) + "</del>");
            }
        }

        //&ruby(文字列,ルビ)→<ruby><rb>文字列</rb><rp>(</rp><rt>ルビ</rt><rp>)</rp></ruby>
        if (str.indexOf("&ruby(") != -1) {
            matcher = RUBY_PAT.matcher(str);
            while (matcher.find()) {
                final String[] found = matcher.group(1).split(",");
                if (found.length < 2) {
                    break;
                }
                str = str.replaceFirst(
                        RUBY_PAT.pattern(),
                        HtmlUtil.getRuby(found[0], found[1])
                    );
            }
        }

        // GitHub Widget.
        if (str.toLowerCase().indexOf("github") != -1) {
            matcher = GITHUB_PATTERN.matcher(str);
            while (matcher.find()) {
                str = str.replaceAll(
                        GITHUB_PATTERN.pattern(), GitHubWidgetFactory.make(matcher.group(1)));
            }
        }

        //COLOR_PATTERN
        if (str.indexOf("{color") != -1) {
            matcher = COLOR_PATTERN.matcher(str);
            while (matcher.find()) {
                str = str.replaceAll(
                        COLOR_PATTERN.pattern(),
                        HtmlUtil.getColor(matcher.group(1), matcher.group(2))
                        );
            }
        }

        if (str.indexOf("![") != -1 && str.indexOf("](") != -1) {
            matcher = IMAGE_LINK_PATTERN.matcher(str);
            while (matcher.find()) {
                final String alt  = matcher.group(1);
                final String link = matcher.group(2);
                str = str.replaceFirst(IMAGE_LINK_PATTERN.pattern(), makeImageTag(alt, link));
            }
        }

        if (str.indexOf("[") != -1 && str.indexOf("](") != -1) {
            matcher = HYPER_LINK_PATTERN.matcher(str);
            while (matcher.find()) {
                final String alt  = matcher.group(1);
                final String link = matcher.group(2);
                final StringBuilder generatedLink = new StringBuilder(180);
                generatedLink.append("<a href=\"");
                generatedLink.append(link);
                generatedLink.append("\"");
                if (openLinkBrank) {
                    //generatedLink.append(" target=_brank ");
                }
                generatedLink.append(">");
                generatedLink.append(alt);
                generatedLink.append("</a>");
                str = str.replaceFirst(HYPER_LINK_PATTERN.pattern(), generatedLink.toString());
            }
        }

        // Wikipedia link.
        if (str.indexOf("ikipedia:") != -1) {
            matcher = WIKIPEDIA_PATTERN.matcher(str);
            while (matcher.find()) {
                final String found = matcher.group(1);
                str = str.replaceFirst(
                        WIKIPEDIA_PATTERN.pattern(),
                        Strings.join("<a target=\"_blank\" href=\"https://ja.wikipedia.org/wiki/" + found + "\">", found, "</a>")
                        );
            }
        }

        // Twitter link.
        if (str.indexOf("witter") != -1) {
            matcher = TWITTER_PATTERN.matcher(str);
            while (matcher.find()) {
                final String found = matcher.group(1);
                final boolean isHashTag = found.startsWith("#");
                final StringBuilder link = new StringBuilder()
                    .append("<a target=\"_blank\" href=\"https://twitter.com/")
                    .append(isHashTag ? "hashtag/" : "")
                    .append(isHashTag ? found.substring(1) : found)
                    .append(isHashTag ? "?src=hash" : "")
                    .append("\">").append(found).append("</a>");
                str = str.replaceFirst(TWITTER_PATTERN.pattern(), link.toString());
            }
        }

        // in-line code.
        if (str.contains("```")) {
            matcher = INLINE_CODE_PATTERN.matcher(str);
            while (matcher.find()) {
                final String found = matcher.group(1);
                str = str.replaceFirst(INLINE_CODE_PATTERN.pattern(), HtmlUtil.inLineCode(found));
            }
        }

        /**
         * <a href="http://d.hatena.ne.jp/srkzhr/20090529/1243614146">
         * Matcher#appendReplacement()の問題</a>
         */
        if (str.indexOf("$") != -1) {
            str = str.replace("$", "\\$");
        }
        return str;
    }

    /**
     * Make image tag.
     * @param alt
     * @param link
     * @return
     */
    private String makeImageTag(final String alt, final String link) {
        return "<img src=\"" + link + "\" alt=\"" + alt + "\" />";
    }

}
