package jp.toastkid.libs.epub;

/**
 * リソースへのパスを格納.
 * @author Toast kid
 *
 */
public final class Resource {
    /** mimet ypeのパス. */
    public static final String MIMETYPE      = "public/resources/epub/mimetype";
    /** container.xmlのパス. */
    public static final String CONTAINER_XML = "public/resources/epub/META-INF/container.xml";
    /** content.opfのパス. */
    public static final String CONTENT_OPF   = "public/resources/epub/OEBPS/content.opf";
    /** toc.ncxのパス. */
    public static final String TOC_NCX       = "public/resources/epub/OEBPS/toc.ncx";
    /** navdoc.htmlのパス. */
    public static final String NAVDOC        = "public/resources/epub/OEBPS/navdoc.html";
    /** stylesheet.cssのパス. */
    public static final String STYLESHEET    = "public/resources/epub/OEBPS/stylesheet.css";
    /** 縦書きスタイルシートのパス. */
    public static final String STYLESHEET_VERTICAL
    	= "public/resources/epub/OEBPS/stylesheet_vertical.css";
    /** title_page.xhtmlのパス. */
    public static final String TITLE_PAGE    = "public/resources/epub/OEBPS/title_page.xhtml";
    /**
     * インスタンス生成を禁止する.
     */
    private Resource() {
        // NOP.
    }
}
