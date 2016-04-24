package jp.toastkid.libs;

import static org.junit.Assert.assertEquals;

import java.awt.Point;

import org.junit.Test;

/**
 * {@link Scales}'s test.
 * @author Toast kid
 *
 */
public class ScalesTest {

    /** testing value. */
    private final Point p1 = new Point(2, 3);
    /** testing value. */
    private final Point p2 = new Point(5, 1);

    /**
     * test {@link Scales#euclid(Point, Point)}.
     */
    @Test
    public final void testEuclid() {
        assertEquals(3.605551275463989, Scales.euclid(p1, p2), 0.0);
    }

    /**
     * test {@link Scales#manhattan(Point, Point)}.
     */
    @Test
    public final void testManhattan() {
        assertEquals(5.0, Scales.manhattan(p1, p2), 0.0);
    }

}
