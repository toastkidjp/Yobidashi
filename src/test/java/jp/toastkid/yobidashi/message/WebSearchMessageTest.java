/**
 *
 */
package jp.toastkid.yobidashi.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import jp.toastkid.libs.WebServiceHelper.Type;

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
    public void testMake_y() {
        final WebSearchMessage message = WebSearchMessage.make("query", "Y!");
        assertEquals("query", message.query());
        assertEquals(Type.WEB_SEARCH,  message.type());
    }

    /**
     * {@link WebSearchMessage#make(java.lang.String, java.lang.String)}'s test method.
     */
    @Test
    public void testMake() {
        final WebSearchMessage message = WebSearchMessage.make("query", "type");
        assertEquals("query", message.query());
        assertEquals(Type.WEB_SEARCH,  message.type());
    }

}
