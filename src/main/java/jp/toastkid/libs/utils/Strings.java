/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.libs.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * {@link String} utilities.
 *
 * @author Toast kid
 */
public final class Strings {

    /** Empty string. */
    private static final String EMPTY = "";

    /** 半角カタカナの集合 */
    private static final String HALFSIZE_KATAKANA
        = "ｱｲｳｴｵｧｨｩｪｫｶｷｸｹｺｻｼｽｾｿﾀﾁﾂｯﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖｬｭｮﾗﾘﾙﾚﾛﾜｦﾝｰﾞﾟ､｡";

    /** 全角カタカナの集合 */
    private static final String FULLSIZE_KATAKANA
        = "アイウエオァィゥェォカキクケコサシスセソタチツッテトナニヌネノ"
                + "ハヒフヘホマミムメモヤユヨャュョラリルレロワヲンー゛゜、.";

    /** 半角記号の集合 */
    private static final String HALFSIZE_SYMBOL = "+-*/=|!?\"#@$%&'`()[],,.;:_<>^";

    /** 全角記号の集合 */
    private static final String FULLSIZE_SYMBOL
        = "＋－＊／＝｜！？”＃＠＄％＆’｀（）［］，、．；：＿＜＞＾";

    /** lamdaの設定がわからない時はこの値を使うといい(定数、0.9). */
    public static final double SAMPLE_LAMDA = 0.9;

    /** Line separator. */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * Deny make instance.
     */
    private Strings(){}

    /**
     * Return directory separator.
     */
    public static String getDirSeparator(){
        if (getOSName().indexOf("indow") != -1) {
            return "/";
        } else {
            return System.getProperty("file.separator");
        }
    }

    /**
     * 現在使用しているOSの文字列表現を返す.
     * (120119) 作成
     * @return OS 名の文字列表現
     */
    public static String getOSName() {
        return System.getProperty("os.name");
    }

    /**
     * 四捨五入した類似度を返す
     * @param x : 比較する文字列
     * @param y : 比較する文字列
     * @param lamda
     * @return value : 計算した類似度
     */
    public static double calcScaledSimilarity(
            final String x,
            final String y,
            final double lamda
            ){
        final BigDecimal bi = new BigDecimal(String.valueOf(calcSimilarity(x,y,lamda)));
        final double value = bi.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        return value;
    }
    /**
     * 四捨五入した類似度を返す
     * @param x : 比較する文字列
     * @param y : 比較する文字列
     * @param lamda
     * @param k
     * @return value : 計算した類似度
     */
    public static double calcScaledSimilarity(
            final String x,
            final String y,
            final double lamda,
            final int k
            ){
        final BigDecimal bi = new BigDecimal(String.valueOf(calcSimilarity(x ,y , lamda)));
        final double value = bi.setScale(k,BigDecimal.ROUND_HALF_UP).doubleValue();
        return value;
    }

    /**
     * カーネル法により類似度を計算し、値を返す.<BR>
     * Lamdaは0.9を使う.
     *
     * @param x : 比較する文字列
     * @param y : 比較する文字列
     * @return similarity : 類似度
     */
    public static double calcSimilarity(
            final String x,
            final String y
            ){
        return calcSimilarity(x,y,SAMPLE_LAMDA);
    }

