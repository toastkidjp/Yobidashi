
package jp.toastkid.libs.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.primitive.IntLists;

import jp.toastkid.libs.comparator.NumberMapComparator;

/**
 * ファイル操作に関するユーティリティクラス<BR>
 *
 * ファイルからデータを読みこんでコレクションを生成するメソッドもここに収録しておく<BR>
 * <HR>
 * (130831) get○○ → implode の対応<BR>
 * (130707) StringBuffer を使う部分をすべてStringBuilderに変更<BR>
 * @author Toast kid
 */
public final class FileUtil {

    /** ファイルプロトコル. */
    public static final String FILE_PROTOCOL = "file:///";

    /** System out. */
    public static final PrintStream SYSOUT = new PrintStream(
            new BufferedOutputStream(new FileOutputStream(FileDescriptor.out), 128), true);

    /** System err. */
    public static final PrintStream SYSERR = new PrintStream(
            new BufferedOutputStream(new FileOutputStream(FileDescriptor.err), 128), true);

    /** 既知の画像ファイル拡張子. */
    private static final String[] IMAGE_FILE_IDENTIFIERS = new String[] {
        ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".ico", "svg"
    };

    /**
     * 出力先pOutPutFileNameと文字コードutf-8で初期化したPrintWriterを生成して返す
     * @param pOutPutFileName 出力先
     * @return resWriter
     */
    public static PrintWriter getFileWriter(final String pOutPutFileName) {
        return makeFileWriter(pOutPutFileName, "utf-8");
    }

