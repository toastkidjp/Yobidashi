/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
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
