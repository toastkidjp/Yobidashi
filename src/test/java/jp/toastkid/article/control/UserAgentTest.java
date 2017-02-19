package jp.toastkid.article.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * {@link UserAgent}'s test cases.
 *
 * @author Toast kid
 *
 */
public class UserAgentTest {

    /**
     * Check of text().
     */
    @Test
    public void test() {
        assertNotNull(UserAgent.IPHONE.text());
        assertNotNull(UserAgent.IPAD.text());
        assertNotNull(UserAgent.MAC.text());
    }

    /**
     * Test for coverage rate.
     */
    @Test
    public void test_for_coverage() {
        assertTrue(0 < UserAgent.values().length);
    }

    /**
     * Test of {@link UserAgent#valueOf(String)};
     */
    @Test
    public void test_valueOf() {
        assertEquals(UserAgent.IPHONE, UserAgent.valueOf("IPHONE"));

    }

    /**
     * Test of {@link UserAgent#valueOf(String)} null case.
     */
    @Test(expected=NullPointerException.class)
    public void test_valueOf_null_case() {
        UserAgent.valueOf(null);
    }

    /**
     * Test of {@link UserAgent#valueOf(String)} failure case.
     */
    @Test(expected=IllegalArgumentException.class)
    public void test_valueOf_failure_case() {
        UserAgent.valueOf("iPhone");
    }

}
