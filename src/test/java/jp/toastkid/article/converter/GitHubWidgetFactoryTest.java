package jp.toastkid.article.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import jp.toastkid.article.converter.GitHubWidgetFactory;

/**
 * Simple test of {@link GitHubWidgetFactory}.
 *
 * @author Toast kid
 *
 */
public class GitHubWidgetFactoryTest {

    /**
     * Test of {@link GitHubWidgetFactory#make(String)}.
     */
    @Test
    public void testMake() {
        final String widget = GitHubWidgetFactory.make("toastkidjp/Yobidashi");
        assertNotNull(widget);
        assertTrue(widget.contains("http://github.com/toastkidjp"));
        assertTrue(widget.contains("http://github.com/toastkidjp/Yobidashi"));
        assertTrue(widget.contains("http://github.com/toastkidjp/Yobidashi/graphs/contributors"));
    }

    /**
     * Test of {@link GitHubWidgetFactory#make(String)} return passed str.
     */
    @Test
    public void testNotMake() {
        assertEquals("toastkidjp", GitHubWidgetFactory.make("toastkidjp"));
    }

}
