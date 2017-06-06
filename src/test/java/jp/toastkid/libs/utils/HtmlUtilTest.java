/**
 *
 */
package jp.toastkid.libs.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

/**
 * HtmlUtil の動作を確認する.
 * @author Toast kid
 *
 */
public final class HtmlUtilTest {

    /**
     * {@link jp.toastkid.libs.utils.HtmlUtil#getTableHtml(java.util.Map)} の動作を確認する.
     */
    @Test
    public final void testGetTableHtmlMapOfStringQ() {
        assertEquals(
                "<table><tr><td>key</td><td>value</td></tr></table>",
                HtmlUtil.getTableHtml(new HashMap<String, String>() {{put("key", "value");}})
                );
    }

    /**
     * {@link jp.toastkid.libs.utils.HtmlUtil#getTableHtml(java.util.Map, java.lang.String)} の動作を確認する.
     */
    @Test
    public final void testGetTableHtmlMapOfStringQString() {
        assertEquals(
                "<table><th>Key</th><th>Value</th><tr><td>key</td><td>value</td></tr></table>",
                HtmlUtil.getTableHtml(
                        new HashMap<String, String>() {{put("key", "value");}},
                        "<th>Key</th><th>Value</th>"
                        )
                );

    }

    /**
     * {@link jp.toastkid.libs.utils.HtmlUtil#getTableHtml(java.util.Map, java.lang.String, int)} の動作を確認する.
     */
    @Test
    public final void testGetTableHtmlMapOfStringQStringInt() {
        final Map<String, String> map = new LinkedHashMap<>();
        map.put("key0", "value");
        map.put("key1", "value");
        map.put("key2", "value");
        assertEquals(
                "<table><th>Key</th><th>Value</th><tr><td>key0</td><td>value</td></tr>"
                        + "<tr><td>key1</td><td>value</td></tr></table>",
                        HtmlUtil.getTableHtml(map,"<th>Key</th><th>Value</th>", 2)
                );
        assertEquals(
                "<table><th>Key</th><th>Value</th><tr><td>key0</td><td>value</td></tr>"
                        + "<tr><td>key1</td><td>value</td></tr><tr><td>key2</td><td>value</td></tr>"
                        + "</table>",
                        HtmlUtil.getTableHtml(map,"<th>Key</th><th>Value</th>", -1)
                );
        assertEquals(
                "<table><th>Key</th><th>Value</th></table>",
                HtmlUtil.getTableHtml(map,"<th>Key</th><th>Value</th>", 0)
                );
    }

    /**
     * {@link jp.toastkid.libs.utils.HtmlUtil#toHtmlTitle(java.lang.String)} の動作を確認する.
     */
    @Test
    public final void testGetHTMLTitle() {
        assertEquals("<title>tamago</title>", HtmlUtil.toHtmlTitle("tamago"));
    }

    /**
     * {@link jp.toastkid.libs.utils.HtmlUtil#getRuby(java.lang.String, java.lang.String)} の動作を確認する.
     */
    @Test
    public final void testGetRuby() {
        assertEquals(
                "<ruby><rb>tomato</rb><rp></rp><rt>じゃがいも</rt><rp></rp></ruby>",
                HtmlUtil.getRuby("tomato", "じゃがいも")
                );
    }

    /**
     * {@link jp.toastkid.libs.utils.HtmlUtil#getTooltip(java.lang.String, java.lang.String)} の動作を確認する.
     */
    @Test
    public final void testGetTooltip() {
        assertEquals(
                "<a href=\"#\" class=\"tooltip\">tomato<span class=\"tooltipBody\">"
                        + "stew<span class=\"tooltipAngle\"><span class=\"tooltipAngleInner\" />"
                        + "</span></span></a>",
                        HtmlUtil.getTooltip("tomato", "stew")
                );
    }

