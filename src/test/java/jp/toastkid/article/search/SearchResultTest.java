package jp.toastkid.article.search;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

/**
 * Test of {@link SearchResult}.
 * @author Toast kid
 *
 */
public class SearchResultTest {

    /**
     * Test of {@link SearchResult} constructor.
     * @throws IOException
     */
    @Test
    public void testSearchResult() throws IOException {
        final Path path = Files.createTempFile("temp", ".txt");
        final SearchResult searchResult = new SearchResult(path.toString());
        assertNotNull(searchResult.toString());
    }

    /**
     * Test of {@link SearchResult#makeSimple(String)}.
     *
     * @throws IOException
     */
    @Test
    public void testMakeSimple() throws IOException {
        final Path path = Files.createTempFile("temp", ".txt");
        final SearchResult simple = SearchResult.makeSimple(path.toString());
        assertNotNull(simple);
    }

}
