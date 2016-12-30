package jp.toastkid.article.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * {@link ContentType}'s test.
 *
 * @author Toast kid
 */
public class ContentTypeText {

    /**
     * {@link ContentType}'s test.
     */
    @Test
    public void testHtml() {
        assertEquals("text/html", ContentType.HTML.getText());
        assertEquals("HTML", ContentType.HTML.toString());
    }

    /**
     * {@link ContentType}'s test.
     */
    @Test
    public void testText() {
        assertEquals("text/plain", ContentType.TEXT.getText());
        assertEquals("TEXT", ContentType.TEXT.toString());
    }

}
