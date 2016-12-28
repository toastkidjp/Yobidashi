package jp.toastkid.libs.lambda;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

/**
 * {@link Filters}'s test cases.
 *
 * @author Toast kid
 */
public class FiltersTest {

    /**
     * test {@link Filters#isNull(Object)}.
     */
    @Test
    public void testIsNull() {
        assertTrue(Filters.isNull(null));
        assertFalse(Filters.isNull(new Object()));
        assertFalse(Filters.isNull(Optional.empty()));
    }

    /**
     * test {@link Filters#isNotNull(Object)}.
     */
    @Test
    public void testIsNotNull() {
        assertFalse(Filters.isNotNull(null));
        assertTrue(Filters.isNotNull(new Object()));
        assertTrue(Filters.isNotNull(Optional.empty()));
    }

}