    /**
     * カーネル法により類似度を計算し、値を返す
     * @param x : 比較する文字列
     * @param y : 比較する文字列
     * @param lamda
     * @return similarity : 類似度
     */
    public static double calcSimilarity(
                                        final String x,
                                        final String y,
                                        final double lamda
                                        ){
        String tmp = EMPTY;
        ArrayList<String> arrangeXY;
        arrangeXY = new ArrayList<String>();
        double xx = 0.0;
        double yy = 0.0;
        double xy = 0.0;

        double tmpxx = 0;
        double tmpyy = 0;

        for(int i = 0; i < x.length() - 1; i++){
            for(int j = i + 1; j < x.length(); j++){
                tmp = EMPTY + x.charAt(i) + x.charAt(j);
                if(arrangeXY.contains(tmp) == false){
                        arrangeXY.add(tmp);
                }
            }
        }
        for(int i = 0; i < y.length() - 1; i++){
            for(int j = i + 1; j < y.length(); j++){
                tmp = EMPTY + y.charAt(i) + y.charAt(j);
                if(arrangeXY.contains(tmp) == false){
                    arrangeXY.add(tmp);
                }
            }
        }
        //int i = 0; i < arrangexy.size(); i++
        for(int i = 0; i < arrangeXY.size(); i++){
            for(int x1 = 0 ;x1 < x.length() - 1; x1++){
                if((EMPTY + x.charAt(x1)).equals(EMPTY + arrangeXY.get(i).charAt(0))){
                    for(int x2 = x1 + 1; x2 < x.length(); x2++){
                        if((EMPTY+x.charAt(x2)).equals(EMPTY+arrangeXY.get(i).charAt(1))){
                            tmpxx += Math.pow(lamda,(x2-x1+1));
                        }
                    }
                }
            }
            for(int y1 = 0; y1 < y.length() - 1; y1++){
                if( (EMPTY + y.charAt(y1) ).equals(EMPTY + arrangeXY.get(i).charAt(0) ) ){
                    for(int y2 = y1 + 1; y2 < y.length(); y2++){
                        if( (EMPTY + y.charAt(y2) ).equals( EMPTY + arrangeXY.get(i).charAt(1) ) ){
                            tmpyy += Math.pow(
                                              lamda,
                                              ( y2 - y1 + 1 )
                                              );
                        }
                    }
                }
            }
            xx = xx + tmpxx * tmpxx;
            yy = yy + tmpyy * tmpyy;
            xy = xy + tmpxx * tmpyy;
            tmpxx = 0.0;
            tmpyy = 0.0;
        }//int i = 0; i < arrangexy.size(); i++
        final double similarity = xy / Math.sqrt( xx * yy );
        return similarity;
    }

    /**
     * 文字列を２グラムに分解して返す
     * @param pStr
     * @return ArrayList
     */
    public static ArrayList<String> getBiGrams(final String pStr){
        final ArrayList<String> resList = new ArrayList<String>();
        for(int i = 0; i < pStr.length() - 1; i++){
            resList.add(EMPTY + pStr.charAt(i) + pStr.charAt(i + 1));
        }
        return resList;
    }

    /**
     * アルファベット全角半角統一(半角に統一)
     * @param x : 処理する文字列
     * @return x : 処理後の文字列
     */
    public static String alphaNonDist(String x){
        final StringBuilder tempBuf = new StringBuilder(x);
        for (int i = 0; i < tempBuf.length(); i++) {
            final char c = tempBuf.charAt(i);
            if (c >= 'ａ' && c <= 'ｚ') {
                tempBuf.setCharAt(i, (char) (c - 'ａ' + 'a'));
            } else if (c >= 'Ａ' && c <= 'Ｚ') {
                tempBuf.setCharAt(i, (char) (c - 'Ａ' + 'A'));
            }
        }
        x = tempBuf.toString();
        return x;
    }

    /**
     * 数字全角半角統一(全角に統一)
     * @param x : 処理する文字列
     * @return x : 処理後の文字列
     */
    public static String numNonDist(String x){
        final StringBuilder tempBuf = new StringBuilder(x);
        for (int i = 0; i < x.length(); i++) {
            final char c = x.charAt(i);
            if (c >= '0' && c <= '9') {
                tempBuf.setCharAt(i, (char) (c - '0' + '０'));
            }
        }
        x = tempBuf.toString();
        return x;
    }

