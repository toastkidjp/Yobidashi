package jp.toastkid.wiki.models;

import java.io.File;

import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.factory.Sets;

import jp.toastkid.libs.utils.FileUtil;


/**
 * WikiClient の各種定義を保存する.
 * <ol>
 * <li>Config……可変値
 * <li>Define……固定値
 * </ol>
 * <HR>
 * (130803) expand 対応<BR>
 * (130616) ユニバーサルフッタ追加<BR>
 * (130203) 作成
 * @author Toast kid
 *
 */
public final class Defines {

    /** path/to/log. */
    public static final String LOG_DIR            = "logs";

    /** path/to/dir/view/template. */
    public static final String TEMPLATE_DIR       = "templates";

    /** dir of assets. */
    public static final String ASSETS_DIR         = "assets";

    /** dir of scene files. */
    public static final String SCENE_DIR          = ASSETS_DIR + "/scenes";

    /** dir of user files. */
    public static final String USER_DIR           = "user";

    /** 設定ファイルフォルダのパス. */
    public static final String CONF_DIR           = USER_DIR + "/conf/";

    /** 設定ファイル名. */
    public static final String CONF_NAME          = "config.properties";

    /** WikiClient の設定ファイルのパス. */
    public static final String CONF_ENCODE        = "UTF-8";

    /** タイトルの文字コード. */
    public static final String TITLE_ENCODE       = "EUC-JP";

    /** 記事ファイルの文字コード. */
    public static final String ARTICLE_ENCODE     = "UTF-8";

    /** 一時ファイルの名前. */
    public static final String TEMP_FILE_NAME     = "temp.html";

    /** Windows デフォルトのエディタ. */
    public static final String NOTEPAD_EXE        = "notepad.exe";

    /** Terapad のオプション. */
    public static final String TERAPAD_OPTIONS    = " /cu8n";

    /** デフォルトの URL. */
    public static final String DEFAULT_HOME       = "http://www.yahoo.co.jp";

    /** 個人的な使用か否か. */
    public static final boolean isMyUse           = true;

    /** ePub生成レシピの置き場所. */
    public static final String EPUB_RECIPE_DIR    = USER_DIR + "/res/epub/";

    /** このプログラムで扱う音楽ファイルの拡張子. */
    public static final ImmutableSet<String> MUSIC_FILES
        = Sets.immutable.of(".mid", ".mp3", ".wav");

    /** Groovy Script Dialog title. */
    public static final String SCRIPT_RUNNER      = "Script Runner";

    /** line separator. */
    public static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * インスタンス生成を禁止する.
     */
    private Defines() {
        //do nothing.
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
}
