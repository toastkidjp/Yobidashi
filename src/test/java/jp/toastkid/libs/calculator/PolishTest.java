package jp.toastkid.libs.calculator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * test {@link Polish}.
 * @author Toast kid
 *
 */
public class PolishTest {

    /**
     * Test of {@link Polish#calculate}.
     */
    @Test
    public final void testCalculate() {
        assertEquals(3, Polish.calculate("+ 1 2"));
        assertEquals(30, Polish.calculate("* + 1 5 + 2 3"));
    }

    /**
     * Test of {@link Polish#calculate}.
     */
    @Test
    public final void testCalculateArray() {
        assertEquals(3, Polish.calculate("+", "1", "2"));
        assertEquals(30, Polish.calculate("*", "+", "1", "5", "+", "2", "3"));
    }

    /**
     * Test of {@link Polish#calculate}.
     */
    @Test
    public final void testDivide() {
        assertEquals(6, Polish.calculate("/", "2", "12"));
    }

    /**
     * Test of {@link Polish#calculate}.
     */
    @Test
    public final void testMinus() {
        assertEquals(-2, Polish.calculate("-", "4", "2"));
    }

}
