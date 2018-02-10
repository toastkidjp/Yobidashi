/**
 *
 */
package jp.toastkid.libs.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

/**
 * {@link jp.toastkid.libs.utils.Strings}'s test cases.
 *
 * @author Toast kid
 *
 */
public final class StringsTest {

    /** sample of CamelCase string. */
    private static final String CAMEL = "TomatoCurry";

    /** sample of snake_case string. */
    private static final String SNAKE = "tomato_curry";

    /**
     * Check {@link Strings#calcScaledSimilarity(String, String, double)}.
     */
    @Test
    public void test_calcScaledSimilarity_3_args() {
        assertEquals(
                0.58,
                Strings.calcScaledSimilarity("ロイ・キーン", "ロビー・キーン", Strings.SAMPLE_LAMDA),
                0.00001d
                );
    }

    /**
     * Check {@link Strings#calcScaledSimilarity(String, String, double, int)}.
     */
    @Test
    public void test_calcScaledSimilarity_4_args() {
        assertEquals(
                0.6,
                Strings.calcScaledSimilarity("ロイ・キーン", "ロビー・キーン", Strings.SAMPLE_LAMDA, 1),
                0.00001d
                );
    }

    /**
     * {@link Strings#getBiGrams(String)}.
     */
    @Test
    public void test_getBiGrams() {
        final ArrayList<String> biGrams = Strings.getBiGrams("隣の客はよく柿食う客だ");
        assertEquals("[隣の, の客, 客は, はよ, よく, く柿, 柿食, 食う, う客, 客だ]", biGrams.toString());
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#getDirSeparator()} 's test method.
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
     * {@link jp.toastkid.libs.utils.Strings#getOSName()} 's test method.
     */
    @Test
    public final void testGetOSName() {
        assertEquals(
                System.getProperty("os.name"),
                Strings.getOSName()
            );
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#katahiraNonDist(java.lang.String)} 's test method.
     */
    @Test
    public final void testKatahiraNonDist() {
        assertEquals("あｶさ他", Strings.katahiraNonDist("アｶさ他"));
    }

    /**
     * Check {@link Strings#alphaNonDist(String)}.
     */
    @Test
    public void test_alphaNonDist() {
        assertEquals("ABCDEF", Strings.alphaNonDist("ＡＢＣＤＥＦ"));
    }

    /**
     * Check {@link Strings#numNonDist(String)}.
     */
    @Test
    public void test_numNonDist() {
        assertEquals("１２３４５６", Strings.numNonDist("123456"));
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#kigouNonDist(java.lang.String)} 's test method.
     */
    @Test
    public final void testKigouNonDist() {
        assertEquals("$$&&", Strings.kigouNonDist("$＄＆&"));
    }

    /**
     * Check of {@link Strings#kigouFullSizeNonDist(String)}.
     */
    @Test
    public final void test_kigouFullSizeNonDist() {
        assertEquals("＄＄＆＆", Strings.kigouFullSizeNonDist("$＄＆&"));
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#levenshteinDistance(java.lang.String, java.lang.String)} 's test method.
     */
    @Test
    public void levenshteinDistanceTest() {
        assertEquals(1, Strings.levenshteinDistance("last", "least"));
        assertEquals(3, Strings.levenshteinDistance("kitten", "sitting"));
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#getFolderStrList(java.lang.String, int)} 's test method.
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
                Stream.of(target).collect(Collectors.joining()),
                target[0].length()
            );
        assertEquals(Arrays.asList(target), actual);
    }

    /**
     * {@link jp.toastkid.libs.utils.Strings#addZero(int)} 's test method.
     */
    @Test
    public void addZeroTest() {
        assertEquals("-1", Strings.addZero(-1));
        assertEquals("00", Strings.addZero(0));
        assertEquals("09", Strings.addZero(9));
        assertEquals("11", Strings.addZero(11));
        assertEquals("10", Strings.addZero(10));
    }

    /**
     * StringUtil#md5Hash 's test method.
     */
    @Test
    public void md5HashTest() {
        final Set<Long> set = makeSet();
        assertTrue(set.stream().allMatch(l -> {return l instanceof Long;}));
        assertEquals(4, set.size());
    }

    private Set<Long> makeSet() {
        final Set<Long> set = new HashSet<>();
        set.add(Strings.md5Hash("トマト"));
        set.add(Strings.md5Hash("トマト1"));
        set.add(Strings.md5Hash("トマト2"));
        set.add(Strings.md5Hash("トマト "));
        return set;
    }

    /**
     * StringUtil#longHash 's test method.
     */
    @Test
    public void longHashTest() {
        final Set<Long> set = makeSet();
        assertTrue(set.stream().allMatch(l -> {return l instanceof Long;}));
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
        final Map<Character, String> map = new HashMap<Character, String>(){
            private static final long serialVersionUID = 1L;
        {
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
                Strings.replace("null", new HashMap<Character, String>(){/**
                     *
                     */
                    private static final long serialVersionUID = 1L;

                {put(null, "tomato");}})
                );
        // ちゃんと処理される
        assertEquals("&lt;b&gt;H&amp;M&lt;/b&gt;", Strings.replace("<b>H&M</b>", map));
    }

    /**
     * Check of  isHttpUrl().
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
     * Check of  join(). (Array ver.)
     */
    @Test
    public void testJoin() {
        assertEquals(
                "1,-1,,orange,tomato,8.2,tohu",
                Strings.join(",", 1, -1, null, "orange", "tomato", 8.2, "tohu")
                );
    }

    /**
     * Check of  join().
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

    /**
     * Check of  {@link Strings#builder()}.
     */
    @Test
    public void testBuilder() {
        final StringBuilder builder = Strings.builder();
        assertEquals(0, builder.length());
        final StringBuilder builder2 = Strings.builder();
        assertEquals(0, builder2.length());
        assertNotEquals(builder, builder2);
        assertNotSame(builder, builder2);
        final StringBuilder withCapacity30 = Strings.builder(30);
        assertEquals(30, withCapacity30.capacity());
    }

    /**
     * Check of  {@link Strings#escapeForRegex(String)}.
     */
    @Test
    public void testEscapeForRegex() {
        assertEquals("\\{\\(\\)\\}", Strings.escapeForRegex("{()}"));
    }

    /**
     * Check of {@link Strings#singleQuote(String)}.
     */
    @Test
    public void test_singleQuote() {
        assertEquals("'abc'", Strings.singleQuote("abc"));
        assertEquals("''", Strings.singleQuote(""));
    }

    /**
     * Check of {@link Strings#doubleQuote(String)}.
     */
    @Test
    public void test_doubleQuote() {
        assertEquals("\"abc\"", Strings.doubleQuote("abc"));
        assertEquals("\"\"", Strings.doubleQuote(""));
    }

    /**
     * Check of {@link Strings#singleQuote(String)} is not nullable.
     */
    @Test(expected=NullPointerException.class)
    public void test_singleQuote_not_nullable() {
        Strings.singleQuote(null);
    }

    /**
     * Check of {@link Strings#doubleQuote(String)} is not nullable.
     */
    @Test(expected=NullPointerException.class)
    public void test_doubleQuote_not_nullable() {
        Strings.doubleQuote(null);
    }

    /**
     * Check of {@link Strings#removeQuote(String)}.
     */
    @Test
    public void test_removeQuote() {
        assertEquals("tomato", Strings.removeQuote("\"tomato\""));
        assertEquals("tomato", Strings.removeQuote("tomato"));
    }

    /**
     * Check of {@link Strings#removeQuote(String)} is not nullable.
     */
    @Test(expected=NullPointerException.class)
    public void test_removeQuote_not_nullable() {
        Strings.removeQuote(null);
    }

    /**
     * Check of {@link Strings#empty()}.
     */
    @Test
    public void test_empty() {
        assertSame("", Strings.empty());
        assertSame(Strings.empty(), Strings.empty());
        assertEquals("", Strings.empty());
        assertEquals(Strings.empty(), Strings.empty());
    }

    /**
     * Check of {@link Strings#countLength}.
     */
    @Test
    public void test_countLength() {
        assertEquals(0, Strings.countLength(""));
        assertEquals(1, Strings.countLength("1"));
        assertEquals(0, Strings.countLength(" "));
        assertEquals(0, Strings.countLength("  "));
        assertEquals(0, Strings.countLength(null));
        assertEquals(1, Strings.countLength(" 1 "));
        assertEquals(6, Strings.countLength("aaaaa 1 "));
    }

}
