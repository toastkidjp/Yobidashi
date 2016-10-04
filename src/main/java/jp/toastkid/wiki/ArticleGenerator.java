package jp.toastkid.wiki;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.groovy.control.CompilationFailedException;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ik.marked4j.Marked;
import am.ik.marked4j.MarkedBuilder;
import groovy.text.SimpleTemplateEngine;
import groovy.text.TemplateEngine;
import jp.toastkid.libs.fileFilter.ImageFileFilter;
import jp.toastkid.libs.markdown.MarkdownConverter;
import jp.toastkid.libs.utils.CalendarUtil;
import jp.toastkid.libs.utils.CollectionUtil;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.wiki.lib.PostProcessor;
import jp.toastkid.wiki.lib.WikiConverter;
import jp.toastkid.wiki.models.Article;
import jp.toastkid.wiki.models.Config;
import jp.toastkid.wiki.models.Config.Key;
import jp.toastkid.wiki.models.Defines;
import jp.toastkid.wiki.models.ViewTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Wiki article generator.
 *
 * @author Toast kid
 *
 */
public final class ArticleGenerator {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleGenerator.class);

    /** Gallery 全体のView. */
    private static final String PATH_GALLERY      = Defines.TEMPLATE_DIR + "/gallery.html";

    /** Gallery 1section分のTemplate. */
    private static final String PATH_GALLERY_ITEM = Defines.TEMPLATE_DIR + "/gallery_item.html";

    /** Slide template. */
    private static final String PATH_SLIDE        = Defines.TEMPLATE_DIR + "/slide.html";

    /** background directory. */
    private static final String USER_BACKGROUND   = Defines.USER_DIR + "/res/images/background/";

    /** for use background graphic in slide. */
    private static final String BG_STATEMENT_DELIMITER = "\\|";

    /** for use background graphic in slide. */
    private static final String BG_ELEMPAIR_DELIMITER = "=";

    /** for use background graphic in slide. */
    private static final Pattern BG_PATTERN
        = Pattern.compile("\\{background:(.+?)\\}", Pattern.DOTALL);

    /** Markdown Converter. */
    private static final Marked MARKED = new MarkedBuilder().gfm(true).build();

    /** line separator. */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /** txt -> Wiki 変換器. */
    private final WikiConverter converter;

    /** リンクのプレフィクス. */
    public String prefix;

    /** Image file chooser. */
    private final ImageChooser chooser;

    /** Groovy template engine. */
    private static final TemplateEngine TEMPLATE_ENGINE = new SimpleTemplateEngine();

    /**
     * init functions.
     * @param conf
     */
    public ArticleGenerator() {
        final long start = System.currentTimeMillis();
        this.converter = Mono.<WikiConverter>create(emitter ->
            emitter.success(new WikiConverter(Config.get("imageDir"), Config.get("articleDir")))
        )
        .subscribeOn(Schedulers.elastic())
        .block();
        this.converter.openLinkBrank = true;
        LOGGER.info("ended init converter. {}[ms]", System.currentTimeMillis() - start);

        chooser = new ImageChooser(USER_BACKGROUND);
        LOGGER.info("ended init ImageChooser. {}[ms]", System.currentTimeMillis() - start);
    }

    /**
     * 引数として渡した HTML 文字列を一時ファイルに出力する.
     * 主に HTML ツールの呼び出しで使用
     * @param content 表示したい HTML 文字列(StringBuilder)
     * @param title HTML の タイトル
     */
    public final void generateHtml(final StringBuilder content, final String title) {
        generateHtml(content.toString(), title);
    }

    /**
     * 引数として渡した HTML 文字列を一時ファイルに出力する.
     * 主に HTML ツールの呼び出しで使用
     * <HR>
     * (130414) 引数 title 追加<BR>
     * (130302) 作成<BR>
     * @param content 表示したい HTML 文字列
     * @param title HTML の タイトル
     */
    public final void generateHtml(final String content, final String title) {
        generateHtml(content, title, false);
    }

    /**
     * 引数として渡した HTML 文字列を一時ファイルに出力する.
     * 主に HTML ツールの呼び出しで使用
     * <HR>
     * (130414) 引数 title 追加<BR>
     * (130302) 作成<BR>
     * @param content 表示したい HTML 文字列
     * @param title HTML の タイトル
     */
    public final void generateHtml(
            final String content,
            final String title,
            final boolean isTool
            ) {
        final ViewTemplate template = ViewTemplate.parse(Config.get("viewTemplate"));
        final PostProcessor post = new PostProcessor(Config.get(Key.ARTICLE_DIR));
        final String processed = post.process(content);
        final String subheading = post.generateSubheadings(template);
        FileUtil.outPutStr(
            ArticleGenerator.bindArgs(
                template.getPath(),
                new HashMap<String, String>(){
                    /** default uid. */
                    private static final long serialVersionUID = 1L;
                {
                    put("installDir",  Defines.findInstallDir());
                    put("title",       title);
                    put("wikiIcon",    Config.get("wikiIcon"));
                    put("wikiTitle",   Config.get("wikiTitle", "Wiklone"));
                    put("subheadings", subheading);
                    put("menu",        post.process(makeMenu(template)));
                    put("content",
                        new StringBuilder().append("<div class=\"body\">")
                            .append(LINE_SEPARATOR)
                            .append("<div class=\"content-area\">")
                            .append(LINE_SEPARATOR)
                            .append(processed)
                            .append("</div>")
                            .toString()
                            );
                    if (new File(USER_BACKGROUND).exists()) {
                        final String choose = chooser.choose();
                        final String bodyAdditional = choose.isEmpty()
                                ? ""
                                : String.format("style=\"background-image: url('%s');\" ", choose);
                        put("bodyAdditional", bodyAdditional);
                    } else {
                        put("bodyAdditional", "");
                    }
                }
            }),
            Defines.TEMP_FILE_NAME,
            Defines.ARTICLE_ENCODE
        );
    }

    /**
     * generate html slide powered by reveal.js.

     * @param title title of slide
     * @param content HTML content
     * @param theme theme of slide
     */
    public void generateSlide(final String title, final String content, final String theme) {
        FileUtil.outPutStr(
                ArticleGenerator.bindArgs(
                    PATH_SLIDE,
                    Maps.mutable.of("title", title, "content", content, "theme", theme)
                ),
                Defines.TEMP_FILE_NAME,
                Defines.ARTICLE_ENCODE
            );
    }

    /**
     * テンプレートに合わせた Menubar を返す.
     * @param tmpl ViewTemplate
     * @return テンプレートに合わせた Menubar
     */
    private final String makeMenu(final ViewTemplate tmpl) {
        switch (tmpl) {
            case MATERIAL:
            case SECOND:
                return converter.makeMenubar(Config.get("articleDir"));
            default:
                return "";
        }
    }

    /**
     * {@link WikiConverter} で変換した結果を返す.
     * @param absolutePath 記事ファイルのパス
     * @return 変換後の HTML 文字列
     */
    public String wiki2Html(final String absolutePath) {
        return converter.convert(absolutePath, Defines.ARTICLE_ENCODE) + "<hr/>Last Modified： "
                + CalendarUtil.longToStr(
                        new File(absolutePath).lastModified(), WikiConverter.STANDARD_DATE_FORMAT);
    }

    /**
     * {@link MarkdownConverter} で変換した結果を返す.
     *
     * @param absolutePath 記事ファイルのパス
     * @return 変換後の HTML 文字列
     */
    public String md2Html(final String absolutePath) {
        try {
            LOGGER.info("PATH = " + absolutePath);
            final Path path = Paths.get(absolutePath);
            final List<String> source = Files.readAllLines(path, StandardCharsets.UTF_8);
            source.add("");
            source.add("---");
            source.add("Last updated： "
                    + CalendarUtil.toUniTypeDate(Files.getLastModifiedTime(path).toMillis()));
            return MARKED.marked(CollectionUtil.implode(source));
        } catch (final IOException e) {
            LOGGER.error("Caught error.", e);
        }
        return "";
    }

    /**
     * TODO write test.
     * @param fileName file name
     * @return new file's content
     */
    public static byte[] makeNewContent() {
        final Optional<String> ext = FileUtil.findExtension(Config.article.file);
        if (!ext.isPresent()) {
            throw new IllegalArgumentException();
        }
        if (Article.Extension.WIKI.text().equals(ext.get())) {
            try {
                return newArticle(Config.article.title).getBytes(Defines.ARTICLE_ENCODE);
            } catch (final UnsupportedEncodingException e) {
                LOGGER.error("Caught error.", e);
            }
        }
        if (Article.Extension.MD.text().equals(ext.get())) {
            try {
                return newMarkdown(Config.article.title).getBytes(Defines.ARTICLE_ENCODE);
            } catch (final UnsupportedEncodingException e) {
                LOGGER.error("Caught error.", e);
            }
        }
        return new byte[0];
    }

    /**
     * 記事新規作成時のひな型を生成して返す.
     * <HR>
     * (130324) ひな型を一部修正<BR>
     * (130112) 無駄に長い処理だったのでメソッドに分離<BR>
     * @param convertedName 自然言語に戻したファイル名
     * @return 記事のひな型
     */
    private static final String newArticle(final String convertedName){
        final StringBuilder newContent = new StringBuilder(2000);
        newContent.append("* ");
        // (130112) 修正
        if (Defines.isMyUse && convertedName.startsWith("日記20") ){
            newContent.append(convertedName.substring(2,convertedName.length()));
        } else {
            newContent.append(convertedName);
        }

        newContent.append(LINE_SEPARATOR);
        if (Defines.isMyUse && convertedName.startsWith("日記20") ){
            newContent.append("未記入").append(LINE_SEPARATOR);
            newContent.append(LINE_SEPARATOR);
            newContent.append("** 消灯").append(LINE_SEPARATOR);
            newContent.append("時分に消灯し、寝る。").append(LINE_SEPARATOR);
            newContent.append(LINE_SEPARATOR);
            // 土日祝分でなければ日経平均記入欄を追加
            if (!convertedName.endsWith("(土)")
                && !convertedName.endsWith("(日)")
                && !convertedName.endsWith("祝)")
                ){
                newContent.append("** 今日の日経平均株価終値").append(LINE_SEPARATOR);
                newContent.append("円(円高安)").append(LINE_SEPARATOR);
                newContent.append(LINE_SEPARATOR);
            }
            // 家計簿欄を追加
            newContent.append("** 家計簿").append(LINE_SEPARATOR);
            newContent.append("| | 円").append(LINE_SEPARATOR);
            newContent.append("| | 円").append(LINE_SEPARATOR);
            newContent.append(LINE_SEPARATOR);
        }
        return newContent.toString();
    }

    /**
     * Markdownのひな型を生成して返す.
     * <HR>
     * (130324) ひな型を一部修正<BR>
     * (130112) 無駄に長い処理だったのでメソッドに分離<BR>
     * @param convertedName 自然言語に戻したファイル名
     * @return 記事のひな型
     */
    private static final String newMarkdown(final String convertedName){
        final StringBuilder newContent = new StringBuilder(2000);
        newContent.append("# ");
        // (130112) 修正
        if (Defines.isMyUse && convertedName.startsWith("日記20") ){
            newContent.append(convertedName.substring(2,convertedName.length()));
        } else {
            newContent.append(convertedName);
        }

        newContent.append(LINE_SEPARATOR);
        if (Defines.isMyUse && convertedName.startsWith("日記20") ){
            newContent.append("未記入").append(LINE_SEPARATOR);
            newContent.append(LINE_SEPARATOR);
            newContent.append("## 消灯").append(LINE_SEPARATOR);
            newContent.append("時分に消灯し、寝る。").append(LINE_SEPARATOR);
            newContent.append(LINE_SEPARATOR);
            // 土日祝分でなければ日経平均記入欄を追加
            if (!convertedName.endsWith("(土)")
                && !convertedName.endsWith("(日)")
                && !convertedName.endsWith("祝)")
                ){
                newContent.append("## 今日の日経平均株価終値").append(LINE_SEPARATOR);
                newContent.append("円(円高安)").append(LINE_SEPARATOR);
                newContent.append(LINE_SEPARATOR);
            }
            // 家計簿欄を追加
            newContent.append("## 家計簿").append(LINE_SEPARATOR);
            newContent.append("| 品目 | 金額 |").append(LINE_SEPARATOR);
            newContent.append("|:---|:---|").append(LINE_SEPARATOR);
            newContent.append("| | 円").append(LINE_SEPARATOR);
            newContent.append("| | 円").append(LINE_SEPARATOR);
            newContent.append(LINE_SEPARATOR);
        }
        return newContent.toString();
    }

    /**
     * テンプレートにパラメータをセットして返す.
     * @param pathToTemplate テンプレートファイルのパス
     * @param params パラメータ
     * @return パラメータをセットしたテンプレートの文字列表現
     */
    public static final String bindArgs(
            final String pathToTemplate,
            final Map<String, String> params
            ) {
        try {
            return TEMPLATE_ENGINE.createTemplate(
                    Lists.immutable.ofAll(
                    FileUtil.readLinesFromStream(pathToTemplate, Defines.ARTICLE_ENCODE)
                    ).makeString(LINE_SEPARATOR)).make(params).toString();
        } catch (final CompilationFailedException | ClassNotFoundException | IOException e) {
            LOGGER.error("Caught error.", e);
        }
        return "";
    }

    /**
     * 引数で渡した文字列を、EUC-JPの16進数表現に変換して返す.
     * byteStrConvert()メソッドと逆の処理をする.<BR>
     * <BR>
     * 「電子書籍は紙の本より読書スピード遅い??専門家がテスト」<BR>
     * という文字列を<BR>
     * 「C5C5BBD2BDF1C0D2A4CFBBE6A4CECBDCA4E8A4EAC6C9BDF1A5B9A5D4A
     *1BCA5C9C3D9A4A4A1BDA1BDC0ECCCE7B2C8A4ACA5C6A5B9A5C8」<BR>
     * というEUC-JPのバイト列に変換できる.<BR>
     * 主に「ひとりWiki」関連のファイルを扱う際に使用する.
     * @param str
     * @return str の EUC-JP の16進数表現
     */
    public static String toBytedString_EUC_JP(final String str){
        //String converted = bytedStrDecode(str, "EUC-JP");
        byte[] by = null;
        try {
            by = str.getBytes("EUC-JP");
        } catch (final UnsupportedEncodingException e) {
            LOGGER.error("Caught error.", e);
        }

        final StringBuilder buf = new StringBuilder(by.length * 3);
        for(int i = 0; i < by.length; i++){
            final String hexStr = Long.toHexString(by[i]);
            final String substring = hexStr.substring(hexStr.length() - 2, hexStr.length())
                    .toUpperCase();
            buf.append(substring);
        }
        return buf.toString();
    }

    /**
     * バイト列を文字列に復号する.toBytedString_EUC_JP()メソッドと逆の処理をする.<BR>
     * 例えば、<BR>
     * 「C5C5BBD2BDF1C0D2A4CFBBE6A4CECBDCA4E8A4EAC6C9BDF1A5B9A5D4A1BCA
     *5C9C3D9A4A4A1BDA1BDC0ECCCE7B2C8A4ACA5C6A5B9A5C8」<BR>
     * というEUC-JPのバイト列を<BR>
     * 「電子書籍は紙の本より読書スピード遅い??専門家がテスト」<BR>
     * という文字列に復号できる.<BR>
     * <BR>
     * 「ひとりWiki」というアプリケーションで生成されるページの
     * ソースファイル名がEUC-JPのバイト列となっていて、それを文字列に復号するために作った.
     * <BR>
     * @param bytestr バイト列の文字表現
     * @param encode  元の文字コード
     * @return バイト列を複合した文字列
     */
    public static String decodeBytedStr(final String bytestr, final String encode) {
        final Charset charset = Charset.forName(encode);
        final List<String> strl = new ArrayList<String>();
        final byte[] b = new byte[(bytestr.length() / 2)];
        final StringBuilder temp = new StringBuilder(5);
        for (int i = 0; i < bytestr.length(); i++) {
            temp.append(bytestr.charAt(i));
            if(temp.length() == 2){
                strl.add(temp.toString());
                temp.delete(0, temp.length());
            }
        }
        //b = cardinalNumberConvert16(b,strl);
        /**
         * 文字列オブジェクトが10進数表記でない場合に、基数を指定してデータ型に変換します.
         * 16進数表記の文字列の場合でも 0x といった接頭辞は不要です.
         * <b>0x があると、例外がスローされます.</b>
         */
        for(int i = 0; i < strl.size(); i++){
            b[i] = (byte) Long.parseLong(strl.get(i),16);
        }
        final ByteBuffer bybuf = ByteBuffer.wrap(b);
        return charset.decode(bybuf).toString();
    }

    /**
     * ギャラリーのHTMLを生成する.
     */
    public void generateGallery() {
        final List<File[]> dirs = new ArrayList<>();
        final File root = new File(Config.get("imageDir"));
        final String title = "Gallery";
        if (!root.exists() || !root.isDirectory()) {
            generateHtml("<p>画像フォルダ「" + Config.get("imageDir") + "」は存在しません。</p>", title);
            return;
        }

        dirs.add(root.listFiles(new ImageFileFilter(true)));
        final StringBuilder contents = new StringBuilder(4000);
        for (int i = 0; i < dirs.size(); i++) {
            final File[] pics = dirs.get(i);
            if (pics == null || pics.length == 0) {
                continue;
            }

            int colspan = 0;
            // expander
            final String expanderId = "expander_ranking_" + i;

            final StringBuilder table = new StringBuilder(1000);
            table.append("<table><tr>");

            for (final File p : pics) {
                if (p.isDirectory()) {
                    dirs.add(p.listFiles(new ImageFileFilter(true)));
                    continue;
                }
                final String imgPath = FileUtil.getHtmlFilePath(p.getAbsolutePath());
                table.append("<td><p>").append(p.getName()).append("</p><a href=\"")
                    .append(imgPath)
                    .append("\" target=_brank>")
                    // lazyload による遅延ロード
                    .append("<img width=\"150\" height=\"150\" class=\"lazy\" data-original=\"")
                    .append(imgPath)
                    .append("\"></a></td>")
                    .append(System.lineSeparator());
                colspan++;
                if (colspan == 4) {
                    table.append("</tr><tr>");
                    colspan = 0;
                }
            }
            // table close
            table.append("</tr></table>");

            contents.append(bindArgs(
                    PATH_GALLERY_ITEM,
                    Maps.mutable.of(
                            "subtitle",   pics[0].getParent().replace("\\", "/"),
                            "expanderId", expanderId,
                            "table",      table.toString()
                            )
                ));
        }
        generateHtml(
                bindArgs(PATH_GALLERY, Maps.fixedSize.of("content", contents.toString())),
                title
                );
    }

    /**
     * generate article file.
     */
    public void generateArticleFile() {
        generateHtml(convertArticle(Config.article.extention()), Config.article.title);
    }

    /**
     * convert article to html content.
     * @param ext file's extension.
     * @return html content.
     */
    public String convertArticle(final String ext) {
        if (Article.Extension.MD.text().equals(ext)) {
            return md2Html(Config.article.file.getAbsolutePath());
        }
        if (Article.Extension.WIKI.text().equals(ext)) {
            return wiki2Html(Config.article.file.getAbsolutePath());
        }
        return "";
    }

    /**
     * Wiki 記事をスライドに変換する.
     * @return
     */
    public String convertArticle2Slide() {
        final MutableList<String> converted = Lists.mutable.empty();
        final StringBuilder content = new StringBuilder();
        final StringBuilder sectitonOption = new StringBuilder();
        converter.convertToLines(Config.article.file.getAbsolutePath(), Defines.ARTICLE_ENCODE)
            .forEach(str -> {
                if (str.startsWith("{background")) {
                    final Matcher matcher = BG_PATTERN.matcher(str);
                    if (matcher.find()) {
                        ArrayIterate.forEach(
                                matcher.group(1).split(BG_STATEMENT_DELIMITER),
                                elemPair -> {
                                    final String[] splited = elemPair.split(BG_ELEMPAIR_DELIMITER);
                                    switch (splited[0]) {
                                        case "src":
                                            sectitonOption.append("data-background-image=")
                                                .append(splited[1]);
                                            break;
                                        case "repeat":
                                            sectitonOption.append("data-background-repeat=")
                                                .append(splited[1]);
                                            break;
                                    }
                                }
                            );
                    }
                    return;
                }
                if ((str.contains("<hr/>") || str.matches("^<h[1-6r]>.*")) && content.length() != 0) {
                    converted.add(String.format("<section %s>%s</section>",
                            sectitonOption.toString(), content.toString()));
                    content.setLength(0);
                    sectitonOption.setLength(0);
                }
                final String trimmed = str.startsWith("<") ? str.replace("<hr/>", "") : str;
                if (trimmed.length() != 0) {
                    content.append(trimmed).append(LINE_SEPARATOR);
                }
            });
        if (content.length() != 0) {
            converted.add(String.format("<section>%s</section>", content.toString()));
        }
        return converted.makeString(LINE_SEPARATOR);
    }

}
