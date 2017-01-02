package jp.toastkid.libs.utils;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;

import jp.toastkid.libs.comparator.NumberMapComparator;


/**
 * List や Set やMap 等を操作するメソッドを収録.
 * <HR>
 * (130831) get○○ → implode の対応<BR>
 * (111208) コメントを一部修正、getZeroList()メソッドをこのクラスに移動
 * (111109) ベクトル計算関連を VectorCalcUtil クラスに移動
 * @author 06ki044
 *
 */
public abstract class CollectionUtil {

    /** line separator. */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * Private constructor.
     */
    private CollectionUtil() {
        // NOP.
    }

    /**
     * String 型配列の中身を "\t" で指定した文字列で繋いで返す.
     * @param pStrArray 中身を取り出したい String 型配列
     * @return 文字列
     */
    public static String implode(final String[] pStrArray){
        return implode(pStrArray, "\t");
    }

    /**
     * String 型配列の中身を glue で指定した文字列で繋いで返す.
     * @param pieces 中身を取り出したい String 型配列
     * @param glue 要素区切り文字、 "," や "\t" などを指定する.
     * @return 文字列
     */
    public static String implode(final Object[] pieces, final String glue) {
        final StringBuilder buf = new StringBuilder(110);
        for (int i = 0; i < pieces.length; i++) {
            if (0 < i) {
                buf.append(glue);
            }
            buf.append(pieces[i].toString());
        }
        return buf.toString();
    }

    /**
     * String 配列から Set&lt;String&gt; を生成して返す.
     * @param pStrArray Set&lt;String&gt; に変換したい String 配列
     * @return pStrArray の 要素を入れた Set&lt;String&gt;
     */
    public static Set<String> arrayToSet(final String[] pStrArray){
        return Sets.mutable.of(pStrArray);
    }

    /**
     * pieces に格納された文字列を取り出して,
     * 改行記号(System.getProperty("line.separator")で取得)で連結して返却する
     * @param pieces
     * @return pSetに格納された文字列
     */
    public static String implode(final Iterable<?> pieces){
        return implode(
                pieces,
                LINE_SEPARATOR
        );
    }
    /**
     * pSet に格納された文字列を取り出して、 dataSeparator で指定したデータ区切り文字で連結して返却する.
     * <HR>
     * (111231) 区切り文字の置き場所を変更
     * @param pieces
     * @param glue
     * @return pSetに格納された文字列
     */
    public static String implode(
            final Iterable<?> pieces,
            final String glue
            ){
        if (pieces == null) {
            return "";
        }
        final StringBuilder resBuf = new StringBuilder(100);
        final Iterator<?> iter = pieces.iterator();
        while(iter.hasNext()){
            resBuf.append(resBuf.length() != 0 ? glue : "");
            resBuf.append(iter.next().toString());
        }
        return resBuf.toString();
    }
      /**
       * 渡された Map&lt;String,Integer> の中身を取りだす
       * @param pTMap
       * @return pTMapの中身
       */
      public static String takeTMap_SI(final Map<String,Integer> pTMap){
          final String lineS = System.getProperty("line.separator");
          final StringBuilder takeBuilder = new StringBuilder();
          final TreeMap<String,Integer> tempMap =
              new TreeMap<>(new NumberMapComparator(pTMap));
            // TreeMap に全部の組をコピー(このときにソートされる)
          tempMap.putAll(pTMap);
            // TreeMap の表示
            final Set<String> termSet = tempMap.keySet();  // ソートされている
            final Iterator<String> iterator = termSet.iterator();
            while(iterator.hasNext()) {
                final String key = iterator.next();
                final Integer value = tempMap.get(key);
                takeBuilder.append(key + ": " + value + lineS);
            }
            return takeBuilder.toString();
      }