    /**
     * 半角全角カタカナひらがな統一(ひらがなに統一)
     * @param x : 処理する文字列
     * @return x : 処理後の文字列
     */
    public static String katahiraNonDist(String x) {
        final StringBuilder tempBuf = new StringBuilder();
        char ch;
        kataZenHanNonDist(x);
        for (int i = 0; i<x.length(); i++) {
            ch = x.charAt(i);
            if (ch >= 0x30A0 && ch <=0x30FA) {
                ch -= 0x60;
            }
            tempBuf.append(ch);
        }
        x = tempBuf.toString();
        return x;
    }

    /**
     * 記号全角半角統一(半角に統一)
     * @param x : 処理する文字列
     * @return x : 処理後の文字列
     */
    public static String kigouNonDist(String x){
        final StringBuilder tempBuf = new StringBuilder();
        char ch;
        int idx;
        for (int i =0; i< x.length(); i++) {
            ch = x.charAt(i);
            if ((idx = FULLSIZE_SYMBOL.indexOf(ch)) >= 0) {
                ch = HALFSIZE_SYMBOL.charAt(idx);
            }
            tempBuf.append(ch);
        }
        x = tempBuf.toString();
        return x;
    }

    /**
     * 記号全角半角統一(全角に統一)
     * @param x : 処理する文字列
     * @return x : 処理後の文字列
     */
    public static String kigouFullSizeNonDist(String x){
        final StringBuilder tempBuf = new StringBuilder();
        char ch;
        int idx;
        for (int i =0; i< x.length(); i++) {
            ch = x.charAt(i);
            if ((idx = HALFSIZE_SYMBOL.indexOf(ch)) >= 0) {
                ch = FULLSIZE_SYMBOL.charAt(idx);
            }
            tempBuf.append(ch);
        }
        x = tempBuf.toString();
        return x;
    }

    /**
     * カタカナ全角半角統一(全角に統一)
     * @param  x : 処理する文字列
     * @return x : 処理後の文字列
     */
    public static String kataZenHanNonDist(String x) {
        final StringBuilder tempBuf = new StringBuilder();
        int idx;
        char ch;
        for (int i =0; i< x.length(); i++) {
            ch = x.charAt(i);
            if ((idx = HALFSIZE_KATAKANA.indexOf(ch)) >= 0) {
                ch = FULLSIZE_KATAKANA.charAt(idx);
            }
            tempBuf.append(ch);
        }
        x = tempBuf.toString();
        return x;
    }

    /**
     * 渡された文字列を ' で括って返す.
     * Excelやデータベースで使用するファイルを作成する際に役立つ.
     * @param str ' で括りたい文字列
     * @return 'で括ったstr
     */
    public static String singleQuote(final String str){
        final StringBuilder resBuf = new StringBuilder(str.length() + 2 );
        return resBuf.append("'").append(str).append("'").toString();
    }

    /**
     * 渡された文字列を " で括って返す.
     * Excelやデータベースで使用するファイルを作成する際に役立つ.
     * @param str "で括りたい文字列
     * @return "で括ったstr
     */
    public static String doubleQuote(final String str){
        final StringBuilder resBuf = new StringBuilder(str.length() + 2 );
        return resBuf.append("\"").append(str).append("\"").toString();
    }

    /**
     * 渡された文字列に付いた " をすべて削除する.
     * @param str "で括りたい文字列
     * @return "で括ったstr
     * 110521作成
     */
    public static String removeQuote(final String str){
        return str.replaceAll("\"", EMPTY);
    }

