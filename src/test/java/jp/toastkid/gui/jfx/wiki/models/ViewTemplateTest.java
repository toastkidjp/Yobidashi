/**
 *
 */
package jp.toastkid.gui.jfx.wiki.models;

import static org.junit.Assert.*;
import jp.toastkid.gui.jfx.wiki.models.ViewTemplate;

import org.junit.Test;

/**
 * ViewTemplate のテストケース.
 * @author Toast kid
 *
 */
public class ViewTemplateTest {

    /**
     * {@link jp.toastkid.gui.jfx.wiki.models.ViewTemplate#parse(java.lang.String)} のためのテスト・メソッド.
     */
    @Test
    public final void testParse() {
        assertEquals(ViewTemplate.MATERIAL, ViewTemplate.parse("materialize"));
        assertEquals(ViewTemplate.MATERIAL, ViewTemplate.parse("MATERIALIZE"));
        assertEquals(ViewTemplate.MATERIAL, ViewTemplate.parse("Materialize"));
        assertEquals(ViewTemplate.CLASSIC, ViewTemplate.parse("classic"));
        assertEquals(ViewTemplate.CLASSIC, ViewTemplate.parse("CLASSIC"));
        assertEquals(ViewTemplate.CLASSIC, ViewTemplate.parse("Classic"));
        assertEquals(ViewTemplate.CLASSIC, ViewTemplate.parse("tekito"));
        assertEquals(ViewTemplate.CLASSIC, ViewTemplate.parse(" "));
        assertEquals(ViewTemplate.CLASSIC, ViewTemplate.parse(null));
    }

}
