/**
 * .
 */
package jp.toastkid.article;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import jp.toastkid.article.converter.PostProcessor;

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
     * test method for {@link jp.toastkid.article.converter.PostProcessor#process(java.lang.String)} .
     */
    @Test
    public void testProcess() {
        assertEquals(
                "<h2><a id=\"tomato\" href=\"#tomato\">■</a>"
                + "<a class='redLink' href=\"file:///internal/md/746F6D61746F.md\">tomato</a>"
                + " <a class='redLink' href=\"file:///internal/md/746F6D61746F.md\">tomato</a></h2>",
                processor.process("<h2>[[tomato]] [[md:tomato]]</h2>")
                );
    }

    /**
     * test method for
     * {@link jp.toastkid.article.converter.PostProcessor#generateSubheadings
     * (jp.toastkid.wiki.models.ViewTemplate)} .
     */
    @Test
    public void testGenerateSubheading() {
        processor.process("<h2>[[tomato]] [[md:tomato]]</h2>");

        final String extected = "<ul><li><a href=\"#tomato\">"
                + "<a class='redLink' href=\"file:///internal/md/746F6D61746F.md\">tomato</a>"
                + " <a class='redLink' href=\"file:///internal/md/746F6D61746F.md\">tomato</a></a></li>"
                + System.lineSeparator() + "</ul>";

        assertEquals(extected, processor.generateSubheadings());
    }

}
