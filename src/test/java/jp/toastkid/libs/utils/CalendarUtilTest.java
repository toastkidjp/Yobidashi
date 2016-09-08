package jp.toastkid.libs.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.Test;

/**
 * {@link CalendarUtil}'s test cases.
 *
 * @author Toast kid
 *
 */
public class CalendarUtilTest {

    /**
     * {@link CalendarUtil#longToStr(long)}'s test case.
     */
    @Test
    public void testLongToStrLong() {
        assertEquals("2016-09-08(木)", CalendarUtil.longToStr(1473341216746L));
    }

    /**
     * {@link CalendarUtil#longToStr(long, String)}'s test case.
     */
    @Test
    public void testLongToStrLongString() {
        assertEquals("2016-09-08(木) 22:26:56",
                CalendarUtil.longToStr(1473341216746L, "yyyy-MM-dd(E) HH:mm:ss"));
    }

    /**
     * {@link CalendarUtil#getCurrentISODate()}'s test case.
     */
    @Test
    public void testGetCurrentISODate() {
        final String isoDate = CalendarUtil.getCurrentISODate();
        assertEquals(24, isoDate.length());
        assertTrue(isoDate.endsWith("+0900"));
    }

    /**
     * {@link CalendarUtil#zoneDateTime2long(ZonedDateTime)}'s test case.
     */
    @Test
    public void testZoneDateTime2long() {
        assertEquals(1456716059000L, CalendarUtil.zoneDateTime2long(
                ZonedDateTime.of(2016, 2, 29, 12, 20, 59, 0, ZoneId.of("Asia/Tokyo"))));
    }

    /**
     * {@link CalendarUtil#ms2LocalDate(long)}'s test case.
     */
    @Test
    public void testMs2LocalDate() {
        assertEquals(LocalDate.of(2016, 9, 8), CalendarUtil.ms2LocalDate(1473341216746L));
    }

    /**
     * {@link CalendarUtil#ms2LocalDateTime(long)}'s test case.
     */
    @Test
    public void testMs2LocalDateTime() {
        assertEquals(LocalDateTime.of(2016, 9, 8, 22, 26, 56, 746000000),
                CalendarUtil.ms2LocalDateTime(1473341216746L));
    }

    /**
     * {@link CalendarUtil#ms2ZonedDateTime(long)}'s test case.
     */
    @Test
    public void testMs2ZonedDateTime() {
        assertEquals(ZonedDateTime.of(2016, 9, 8, 22, 26, 56, 746000000, ZoneId.of("Asia/Tokyo")),
                CalendarUtil.ms2ZonedDateTime(1473341216746L));
    }

    /**
     * {@link CalendarUtil#ms2OffsetDateTime(long)}'s test case.
     */
    @Test
    public void testMs2OffsetDateTime() {
        assertEquals(OffsetDateTime.of(2016, 9, 8, 22, 26, 56, 746000000, ZoneOffset.of("+09:00")),
                CalendarUtil.ms2OffsetDateTime(1473341216746L));
    }

}
