package jp.toastkid.libs.calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import jp.toastkid.libs.utils.CalendarUtil;

/**
 * JapaneseHoliday's test.
 * @author Toast kid
 *
 */
public class JapaneseHolidayTest {
    /**
     * dateOfWeekSimple() 's test.
     */
    @Test
    public void testDateOfWeekSimple() {
        assertEquals(
                        "Thu",
                        JapaneseHoliday.dateOfWeekSimple(LocalDate.of(2015, 1, 1))
                        );
        assertEquals(
                        "Thu",
                        JapaneseHoliday.dateOfWeekSimple(CalendarUtil.localDate2ms(LocalDate.of(2015, 1, 1)))
                        );
    }
    /**
     * dateOfWeekJA() 's test.
     */
    @Test
    public void testDateOfWeekJA() {
        assertEquals(
                        "木",
                        JapaneseHoliday.dateOfWeekJA(LocalDate.of(2015, 1, 1))
                        );
        assertEquals(
                        "木",
                        JapaneseHoliday.dateOfWeekJA(CalendarUtil.localDate2ms(LocalDate.of(2015, 1, 1)))
                        );
    }
    /**
     * getNationalHoliday() 's test.
     */
    @Test
    public final void testGetNationalHoliday() {
        assertEquals("[2009-09-22]", JapaneseHoliday.getNationalHoliday(2009).toString());
        assertEquals("[2015-09-22]", JapaneseHoliday.getNationalHoliday(2015).toString());
        assertTrue(JapaneseHoliday.getNationalHoliday(2016).isEmpty());
    }
    /**
     * {@link JapaneseHoliday#isHoliday(LocalDate)}'s test.
     */
    @Test
    public final void testIsHoliday(){
        assertTrue(
                        JapaneseHoliday.isHoliday(LocalDate.of(2015, 1, 1))
                        );
        assertFalse(JapaneseHoliday.isHoliday(LocalDate.of(2015, 1, 2)));
    }
    /**
     * {@link JapaneseHoliday#isHoliday()}'s test.
     */
    @Test
    public final void testisHoliday(){
        assertTrue(JapaneseHoliday.isHoliday(LocalDate.of(2015, 1, 1)));
        assertTrue(JapaneseHoliday.isHoliday(LocalDate.of(2015, 1, 12)));
        assertTrue(JapaneseHoliday.isHoliday(LocalDate.of(2015, 2, 11)));
        assertTrue(JapaneseHoliday.isHoliday(LocalDate.of(2015, 3, 21)));
        assertTrue(JapaneseHoliday.isHoliday(LocalDate.of(2015, 4, 29)));
        assertTrue(JapaneseHoliday.isHoliday(LocalDate.of(2015, 5, 3)));
        assertTrue(JapaneseHoliday.isHoliday(LocalDate.of(2015, 5, 4)));
        assertTrue(JapaneseHoliday.isHoliday(LocalDate.of(2015, 5, 5)));
        assertTrue(JapaneseHoliday.isHoliday(LocalDate.of(2015, 5, 6)));
        assertTrue(JapaneseHoliday.isHoliday(LocalDate.of(2015, 7, 20)));
        assertTrue(JapaneseHoliday.isHoliday(LocalDate.of(2016, 8, 11)));
        assertTrue(JapaneseHoliday.isHoliday(LocalDate.of(2015, 9, 21)));
        assertTrue(JapaneseHoliday.isHoliday(LocalDate.of(2015, 9, 22)));
        assertTrue(JapaneseHoliday.isHoliday(LocalDate.of(2015, 9, 23)));
        assertTrue(JapaneseHoliday.isHoliday(LocalDate.of(2015, 10, 12)));
        assertTrue(JapaneseHoliday.isHoliday(LocalDate.of(2015, 11, 3)));
        assertTrue(JapaneseHoliday.isHoliday(LocalDate.of(2015, 11, 23)));
        assertTrue(JapaneseHoliday.isHoliday(LocalDate.of(2015, 12, 23)));
    }

