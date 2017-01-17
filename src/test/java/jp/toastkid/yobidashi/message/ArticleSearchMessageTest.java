/**
 *
 */
package jp.toastkid.yobidashi.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * {@link ArticleSearchMessage}'s test cases.
 *
 * @author Toast kid
 *
 */
public class ArticleSearchMessageTest {

    /**
     * {@link ArticleSearchMessage#make(java.lang.String)}'s test method.
     */
    @Test
    public void testMakeString() {
        final ArticleSearchMessage message = ArticleSearchMessage.make("query");
        assertEquals("query", message.query());
        assertEquals("",      message.filter());
    }

    /**
     * {@link ArticleSearchMessage#make(java.lang.String, java.lang.String)}'s test method.
     */
    @Test
    public void testMakeStringString() {
        final ArticleSearchMessage message = ArticleSearchMessage.make("query", "filter");
        assertEquals("query",  message.query());
        assertEquals("filter", message.filter());
    }

}
