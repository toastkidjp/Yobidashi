package jp.toastkid.jfx.common;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.text.Font;

/**
 * Font factory.
 *
 * @author Toast kid
 *
 */
public class FontFactory {

    /** Default font. */
    private static final Font DEFAULT_FONT = Font.getDefault();

    /**
     * Deny make instance.
     */
    private FontFactory() {
        // NOP.
    }

    /**
     * Return font.
     * @return Font
     */
    public static final Font make(final String family, final int size) {
        if (StringUtils.isBlank(family) || size < 0) {
            return DEFAULT_FONT;
        }
        return Font.font(family, size);
    }

}