    /**
     * {@link JapaneseHoliday#queryHoliday(LocalDate)}'s test.
     */
    @Test
    public final void testQueryHoliday(){
        assertEquals(
                        "元旦",
                        JapaneseHoliday.queryHoliday(LocalDate.of(2015, 1, 1))
                        );
        assertNull(JapaneseHoliday.queryHoliday(LocalDate.of(2015, 1, 2)));
        assertEquals(
                        "成人の日",
                        JapaneseHoliday.queryHoliday(LocalDate.of(2015, 1, 12))
                        );
        assertEquals(
                        "建国記念日",
                        JapaneseHoliday.queryHoliday(LocalDate.of(2015, 2, 11))
                        );
        assertEquals(
                        "春分の日",
                        JapaneseHoliday.queryHoliday(LocalDate.of(2015, 3, 21))
                        );
        assertEquals(
                        "昭和の日",
                        JapaneseHoliday.queryHoliday(LocalDate.of(2015, 4, 29))
                        );
        assertEquals(
                        "憲法記念日",
                        JapaneseHoliday.queryHoliday(LocalDate.of(2015, 5, 3))
                        );
        assertEquals(
                        "みどりの日",
                        JapaneseHoliday.queryHoliday(LocalDate.of(2015, 5, 4))
                        );
        assertEquals(
                        "こどもの日",
                        JapaneseHoliday.queryHoliday(LocalDate.of(2015, 5, 5))
                        );
        assertEquals(
                        "振替休日（憲法記念日）",
                        JapaneseHoliday.queryHoliday(LocalDate.of(2015, 5, 6))
                        );
        assertEquals(
                        "海の日",
                        JapaneseHoliday.queryHoliday(LocalDate.of(2015, 7, 20))
                        );
        assertNull(JapaneseHoliday.queryHoliday(LocalDate.of(2015, 8, 11)));
        assertEquals(
                        "山の日",
                        JapaneseHoliday.queryHoliday(LocalDate.of(2016, 8, 11))
                        );
        assertEquals(
                        "敬老の日",
                        JapaneseHoliday.queryHoliday(LocalDate.of(2015, 9, 21))
                        );
        assertEquals(
                        "国民の休日",
                        JapaneseHoliday.queryHoliday(LocalDate.of(2015, 9, 22))
                        );
        assertEquals(
                        "秋分の日",
                        JapaneseHoliday.queryHoliday(LocalDate.of(2015, 9, 23))
                        );
        assertEquals(
                        "体育の日",
                        JapaneseHoliday.queryHoliday(LocalDate.of(2015, 10, 12))
                        );
        assertEquals(
                        "文化の日",
                        JapaneseHoliday.queryHoliday(LocalDate.of(2015, 11, 3))
                        );
        assertEquals(
                        "勤労感謝の日",
                        JapaneseHoliday.queryHoliday(LocalDate.of(2015, 11, 23))
                        );
        assertEquals(
                        "天皇誕生日",
                        JapaneseHoliday.queryHoliday(LocalDate.of(2015, 12, 23))
                        );
    }

    /**
     * Check {@link JapaneseHoliday#findHolidaysSet(int, int)}.
     */
    @Test
    public final void  testFindHolidaysSet() {
        final Set<Integer> holidays = JapaneseHoliday.findHolidays(2016, 5);
        assertEquals(3, holidays.size());
        assertEquals(new HashSet<Integer>(Arrays.asList(3, 4, 5)), holidays);

        // 6月は null でなく empty が返る.
        final Set<Integer> empty = JapaneseHoliday.findHolidays(2016, 6);
        assertNotNull(empty);
        assertTrue(empty.isEmpty());

        // 1970年より前でもいける模様.
        final Set<Integer> preEpoch = JapaneseHoliday.findHolidays(1970, 1);
        assertNotNull(preEpoch);
        assertEquals(2, preEpoch.size());
    }

    /**
     * Check {@link JapaneseHoliday#findHolidaysSet(int, int)} when passed month over 12.
     */
    @Test(expected=IllegalArgumentException.class)
    public final void  testFindHolidaysSetIllegalMonth() {
        JapaneseHoliday.findHolidays(2016, 25);
    }

    /**
     * Check {@link JapaneseHoliday#findHolidaysSet(int, int)} when passed month = -1.
     */
    @Test(expected=IllegalArgumentException.class)
    public final void  testFindHolidaysSetIllegalMonthUnder() {
        JapaneseHoliday.findHolidays(2016, -1);
    }

    /**
     * Check {@link JapaneseHoliday#findHolidaysSet(int, int)}.
     */
    @Test
    public final void  testFindHolidaysIntSet() {
        final Set<Integer> holidays = JapaneseHoliday.findHolidays(2016, 5);
        assertEquals(3, holidays.size());
        assertEquals(new HashSet<Integer>(Arrays.asList(3, 4, 5)), holidays);
    }

    /**
     * Check {@link JapaneseHoliday#listHoliDayDates(int, int)}.
     */
    @Test
    public void test_listHoliDayDates() {
        final Set<LocalDate> holiDayDates = JapaneseHoliday.listHoliDayDates(2015, 9);
        assertEquals("[2015-09-21, 2015-09-23, 2015-09-22]", holiDayDates.toString());
    }

    /**
     * Check {@link JapaneseHoliday#listHoliDayDates(int, int)}'s irregular case.
     */
    @Test(expected=IllegalArgumentException.class)
    public void test_listHoliDayDates_minus_month() {
        JapaneseHoliday.listHoliDayDates(2017, -1);
    }

    /**
     * Check {@link JapaneseHoliday#listHoliDayDates(int, int)}'s irregular case.
     */
    @Test(expected=IllegalArgumentException.class)
    public void test_listHoliDayDates_zeo_month() {
        JapaneseHoliday.listHoliDayDates(2017, 0);
    }

    /**
     * Check {@link JapaneseHoliday#listHoliDayDates(int, int)}'s irregular case.
     */
    @Test(expected=IllegalArgumentException.class)
    public void test_listHoliDayDates_over_month() {
        JapaneseHoliday.listHoliDayDates(2017, 13);
    }

    /**
     * Check {@link JapaneseHoliday#isHoliday(long)}.
     */
    @Test
    public void test_isHoliday() {
        assertFalse(JapaneseHoliday.isHoliday(1442674800000L));
        assertFalse(JapaneseHoliday.isHoliday(1442761199999L));
        assertTrue(JapaneseHoliday.isHoliday(1442761200000L));
        assertTrue(JapaneseHoliday.isHoliday(1443020399999L));
        assertFalse(JapaneseHoliday.isHoliday(1443020400000L));

    }
}
