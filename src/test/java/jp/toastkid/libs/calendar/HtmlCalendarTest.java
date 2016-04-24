package jp.toastkid.libs.calendar;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.Test;

/**
 * test {@link HtmlCalendar}.
 * @author Toast kid
 *
 */
public class HtmlCalendarTest {

    /** 2016/08/01. This month contains newest Japanese holiday. */
    private static final Calendar AUGUST_2016 = new GregorianCalendar(2016, 7, 1);

    /**
     * test {@link HtmlCalendar#makeOneMonth(java.util.Calendar)}.
     */
    @Test
    public final void testMakeOneMonth() {
        final String oneMonth = HtmlCalendar.makeOneMonth(AUGUST_2016);
        assertTrue(800 < oneMonth.length());
        assertTrue(oneMonth.contains("<td class=\"holiday\" >11</td>"));
    }
}
