package jp.toastkid.libs.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * {@link MathUtil}'s test case.
 * @author Toast kid
 *
 */
public final class MathUtilTest {

    /**
     * MathUtil#min()'s test case.
     */
    @Test
    public final void testMinPrimitive() {
        final int[] array = new int[]{19, 2, 4, 1, 33, 22, 2};
        assertEquals(1, MathUtil.minPrim(array));
    }

    /**
     * MathUtil#max()'s test case.
     */
    @Test
    public final void testMaxPrimitive() {
        final int[] array = new int[]{19, 2, 4, 1, 33, 22, 2};
        assertEquals(33, MathUtil.maxPrim(array));
    }

    /**
     * MathUtil#min()'s test case.
     */
    @Test
    public final void testMin() {
        final Integer[] array = new Integer[]{19, 2, 4, 1, 33, 22, 2};
        assertEquals(1, MathUtil.min(array).intValue());
    }

    /**
     * MathUtil#max()'s test case.
     */
    @Test
    public final void testMax() {
        final Integer[] array = new Integer[]{19, 2, 4, 1, 33, 22, 2};
        assertEquals(33, MathUtil.max(array).intValue());
    }

    /**
     * MathUtil#getInt()'s test case..
     */
    @Test
    public final void getIntTest() {
        assertEquals(0, MathUtil.getInt("0"));
        assertEquals(1, MathUtil.getInt("+1"));
        assertEquals(-1, MathUtil.getInt("-1"));
        assertEquals(0, MathUtil.getInt(null));
        assertEquals(0, MathUtil.getInt(" "));
        assertEquals(0, MathUtil.getInt("tomato"));
    }

    /**
     * getDailyRandomIntSet の基本動作を確認する.
     */
    @Test
    public final void getDailyRandomIntSetTest() {
        final int size = 10;
        final int max = 15;
        final Set<Integer> set = MathUtil.getDailyRandomIntSet(size, max);
        assertEquals(size, set.size());
        assertTrue(Collections.max(set) <= new Integer(max));
    }

    /**
     * sizeが0以下の時は IllegalArgumentException を返すことのテスト.
     */
    @Test
    public final void getDailyRandomIntSetUnderZeroTest() {
        final int max = 1;
        for (int i = -2; i < 1; i++) {
            try {
                MathUtil.getDailyRandomIntSet(i, max);
                fail();
            } catch (final IllegalArgumentException e) {
                assertTrue(true);
            }
        }
    }

    /**
     * size より max が小さい時は0から連番を返すことを確認.
     */
    @Test
    public final void getDailyRandomIntSetOverSizeTest() {
        final int size = 4;
        final int max = 3;
        final Set<Integer> set = MathUtil.getDailyRandomIntSet(size, max);
        assertEquals(max, set.size());
        assertEquals(new Integer(max), Collections.max(set));
    }

    /**
     * size と max が同じ数値の時でも正常に Set を返すことを確認.
     */
    @Test
    public final void getDailyRandomIntSetEqualSizeTest() {
        final int size = 4;
        final int max = 4;
        final Set<Integer> set = MathUtil.getDailyRandomIntSet(size, max);
        assertEquals(max, set.size());
        assertEquals(new Integer(max - 1), Collections.max(set));
    }

    /**
     * 0 を除外したときに最大値が4になることを確認.
     */
    @Test
    public final void getDailyRandomIntSetExcludeZeroTest() {
        final int size = 4;
        final int max = 4;
        final Set<Integer> excludeZeroSet
            = MathUtil.getDailyRandomIntSet(size, max, "tomato", false);
        assertEquals(max, excludeZeroSet.size());
        assertEquals(new Integer(max), Collections.max(excludeZeroSet));
    }

    /**
     * Test {@link MathUtil#leastSquare(double[][])}.
     */
    @Test
    public final void testLeastSquare() {
        final double[][] array = {
                {1,   5},
                {3,   9},
                {6,  15},
                {8,  19},
                {12, 24}
            };
        final double[] leastSquare = MathUtil.leastSquare(array);
        assertEquals(1.76, leastSquare[0], 0.1);
        assertEquals(3.86, leastSquare[1], 0.1);
    }

    /**
     * Test {@link MathUtil#leastSquare(double[][])}.
     */
    @Test
    public final void testLeastSquareUseMap() {
        final Map<Number, Number> array = new HashMap<Number, Number>() {
            /** serialVersionUID. */
            private static final long serialVersionUID = 1L;
        {
            put(1,   5);
            put(3,   9);
            put(6,  15);
            put(8,  19);
            put(12, 24);
        }};
        final double[] leastSquare = MathUtil.leastSquare(array);
        assertEquals(1.76, leastSquare[0], 0.1);
        assertEquals(3.86, leastSquare[1], 0.1);
    }

    /**
     * Test {@link MathUtil#parseOrZero(String)}.
     */
    @Test
    public final void testParseOrZero() {
        assertEquals(1, MathUtil.parseOrZero("1"));
        assertEquals(-1, MathUtil.parseOrZero("-1"));
        assertEquals(0, MathUtil.parseOrZero("0"));

        // failed parse.
        assertEquals(0, MathUtil.parseOrZero(Long.toString(Long.MAX_VALUE)));
        assertEquals(0, MathUtil.parseOrZero("and"));
        assertEquals(0, MathUtil.parseOrZero(null));
    }

    /**
     * Check of {@link MathUtil#newtonAlgorithm(int, double[])}.
     */
    @Test
    public final void test_newtonAlgorithm() {
        assertEquals(-1, MathUtil.newtonAlgorithm(3, new double[]{1d, 3d, 3d, 1d}));;
    }

    /**
     * Check of {@link MathUtil#calcEucridian(int, int)}.
     */
    @Test
    public void test_calcEucridian() {
        assertEquals(2, MathUtil.calcEucridian(12, 206));
    }

    /**
     * Check of {@link MathUtil#fibonacci(int)}.
     */
    @Test
    public final void test_fibonacci() {
        assertEquals(21, MathUtil.fibonacci(8));
        assertEquals(-1, MathUtil.fibonacci(-2));
        assertEquals(1, MathUtil.fibonacci(-1));
        assertEquals(0, MathUtil.fibonacci(0));
        assertEquals(1, MathUtil.fibonacci(1));
        assertEquals(1, MathUtil.fibonacci(2));
    }

    /**
     * Check of {@link MathUtil#factorial(int)}.
     */
    @Test
    public final void test_factorial() {
        assertEquals(1, MathUtil.factorial(-1));
        assertEquals(1, MathUtil.factorial(0));
        assertEquals(1, MathUtil.factorial(1));
        assertEquals(2, MathUtil.factorial(2));
        assertEquals(5040, MathUtil.factorial(7));
    }

    /**
     * Check of {@link MathUtil#nCr(int, int)}.
     */
    @Test
    public final void test_nCr() {
        assertEquals(1,  MathUtil.nCr(-1, -1));
        assertEquals(1,  MathUtil.nCr(0, 0));
        assertEquals(0,  MathUtil.nCr(0, 1));
        assertEquals(1,  MathUtil.nCr(3, 0));
        assertEquals(3,  MathUtil.nCr(3, 1));
        assertEquals(3,  MathUtil.nCr(3, 2));
        assertEquals(15, MathUtil.nCr(6, 2));
        assertEquals(20, MathUtil.nCr(6, 3));
    }
}
