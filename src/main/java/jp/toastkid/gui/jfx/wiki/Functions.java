package jp.toastkid.gui.jfx.wiki;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;
import org.eclipse.collections.impl.utility.ArrayIterate;

import am.ik.marked4j.Marked;
import am.ik.marked4j.MarkedBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import jp.toastkid.gui.jfx.wiki.models.Article;
import jp.toastkid.gui.jfx.wiki.models.Config;
import jp.toastkid.gui.jfx.wiki.models.Config.Key;
import jp.toastkid.gui.jfx.wiki.models.Defines;
import jp.toastkid.gui.jfx.wiki.models.Resources;
import jp.toastkid.gui.jfx.wiki.models.ViewTemplate;
import jp.toastkid.libs.Zip;
import jp.toastkid.libs.comparator.NumberMapComparator;
import jp.toastkid.libs.epub.DocToEpub;
import jp.toastkid.libs.epub.EpubMetaData;
import jp.toastkid.libs.epub.PageLayout;
import jp.toastkid.libs.epub.PageProgressDirection;
import jp.toastkid.libs.fileFilter.ImageFileFilter;
import jp.toastkid.libs.markdown.MarkdownConverter;
import jp.toastkid.libs.tinysegmenter.TinySegmenter;
import jp.toastkid.libs.utils.CalendarUtil;
import jp.toastkid.libs.utils.CollectionUtil;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.libs.utils.Strings;
import jp.toastkid.libs.wiki.PostProcessor;
import jp.toastkid.libs.wiki.WikiConverter;

/**
 * WikiClient で共通して使えるメソッドを収録.
 * @author Toast kid
 *
 */
public final class Functions {
    /** テンプレート内パラメータの検出用パターン. */
    private static final Pattern PARAM_TEMPLATE_PATTERN
        = Pattern.compile("\\$\\{(.+?)\\}", Pattern.DOTALL);

    /** Markdown Converter. */
    private static final Marked MARKED = new MarkedBuilder().gfm(true).build();

    /** line separator. */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /** txt -> Wiki 変換器. */
    private final WikiConverter converter;

    /** 形態素解析ライブラリ. */
    private final TinySegmenter ts;

    /** リンクのプレフィクス. */
    public String prefix;

    /**
     * init functions.
     * @param conf
     */
    public Functions() {
        this.converter = new WikiConverter(Config.get("imageDir"), Config.get("articleDir"));
        converter.openLinkBrank = true;
        ts = TinySegmenter.getInstance();
        ts.isAllowChar     = false;
        ts.isAllowHiragana = false;
        ts.isAllowNum      = false;
    }