    /**
     * 2つの文字列のレーベンシュタイン距離(置換許容)を計算する.
     * @see <a href="http://www.mwsoft.jp/programming/munou/javascript_levenshtein.html">
     * Javascript でレーベンシュタイン距離の実演</a>
     * @see <a href="http://ja.wikipedia.org/wiki/レーベンシュタイン距離">レーベンシュタイン距離</a>
     * @param str1 文字列
     * @param str2 文字列
     * @return レーベンシュタイン距離(int)
     */
    public static final int levenshteinDistance(
            final String str1,
            final String str2
        ) {
        final int longerLength = Math.max(str1.length(), str2.length()) + 1;
        final int[][] d = new int[longerLength][longerLength];
        int cost = 0;
        for (int i = 1; i <= str1.length(); i++ ) {
            for (int j = 1; j <= str2.length(); j++ ) {
                cost = str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1;
                d[i][j] = MathUtil.minPrim(
                    d[i - 1][j] + 1,
                    d[i][j - 1] + 1,
                    d[i - 1][j - 1] + cost
                );
            }
        }
        return d[str1.length()][str2.length()];
    }
    /**
     * oneLineLength を最大長とする文字列 List を生成して返す.
     * @param str 文字列
     * @param oneLineLength 1行の最大長
     * @return oneLineLength を最大長とする文字列 List
     * @see <a href="http://okwave.jp/qa/q2023486.html">文字列を１文字ずつ分割する方法</a>
     */
    public static final List<String> getFolderStrs(
            final String str,
            final int oneLineLength
            ) {
        final List<String>  result = new ArrayList<String>(str.length() / oneLineLength);
        final StringBuilder sb     = new StringBuilder(oneLineLength);
        for (final char c : str.toCharArray()) {
            sb.append(c);
            if (sb.length() == oneLineLength) {
                result.add(sb.toString());
                sb.setLength(0);
            }
        }
        result.add(sb.toString());
        return result;
    }
    /**
     * 複数の文字列をそのまま連結して1つの文字列にして返す.
     * @param pieces 複数の文字列
     * @return pieces の要素をすべてつなげた1つの文字列
     */
    public static final String join(final String... pieces) {
        final StringBuilder joined = new StringBuilder();
        for (final String piece : pieces) {
            joined.append(piece);
        }
        return joined.toString();
    }

    /**
     * 複数のオブジェクトをglueで連結して1つの文字列にして返す.
     * @param glue 連結記号
     * @param pieces 複数のオブジェクト
     * @return pieces の要素をglueで連結した1つの文字列
     */
    public static final String join(final String glue, final Object... pieces) {
        final StringBuilder joined = new StringBuilder();
        for (final Object piece : pieces) {
            joined.append(joined.length() != 0 ? glue : EMPTY)
                  .append(piece != null ? piece.toString() : EMPTY);
        }
        return joined.toString();
    }

    /**
     * 0から9までのintに0をつけて返す.例として、3を渡した時は03を返す.
     * @param number
     * @return 3を渡した時は03
     */
    public static String addZero(final int number) {
        return number < 0 || 9 < number
                ? Integer.toString(number)
                : "0" + number;
    }

    /**
     * MD5で生成したlongのhash値を返す.
     * @param str 文字列
     * @return longのhash値
     * @see <a href="http://stackoverflow.com/questions/1660501/
     *what-is-a-good-64bit-hash-function-in-java-for-textual-strings">
     * What is a good 64bit hash function in Java for textual strings?</a>
     */
    public static long md5Hash(final String str) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final byte[] data = str.getBytes();
            md.update(data);
            final byte[] digest = md.digest();
            return new BigInteger(digest).longValue();
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * MD5で生成したlongのhash値を返す.
     * @param str 文字列
     * @return longのhash値
     * * @see <a href="http://stackoverflow.com/questions/1660501/
     *what-is-a-good-64bit-hash-function-in-java-for-textual-strings">
     * What is a good 64bit hash function in Java for textual strings?</a>
     */
    public static final long longHash(final String str) {
        long h = 1125899906842597L; // prime
        final int len = str.length();
        for (int i = 0; i < len; i++) {
            h = 31 * h + str.charAt(i);
        }
        return h;
    }

