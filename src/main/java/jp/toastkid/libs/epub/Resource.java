/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.libs.epub;

/**
 * Holding path to Resources.
 *
 * @author Toast kid
 */
public final class Resource {

    /** mimetype. */
    public static final String MIMETYPE      = "assets/epub/mimetype";

    /** container.xml. */
    public static final String CONTAINER_XML = "assets/epub/META-INF/container.xml";

    /** content.opf. */
    public static final String CONTENT_OPF   = "assets/epub/OEBPS/content.opf";

    /** toc.ncx. */
    public static final String TOC_NCX       = "assets/epub/OEBPS/toc.ncx";

    /** navdoc.html. */
    public static final String NAVDOC        = "assets/epub/OEBPS/navdoc.html";

    /** stylesheet.css. */
    public static final String STYLESHEET    = "assets/epub/OEBPS/stylesheet.css";

    /** CSS of vertical writing style. */
    public static final String STYLESHEET_VERTICAL = "assets/epub/OEBPS/stylesheet_vertical.css";

    /** title_page.xhtml. */
    public static final String TITLE_PAGE    = "assets/epub/OEBPS/title_page.xhtml";

    /** title_page.xhtml. */
    public static final String TEMPLATE      = "assets/epub/OEBPS/template.xhtml";

    /**
     * Deny make instances.
     */
    private Resource() {
        // NOP.
    }
}
