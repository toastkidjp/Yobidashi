package jp.toastkid.chart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * {@link FilesLengthExtractor}'s test cases.
 *
 * @author Toast kid
 *
 */
public class FilesLengthExtractorTest {

    /**
     * {@link FilesLengthExtractor#extract(java.lang.String, java.lang.String)}'s test case.
     * @throws URISyntaxException
     */
    @Test
    public void testExtract() throws URISyntaxException {
        final FilesLengthExtractor extractor = new FilesLengthExtractor();
        assertTrue(extractor.getTableValues().isEmpty());
        final Map<String, Number> map = extractor.extract(
                Paths.get(getClass().getClassLoader().getResource("chart").toURI()).toString(),
                "日記2017-01"
                );
        assertEquals(
                "{2017-01-20(金)=87, 2017-01-21(土)=109, 2017-01-23(月)=79, 合計=275}",
                map.toString()
                );
        assertEquals("日記の文字数: 2017-01 275字", extractor.getTitle());
        final List<KeyValue> tableValues = extractor.getTableValues();
        assertEquals(0, tableValues.size());
    }

    /**
     * Cannot use empty prefix.
     */
    @Test(expected=NumberFormatException.class)
    public void test_illegalInput() {
        final FilesLengthExtractor extractor = new FilesLengthExtractor();
        extractor.extract("", "");
    }

    /**
     * Test of {@link FilesLengthExtractor#getTitle()}'s failure case.
     * @throws URISyntaxException
     */
    @Test(expected=IllegalStateException.class)
    public void test_getTitle_IllegalState() throws URISyntaxException {
        new FilesLengthExtractor().getTitle();
    }


}
