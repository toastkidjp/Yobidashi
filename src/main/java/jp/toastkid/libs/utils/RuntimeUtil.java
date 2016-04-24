package jp.toastkid.libs.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 他プログラム起動関連のメソッドを収録.
 * <HR>
 * (120120) callExplorer() 作成<BR>
 * (120119) callCalculator() 作成<BR>
 * (120109) 作成<BR>
 * @author 10fmi13
 * @see <a href="http://www.atmarkit.co.jp/fjava/javatips/172java057.html">    プログラムから別のアプリケーションを起動するには </a>
 * @see <a href="http://allabout.co.jp/gm/gc/80624/">外部のプログラムを実行するには？</a>
 */
public final class RuntimeUtil {

    private RuntimeUtil() {
        // NOP.
    }

    /**
     * 他のプログラムを Runtime クラスの exec() メソッドを使って起動させる.
     * 誤作動防止のため、フルパスで指定すること
     * <HR>
     * launch("notepad.exe");
     * <HR>
     * (120109) 作成
     * @param pPath 起動したいプログラムのパス
     * @return Process
     */
    public static Process launch(final String pPath) {
        return launchProcess(pPath, false);
    }
    /**
     * 他の Java プログラムを Runtime クラスの exec() メソッドを使って起動させる.
     * 誤作動防止のため、フルパスで指定すること
     * <HR>
     * launch("\"C:\\allabout\\FreeLife.jar\"");
     * <HR>
     * (120109) 作成
     * @param pJavaAppPath 起動したい Java プログラムのパス
     * @return Process
     */
    public static Process launchJavaApp(final String pJavaAppPath) {
        return launchProcess(pJavaAppPath, true);
    }
    /**
     * 他のプログラムを Runtime クラスの exec() メソッドを使って起動させる.
     * 誤作動防止のため、フルパスで指定すること
     * <HR>
     * launch("notepad.exe")
     * <HR>
     * (120109) 作成
     * @param pPath 起動したいプログラムのパス
     * @param isJava java プログラムを起動するなら true
     * @return Process
     */
    private static Process launchProcess(
            final String pPath,
            final boolean isJava
            ) {
        Process retPro = null;
        try {
            final Runtime rt = Runtime.getRuntime();
            if (isJava) {
                retPro = rt.exec("java -jar " + pPath);
            }else{
                retPro = rt.exec(pPath);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        return retPro;
    }
    /**
     * Process クラスのオブジェクトから内容を read して文字列にし、返す.
     * <HR>
     * (120109) 作成
     * @param proc Process クラスのオブジェクト
     * @return 文字列
     */
    public static String getProcessStr(final Process proc) {
        final StringBuffer result = new StringBuffer(3000);
        final String lineSepar = Strings.LINE_SEPARATOR;
        try (final InputStreamReader isr = new InputStreamReader(proc.getInputStream());
             final BufferedReader    br  = new BufferedReader(isr);) {
            String str = br.readLine();
            while ( str != null ) {
                result.append(str);
                result.append(lineSepar);
                str = br.readLine();
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        return result.toString();
    }
    /**
     * Windows 環境の場合、電卓を呼び出す.
     * <HR>
     * (120119) 作成<BR>
     * @see <a href="http://www.atmarkit.co.jp/fjava/javatips/172java057.html">
     * プログラムから別のアプリケーションを起動するには </a>
     * @return 実行のプロセス Process
     */
    public static Process callCalculator() {
        final String name = Strings.getOSName().toLowerCase();
        //System.out.println(name);
        if (name.startsWith("windows")) {
            return launch("calc");
        }
        return null;
    }
    /**
     * Windows 環境の場合、コマンドプロンプトを呼び出す.
     * @see <a href="http://www.atmarkit.co.jp/fjava/javatips/172java057.html">
     * プログラムから別のアプリケーションを起動するには </a>
     * @return 実行のプロセス Process
     */
    public static Process callCmd() {
        final String name = Strings.getOSName().toLowerCase();
        if (name.startsWith("windows")) {
            try {
                return Runtime.getRuntime().exec("cmd.exe /c start");
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
    /**
     * Windows 環境の場合、引数で指定したフォルダをエクスプローラで呼び出す.
     * <HR>
     * (120120) 作成<BR>
     * @param dirPath 呼び出すフォルダのパス
     * @see <a href="http://d.hatena.ne.jp/language_and_engineering/20081028/1225160338">
     * コマンドラインからプロセスを起動・終了する方法（環境変数とレジストリについて）</a>
     * @return 実行のプロセス Process
     */
    public static Process callExplorer(final String dirPath) {
        final String name = Strings.getOSName().toLowerCase();
        if (name.startsWith("windows")) {
            return launch("explorer.exe " + Strings.doubleQuote(dirPath));
        }
        return null;
    }

    /**
     * calculate used memory size.
     * @return used memory size (long)
     */
    public static final long calcUsedMemorySize() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

}
