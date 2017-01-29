package jp.toastkid.libs.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.toastkid.article.models.Articles;
import jp.toastkid.yobidashi.models.Defines;

/**
 * 青空文庫に関する便利クラス
 * @author Toast kid
 * @version 0.0.1
 */
public final class AobunUtils {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AobunUtils.class);

    /** 改ページ記号 */
    public static final String REPAGE = "［＃改ページ］";

    /** ルビの正規表現 */
    private static final Pattern RUBY_PATTERN = Pattern.compile("&ruby\\((.+?)\\)", Pattern.DOTALL);

    /** matcher */
    private static Matcher matcher;

    /**
     * 記事を青空文庫形式のテキストファイルに変換して出力する。
     * @param source 処理対象記事ファイルのパス
     * @param outputTo   出力フォルダ
     */
    public static final void docToTxt(final Path source, final Path outputTo) {
        final String        bookTitle  = getTitleFromPath(source);
        final List<String>  list       = FileUtil.readLines(source, Defines.ARTICLE_ENCODE);
        final List<String>  output     = convert(bookTitle, list);
        outputFile(output, bookTitle, outputTo);
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
            final Path outputDir
            ) {
        final String outputPath = bookTitle.replace("?", "").replace(" ", "_") + ".txt";
        try {
            final byte[] outputBytes = output.stream().collect(Collectors.joining(Strings.LINE_SEPARATOR))
                                                      .getBytes(StandardCharsets.UTF_8);
            Files.write(outputDir.resolve(outputPath), outputBytes);
        } catch (final IOException e) {
            LOGGER.error("ERROR!", e);
        }
    }

    /**
     * ファイルパスを文書タイトルに変換して返す。
     * @param path ファイルパス
     * @return 文書タイトル
     */
    private static String getTitleFromPath(final Path path) {
        return Articles.convertTitle(path.getFileName().toString()).replace("_", " ");
    }

    /**
     * 青空文庫形式に変換する。
     * @param bookTitle 書籍のタイトル
     * @param list      書籍の内容を入れた文字列 List
     * @return 変換した文字列の List
     */
    private static List<String> convert(
            final String       bookTitle,
            final List<String> list
            ) {
        final List<String>  output    = new ArrayList<>(list.size());
        final StringBuilder paragraph = new StringBuilder();
        for (String str : list) {
            if (str.contains("&ruby")) {
                str = reform(str);
            }

            if (str.startsWith("###")) {
                str = str.replaceFirst("###", "");
                output.add("");
                output.add(str + "［＃「" + str + "」は小見出し］");
            } else if (str.startsWith("##")) {
                str = str.replaceFirst("##", "");
                output.add("");
                output.add(str + "［＃「" + str + "」は中見出し］");
            } else if (str.startsWith("#")) {
                str = str.replaceFirst("#", "");
                str = str.trim();
                output.add(str + "［＃「" + str + "」は大見出し］");
            } else if (str.equals("---")) {
                output.add("");
                output.add("---------------------------------------------------");
                output.add("");
            } else if (str.length() == 0
                    || str.startsWith("-")
                    || str.startsWith("1.")
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
        output.add(1, "");
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
        if (!str.contains("&ruby")) {
            return result;
        }

        matcher = RUBY_PATTERN.matcher(str);
        while (matcher.find()) {
            final String[] split = matcher.group(1).split(",");
            final String replacement = split[0] + "《" + split[1] + "》";
            result = str.replaceFirst(RUBY_PATTERN.pattern(), replacement);
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
