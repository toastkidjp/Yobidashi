package jp.toastkid.libs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.eclipse.collections.impl.factory.Lists;

/**
 * check Pair's behavior.
 * @author Toast kid
 *
 */
public class PairTest {
    /** test target. */
    private Pair<String, Integer> pair;

    /**
     * initialize before every test.
     */
    @Before
    public void setUp() {
        pair = new Pair<>("tomato", 120);
    }

    /**
     * test make instance by constructor.
     */
    @Test
    public final void testConstructor() {
        assertNotNull(pair);
        assertEquals("tomato", pair.left);
        assertEquals(120, pair.right.intValue());
    }

    /**
     * test make instance by constructor.
     */
    @Test
    public final void testOf() {
        final Pair<String, Integer> of = Pair.of("tomato", 120);
        assertNotNull(of);
        assertEquals("tomato", of.left);
        assertEquals(120, of.right.intValue());
        assertEquals(pair, of);
    }

    /**
     * test make instance by constructor.
     */
    @Test
    public final void testWith() {
        final Pair<String, Integer> with = Pair.with("tomato", 120);
        assertNotNull(pair);
        assertEquals("tomato", with.left);
        assertEquals(120, with.right.intValue());
        assertEquals(pair, with);
    }

    @Test
    public final void testLefts() {
        @SuppressWarnings("unchecked")
        final List<Pair<String, Integer>> list = Lists.mutable.of(
                Pair.of("tomato", 120),
                Pair.of("orange", 100),
                Pair.of("apple", 110)
            );
        assertEquals(Lists.mutable.of("tomato", "orange", "apple"), Pair.lefts(list));
    }

    @Test
    public final void testRights() {
        @SuppressWarnings("unchecked")
        final List<Pair<String, Integer>> list = Lists.mutable.of(
                Pair.of("tomato", 120),
                Pair.of("orange", 100),
                Pair.of("apple", 110)
            );
        assertEquals(Lists.mutable.of(120, 100, 110), Pair.rights(list));
    }
}
