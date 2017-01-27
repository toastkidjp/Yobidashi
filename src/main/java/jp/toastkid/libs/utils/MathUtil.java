package jp.toastkid.libs.utils;

import java.time.LocalDate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.impl.factory.primitive.IntSets;

/**
 * 数字に関する独自定義のユーティリティクラス.
 * <HR>
 * (120325) 乱数発生メソッド xor128() を追加<BR>
 * @author 06ki044
 *
 */
public final class MathUtil {

    /**
     * Private constructor.
     */
    private MathUtil() {
        // NOP.
    }

    /**
     * pNumberを最大の数とする  pSize 個のユニークな要素を持つ int 集合を生成し返す
     * @param pNumber : 集合に含まれる最大の数
     * @param pSize : 集合の要素数
     * @return randamNumberSet Set<Integer>
     */
    public static MutableIntSet createRandamNumberSet(final int pNumber, final int pSize){
        final int max = pNumber + 1;
        MutableIntSet randamNumberSet = null;
        randamNumberSet = IntSets.mutable.empty();
        while(randamNumberSet.size() < pSize){
            randamNumberSet.add((int)(Math.random() * max));
            if (max == randamNumberSet.size()) {
                break;
            }
        }
        return randamNumberSet;
    }

    /**
     * pNumberを最大の数とする,pSize個のユニークな要素を持つint型配列を生成し返す.
     * @param pNumber : 集合に含まれる最大の数
     * @param pSize : 集合の要素数
     * @return intSet pNumberを最大の数とする,pSize個のユニークな要素を持つint型配列
     */
    public static int[] makeRandamNumberArray(
            final int pNumber,
            final int pSize
            ){
        final MutableIntSet randamNumberSet = createRandamNumberSet(pNumber, pSize);
        return randamNumberSet.toArray();
    }

    /**
     * 引数として渡された２数の最大公約数をユークリッドの互除法で求める.
     * @param a 引数１
     * @param b 引数２
     * @return a : ２数の最大公約数
     */
    public static int calcEucridian(int a, int b){
        int p = 0;
        while(a != 0 && b != 0){
            p = a / b;
            a = a - (p * b);
            if(a < b){
                final int temp = a;
                a = b;
                b = temp;
            }
        }
        return a;
    }

    /**
     * ニュートンアルゴリズムで方程式 a[3]x^3 + a[2]x^2 + a[1]x + a[0] = 0 の解の一つを求める
     *
     * 以下のようにして使う。<BR>
     * <PRE>
     * double[] a = {1,3,3,1};
     * System.out.println("この方程式の解は x = " + newtonAlgorithm(3, a));
     * </PRE>
     *
     * @see <a href ="http://情報処理試験.jp/FE21b-pm/t08.html">平成21年 秋期 基本情報技術者 午後 問08 </a>
     * @param x 解の予測値
     * @return x 解
     */
    public static int newtonAlgorithm(
            final int x,
            final double[] pA
            ){
        // a[3]x^3 + a[2]x^2 + a[1]x + a[0]の値
        double f;
        // b[2]x^2 + b[1]x + b[0]の値
        double d;

        double resX = x;

        // 感覚的にわかりやすくするための処理
        final double[] a = new double[4];
        a[0] = pA[3];
        a[1] = pA[2];
        a[2] = pA[1];
        a[3] = pA[0];

        final double[] b = new double[3];
        b[2] = 3.0 * a[3];
        b[1] = 2.0 * a[2];
        b[0] = a[1];
        for(int i = 0; i < 99; i++){
            f = ( ( a[3] * resX + a[2] ) * resX + a[1]) * resX + a[0];
            d = (b[2] * resX + b[1] ) * resX + b[0];
            // System.out.println("x = " + resX + " : f = " + f + " : d = " + d);
            if(f != 0 && d != 0){
                resX = resX - (f / d);
            }else{
                break;
            }
        }
        return Math.round((float)resX);
    }
    /**
     * フィボナッチ数列の解答を計算し返却する
     * @param n 整数
     * @return フィボナッチ数列の解
     */
    public static int fibonacci(final int n){
        if(n < 0){
            if(Math.abs(n) % 2 == 1){
                return fibCalculate(n);
            }
            return (-1) * fibCalculate(n);
        }
        return (fibCalculate(n));
    }
     /**
      * fibonacci()メソッドから呼び出され、計算を行う。
      * @param n 整数
      * @return フィボナッチ数列の解
      */
    private static int fibCalculate(final int n){

        if (n < 0) {/*F(－n) = (－1)^(n+1)*Fn */
            return fibCalculate(Math.abs(n));
        } else if (n == 0) {
            return 0;
        } else if (n == 1) {
            return 1;
        }

        /*F(n)=F(n-1)+F(n-2)*/
        return fibCalculate(n - 1) + fibCalculate(n - 2);
    }

    /**
     * nCr の値を返す
     * @param n
     * @param r
     * @return res : nCr の値
     */
    public static int nCr(final int n, final int r){
        int res = 1;
        for(int i = 0; i < r; i++){
            if(i != r){
                res = res * (n - i);
                continue;
            }

            res = res * (n - i + 1);
        }
        res = res / factorial(r);
        return res;
    }

    /**
     * a の階乗を返す。
     * @param a
     * @return factorialVal : a の階乗の値
     */
    public static int factorial(final int a){
        int factorialVal = 1;
        for (int i = 2; i <= a; i++) {
            factorialVal = factorialVal * i;
        }
        return factorialVal;
    }


