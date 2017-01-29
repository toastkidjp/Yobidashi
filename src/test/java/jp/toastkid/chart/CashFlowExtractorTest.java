/**
 *
 */
package jp.toastkid.chart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * {@link CashFlowExtractor}'s test case.
 *
 * @author Toast kid
 *
 */
public class CashFlowExtractorTest {

    /**
     * {@link CashFlowExtractor#extract(java.lang.String, java.lang.String)}'s test case.
     * @throws URISyntaxException
     */
    @Test
    public void testExtract() throws URISyntaxException {
        final CashFlowExtractor extractor = new CashFlowExtractor();
        assertTrue(extractor.getTableValues().isEmpty());
        final Map<String, Number> map = extractor.extract(
                Paths.get(getClass().getClassLoader().getResource("chart").toURI()).toString(),
                "日記2017-01"
                );
        assertEquals("{2017-01-20=320, 2017-01-21=1302, 2017-01-23=1632}", map.toString());
        assertEquals("出費: 2017-01 1,632円", extractor.getTitle());
        final List<KeyValue> tableValues = extractor.getTableValues();
        assertEquals(8, tableValues.size());
        final KeyValue keyValue = tableValues.get(0);
        assertEquals("2017-01-20(金)", keyValue.keyProperty().get());
        assertEquals("朝食", keyValue.middleProperty().get());
        assertEquals(320L, keyValue.valueProperty().get());
    }

    /**
     * {@link CashFlowExtractor#extract(String, String)}'s empty input case.
     */
    @Test
    public void test_emptyInput() {
        final CashFlowExtractor extractor = new CashFlowExtractor();
        final Map<String, Number> map = extractor.extract("", "");
        assertTrue(map.isEmpty());
        assertTrue(extractor.getTitle().isEmpty());
        assertTrue(extractor.getTableValues().isEmpty());
    }

    /**
     * Test of {@link CashFlowExtractor#getTitle()}'s failure case.
     * @throws URISyntaxException
     */
    @Test(expected=IllegalStateException.class)
    public void test_getTitle_IllegalState() throws URISyntaxException {
        new CashFlowExtractor().getTitle();
    }

}
