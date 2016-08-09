/**
 * .
 */
package jp.toastkid.libs.wiki;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import jp.toastkid.wiki.models.ViewTemplate;

/**
 * {@link PostProcessor}'s test cases.
 * @author Toast kid
 *
 */
public class PostProcessorTest {

    /** test object. */
    private PostProcessor processor;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        processor = new PostProcessor("");
    }

    /**
     * test method for {@link jp.toastkid.libs.wiki.PostProcessor#process(java.lang.String)} .
     */
    @Test
    public void testProcess() {
        assertEquals(
                "<h2><a id=\"tomato\" href=\"#tomato\">■</a>"
                + "<a class=\"redLink\" href=\"/txt/746F6D61746F.txt\">tomato</a>"
                + " <a class=\"redLink\" href=\"/md/746F6D61746F.md\">tomato</a></h2>",
                processor.process("<h2>[[tomato]] [[md:tomato]]</h2>")
                );
    }

    /**
     * test method for
     * {@link jp.toastkid.libs.wiki.PostProcessor#generateSubheadings
     * (jp.toastkid.wiki.models.ViewTemplate)} .
     */
    @Test
    public void testGenerateSubheading() {
        processor.process("<h2>[[tomato]] [[md:tomato]]</h2>");

        final String extected = "<ul class=\"nav\"><li><a href=\"#tomato\">"
                + "<a class=\"redLink\" href=\"/txt/746F6D61746F.txt\">tomato</a>"
                + " <a class=\"redLink\" href=\"/md/746F6D61746F.md\">tomato</a></a><br/>"
                + System.lineSeparator() + "</ul>";

        assertEquals(extected, processor.generateSubheadings(ViewTemplate.MATERIAL));
    }

}
