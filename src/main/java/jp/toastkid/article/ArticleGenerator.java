package jp.toastkid.article;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.codehaus.groovy.control.CompilationFailedException;
import org.eclipse.collections.impl.factory.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.text.SimpleTemplateEngine;
import groovy.text.TemplateEngine;
import jp.toastkid.article.converter.MarkdownConverter;
import jp.toastkid.article.converter.PostProcessor;
import jp.toastkid.article.models.Article;
import jp.toastkid.libs.utils.CalendarUtil;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.yobidashi.Config;
import jp.toastkid.yobidashi.Defines;
import jp.toastkid.yobidashi.Config.Key;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Wiki article generator.
 *
 * @author Toast kid
 */
public final class ArticleGenerator {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleGenerator.class);

    /** background directory. */
    private static final String USER_BACKGROUND   = Defines.USER_DIR + "/res/images/background/";

    /** line separator. */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /** txt -> Wiki 変換器. */
    private final MarkdownConverter converter;

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
        this.converter = Mono.<MarkdownConverter>create(
                emitter -> emitter.success(new MarkdownConverter(Config.get("imageDir")))
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
     * <HR>
     * (130414) 引数 title 追加<BR>
     * (130302) 作成<BR>
     * @param content 表示したい HTML 文字列
     * @param title HTML の タイトル
     */
    /*TODO けすpublic final void generateHtml(
            final String content,
            final String title,
            final boolean isTool
            ) {
        final PostProcessor post = new PostProcessor(Config.get(Key.ARTICLE_DIR));
        final String processed   = post.process(content);
        final String subheading  = post.generateSubheadings();
        FileUtil.outPutStr(
            decorate(title, processed, subheading),
            Defines.TEMP_FILE_NAME,
            Defines.ARTICLE_ENCODE
        );
    }*/

    /**
     * Decorate HTML content with template and CSS.
     * @param title
     * @param processed
     * @return decorated HTML content
     */
    public String decorate(final String title, final File file) {

        final PostProcessor post = new PostProcessor(Config.get(Key.ARTICLE_DIR));
        final String processed   = post.process(convertToHtml(file));
        final String subheading  = post.generateSubheadings();
        return decorate(title, processed, subheading);
    }

    /**
     * Decorate HTML content with template and CSS.
     * @param title
     * @param processed
     * @param subheading
     * @return decorated HTML content
     */
    public String decorate(final String title, final String processed, final String subheading) {
        return ArticleGenerator.bindArgs(
            Defines.TEMPLATE_DIR + "/main.html",
            new HashMap<String, String>(){
                /** default uid. */
                private static final long serialVersionUID = 1L;
            {
                put("installDir",  Defines.findInstallDir());
                put("title",       title);
                put("wikiIcon",    Config.get("wikiIcon"));
                put("wikiTitle",   Config.get("wikiTitle", "Wiklone"));
                put("subheadings", subheading);
                put("jarPath",     getClass().getClassLoader().getResource("assets/").toString());
                put("content",
                    new StringBuilder()
                        .append("<div class=\"content-area\">")
                        .append(LINE_SEPARATOR)
                        .append(processed)
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
        });
    }

    /**
     * {@link MarkdownConverter} で変換した結果を返す.
     * @param article Article object
     * @return 変換後の HTML 文字列
     */
    public String convertToHtml(final Article article) {
        return convertToHtml(article.file);
    }

    /**
     * {@link MarkdownConverter} で変換した結果を返す.
     * @param file Article file
     * @return 変換後の HTML 文字列
     */
    public String convertToHtml(final File file) {
        final String absolutePath = file.getAbsolutePath();
        return converter.convert(absolutePath , Defines.ARTICLE_ENCODE) + "<hr/>Last Modified： "
                + CalendarUtil.longToStr(
                        new File(absolutePath).lastModified(), MarkdownConverter.STANDARD_DATE_FORMAT);
    }

    /**
     * Make new file's content.
     * @param article Article object
     * @return new file's content
     */
    public static byte[] makeNewContent(final Article article) {
        final Optional<String> ext = FileUtil.findExtension(article.file);
        if (!ext.isPresent()) {
            throw new IllegalArgumentException();
        }

        if (Article.Extension.MD.text().equals(ext.get())) {
            try {
                return newMarkdown(article.title).getBytes(Defines.ARTICLE_ENCODE);
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
        return bindArgsInternal(
                FileUtil.readLinesFromStream(pathToTemplate, Defines.ARTICLE_ENCODE), params);
    }

    private static final String bindArgsInternal(
            final List<String> strs,
            final Map<String, String> params
            ) {
        try {
            return TEMPLATE_ENGINE.createTemplate(
                    Lists.immutable.ofAll(strs).makeString(LINE_SEPARATOR)).make(params).toString();
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
    public static String titleToFileName(final String str){
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
        final List<String> strl = new ArrayList<>();
        final byte[] b = new byte[(bytestr.length() / 2)];
        final StringBuilder temp = new StringBuilder(5);
        for (int i = 0; i < bytestr.length(); i++) {
            temp.append(bytestr.charAt(i));
            if(temp.length() == 2){
                strl.add(temp.toString());
                temp.delete(0, temp.length());
            }
        }
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
     * Return background image URL.
     * @return image URL
     */
    public String getBackground() {
        return chooser.choose();
    }

}
