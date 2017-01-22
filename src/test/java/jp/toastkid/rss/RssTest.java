package jp.toastkid.rss;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * {@link Rss}'s test.
 *
 * @author Toast kid
 *
 */
public class RssTest {

    /**
     * Test {@link Rss#empty()}.
     */
    @Test
    public void testEmpty() {
        final Rss empty = Rss.empty();
        assertNull(empty.expandTitle());
        assertNull(empty.getCreator());
        assertNull(empty.getDate());
        assertNull(empty.getDescription());
        assertNull(empty.getLink());
        assertNull(empty.getTitle());
        assertNull(empty.getUrl());
        assertTrue(empty.getSubjects().isEmpty());
        assertTrue(empty.items().isEmpty());

        assertSame(empty, Rss.empty());
    }

}
