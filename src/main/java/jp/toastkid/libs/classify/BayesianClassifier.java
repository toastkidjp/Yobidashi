package jp.toastkid.libs.classify;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.collections.impl.factory.Sets;

/**
 * ナイーブベイズ分類器による分類器
 * <HR>
 * (130406) 抽象度を高める修正を実施<BR>
 * @author Toast kid
 *
 */
public final class BayesianClassifier {
    /** 正解数 */
    private int morningcount = 0;
    /** 学習データ総数 */
    private int totalcount   = 0;
    /** 学習結果マップ */
    private final Map<String, int[]> bicount = new HashMap<String, int[]>();
    /** バイアス */
    private final double bias = 0.2;
    /** gramCount-グラムに分割、2 を推奨 */
    private final int gramCount = 2;
    /**
     * Listに入れられた学習データから学習する。
     * なお、正解データは「T\t文章」のように、
     * <PRE>
     * T	文章
     * <PRE>
     * の形式で記述すること
     * <HR>
     * (130406) 修正
     * @param list 学習データ文字列を入れたList
     */
    public BayesianClassifier(final List<String> list) {
        for (final Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
            learn(iterator.next());
        }
    }
    /**
     * 1行ずつ学習を行う
     * @param str
     */
    private void learn(final String str){
        final Set<String> appear = Sets.mutable.empty();
        final boolean isValidKind = "T".equals(str.split("\t")[0]);
        final String mat = str.split("\t")[1];
        for (int i = 0; i < (mat.length() - (gramCount - 1) ); ++i){
            final String bi = mat.substring(i, i + gramCount);
            if(appear.contains(bi)){
                continue;
            }
            appear.add(bi);
            if(!bicount.containsKey(bi)){
                bicount.put(bi, new int[gramCount]);
            }
            bicount.get(bi)[isValidKind ? 0 : 1]++;
        }
        if(isValidKind){
            //System.out.println(isValidKind + " : "+str);
            morningcount++;
        }
        totalcount++;
    }
    /**
     * 評価を行う.
     * @param  str 評価を行う文字列
     * @return morningProb > normalProb なら 1 、否なら -1
     */
    public final boolean trial(final String str) {
        double morningProb = 1;
        double normalProb  = 1;
        final Set<String> appear = Sets.mutable.empty();
        final int length = str.length() - 1;
        for (int i = 0; i < length; ++i){
            final String bi = str.substring(i, i + gramCount);
            if(appear.contains(bi)){
                continue;
            }
            appear.add(bi);
            double p1 = 0;
            double p2 = 0;
            if(bicount.containsKey(bi)){
                p1 = bicount.get(bi)[0];
                p2 = bicount.get(bi)[1];
            }
            p1 = (p1 + 0.5 * bias) / (morningcount + bias);
            p2 = (p2 + 0.5 * bias) / ((totalcount - morningcount) + bias);
            morningProb *= p1;
            normalProb  *= p2;
        }
        morningProb *= morningcount;
        normalProb  *= (totalcount - morningcount);
        return normalProb < morningProb ? true : false;
    }
    /**
     * 評価を行う
     * @param  str 評価を行う文字列
     * @return morningProb
     */
    public final double trialRetVal(final String str) {
        double morningProb = 1;
        //double normalProb = 1;
        final Set<String> appear = Sets.mutable.empty();
        final int length = str.length() - 1;
        for (int i = 0; i < length; ++i){
            final String bi = str.substring(i, i + 2);
            if(appear.contains(bi)){
                continue;
            }
            appear.add(bi);
            double p1 = 0;
            double p2 = 0;
            if(bicount.containsKey(bi)){
                p1 = bicount.get(bi)[0];
                p2 = bicount.get(bi)[1];
            }
            p1 = (p1 + 0.5 * bias) / (morningcount + bias);
            p2 = (p2 + 0.5 * bias) / ((totalcount - morningcount) + bias);
            morningProb *= p1;
            //normalProb  *= p2;
        }
        morningProb *= morningcount;
        //normalProb  *= (totalcount - morningcount);
        return morningProb;
    }
    /**
     * 文字列カーネル法方式、未実装
     * @param x1
     * @param x2
     * @return (n + 1) * (n + 1)
     * @deprecated
     */
    @Deprecated
    public static double kernel(final Map<String, Double> x1, final Map<String, Double> x2){
        double n = 0;
        for(final Map.Entry<String, Double> ent : x1.entrySet()){
            if(!x2.containsKey(ent.getKey())) continue;
            n += ent.getValue() * x2.get(ent.getKey());
        }
        return (n + 1) * (n + 1);
    }/*
    public static void main(String[] args) {
        BayesianClassifier bc = new BayesianClassifier(FileUtil.createStrListFromFile("teach.txt"));
        System.out.println(bc.trial("おはようございます"));
        System.out.println(bc.trial("おはつにお目にかかります"));
        System.out.println(bc.trial("おはっし"));
        System.out.println(bc.trial("こんばんわんこ"));
        System.out.println(bc.trial("イエッサー!"));
    }*/
}