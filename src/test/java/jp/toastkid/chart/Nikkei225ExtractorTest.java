package jp.toastkid.chart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * {@link Nikkei225Extractor}'s test case.
 *
 * @author Toast kid
 *
 */
public class Nikkei225ExtractorTest {

    /**
     * {@link Nikkei225Extractor#extract(java.lang.String, java.lang.String)}'s test case.
     * @throws URISyntaxException
     */
    @Test
    public void testExtract() throws URISyntaxException {
        final Nikkei225Extractor extractor = new Nikkei225Extractor();
        assertTrue(extractor.getTableValues().isEmpty());
        final Map<String, Number> map = extractor.extract(
                Paths.get(getClass().getClassLoader().getResource("chart").toURI()).toString(),
                "日記2017-01"
                );
        assertEquals("{2017-01-20(金)=19137, 2017-01-23(月)=18891}", map.toString());
        assertEquals("日経平均株価: 2017-01 ", extractor.getTitle());
        final List<KeyValue> tableValues = extractor.getTableValues();
        assertEquals(0, tableValues.size());
    }

    /**
     * Cannot use empty prefix.
     */
    @Test(expected=NumberFormatException.class)
    public void test_illegalInput() {
        final Nikkei225Extractor extractor = new Nikkei225Extractor();
        extractor.extract("", "");
    }

    /**
     * Test of {@link Nikkei225Extractor#getTitle()}'s failure case.
     * @throws URISyntaxException
     */
    @Test(expected=IllegalStateException.class)
    public void test_getTitle_IllegalState() throws URISyntaxException {
        new Nikkei225Extractor().getTitle();
    }


}