    /**
     * {@link jp.toastkid.libs.utils.HtmlUtil#getColor(java.lang.String, java.lang.String)} の動作を確認する.
     */
    @Test
    public final void testGetColorStringString() {
        assertEquals(
                "<span style=\"color: white; \">くろ</span>",
                HtmlUtil.getColor("white", "くろ")
                );
    }

    /**
     * {@link jp.toastkid.libs.utils.HtmlUtil#getColor(java.lang.String, java.lang.String, java.lang.String)}
     * の動作を確認する.
     */
    @Test
    public final void testGetColorStringStringString() {
        assertEquals(
                "<span style=\"color: white; background-color: black\">はい</span>",
                HtmlUtil.getColor("white", "black", "はい")
                );
    }

    /**
     * {@link jp.toastkid.libs.utils.HtmlUtil#makeLink(java.lang.String, java.lang.String)} の動作を確認する.
     */
    @Test
    public final void testMakeLinkStringString() {
        assertEquals(
                "<a href=\"http://sample.jp\">tomato</a>",
                HtmlUtil.makeLink("http://sample.jp", "tomato")
                );
    }

    /**
     * {@link jp.toastkid.libs.utils.HtmlUtil#makeLink(java.lang.String, java.lang.String, boolean)}
     * の動作を確認する.
     */
    @Test
    public final void testMakeLinkStringStringBoolean() {
        assertEquals(
                "<a href=\"http://sample.jp\" target='_blank' rel='noopener'>tomato</a>",
                HtmlUtil.makeLink("http://sample.jp", "tomato", true)
                );
        assertEquals(
                "<a href=\"http://sample.jp\">tomato</a>",
                HtmlUtil.makeLink("http://sample.jp", "tomato", false)
                );
    }

    /**
     * {@link jp.toastkid.libs.utils.HtmlUtil#tagEscape(java.lang.String)} のためのテスト.・メソッド.
     */
    @Test
    public final void tagEscapeTest() {
        assertEquals("&lt;a&gt;&lt;/a&gt;", HtmlUtil.tagEscape("<a></a>"));
    }

    /**
     * 文字列中の HTML タグをすべて除去できることを確認する.
     */
    @Test
    public void tagRemoveTest() {
        assertEquals("tomato", HtmlUtil.tagRemove("<b><a href=\"tomato.html\">tomato</a></b>"));
    }

    /**
     * {@link jp.toastkid.libs.utils.HtmlUtil#tabRemove(java.lang.String)} のためのテスト.・メソッド.
     */
    @Test
    public final void testTabRemove() {
        assertEquals("ab", HtmlUtil.tabRemove("a\tb"));
        assertEquals("ab", HtmlUtil.tabRemove("ab"));
    }

    /**
     * check {@link HtmlUtil#extractBody(String)}.
     */
    @Test
    public final void testExtractBody() {
        assertEquals("tomato", HtmlUtil.extractBody("<body>tomato</body>"));
    }

    /**
     * check {@link HtmlUtil#extractTitle(String)}.
     */
    @Test
    public final void testExtractTitle() {
        assertEquals("tomato", HtmlUtil.extractTitle("<title>tomato</title>"));
        assertEquals("  ", HtmlUtil.extractTitle("  "));
        assertNull(HtmlUtil.extractTitle(null));
    }

    /**
     * check {@link HtmlUtil#underLine(String)}.
     */
    @Test
    public final void testUnderLine() {
        assertEquals("<u>text</u>", HtmlUtil.underLine("text"));
    }

    /**
     * check {@link HtmlUtil#inLineCode(String)}.
     */
    @Test
    public final void testInLineCode() {
        assertEquals("<code>text</code>", HtmlUtil.inLineCode("text"));
    }

    @Test
    public void test_tagScriptRemove() {
        System.out.println(HtmlUtil.tagScriptRemove(
                "<a href='http://www.yahoo.com'><Script>alert(\"hello\");</Script></a>"));
    }

}
