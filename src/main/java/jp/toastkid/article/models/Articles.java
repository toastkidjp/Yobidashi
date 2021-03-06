/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.article.models;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.text.SimpleTemplateEngine;
import groovy.text.TemplateEngine;
import javafx.scene.control.ListView;
import jp.toastkid.article.models.Article.Extension;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.libs.utils.Strings;

/**
 * Utilities of {@link Article}.
 *
 * @author Toast kid
 */
public final class Articles {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Articles.class);

    /** Groovy template engine. */
    private static final TemplateEngine TEMPLATE_ENGINE = new SimpleTemplateEngine();

    /** 左の ListView で中心をいくつずらすか. */
    private static final int FOCUS_MARGIN = 10;

    /** line separator. */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * Private constructor.
     */
    private Articles() {
        // NOP.
    }

    /**
     * 記事名一覧を返す.
     * @param articleDir dir of articles
     * @return 記事名一覧の Set (ソート済み)
     */
    public static final List<Article> readAllArticleNames(final String articleDir) {
        final Path dir = Paths.get(articleDir);

        if (dir == null || !Files.isReadable(dir)){
            return Collections.emptyList();
        }


        try {
            return Files.list(dir)
                    .map(Article::new)
                    .filter( a -> a.isValid())
                    .collect(Collectors.toList());
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    /**
     * URL から記事ファイル名を取り出す.
     * <HR>
     * (130707) 作成<BR>
     * @param url URL
     * @return 記事ファイル名
     */
    public static final String findFileNameByUrl(final String url) {
        final String[] split = url.split("/");
        return split[split.length - 1];
    }

    /**
     * URL が Wiki 記事であるかを判定する.
     * @param url URL
     * @return Wiki 記事なら true
     */
    public static final boolean isInternalLink(final String url) {
        return url.startsWith(Article.INTERNAL_PROTOCOL);
    }

    /**
     * Return converted title.
     * @param path Path object
     * @return title
     */
    public static final String convertTitle(final Path path) {
        return convertTitle(path.getFileName().toString());
    }

    /**
     * ファイル名(<b>path は不可</b>)を人間の読める形式にして返す.
     * 「.txt」 が入っていても可
     * <HR>
     * (121014) 作成<BR>
     * @param filePath
     * @return ファイル名
     */
    public static final String convertTitle(final String filePath) {
        return (StringUtils.isNotEmpty(filePath))
                   ? decodeBytedStr(FileUtil.removeExtension(filePath), "EUC-JP")
                   : null;
    }

    /**
     * 記事名から Article オブジェクトを生成.
     * @param newFileName ファイル名(パスではない)
     * @return Article オブジェクト
     */
    public static Article findByTitle(final String articleDir, final String newFileName) {
        return find(articleDir, titleToFileName(newFileName) + ".md");
    }

    /**
     * URL から Article オブジェクトを生成.
     * @param url URL
     * @return Article オブジェクト
     */
    public static Article findByUrl(final String articleDir, final String url) {
        return find(articleDir, findFileNameByUrl(url));
    }

    /**
     * fileName から Article オブジェクトを生成.
     * @param fileName ファイル名(パスではない)
     * @param articleDir
     * @return Article オブジェクト
     */
    private static Article find(final String articleDir, final String fileName) {
        return new Article(Paths.get(articleDir, fileName));
    }

    /**
     * distinguish usable file extension.
     *
     * @param p {@link Path} Object
     * @return f is valid file?
     */
    public static boolean isValidContentPath(final Path p) {
        final Optional<String> ext = FileUtil.findExtension(p);
        if (!ext.isPresent()) {
            return false;
        }
        return Extension.MD.text().equals(ext.orElseGet(Strings::empty));
    }

    /**
     * Generate new article file.
     * @param newArticle
     */
    public static void generateNewArticle(final Article newArticle) {
        try {
            Files.write(newArticle.path, makeNewContent(newArticle));
        } catch (final IOException e) {
            LOGGER.error("Error", e);;
        }
    }

    /**
     * Make new file's content.
     * @param article Article object
     * @return new file's content
     */
    private static byte[] makeNewContent(final Article article) {
        try {
            return newMarkdown(article.title).getBytes(Article.ENCODE);
        } catch (final UnsupportedEncodingException e) {
            LOGGER.error("Caught error.", e);
        }
        return new byte[0];
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
        if (convertedName.startsWith("日記20") ){
            newContent.append(convertedName.substring(2,convertedName.length()));
        } else {
            newContent.append(convertedName);
        }

        newContent.append(LINE_SEPARATOR);
        if (convertedName.startsWith("日記20") ){
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
            final String pathToTemplate, final Map<String, String> params) {
        return bindArgsInternal(FileUtil.readLinesFromStream(pathToTemplate, Article.ENCODE), params);
    }

    /**
     * Internal method.
     * @param strs
     * @param params
     * @return
     */
    private static final String bindArgsInternal(
            final List<String> strs, final Map<String, String> params) {
        try {
            return TEMPLATE_ENGINE
                    .createTemplate(strs.stream().collect(Collectors.joining(LINE_SEPARATOR)))
                    .make(params)
                    .toString();
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
     * Focus ListView on this item.
     * @param listView
     */
    public static void focus(final Article article, final ListView<Article> listView) {
        final int indexOf = listView.getItems().indexOf(article);
        if (indexOf != -1){
            listView.getSelectionModel().select(indexOf);
            listView.scrollTo(indexOf - FOCUS_MARGIN);
            return;
        }
        listView.getSelectionModel().clearSelection();
    }

}
