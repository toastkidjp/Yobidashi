/**
 *
 */
package jp.toastkid.libs.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.collections.api.set.FixedSizeSet;
import org.eclipse.collections.impl.factory.Sets;
import org.junit.Ignore;
import org.junit.Test;

/**
 * {@link jp.toastkid.libs.utils.Strings} のテスト.クラス.
 * @author Toast kid
 *
 */
public final class StringsTest {
    /** sample of CamelCase string. */
    private static final String CAMEL = "TomatoCurry";
    /** sample of snake_case string. */
    private static final String SNAKE = "tomato_curry";

    /**
     * {@link jp.toastkid.libs.utils.Strings#getDirSeparator()} のためのテスト.・メソッド.
     */
    @Test
    public final void testGetDirSeparator() {
        final String expected;
        if (Strings.getOSName().indexOf("indow") != -1) {
            expected = "/";
        } else {
            expected = System.getProperty("file.separator");
        }
        assertEquals(
                expected,
                Strings.getDirSeparator()
            );
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#getOSName()} のためのテスト.・メソッド.
     */
    @Test
    public final void testGetOSName() {
        assertEquals(
                System.getProperty("os.name"),
                Strings.getOSName()
            );
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#calcScaledSimilarity(java.lang.String, java.lang.String, double)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testCalcScaledSimilarityStringStringDouble() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#calcScaledSimilarity(java.lang.String, java.lang.String, double, int)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testCalcScaledSimilarityStringStringDoubleInt() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#calcSimilarity(java.lang.String, java.lang.String)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testCalcSimilarityStringString() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#calcSimilarity(java.lang.String, java.lang.String, double)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testCalcSimilarityStringStringDouble() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#calcSimilarity(java.util.ArrayList, java.lang.String)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testCalcSimilarityArrayListOfStringString() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#calcSimilarity(java.util.ArrayList, java.lang.String, double)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testCalcSimilarityArrayListOfStringStringDouble() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#getBiGrams(java.lang.String)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testGetBiGrams() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#alphaNonDist(java.lang.String)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testAlphaNonDist() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#numNonDist(java.lang.String)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testNumNonDist() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#katahiraNonDist(java.lang.String)} のためのテスト.・メソッド.
     */
    @Test
    public final void testKatahiraNonDist() {
        assertEquals("あｶさ他", Strings.katahiraNonDist("アｶさ他"));
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#kigouNonDist(java.lang.String)} のためのテスト.・メソッド.
     */
    @Test
    public final void testKigouNonDist() {
        assertEquals("$$&&", Strings.kigouNonDist("$＄＆&"));
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#kataZenHanNonDist(java.lang.String)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testKataZenHanNonDist() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#encodeHTML(java.lang.String)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testEncodeHTML() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#doubleQuote(java.lang.String)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testQuoteString() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#removeQuote(java.lang.String)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testRemoveQuote() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#uniteTwoString(java.lang.String, java.lang.String)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testUniteTwoString() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#htmlStringConvert(java.lang.String)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testHtmlStringConvert() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#parseDoubleDivide(java.lang.String, java.lang.String)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testParseDoubleDivide() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.wiki.ArticleGenerator#toBytedString_EUC_JP(java.lang.String)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testToBytedString_EUC_JP() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.wiki.ArticleGenerator#decodeBytedStr(java.lang.String, java.lang.String)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testDecodeBytedStr() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#printDebugString(java.lang.String)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testPrintDebugString() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#getNowTime()} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testGetNowTime() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#getNowOclock()} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testGetNowOclock() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#getNowTime_YMDHMSSE()} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testGetNowTime_YMDHMSSE() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#toYmdhmsse(java.util.Date)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testGetNowTime_YMDHMSSEDate() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#toUniTypeDate(java.util.Date)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testGetNowTime_YMDHMSSE_typeUni() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#getNowDate_YMDE()} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testGetNowDate_YMDE() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#getNowDate_YMDE_forMyWiki()} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testGetNowDate_YMDE_forMyWiki() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#getNowDate_YMDE_forMyWiki(int, int, int)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testGetNowDate_YMDE_forMyWikiIntIntInt() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#getNowDate_YMDE_forMyWiki(java.util.Calendar)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testGetNowDate_YMDE_forMyWikiCalendar() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#getRSSURLsMap(java.lang.String)} のためのテスト.・メソッド.
     */
    @Ignore
    public final void testGetRSSURLsMap() {
        fail("まだ実装されていません"); // TODO
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#levenshteinDistance(java.lang.String, java.lang.String)} のテスト.
     */
    @Test
    public void levenshteinDistanceTest() {
        assertEquals(1, Strings.levenshteinDistance("last", "least"));
        assertEquals(3, Strings.levenshteinDistance("kitten", "sitting"));
    }
    /**
     * {@link jp.toastkid.libs.utils.Strings#getFolderStrList(java.lang.String, int)} のテスト.
     */
    @Test
    public void getFolderStrListTest() {
        final String[] target = {
                "　安政と年号のあらたまった年の三月十八日であった。",
                "半七はこれから午飯《ひるめし》を食って、浅草の三社",
                "《さんじゃ》祭りを見物に出かけようかと思っていると",
                "ころへ、三十五六の男がたずねて来た。かれは神田の明",
                "神下の山城屋という質屋の番頭で、利兵衛という白鼠《",
                "しろねずみ》であることを半七はかねて知っていた。"
        };
        final List<String> actual = Strings.getFolderStrs(
                CollectionUtil.implode(target, ""),
                target[0].length()
            );
        assertEquals(Arrays.asList(target), actual);
    }
    /**
     * {@link jp.toastkid.libs.utils.Strings#addZero(int)} のテスト.
     */
    @Test
    public void addZeroTest() {
        assertEquals("03", Strings.addZero(3));
    }
    /**
     * StringUtil#md5Hash のテスト.
     */
    @Test
    public void md5HashTest() {
        final FixedSizeSet<Long> set = Sets.fixedSize.of(
                Strings.md5Hash("トマト"),
                Strings.md5Hash("トマト1"),
                Strings.md5Hash("トマト2"),
                Strings.md5Hash("トマト ")
                );
        assertTrue(set.allSatisfy(l -> {return l instanceof Long;}));
        assertEquals(4, set.size());
    }
    /**
     * StringUtil#longHash のテスト.
     */
    @Test
    public void longHashTest() {
        final FixedSizeSet<Long> set = Sets.fixedSize.of(
                Strings.longHash("トマト"),
                Strings.longHash("トマト1"),
                Strings.longHash("トマト2"),
                Strings.longHash("トマト ")
                );
        assertTrue(set.allSatisfy(l -> {return l instanceof Long;}));
        assertEquals(4, set.size());
    }
    /**
     * replace の動作確認.
     */
    @Test
    public void replaceTest() {
        // 門前払い
        assertNull(Strings.replace(null, 'a', ""));
        assertEquals("", Strings.replace("", 'a', ""));
        // nullnull する
        assertEquals("nullnull", Strings.replace("aa", 'a', null));
        // ちゃんと処理される
        assertEquals("aba", Strings.replace("cbc", 'c', "a"));
        assertEquals("thomasbthomas", Strings.replace("cbc", 'c', "thomas"));
        assertEquals("&lt;b>", Strings.replace("<b>", '<', "&lt;"));
    }
    /**
     * replace(str, map) の動作確認.
     */
    @Test
    public void replaceMultiTest() {
        final Map<Character, String> map = new HashMap<Character, String>(){{
            put('<', "&lt;");
            put('>', "&gt;");
            put('&', "&amp;");
            put('\"', "&quot;");
            }};
        // 門前払い
        assertNull(Strings.replace(null, map));
        assertEquals("", Strings.replace("", map));
        assertEquals("aa", Strings.replace("aa", null));
        assertEquals("aa", Strings.replace("aa", new HashMap<>()));
        // nullnull する
        assertEquals(
                "nullnull",
                Strings.replace("aa", new HashMap<Character, String>(){{put('a', null);}})
                );
        // とりあえず止まらない
        assertEquals(
                "null",
                Strings.replace("null", new HashMap<Character, String>(){{put(null, "tomato");}})
                );
        // ちゃんと処理される
        assertEquals("&lt;b&gt;H&amp;M&lt;/b&gt;", Strings.replace("<b>H&M</b>", map));
    }
    /**
     * test isHttpUrl().
     */
    @Test
    public void isHttpUrlTest() {
        assertTrue(Strings.isHttpUrl("http://www.yahoo.co.jp"));
        assertTrue(Strings.isHttpUrl("https://www.yahoo.co.jp"));
        assertFalse(Strings.isHttpUrl("ftp://www.yahoo.co.jp"));
        assertFalse(Strings.isHttpUrl(""));
        assertFalse(Strings.isHttpUrl(null));
    }

    /**
     * test join(). (Array ver.)
     */
    @Test
    public void testJoin() {
        assertEquals(
                "1,-1,,orange,tomato,8.2,tohu",
                Strings.join(",", 1, -1, null, "orange", "tomato", 8.2, "tohu")
                );
    }

    /**
     * test join().
     */
    @Test
    public void testJoinSimple() {
        assertEquals(
                "1-1nullorangetomatotohu",
                Strings.join("1", "-1", null, "orange", "tomato", "tohu")
                );
    }


    /**
     * check behavior {@link Strings#camelToSnake(String)}.
     */
    @Test
    public void testCamelToSnake() {
        assertEquals(SNAKE, Strings.camelToSnake(CAMEL));
        assertEquals("orange_shake", Strings.camelToSnake("orangeShake"));
    }

    /**
     * check behavior {@link Strings#snakeToCamel(String)}.
     */
    @Test
    public void testSnakeToCamel() {
        assertEquals(CAMEL, Strings.snakeToCamel(SNAKE));
        assertEquals("Adapter", Strings.snakeToCamel("_adapter"));
    }

    /**
     * check {@link Strings#extractMatchesOpt(String, String)}.
     */
    @Test
    public void testExtractMatchesOpt() {
        final String target = "<img src=\"http://www.yahoo.co.jp/favicon.ico\">";
        final Optional<String> extractMatches
            = Strings.extractMatchesOpt(target, "src=\"(.+?)\"");
        assertTrue(extractMatches.isPresent());
        assertEquals("http://www.yahoo.co.jp/favicon.ico", extractMatches.get());

        // check not found case.
        assertFalse(Strings.extractMatchesOpt(target, "alt=\"(.+?)\"").isPresent());

        // check nullable.
        assertFalse(Strings.extractMatchesOpt(null, "").isPresent());
        assertFalse(Strings.extractMatchesOpt("", null).isPresent());
    }

    /**
     * check {@link Strings#extractMatchesOpt(String, String)}.
     */
    @Test
    public void testExtractMatches() {
        final String target = "<img src=\"http://www.yahoo.co.jp/favicon.ico\">";
        assertEquals(
                "http://www.yahoo.co.jp/favicon.ico",
                Strings.extractMatches(target, "src=\"(.+?)\"")
                );

        // check not found case.
        assertNull(Strings.extractMatches(target, "alt=\"(.+?)\""));

        // check nullable.
        assertNull(Strings.extractMatches(null, ""));
        assertNull(Strings.extractMatches("", null));
    }
}
