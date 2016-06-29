/**
 *
 */
package jp.toastkid.gui.jfx.wiki.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * ViewTemplate のテストケース.
 * @author Toast kid
 *
 */
public class ViewTemplateTest {

    /**
     * {@link jp.toastkid.gui.jfx.wiki.models.ViewTemplate#parse(java.lang.String)}
     * のためのテスト・メソッド.
     */
    @Test
    public final void testParse() {
        assertEquals(ViewTemplate.MATERIAL, ViewTemplate.parse("materialize"));
        assertEquals(ViewTemplate.MATERIAL, ViewTemplate.parse("MATERIALIZE"));
        assertEquals(ViewTemplate.MATERIAL, ViewTemplate.parse("Materialize"));;
        assertEquals(ViewTemplate.MATERIAL, ViewTemplate.parse("tekito"));
        assertEquals(ViewTemplate.MATERIAL, ViewTemplate.parse(" "));
        assertEquals(ViewTemplate.MATERIAL, ViewTemplate.parse(null));
    }

}
