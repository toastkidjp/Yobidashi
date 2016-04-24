package jp.toastkid.libs.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

/**
 * UNIXTIME とカレンダー関連の便利メソッドを収録しておくクラス.
 * <HR>
 * (130102) 作成<BR>
 * TODO write test.
 * @author Toast kid
 *
 */
public final class CalendarUtil {

    /** epub の日付フォーマット. */
    public static final String DATE_FORMAT_STR = "yyyy-MM-dd'T'HH:mm:ssZ";

    /**
     * 引数として渡された UNIX 時間(long型、ミリ秒)から、
     * 「2012-05-11(金)」のような形式をした年月日表現(年/月/日/曜日)を取得する.
     * <HR>
     * (130102) 作成<BR>
     * @param unixMilliSec (ミリ秒)
     * @return 「2012-05-11(金)」のような形式をした年月日表現
     */
    public static String longToStr(final long unixMilliSec) {
        return longToStr(unixMilliSec, "yyyy-MM-dd(E)");
    }

    /**
     * 引数として渡された UNIX 時間から、
     * 第2引数で指定した形式の年月日表現(年/月/日/曜日)を取得する.
     * <HR>
     * (130102) 作成<BR>
     * @param unixMilliSec UNIXTIME をミリ秒に直した long 値
     * @param format (例) "yyyy-MM-dd(E)"
     * @return 2012-05-11(金)」のような形式をした年月日表現
     */
    public static String longToStr(
            final long unixMilliSec,
            final String format
            ) {
        final SimpleDateFormat fmt1 = new SimpleDateFormat( format );
        final Calendar cal = longToCalendar(unixMilliSec);
        return fmt1.format(cal.getTime());
    }

    /**
     * long 型の UNIX 時間(ミリ秒、秒で取得している場合は * 1000lして渡すこと)から
     * Calendar 型のオブジェクトを生成して返す.
     * <HR>
     * (130102) 作成<BR>
     * @param unixMilliSec long 型の UNIX 時間(ミリ秒、秒で取得している場合は * 1000lして渡すこと)
     * @return Calendar 型のオブジェクト
     */
    public static Calendar longToCalendar(final long unixMilliSec) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(unixMilliSec);
        return cal;
    }

    /**
     * Calendar 型のオブジェクトから「2012-05-11(金)」のような形式をした年月日表現を生成して返す.
     * <HR>
     * (130102) 作成<BR>
     * @param cal Calendar 型のオブジェクト
     * @return 「2012-05-11(金)」のような形式をした年月日表現
     */
    public static String calendarToFormated(final Calendar cal) {
        return longToStr(cal.getTimeInMillis());
    }

    /**
     * Calendar 型のオブジェクトから「2012-05-11(金)」のような形式をした年月日表現を生成して返す.
     * 第2引数で指定した形式の年月日表現(年/月/日/曜日)を取得する.
     * <HR>
     * (130102) 作成<BR>
     * @param cal Calendar 型のオブジェクト
     * @param format (例) "yyyy-MM-dd(E)"
     * @return 年月日表現
     */
    public static String calendarToFormated(
            final Calendar cal,
            final String format
            ) {
        return longToStr(cal.getTimeInMillis(), format);
    }

    /**
     * 現在時刻の ISO Date (yyyy-MM-dd'T'HH:mm:ssZ) での文字列表現を返す.
     * @return ISO Date (yyyy-MM-dd'T'HH:mm:ssZ)
     */
    public static String getCurrentISODate() {
        return CalendarUtil.longToStr(System.currentTimeMillis(), DATE_FORMAT_STR);
    }

    /**
     * convert ZonedDateTime to millisecond(long).
     * @param zdt ZonedDateTime Object.
     * @return millisecond(long)
     */
    public static long zoneDateTime2long(final ZonedDateTime zdt) {
        return zdt.toInstant().toEpochMilli();
    }

    /**
     * ms -> LocalDate object.
     * @param ms millisecond
     * @return LocalDate
     */
    public static LocalDate Zmd2LocalDate(final long ms) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.systemDefault())
                .toLocalDate();
    }
}
