package jp.toastkid.article.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * {@link ContentType}'s test.
 *
 * @author Toast kid
 */
public class ContentTypeText {

    /**
     * {@link ContentType#getText()}'s test.
     */
    @Test
    public void testHtml() {
        assertEquals("text/html", ContentType.HTML.getText());
        assertEquals("HTML", ContentType.HTML.toString());
    }

    /**
     * {@link ContentType#getText()}'s test.
     */
    @Test
    public void testText() {
        assertEquals("text/plain", ContentType.TEXT.getText());
        assertEquals("TEXT", ContentType.TEXT.toString());
    }

    /**
     * {@link ContentType#valueOf(String)}'s test.
     */
    @Test
    public void test_valueOf() {
        assertEquals(ContentType.HTML, ContentType.valueOf("HTML"));
    }

    /**
     * {@link ContentType#values()}'s test.
     */
    @Test
    public void test_for_tc() {
        assertTrue(ContentType.values().length != 0);
    }

}
