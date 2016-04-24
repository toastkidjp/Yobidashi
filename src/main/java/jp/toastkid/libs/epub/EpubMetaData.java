package jp.toastkid.libs.epub;

import java.util.List;

import jp.toastkid.gui.jfx.wiki.models.Defines;
import jp.toastkid.libs.utils.FileUtil;
import net.arnx.jsonic.JSON;

/**
 * Epub のメタデータ.
 * @author Toast kid
 *
 */
public final class EpubMetaData {
    /** epubファイルの拡張子. */
    public static final String EPUB_SUFFIX = ".epub";
    /** ルールセットファイルの拡張子. */
    public static final String RULESET_SUFFIX = ".rs";
    /** タイトル. */
    public String title;
    /** サブタイトル. */
    public String subtitle;
    /** 著者. */
    public String author;
    /** 編者. */
    public String editor;
    /** 発行者. */
    public String publisher;
    /** バージョン. */
    public String version   = "0.0.1";
    /** 出力先. */
    public String zipFilePath = "epub.epub";
    /** 記事名のプレフィクス、前方一致でマッチした記事をePubのコンテンツに含める. */
    public String targetPrefix;
    public List<String> targets;
    public boolean containInnerLinks;
    public String ruleSetFileName;
    /** 再帰的に文書を入れるか否か. */
    public boolean recursive = false;
    /** ページめくり方向. */
    public PageProgressDirection ppd = PageProgressDirection.RTL;
    /** ページ方向. */
    public PageLayout layout = PageLayout.VERTICAL;
    @Override
    public final String toString() {
        return JSON.encode(this);
    }
    /**
     * ルールセットをファイルに出力する.
     */
    public final void store() {
        final String outputName = ruleSetFileName;
        this.ruleSetFileName = null;
        FileUtil.outPutStr(
            this.toString(),
            outputName,
            Defines.ARTICLE_ENCODE
        );
    }
}
