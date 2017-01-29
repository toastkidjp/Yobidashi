package jp.toastkid.yobidashi.models;

import java.nio.file.Path;
import java.nio.file.Paths;

import jp.toastkid.libs.utils.FileUtil;


/**
 * This constants hold defines of Yobidashi.
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
    public static final String SCENE_DIR          = "scenes";

    /** dir of user files. */
    public static final String USER_DIR           = "user";

    /** Slide's css dir. */
    public static final String SLIDE_CSS_DIR      = USER_DIR + "/css/slide/";

    /** 設定ファイルフォルダのパス. */
    public static final Path   CONFIG           = Paths.get(USER_DIR + "/conf/config.properties");

    /** 設定ファイルの文字コード. */
    public static final String CONF_ENCODE        = "UTF-8";

    /** タイトルの文字コード. */
    public static final String TITLE_ENCODE       = "EUC-JP";

    /** 記事ファイルの文字コード. */
    public static final String ARTICLE_ENCODE     = "UTF-8";

    /** ePub生成レシピの置き場所. */
    public static final String EPUB_RECIPE_DIR    = USER_DIR + "/res/epub/";

    /** Groovy Script Dialog title. */
    public static final String SCRIPT_RUNNER      = "Script Runner";

    /** path/to/template. */
    public static final String PATH_TO_TEMPLATE   = Defines.TEMPLATE_DIR + "/main.html";

	/** Bookmark file path. */
	public static final Path PATH_TO_BOOKMARK = Paths.get("user/bookmark.txt");

    /**
     * Deny make this instance.
     */
    private Defines() {
        //do nothing.
    }

    /**
     * Find path to install folder.
     *
     * @return Path of install folder
     */
    public static String findInstallDir() {
        return FileUtil.FILE_PROTOCOL
                + Paths.get(".").toAbsolutePath().getParent().toString().replace("\\", "/") + "/";
    }
}
