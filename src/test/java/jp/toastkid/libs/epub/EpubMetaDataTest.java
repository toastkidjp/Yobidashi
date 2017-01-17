package jp.toastkid.libs.epub;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

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
        + "\"targetPrefix\":null,\"targets\":null,\"containInnerLinks\":false,"
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

}
