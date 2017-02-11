package jp.toastkid.libs;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link Stopwatch}'s test case.
 *
 * @author Toast kid
 *
 */
public class StopwatchTest {

    /** Test object. */
    private Stopwatch stopwatch;

    /**
     * Initialize object each testing.
     */
    @Before
    public void setUp() {
        stopwatch = new Stopwatch();
    }

    /**
     * Check simple use case of {@link Stopwatch}.
     */
    @Test
    public void test() {
        stopwatch.add("A");
        IntStream.range(0, 1000).forEach(System.out::print);
        System.out.println();
        stopwatch.add("B");
        assertTrue(0L < stopwatch.get("A"));
        assertTrue(stopwatch.get("B") < 10L);
        stopwatch.print();
    }

    /**
     * Check cannot set null key.
     */
    @Test(expected=IllegalArgumentException.class)
    public void test_nullSet() {
        stopwatch.add(null);
    }

    /**
     * Check cannot get null key.
     */
    @Test(expected=IllegalArgumentException.class)
    public void test_nullGet() {
        stopwatch.get(null);
    }

    /**
     * Check initialize with initial capacity.
     */
    @Test
    public void test_initWithInitialCapacity() {
        final Stopwatch initialCapacity = new Stopwatch(10);
        assertNotNull(initialCapacity);
        assertNotNull(initialCapacity.get("C"));
    }

}
