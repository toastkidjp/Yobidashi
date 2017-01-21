package jp.toastkid.rss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import jp.toastkid.rss.Rss.Item;

/**
 * {@link RssParser}'s test.
 * @author Toast kid
 *
 */
public class RssParserTest {

    /**
     * Test of {@link RssParser#parse(String)}.
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testParseString() throws IOException, URISyntaxException {
        final Path path
            = Paths.get(getClass().getClassLoader().getResource("rss/sample.xml").toURI());
        final String rssText = new String(Files.readAllBytes(path), StandardCharsets.UTF_8.name());
        final Rss rss = new RssParser().parse(rssText);
        assertNull(rss.expandTitle());
        assertNull(rss.getCreator());
        assertEquals("Sat, 21 Jan 2017 19:32:11 +0900", rss.getDate());
        assertEquals("This instance has only 1 item.", rss.getDescription());
        assertEquals("http://www.yahoo.co.jp/", rss.getLink());
        assertEquals("RSS title", rss.getTitle());
        assertNull(rss.getUrl());
        assertTrue(rss.getSubjects().isEmpty());

        final List<Item> items = rss.items();
        assertFalse(items.isEmpty());

        final Item item = items.get(0);
        assertTrue(item.getContent().isEmpty());
        assertNull(item.getDate());
        assertEquals("This item is a mere mock.", item.getDescription());
        assertEquals("http://www.yahoo.co.jp", item.getLink());
        assertEquals("Item title", item.getTitle());
    }

}
