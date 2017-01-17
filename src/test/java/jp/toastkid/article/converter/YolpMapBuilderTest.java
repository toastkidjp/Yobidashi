package jp.toastkid.article.converter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import jp.toastkid.article.converter.YolpMapBuilder;

/**
 * {@link YolpMapBuilder}'s test case.
 * @author Toast kid
 *
 */
public class YolpMapBuilderTest {

    /** expected result. */
    private static final String EXPECTED
        = "<img width=\"600\" height=\"400\" "
                + "src=\"http://map.olp.yahooapis.jp/OpenLocalPlatform/V1/static?appid=test"
                + "&width=600&height=400&pin1=35.748807,139.80978,レストラン三幸,blue"
                + "&pin2=35.747296,139.800864,キッチンフライパン,blue"
                + "&pin3=35.746024,139.802037,いな穂,blue\" />";

    /**
     * check to build url.
     */
    @Test
    public final void testToString() {
        final YolpMapBuilder map = new YolpMapBuilder();
        map.setWidth(600);
        map.setHeight(400);
        map.setAppId("test");
        map.pins.add("35.748807,139.80978,レストラン三幸,blue");
        map.pins.add("35.747296,139.800864,キッチンフライパン,blue");
        map.pins.add("35.746024,139.802037,いな穂,blue");
        assertEquals(EXPECTED, map.toString());
    }

}