    /**
     * 渡された文字列から seed を作り、乱数生成クラスのオブジェクトを返す。
     * @param seedStr
     * @return Random クラスのオブジェクト
     */
    public static final Random getRand(final String seedStr) {
        int score = 0;
        final byte[] bytes = seedStr.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            score = score + (bytes[i] * 10);
        }
        return new Random(score);
    }

    /**
     * 最大値 max の要素を  size 個持つ TreeSet を生成して返す。
     * 日付が変わると生成される値も変わる。
     * <HR>
     * (130319) ロト6数字セレクタに使うため作成<BR>
     * @param size いくつ乱数を生成するか
     * @param max 最大値
     * @param word 好きな言葉
     * @param isAllowZero ゼロを許容するか否か
     * @return 最大値 max の要素を size 個持つ TreeSet
     */
    public static final Set<Integer> getDailyRandomIntSet(
            final int size,
            final int max,
            final String word,
            final boolean isAllowZero
            ) {
        if (size < 1) {
            throw new IllegalArgumentException();
        }
        final Set<Integer> set = new TreeSet<>();
        if (max < size) {
            for (int i = 1; i <= max; i++) {
                set.add(i);
            }
            return set;
        }
        final LocalDate today = LocalDate.now();
        final String seedStr = Integer.toString(today.getYear() + today.getDayOfYear());
        final Random rand = getRand(seedStr + word);
        while (set.size() < size) {
            final int nextInt = rand.nextInt(max);
            if (isAllowZero){
                set.add(nextInt);
            } else {
                set.add(nextInt + 1);
            }
        }
        return set;
    }
    /**
     * 最大値 max の要素を size 個持つ TreeSet を生成して返す。
     * 日付が変わると生成される値も変わる。
     * なお、0 が含まれる。
     * <HR>
     * (130319) ロト6数字セレクタに使うため作成<BR>
     * @param size いくつ乱数を生成するか
     * @param max 最大値
     * @return 最大値 max の要素を  size 個持つ TreeSet
     */
    public static final Set<Integer> getDailyRandomIntSet(
            final int size,
            final int max
            ){
        return getDailyRandomIntSet(size, max, "", true);
    }
    /**
     * Nullpo 回避用メソッド、パースができない時は 0 を返す。
     * @param str 文字列
     * @return int 値、パースができない時は0
     */
    public static int getInt(final String str) {
        try {
            return Integer.parseInt(str);
        } catch (final NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 最小値を返す。
     * @param numbers 複数の数値
     * @return 最小値
     */
    public static Number min(final Number... numbers) {
        double min = Double.MAX_VALUE;
        for (final Number n : numbers) {
            final double d = n.doubleValue();
            min = Math.min(min, d);
        }
        return min;
    }

    /**
     * 最小値を返す。
     * @param numbers 複数の数値
     * @return 最小値
     */
    public static int minPrim(final int... numbers) {
        int min = Integer.MAX_VALUE;
        for (final int n : numbers) {
            min = Math.min(min, n);
        }
        return min;
    }

    /**
     * 最大値を返す。
     * @param numbers 複数の数値
     * @return 最大値
     */
    public static Number max(final Number... numbers) {
        double max = Double.MIN_VALUE;
        for (final Number n : numbers) {
            final double d = n.doubleValue();
            max = Math.max(max, d);
        }
        return max;
    }

    /**
     * 最大値を返す。
     * @param numbers 複数の数値
     * @return 最大値
     */
    public static int maxPrim(final int... numbers) {
        int max = Integer.MIN_VALUE;
        for (final int n : numbers) {
            max = Math.max(max, n);
        }
        return max;
    }

    /**
     * return least squares method's result pair.
     * @param double square array.
     * @return double[]
     * @see <a href="https://ja.wikipedia.org/wiki/%E6%9C%80%E5%B0%8F%E4%BA%8C%E4%B9%97%E6%B3%95">
     * 最小二乗法</a>
     * @see <a href="http://qiita.com/HaLGa0710/items/505f8de5484031eaa7a0">
     * 【備忘録】とても美しい最小二乗法</a>
     */
    public static double[] leastSquare(final double[][] array) {
        int b = 0;
        int c = 0;
        int d = 0;
        int e = 0;
        final int n = array.length;
        for (int i = 0; i < n;i++){
            b += array[i][0] * array[i][1];
            c += array[i][1];
            d += array[i][0] * array[i][0];
            e += array[i][0];
        }

        return new double[]{
                (double) (b * n - c * e) / (-e * e + n * d),
                (double) (c * d - b * e) / (-e * e + n * d)
                };
    }

    /**
     * return least squares method's result pair.
     * @param map Number map.
     * @return double[]
     * @see <a href="https://ja.wikipedia.org/wiki/%E6%9C%80%E5%B0%8F%E4%BA%8C%E4%B9%97%E6%B3%95">
     * 最小二乗法</a>
     * @see <a href="http://qiita.com/HaLGa0710/items/505f8de5484031eaa7a0">
     * 【備忘録】とても美しい最小二乗法</a>
     */
    public static double[] leastSquare(final Map<Number, Number> map) {
        int b = 0;
        int c = 0;
        int d = 0;
        int e = 0;
        final int n = map.size();
        for (final Entry<Number, Number> entry : map.entrySet() ) {
            b += entry.getKey().intValue() * entry.getValue().intValue();
            c += entry.getValue().intValue();
            d += entry.getKey().intValue() * entry.getKey().intValue();
            e += entry.getKey().intValue();
        }

        return new double[]{
                (double) (b * n - c * e) / (-e * e + n * d),
                (double) (c * d - b * e) / (-e * e + n * d)
                };
    }
    /**
     * parse に成功したらその数値、失敗したら0を返す.
     * @param str
     * @return parse に成功したらその数値、失敗したら0を返す.
     */
    public static int parseOrZero(final String str) {
        try {
            return Integer.parseInt(str);
        } catch (final NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

}
