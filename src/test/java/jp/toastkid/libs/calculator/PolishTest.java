package jp.toastkid.libs.calculator;

import static org.junit.Assert.*;
import jp.toastkid.libs.calculator.Polish;

import org.junit.Test;

/**
 * test {@link Polish}.
 * @author Toast kid
 *
 */
public class PolishTest {

    /**
     * test calculate.
     */
    @Test
    public final void testCalculate() {
        assertEquals(3, Polish.calculate("+ 1 2"));
        assertEquals(30, Polish.calculate("* + 1 5 + 2 3"));
    }

    /**
     * test calculate.
     */
    @Test
    public final void testCalculateArray() {
        assertEquals(3, Polish.calculate("+", "1", "2"));
        assertEquals(30, Polish.calculate("*", "+", "1", "5", "+", "2", "3"));
    }

}
