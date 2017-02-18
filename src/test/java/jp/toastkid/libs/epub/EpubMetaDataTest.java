package jp.toastkid.libs.epub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

/**
 * {@link EpubMetaData}'s test.
 *
 * @author Toast kid
 *
 */
public class EpubMetaDataTest {

    /** Expected metadata content. */
    private static final String EXPECTED
        = "{\"title\":null,\"subtitle\":null,\"author\":null,\"editor\":null,"
        + "\"publisher\":null,\"version\":\"0.0.1\",\"zipFilePath\":\"epub.epub\","
        + "\"targetPrefix\":null,\"targets\":[],\"containInnerLinks\":false,"
        + "\"ruleSetFileName\":null,\"recursive\":false,\"direction\":\"RTL\","
        + "\"layout\":\"VERTICAL\"}";

    /**
     * Check of {@link EpubMetaData#toString()}.
     */
    @Test
    public void test_toString() {
        final EpubMetaData epubMetaData = new EpubMetaData();
        assertEquals(
                EXPECTED,
                epubMetaData.toString()
                );
    }

    /**
     * Check of {@link EpubMetaData#store()}.
     * @throws IOException
     */
    @Test
    public void test_store() throws IOException {
        final EpubMetaData epubMetaData = new EpubMetaData();
        final Path tempFile = Files.createTempFile("temp", "json");
        epubMetaData.ruleSetFileName = tempFile.toAbsolutePath().toString();
        epubMetaData.store();
        assertEquals(
                EXPECTED,
                new String(Files.readAllBytes(tempFile), StandardCharsets.UTF_8.name())
            );
    }

    /**
     * Test of {@link EpubMetaData#readJson(String)}.
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void test_readJson() throws IOException, URISyntaxException {
        final Path path = Paths.get(getClass().getClassLoader().getResource("epub/test.json").toURI());
        final EpubMetaData epubMetaData = EpubMetaData.readJson(Files.readAllLines(path).get(0));
        assertEquals("test", epubMetaData.title);
        assertEquals("test_sub", epubMetaData.subtitle);
        assertEquals("test_author", epubMetaData.author);
        assertEquals("test_editor", epubMetaData.editor);
        assertEquals("test_publisher", epubMetaData.publisher);
        assertEquals("0.0.1", epubMetaData.version);
        assertEquals("epub.epub", epubMetaData.zipFilePath);
        assertEquals("test_", epubMetaData.targetPrefix);
        assertFalse(epubMetaData.containInnerLinks);
        assertEquals("test_rule.json", epubMetaData.ruleSetFileName);
        assertFalse(epubMetaData.recursive);
        assertEquals(PageProgressDirection.RTL, epubMetaData.direction);
        assertEquals(PageLayout.VERTICAL, epubMetaData.layout);
        assertTrue(epubMetaData.targets.isEmpty());
    }

}