    /**
     * 出力先pOutPutFileNameと文字コードpEncodeで初期化したPrintWriterを生成して返す
     * <HR>
     *
     * 雑なコードだけど、大本なので絶対に修正しないように
     *
     * @param pOutPutFileName 出力先
     * @param pEncode 扱う文字コード
     * @return resWriter
     */
    public static PrintWriter makeFileWriter(
            final String pOutPutFileName,
            final String pEncode
            ) {
        try {
            return new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(
                        new FileOutputStream(pOutPutFileName),
                        pEncode
                    )
                )
            );
        } catch(final IOException iOE) {
            iOE.printStackTrace();
        }
        return null;
    }
    /**
     * バッファ処理式ファイルリーダを渡されたファイル名と文字コードutf-8で初期化する.<BR>
     * @param pTargetFileName リーダで読み込むファイルの名前
     * @return fileReader : 与えたパラメータで初期化したファイルリーダ
     */
    public static BufferedReader makeFileReader(final String pTargetFileName) {
           return makeFileReader(pTargetFileName, "utf-8");
    }
    /**
     * バッファ処理式ファイルリーダを渡されたファイル名と文字コードで初期化する.
     * @param pTargetFileName リーダで読み込むファイルの名前
     * @param pEncode 初期化する文字コード
     * @return fileReader : 与えたパラメータで初期化したファイルリーダ
     */
    public static BufferedReader makeFileReader(
            final String pTargetFileName,
            final String pEncode
            ) {
        try {
            return makeInputStreamReader(new FileInputStream(pTargetFileName), pEncode);
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * バッファ処理式ファイルリーダを渡されたファイル名と文字コードで初期化する.
     * @param pFile リーダで読み込むファイルの名前
     * @param pEncode 初期化する文字コード
     * @return fileReader : 与えたパラメータで初期化したファイルリーダ
     */
    public static BufferedReader makeFileReader(
            final File pFile,
            final String pEncode
            ) {
        try {
            return makeInputStreamReader(new FileInputStream(pFile), pEncode);
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * InputStream から BufferedReader を構築して返す.
     * <HR>
     * (130706) 作成<BR>
     * @param pStream
     * @param pEncode
     * @return BufferedReader
     * @throws UnsupportedEncodingException
     */
    private static BufferedReader makeInputStreamReader(
            final InputStream pStream,
            final String pEncode
            ) {
        try {
            return new BufferedReader(new InputStreamReader(pStream, pEncode));
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 指定されたファイルの要素を一行ずつ入れた Set<String> を返す.
     * @param pFilePath 指定するファイルのパス
     * @return 指定されたファイルの要素を一行ずつ入れた Set<String>
     */
    public static Set<String> createStrSetFromFile( final String pFilePath ) {
        try {
            return Sets.mutable.withAll(Files.readAllLines(Paths.get(new File(pFilePath).toURI())));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return Sets.mutable.empty();
    }
    /**
     * 指定されたファイルの要素を一行ずつ入れた List<String> を返す.<BR>
     * 文字コードは "shift-jis" を使う.
     * @param pFilePath 指定するファイルのパス
     * @return 指定されたファイルの要素を一行ずつ入れた Set<String>
     */
    public static List<String> createStrListFromFile(final String pFilePath) {
        return readLines(pFilePath, "shift-jis");
    }
    /**
     * 指定されたファイルの要素を一行ずつ入れた List<String> を返す.
     * <HR>
     * (130406) 修正<BR>
     * @param pFilePath 指定するファイルのパス
     * @param pEncode 指定するファイルの文字コード
     * @return 指定されたファイルの要素を一行ずつ入れた Set<String>
     */
    public static List<String> readLines(
            final String pFilePath,
            final String pEncode
            ) {
        return readLines(new File(pFilePath), pEncode);
    }
    /**
     * 指定されたファイルの要素を一行ずつ入れた List<String> を返す.
     * <HR>
     * (130406) 修正<BR>
     * @param pFile 読み込むファイル
     * @param pEncode 指定したファイルの文字コード
     * @return 指定されたファイルの要素を一行ずつ入れた Set<String>
     */
    public static List<String> readLines(
            final File pFile,
            final String pEncode
            ) {
        final List<String> resSet = new ArrayList<String>(100);
        try (final BufferedReader fileReader = FileUtil.makeFileReader(pFile, pEncode);) {
            String str = fileReader.readLine();
            while(str != null) {
                resSet.add(str);
                str = fileReader.readLine();
            }
            fileReader.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return resSet;
    }
    /**
     * 指定されたファイルの要素を一行ずつ入れた List<String> を返す.
     * <HR>
     * (130406) 修正<BR>
     * @param filePath 指定するファイルのパス
     * @param pEncode 指定するファイルの文字コード
     * @return 指定されたファイルの要素を一行ずつ入れた Set<String>
     */
    public static List<String> readLinesFromStream(
            final String filePath,
            final String pEncode
            ) {
        final List<String> lines = new ArrayList<String>(5000);
        try (final BufferedReader fileReader = makeReader(filePath, pEncode)) {

            if (fileReader == null) {
                return lines;
            }

            String str = fileReader.readLine();
            while(str != null) {
                lines.add(str);
                str = fileReader.readLine();
            }
            fileReader.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private static BufferedReader makeReader(final String filePath, final String pEncode) {
        final BufferedReader fileReader;
        final InputStream in = FileUtil.getStream(filePath);
        if (in != null) {
            fileReader = FileUtil.makeInputStreamReader(in, pEncode);
        } else {
            fileReader = FileUtil.makeFileReader(filePath, pEncode);
        }
        return fileReader;
    }
    /**
     * 引数として渡したファイル(String，String)に記述されているデータをMap<String，String>に格納して返す<BR>
     * 要素は一行につき一つ記録しておくこと<BR>
     * ファイルのエンコーディングはShift-jisにするように<BR>
     * @param pTablePath : リストの要素を記録したファイル名
     * @param pSeparator : データの区切り文字
     * @param pEncode    ： ファイルのエンコーディング
     * @return 作成したMap
     */
    private static Map<String, Integer> createIntMapFromFileNative(
            final String pTablePath,
            final String pSeparator,
            final String pEncode
            ) {
        final Map<String, Integer> resMap = new HashMap<String, Integer>();
        try (final BufferedReader fileReader = makeFileReader(pTablePath, pEncode);) {
            String str = new String();
            String[] temp;
            str = fileReader.readLine();

            while(str != null) {
                temp = str.split(pSeparator,2);
                // 要素数が足りない場合はMapにputしない、ArrayIndexOutOfBoundsException回避用の処理.
                if (1 < temp.length) {
                    resMap.put(temp[0],Integer.parseInt(temp[1]));
                }
                str = fileReader.readLine();
            }
            fileReader.close();
        } catch(final IOException iOE) {
            iOE.printStackTrace();
        }
        return resMap;
    }
    /**
     * 引数として渡したファイル(String，String)に記述されているデータを
     * Map<String，String>に格納して返す.<BR>
     * データの区切り文字は第二引数で指定する<BR>
     * 要素は一行につき一つ記録しておくこと<BR>
     * ファイルのエンコーディングは pEncode で指定したものを使う<BR>
     * @param pTablePath : リストの要素を記録したファイル名
     * @param pTabledelimiter ファイルのデータ区切り文字
     * @param pEncode ファイルの文字コード
     * @return 作成したMap
     */
    public static Map<String, Integer> createIntMapFromFile(
            final String pTablePath,
            final String pTabledelimiter,
            final String pEncode
            ) {
        return createIntMapFromFileNative(
                pTablePath,
                pTabledelimiter ,
                pEncode
                );
    }
    /**
     * 引数として渡したファイル(String，String)に記述されているデータをMap<String，String>に格納して返す<BR>
     * データの区切り文字は第二引数で指定する<BR>
     * 要素は一行につき一つ記録しておくこと<BR>
     * ファイルのエンコーディングはShift-jisにするように<BR>
     * @param pTablePath : リストの要素を記録したファイル名
     * @param pTabledelimiter ファイルのデータ区切り文字
     * @return 作成したMap
     */
    public static Map<String, Integer> createIntMapFromFile(
            final String pTablePath,
            final String pTabledelimiter
            ) {
        return createIntMapFromFileNative(pTablePath, pTabledelimiter , "shift-jis");
    }
    /**
     *
     * 引数として渡したファイル(String，String)に記述されているデータをMap<String，String>に格納して返す<BR>
     * データの区切り文字は拡張子から自動判別する<BR>
     * 要素は一行につき一つ記録しておくこと<BR>
     * ファイルのエンコーディングはShift-jisにするように<BR>
     * @param pTablePath : リストの要素を記録したファイル名
     * @return 作成したMap
     */
    public static Map<String, Integer> createIntMapFromFile(final String pTablePath) {
        return createIntMapFromFileNative(
                pTablePath,
                getDelimiterForFile(pTablePath),
                "shift-jis"
                );
    }
    /**
     * ファイルの拡張子からデータ区切り文字を判断し返す.
     * ファイル拡張子を参照できない場合は正常に働かないので注意する.
     * @param pFileName ファイル名(拡張子を含むこと)
     * @return ".tsv" なら "\t"、".csv" なら "," 、 それ以外は ” ”
     */
    public static String getDelimiterForFile(final String pFileName) {
        if (pFileName.toLowerCase().endsWith(".tsv") ) {
            return "\t";
        } else if (pFileName.toLowerCase().endsWith(".csv") ) {
            return ",";
        } else{
            return " ";
        }
    }
    /**
     * 引数として渡したファイル(String，String)に記述されているデータをMap<String，String>に格納して返す<BR>
     * 要素は一行につき一つ記録しておくこと<BR>
     * ファイルのエンコーディングはShift-jisにするように<BR>
     * @param pTablePath : リストの要素を記録したファイル名
     * @param pSeparator : データの区切り文字
     * @param pEncode    ： ファイルのエンコーディング
     * @return 作成したMap
     */
    private static Map<String, String> createMapFromFileNative(
            final String pTablePath,
            final String pSeparator,
            final String pEncode
            ) {
        final Map<String, String> resMap = new HashMap<String, String>();
        try{
            final BufferedReader fileReader
            = makeFileReader(
                    pTablePath,
                    pEncode
                    );
            String str = new String();
            String[] temp;
            str = fileReader.readLine();

            while(str != null) {
                temp = str.split(pSeparator,2);
                if (1 < temp.length) {// 要素数が足りない場合はMapにputしない、ArrayIndexOutOfBoundsException回避用の処理
                    resMap.put(temp[0],temp[1]);
                }
                str = fileReader.readLine();
            }
            fileReader.close();
        }catch(final IOException iOE) {
            iOE.printStackTrace();
        }
        return resMap;
    }
    /**
     * 引数として渡したファイルに記述されているデータを入れたMap<String，String>を返す.<BR>
     * データの区切り文字は第3引数のものを使う.<BR>
     * 要素は一行につき一つ記録しておくこと<BR>
     * ファイルのエンコーディングは引数pEncodeで指定したものを用いる<BR>
     * <HR>
     * (130630) 作成<BR>
     * @param pTablePath : リストの要素を記録したファイル名
     * @param pEncode
     * @param pSeparator
     * @return 作成したMap
     */
    public static Map<String, String> createMapFromFile(
            final String pTablePath,
            final String pEncode,
            final String pSeparator
            ) {
        return createMapFromFileNative(pTablePath, pSeparator , pEncode);
    }
    /**
     * 引数として渡したファイルに記述されているデータを入れたMap<String，String>を返す<BR>
     * データの区切り文字はファイルの拡張子から自動判別する<BR>
     * 要素は一行につき一つ記録しておくこと<BR>
     * ファイルのエンコーディングは引数pEncodeで指定したものを用いる<BR>
     * @param pTablePath : リストの要素を記録したファイル名
     * @return 作成したMap
     */
    public static Map<String, String> createMapFromFile(
            final String pTablePath,
            final String pEncode
            ) {
        if (pTablePath.toLowerCase().endsWith(".tsv") ) {
            return createMapFromFileNative(pTablePath, "\t" , pEncode);
        } else if (pTablePath.toLowerCase().endsWith(".csv") ) {
            return createMapFromFileNative(pTablePath, ","  , pEncode);
        } else{
            return createMapFromFileNative(pTablePath, " "  , pEncode);
        }
    }
    /**
     * 引数として渡したファイルに記述されているデータを入れたMap<String，String>を返す<BR>
     * データの区切り文字はファイルの拡張子から自動判別する<BR>
     * 要素は一行につき一つ記録しておくこと<BR>
     * ファイルのエンコーディングはShift-jisにするように<BR>
     * @param pTablePath : リストの要素を記録したファイル名
     * @return 作成したMap
     */
    public static Map<String, String> createMapFromFile(final String pTablePath) {
        if (pTablePath.toLowerCase().endsWith(".tsv") ) {
            return createMapFromFileNative(pTablePath, "\t" , "shift-jis");
        } else if (pTablePath.toLowerCase().endsWith(".csv") ) {
            return createMapFromFileNative(pTablePath, ","  , "shift-jis");
        } else{
            return createMapFromFileNative(pTablePath, " "  , "shift-jis");
        }
    }

    /**
     * 文字コードを変換して出力する
     * @param pTargetFileName 元のファイル名
     * @param pOutPutFileName 変換結果の出力ファイル名
     * @param pCurrentEncode 現在の文字コード
     * @param pNewEncode 新しい文字コード
     */
    public static void textFileEncoder(
            final String pTargetFileName,
            final String pOutPutFileName,
            final String pCurrentEncode,
            final String pNewEncode
            ) {
        String str = "";
        try (final PrintWriter nameWriter = makeFileWriter(pOutPutFileName, pNewEncode);
             final BufferedReader fileReader = makeFileReader(pTargetFileName, pCurrentEncode);
                ) {
            str = fileReader.readLine();
            while(str != null) {
                nameWriter.println(str);
                str = fileReader.readLine();
            }
            fileReader.close();
            nameWriter.close();
        } catch(final IOException iOE) {
            iOE.printStackTrace();
        }

    }
    /**
     * filesListに格納されたファイル名を持つファイルを結合する.<BR>
     * 出力ファイルの文字コードは"utf-8"を使う.
     * @param filesList 統合するファイルの名前を入れたリスト
     * @param pOutPutFileName 出力ファイル名
     */
    public static void mergeTextFiles(
            final List<String> filesList,
            final String pOutPutFileName
            ) {
        mergeTextFiles(filesList,pOutPutFileName,"utf-8");
    }
    /**
     * filesListに格納されたファイル名を持つファイルを結合する.<BR>
     * 読み込み対象のファイルと、出力したいファイルの文字コードを合わせておくこと
     *
     * 110719 修正
     *
     * @param filesList 統合するファイルの名前を入れたリスト
     * @param pOutPutFileName 出力ファイル名
     * @param outputFileEncode 出力ファイルの文字コード
     */
    public static void mergeTextFiles(
            final List<String> filesList,
            final String pOutPutFileName,
            final String outputFileEncode
            ) {
        mergeTextFiles(
                filesList,
                pOutPutFileName,
                outputFileEncode,
                false
                );
    }
    /**
     * filesListに格納されたファイル名を持つファイルを結合する.<BR>
     * 引数 isPrintFileName を true にすれば、ファイル名を記録する.<BR>
     * 読み込み対象のファイルと、出力したいファイルの文字コードを合わせておくこと
     *
     * 110719 作成
     *
     * @param filesList 統合するファイルの名前を入れたリスト
     * @param pOutPutFileName 出力ファイル名
     * @param outputFileEncode 出力ファイルの文字コード
     */
    public static void mergeTextFiles(
            final List<String> filesList,
            final String pOutPutFileName,
            final String outputFileEncode,
            final boolean isPrintFileName
            ) {
        String str = "";
        try (final PrintWriter nameWriter = makeFileWriter(pOutPutFileName , outputFileEncode);) {
            for(int i = 0; i < filesList.size(); i++) {
                final String processingFileName = filesList.get(i);
                if (new File(processingFileName).exists()) {
                    if (isPrintFileName) {
                        nameWriter.println(processingFileName);
                    }
                    try (final BufferedReader fileReader
                            = makeFileReader(processingFileName, outputFileEncode);) {
                        str = fileReader.readLine();
                        while(str != null) {
                            nameWriter.println(str);
                            str = fileReader.readLine();
                        }
                        fileReader.close();
                    }
                } else {
                    System.err.println("ファイル " + processingFileName + " は存在しません.");
                }
            }
            nameWriter.close();
        } catch(final IOException iOE) {
            iOE.printStackTrace();
        }
    }

    /**
     * 渡された String を<BR>
     * ファイル名 : 引数 pOutputName で指定したもの<BR>
     * 文字コード : 引数 pEncode で指定したもの<BR>
     * で出力する
     * <HR>
     * (120819) 作成
     * @param pStr ファイルに出力する内容を入れた String
     * @param pOutputName 出力ファイル名
     * @param pEncode 出力文字コード
     */
    public static void outPutStr(
            final String pStr,
            final String pOutputName,
            final String pEncode
            ) {
        try (final PrintWriter nameWriter = makeFileWriter(pOutputName,pEncode);) {
            nameWriter.print(pStr);
            nameWriter.close();
        }
    }
    /**
     * 渡された String の内容を ファイル名 : 引数 pOutputName で指定したもの、文字コード : "shift-jis"<BR>
     * で出力する
     * @param pStr ファイルに出力する内容
     * @param pOutputName 出力ファイル名
     */
    public static void outPutStr(
            final String pStr,
            final String pOutputName
            ) {
        outPutStr(pStr,pOutputName,"shift-jis");
    }
    /**
     * 渡された String の内容をファイル名 : "NoName.txt"、文字コード : "shift-jis" で出力する.
     * @param pStr ファイルに出力する内容を入れた String
     */
    public static void outPutStr(final String pStr) {
        outPutStr(pStr,"NoName.txt","shift-jis");
    }
    /**
     * Map<String, Integer>から内容を取り出し、結果をpOutputNameで指定したファイル名で、"utf-8"で出力する
     * @param passedMap
     * @param pOutputName 出力するファイル名
     */
    public static void outPutMap_S_I(
            final Map<String, Integer> passedMap,
            final String pOutputName) {
        outPutMap_S_I(passedMap,pOutputName,"utf-8");
    }
    /**
     *  Map<String, Integer>から内容を取り出し、結果をpOutputNameで指定したファイル名で出力する
     * @param passedMap
     * @param pOutputName 出力するファイル名
     * @param pEncode
     */
    public static void outPutMap_S_I(
            final Map<String, Integer> passedMap,
            final String pOutputName,
            final String pEncode
            ) {
        final String delimiter = ",";
        final TreeMap<String, Integer> tempMap = getSortedMapWithIntValue(passedMap);
        // TreeMap の表示
        final Set<String> termSet = tempMap.keySet();  // ソートされている
        final Iterator<String> iterator = termSet.iterator();
        final PrintWriter nameWriter = makeFileWriter(pOutputName,pEncode);
        while(iterator.hasNext()) {
            final String key = iterator.next();
            final Integer value = tempMap.get(key);
            nameWriter.println(key + delimiter + value);
        }
        nameWriter.close();
        return ;
    }
    /**
     * マップの値を基準としてキーをソートし直し、そのマップを返す.
     * <HR>
     * 111102 作成
     * @param passedMap 値を基準にキーを並び替えたい Map
     * @return 整列後の Map
     * @see NumberMapComparator
     */
    public static TreeMap<String, Integer> getSortedMapWithIntValue(final Map<String, Integer> passedMap) {
        final TreeMap<String,Integer> tempMap =
            new TreeMap<String,Integer>(new NumberMapComparator(passedMap));
        // TreeMap に全部の組をコピー(このときにソートされる)
        tempMap.putAll(passedMap);
        return tempMap;
    }
    /**
     * マップの値を基準としてキーをソートし直し、そのマップを返す.
     * <HR>
     * 111102 作成
     * @param passedMap 値を基準にキーを並び替えたい Map
     * @return 整列後の Map
     * @see NumberMapComparator
     */
    public static Map<String, Double> getSortedMapWithDoubleValue(final Map<String, Double> passedMap) {
        final TreeMap<String,Double> tempMap =
            new TreeMap<String,Double>(new NumberMapComparator(passedMap));
        // TreeMap に全部の組をコピー(このときにソートされる)
        tempMap.putAll(passedMap);
        return tempMap;
    }
    /**
     * Map<String, Integer> から内容を取り出し、結果を pOutputName で指定したファイル名で出力する<BR>
     * ファイル形式は CSV
     * @param passedMap
     * @param pOutputName 出力するファイル名
     * @param pdelimiter データ区切り文字
     */
    public static void outPutMap_S_D(
            final Map<String, Double> passedMap,
            final String pOutputName,
            final String pdelimiter
            ) {
        String localdelimiter = "";
        if ("".equals(pdelimiter)) {
            localdelimiter = ",";
        }else{
            localdelimiter = pdelimiter;
        }

        final TreeMap<String,Double> tempMap =
            new TreeMap<String,Double>();
        // TreeMap に全部の組をコピー(このときにソートされる)
        tempMap.putAll(passedMap);
        // TreeMap の表示
        final Set<String> termSet = tempMap.keySet();  // ソートされている
        final Iterator<String> iterator = termSet.iterator();

        final PrintWriter nameWriter = makeFileWriter(
                pOutputName,
                "shift-jis"
                );
        while(iterator.hasNext()) {
            final String key = iterator.next();
            final Double value = tempMap.get(key);
            nameWriter.println(key + localdelimiter + value);
        }
        nameWriter.close();

        return ;
    }
    /**
     * Map<String, String>から内容を取り出し、結果をファイル名「Default.tsv」で出力する<BR>
     * ファイル形式はTSVとなる
     * @param passedMap
     */
    public static void outPutMapToTSV_S_S(final Map<String, String> passedMap) {
        outPutMapToTSV_S_S(passedMap,"Default.tsv");
    }
    /**
     * Map<String, String>から内容を取り出し、結果をpOutputNameで指定したファイル名で出力する<BR>
     * 文字コードは"shift-jis"、ファイル形式はTSVとなる
     * @param passedMap 内容を出力する Map
     * @param pOutputName 出力するファイル名
     */
    public static void outPutMapToTSV_S_S(
            final Map<String, String> passedMap,
            final String pOutputName
            ) {
        outPutMapToTSV_S_S(
                passedMap,
                pOutputName,
                "",
                false
                );
    }
    /**
     * Map<String, String>から内容を取り出し、結果をpOutputNameで指定したファイル名で出力する<BR>
     * さらに、指定した文字列を1行目に出力する(表を作るときに使う)<BR>
     * 文字コードは"shift-jis"、ファイル形式はTSVとなる
     * @param passedMap 内容を出力する Map
     * @param pOutputName 出力するファイル名
     * @param header
     */
    public static void outPutMapToTSV_S_S(
            final Map<String, String> passedMap,
            final String pOutputName,
            final String header
            ) {
        outPutMapToTSV_S_S(passedMap,pOutputName,header,false);
    }
    /**
     * Map<String, String>から内容を取り出し、結果をpOutputNameで指定したファイル名で出力する<BR>
     * さらに、指定した文字列を1行目に出力する(表を作るときに使う)<BR>
     * 文字コードは"shift-jis"、ファイル形式はTSVとなる
     * @param passedMap 内容を出力する Map
     * @param pOutputName 出力するファイル名
     * @param header ヘッダ
     * @param pEncode 出力文字コード
     */
    public static void outPutMapToTSV_S_S(
            final Map<String, String> passedMap,
            final String pOutputName,
            final String header,
            final String pEncode
            ) {
        outPutMapToTSV_S_S(passedMap,pOutputName,header,false,pEncode);
    }
    /**
     * Map<String, String>から内容を取り出し、結果をpOutputNameで指定したファイル名で出力する<BR>
     * さらに、指定した文字列を1行目に出力する(表を作るときに使う)<BR>
     * 文字コードは"shift-jis"、ファイル形式はTSVとなる
     * @param passedMap 内容を出力する Map
     * @param pOutputName 出力するファイル名
     * @param header ヘッダ
     * @param isQuote キーをダブルクォーテーションでくくるか？
     * R 等で分析するためのテーブルを作成する場合はtrueにしておくことを勧める.
     */
    public static void outPutMapToTSV_S_S(
            final Map<String, String> passedMap,
            final String pOutputName,
            final String header,
            final boolean isQuote
            ) {
        outPutMapToTSV_S_S(passedMap,pOutputName,header,isQuote,"shift-jis");
    }
    /**
     * Map<String, String>から内容を取り出し、結果をpOutputNameで指定したファイル名で出力する<BR>
     * さらに、指定した文字列を1行目に出力する(表を作るときに使う)<BR>
     * 文字コードは"shift-jis"、ファイル形式はTSVとなる
     * <HR>
     * (130630) header が空文字&nullの時はヘッダを出力しないよう修正<BR>
     * @param passedMap 内容を出力する Map
     * @param pOutputName 出力するファイル名
     * @param header ヘッダ
     * @param isQuote キーをダブルクォーテーションでくくるか？
     *  R 等で分析するためのテーブルを作成する場合はtrueにしておくことを勧める.
     */
    public static void outPutMapToTSV_S_S(
            final Map<String, String> passedMap,
            final String pOutputName,
            final String header,
            final boolean isQuote,
            final String pEncode
            ) {
        final String delimiter = "\t";
        final TreeMap<String,String> tempMap =
            new TreeMap<String,String>(passedMap);
        // TreeMap に全部の組をコピー(このときにソートされる)
        tempMap.putAll(passedMap);
        // TreeMap の表示
        final Set<String> termSet = tempMap.keySet();  // ソートされている

        PrintWriter nameWriter = null;
        nameWriter = makeFileWriter(pOutputName, pEncode);
        // ヘッダが指定されているならプリント
        if (StringUtils.isNotEmpty(header)) {
            nameWriter.println(header);
        }

        final Iterator<String> iterator = termSet.iterator();
        while(iterator.hasNext()) {
            String key = iterator.next();
            final String value = tempMap.get(key);

            //二重引用句で括っておく
            if (isQuote && !key.startsWith("\"")) {
                key = Strings.doubleQuote(key);
            }

            nameWriter.println(key + delimiter + value);
        }
         nameWriter.close();

        return ;
    }
    /**
     * Map<String, Integer>から内容を取り出し、結果をファイルに出力する
     * @param passedMap
     */
    public static void outPutMap_S_I(final Map<String, Integer> passedMap) {
        outPutMap_S_I(passedMap,"NoName.txt");
    }
    /**
     * outPutSet メソッドにヘッダプリント機能を追加
     *
     * <HR>
     * (130630) header が空文字&nullの時はヘッダを出力しないよう修正<BR>
     * (110717) 作成<BR>
     *
     * passedSet&lt;String&gt; から内容を取り出し、
     * pOutputName で指定したファイル名で結果を出力する.<BR>
     * 文字コードは "shift-jis" を使用する.
     * @param passedSet String の内容が入った Set&lt;String&gt;
     * @param pOutputName 出力先
     */
    public static void outPutSetWithHeader(
            final Set<String> passedSet ,
            final String pOutputName,
            final String pEncode,
            final String header
            ) {
        final TreeSet<String>  useSet = new TreeSet<String>(passedSet);
        final Iterator<String> iterator = useSet.iterator();
        try (final PrintWriter nameWriter = makeFileWriter(pOutputName, pEncode);) {
            if (StringUtils.isNotEmpty(header)) {
                nameWriter.println(header);
            }

            while(iterator.hasNext()) {
                final String key = iterator.next();
                nameWriter.println(key);
            }
            nameWriter.close();
        }
    }
    /**
     * passedSet<String>から内容を取り出し、結果をNoName.txtというファイルに出力する
     * @param passedSet
     */
    public static void outPutSet(final Set<String> passedSet) {
        outPutSet(passedSet, "NoName.txt", "shift-jis");
    }
    /**
     * passedSet&lt;String&gt; から内容を取り出し、 pOutputName で指定したファイル名で結果を出力する.<BR>
     * 文字コードは "shift-jis" を使用する.
     * @param passedSet String の内容が入った Set&lt;String&gt;
     * @param pOutputName 出力先
     */
    public static void outPutSet(
            final Set<String> passedSet ,
            final String pOutputName
            ) {
        outPutSet(passedSet, pOutputName, "shift-jis");
    }
    /**
     * passedSet<String>から内容を取り出し、<BR>
     * pOutputNameで指定したファイル名、<BR>
     * pEncodeで指定した文字コードで結果を出力する
     * @param passedSet
     * @param pOutputName
     * @param pEncode
     */
    public static void outPutSet(
            final Set<String> passedSet ,
            final String pOutputName,
            final String pEncode
            ) {
        outPutSetWithHeader(passedSet, pOutputName, pEncode, "");
    }
    /**
     * List<String>から内容を取り出し、pOutputNameで指定したファイル名で結果を出力する<BR>
     * 文字コードは"shift-jis"を使う.
     * @param passedAList
     * @param pOutputName 出力先
     */
    public static void outPutList(
            final List<String> passedAList ,
            final String pOutputName
            ) {
        outPutList(passedAList, pOutputName, "shift-jis");
    }
    /**
     * List<String>から内容を取り出し、pOutputNameで指定したファイル名で結果を出力する
     * @param passedAList
     * @param pOutputName 出力先
     * @param encode 文字コード
     */
    public static void outPutList(
            final List<String> passedAList ,
            final String pOutputName,
            final String encode
            ) {
        try (final PrintWriter nameWriter = makeFileWriter(pOutputName, encode);) {
            for(int i = 0; i < passedAList.size(); i++) {
                nameWriter.println(passedAList.get(i));
            }
            nameWriter.close();
        }
    }
    /**
     * List<String>から内容を取り出し、NoName.txtに結果を出力する
     * @param passedAList
     */
    public static void outPutList(final List<String> passedAList) {
        outPutList(passedAList,"NoName.txt");
    }
    /**
     * データファイルの重複要素を削除し、辞書順に並び替える
     * @param pFileName
     */
    public static void uniqueDataFile(final String pFileName) {

        final TreeSet<String> uniqueDataSet = new TreeSet<String>();
        try (final BufferedReader  fileReader = makeFileReader(pFileName,"shift-jis");
             final PrintWriter     fileWriter = makeFileWriter(pFileName,"shift-jis");
                ) {
            String str = fileReader.readLine();
            while (str != null) {
                uniqueDataSet.add(str);
                str = fileReader.readLine();
            }
            fileReader.close();
            fileWriter.println(CollectionUtil.implode(uniqueDataSet));
            fileWriter.close();
        } catch (final IOException iOE) {
            iOE.printStackTrace();
        }
    }
    /**
     * wordsTMapの中身を取りだす
     */
    public static void takeMaps(
            final SortedMap<String,TreeMap<String,Integer>> filesTMap,
            final Set<String> keySet
    ) {
        final StringBuilder takeBld = new StringBuilder();
        Iterator<String> iterator = keySet.iterator();
        final String localLinesep = System.getProperty("line.separator");
        String nowFileAbsolutePath = "";
        TreeMap<String,Integer> tempMap = null;
        //全ファイルにおける単語セット
        final Set<String> termSet = Sets.mutable.empty();

        //まずMapに含まれている単語をすべて取り出す
        while(iterator.hasNext()) {
            nowFileAbsolutePath = iterator.next();
            // filesTMapに含まれるWordsTMapのキーを記録する
            //System.out.println(filesTMap.get(nowFileAbsolutePath));
            try {
                termSet.addAll(filesTMap.get(nowFileAbsolutePath).keySet());  // ソートされている
            } catch(final NullPointerException np) {
                np.printStackTrace();
            }
        }

        //次にMapを構成する
        Iterator<String> setIter = termSet.iterator();
        takeBld.append(",");
        while(setIter.hasNext()) {
            takeBld.append(setIter.next() + ",");
        }
        takeBld.append(localLinesep);

        //再利用
        iterator = keySet.iterator();
        while(iterator.hasNext()) {
            nowFileAbsolutePath = iterator.next();
            final String  nowFile = new File(nowFileAbsolutePath).getName();

            tempMap = filesTMap.get(nowFileAbsolutePath);
            //System.out.println(nowFileAbsolutePath + " : " + tempMap);
            takeBld.append(nowFile);
            setIter = termSet.iterator();
            while(setIter.hasNext()) {
                final String  key   = setIter.next();
                final Integer value = tempMap.get(key);
                //System.out.println(key);
                if (value == null) {
                    takeBld.append("," + 0);
                }else{
                    takeBld.append("," + value);
                }
            }
            takeBld.append(localLinesep);
        }
        outPutStr(takeBld.toString());
    }

    /**
     * Map<String,Map<String,Integer>>の中身を取りだし、TSV形式で出力する.<BR>
     * ClassifierMainクラスのanalysisNoteCSV()メソッドで使う
     * @param pMap
     * @param outPutFileName
     */
    public static final void takeMapsInMap(
            final Map< String, Map<String,Integer> > pMap,
            final String outPutFileName
    ) {
        final StringBuilder takeBuffer = new StringBuilder();
        String gotItem = "";
        String gotItemInner = "";
        final String lineSepar = System.getProperty("line.separator");
        Iterator<String> iterator;

        System.out.println(pMap);

        Map<String,Integer> tempMap = null;
        //全ファイルにおける分類セット
        final Set<String> termSet = Sets.mutable.empty();

        // pMapのキーセット
        final Set<String> pMapKeySet = pMap.keySet();

        iterator = pMapKeySet.iterator();

        // 子マップのキーをすべて取り出す
        while(iterator.hasNext()) {
            gotItem = iterator.next();
            termSet.addAll(pMap.get(gotItem).keySet());
        }

        iterator = termSet.iterator();
        // バッファ一行目に分類を置く
        while(iterator.hasNext()) {
            gotItem = iterator.next();
            takeBuffer.append("\t" + iterator.next());
        }
        takeBuffer.append(lineSepar);
        System.out.println("子マップのキーをすべて取り出す : " + takeBuffer.toString());
        iterator = pMapKeySet.iterator();
        Iterator<String> iter;
        while(iterator.hasNext()) {
            gotItem = iterator.next();
            takeBuffer.append(gotItem);
            tempMap = pMap.get(gotItem);
            iter = tempMap.keySet().iterator();
            while(iter.hasNext()) {
                gotItemInner = iter.next();
                if (tempMap.get(gotItemInner) == null) {
                    takeBuffer.append("," + 0);
                }else{
                    takeBuffer.append("," + tempMap.get(gotItemInner));
                }
            }
            takeBuffer.append(lineSepar);
        }
        outPutStr(takeBuffer.toString(), outPutFileName,"shift-jis");
        return ;
    }
    /**
     * 複数のデータファイル<String,Integer>をマージする.
     * 結果はCSV(ファイル名は第二引数で指定)で出力する.
     * データの区切り文字はファイルの拡張子から自動判別する.
     * @param fileNames マージするファイル名を入れたString型配列
     * @param outputFileName 出力ファイル名
     */
    public static final void mergeDataFiles(
            final String[] fileNames,
            final String outputFileName
            ) {
        String dSepar;
        String[] tempStrA;
        final Map<String, Integer> resMap = new HashMap<String, Integer>();
        try{
            for (int i = 0; i < fileNames.length; i++) {
                final BufferedReader fileReader = makeFileReader(fileNames[i], "shift-jis");
                String str = fileReader.readLine();
                // データ区切り文字はファイルの拡張子から自動判別する
                if (fileNames[i].toLowerCase().endsWith(".csv")) {
                    dSepar = ",";
                } else if (fileNames[i].toLowerCase().endsWith(".tsv")) {
                    dSepar = "\t";
                } else {
                    dSepar = " ";
                }
                while(str != null) {
                    tempStrA = str.split(dSepar);
                    if (tempStrA.length > 1) {
                        int count = 1;
                        if (resMap.containsKey(tempStrA[0])) {
                            count = resMap.get(tempStrA[0]) + Integer.parseInt(tempStrA[1]);
                            resMap.put(tempStrA[0], count);
                        } else {
                            resMap.put(tempStrA[0], Integer.parseInt(tempStrA[1]));
                        }
                    }

                    str = fileReader.readLine();
                }
                fileReader.close();
            }
        } catch(final IOException iOE) {
            iOE.printStackTrace();
        }
        outPutMap_S_I(resMap, outputFileName);
    }
    /**
     * 複数のデータファイル<String,Integer>をマージする.
     * 結果はCSV(ファイル名"merged.csv")で出力する.
     * データの区切り文字はファイルの拡張子から自動判別する.
     * @param fileNames マージするファイル名を入れたString型配列
     */
    public static final void mergeDataFiles(final String[] fileNames) {
        mergeDataFiles(fileNames,"merged.csv");
    }
    /**
     * 指定されたパスにフォルダを生成する
     * @param pOutDirPath
     * @return すでにフォルダが存在するか、生成に成功したらtrue、失敗したらfalse
     */
    public static final boolean mkdir(final String pOutDirPath) {
        // 結果出力用フォルダがまだ存在しないなら生成しておく
        if (!new File(pOutDirPath).exists()) {
            return new File(pOutDirPath).mkdirs();
        } else {
            return true;
        }
    }
    /**
     * テキストファイルから Set&lt;String&gt; を値に持つ Map&lt;String,Set&lt;String&gt;&gt; を構成して返す.<BR>
     * 0番目の要素をキーに、1番目以降の要素を入れた Set を値に持つ.
     * @param targetFilePath
     * @param targetFileEncode
     * @param delimiter
     * @return  Set&lt;String&gt; を値に持つ Map&lt;String,Set&lt;String&gt;&gt;
     */
    public static final Map<String,Set<String>> getSetMapFromFile(
            final String targetFilePath,
            final String targetFileEncode,
            final String delimiter
            ) {
        final Map<String,Set<String>> resMap = new HashMap<String, Set<String>>(1000);
        final BufferedReader fileReader = makeFileReader(targetFilePath, targetFileEncode);
        try {
            String str = fileReader.readLine();
            String[] temp;
            Set<String> tempSet;
            while(str != null) {
                temp = str.split(delimiter);
                tempSet = CollectionUtil.arrayToSet(temp);
                tempSet.remove(temp[0]);
                resMap.put(temp[0],tempSet);
                str = fileReader.readLine();
            }
            fileReader.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return resMap;
    }
    /**
     * テキストファイルから List&lt;String> を値に持つ Map&lt;String,List&lt;String>> を構成して返す.
     * @param targetFilePath
     * @param targetFileEncode
     * @param delimiter
     * @return List<String> を値に持つ Map<String,List<String>>
     */
    public static final Map<String,List<String>> getListMapFromFile(
            final String targetFilePath,
            final String targetFileEncode,
            final String delimiter
            ) {
        final Map<String,List<String>> resMap = new HashMap<String, List<String>>(1000);
        final BufferedReader fileReader = makeFileReader(targetFilePath, targetFileEncode);
        try {
            String str = fileReader.readLine();
            String[] temp;
            List<String> tempList;
            while (str != null) {
                temp = str.split(delimiter);
                tempList = Lists.mutable.of(temp);
                tempList.remove(temp[0]);
                resMap.put(temp[0],tempList);
                str = fileReader.readLine();
            }
            fileReader.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return resMap;
    }
    /**
     * Set&lt;List&lt;String>> をデータファイルから構成し、返す.
     * @param filePath      データファイルの場所
     * @param fileEncode    データファイルの文字コード
     * @param delimiter データファイルのデータ区切り文字(例：.tsv なら"\t")
     * @return Set&lt;List&lt;String&gt;&gt;
     */
    public static Set<List<String>> getListSetFromFile(
            final String filePath,
            final String fileEncode,
            final String delimiter
            ) {
        final Set<List<String>> catSet = Sets.mutable.empty();
        final BufferedReader fileReader = makeFileReader(filePath, fileEncode);
        String str = "";
        try {
            str = fileReader.readLine();
            while(str != null) {
                catSet.add(Lists.mutable.of(str.split(delimiter)));
                str = fileReader.readLine();
            }
            fileReader.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return catSet;
    }
    /**
     * List&lt;List&lt;String&gt;&gt; をデータファイルから構成し、返す.
     * <HR>
     * (120217) 二次元データセットを扱うために定義
     * @param filePath      データファイルの場所
     * @param fileEncode    データファイルの文字コード
     * @param delimiter データファイルのデータ区切り文字(例：.tsv なら"\t")
     * @return List&lt;List&lt;String&gt;&gt;
     */
    public static final List<List<String>> getListListFromFile(
            final String filePath,
            final String fileEncode,
            final String delimiter
            ) {
        final List<List<String>> resList = new ArrayList<List<String>>(8000);
        final BufferedReader fileReader = makeFileReader(filePath, fileEncode);
        String str = "";
        try {
            str = fileReader.readLine();
            while(str != null) {
                resList.add(Lists.mutable.of(str.split(delimiter)));
                str = fileReader.readLine();
            }
            fileReader.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return resList;
    }
    /**
     * ファイルのユニーク要素数を計測して、コンソールに表示する.<BR>
     * 具体的には、ColleUtil.getContainsSetFromMap()メソッドを使って要素全体を持つSetを構築し、
     * そのサイズを表示している.
     * @param targetFilePath   要素数を計測したいファイル
     * @param targetFileEncode 要素数を計測したいファイルの文字コード
     * @param delimiter    要素数を計測したいファイルのデータ区切り文字
     */
    public static final void printFileContainsVal(
            final String targetFilePath,
            final String targetFileEncode,
            final String delimiter
            ) {
        System.out.println(
                targetFilePath
                + " contains : "
                + CollectionUtil.getContainsSetFromDataFile(
                            targetFilePath,
                            targetFileEncode,
                            delimiter
                            ).size()
                        );
    }
    /**
     * 指定したファイルの文字数を計測して返す.
     * @param pFileName
     * @param pEncode
     */
    public static int countCharacters(final String pFileName, final String pEncode) {
        final MutableIntList total = IntLists.mutable.empty();
        try {
            Files.readAllLines(Paths.get(pFileName), Charset.forName(pEncode)).forEach(line -> {
                total.add(line.replaceAll("\\s", "").length());
            });
        } catch (final Exception e) {
            //e.printStackTrace();
            System.out.println(pFileName);
        }
        return (int) total.sum();
    }
    /**
     * 指定したファイルの中身を String で返す.行間は改行記号で連結する.
     * @param pFileName
     * @param pEncode
     * @throws IOException
     */
    public static final String getStrFromFile(final String pFileName, final String pEncode) {
        return getStrFromFile(pFileName, pEncode, System.lineSeparator());
    }

    /**
     * 指定したファイルの中身を String で返す.
     * 行ごとはlineSeparatorで連結する.
     * @param pFileName
     * @param pEncode
     * @param lineSeparator
     * @return 指定したファイルの中身を入れたString
     */
    public static final String getStrFromFile(
            final String pFileName,
            final String pEncode,
            final String lineSeparator
            ) {
        final StringBuilder buf = new StringBuilder(10000);
        try (final BufferedReader read = makeFileReader(pFileName, pEncode)) {
            String str = read.readLine();
            while (str != null) {
                buf.append(str).append(lineSeparator);
                str = read.readLine();
            }
            read.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }
    /**
     * 指定した InputStream の中身を String に入れて返す.
     * 行ごとはlineSeparatorで連結する.
     * <HR>
     * (130706) 作成<BR>
     * @param pStream InputStream
     * @param pEncode InputStream の文字コード
     * @return 指定したファイルの中身を入れ たString
     */
    public static final String getStrFromStream(final InputStream pStream, final String pEncode) {
        final StringBuilder buf = new StringBuilder(10000);
        try (final BufferedReader read = makeInputStreamReader(pStream, pEncode);) {
            final String line = System.lineSeparator();
            String str = read.readLine();
            while (str != null) {
                buf.append(str).append(line);
                str = read.readLine();
            }
            read.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }
    /**
     * 指定した列の要素をすべて格納したSet&lt;String&gt;を返す.
     * ファイルのデータ区切り文字は拡張子から自動判別する.
     * <HR>
     * @param pFileName 指定したファイルのパス
     * @param pEncode   指定したファイルのエンコード
     * @param index     データを取り出したい列の番号、<b>0から数えることに注意</b>
     * @return 指定したファイルの中身を入れたString
     */
    public static final Set<String> takeDesignatedIndexSet(
            final String pFileName,
            final String pEncode,
            final int index
            ) {
        final Set<String> resSet = Sets.mutable.empty();
        final String delimiter = getDelimiterForFile(pFileName);
        try (final BufferedReader read = makeFileReader(pFileName, pEncode);) {
            String str = read.readLine();
            while (str != null) {
                final String string = str.split(delimiter)[index];
                if (!"".equals(string)) {
                    resSet.add(string);
                }
                str = read.readLine();
            }
            read.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return resSet;
    }
    /**
     * 指定したファイルの一行目を返す.
     * 表ファイルのヘッダ取得等に利用
     * @param filePath 一行目が欲しいファイルのパス
     * @param encode そのファイルの文字コード
     * @return 一行目(String)
     */
    public static String getHeader(
            final String filePath,
            final String encode
    ) {
        try (final BufferedReader fileReader = FileUtil.makeFileReader(filePath, encode);) {
            return fileReader.readLine();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 指定したファイルの行数を計測して返す.
     * <HR>
     * 111011作成
     * @param filePath 一行目が欲しいファイルのパス
     * @param encode そのファイルの文字コード
     * @return 指定したファイルの行数(int)
     */
    public static int getColumnVal(final String filePath, final String encode) {
        int i = 0;
        try (final BufferedReader fileReader = FileUtil.makeFileReader(filePath, encode);) {
            String str = fileReader.readLine();
            while (str != null) {
                i++;
                str = fileReader.readLine();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return i;
    }
    /**
     * データファイルから 指定した第0要素を持つ行を取り出す.
     * <HR>
     * 111017 作成
     */
    public static Set<String> getDesignatedColumn(
            final Set<String> targetNamesSet,
            final String targetFilePath,
            final String targetFileEncode,
            final String delimiter
            ) {
        final Set<String> resSet = Sets.mutable.empty();
        try (final BufferedReader fileReader = makeFileReader(targetFilePath, targetFileEncode);) {
            String str = fileReader.readLine();
            String temp;

            while(str != null) {
                temp = str.split(delimiter)[0];
                if (targetNamesSet.contains(temp)) {
                    resSet.add(str);
                }
                str = fileReader.readLine();
            }
            fileReader.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return resSet;
    }
    /**
     * Javaでファイルを移動するサンプルです.
     * (111230) 拝借
     * <PRE>
     * ■使い方
     *
     * moveFile("c:\\hello.txt", "c:\new\\")
     *
     * c:\\hello.txtファイルをc:\new\\に移動します.
     * </PRE>
     * @see <a href="http://www.syboos.jp/java/doc/move-file-to-another-directory.html">
     * File.renameToでファイルを移動</a>
     * @param orgFilePath 移動させたいファイルのパス
     * @param destDir 移動先フォルダのパス
     * @return 移動が成功したか否か
     */
    public static boolean moveFile(
            final String orgFilePath,
            final String destDir
            ) {
        // 移動元のファイルパス
        final File file = new File(orgFilePath);
        // 移動先のフォルダ
        File dir = new File(destDir);
        if (!dir.getName().endsWith("\\")
                || !dir.getName().endsWith(Strings.getDirSeparator())) {
            dir = new File(dir.getAbsolutePath() + Strings.getDirSeparator());
        }
        //System.out.println(dir.getAbsolutePath());//111230 CO
        // 移動
        return file.renameTo(new File(dir, file.getName()));
    }
    /**
     * ファイルが存在するか否かを返す.
     * @param filePath ファイルへのパス
     * @return ファイルが存在する場合は true、しない場合は false
     */
    public static boolean isExistFile(final String filePath) {
        return new File(filePath).exists();
    }
    /**
     * map の中身を delimiter で連結してファイルに出力する.
     * <HR>
     * (130707) 作成<BR>
     * @param map 出力する内容を入れた Map
     * @param filePath 出力先
     * @param encode 出力ファイルの文字コード
     * @param delimiter データ区切り文字
     */
    public static final void outPutMap(
            final Map<String, ?> map,
            final String filePath,
            final String encode,
            final String delimiter
            ) {
        final StringBuilder bld = new StringBuilder(map.size() * 300);
        final Iterator<String> iter = map.keySet().iterator();
        while (iter.hasNext()) {
            final String key = iter.next();
            bld.append(key);
            bld.append(delimiter);
            bld.append(map.get(key).toString());
            bld.append(System.lineSeparator());
        }
        outPutStr(bld.toString(), filePath, encode);
    }
    /**
     * ファイルから Stream を取得する.
     * @param filePath
     * @return InputStream オブジェクト
     */
    public static final InputStream getStream(final String filePath) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (final InputStream in = classLoader.getResourceAsStream(filePath);) {
            if (in != null) {
                return new BufferedInputStream(
                        classLoader.getResourceAsStream(filePath)
                    );
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * pathToFile のファイルが存在するか否かを調べる.
     * @param pathToFile ファイルへのパス
     * @return ファイルが存在すれば true を返す
     */
    public static final boolean exists(final String pathToFile) {
        return new File(pathToFile).exists();
    }
    /**
     * コピー元のパス[srcPath]から、コピー先のパス[destPath]へファイルのコピーを行う.
     * コピー処理にはFileChannel#transferToメソッドを利用する.
     * 尚、コピー処理終了後、入力・出力のチャネルをクローズする.
     * @param srcPath      コピー元のパス
     * @param destPath     コピー先のパス
     * @return             コピーが正常に完了した時は true
     * @throws IOException 何らかの入出力処理例外が発生した場合
     * @see <a href="http://sattontanabe.blog86.fc2.com/blog-entry-71.html"> Java ファイルコピー（簡単・高速）</a>
     */
    public static boolean copyTransfer(
            final String srcPath,
            final String destPath
            )
        throws IOException {
        if (StringUtils.isEmpty(srcPath) || StringUtils.isEmpty(destPath)) {
            return false;
        }

        try (
            @SuppressWarnings("resource")
            final FileChannel srcChannel  = new FileInputStream(srcPath).getChannel();
            @SuppressWarnings("resource")
            final FileChannel destChannel = new FileOutputStream(destPath).getChannel();
            ) {
            final long expected = srcChannel.size();
            final long actual   = srcChannel.transferTo(0, srcChannel.size(), destChannel);
            return (expected != actual) ? false : true;
        }
    }
    /**
     * filePath を URL オブジェクトに変換して返す.
     * JavaFX で多用するため定義しておく
     * @return URL オブジェクト
     */
    public static final URL getUrl(final String filePath) {
        try {
            return new File(filePath).toURI().toURL();
        } catch (final MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 最終更新時刻を Unixtime で返す.
     * @param filePath ファイルパス
     * @return Unixtime
     */
    public static final long lastModified(final String filePath) {
        final File file = new File(filePath);
        return file != null && file.exists() ? file.lastModified() : -1;
    }
    /**
     * 画像か否かを判定する.
     * @param filePath ファイルパス
     * @return 既知の画像拡張子ならtrue
     */
    public static final boolean isImageFile(final String filePath) {
        final String lowerCase = filePath.toLowerCase();
        for (final String identifier : IMAGE_FILE_IDENTIFIERS) {
            if (lowerCase.endsWith(identifier)) {
                return true;
            }
        }
        return false;
    }
    /**
     * ファイルパスをHTMLで扱える形式に変換して返す.
     * ex)
     * D:\img\tomato.png -> file:///D:/img/tomato.png
     * @param f ファイルオブジェクト
     * @return HTMLで扱える形式のファイルパス
     */
    public static final String getHtmlFilePath(final File f) {
        return getHtmlFilePath(f.getAbsolutePath());
    }
    /**
     * ファイルパスをHTMLで扱える形式に変換して返す.
     * ex)
     * D:\img\tomato.png -> file:///D:/img/tomato.png
     * @param path ファイルパス文字列
     * @return HTMLで扱える形式のファイルパス
     */
    public static final String getHtmlFilePath(final String path) {
        return FILE_PROTOCOL + path.replace("\\", "/").toLowerCase();
    }

    /**
     * File オブジェクトをファイルに出力する.
     * @param file File object.
     * @param pathToFile path/to/file.
     * @see <a href="https://github.com/apache/commons-io/blob/trunk/src/main/java/org/apache/
     *commons/io/FileUtils.java#L1138">org.apache.commons.io.FileUtils.java</a>
     * @throws IOException
     */
    public static void printFile(
            final File file,
            final String pathToFile
            ) throws IOException {
        try (final OutputStream fos = new BufferedOutputStream(new FileOutputStream(pathToFile));
                final InputStream in = new BufferedInputStream(new FileInputStream(file));
                ) {
            final byte[] buff = new byte[128];
            int len;
            while ((len = in.read(buff)) != -1) {
                for (int i = 0; i < len; i++) {
                    fos.write(buff[i]);
                }
            }
            in.close();
            fos.close();
        }
    }

    /**
     * remove file protocol.
     * @param uri starts with "file:///"
     * @return uri removed file protocol.
     */
    public static final String uriToPath(final String uri) {
        if (StringUtils.isEmpty(uri)) {
            return uri;
        }
        return uri.startsWith(FILE_PROTOCOL) ? uri.substring(FILE_PROTOCOL.length()) : uri;
    }

    /**
     * find file extension from passed file name.
     * @param fileName
     * @return Optional string. It contains ".txt".
     */
    public static Optional<String> findExtension(final File file) {
        if (file == null) {
            return Optional.empty();
        }
        return findExtension(file.getName());
    }

    /**
     * find file extension from passed file name.
     * @param fileName
     * @return Optional string.
     */
    public static Optional<String> findExtension(final String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return Optional.empty();
        }
        final int index = fileName.lastIndexOf(".");
        return (index == -1) ? Optional.empty() : Optional.of(fileName.substring(index));
    }

    /**
     * read all files content. not recursive.
     * @param pathToDir
     * @return list contains all files content.
     */
    public static List<String> readDirLines(final String pathToDir) {
        final Path dir = Paths.get(pathToDir);
        if (!Files.isDirectory(dir)) {
            return Collections.emptyList();
        }
        final List<String> list = Lists.mutable.empty();
        try {
            Files.list(dir).filter(path -> {return !Files.isDirectory(path);}).forEach(path -> {
                try {
                    list.addAll(Files.readAllLines(path));
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            });
            return list;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * open reader.
     * @param conn
     * @return
     * @throws IOException
     */
    public static BufferedReader openReader(final InputStream in) throws IOException {
        return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    /**
     * 最終更新日時が n 日以内のファイルなら true を返す.
     * @param file ファイル
     * @param ndays n 日
     * @return 最終更新日時が n 日以内のファイルなら true
     */
    public static boolean isLastModifiedNdays(final File file, final long ndays) {
        return System.currentTimeMillis() - file.lastModified() < TimeUnit.DAYS.toMillis(ndays);
    }

    /**
     * TODO write test.
     * @param filePath
     * @return
     */
    public static String removeExtension(final String filePath) {
        return filePath.substring(0, filePath.lastIndexOf("."));
    }

}