      /**
       * 2グラムマップを生成し返す
       * @param str
       */
      public static  Map<String, int[]> makeBiGramMap(final String str){
            /** gramCount-グラムに分割 */
            final int gramCount = 2;

            final Map<String, int[]> resMap = new HashMap<>();
            final Set<String> appear = Sets.mutable.empty();
            //boolean isValidKind = kindOfClassify.equals(str.split("\t")[1]);

            //String mat = str.split("\t")[2];
            //System.out.println(str);
            for(int i = 0; i < (str.length() - (gramCount - 1) ); ++i){
                final String bi = str.substring(i, i + gramCount);
                if(appear.contains(bi)){
                    continue;
                }
                appear.add(bi);
                if(!resMap.containsKey(bi)){
                    resMap.put(bi, new int[1]);
                }
                //resMap.get(bi)[isValidKind ? 0 : 1]++;
            }
            return resMap;
      }

      /**
       * String 型配列の要素の一致度を計測する
       * @param arrayA String 型配列１
       * @param arrayB String 型配列２
       * @return 要素の一致度( double 値)
       */
      public static double calcStrArraySimilarity(
              final String[] arrayA,
              final String[] arrayB
              ){
          return calcStrArrayListSimilarity(Lists.mutable.of(arrayA), Lists.mutable.of(arrayB));
      }

