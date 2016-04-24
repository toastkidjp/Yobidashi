package jp.toastkid.libs.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import jp.toastkid.gui.jfx.wiki.models.Article;
import jp.toastkid.gui.jfx.wiki.models.Defines;

/**
 * 青空文庫に関する便利クラス
 * @author Toast kid
 * @version 0.0.1
 */
public final class AobunUtils {
    /** 改ページ記号 */
    public static final String REPAGE = "［＃改ページ］";

    /** 処理対象フォルダ */
    private static final String targetDirPath = "D:/MyWiki/MyWikiData";
    /** ルビの正規表現 */
    private static final Pattern RUBY_PATTERN = Pattern.compile("&ruby\\((.+?)\\)", Pattern.DOTALL);
    /** matcher */
    private static Matcher matcher;

    /**
     * @param args
     */
    public static void main(final String[] args) {
        process("(秘)");
    }

    /**
     * 複数ファイルの変換処理を実施する。
     */
    private static void process(final String prefix) {
        final File[] files = new File(targetDirPath).listFiles();
        final int length = files.length;
        for (int i = 0; i < length; i++){
            if (!files[i].isDirectory()) {
                final String title = Article.convertTitle(files[i].getName());
                if (title.startsWith(prefix)) {
                    docToTxt(files[i].getAbsolutePath(), prefix);
                    System.out.println("Now processing (" + (i + 1) + " / " + length + ") ... " + title);
                }
            }
        }
        System.out.println("done.");
    }

    /**
     * 記事を青空文庫形式のテキストファイルに変換して出力する。
     * @param pFilePath 処理対象記事ファイルのパス
     */
    public static final void docToTxt(final String pFilePath) {
        docToTxt(pFilePath, "");
    }

    /**
     * 記事を青空文庫形式のテキストファイルに変換して出力する。
     * @param pFilePath 処理対象記事ファイルのパス
     * @param pPrefix   出力フォルダ名となる
     */
    public static final void docToTxt(
            final String pFilePath,
            final String pPrefix
            ) {
        final String        bookTitle  = getTitleFromPath(pFilePath);
        final List<String>  list       = FileUtil.readLines(pFilePath, Defines.ARTICLE_ENCODE);
        final List<String>  output     = convert(bookTitle, list);
        outputFile(output, bookTitle, pPrefix);
    }

    /**
     * ファイルに出力する。
     * @param output    出力内容
     * @param bookTitle 出力ファイル名
     * @param outputDir 出力フォルダ
     */
    private static void outputFile(
            final List<String> output,
            final String bookTitle,
            final String outputDir
            ) {
        boolean isMkdir = false;
        if (StringUtils.isNotEmpty(outputDir)) {
            isMkdir = FileUtil.mkdir(outputDir);
        }
        String outputPath = bookTitle.replace("?", "").replace(" ", "_") + ".txt";
        if (isMkdir) {
            outputPath = outputDir + Strings.getDirSeparator() + outputPath;
        }
        FileUtil.outPutList(output, outputPath, Defines.ARTICLE_ENCODE);
    }

    /**
     * ファイルパスを文書タイトルに変換して返す。
     * @param filePath ファイルパス
     * @return 文書タイトル
     */
    public static String getTitleFromPath(final String filePath) {
        return Article.convertTitle(new File(filePath).getName()).replace("_", " ");
    }

    /**
     * 青空文庫形式に変換する。
     * @param bookTitle 書籍のタイトル
     * @param list      書籍の内容を入れた文字列 List
     * @return 変換した文字列の List
     */
    public static List<String> convert(
            final String       bookTitle,
            final List<String> list
            ) {
        final List<String>  output    = new ArrayList<String>(list.size());
        final StringBuilder paragraph = new StringBuilder();
        for (String str : list) {
            if (str.contains("&ruby")) {
                str = reform(str);
            }

            if (str.startsWith("***")) {
                str = str.replaceFirst("\\*\\*\\*", "");
                output.add("");
                output.add(str + "［＃「" + str + "」は小見出し］");
            } else if (str.startsWith("**")) {
                str = str.replaceFirst("\\*\\*", "");
                output.add("");
                output.add(str + "［＃「" + str + "」は中見出し］");
            } else if (str.startsWith("*")) {
                str = str.replaceFirst("\\*", "");
                str = str.trim();
                output.add(str/* + "［＃「" + str + "」は大見出し］"*/);
            } else if (str.equals("----")) {
                output.add("");
                output.add("---------------------------------------------------");
                output.add("");
            } else if (str.length() == 0
                    || str.startsWith("-")
                    || str.startsWith("#")
                    ) {
                // パラグラフ単位での収集
                if (paragraph.length() != 0) {
                    output.add(paragraph.toString());
                    paragraph.setLength(0);
                }
                if (str.length() != 0) {
                    output.add(str);
                }
            } else {
                if (paragraph.length() == 0) {
                    paragraph.append("　");
                }
                paragraph.append(str);
            }
        }
        output.set(0, Strings.kigouFullSizeNonDist(bookTitle) + " " + output.get(0));
        output.add(1, Defines.AUTHOR);
        output.add(2, "");
        output.add(3, "---------------------------------------------------");
        output.add(4, "");
        output.add(5, "---------------------------------------------------");
        return output;
    }

    /**
     * 文字列を整形する。
     * @param str 文字列
     * @return 整形後の文字列
     */
    private static String reform(final String str) {
        String result = str;
        if (str.contains("&ruby")) {
            matcher = RUBY_PATTERN.matcher(str);
            while (matcher.find()) {
                final String[] split = matcher.group(1).split(",");
                final String replacement = split[0] + "《" + split[1] + "》";
                result = str.replaceFirst(RUBY_PATTERN.pattern(), replacement);
            }
        }
        return result;
    }

    /**
     * インスタンス生成を許可しない。
     */
    private AobunUtils() {
        // Do nothing.
    }
}
