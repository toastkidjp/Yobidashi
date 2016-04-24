package jp.toastkid.libs.epub;

/**
 * Constant values for generating ePub.
 * @author Toast kid
 *
 */
public final class EpubDefine {
    /**
     * deny make instance,
     */
    private EpubDefine() {
        // deny make instance.
    }
    /** epubに同梱するファイルの拡張子. */
    public static final String FILE_SUFFIX = ".html";
    /** 縦書きスタイルシート. */
    public static final String STYLESHEET_VERTICAL = "stylesheet_vertical.css";
    /** 横書きスタイルシート. */
    public static final String STYLESHEET_HORIZONTAL = "stylesheet.css";

}
