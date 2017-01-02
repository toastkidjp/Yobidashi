package jp.toastkid.libs.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * TODO 解体する.
 *
 * Web 上の文書を取得する.
 *
 * @author Toast kid
 * @deprecated
 */
@Deprecated
public final class WebDocumentUtil {

    /** Y!ファイナンスの日経平均株価のページ. */
    private static final String STOCKS_FINANCE_YAHOO = "http://stocks.finance.yahoo.co.jp/stocks/detail/?code=998407.O";

    /** 日経平均株価を抜き出す正規表現. */
    private static final Pattern NIKKEI_PATTERN
        = Pattern.compile("<td class=\"stoksPrice\">(.+?)</td>", Pattern.DOTALL);

    /** 値動きを抜き出す正規表現. */
    private static final Pattern CHANGE_PATTERN
        = Pattern.compile("<span class=\".*yjMSt\">(.+?)</span>", Pattern.DOTALL);

    /** エンコード */
    public static String encode = "utf-8";

    /**
     * Private constructor.
     */
    private WebDocumentUtil() {
        // TODO
    }

    /**
     * 指定した URL の Web 文書から特定部分を正規表現で取得する.
     * @param urlStr 取得したい文書の URL 文字列
     * @param pEncode 取得したい文書の文字コード
     * @param targetPat Web 文書から取り出したい部分の正規表現
     * @return 取得した Web 文書を入れた StringBuffer
     */
    public static String getWebStr(
            final String urlStr,
            final String pEncode,
            final Pattern targetPat
    ){
        final StringBuilder result = new StringBuilder();
        Matcher matcher;
        try {
            final HttpURLConnection urlConnection = getHttpURLConnection(urlStr);
            urlConnection.connect();
            try (final BufferedReader getReader
                    = makeReader(urlConnection.getInputStream(), pEncode)) {
                final String prefix = targetPat.pattern()
                                        .substring(0, targetPat.pattern().length() / 5);
                String str = getReader.readLine();
                while(str != null){
                    if(str.contains(prefix)){
                        matcher = targetPat.matcher(str);
                        if (matcher.find()) {
                            result.append(matcher.group(1));
                            break;
                        }
                    }
                    str = getReader.readLine();
                }
                getReader.close();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
    /**
     * 指定した URL の Web 文書を取得する.
     * @param urlStr 取得したい文書の URL 文字列
     * @param pEncode 取得したい文書の文字コード
     * @return 取得した Web 文書を入れた StringBuffer
     */
    public static StringBuffer getWebDocs(
            final String urlStr,
            final String pEncode
    ){
        final StringBuffer resBuf = new StringBuffer();
        try {
            final HttpURLConnection urlConnection = getHttpURLConnection(urlStr);
            urlConnection.connect();
            try (final BufferedReader getReader = makeReader(urlConnection.getInputStream(), pEncode);
                    ) {
                String str = getReader.readLine();
                while (str != null) {
                    resBuf.append(str).append(Strings.LINE_SEPARATOR);
                    str = getReader.readLine();
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return resBuf;
    }

    /**
     *
     * @param in
     * @param pEncode
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    private static BufferedReader makeReader(
            final InputStream in,
            final String pEncode
            ) throws UnsupportedEncodingException, IOException {
        return new BufferedReader(new InputStreamReader(in, pEncode));
    }

    /**
     * 要求されたURLのHttpURLConnectionを生成して返す.
     * @param urlStr URL 文字列
     * @return 要求されたURLのHttpURLConnection
     */
    public static HttpURLConnection getHttpURLConnection(final String urlStr){
        HttpURLConnection urlConnection = null;
        try {
            // 漢字を含むURL対策
            if(urlStr.indexOf(".jp") != -1){
                //System.out.println(targetURLString.split("/")[2]);
                urlStr.replaceFirst(
                        urlStr.split("/")[2],
                        URLEncoder.encode(urlStr.split("/")[2],"utf-8")
                        );
            }
            //System.out.println(targetURLString);//120510 CO

            final URL url = new URL(urlStr);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)"
                    );
            urlConnection.setRequestMethod("GET");
            //urlConnection.setRequestProperty("Accept-Language","ja");
            //urlConnection.setRequestProperty("Accept","image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, application/x-shockwave-flash, */*");
            //urlConnection.setRequestProperty("Proxy-Connection","Keep-Alive");
            //urlConnection.setRequestProperty("Host","xml-jp.amznxslt.com");
            //*/
            //System.out.println("Source Type = " + urlConnection.getContentType() + "\n");

        } catch (final MalformedURLException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return urlConnection;
    }

    /**
     * Web文書を取得する.
     * @param urlStr
     * @return 取得した Web 文書の文字列表現
     */
    public static StringBuffer getWebDocs(final String urlStr){
        return getWebDocs(urlStr,WebDocumentUtil.encode);
    }

    /**
     * Web 文書を取得し、String で返却する.
     * @param urlStr
     * @param pEncode
     * @return 取得した Web 文書の文字列表現
     */
    public static String getWebDocsStr(
            final String urlStr,
            final String pEncode
            ){
        return getWebDocs(urlStr,pEncode).toString();
    }

    /**
     * Web 文書を文字コード UTF-8 で取得し、String で返却する
     * @param urlStr
     * @return 取得した Web 文書の文字列表現
     */
    public static String getWebDocsStr(final String urlStr){
        return getWebDocs(urlStr, WebDocumentUtil.encode).toString();
    }
    /**
     * 渡されたエンコードを設定する.
     * @param passedEncode
     */
    public static void setEncoding(final String passedEncode){
        encode = passedEncode;
        //System.out.println("エンコードを " + encode + " に設定しました。");
        return ;
    }

    /**
     * エンコードをutf-8に設定する.
     */
    public static void setEncoding(){
        if(encode == null){
            encode = "utf-8";
        }
        return ;
    }
    /**
     * 最新の日経平均株価を取得する.取引日の15時30分以降は必然的に終値を取得する.
     * 下記のような HTML から抜粋する.
     * <pre>
     * &lt;td class="stoksPrice"&gt;17,344.06&lt;/td&gt;
     * &lt;td class="change"&gt;&lt;span class="yjSt"&gt;前日比&lt;/span&gt;
     * &lt;span class="icoUpGreen yjMSt"&gt;+370.26（+2.18%）&lt;/span&gt;&lt;/td&gt;
     * </pre>
     * @see <a href="http://stocks.finance.yahoo.co.jp/stocks/detail/?code=998407.O">
     * Y! ファイナンスの日経平均株価のページ</a>
     * @return 最新の日経平均株価と前日比
     */
    public static String getNikkei225() {
        Matcher matcher;
        String stockPrice = "";
        String change = "";
        final String[] content
            = WebDocumentUtil.getWebDocsStr(STOCKS_FINANCE_YAHOO).split(Strings.LINE_SEPARATOR);
        for (final String line : content) {
            if (line.indexOf("stoksPrice") != -1) {
                matcher = NIKKEI_PATTERN.matcher(line);
                if (matcher.find()) {
                    stockPrice = matcher.group(1);
                }
            }
            if (line.indexOf("yjSt") != -1) {
                matcher = CHANGE_PATTERN.matcher(line);
                if (matcher.find()) {
                    change = matcher.group(1);
                }
            }
        }
        return new StringBuilder().append(stockPrice).append(" ").append(change).toString();
    }

}