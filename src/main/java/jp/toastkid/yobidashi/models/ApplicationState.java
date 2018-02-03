/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi.models;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * Private constructor.
     */
    private ApplicationState() {
        // NOP.
    }

    /** 1MB に等しい数値を返す.すなわち 1024  の 2 乗 */
    private static final long MEGA_BYTE = Math.round(Math.pow(1024, 2));

    /**
     * アプリケーションの情報を取得する。
     * <HR>
     * (121229) 作成<BR>
     */
    public static Map<String, String> getConfigMap() {
        final Map<String, String> configMap = new HashMap<>(6);
        configMap.putAll(getJavaConfigMap());
        configMap.putAll(getRuntimeConfigMap());
        return configMap;
    }

    /**
     * Java の情報を Map に入れて返す。
     * <HR>
     * (121229) 作成<BR>
     */
    private static Map<String, String> getJavaConfigMap() {
        final Map<String, String> configMap = new HashMap<>(2);
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
    private static Map<String, String> getRuntimeConfigMap() {
        final Map<String, String> configMap = new HashMap<>(4);
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
    private static String getFormatNum(final long l) {
        return NumberFormat.getInstance().format(l / MEGA_BYTE);
    }
}
