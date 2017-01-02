package jp.toastkid.libs.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * スレッドの実行を一定時間停止するメソッド集.
 * @author Toast kid
 * @see <a href="https://github.com/apache/storm/blob/master/storm-core/
 *src/jvm/backtype/storm/utils/Utils.java">Utils</a>
 * @see <a href="https://github.com/apache/storm/blob/master/storm-core/
 *src/jvm/backtype/storm/utils/Time.java">Time</a>
 *
 */
public final class TimeUtil {

    /** スレッド停止時間. */
    private static volatile Map<Thread, AtomicLong> threadSleepTimes;

    private static AtomicBoolean simulating = new AtomicBoolean(false);

    private static final Object sleepTimesLock = new Object();

    private static ThreadLocal<AtomicLong> simulatedCurrTimeMs
        = ThreadLocal.withInitial(() -> new AtomicLong(0));

    /**
     * Private constructor.
     */
    private TimeUtil() {
        // NOP.
    }

    public static void startSimulating() {
        synchronized(sleepTimesLock) {
            simulating.set(true);
            threadSleepTimes = new ConcurrentHashMap<>();
        }
    }

    public static void stopSimulating() {
        synchronized(sleepTimesLock) {
            simulating.set(false);
            threadSleepTimes = null;
        }
    }

    public static boolean isSimulating() {
        return simulating.get();
    }

    /**
     * Threadクラスのsleepメソッドを用いて、指定ミリ秒間プログラムの実行を停止する<BR>
     * 実行秒数はvariableWaitTime分の幅ができる
     * @param pWaitTime 停止する時間(単位:ミリ秒),推奨値10000
     * @param variableWaitTime ランダム停止幅(単位:ミリ秒),推奨値5000
     */
    public static final void waitRandomTime(
            final long pWaitTime,
            final long variableWaitTime
            ) {
        // 一定時間待機する
        long temp = pWaitTime + (long)(variableWaitTime * Math.random());
        //値が 0 以下になった場合は 0 にする
        if(temp < 0) {
            temp = 0;
        }
        TimeUtil.waitFixedTime(temp);
    }

    /**
     * Threadクラスのsleepメソッドを用いて指定ミリ秒間プログラムの実行を停止する<BR>
     * 実際には次の命令を実行するだけである
     * <pre>
     * Thread.sleep(pWaitTime);
     * </pre>
     * @param ms 停止する時間(単位:ミリ秒)
     */
    public static final void waitFixedTime(final long ms) {
        try{
            Thread.sleep(ms);
        } catch(final InterruptedException interuE) {
            interuE.printStackTrace();
        }
    }

    /**
     * Threadクラスのsleepメソッドを用いて30秒間プログラムの実行を停止する<BR>
     * 実際には次の命令を実行するだけである
     * <pre>
     * Thread.sleep(30000);
     * </pre>
     */
    public static final void waitHalfMinute() {
        try{
            Thread.sleep(30000);
        } catch(final InterruptedException interuE) {
            interuE.printStackTrace();
        }
    }

    /**
     * 指定ミリ秒後まで sleep する.
     * @param targetTimeMs sleepさせるミリ秒数
     */
    public static void sleep(final long ms) throws InterruptedException {
        sleepUntil(System.currentTimeMillis() + ms);
    }

    /**
     * 指定 timestamp ([ms]) まで sleep する.
     * @param targetTimeMs timestamp [ms]
     */
    public static void sleepUntil(final long targetTimeMs) throws InterruptedException {
        if(simulating.get()) {
            try {
                synchronized(sleepTimesLock) {
                    threadSleepTimes.put(Thread.currentThread(), new AtomicLong(targetTimeMs));
                }
                while(simulatedCurrTimeMs.get().get() < targetTimeMs) {
                    Thread.sleep(10);
                }
            } finally {
                synchronized(sleepTimesLock) {
                    if (simulating.get()) {
                        threadSleepTimes.remove(Thread.currentThread());
                    }
                }
            }
        } else {
            final long sleepTime = targetTimeMs - System.currentTimeMillis();
            if(sleepTime > 0) {
                Thread.sleep(sleepTime);
            }
        }
    }

    /**
     * タイムスタンプを秒で取得する.
     * @return 現在のタイムスタンプ(s)
     */
    public static final int currentTimeSecs() {
        return (int) (System.currentTimeMillis() / 1000);
    }
}
