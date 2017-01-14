/**
 *
 */
package jp.toastkid.yobidashi.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * {@link WebSearchMessage}'s test case.
 *
 * @author Toast kid
 *
 */
public class WebSearchMessageTest {

    /**
     * {@link WebSearchMessage#make(java.lang.String, java.lang.String)}'s test method.
     */
    @Test
    public void testMake() {
        final WebSearchMessage message = WebSearchMessage.make("query", "type");
        assertEquals("query", message.query());
        assertEquals("type",  message.type());
    }

}
