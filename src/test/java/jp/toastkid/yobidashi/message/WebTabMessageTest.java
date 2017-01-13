package jp.toastkid.yobidashi.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import jp.toastkid.article.models.ContentType;

/**
 * {@link WebTabMessage}'s test.
 *
 * @author Toast kid
 *
 */
public class WebTabMessageTest {

    /**
     * Check {@link WebTabMessage#make(String, String, ContentType)}.
     */
    @Test
    public void test_make() {
        final String t = "title";
        final String c = "html_content";
        final ContentType type = ContentType.HTML;
        final WebTabMessage webTabMessage = WebTabMessage.make(t , c , type );
        assertEquals(t, webTabMessage.getTitle());
        assertEquals(c, webTabMessage.getContent());
        assertEquals(type, webTabMessage.getContentType());
    }

}