    /**
     * 印刷用HTMLを生成してファイルに出力する.
     * @param title
     */
    public final void generateHtmlForPrint(final String title) {
        final String converted
            = converter.convert(Config.article.file.getAbsolutePath(), Defines.ARTICLE_ENCODE);
        final String content = bindArgs(
                "src/main/views/print.html",
                Maps.fixedSize.of(
                        "title",      title,
                        "installDir", findInstallDir(),
                        "body",       converted
                        )
                );
        FileUtil.outPutStr(content, Defines.TEMP_FILE_NAME, "utf-8");
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
            Functions.bindArgs(
                template.getPath(),
                new HashMap<String, String>(){
                    /** default uid. */
                    private static final long serialVersionUID = 1L;
                {
                    put("installDir",  findInstallDir());
                    put("title",       title);
                    put("wikiIcon",    Config.get("wikiIcon"));
                    put("wikiTitle",   Config.get("wikiTitle", "Wiklone"));
                    put("subheadings", subheading);
                    put("menu",        post.process(makeMenu(template)));
                    put("content",
                        new StringBuilder().append("<div class=\"body\">")
                            .append(LINE_SEPARATOR)
                            .append("<div class=\"content_area\">")
                            .append(LINE_SEPARATOR)
                            .append(processed)
                            .append("</div>")
                            .toString()
                            );
                }}),
            Defines.TEMP_FILE_NAME,
            Defines.ARTICLE_ENCODE
        );
    }

    /**
     * generate html slide powered by reveal.js.
     * @param content HTML content.
     * @param title title.
     */
    private void generateSlide(final String content, final String title) {
        FileUtil.outPutStr(
                Functions.bindArgs(
                    Resources.PATH_SLIDE,
                    Maps.fixedSize.of("title", title, "content", content)
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
                return converter.makeMaterialMenubar(Config.get("articleDir"));
            case CLASSIC:
                return converter.makeSecondMenubar(Config.get("articleDir"));
            case SECOND:
                return converter.makeSecondMenubar(Config.get("articleDir"));
            default:
                return "";
        }
    }

    /**
     * 記事名一覧を返す.
     * @return 記事名一覧の Set (ソート済み)
     */
    public static final List<Article> readArticleNames() {
        final File dir = new File(Config.get("articleDir"));
        if (dir == null || !dir.canRead()){
            return Collections.emptyList();
        }
        return ArrayAdapter
                .newArrayWith(dir.listFiles((f) -> {return Article.isValidContentFile(f);}))
                .asParallel(Executors.newFixedThreadPool(24), 24)
                .collect(f -> {return new Article(f);})
                .select( a -> {return a.isValid();})
                .toList();
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
            final Path path = Paths.get(absolutePath);
            final List<String> source = Files.readAllLines(path, StandardCharsets.UTF_8);
            source.add("");
            source.add("---");
            source.add("最終更新： " + Strings.toYmdhmsse(Files.getLastModifiedTime(path).toMillis()));
            return MARKED.marked(CollectionUtil.implode(source));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * ファイルの字数計測結果を文字列にまとめて返す.
     * @param titile ファイルのタイトル
     * @param file   ファイルオブジェクト
     * @param encode ファイルの文字コード
     * @return ファイルの字数計測結果(文字列)
     */
    public final String makeCharCountResult(
            final String title,
            final File file,
            final String encode
        ) {
        return new StringBuilder()
            .append(title).append(" は ")
            .append(FileUtil.countCharacters(file.getAbsolutePath(), encode))
            .append(" 字です。").append(LINE_SEPARATOR)
            .append(file.length() / 1024L).append("[KB]").toString();
    }

    /**
     * 単語頻度マップを作成して返す.
     * @return 単語頻度マップ
     */
    public final Map<String, Integer> makeTermFrequencyMap() {
        final Map<String,Integer> map = new HashMap<String,Integer>(100);
        FileUtil.readLines(Config.article.file, Defines.ARTICLE_ENCODE)
            .stream()
            .filter(str  -> {return StringUtils.isNotEmpty(str);})
            .forEach(str -> {
                ts.segment(str).stream()
                    .map(seg     -> {return seg.replace("\"", "");})
                    .filter(seg  -> {return StringUtils.isNotEmpty(seg);})
                    .forEach(seg -> {map.put(seg, map.getOrDefault(seg, 0) + 1);});
            });
        final Map<String, Integer> resMap = new TreeMap<>(new NumberMapComparator(map));
        resMap.putAll(map);
        return resMap;
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
                e.printStackTrace();
            }
        }
        if (Article.Extension.MD.text().equals(ext.get())) {
            try {
                return newMarkdown(Config.article.title).getBytes(Defines.ARTICLE_ENCODE);
            } catch (final UnsupportedEncodingException e) {
                e.printStackTrace();
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
     * ObservableList の要素をユニークにして返す.
     * @param list ObservableList
     * @return ユニークにした List
     */
    public static final ObservableList<?> getUniqueItemList(final ObservableList<?> list){
        return FXCollections.observableArrayList(Sets.mutable.ofAll(list));
    }

    /**
     * Wiki の HTML テンプレートヘッダを返す.
     * <HR>
     * (130302) 作成<BR>
     * @param installDir アプリケーションのインストールフォルダ
     * @return Wiki コンテンツの文字列表現
     */
    public static final String loadHtmlContent(final String installDir) {
        return Functions.bindArgs(
            ViewTemplate.CLASSIC.getPath(),
            Maps.fixedSize.of("installDir", installDir)
        );
    }

    /**
     * 現在開いている記事をePubに変換する.
     * @param isVertival
     * @param fileName
     */
    public final void toEpub(final boolean isVertival) {
        final EpubMetaData meta = new EpubMetaData();
        meta.recursive = true;
        final String convertTitle = Config.article.title;
        meta.title        = convertTitle;
        meta.subtitle     = convertTitle;
        meta.author       = Defines.AUTHOR;
        meta.editor       = Defines.AUTHOR;
        meta.publisher    = Defines.AUTHOR;
        meta.version      = "0.0.1";
        meta.zipFilePath  = convertTitle + ".epub";
        meta.targetPrefix = convertTitle;
        meta.containInnerLinks = false;
        meta.layout       = isVertival ? PageLayout.VERTICAL : PageLayout.HORIZONTAL;
        meta.direction    = isVertival ? PageProgressDirection.RTL : PageProgressDirection.LTR;
        DocToEpub.run(meta.toString());
    }

    /**
     * DocToEpub を動かし、EPUB_RECIPE_DIR のレシピ json から複数のePubを生成する.
     */
    public final void runEpubGenerator() {
        final List<String> absPathes
            = Stream.of(new File(Defines.EPUB_RECIPE_DIR).listFiles())
                .map((file) -> {return file.getAbsolutePath();})
                .filter((fileName) -> {return fileName.toLowerCase().endsWith(".json");})
                .collect(Collectors.toList());
        DocToEpub.run(absPathes.toArray(new String[]{}));
    }

    /**
     * do simpleBackup.
     * @param articleDir
     * @param offsetMs
     */
    public void simpleBackup(final String articleDir, final long offsetMs) {
        try {
            final Zip backup
                = new Zip("backup" + ZonedDateTime.now().toInstant().getEpochSecond() + ".zip");
            ArrayAdapter.adapt(new File(articleDir).listFiles())
                .select(file -> {return offsetMs < file.lastModified();})
                .each(Procedures.throwing((file) -> {backup.entry(file);}));
            backup.doZip();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 正規化されたファイルパスを返す.
     * @param path ファイルのパス
     * @return 正規化されたファイルパス
     */
    public static final String format4Mp3Player(final String path) {
        return FileUtil.FILE_PROTOCOL + path.replace("\\", "/").replace(" ", "%20");
    }

    /**
     * 妥当なファイルか否かを拡張子で判定する.
     * <HR>
     * (130622) 作成<BR>
     * @param pName
     * @return 妥当な音楽ファイルの拡張子なら true
     */
    public static final boolean isValidMusicFile(final String pName) {
        return Defines.MUSIC_FILES.anySatisfy(suffix -> {
            return pName.toLowerCase().endsWith(suffix);
        });
    }

    /**
     * capture current window.
     * @param fileName output file name.
     * @param rect rectangle size.
     */
    public void capture(final String fileName, final Rectangle rect) {
        final String name
            = fileName.toLowerCase().endsWith(".png") ? fileName : fileName.concat(".png");
        try {
            final BufferedImage img = new Robot().createScreenCapture(rect);
            ImageIO.write(img, "png", new File(name));
        } catch (final IOException | AWTException e) {
            e.printStackTrace();;
        }
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
        final String lineSeparator = System.lineSeparator();
        final ImmutableList<String> templates = Lists.immutable.ofAll(
                FileUtil.readLinesFromStream(pathToTemplate, Defines.ARTICLE_ENCODE)
            );
        final StringBuilder convertedText = new StringBuilder();
        templates.each(template -> {
            if (template.contains("${")) {
                Matcher matcher = Functions.PARAM_TEMPLATE_PATTERN.matcher(template);
                while (matcher.find()) {
                    final String key = matcher.group(1);
                    if (!params.containsKey(key)) {
                        continue;
                    }
                    String value = params.get(key);
                    if (value == null) {
                        continue;
                    }
                    if (value.contains("$") && !value.contains("\\$")) {
                        value = value.replace("$", "\\$");
                    }
                    //System.out.println("value = " + value);
                    template = matcher.replaceFirst(value);
                    matcher = Functions.PARAM_TEMPLATE_PATTERN.matcher(template);
                }
            }
            convertedText.append(template).append(lineSeparator);
        });
        return convertedText.toString();
    }

    /**
     * インストールフォルダを取得して返す.
     * <HR>
     * (130803) 作成<BR>
     * @return インストールフォルダのパス
     */
    public static String findInstallDir() {
        return FileUtil.FILE_PROTOCOL
                + new File(".").getAbsoluteFile().getParent().replace("\\", "/") + "/";
    }

    /**
     * URL から記事ファイル名を取り出す.
     * <HR>
     * (130707) 作成<BR>
     * @param url URL
     * @return 記事ファイル名
     */
    public static final String findFileNameFromUrl(final String url) {
        final String[] split = url.split("/");
        return split[split.length - 1];
    }

    /**
     * URL が Wiki 記事であるかを判定する.
     * @param url URL
     * @return Wiki 記事なら true
     */
    public static final boolean isWikiArticleUrl(final String url) {
        return (url.contains("/txt/") || url.contains("/md/"));
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
            e.printStackTrace();
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
                    Resources.PATH_GALLERY_ITEM,
                    Maps.fixedSize.of(
                            "subtitle",   pics[0].getParent().replace("\\", "/"),
                            "expanderId", expanderId,
                            "table",      table.toString()
                            )
                ));
        }
        generateHtml(
                bindArgs(Resources.PATH_GALLERY, Maps.fixedSize.of("content", contents.toString())),
                title
                );
    }

    /**
     * make empty closable tab.
     * @param title Tab's title
     * @param parent Parent TabPane
     * @return 空の Tab
     */
    public static Tab makeClosableTab(final String title, final TabPane parent) {
        final Tab tab = new Tab(title);
        tab.setClosable(true);

        final Button closeButton = new Button("x");
        closeButton.setOnAction(e -> { parent.getTabs().remove(tab);});
        tab.setGraphic(closeButton);
        return tab;
    }

    /**
     * ツールを閉じる.
     */
    public void close() {
        // Do nothing.
    }

    /**
     * generate article file.
     */
    public void generateArticleFile() {

        final String extension = Config.article.extention();
        if (Article.Extension.SLIDE.text().equals(extension)) {
            final String convertArticle = convertArticle(extension);
            generateSlide(convertArticle, Config.article.title);
            return;
        }
        generateHtml(convertArticle(extension), Config.article.title);
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
        if (Article.Extension.SLIDE.text().equals(ext)) {
            final String content
                = converter.convert(Config.article.file.getAbsolutePath(), Defines.ARTICLE_ENCODE);
            return ArrayAdapter.adapt(content.split("<hr/>"))
                .collect(str -> {return String.format("<section>%s</section>", str);})
                .makeString(LINE_SEPARATOR);
        }
        return "";
    }

    private static final Pattern BG_PATTERN = Pattern.compile("\\{background:(.+?)\\}", Pattern.DOTALL);

    private static final String BG_STATEMENT_DELIMITER = "\\|";
    private static final String BG_ELEMPAIR_DELIMITER = "=";

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
