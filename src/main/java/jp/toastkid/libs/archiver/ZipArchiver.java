package jp.toastkid.libs.archiver;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jp.toastkid.libs.utils.Strings;

/**
 * zipファイルを作成するサンプル
 * <HR>
 * <pre>
 * ■使用例
 * // ディレクトリ圧縮
 * ZipCompression comp = new ZipCompression();
 * comp.doDirectory("c:/hoge/hogedir");
 *
 * 【注意】
 * 本クラスはJDK1.4(java.util.zip)で実現しているので、
 * マルチバイトのファイル名に対応していません.
 * java.util.zipではファイル名をUTF8固定でエンコードしています.
 *
 * これを回避する方法はorg.apache.tools.zip(ant.jar梱包)を使用する方法があるそうです.
 *
 * exp)org.apache.tools.zip.ZipOutputStream#setEncodig("MS932")
 *
 * http://www.atmarkit.co.jp/bbs/phpBB/viewtopic.php?topic=19724&forum=12&5
 *
 * </pre>
 *
 * <HR>
 * (121020) 下記参考サイトからコピー<BR>
 * @see <a href="http://d.hatena.ne.jp/koichiarchi/20060828/1156758449">[java]ファイル圧縮＆ディレクトリ圧縮</a>
 * @author koichiarchi
 * @author Toast kid
 *
 */
public final class ZipArchiver {
    /** 圧縮ファイル名(コンストラクタ指定時のみ) */
    private String zipFilePath;
    /** 圧縮ディレクトリパス(ディレクトリ処理時のみ使用) */
    private String targetDirPath;
    /** ZIP形式拡張子 */
    public static final String EXTENSION_ZIP = ".zip";
    /** folder separator. */
    private static final String DIR_SEPARATOR = Strings.getDirSeparator();
    /**
     * 圧縮時のファイル名はデフォルト設定になります.
     *
     * <pre>
     * ディレクトリ指定時
     *     c:/hoge → c:/hoge.zip
     *
     * ファイル指定時
     *     c:/hoge/hoge.txt → c:/hoge/hoge.zip
     * </pre>
     */
    public ZipArchiver() {
    }

    /**
     * 圧縮ファイル名を指定します.
     * 親ディレクトリが存在しない場合はエラーになります.
     * @param zipFilePath 圧縮ファイル名(フルパス指定)
     * @throws FileNotFoundException 親ディレクトリが存在しない場合
     */
    public ZipArchiver(final String zipFilePath) throws FileNotFoundException {
        if (!Files.isDirectory(Paths.get(zipFilePath).getParent())) {
            throw new FileNotFoundException("親ディレクトリが存在しません." + zipFilePath);
        }
        this.zipFilePath = zipFilePath;
    }

