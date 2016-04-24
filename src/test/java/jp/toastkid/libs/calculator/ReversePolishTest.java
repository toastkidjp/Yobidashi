package jp.toastkid.libs.calculator;

import static org.junit.Assert.*;
import jp.toastkid.libs.calculator.ReversePolish;

import org.junit.Test;

/**
 * test {@link ReversePolish}.
 * @author Toast kid
 * @see <a href="https://ja.wikipedia.org/wiki/逆ポーランド記法">逆ポーランド記法</a>
 */
public class ReversePolishTest {

    /**
     * test calculate.
     */
    @Test
    public final void testCalculate() {
        assertEquals(27, ReversePolish.calculate("1 2 + 4 5 + *"));
    }

    /**
     * test calculate.
     */
    @Test
    public final void testCalculateChars() {
        assertEquals(27, ReversePolish.calculate("1", "2", "+", "4", "5", "+", "*"));
    }

}
