package jp.toastkid.article.search;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import static org.junit.Assert.*;

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
        assertNotNull(searchResult.filePath());
        assertTrue(searchResult.getOrEmpty("").isEmpty());
        assertTrue(0 < searchResult.lastModified());
        assertTrue(searchResult.mapIsEmpty());
        assertSame(0, searchResult.size());
        assertNull(searchResult.title());
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
