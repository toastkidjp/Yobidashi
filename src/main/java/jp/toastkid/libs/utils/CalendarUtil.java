package jp.toastkid.libs.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class of UNIXTIME.
 *
 * @author Toast kid
 */
public final class CalendarUtil {

    /**
     * 書式 = yyyy/M/dd(E) HH:mm:ss..
     */
    private static final DateTimeFormatter UNI_DF_HOLDER
        = DateTimeFormatter.ofPattern("yyyy/M/dd(E) HH:mm:ss");

    /** ISO DATE format. It's used in ePub generator. */
    private static final DateTimeFormatter ISO_DATE
        = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    /**
     * Private constructor.
     */
    private CalendarUtil() {
        // NOP.
    }

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
        return ms2LocalDateTime(unixMilliSec).format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * 現在時刻の ISO Date (yyyy-MM-dd'T'HH:mm:ssZ) での文字列表現を返す.
     * @return ISO Date (yyyy-MM-dd'T'HH:mm:ssZ)
     */
    public static String getCurrentISODate() {
        return ms2OffsetDateTime(System.currentTimeMillis()).format(ISO_DATE);
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
     * LocalDate to ms.
     * @param ld
     * @return ms
     */
    public static long localDate2ms(final LocalDate ld) {
        return ld.atStartOfDay().toEpochSecond(ZoneOffset.ofHours(9)) * 1000L;
    }

    /**
     * ms -> LocalDate object.
     * @param ms millisecond
     * @return LocalDate
     */
    public static LocalDate ms2LocalDate(final long ms) {
        return LocalDateTime
                .ofInstant(Instant.ofEpochMilli(ms), ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * ms -> LocalDateTime object.
     * @param ms millisecond
     * @return LocalDateTime
     */
    public static LocalDateTime ms2LocalDateTime(final long ms) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.systemDefault());
    }

    /**
     * ms -> ZonedDateTime object.
     * @param ms millisecond
     * @return LocalDateTime
     */
    public static ZonedDateTime ms2ZonedDateTime(final long ms) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.systemDefault());
    }

    /**
     * ms -> OffsetDateTime object.
     * @param ms millisecond
     * @return LocalDateTime
     */
    public static OffsetDateTime ms2OffsetDateTime(final long ms) {
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.systemDefault());
    }

    /**
     * 現在日時(書式 = yyyy/M/dd HH:mm:ss(E))を取得する.
     * @param ms
     * @return 現在時刻の文字列表現
     */
    public static String toUniTypeDate(final long ms) {
        return toUniTypeDate(
                LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.systemDefault()));
    }

    /**
     * 現在日時(書式 = yyyy/M/dd HH:mm:ss(E))を取得する.
     * @param date
     * @return 現在時刻の文字列表現
     */
    public static String toUniTypeDate(final LocalDateTime date) {
        return UNI_DF_HOLDER.format(date).toString();
    }

}
