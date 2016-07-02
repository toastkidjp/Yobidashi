package jp.toastkid.libs.wiki;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;

import jp.toastkid.gui.jfx.wiki.Functions;
import jp.toastkid.gui.jfx.wiki.models.Config;
import jp.toastkid.gui.jfx.wiki.models.Defines;
import jp.toastkid.libs.calendar.HtmlCalendar;
import jp.toastkid.libs.utils.CalendarUtil;
import jp.toastkid.libs.utils.CollectionUtil;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.libs.utils.HtmlUtil;
import jp.toastkid.libs.utils.Strings;

/**
 * テキストファイルの中身を YukiWiki 風のルールで HTML に変換する.
 * <HR>
 * <PRE>
 * generateConvertedHTML(
 * "D:/SRC/Stock/MyWikiData/C6FCB5AD323031322D30352D303228BFE529.txt",
 * "EUC-JP",
 * "D:/Study/CitationsTest"
 * );
 * </PRE>
 * <HR>
 * (130831) インスタンスを生成して使う形式に変更<BR>
 * (130804) 別名リンク 機能追加<BR>
 * (130803) expand 機能追加<BR>
 * (130727) プラグインが先頭に来ている時に段落タグで囲っていなかったのを修正<BR>
 * (130615) 変数名指定誤りのバグを修正<BR>
 * (130319) "|" での表変換に対応<BR>
 * (121118) ptag<BR>
 * (121020) link の条件分岐を追加<BR>
 * (121019) 数字リスト変換、h1タグ対応<BR>
 * (121014) レンダリング時に、半角%が含まれていると
 * レンダリングに失敗するPlay!のバグを回避する修正を実施<BR>
 * (121014) 設定したソースフォルダからファイルを読み込むよう修正<BR>
 * (121012) テーブルの閉じ忘れを修正<BR>
 * (121010) Android 版をコピペして作成開始<BR>
 * @author 10fmi13
 * @see <a href="http://itref.fc2web.com/java/lang.html">lang</a>
 * @see <a href="http://mimizun.com/log/2ch/tech/1089530578/">【初心者歓迎】iアプリ相談室</a>
 */
public final class WikiConverter {

    /** value separator. */
    private static final String VALUE_SEPARATOR = "\\|";

    /** 既定の日付フォーマット. */
    public  static final String STANDARD_DATE_FORMAT = "yyyy-MM-dd (E) HH:mm:ss.SS";

    /** 既定の日付フォーマットから秒以下を除いたもの. */
    public  static final String WITHOUT_SECONDS      = "yyyy-MM-dd (E) HH:mm";

    /** output encoding. */
    private static String outputEncode = "UTF-8";

    /** 折り畳みのデフォルト表示文字列 */
    private static final String MESSAGE_DEFAULT_EXPAND = "ここをクリックすると開きます.";

    /** メニューバーのファイル名(定数) */
    private static final String MENUBAR_FILE_NAME
        = Functions.toBytedString_EUC_JP("MenuBar") + ".txt";

    /**
     * {center}中央揃え{center}検出用正規表現.
     */
    private static final Pattern CENTER_PATTERN
        = Pattern.compile("\\{center\\}(.+?)\\{center\\}", Pattern.DOTALL);

    /**
     * ひとりWiki の&color()プラグインを再現するための検出用正規表現.
     * ひとりWiki プラグインとの互換性を持たせるためのもの
     */
    private static final Pattern COLOR_PAT = Pattern.compile("&color\\((.+?)\\)", Pattern.DOTALL);

    /** color pattern. */
    private static final Pattern COLOR_PATTERN
        = Pattern.compile("\\{color:(.+?)\\}(.+?)\\{color\\}", Pattern.DOTALL);

    /** background pattern. */
    private static final Pattern BACKGROUND_PATTERN
        = Pattern.compile("\\{bgcolor:(.+?)\\}(.+?)\\{bgcolor\\}", Pattern.DOTALL);

    /**
     * ひとりWiki のボールド表記を再現するための検出用正規表現.
     * ひとりWiki プラグインとの互換性を持たせるためのもの
     */
    private static final Pattern BOLDING_PATTERN = Pattern.compile("''(.+?)''", Pattern.DOTALL);

    /**
     * italic 検出用正規表現.
     */
    private static final Pattern ITALIC_PATTERN = Pattern.compile("'''(.+?)'''", Pattern.DOTALL);