    /**
     * 第1引数の文字列に対し、文字 target を文字<b>列</b> replacement に置換する.
     * @param str 文字列
     * @param target 置換対象(1文字)
     * @param replacement 置換する文字列
     * @return 置換後の文字列
     */
    public static final String replace(
            final String str,
            final char target,
            final String replacement
            ) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        final StringBuilder builder = new StringBuilder(str.length() + str.length());
        for (int i = 0; i < str.length(); i++) {
            final char c = str.charAt(i);
            if (c == target) {
                builder.append(replacement);
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }
    /**
     * 第1引数の文字列に対し、replacement のキーと一致する文字を、
     * replacement の値(文字<b>列</b>)に置換する.
     * @param str 文字列
     * @param replacement Map&lt;Character, String&gt;
     * @return 置換後の文字列
     */
    public static final String replace(
            final String str,
            final Map<Character, String> replacement
            ) {
        if (StringUtils.isEmpty(str) || replacement == null || replacement.size() == 0) {
            return str;
        }
        final StringBuilder builder = new StringBuilder(str.length() + str.length());
        for (int i = 0; i < str.length(); i++) {
            final char c = str.charAt(i);
            if (replacement.containsKey(c)) {
                builder.append(replacement.get(c));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }
    /**
     * Is started string http:// or https://
     * @param url URL string.
     * @return if url startswith http protocol.
     */
    public static boolean isHttpUrl(final String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        return url.startsWith("http://") || url.startsWith("https://");
    }

    /**
     * CamelCase string to snake_case.
     * @param camel string
     */
    public static final String camelToSnake(final String camel) {
        if (StringUtils.isEmpty(camel)) {
            return camel;
        }
        final StringBuilder sb = new StringBuilder(camel.length() + camel.length());
        final char[] chars = camel.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            final char c = chars[i];
            if (Character.isUpperCase(c)) {
                sb.append(sb.length() != 0 ? '_' : EMPTY).append(Character.toLowerCase(c));
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }

    /**
     * Snake_case convert to CamelCase string.
     * @param snake string
     */
    public static final String snakeToCamel(final String snake) {
        if (StringUtils.isEmpty(snake)) {
            return snake;
        }
        final StringBuilder sb = new StringBuilder(snake.length() + snake.length());
        final char[] chars = snake.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            final char c = chars[i];
            if (c == '_') {
                sb.append((i + 1) < chars.length ? Character.toUpperCase(chars[++i]) : EMPTY);
            } else {
                sb.append(sb.length() == 0 ? Character.toUpperCase(c) : Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }

    /**
     * Extract string with passed regex and wrapped result.
     * @param target string
     * @param regex regex
     * @return string wrapped Optional.
     */
    public static Optional<String> extractMatchesOpt(final String target, final String regex) {
        return Optional.ofNullable(extractMatches(target, regex));
    }

    /**
     * Extract string with passed regex.
     * @param target string
     * @param regex regex
     * @return string.
     */
    public static String extractMatches(final String target, final String regex) {

        if (StringUtils.isEmpty(target)) {
            return null;
        }

        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(target);
        return (matcher.find()) ? matcher.group(1) : null;
    }

    /**
     * For avoiding java.util.regex.PatternSyntaxException: Unclosed group near index.
     * @param query
     * @return ( and ) escaped.
     */
    public static String escapeForRegex(final String str) {
        return str.replace("{", "\\{").replace("}", "\\}").replace("(", "\\(").replace(")", "\\)")
                .replace("|", "\\|").replace("?", "\\?").replace(".", "\\.").replace("*", "\\*");
    }

    /**
     * Initialize StringBuilder.
     * @return StringBuilder
     */
    public static StringBuilder builder() {
        return new StringBuilder();
    }

    /**
     * Initialize StringBuilder with initialCapacity.
     * @param initialCapacity
     * @return StringBuilder
     */
    public static StringBuilder builder(final int initialCapacity) {
        return new StringBuilder(initialCapacity);
    }

    /**
     * Return empty string.
     * @return empty string("")
     */
    public static String empty() {
        return EMPTY;
    }

    /**
     * Count character number.
     * @param str
     * @return character number.
     */
    public static long countLength(final String str) {
        return str != null ? str.replaceAll("\\s", "").codePoints().count() : 0;
    }
}
