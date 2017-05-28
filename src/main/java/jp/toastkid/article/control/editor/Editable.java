/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.article.control.editor;

import javafx.scene.text.Font;

/**
 * Editor interface.
 *
 * @author Toast kid
 *
 */
public interface Editable {

    /**
     * Set font size.
     * @param size
     */
    void setFont(final Font font);

    /**
     * Edit.
     * @return
     */
    String edit();

    /**
     * Is editing now.
     * @return
     */
    boolean isEditing();

    /**
     * Save content to file.
     * @return
     */
    String saveContent();
}
