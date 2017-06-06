/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
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
    public static final String FILE_SUFFIX           = ".html";
    /** 縦書きスタイルシート. */
    public static final String STYLESHEET_VERTICAL   = "stylesheet_vertical.css";
    /** 横書きスタイルシート. */
    public static final String STYLESHEET_HORIZONTAL = "stylesheet.css";

}
