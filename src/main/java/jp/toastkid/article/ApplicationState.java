package jp.toastkid.article;

import java.text.NumberFormat;
import java.util.Map;

import org.eclipse.collections.impl.factory.Maps;

/**
 * 現在稼働中のアプリケーションの情報を表示する画面
 * <HR>
 * (130616) Play から独立させた形で実装<BR>
 * (130101) ユーザエージェント情報を追加<BR>
 * (121229) 作成<BR>
 * @author Toast kid
 *
 */
public final class ApplicationState {

    /** 1MB に等しい数値を返す.すなわち 1024  の 2 乗 */
    private static final long MEGA_BYTE = Math.round(Math.pow(1024, 2));

    /**
     * アプリケーションの情報を取得する。
     * <HR>
     * (121229) 作成<BR>
     */
    public static final Map<String, String> getConfigMap() {
        final Map<String, String> configMap = Maps.mutable.withInitialCapacity(6);
        configMap.putAll(getJavaConfigMap());
        configMap.putAll(getRuntimeConfigMap());
        return configMap;
    }

    /**
     * Java の情報を Map に入れて返す。
     * <HR>
     * (121229) 作成<BR>
     */
    private static final Map<String, String> getJavaConfigMap() {
        final Map<String, String> configMap = Maps.mutable.withInitialCapacity(2);
        configMap.put("Java Version", System.getProperty("java.version"));
        configMap.put("Java Home",    System.getProperty("java.home"));
        return configMap;
    }

    /**
     * Runtime の情報を Map に入れて返す。
     * <HR>
     * (130101) ユーザエージェント情報を追加<BR>
     * (121229) 作成<BR>
     */
    private static final Map<String, String> getRuntimeConfigMap() {
        final Map<String, String> configMap = Maps.mutable.withInitialCapacity(4);
        final Runtime runtime = Runtime.getRuntime();
        configMap.put("Max Memory",          getFormatNum(runtime.maxMemory() ));
        configMap.put("Free Memory",         getFormatNum(runtime.freeMemory() ));
        configMap.put("Total Memory",        getFormatNum(runtime.totalMemory() ));
        configMap.put("Available Processor", Long.toString(runtime.availableProcessors()));
        //configMap.put("User Agent",          request.headers.get("user-agent").toString());
        return configMap;
    }

    /**
     * long 値を整形して文字列で返す。
     * <HR>
     * (130616) 作成<BR>
     * @param l long 値
     * @return 整形済み long 値の文字列表現
     */
    private static final String getFormatNum(final long l) {
        return NumberFormat.getInstance().format(l / MEGA_BYTE);
    }
}
