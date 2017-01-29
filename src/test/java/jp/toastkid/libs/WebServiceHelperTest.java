package jp.toastkid.libs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import jp.toastkid.libs.WebServiceHelper.Type;

/**
 * WebServiceHelper's Test
 * @author Toast kid
 *
 */
public class WebServiceHelperTest {

    /**
     * check {@link Type#getText()}.
     */
    @Test
    public final void testTypeGetText() {
        assertEquals("Dictionary", Type.Y_DICTIONARY.getText());
        assertEquals("Realtime", Type.REALTIME_SEARCH.getText());
        assertEquals("Restaurant", Type.TABELOG.getText());
        assertEquals("Weblio", Type.WEBLIO.getText());
        assertEquals("Wikipedia", Type.WIKIPEDIA.getText());
        assertEquals("Y!", Type.WEB_SEARCH.getText());
    }

    /**
     * check {@link Type#find(String)}.
     */
    @Test
    public final void testTypeFind() {
        assertEquals(Type.Y_DICTIONARY, Type.find("Dictionary"));
        assertEquals(Type.REALTIME_SEARCH, Type.find("Realtime"));
        assertEquals(Type.TABELOG, Type.find("Restaurant"));
        assertEquals(Type.WEBLIO, Type.find("Weblio"));
        assertEquals(Type.WIKIPEDIA, Type.find("Wikipedia"));
        assertEquals(Type.WEB_SEARCH, Type.find("Y!"));
        // null case.
        assertEquals(Type.WEB_SEARCH, Type.find(null));
        assertEquals(Type.WEB_SEARCH, Type.find("Google"));
    }

    /**
     * check buildRequestUrl.
     */
    @Test
    public final void testBuildRequestUrl() {
        final String expected = "https://search.yahoo.co.jp/search?p=%E3%83%88%E3%83%9E%E3%83%88"
                + "&search.x=1&ei=UTF-8&aq=&oq=";
        assertEquals(expected, WebServiceHelper.buildRequestUrl("トマト", Type.WEB_SEARCH));
        assertNull(expected, WebServiceHelper.buildRequestUrl("トマト", null));
    }

    /**
     * Check of {@link WebServiceHelper#buildRequestUrl(String, Type)}.
     */
    @Test
    public void test_buildRequestUrl_rts() {
        final String expected
            = "http://realtime.search.yahoo.co.jp/search?ei=UTF-8&p=%E3%83%88%E3%83%9E%E3%83%88";
        assertEquals(expected, WebServiceHelper.buildRequestUrl("トマト", Type.REALTIME_SEARCH));
    }

    /**
     * Check of {@link WebServiceHelper#buildRequestUrl(String, Type)}.
     */
    @Test
    public void test_buildRequestUrl_tabelog() {
        final String expected
            = "https://tabelog.com/japan/0/0/lst/?vs=1&sk=%E3%83%88%E3%83%9E%E3%83%88"
                    + "&SrtT=trend&LstCosT=0&sa=&cid=top_navi1&sw=%E3%83%88%E3%83%9E%E3%83%88";
        assertEquals(expected, WebServiceHelper.buildRequestUrl("トマト", Type.TABELOG));
    }

    /**
     * Check of {@link WebServiceHelper#buildRequestUrl(String, Type)}.
     */
    @Test
    public void test_buildRequestUrl_weblio() {
        final String expected
            = "http://ejje.weblio.jp/content/%E3%83%88%E3%83%9E%E3%83%88";
        assertEquals(expected, WebServiceHelper.buildRequestUrl("トマト", Type.WEBLIO));
    }

    /**
     * Check of {@link WebServiceHelper#buildRequestUrl(String, Type)}.
     */
    @Test
    public void test_buildRequestUrl_wikipedia() {
        final String expected
            = "https://ja.wikipedia.org/wiki/Special:Search?search=%E3%83%88%E3%83%9E%E3%83%88";
        assertEquals(expected, WebServiceHelper.buildRequestUrl("トマト", Type.WIKIPEDIA));
    }

    /**
     * Check of {@link WebServiceHelper#buildRequestUrl(String, Type)}.
     */
    @Test
    public void test_buildRequestUrl_dictionary() {
        final String expected
            = "http://dic.search.yahoo.co.jp/search?ei=UTF-8&p=%E3%83%88%E3%83%9E%E3%83%88";
        assertEquals(expected, WebServiceHelper.buildRequestUrl("トマト", Type.Y_DICTIONARY));
    }

    /**
     * Check of {@link WebServiceHelper#buildRequestUrl(String, Type)}.
     */
    @Test
    public void test_buildRequestUrl_image() {
        final String expected
            = "http://image.search.yahoo.co.jp/search?ei=UTF-8&p=%E3%83%88%E3%83%9E%E3%83%88";
        assertEquals(expected, WebServiceHelper.buildRequestUrl("トマト", Type.Y_IMAGE));
    }

    /**
     * Check of {@link WebServiceHelper#buildRequestUrl(String, Type)}.
     */
    @Test
    public void test_buildRequestUrl_loco() {
        final String expected
            = "http://search.loco.yahoo.co.jp/search?ei=UTF-8&p=%E6%B0%B4%E6%88%B8%E9%A7%85";
        assertEquals(expected, WebServiceHelper.buildRequestUrl("水戸駅", Type.Y_LOCO));
    }

    /**
     * Check of {@link WebServiceHelper#buildRequestUrl(String, Type)}.
     */
    @Test
    public void test_buildRequestUrl_map() {
        final String expected
            = "http://map.search.yahoo.co.jp/search?ei=UTF-8&p=%E6%B0%B4%E6%88%B8%E9%A7%85";
        assertEquals(expected, WebServiceHelper.buildRequestUrl("水戸駅", Type.Y_MAP));
    }

    /**
     * Check of {@link WebServiceHelper#buildRequestUrl(String, Type)}.
     */
    @Test
    public void test_buildRequestUrl_video() {
        final String expected
            = "http://video.search.yahoo.co.jp/search?ei=UTF-8&p=%E3%83%88%E3%83%9E%E3%83%88";
        assertEquals(expected, WebServiceHelper.buildRequestUrl("トマト", Type.Y_VIDEO));
    }

}