    /**
     * 指定ファイルをZIP形式に圧縮します.
     * @param filePath 圧縮対象ファイル(フルパス指定)
     * @return 圧縮ファイル名(フルパス)
     * @throws IOException 入出力関連エラーが発生した場合
     */
    public String doFile(final String filePath) throws IOException {
        //System.out.println("圧縮開始");
        //System.out.println("圧縮ファイル=" + filePath);
        // ファイル存在チェック
        final Path targetPath = Paths.get(filePath);
        if (!Files.exists(targetPath)) {
            throw new FileNotFoundException("指定のファイルが存在しません." + filePath);
        }
        // 圧縮先ファイルへのストリームを開く
        if (zipFilePath == null) {
            zipFilePath = getCompressFileName(filePath);
        }
        //System.out.println("圧縮ファイル名=" + zipFilePath);
        final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFilePath));
        // ファイル圧縮処理
        putEntryFile(out, targetPath);
        // 出力ストリームを閉じる
        out.flush();
        out.close();
        //System.out.println("圧縮終了");
        return zipFilePath;
    }

    /**
     * 指定ディレクトリをZIP形式に圧縮します.
     * @param directoryPath 圧縮対象ディレクトリ(フルパス指定)
     * @return 圧縮ファイル名(フルパス)
     * @throws IOException 入出力関連エラーが発生した場合
     */
    public String doDirectory (final String directoryPath) throws IOException {
        //System.out.println("圧縮開始");
        //System.out.println("圧縮ディレクトリ=" + directoryPath);
        targetDirPath = directoryPath;
        // ディレクトリ存在チェック
        final Path targetDirectory = Paths.get(directoryPath);
        if (!Files.isDirectory(targetDirectory)) {
            throw new FileNotFoundException("指定のディレクトリが存在しません." + directoryPath);
        }

        // 指定ディレクトリ直下のファイル一覧を取得
        final List<Path> rootFiles = Files.list(targetDirectory).collect(Collectors.toList());
        // 圧縮先ファイルへのストリームを開く
        if (zipFilePath == null) {
            zipFilePath = getCompressFileName(directoryPath);
        }
        //System.out.println("圧縮ファイル名=" + zipFilePath);
        final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFilePath));
        // ディレクトリ自体を書き込む
        putEntryDirectory(out, targetDirectory);
        // ファイルリスト分の圧縮処理
        compress(out, rootFiles);

        // 出力ストリームを閉じる
        out.flush();
        out.close();

        //System.out.println("圧縮終了");
        return zipFilePath;

    }

    /**
     * <pre>
     * ファイル一覧(ディレクトリ含む)をZipOutputStreamに登録します.
     * ディレクトリが存在する場合は再帰的に本メソッドをコールし全てのファイルを登録します.
     * </pre>
     * @param out
     * @param fileList
     * @throws IOException
     */
    private void compress(
            final ZipOutputStream out,
            final List<Path> fileList
            ) throws IOException {
        // ファイルリスト分の圧縮処理
        //TODO
        for (final Path path : fileList) {
            if (Files.exists(path)) {
                // file compress->
                System.out.println( zipFilePath + "<-" + path.toString());
                // ファイル書き込み
                putEntryFile(out, path);
                continue;
            }

            // ディレクトリ自体を書き込む
            putEntryDirectory(out, path);
            // ディレクトリ内のファイルについては再帰的に本メソッドを処理する
            final List<Path> inFiles = Files.list(path).collect(Collectors.toList());
            compress(out, inFiles);
        }
    }
    /**
     * <pre>
     * ZipOutputStreamに対してファイルを登録します.
     * </pre>
     * @param out
     * @param file
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void putEntryFile(
            final ZipOutputStream out,
            final Path file
            ) throws FileNotFoundException, IOException {
        final byte[] buf = new byte[128];
        // 圧縮元ファイルへのストリームを開く
        final BufferedInputStream in = new BufferedInputStream(new FileInputStream(file.toFile()));
        // エントリを作成する
        final ZipEntry entry = new ZipEntry(getZipEntryName(file.toString()));
        out.putNextEntry(entry);
        // データを圧縮して書き込む
        int size;
        while ((size = in.read(buf, 0, buf.length)) != -1) {
            out.write(buf, 0, size);
        }
        // エントリと入力ストリームを閉じる
        out.closeEntry();
        in.close();
    }
    /**
     * <pre>
     * ZipOutputStreamに対してディレクトリを登録します.
     * </pre>
     * @param out
     * @param path
     * @throws IOException
     */
    private void putEntryDirectory(
            final ZipOutputStream out,
            final Path path
            ) throws IOException {
        final ZipEntry entry = new ZipEntry(getZipEntryName(path.toString()) + "/");
        entry.setSize(0);
        out.putNextEntry(entry);
    }
    /**
     * <pre>
     * ZipEntryを生成する為のパスを取得します.
     * 主に階層構造(ディレクトリ)に対応する為に作成しました.
     *
     * 圧縮指定ディレクトリが"C:/hoge"と指定された場合に以下のように取得します.
     *     C:/hoge/hogehoge.txt    → hogehoge.txt
     *     C:/hoge/hoge2/hoge3.txt → hoge2/hoge3.txt
     * </pre>
     * @param filePath ファイルパス
     * @return ファイル名
     */
    private String getZipEntryName (final String filePath) {
        if (targetDirPath == null) {
            // ファイル圧縮時
            return (Paths.get(filePath)).getFileName().toString();
        }
        String parantPath = (Paths.get(targetDirPath)).getParent().toString();
        parantPath = removeLastSeparator(parantPath);
        return filePath.substring(parantPath.length() + 1);
    }

    /**
     * <pre>
     * 圧縮ファイル名を求めます.
     * fileNameが拡張子なし(ディレクトリ扱い)の場合、".zip"が付加されます.
     * fileNameが拡張子あり(ファイル扱い)の場合、既存の拡張子が取り除かれ".zip"が付加されます.
     *
     *     hogeDirectory → hogeDirectory.zip
     *     hogeFile.txt  → hogeFile.zip
     *
     * fileNameにはフルパスを指定することも可能です.
     *
     *     c:/hogeDirectory → c:/hogeDirectory.zip
     *     c:/hogeFile.txt  → c:/hogeFile.zip
     * </pre>
     * @param directoryPath 圧縮ディレクトリ名
     * @return 圧縮ファイル名
     */
    private String getCompressFileName (final String directoryPath) {
        final int tmp1 = directoryPath.lastIndexOf("\\");
        final int tmp2 = directoryPath.lastIndexOf("/");
        final int sepIndex = tmp1 > tmp2 ? tmp1 : tmp2;
        final int extIndex = directoryPath.lastIndexOf(".");
        String zipName;
        if (sepIndex >= extIndex) {
            // (121020) フォルダパスの末尾にセパレータがある場合は除去
            zipName = removeLastSeparator(directoryPath) + EXTENSION_ZIP;
        } else {
            zipName = directoryPath.substring(0, extIndex) + EXTENSION_ZIP;
        }
        return zipName;

    }
    /**
     * パスの最後にセパレータがある場合のみそれを除去して返却します.
     * それ以外はそのまま返却します.
     * @param path c:/hoge/
     * @return c:/hoge
     */
    private String removeLastSeparator (final String path) {
        if (!path.endsWith(DIR_SEPARATOR) && !path.endsWith("/")) {
            return path;
        }
        return path.substring(0, path.length() - 1);
    }
}
