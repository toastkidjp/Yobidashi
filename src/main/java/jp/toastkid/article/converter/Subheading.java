package jp.toastkid.article.converter;

import java.io.Serializable;

/**
 * Html subheading.
 * @author Toast kid
 *
 */
public class Subheading implements Serializable {
    /** serialVersionUID. */
    private static final long serialVersionUID = 6144685566158773878L;

    /** title. */
    public final String title;
    /** subheading id. */
    public final String id;
    /** html depth. */
    public final int    depth;
    
    /**
     * initialize object.
     * @param str
     * @param id
     * @param i
     */
    public Subheading(final String str, final String id, final int depth) {
        this.title = str;
        this.id    = id;
        this.depth = depth;
    }
}