    /**
     * ひとりWiki の&del()プラグインを再現するための検出用正規表現.
     * ひとりWiki プラグインとの互換性を持たせるためのもの
     */
    private static final Pattern DEL_PAT = Pattern.compile("&del\\((.+?)\\)", Pattern.DOTALL);

    /**
     * Markdown での取り消し線検出用正規表現.
     */
    private static final Pattern DELETE_MARKDOWN_PATTERN
        = Pattern.compile("~~(.+?)~~", Pattern.DOTALL);

    /**
     * 下線検出用正規表現.
     */
    private static final Pattern UNDERLINE_PATTERN = Pattern.compile("\\+(.+?)\\+", Pattern.DOTALL);

    /**
     * ひとりWiki の&img()プラグインを再現するための検出用正規表現.
     * ひとりWiki プラグインとの互換性を持たせるためのもの
     */
    private static final Pattern IMG_PAT = Pattern.compile("&img\\((.+)\\)", Pattern.DOTALL);

    /**
     * ひとりWiki の&ruby()プラグインを再現するための検出用正規表現.
     * ひとりWiki プラグインとの互換性を持たせるためのもの
     */
    private static final Pattern RUBY_PAT = Pattern.compile("&ruby\\((.+?)\\)", Pattern.DOTALL);

    /**
     * Confluence の リンク を再現するための検出用正規表現.
     */
    private static final Pattern HYPER_LINK_PATTERN
        = Pattern.compile("\\[(.+?)\\|(.+?)\\]", Pattern.DOTALL);

    /** Github widget 記述を検出する正規表現. */
    private static final Pattern GITHUB_PATTERN  = Pattern.compile("\\{[g|G]it[h|H]ub:(.+?)\\}", Pattern.DOTALL);

    /** Wikipedia リンク記述を検出する正規表現. */
    private static final Pattern WIKIPEDIA_PATTERN
        = Pattern.compile("\\{[W|w]ikipedia\\:(.+?)\\}", Pattern.DOTALL);

    /** Twitter リンク記述を検出する正規表現. */
    private static final Pattern TWITTER_PATTERN
        = Pattern.compile("\\{[T|t]witter\\:(.+?)\\}", Pattern.DOTALL);

    /** Calendar 記述を検出する正規表現. */
    private static final Pattern CALENDAR_PATTERN
        = Pattern.compile("\\{[C|c]alendar\\:(.+?)\\}", Pattern.DOTALL);

    /** tooltip の正規表現. */
    private static final Pattern TOOLTIP_PATTERN
        = Pattern.compile("\\{tooltip\\:(.+?)\\}", Pattern.DOTALL);

    /** Overflow hidden の正規表現. */
    private static final Pattern OVERFLOW_HIDDEN_PATTERN
        = Pattern.compile("\\{hide\\:(.+?)\\}", Pattern.DOTALL);

    /** yukiwiki's header pattern. */
    private static final Pattern HEADER_PATTERN = Pattern.compile(".\\**");

    /** Blockquote tag. */
    private static final String QUOTE_TAG = "{quote}";

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
    /** title. */
    private String title = "";
    /** 画像フォルダのパス. */
    private final String imgDir;
    /** ソースフォルダのパス. */
    private final String sourceDir;
    /** File オブジェクト. */
    private File file;
    /** メニューバーを含めるか否か. */
    public boolean containsMenubar = true;
    /** リンクを target=_brank で生成するか. */
    public boolean openLinkBrank   = false;
    /** 前回の処理で回収した画像のパス一覧. DocToEpubで参照. */
    public Set<String> latestImagePaths;

