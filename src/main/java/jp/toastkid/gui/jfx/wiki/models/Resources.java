package jp.toastkid.gui.jfx.wiki.models;

/**
 * リソースへのパス.
 * @author Toast kid
 *
 */
public final class Resources {

    /**
     * インスタンス生成を禁止する.
     */
    private Resources() {
        // You can't create this class instance.
    }

    /** 編集履歴の参照・出力先 */
    public static final String HISTORY_PATH           = "public/resources/history.txt";
    /** アイコン画像ファイルへのパス */
    public static final String PATH_IMG_ICON          = "public/images/Icon.png";
    /** 「停止」ボタンの画像ファイルへのパス */
    public static final String PATH_IMG_STOP          = "public/images/stop.png";
    /** 「リロード」ボタンの画像ファイルへのパス */
    public static final String PATH_IMG_RELOAD        = "public/images/reload.png";
    /** 「進む」ボタンの画像ファイルへのパス */
    public static final String PATH_IMG_FORWARD       = "public/images/forward.png";
    /** 「戻る」ボタンの画像ファイルへのパス */
    public static final String PATH_IMG_BACK          = "public/images/back.png";
    /** 「検索」画像ファイルへのパス. */
    public static final String PATH_IMG_SEARCH        = "public/images/search.png";

    /** Readme ファイルのファイル名 (130512) */
    public static final String READ_ME_NAME           = "README.md";

    /** RSS取得対象のURLリスト. */
    public static final String PATH_RSS_TARGETS       = "user/res/rss";

    /** path/to/dir/view/template. */
    private static final String VIEW_DIR = "src/main/views/";

    /** Gallery 全体のView. */
    public static final String PATH_GALLERY           = VIEW_DIR + "gallery.html";
    /** Gallery 1section分のTemplate. */
    public static final String PATH_GALLERY_ITEM      = VIEW_DIR + "gallery_item.html";
    /** Slide template. */
    public static final String PATH_SLIDE             = VIEW_DIR + "slide.html";

}
