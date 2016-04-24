package jp.toastkid.libs.calendar;

import java.util.HashMap;
import java.util.Map;

/**
 * 閏月.
 * @author Toast kid
 * @see <a href="http://detail.chiebukuro.yahoo.co.jp/qa/question_detail/q1451934619">y!chie</a>
 */
public class LeapMonth {
    /**
     * 太陰太陽暦の閏月.
     * 1843/9｜1846/5｜1849/4｜1852/2 ｜1854/7｜1857/5｜1860/3
     * 1862/8｜1865/5｜1868/4｜1870/10｜1873/6｜1876/5｜1879/3
     * 1881/7｜1884/5｜1887/4｜1889/12｜1892/6｜1895/5｜1898/3
     * 1900/8｜1903/5｜1906/4｜1909/2 ｜1911/6｜1914/5｜1917/2
    1919/7｜1922/5｜1925/4｜1928/2 ｜1930/6｜1933/5｜1936/3
    1938/7｜1941/6｜1944/4｜1947/2 ｜1949/7｜1952/5｜1955/3
    1957/8｜1960/6｜1963/4｜1966/3 ｜1968/7｜1971/5｜1974/4
    1976/8｜1979/6｜1982/4｜1984/10｜1987/6｜1990/5｜1993/3
    1995/8｜1998/5｜2001/4｜2004/2 ｜2006/7｜2009/5｜2012/3
    2014/9｜2017/5｜2020/4｜2023/2 ｜2025/6｜2028/5｜2031/3


    なおAJD4JPだと下記の閏月判定が間違っている
    2003 10 false
    2006 6 false
    2014 8 false
    2022 11 false
    2033 7 false
    2034 11 false
    2036 5 false
     */
    private static final Map<Integer, Integer> LEAP_PAIR;
    static {
        LEAP_PAIR = new HashMap<Integer, Integer>(){
            /** default. */
            private static final long serialVersionUID = 1L;
            {
            put(1843, 9);
            put(1846, 5);
            put(1849, 4);
            put(1852, 2);
            put(1854, 7);
            put(1857, 5);
            put(1860, 3);
            put(1862, 8);
            put(1865, 5);
            put(1868, 4);
            put(1870, 10);
            put(1873, 6);
            put(1876, 5);
            put(1879, 3);
            put(1881, 7);
            put(1884, 5);
            put(1887, 4);
            put(1889, 12);
            put(1892, 6);
            put(1895, 5);
            put(1898, 3);
            put(1900, 8);
            put(1903, 5);
            put(1906, 4);
            put(1909, 2);
            put(1911, 6);
            put(1914, 5);
            put(1917, 2);
            put(1919, 7);
            put(1922, 5);
            put(1925, 4);
            put(1928, 2);
            put(1930, 6);
            put(1933, 5);
            put(1936, 3);
            put(1938, 7);
            put(1941, 6);
            put(1944, 4);
            put(1947, 2);
            put(1949, 7);
            put(1952, 5);
            put(1955, 3);
            put(1957, 8);
            put(1960, 6);
            put(1963, 4);
            put(1966, 3);
            put(1968, 7);
            put(1971, 5);
            put(1974, 4);
            put(1976, 8);
            put(1979, 6);
            put(1982, 4);
            put(1984, 10);
            put(1987, 6);
            put(1990, 5);
            put(1993, 3);
            put(1995, 8);
            put(1998, 5);
            put(2001, 4);
            put(2004, 2);
            put(2006, 7);
            put(2009, 5);
            put(2012, 3);
            put(2014, 9);
            put(2017, 5);
            put(2020, 4);
            put(2023, 2);
            put(2025, 6);
            put(2028, 5);
            put(2031, 3);
        }};
    }
    /**
     * 閏月か否かを判定して返す.
     * @param year 太陰太陽暦での年
     * @param month 太陰太陽暦での月
     * @return 閏月なら true
     */
    public static final boolean isLeapMonth(final int year, final int month) {
        if (!LEAP_PAIR.containsKey(year) || LEAP_PAIR.get(year) != month) {
            return false;
        }
        return true;
    }
    /**
     * 指定された年の閏月を返す.
     * @param year 太陰太陽暦での年
     * @return 閏月、ない場合は -1
     */
    public static final int getLeapMonth(final int year) {
        return !LEAP_PAIR.containsKey(year) ? -1 : LEAP_PAIR.get(year);
    }
}