    /**
     * 指定されたパスで変換器を初期化する.
     * @param imgDir 画像のフォルダ
     * @param sourceDir ソースファイルのフォルダ
     */
    public WikiConverter(final String imgDir, final String sourceDir) {
        this.imgDir    = StringUtils.isNotBlank(imgDir)
                            ? new File(imgDir.replace("\\", "/")).toURI().toString() : "";
        this.sourceDir = sourceDir;
    }
    /**
     * .txt ファイルを YukiWiki のルールに従って HTML ファイルへ変換し、出力する.
     * <HR>
     * @param filePath   コンバートしたいファイルのパス
     * @param fileEncode コンバートしたいファイルのエンコード
     */
    public void convertedToHtml(final String filePath, final String fileEncode) {
        convertedToHtml(filePath, fileEncode, "");
    }
    /**
     * .txt ファイルを YukiWiki のルールに従って HTML ファイルへ変換し、出力する.
     * <HR>
     * (111229) ヘッダ・フッタ処理をこちらに移動
     * @param filePath   コンバートしたいファイルのパス
     * @param fileEncode コンバートしたいファイルのエンコード
     * @param outputDir     コンバート後のファイルを出力するフォルダ
     */
    public void convertedToHtml(
            final String filePath,
            final String fileEncode,
            final String outputDir
            ) {
        final String outputTo = StringUtils.isEmpty(outputDir)
                ? filePath.replaceFirst("\\.txt", ".html")
                :  outputDir + Strings.getDirSeparator() + file.getName().replaceFirst("\\.txt", ".html");
        FileUtil.outPutStr(convert(filePath, fileEncode), outputTo, outputEncode);
    }
    /**
     * .txt ファイルを読み込み、Wiki 変換した文字列を返す.
     * @param filePath 変換するソースのテキストファイルパス
     * @param fileEncode 変換するソースのテキストファイル文字コード
     * @param isContainsMenubar メニューバーを含めるか否か
     * @return txtFilePath の中身を Wiki 変換した文字列
     */
    public String convert(final String filePath, final String fileEncode) {
        return CollectionUtil.implode(convertToLines(filePath, fileEncode), Strings.LINE_SEPARATOR);
    }

    /**
     * テキストファイルの中身を読み込み Wiki 変換する.
     * @param filePath テキストファイルのパス
     * @param fileEncode テキストファイルの文字コード
     * @return Wiki 変換された行を入れた List
     */
    public List<String> convertToLines(final String filePath, final String fileEncode) {
        this.latestImagePaths = Sets.mutable.empty();
        List<String> strs = FileUtil.readLines(filePath, fileEncode);

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
        setFile(filePath);
        final String bytestr = FileUtil.removeExtension(file.getName());
        setTitle(Functions.decodeBytedStr(bytestr, Defines.TITLE_ENCODE));
        strs = wikiConvert(strs, true);
        return strs;
    }

    /**
     * return toggle html.
     * @param id toggle id.
     * @return toggle html.
     */
    private String getToggle(final String id) {
        return "<span class=\"menu-collapse-toggle collapsed\" data-target=\"#" + id + "\" "
                + "data-toggle=\"collapse\"><i class=\"icon "
                + "icon-close menu-collapse-toggle-close\"></i>"
                + "<i class=\"icon icon-add menu-collapse-toggle-default\"></i></span>"
                + "<ul class=\"menu-collapse collapse\" id=\"" + id + "\">";
    }

