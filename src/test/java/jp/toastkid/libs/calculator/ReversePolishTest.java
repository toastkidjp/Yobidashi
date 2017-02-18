package jp.toastkid.libs.calculator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test of {@link ReversePolish}.
 * @author Toast kid
 * @see <a href="https://ja.wikipedia.org/wiki/逆ポーランド記法">逆ポーランド記法</a>
 */
public class ReversePolishTest {

    /**
     * Test of {@link ReversePolish#calculate}.
     */
    @Test
    public final void testCalculate() {
        assertEquals(27, ReversePolish.calculate("1 2 + 4 5 + *"));
    }

    /**
     * Test of {@link ReversePolish#calculate}.
     */
    @Test
    public final void testCalculateChars() {
        assertEquals(27, ReversePolish.calculate("1", "2", "+", "4", "5", "+", "*"));
    }

    /**
     * Test of {@link ReversePolish#calculate}.
     */
    @Test
    public final void testDivide() {
        assertEquals(2, ReversePolish.calculate("4", "2", "/"));
    }

    /**
     * Test of {@link ReversePolish#calculate}.
     */
    @Test
    public final void testMinus() {
        assertEquals(2, ReversePolish.calculate("4", "2", "-"));
    }

}
