package jp.toastkid.libs.calendar;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;

/**
 * test {@link HtmlCalendar}.
 * @author Toast kid
 *
 */
public class HtmlCalendarTest {

    /** 2016/08/01. This month contains newest Japanese holiday. */
    private static final LocalDate AUGUST_2016 = LocalDate.of(2016, 8, 1);

    /**
     * test {@link HtmlCalendar#makeOneMonth(java.util.Calendar)}.
     */
    @Test
    public final void testMakeOneMonth() {
        final String oneMonth = HtmlCalendar.makeOneMonth(AUGUST_2016);
        System.out.println(oneMonth);
        assertTrue(800 < oneMonth.length());
        assertTrue(oneMonth.contains("<td class=\"holiday\" >11</td>"));
    }
}