    /**
     * Make menu bar html.
     * @param articleDirPath
     * @return menu bar html string
     */
    public String makeMenubar(final String articleDirPath) {
        final String menubarFilePath = sourceDir + MENUBAR_FILE_NAME;
        if (!new File(menubarFilePath).exists()) {
            return "";
        }
        final StringBuilder menuBuf = new StringBuilder(2000);
        menuBuf.append("<ul class=\"nav\">");
        // メニューバーソース読み込み
        final List<String> menuBarStrs = FileUtil.readLines(
                menubarFilePath,
                Defines.ARTICLE_ENCODE
                );
        int depth = 0;
        int uid = 0;
        for (int i = 0; i < menuBarStrs.size(); i++) {
            final String str = menuBarStrs.get(i);
            if (str.startsWith("-- ")) {
                final String id = str.substring(str.indexOf(" "));
                if (depth != 2) {
                    menuBuf.append(getToggle("toggle" + uid++)).append(Strings.LINE_SEPARATOR);
                }
                menuBuf.append("<li>").append(convertLine(id)).append("</li>")
                .append(Strings.LINE_SEPARATOR);
                depth = 2;
            }
            if (str.startsWith("- ")) {
                menuBuf.append("<li>").append("<span data-target=\"#toggle").append(uid)
                    .append("\" data-toggle=\"collapse\">")
                    .append(str.substring(str.indexOf(" ")))
                    .append("</span>").append(Strings.LINE_SEPARATOR);
                depth = 1;
            }
            if (StringUtils.isEmpty(str) && depth != 0) {
                menuBuf.append("</ul></li>").append(Strings.LINE_SEPARATOR);
                depth = 0;
            }
        }
        menuBuf.append("</ul>");
        menuBuf.append(Strings.LINE_SEPARATOR);
        menuBuf.append(Strings.LINE_SEPARATOR);
        return menuBuf.toString();
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
     * @param strList .txt ファイルを読み込んだ List
     * @param isCount 文字数計測に含めるか否か
     */
    public List<String> wikiConvert(
            final List<String> strList,
            final boolean isCount
            ) {
        final List<String> contents = new ArrayList<String>(strList.size());
        boolean isInBlockQuote = false;
        boolean isInQuote      = false;
        boolean isInPre        = false;
        boolean isInP          = false;
        boolean isInTable      = false;
        boolean isInExpand     = false;
        boolean isInFormation  = false;
        boolean isInAA         = false;
        boolean isInCodeBlock  = false;
        boolean isInMap        = false;

        YolpMapBuilder map = null;

        int formation = -1;
        List<Footballer> team = new ArrayList<Footballer>(11);
        int uLTagDepth  = 0;
        int oLTagDepth  = 0;
        /** 何番目の expand か */
        int expanderCount = 0;
        for (int i = 0; i < strList.size(); i++) {
            String str = strList.get(i);
            // 変換前の行を保存しておく
            final String source = str;
            // AA area のところは変換処理をしない.
            str = (isInAA || isInQuote || isInCodeBlock) ? str : convertLine(str);

            final String lineSep = Strings.LINE_SEPARATOR;
            if (str.startsWith(" ") && !isInPre && !isInCodeBlock) {
                if (isInP) {
                    contents.add("</p>");
                    isInP = false;
                }
                str = "<pre>" + lineSep + str;
                str = str.replaceFirst(lineSep + " ", lineSep);
                isInPre = true;
            }
            // (130713)
            //isInQuote
            if (str.startsWith(QUOTE_TAG) && !isInQuote) {
                if (isInP) {
                    contents.add("</p>");
                    isInP = false;
                }
                str = str.replace(QUOTE_TAG, "");
                str = str.length() != 0 ? str.substring(1).trim() : str;
                str = "<blockquote>" + lineSep + str;
                isInQuote = true;
            }
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
            if (str.startsWith("-") ) {
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
            // (121019) 数字付きリスト
            if (str.startsWith("#") ) {
                if (source.startsWith("##") && oLTagDepth < 2) {
                    if (isInP) {
                        contents.add("</p>");
                        isInP = false;
                    }
                    contents.add("<ol>");
                    if (oLTagDepth == 0) {
                        contents.add("<ol>");
                    }
                    oLTagDepth = 2;
                } else if (source.startsWith("#") && !source.startsWith("##") ) {
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
            }
            if (!isInPre
                    && !isInCodeBlock
                    && !isInQuote
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
                    if (isInP) {
                        if (!allowP(str)
                                ) {
                            contents.add("</p>");
                            isInP = false;
                        }
                    }
                    if (!isInP && allowP(str)) {
                        contents.add("<p>");
                        isInP = true;
                    }
                }
            }
            if (isInPre
                    && !(str.startsWith(" "))
                    && !(str.startsWith("<pre>"))
                    ) {
                isInPre = false;
                str = "</pre>" + lineSep + "<p>" + str;
                isInP = true;
            }
            if (isInPre) {
                str = str.replaceFirst("^ ", "");
            }

            if (isInQuote && str.startsWith(QUOTE_TAG) && !str.startsWith("<block")) {
                isInQuote = false;
                str = str.replace(QUOTE_TAG, "");
                final boolean isNotEmpty = str.length() != 0;
                str = new StringBuilder().append("</blockquote>").append(lineSep)
                        .append(isNotEmpty ? "<p>" + str : "").toString();
                isInP = isNotEmpty;
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
                if (source.startsWith("#") ) {
                    str =  str.replaceFirst("#*#", "<li>") + "</li>";
                }
                if (!source.startsWith("#") ) {
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

            // code block.
            if (!isInCodeBlock && str.trim().startsWith("{code")) {

                if (isInP) {
                    contents.add("</p>");
                    isInP = false;
                }

                String title = null;
                String type = null;
                final String[] split = str.split(":");
                if (1 < split.length) {
                    final String[] properties = split[1].split(VALUE_SEPARATOR);
                    if (0 < properties.length) {
                        for (final String s : properties) {
                            final String[] prop = s.split("=");
                            switch (prop[0]) {
                                case "style":
                                    type = prop[1].substring(0, prop[1].length() - 1);
                                    break;
                                case "title":
                                    title = "<span class=\"code-title\">"
                                            + prop[1].substring(0, prop[1].length() - 1)
                                            + "</span>";
                                    break;
                            }
                        }
                    }
                }//title
                str = new StringBuilder("<pre>")
                        .append(StringUtils.isNotBlank(title) ? title : "").append("<code")
                        .append(StringUtils.isNotBlank(type) ? String.format(" class=\"%s\"", type) : "")
                        .append(">").append(strList.get(++i)).toString();
                isInCodeBlock = true;
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
                        .append(">").append(strList.get(++i)).toString();
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
                map.setAppId(Config.get("yid", ""));
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
            if (!isInPre && !isInFormation && str.startsWith("{formation")) {
                isInFormation = true;
                formation   = Formation.parseFormation(str);
            }
            if (!isInPre && isInFormation) {
                if (str.indexOf("|") != -1) {
                    team.add(Footballer.getFootballer(str));
                }
                if ("{formation}".equals(str)) {
                    contents.add(Formation.getPitch(team, formation));
                    isInFormation = false;
                    formation   = -1;
                    team = new ArrayList<Footballer>(11);
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

        if (str.startsWith("*")) {
            final Matcher matcher = HEADER_PATTERN.matcher(str);
            if (matcher.find()) {
                str = HtmlUtil.makeHead(matcher.group(0).length(), matcher.replaceFirst(""));
            }
        }

        if (str.startsWith("h") ) {
            final char c = str.charAt(1);
            if (Character.isDigit(c)) {
                final String[] split = str.split("h\\d\\.");
                final int index = split.length - 1;
                final String subheading = (0 < index) ? split[index].trim() : "";
                str = HtmlUtil.makeHead(Character.getNumericValue(c), subheading);
            }
        }

        if (str.indexOf("---") != -1) {
            str = str.replaceFirst("---*-", "<hr/>");
        }
        // ひとりWiki用プラグイン変換
        if (str.indexOf("&br()") != -1) {
            str = str.replaceAll("&br\\(\\)", "<br/>");
        }
        if (str.toLowerCase().indexOf("{lastmodify}") != -1) {
            str = str.replace(
                    "{lastmodify}",
                    CalendarUtil.longToStr(file.lastModified(), STANDARD_DATE_FORMAT)
                );
        }
        //<del>aaaa</del>
        if (str.indexOf("&del(") != -1) {
            matcher = DEL_PAT.matcher(str);
            while (matcher.find()) {
                str = str.replaceAll(DEL_PAT.pattern(), "<del>" + matcher.group(1) + "</del>");
            }
        }

        if (str.contains("~~")) {
            matcher = DELETE_MARKDOWN_PATTERN.matcher(str);
            while (matcher.find()) {
                str = str.replaceAll(DELETE_MARKDOWN_PATTERN.pattern(),
                        "<del>" + matcher.group(1) + "</del>");
            }
        }

        // italic(文字列)
        if (str.indexOf("'''") != -1) {
            matcher = ITALIC_PATTERN.matcher(str);
            while (matcher.find()) {
                str = str.replaceAll(ITALIC_PATTERN.pattern(), "<i>" + matcher.group(1) + "</i>");
            }
        }

        // bolding
        if (str.indexOf("''") != -1) {
            matcher = BOLDING_PATTERN.matcher(str);
            while (matcher.find()) {
                str = str.replaceAll(BOLDING_PATTERN.pattern(), "<b>" + matcher.group(1) + "</b>");
            }
        }
        //&img(代替文字列,画像) →<img src="画像" alt="代替文字列">
        if (str.indexOf("&img(") != -1) {
            matcher = IMG_PAT.matcher(str);
            while (matcher.find()) {
                final String[] found = matcher.group(1).split(",");
                // (121010) 画像フォルダのパスを追加
                str = str.replaceAll(
                        IMG_PAT.pattern(),
                        "<p><img src=\""+ imgDir + found[1] + "\" alt=\"" + found[0] + "\" /></p>"
                        );
                latestImagePaths.add(found[1]);
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
                final String match = matcher.group(1);
                str = str.replaceAll(
                        GITHUB_PATTERN.pattern(),
                        "<div class=\"github-widget\" data-repo=\"" + match + "\"></div>"
                        );
            }
        }
        // color
        if (str.indexOf("&color(") != -1) {
            matcher = COLOR_PAT.matcher(str);
            while (matcher.find()) {
                final String[] found = matcher.group(1).split(",");
                //&color(文字色,背景色,文字列)
                //<span style="color: white; background-color: ">ホワイト</span>
                //<span style="color: white; background-color: black">
                if (found.length == 2) {
                    str = str.replaceAll(
                            COLOR_PAT.pattern(),
                            HtmlUtil.getColor(found[0], found[1])
                            );
                } else if (found.length == 3) {
                    str = str.replaceAll(
                            COLOR_PAT.pattern(),
                            HtmlUtil.getColor(found[0], found[1], found[2])
                            );
                }
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

        if (str.indexOf("{bgcolor") != -1) {
            matcher = BACKGROUND_PATTERN.matcher(str);
            while (matcher.find()) {
                str = str.replaceAll(
                        BACKGROUND_PATTERN.pattern(),
                        HtmlUtil.getColor(null, matcher.group(1), matcher.group(2))
                        );
            }
        }

        // centering.
        if (str.indexOf("{center}") != -1) {
            matcher = CENTER_PATTERN.matcher(str);
            while (matcher.find()) {
                final String found = matcher.group(1);
                //<div style="text-align:center;">まん中</div></li>
                str = str.replace(
                        matcher.group(0),
                        "<div style=\"text-align:center;\">" + found + "</div>"
                        );
            }
        }

        if (str.indexOf("[") != -1 && str.indexOf("|") != -1) {
            matcher = HYPER_LINK_PATTERN.matcher(str);
            while (matcher.find()) {
                final String alt  = matcher.group(1);
                final String link = matcher.group(2);
                final StringBuilder generatedLink = new StringBuilder(180);
                // (121010) ソースフォルダをリンクパスに追加
                generatedLink.append("<a ");
                generatedLink.append("href=\"");
                generatedLink.append(link);
                generatedLink.append("\"");
                if (openLinkBrank) {
                    generatedLink.append(" target=_brank ");
                }
                generatedLink.append(">");
                generatedLink.append(alt);
                generatedLink.append("</a>");
                str = str.replaceFirst(HYPER_LINK_PATTERN.pattern(), generatedLink.toString());
            }
        }
        if (str.indexOf("{hide:") != -1) {
            matcher = OVERFLOW_HIDDEN_PATTERN.matcher(str);
            while (matcher.find()) {
                final String found = matcher.group(1);
                str = str.replaceAll(
                        OVERFLOW_HIDDEN_PATTERN.pattern(),
                        Strings.join(
                            "<span class=\"overflowhidden\" tabIndex=\"0\">", found, "</span>")
                        );
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

        // {calendar}.
        if (str.contains("alendar")) {
            matcher = CALENDAR_PATTERN.matcher(str);
            while (matcher.find()) {
                final String found = matcher.group(1);
                final Calendar cal = Calendar.getInstance();
                if (found.contains("=")) {
                    ArrayAdapter.adapt(found.split(VALUE_SEPARATOR)).select(it -> {return it.contains("=");})
                        .each(it -> {
                            final String[] pair = it.split("=");
                            if (pair.length < 1) {
                                return;
                            }
                            switch (pair[0]) {
                                case "year":
                                    cal.set(Calendar.YEAR,  Integer.parseInt(pair[1]));
                                    break;
                                case "month":
                                    cal.set(Calendar.MONTH, Integer.parseInt(pair[1]) - 1);
                                    break;
                            }
                        });
                }
                str = str.replaceFirst(CALENDAR_PATTERN.pattern(), HtmlCalendar.makeOneMonth(cal));
            }
        }

        // under line.
        if (str.contains("+")) {
            matcher = UNDERLINE_PATTERN.matcher(str);
            while (matcher.find()) {
                final String found = matcher.group(1);
                str = str.replaceFirst(UNDERLINE_PATTERN.pattern(), HtmlUtil.underLine(found));
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
     * タイトルを返す.
     * @return title タイトル
     */
    public final String getTitle() {
        return title;
    }
    /**
     * タイトルをセットする.
     * @param title 設定する title
     */
    public void setTitle(final String title) {
        this.title = title;
    }
    /**
     * txtFilePath が示すファイルが存在する時のみ、 file を セットする.
     * @param txtFilePath ファイルのパス
     */
    private final void setFile(final String txtFilePath) {
        final File newFile = new File(txtFilePath);
        if (newFile != null && newFile.exists()) {
            file = newFile;
        }
    }
}

