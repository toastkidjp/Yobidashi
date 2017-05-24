package jp.toastkid.libs.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import jp.toastkid.libs.utils.Strings;

/**
 * Html のカレンダーを生成.
 * <HR>
 * (130129) 0月表示を修正<BR>
 * (130129) 1月29日に2月のカレンダーが3月化するバグを修正<BR>
 * (121021) 作成開始<BR>
 * @author Toast kid
 * @see JapaneseHoliday
 *
 */
public abstract class HtmlCalendar {

    /** 今日を示す Calendar Object. 当日判定に利用. */
    private static LocalDateTime today = LocalDateTime.now();

    /** 今日を取得する. */
    private  static LocalDateTime getToday() {
        return today;
    }

    /**
     * HTML 表現のカレンダーを返す.
     * <HR>
     * (121027) 作成<BR>
     */
    public static String makeOneMonth(final LocalDate ld){
        final int year  = ld.getYear();
        final int month = ld.getMonthValue();
        final StringBuilder bui = new StringBuilder(2000);
        bui.append("<table border=\"0\" width=\"150\" class=\"calendar\" id=\"leftCal\">");
        bui.append("<tr>");
        bui.append("<th align=\"center\" colspan=\"7\" id=\"pageTitle\">").append(year)
            .append("年").append(month).append("月</th>");
        bui.append("</tr>");
        bui.append("<tr align=\"center\">");
        bui.append("<th><font color=\"red\">日</font></th><th>月</th><th>火</th><th>水</th>")
           .append("<th>木</th><th>金</th><th><font color=\"blue\">土</font></th>");
        bui.append("</tr>");
        bui.append(makeMonth( ld ));
        bui.append("</table>");
        return bui.toString();
    }

    /**
     * HTML 表現のカレンダー文字列を日付部分だけ生成して返す.
     * <HR>
     * (121021) JavaScript 版と Android 版のコードを流用して作成<BR>
     * @see <a href="http://www.syboos.jp/java/doc/get-fields-value-from-calendar.html">
     * java.util.Calendarよくある使い方 - 指定日時の年・月・日・時・分・秒の取得</a>
     * @param ld
     * @return HTML 表現のカレンダー文字列
     */
    private static String makeMonth( final LocalDate ld ) {
        final int year  = ld.getYear();
        final int month = ld.getMonthValue();
        // その月の祝日を取得、祝日のない6月や8月は null が返ってくる.
        final Set<Integer> holidaySet = JapaneseHoliday.findHolidays(year, month);
        // 月の初めの曜日を求める
        final int firstDayOfWeek = LocalDate.of(year, month, 1).getDayOfWeek().getValue();
        // 1=日曜..7=土曜
        int dayOfWeek = firstDayOfWeek;
        // 月末の日付を求める
        final int endDayOfMonth = LocalDate.of(year, month + 1, 1).minusDays(1).getDayOfMonth();
        // 1週間分の文字列生成
        final StringBuilder weekBuild = new StringBuilder(1500);
        weekBuild.append("<tr>");
        // 第1週は1日まで空白で埋めておく
        for (int i = 1; i < dayOfWeek; i++) {
            weekBuild.append("<td></td>");
        }
        final boolean isCurrentMonth = month == (getToday().getMonthValue());

        for (int i = 1; i <= endDayOfMonth; i++) {
            weekBuild.append("<td " );
            final boolean isSaturday = dayOfWeek == DayOfWeek.SATURDAY.getValue();
            if (dayOfWeek == DayOfWeek.SUNDAY.getValue()){
                weekBuild.append("class=\"sunday\" ");
                if (isCurrentMonth && i == getToday().getDayOfMonth() ){
                    weekBuild.append( "id=\"today\" " );
                }
                // 最後に1足すので
                dayOfWeek = DayOfWeek.MONDAY.getValue() - 1;
            } else if (holidaySet != null && holidaySet.contains(i)){
                weekBuild.append("class=\"holiday\" "  );
            } else if (isSaturday){
                weekBuild.append("class=\"saturday\" " );
            }
            weekBuild.append(">").append(i).append("</td>");
            // 土曜日は改行
            if (isSaturday){
                 weekBuild.append("</tr>").append(Strings.LINE_SEPARATOR).append("<tr>");
            }
            dayOfWeek++;
        }
        return weekBuild.toString();
    }
}
