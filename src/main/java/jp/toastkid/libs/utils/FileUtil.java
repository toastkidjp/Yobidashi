
package jp.toastkid.libs.utils;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.IntLists;

/**
 * ファイル操作に関するユーティリティクラス<BR>
 *
 * ファイルからデータを読みこんでコレクションを生成するメソッドもここに収録しておく<BR>
 * <HR>
 * (130831) get○○ → implode の対応<BR>
 * (130707) StringBuffer を使う部分をすべてStringBuilderに変更<BR>
 * @author Toast kid
 */
public final class FileUtil {

    /** URL's file protocol. */
    public static final String FILE_PROTOCOL = "file:///";

    /** System out. */
    public static final PrintStream SYSOUT = new PrintStream(
            new BufferedOutputStream(new FileOutputStream(FileDescriptor.out), 128), true);

    /** System err. */
    public static final PrintStream SYSERR = new PrintStream(
            new BufferedOutputStream(new FileOutputStream(FileDescriptor.err), 128), true);

    /** 既知の画像ファイル拡張子. */
    private static final String[] IMAGE_FILE_IDENTIFIERS = new String[] {
        ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".ico", "svg"
    };

    /**
     * Private constructor.
     */
    private FileUtil() {
        // NOP.
    }

    /**
     * バッファ処理式ファイルリーダを渡されたファイル名と文字コードで初期化する.
     * @param pTargetFileName リーダで読み込むファイルの名前
     * @param pEncode 初期化する文字コード
     * @return fileReader : 与えたパラメータで初期化したファイルリーダ
     */
    public static BufferedReader makeFileReader(
            final String pTargetFileName,
            final String pEncode
            ) {
        try {
            return makeInputStreamReader(new FileInputStream(pTargetFileName), pEncode);
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * InputStream から BufferedReader を構築して返す.
     * <HR>
     * (130706) 作成<BR>
     * @param pStream
     * @param pEncode
     * @return BufferedReader
     * @throws UnsupportedEncodingException
     */
    private static BufferedReader makeInputStreamReader(
            final InputStream pStream,
            final String pEncode
            ) {
        try {
            return new BufferedReader(new InputStreamReader(pStream, pEncode));
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 指定されたファイルの要素を一行ずつ入れた List<String> を返す.
     * <HR>
     * (130406) 修正<BR>
     * @param pFilePath 指定するファイルのパス
     * @param pEncode 指定するファイルの文字コード
     * @return 指定されたファイルの要素を一行ずつ入れた Set<String>
     */
    public static MutableList<String> readLines(
            final String pFilePath,
            final String pEncode
            ) {
        return readLines(Paths.get(pFilePath), pEncode);
    }

    /**
     * 指定されたファイルの要素を一行ずつ入れた List<String> を返す.
     * <HR>
     * (130406) 修正<BR>
     * @param path 指定するファイルのパス
     * @param pEncode 指定するファイルの文字コード
     * @return 指定されたファイルの要素を一行ずつ入れた Set<String>
     */
    public static MutableList<String> readLines(
            final Path path,
            final String pEncode
            ) {
        if (path == null || !Files.exists(path) || !Files.isReadable(path)) {
            return Lists.fixedSize.empty();
        }

        final MutableList<String> resSet = Lists.mutable.empty();
        try (final BufferedReader fileReader = Files.newBufferedReader(path, Charset.forName(pEncode))) {
            String str = fileReader.readLine();
            while(str != null) {
                resSet.add(str);
                str = fileReader.readLine();
            }
            fileReader.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return resSet;
    }
    /**
     * 指定されたファイルの要素を一行ずつ入れた List<String> を返す.
     * <HR>
     * (130406) 修正<BR>
     * @param filePath 指定するファイルのパス
     * @param pEncode 指定するファイルの文字コード
     * @return 指定されたファイルの要素を一行ずつ入れた Set<String>
     */
    public static List<String> readLinesFromStream(
            final String filePath,
            final String pEncode
            ) {
        final List<String> lines = new ArrayList<>(5000);
        try (final BufferedReader fileReader = makeReader(filePath, pEncode)) {

            if (fileReader == null) {
                return lines;
            }

            String str = fileReader.readLine();
            while(str != null) {
                lines.add(str);
                str = fileReader.readLine();
            }
            fileReader.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    /**
     * Make file reader.
     * @param filePath
     * @param pEncode
     * @return
     */
    private static BufferedReader makeReader(final String filePath, final String pEncode) {
        final InputStream in = FileUtil.getStream(filePath);
        return in != null
                ? FileUtil.makeInputStreamReader(in, pEncode)
                : FileUtil.makeFileReader(filePath, pEncode);
    }

    /**
     * 出力先pOutPutFileNameと文字コードpEncodeで初期化したPrintWriterを生成して返す
     * <HR>
     *
     * 雑なコードだけど、大本なので絶対に修正しないように
     *
     * @param pOutPutFileName 出力先
     * @param pEncode 扱う文字コード
     * @return resWriter
     */
    public static PrintWriter makeFileWriter(
            final String pOutPutFileName,
            final String pEncode
            ) {
        try {
            return new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(
                        new FileOutputStream(pOutPutFileName),
                        pEncode
                    )
                )
            );
        } catch(final IOException iOE) {
            iOE.printStackTrace();
        }
        return null;
    }

    /**
     * 指定したファイルの文字数を計測して返す.
     *
     * @param path
     * @param pEncode
     */
    public static int countCharacters(final Path path, final String pEncode) {
        return countCharacters(path.toAbsolutePath().toString(), pEncode);
    }

    /**
     * 指定したファイルの文字数を計測して返す.
     *
     * @param pFileName
     * @param pEncode
     */
    public static int countCharacters(final String pFileName, final String pEncode) {
        final MutableIntList total = IntLists.mutable.empty();
        try {
            Files.readAllLines(Paths.get(pFileName), Charset.forName(pEncode)).forEach(line -> {
                total.add(line.replaceAll("\\s", "").length());
            });
        } catch (final Exception e) {
            //e.printStackTrace();
            System.out.println(pFileName);
        }
        return (int) total.sum();
    }

    /**
     * ファイルから Stream を取得する.
     * @param filePath
     * @return InputStream オブジェクト
     */
    public static final InputStream getStream(final String filePath) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (final InputStream in = classLoader.getResourceAsStream(filePath);) {
            if (in != null) {
                return new BufferedInputStream(classLoader.getResourceAsStream(filePath));
            }
            return Files.newInputStream(Paths.get(filePath));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 画像か否かを判定する.
     * @param filePath ファイルパス
     * @return 既知の画像拡張子ならtrue
     */
    public static final boolean isImageFile(final String filePath) {
        if (filePath == null) {
            return false;
        }
        final String lowerCase = filePath.toLowerCase();
        for (final String identifier : IMAGE_FILE_IDENTIFIERS) {
            if (lowerCase.endsWith(identifier)) {
                return true;
            }
        }
        return false;
    }

    /**
     * remove file protocol.
     * @param uri starts with "file:///"
     * @return uri removed file protocol.
     */
    public static final String uriToPath(final String uri) {
        if (StringUtils.isEmpty(uri)) {
            return uri;
        }
        return uri.startsWith(FILE_PROTOCOL) ? uri.substring(FILE_PROTOCOL.length()) : uri;
    }

    /**
     * find file extension from passed file name.
     * @param fileName
     * @return Optional string. It contains ".txt".
     */
    public static Optional<String> findExtension(final Path path) {
        if (path == null) {
            return Optional.empty();
        }
        return findExtension(path.getFileName().toString());
    }

    /**
     * find file extension from passed file name.
     * @param fileName
     * @return Optional string.
     */
    public static Optional<String> findExtension(final String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return Optional.empty();
        }
        final int index = fileName.lastIndexOf(".");
        return (index == -1) ? Optional.empty() : Optional.of(fileName.substring(index));
    }

    /**
     * read all files content. not recursive.
     * @param pathToDir
     * @return list contains all files content.
     */
    public static List<String> readDirLines(final String pathToDir) {
        return readDirLines(Paths.get(pathToDir));
    }

    /**
     * read all files content. not recursive.
     * @param pathToDir
     * @return list contains all files content.
     */
    public static List<String> readDirLines(final Path dir) {
        if (!Files.isDirectory(dir)) {
            return Collections.emptyList();
        }
        final List<String> list = Lists.mutable.empty();
        try {
            Files.list(dir).filter(path -> {return !Files.isDirectory(path);}).forEach(path -> {
                try {
                    list.addAll(Files.readAllLines(path));
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            });
            return list;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Remove file extensions from passed file path.
     * @param filePath
     * @return file name removed file extension.
     */
    public static String removeExtension(final String filePath) {
        if (filePath == null) {
            return null;
        }
        final int index = filePath.lastIndexOf(".");
        return index == -1 ? filePath : filePath.substring(0, index);
    }

    /**
     * Capture current window.
     * @param fileName output file name.
     * @param rect rectangle size.
     */
    public static void capture(final String fileName, final Rectangle rect) {
        final String name
            = fileName.toLowerCase().endsWith(".png") ? fileName : fileName.concat(".png");
        try {
            final BufferedImage img = new Robot().createScreenCapture(rect);
            ImageIO.write(img, "png", Paths.get(name).toFile());
        } catch (final IOException | AWTException e) {
            e.printStackTrace();
        }
    }

}