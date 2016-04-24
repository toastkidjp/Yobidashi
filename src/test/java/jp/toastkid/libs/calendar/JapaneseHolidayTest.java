package jp.toastkid.libs.calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.junit.Test;

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
                JapaneseHoliday.dateOfWeekSimple(new GregorianCalendar(2015, 0, 1).getTime())
                );
        assertEquals(
                "Thu",
                JapaneseHoliday.dateOfWeekSimple(new GregorianCalendar(2015, 0, 1).getTimeInMillis())
                );
    }
    /**
     * dateOfWeekJA() 's test.
     */
    @Test
    public void testDateOfWeekJA() {
        assertEquals(
                "木",
                JapaneseHoliday.dateOfWeekJA(new GregorianCalendar(2015, 0, 1).getTime())
                );
        assertEquals(
                "木",
                JapaneseHoliday.dateOfWeekJA(new GregorianCalendar(2015, 0, 1).getTimeInMillis())
                );
    }
    /**
     * getNationalHoliday() 's test.
     */
    @Test
    public final void testGetNationalHoliday() {
        assertTrue(Arrays.toString(
                JapaneseHoliday.getNationalHoliday(2009)).startsWith("[Tue Sep 22"));
        assertTrue(Arrays.toString(
                JapaneseHoliday.getNationalHoliday(2015)).startsWith("[Tue Sep 22"));
        assertNull(JapaneseHoliday.getNationalHoliday(2016));
    }
    /**
     * {@link JapaneseHoliday#isHoliday(Date)}'s test.
     */
    @Test
    public final void testIsHoliday(){
        assertTrue(
                JapaneseHoliday.isHoliday(new GregorianCalendar(2015, 0, 1).getTime())
                );
        assertFalse(JapaneseHoliday.isHoliday(new GregorianCalendar(2015, 0, 2).getTime()));
    }
    /**
     * {@link JapaneseHoliday#isHoliday(Calendar)}'s test.
     */
    @Test
    public final void testisHoliday(){
        assertTrue(JapaneseHoliday.isHoliday(new GregorianCalendar(2015, 0, 1)));
        assertTrue(JapaneseHoliday.isHoliday(new GregorianCalendar(2015, 0, 12)));
        assertTrue(JapaneseHoliday.isHoliday(new GregorianCalendar(2015, 1, 11)));
        assertTrue(JapaneseHoliday.isHoliday(new GregorianCalendar(2015, 2, 21)));
        assertTrue(JapaneseHoliday.isHoliday(new GregorianCalendar(2015, 3, 29)));
        assertTrue(JapaneseHoliday.isHoliday(new GregorianCalendar(2015, 4, 3)));
        assertTrue(JapaneseHoliday.isHoliday(new GregorianCalendar(2015, 4, 4)));
        assertTrue(JapaneseHoliday.isHoliday(new GregorianCalendar(2015, 4, 5)));
        assertTrue(JapaneseHoliday.isHoliday(new GregorianCalendar(2015, 4, 6)));
        assertTrue(JapaneseHoliday.isHoliday(new GregorianCalendar(2015, 6, 20)));
        assertTrue(JapaneseHoliday.isHoliday(new GregorianCalendar(2016, 7, 11)));
        assertTrue(JapaneseHoliday.isHoliday(new GregorianCalendar(2015, 8, 21)));
        assertTrue(JapaneseHoliday.isHoliday(new GregorianCalendar(2015, 8, 22)));
        assertTrue(JapaneseHoliday.isHoliday(new GregorianCalendar(2015, 8, 23)));
        assertTrue(JapaneseHoliday.isHoliday(new GregorianCalendar(2015, 9, 12)));
        assertTrue(JapaneseHoliday.isHoliday(new GregorianCalendar(2015, 10, 3)));
        assertTrue(JapaneseHoliday.isHoliday(new GregorianCalendar(2015, 10, 23)));
        assertTrue(JapaneseHoliday.isHoliday(new GregorianCalendar(2015, 11, 23)));
        assertFalse(JapaneseHoliday.isHoliday(new GregorianCalendar(2015, 7, 11)));
    }

    /**
     * {@link JapaneseHoliday#queryHoliday(Date)}'s test.
     */
    @Test
    public final void testQueryHoliday(){
        assertEquals(
                "元旦",
                JapaneseHoliday.queryHoliday(new GregorianCalendar(2015, 0, 1).getTime())
                );
        assertNull(JapaneseHoliday.queryHoliday(new GregorianCalendar(2015, 0, 2).getTime()));
        assertEquals(
                "成人の日",
                JapaneseHoliday.queryHoliday(new GregorianCalendar(2015, 0, 12).getTime())
                );
        assertEquals(
                "建国記念日",
                JapaneseHoliday.queryHoliday(new GregorianCalendar(2015, 1, 11).getTime())
                );
        assertEquals(
                "春分の日",
                JapaneseHoliday.queryHoliday(new GregorianCalendar(2015, 2, 21).getTime())
                );
        assertEquals(
                "昭和の日",
                JapaneseHoliday.queryHoliday(new GregorianCalendar(2015, 3, 29).getTime())
                );
        assertEquals(
                "憲法記念日",
                JapaneseHoliday.queryHoliday(new GregorianCalendar(2015, 4, 3).getTime())
                );
        assertEquals(
                "みどりの日",
                JapaneseHoliday.queryHoliday(new GregorianCalendar(2015, 4, 4).getTime())
                );
        assertEquals(
                "こどもの日",
                JapaneseHoliday.queryHoliday(new GregorianCalendar(2015, 4, 5).getTime())
                );
        assertEquals(
                "振替休日（憲法記念日）",
                JapaneseHoliday.queryHoliday(new GregorianCalendar(2015, 4, 6).getTime())
                );
        assertEquals(
                "海の日",
                JapaneseHoliday.queryHoliday(new GregorianCalendar(2015, 6, 20).getTime())
                );
        assertNull(JapaneseHoliday.queryHoliday(new GregorianCalendar(2015, 7, 11).getTime()));
        assertEquals(
                "山の日",
                JapaneseHoliday.queryHoliday(new GregorianCalendar(2016, 7, 11).getTime())
                );
        assertEquals(
                "敬老の日",
                JapaneseHoliday.queryHoliday(new GregorianCalendar(2015, 8, 21).getTime())
                );
        assertEquals(
                "国民の休日",
                JapaneseHoliday.queryHoliday(new GregorianCalendar(2015, 8, 22).getTime())
                );
        assertEquals(
                "秋分の日",
                JapaneseHoliday.queryHoliday(new GregorianCalendar(2015, 8, 23).getTime())
                );
        assertEquals(
                "体育の日",
                JapaneseHoliday.queryHoliday(new GregorianCalendar(2015, 9, 12).getTime())
                );
        assertEquals(
                "文化の日",
                JapaneseHoliday.queryHoliday(new GregorianCalendar(2015, 10, 3).getTime())
                );
        assertEquals(
                "勤労感謝の日",
                JapaneseHoliday.queryHoliday(new GregorianCalendar(2015, 10, 23).getTime())
                );
        assertEquals(
                "天皇誕生日",
                JapaneseHoliday.queryHoliday(new GregorianCalendar(2015, 11, 23).getTime())
                );
    }

    /**
     * check {@link JapaneseHoliday#findHolidaysSet(int, int)}.
     */
    @Test
    public final void  testFindHolidaysSet() {
        final Set<Integer> holidays = JapaneseHoliday.findHolidaysSet(2016, 4);
        assertTrue(holidays instanceof Set);
        assertEquals(3, holidays.size());
        assertEquals(Sets.fixedSize.of(3, 4, 5), holidays);

        // 6月は null でなく empty が返る.
        final Set<Integer> empty = JapaneseHoliday.findHolidaysSet(2016, 5);
        assertNotNull(empty);
        assertTrue(empty.isEmpty());

        // 1970年より前でもいける模様.
        final Set<Integer> preEpoch = JapaneseHoliday.findHolidaysSet(1970, 0);
        assertNotNull(preEpoch);
        assertEquals(2, preEpoch.size());
    }

    /**
     * check {@link JapaneseHoliday#findHolidaysSet(int, int)} when passed month over 12.
     */
    @Test(expected=IllegalArgumentException.class)
    public final void  testFindHolidaysSetIllegalMonth() {
        JapaneseHoliday.findHolidaysSet(2016, 25);
    }

    /**
     * check {@link JapaneseHoliday#findHolidaysSet(int, int)} when passed month = -1.
     */
    @Test(expected=IllegalArgumentException.class)
    public final void  testFindHolidaysSetIllegalMonthUnder() {
        JapaneseHoliday.findHolidaysSet(2016, -1);
    }

    /**
     * check {@link JapaneseHoliday#findHolidaysSet(int, int)}.
     */
    @Test
    public final void  testFindHolidaysIntSet() {
        final IntSet holidays = JapaneseHoliday.findHolidaysIntSet(2016, 4);
        assertTrue(holidays instanceof IntSet);
        assertEquals(3, holidays.size());
        assertEquals(IntSets.mutable.of(3, 4, 5), holidays);
    }
}
