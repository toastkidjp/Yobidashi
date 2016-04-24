package jp.toastkid.libs.calendar;

import java.util.Calendar;

import jp.toastkid.libs.utils.Strings;

import org.eclipse.collections.api.set.primitive.IntSet;

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
    private static Calendar today = Calendar.getInstance();

    /** 今日を取得する. */
    private  static Calendar getToday() {
        return today;
    }

    /**
     * HTML 表現のカレンダーを返す.
     * <HR>
     * TODO 後で月移動をAJAX対応させておくこと
     * (121027) 作成<BR>
     */
    public static String makeOneMonth(final Calendar cal){
        final int year  = cal.get(Calendar.YEAR  );
        final int month = cal.get(Calendar.MONTH );
        cal.set(year, month, 1);
        final StringBuilder bui = new StringBuilder(2000);
        bui.append("<table border=\"0\" width=\"150\" class=\"calendar\" id=\"leftCal\">");
        bui.append("<tr>");
        bui.append("<th align=\"center\" colspan=\"7\" id=\"pageTitle\">").append(year)
            .append("年").append((month + 1)).append("月</th>");
        bui.append("</tr>");
        bui.append("<tr align=\"center\">");
        bui.append("<th><font color=\"red\">日</font></th><th>月</th><th>火</th><th>水</th>")
           .append("<th>木</th><th>金</th><th><font color=\"blue\">土</font></th>");
        bui.append("</tr>");
        bui.append(makeMonth( cal ));
        bui.append("</table>");
        return bui.toString();
    }

    /**
     * HTML 表現のカレンダー文字列を日付部分だけ生成して返す.
     * <HR>
     * (121021) JavaScript 版と Android 版のコードを流用して作成<BR>
     * @see <a href="http://www.syboos.jp/java/doc/get-fields-value-from-calendar.html">
     * java.util.Calendarよくある使い方 - 指定日時の年・月・日・時・分・秒の取得</a>
     * @param cal
     * @return HTML 表現のカレンダー文字列
     */
    private static String makeMonth( final Calendar cal ) {
        final int year  = cal.get(Calendar.YEAR  );
        final int month = cal.get(Calendar.MONTH );
        // その月の祝日を取得、祝日のない6月や8月は null が返ってくる.
        final IntSet holidaySet = JapaneseHoliday.findHolidaysIntSet(year, month);
        // 月の初めの曜日を求める
        cal.set(year, month , 1); // 引数: 1月: 0, 2月: 1, ...
        final int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        // 1=日曜..7=土曜
        int dayOfWeek = firstDayOfWeek;
        // 月末の日付を求める
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DATE, -1);
        final int endDayOfMonth = cal.get(Calendar.DATE);
        // 1週間分の文字列生成
        final StringBuilder weekBuild = new StringBuilder(1500);
        weekBuild.append("<tr>");
        // 第1週は1日まで空白で埋めておく
        for (int i = 1; i < dayOfWeek; i++) {
            weekBuild.append("<td></td>");
        }
        //Logger.info("" + getToday().get(Calendar.MONTH));
        final boolean isNowMonth = month == (getToday().get(Calendar.MONTH) + 1);

        for (int i = 1; i <= endDayOfMonth; i++) {
            weekBuild.append("<td " );
            final boolean isSaturday = dayOfWeek == Calendar.SATURDAY;
            if (dayOfWeek == Calendar.SUNDAY){
                weekBuild.append("class=\"sunday\" ");
                if (isNowMonth && i == getToday().get(Calendar.DAY_OF_MONTH) ){
                    weekBuild.append( "id=\"today\" " );
                }
            } else if (holidaySet != null && holidaySet.contains(i)){
                weekBuild.append("class=\"holiday\" "  );
            } else if (isSaturday){
                weekBuild.append("class=\"saturday\" " );
            }
            weekBuild.append(">").append(i).append("</td>");
            // 土曜日は改行
            if (isSaturday){
                 dayOfWeek = Calendar.SUNDAY - 1;
                 weekBuild.append("</tr>").append(Strings.LINE_SEPARATOR).append("<tr>");
            }
            dayOfWeek++;
        }
        return weekBuild.toString();
    }
}
