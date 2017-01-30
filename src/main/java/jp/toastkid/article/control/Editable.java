package jp.toastkid.article.control;

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
