package jp.toastkid.libs.epub;

/**
 * コンテンツ単位のメタデータ
 * @author Toast kid
 *
 */
public final class ContentMetaData {
    /** epubにエントリする時のタイトル. */
    public String title;
    /** 元ファイルのパス. */
    public String source;
    /** epubにエントリする時の親パス. */
    public String dest;
    /** epubにエントリする時のパス. */
    public String entry;
}