      /**
       * String 型 ArrayList の要素の一致度を計測する
       * @param strAList1 String型ArrayList１
       * @param strAList2 String型ArrayList２
       * @return 要素の一致度( double 値)
       */
      public static double calcStrArrayListSimilarity(
              final List<String> strAList1,
              final List<String> strAList2
              ){
          int containsCount = 0;
          List<String> biggerList;
          List<String> smallerList;
          if(strAList1.size() < strAList2.size()){
              biggerList = strAList2;
              smallerList = strAList1;
          } else if(strAList2.size() < strAList1.size()){
              biggerList = strAList1;
              smallerList = strAList2;
          } else {
              biggerList = strAList1;
              smallerList = strAList2;
          }

          for(int i = 0; i < biggerList.size(); i++){
              if(smallerList.contains(biggerList.get(i))){
                  containsCount++;
              }
          }

          return (double)containsCount / (double)smallerList.size();
      }
      /**
       * String 型 Set の要素の一致度を計測する
       * @param strASet1 String 型 Set１
       * @param strASet2 String 型 Set２
       * @return 要素の一致度( double 値)
       * (110517)作成
       */
      public static double calcStrSetSimilarity(
              final Set<String> strASet1,
              final Set<String> strASet2
              ){
          int containsCount = 0;
          final Iterator<String> iter = strASet2.iterator();
          while(iter.hasNext()){
              if(strASet1.contains(iter.next())){
                  containsCount++;
              }
          }
          return (double)containsCount / (double)strASet1.size();
      }
      /**
       * Map&lt;String,Double> の最大値をもつ Key と、その最大値をタブ(\t)つなぎの String で返却する.<BR>
       * @param pMap 最大値を取り出したい Map
       * @return 最大値をもつ Key と、その最大値をタブ(\t)つなぎの String
       */
      public static String getKeyWithMaxValue(final Map<String,Double> pMap){
          final Set<String> tempSet = pMap.keySet();
          final Iterator<String> iter = tempSet.iterator();
          double max = Integer.MIN_VALUE;
          String maxValsKey = "";
          String temp = "";
          while(iter.hasNext()){
              temp = iter.next();
              if(pMap.get(temp) > max){
                  max = pMap.get(temp);
                  maxValsKey = temp;
              }
          }
          return maxValsKey + "\t" + max;
      }
    /**
     * Map&lt;String , Set&lt;String>> を　Map&lt;String,String> の形にして返す
     * @param pMap Map<String , Set<String>>
     * @param glue データ区切り文字
     * @return Map<String,String>
     */
    public static Map<String,String> takeFromSetMap(
            final Map<String , Set<String>> pMap,
            final String glue
            ) {
        final Map<String,String> resMap = new HashMap<>(100);
        final Iterator<String> iter = pMap.keySet().iterator();
        String key;
        while(iter.hasNext()){
            key = iter.next();
            resMap.put(key, implode(pMap.get(key), glue));
        }
        return resMap;
    }
    /**
     * Map&lt;String , List&lt;String>> を Map&lt;String,String> の形に変換して返す
     * @param pMap
     * @param dataSeparator データ区切り文字
     * @return  Map&lt;String,String>
     */
    public static Map<String,String> takeFromListMap(
            final Map<String , List<String>> pMap,
            final String dataSeparator
            ){
        final Map<String,String> resMap = new HashMap<>(100);
        final Set<String> kSet = pMap.keySet();
        final Iterator<String> iter = kSet.iterator();
        List<String> pMapValSet;
        String temp;
        while(iter.hasNext()){
            temp = iter.next();
            pMapValSet = pMap.get(temp);
            resMap.put(
                    temp ,
                    implode(pMapValSet).toString().replaceAll(LINE_SEPARATOR, dataSeparator)
                    );
        }
        return resMap;
    }
    /**
     * 二つの Set&lt;String> の差を取得し、結果を "a-b.txt" に出力する.
     * @param aSet
     * @param bSet
     */
    public static void getDifferenceFromTwoMap(
            final Set<String> aSet,
            final Set<String> bSet
            ){
        CollectionUtil.getDifferenceFromTwoMap(aSet, bSet, "a-b.txt");
    }
    /**
     * 二つの Set&lt;String> の差を取得し、結果を outputFilePath で指定した場所に出力する.
     * @param aSet
     * @param bSet
     * @param outputFilePath
     */
    public static void getDifferenceFromTwoMap(
            Set<String> aSet,
            Set<String> bSet,
            final String outputFilePath
            ){
        Set<String> resSet = Sets.mutable.empty();
        if(aSet.size() < bSet.size()){
            resSet = aSet;
            aSet   = bSet;
            bSet   = resSet;
            resSet.clear();
        }
        String str = "";
        final Iterator<String> iter = aSet.iterator();
        while(iter.hasNext()){
            str = iter.next();
            if(!bSet.contains(str)){
                resSet.add(str);
            }
        }
        FileUtil.outPutSet(resSet, outputFilePath);
    }
    /**
     * Map&lt;String,Set&lt;String>> から value の Set&lt;String> の内容をすべて取り出した Set&lt;String> を構成して返す.
     * @param targetFilePath
     * @param targetFileEncode
     * @param dataSeparator
     * @return  value の Set&lt;String> の内容をすべて取り出した Set&lt;String>
     */
    public static Set<String> getContainsSetFromDataFile(
            final String targetFilePath,
            final String targetFileEncode,
            final String dataSeparator
            ){
        final Map<String,Set<String>> catMap = FileUtil.getSetMapFromFile(
                targetFilePath,
                targetFileEncode,
                dataSeparator
                );
        final Iterator<String> iter = catMap.keySet().iterator();
        String tempKey;
        final Set<String> catSet = new TreeSet<>();
        while(iter.hasNext()){
            tempKey = iter.next();
            //System.out.println(tempKey + " : " + catMap.get(tempKey).size());
            catSet.addAll(catMap.get(tempKey));
        }
        return catSet;
    }
    /**
     * List&lt;List&lt;String>> の中身を List&lt;String> の形にして返却する.
     * @param pList
     * @return List&lt;String>
     */
    public static List<String> takeStrListFromList_List(final List<List<String>> pList){
        final List<String> resList = new ArrayList<>(pList.size());
        for (int i = 0; i < pList.size(); i++) {
            resList.add(implode(pList.get(i)));
        }
        return resList;
    }
    /**
     * Set&lt;List&lt;String>> の中身を Set&lt;String> の形にして返却する.<BR>
     * 各要素は"\t"で連結する.
     * @param pList
     * @return List&lt;String>
     */
    public static Set<String> takeStrSetFromListSet(final Set<List<String>> pList){
        return takeStrSetFromListSet(pList,"\t");
    }
    /**
     * Set&lt;List&lt;String>> の中身を Set&lt;String> の形にして返却する.<BR>
     * 各要素は dataSeparator で連結する.
     * @param pList
     * @return List<String>
     */
    public static Set<String> takeStrSetFromListSet(
            final Set<List<String>> pList,
            final String dataSeparator
            ){
        final Set<String> resList = Sets.mutable.empty();
        final Iterator<List<String>> iter = pList.iterator();
        while (iter.hasNext()) {
            resList.add(CollectionUtil.implode(iter.next(), dataSeparator));
        }
        return resList;
    }
    /**
     * Integer を値とする Map の中身を取り出す.<BR>
     * キーと値の二項関係を dataSeparator で繋ぎ、関係間を StringUtil.lineSeparator で繋いだ文字列で返す.
     * @param pMap
     * @param dataSeparator
     * @return キーと値の二項関係を dataSeparator で繋ぎ、関係間を StringUtil.lineSeparator で繋いだ文字列
     */
    public static String takeFromIntMap(
            final Map<String,Integer> pMap,
            final String dataSeparator
            ){
        String gotKey = "";
        Iterator<String> innerIter;
        final StringBuffer tempBuf = new StringBuffer();
        innerIter = pMap.keySet().iterator();
        while (innerIter.hasNext()) {
            gotKey = innerIter.next();
            tempBuf.append(gotKey + dataSeparator + pMap.get(gotKey).toString() + LINE_SEPARATOR);
        }
        return tempBuf.toString();
    }
    /**
     * Map&lt;String, List&lt;Integer&gt;&gt; を Map&lt;String, String&gt;に変換する.
     * @param pathLengthMap
     * @return  Map&lt;String, String&gt;
     */
    public static Map<String, String> takeFromMap_STR_INTLST(
            final Map<String, List<Integer>> pathLengthMap
            ) {
        final Map<String,String> resMap = new HashMap<>(pathLengthMap.size());
        final Iterator<String> iter = pathLengthMap.keySet().iterator();
        while(iter.hasNext()){
            final String key = iter.next();
            resMap.put(key, implode(pathLengthMap.get(key)));
        }
        return resMap;
    }
    /**
     * 空の Set&lt;List&lt;String&gt;&gt; に要素を入れて返す.
     * @param target 最初に入れておきたい要素
     * @param defaultSize 初期容量、10000 ほどを設定しておくとよい
     * @return Set&lt;List&lt;String&gt;&gt;
     */
    public static Set<List<String>> getAddStringListSet(
            final String target,
            final int defaultSize
            ) {
        final Set<List<String>> resLList = Sets.mutable.empty();

        List<String> first = new ArrayList<>(10);
        first.add(target);

        resLList.add(first);
        first = null;
        return resLList;
    }
    /**
     * 空の ListSet に要素を入れて返す.初期容量は 10000 とする.
     * @param target 最初に入れておきたい要素
     * @return 要素を入れた Set&lt;List&lt;String&gt;&gt;
     */
    public static Set<List<String>> getAddStringListSet(final String target){
        return getAddStringListSet(
                target,
                10000
                );
    }
    /**
     * 指定したサイズの分だけ 0 を入れた List&lt;String> を返す.
     * (111208) ColleUtil クラスに移動
     * @param size List のサイズ
     * @return 指定したサイズの分だけ 0 を入れた List&lt;String>
     */
    public static List<String> getZeroList(final int size){
        final ArrayList<String> resList = new ArrayList<>(size);
        for(int i = 0; i < size; i++){
            resList.add("0");
        }
        return resList;
    }
    /**
     * Map を要素とする Map から文字列を取り出して StringBuffer で返す.
     * <HR>
     * (120219) 作成
     * @param filesTMap マップを要素とするマップ
     * @param dataSeparator データ区切り文字(120217 追加)
     * @return Map を要素とする Map から文字列を取り出した StringBuffer
     */
    public static StringBuffer takeMappedMap(
            final Map<String, Map<String, String>> filesTMap,
            final String filePath,
            final String encode,
            final String dataSeparator
            ){
        PrintWriter writer = null;
        boolean isOver = false;
        if(100000 < filesTMap.size() ){
            writer = FileUtil.makeFileWriter(filePath, encode);
            isOver = true;
        }
        final StringBuffer takeBuffer = new StringBuffer();
        Iterator<String> iterator;
        String nowFileAbsolutePath = "";
        final String localLinesep        = System.getProperty("line.separator");
        TreeMap<String, String> tempMap = null;
        //全ファイルにおける単語セット
        final Set<String> termSet = Sets.mutable.empty();

        iterator = filesTMap.keySet().iterator();
        while(iterator.hasNext()){
            nowFileAbsolutePath = iterator.next().toString();
            // filesTMapに含まれるWordsTMapのキーを記録する
            //System.out.println(filesTMap.get(nowFileAbsolutePath));
            try{
                termSet.addAll(filesTMap.get(nowFileAbsolutePath).keySet());  // ソートされている
            } catch(final NullPointerException np){
                np.printStackTrace();
            }
        }

        // 1行目の作成
        Iterator<String> setIter = termSet.iterator();
        if(isOver){
            takeBuffer.append("ja");
            takeBuffer.append(dataSeparator);
            takeBuffer.append(CollectionUtil.implode(termSet, dataSeparator));
            writer.println(takeBuffer.toString());
            takeBuffer.delete(0, takeBuffer.length());
        }else{
            takeBuffer.append(dataSeparator);
            takeBuffer.append(CollectionUtil.implode(termSet, dataSeparator));
            takeBuffer.append(localLinesep);
        }

        //次にMapを構成する

        final StringBuffer footer = new StringBuffer(200);

        //再利用
        iterator = filesTMap.keySet().iterator();
        while(iterator.hasNext()){
            nowFileAbsolutePath = iterator.next().toString();

            tempMap = new TreeMap<>(filesTMap.get(nowFileAbsolutePath));
            //System.out.println(nowFileAbsolutePath + " : " + filesTMap.size()/*+ tempMap */);
            if(isOver){
                writer.print(nowFileAbsolutePath);
            } else{
                takeBuffer.append(nowFileAbsolutePath);
            }

            setIter = termSet.iterator();
            while(setIter.hasNext()) {
                final String key   = setIter.next().toString();
                String value = "";
                if(tempMap.containsKey(key)){
                    value = tempMap.get(key).toString();
                }
                if(isOver){
                    writer.print(dataSeparator + value);
                } else{
                    takeBuffer.append(dataSeparator + value);
                }
            }
            if(iterator.hasNext()){
                if(isOver){
                    writer.println("");
                } else{
                    takeBuffer.append(localLinesep);
                }
            }
        }
        if(isOver){
            writer.println(footer.toString());
            writer.close();
            takeBuffer.delete(0, takeBuffer.length());
        } else{
            takeBuffer.append(footer.toString());
            takeBuffer.append(localLinesep);
        }
        return takeBuffer;
    }
    /**
     * JavaScript で使うデータ形式に変換して返す.
     * @param map
     * @return
     */
    public static String convertJsData(
            final Map<?, ?> map,
            final String kLabel,
            final String vLabel
            ) {
        if (map.isEmpty()) {
            return "[]";
        }
        final StringBuilder dataStr = new StringBuilder(5000);
        dataStr.append("[");
        Sets.immutable.ofAll(map.keySet()).each(key -> {
            if (1 < dataStr.length()) {
                dataStr.append(", ");
            }
            dataStr.append("{\"").append(kLabel).append("\"")
                .append(": ").append(Strings.doubleQuote(key.toString())).append(",")
                .append("\"").append(vLabel).append("\": ")
                .append(Strings.doubleQuote(map.get(key).toString()))
                .append("}");
        });
        dataStr.append("]");
        return dataStr.toString();

    }
    /**
     * JavaScript で使うデータ形式に変換して返す.
     * @param map Map
     * @return JavaScript で使うデータ形式
     */
    public static String convertJsData(final Map<?, ?> map) {
        return convertJsData(map, "key", "value");
    }
}
