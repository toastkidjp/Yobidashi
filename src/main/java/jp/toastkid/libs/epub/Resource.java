package jp.toastkid.libs.epub;

import jp.toastkid.wiki.models.Defines;

/**
 * リソースへのパスを格納.
 * @author Toast kid
 *
 */
public final class Resource {
    /** mimet ypeのパス. */
    public static final String MIMETYPE      = Defines.ASSETS_DIR + "/resources/epub/mimetype";
    /** container.xmlのパス. */
    public static final String CONTAINER_XML = Defines.ASSETS_DIR + "/resources/epub/META-INF/container.xml";
    /** content.opfのパス. */
    public static final String CONTENT_OPF   = Defines.ASSETS_DIR + "/resources/epub/OEBPS/content.opf";
    /** toc.ncxのパス. */
    public static final String TOC_NCX       = Defines.ASSETS_DIR + "/resources/epub/OEBPS/toc.ncx";
    /** navdoc.htmlのパス. */
    public static final String NAVDOC        = Defines.ASSETS_DIR + "/resources/epub/OEBPS/navdoc.html";
    /** stylesheet.cssのパス. */
    public static final String STYLESHEET    = Defines.ASSETS_DIR + "/resources/epub/OEBPS/stylesheet.css";
    /** 縦書きスタイルシートのパス. */
    public static final String STYLESHEET_VERTICAL
    	= Defines.ASSETS_DIR + "/resources/epub/OEBPS/stylesheet_vertical.css";
    /** title_page.xhtmlのパス. */
    public static final String TITLE_PAGE    = Defines.ASSETS_DIR + "/resources/epub/OEBPS/title_page.xhtml";
    /**
     * インスタンス生成を禁止する.
     */
    private Resource() {
        // NOP.
    }
}
