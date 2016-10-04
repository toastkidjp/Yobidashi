package jp.toastkid.libs.wiki;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import jp.toastkid.wiki.lib.Footballer;

/**
 * Footballer's test case.
 * @author Toast kid
 *
 */
public class FootballerTest {
    /**
     * インスタンス生成が上手くいくことを確認.
     */
    @Test
    public final void testGetFootballer() {
        final String str = "1 | GK | スポルティエッロ";
        final Footballer f = Footballer.getFootballer(str);
        assertEquals("スポルティエッロ", f.familyName);
        assertEquals(1, f.number);
        assertEquals("GK", f.position.toString());
        assertEquals(Footballer.Position.GK, f.position);
    }

}
