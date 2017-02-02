package jp.toastkid.libs.calendar;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.primitive.IntSets;

import jp.toastkid.libs.utils.CalendarUtil;

/**
 * 祝日計算クラス.
 * 2007年以降のみ有効
 * 春分・秋分の日は、『海上保安庁水路部 暦計算研究会編 新こよみ便利帳』による計算式に
 * よる結果を求めてるにすぎない.毎年の官報公示の決定と異なったら官報公示に従うこと.
 * 年間の祝日リスト(国民の休日を含む）（配列）の算出、
 * 年と月を指定して対象月の祝日(国民の休日を含む）の算出、
 * 指定日付の祝日判定、
 * 指定する祝日の日付の取得を目的とする.
 *
 * 任意の祝日を指定して情報を取得するために、
 *     public abstract class HolidayBundle を提供している.
 * この抽象クラスの具象クラスとして祝日ごとのクラスが用意されており、祝日名、祝日計算は
 * 個々の祝日HolidayBundle で実装する.
 * 振替休日計算は、HolidayBundle 抽象クラスで実装するが、特定の祝日は具象クラスで
 * オーバーライドで実装する.
 * 【使用例】
 *     // 2009年の祝日配列
 *         Holiday holday = new Holiday(2009);  // コンストラクタを使用すること
 *         Date[] ary = holday.listHoliDays();
 *         for(int i=0;i < ary.length;i++){
 *            System.out.println(Holiday.YMD_FORMAT.format(ary[i])
 *                          +"\t"+Holiday.dateOfWeekJA(ary[i])
 *                          +"\t"+Holiday.queryHoliday(ary[i]));
 *         }
 *     // 2009年の９月の祝日、
 *         int[] days = Holiday.listHoliDays(2009,Calendar.SEPTEMBER);
 *         Date[] dts = Holiday.listHoliDayDates(2009,Calendar.SEPTEMBER);
 *     // 指定日付の祝日判定
 *        String target = "2009/05/06";
 *        String res = Holiday.queryHoliday(Holiday.YMD_FORMAT.parse(target));
 *        System.out.println(res);
 *     // 指定する祝日の日付の取得
 *     //   Holiday.HolidayType enum から、getBundleメソッドで、Holiday.HolidayBundle
 *     //   を取得してHoliday.HolidayBundleが提供するメソッドを利用する
 *          //    Holiday.HolidayBundle#getMonth()       → 月
 *          //    Holiday.HolidayBundle#getDay()         → 日
 *          //    Holiday.HolidayBundle#getDescription() → 祝日名
 *          //    Holiday.HolidayBundle#getDate()        → 祝日のDate
 *          //    Holiday.HolidayBundle#getChangeDay()   → 振替休日ある場合のDay
 *          //    Holiday.HolidayBundle#getChangeDate()  → 振替休日ある場合のDate
 *       // 2009年の春分の日
 *          Holiday.HolidayBundle h = Holiday.HolidayType.SPRING_EQUINOX_DAY.getBundle(2009);
 *          System.out.println(h.getMonth()+"月 "+h.getDay()+"日"
 *                              +"（"+Holiday.WEEKDAYS_JA[h.getWeekDay()-1]+"）"
 *                              +" "+h.getDescription());
 *     // 指定年→国民の休日のみのDate[]の取得
 *          // 2009年の国民の休日配列
 *          Date[] ds = Holiday.getNatinalHoliday(2009);
 *          for(int i=0;i < ds.length;i++){
 *             System.out.println(Holiday.YMD_FORMAT.format(ds[i])+"-->"+Holiday.queryHoliday(ds[i]));
 *          }
 * @see <a href="http://sourceforge.jp/projects/jholiday/">Java祝日計算</a>
 * @author Toast kid
 */
public final class JapaneseHoliday{

    private static final int SEPTEMBER = Month.SEPTEMBER.getValue();

    private static final int SUNDAY = DayOfWeek.SUNDAY.getValue();

    private static final int MONDAY = DayOfWeek.MONDAY.getValue();

    private ImmutableSet<LocalDate> holidayDates;

   /** HolidayType は、祝日タイプ→HolidayBundle class を紐付ける enum */
   public enum HolidayType{
      /** 元旦        ：１月１日            */  NEWYEAR_DAY             (NewYearDayBundle.class)
      /** 成人の日    ：１月の第２月曜日    */ ,COMING_OF_AGE_DAY       (ComingOfAgeDayBundle.class)
      /** 建国記念日  ：２月１１日          */ ,NATIONAL_FOUNDATION_DAY (NatinalFoundationBundle.class)
      /** 春分の日    ：３月 官報公示で決定 */ ,SPRING_EQUINOX_DAY      (SpringEquinoxBundle.class)
      /** 昭和の日    ：４月２９日          */ ,SHOUWA_DAY              (ShowaDayBundle.class)
      /** 憲法記念日  ：５月３日            */ ,KENPOUKINEN_DAY         (KenpoukikenDayBundle.class)
      /** みどりの日  ：５月４日            */ ,MIDORI_DAY              (MidoriDayBundle.class)
      /** こどもの日  ：５月５日            */ ,KODOMO_DAY              (KodomoDayBundle.class)
      /** 海の日      ：７月の第３月曜日    */ ,SEA_DAY                 (SeaDayBundle.class)
      /** 山の日      ：８月１１日(2016年-  */ ,MOUNTAIN_DAY            (MountainDayBundle.class)
      /** 敬老の日    ：９月の第３月曜      */ ,RESPECT_FOR_AGE_DAY     (RespectForAgeDayBundle.class)
      /** 秋分の日    ：９月 官報公示で決定 */ ,AUTUMN_EQUINOX_DAY      (AutumnEquinoxBundle.class)
      /** 体育の日    ：１０月の第２月曜日  */ ,HEALTH_SPORTS_DAY       (HealthSportsDayBundle.class)
      /** 文化の日    ：１１月３日          */ ,CULTURE_DAY             (CultureDayBundle.class)
      /** 勤労感謝の日：１１月２３日        */ ,LABOR_THANKS_DAY        (LaborThanksDayBundle.class)
      /** 天皇誕生日  ：１２月２３日        */ ,TENNO_BIRTHDAY          (EmperorBirthDayBundle.class)
      ;

      private final Class<? extends HolidayBundle> cls;

      private HolidayType(final Class<? extends HolidayBundle> cls){
         this.cls = cls;
      }

      public HolidayBundle getBundle(final int year){
          Constructor<?> ct;
          HolidayBundle holBand = null;
          try {
              ct = this.cls.getDeclaredConstructor(JapaneseHoliday.class,int.class);
              holBand = (HolidayBundle)ct.newInstance(null,year);
              } catch (final SecurityException e1) {
                  e1.printStackTrace();
                  return null;
              } catch (final NoSuchMethodException e1) {
                  e1.printStackTrace();
                  return null;
              } catch (final IllegalArgumentException e) {
                  e.printStackTrace();
              } catch (final InstantiationException e) {
                  e.printStackTrace();
              } catch (final IllegalAccessException e) {
                  e.printStackTrace();
              } catch (final InvocationTargetException e) {
                  e.printStackTrace();
              }
              return holBand;
      }
   }
   // 月→HolidayBundle class 参照 enum
   enum MonthBundle{
      JANUARY       (NewYearDayBundle.class,ComingOfAgeDayBundle.class)
      ,FEBRUARY     (NatinalFoundationBundle.class)
      ,MARCH        (SpringEquinoxBundle.class)
      ,APRIL        (ShowaDayBundle.class)
      ,MAY          (KenpoukikenDayBundle.class,MidoriDayBundle.class,KodomoDayBundle.class)
      ,JUNE         ()
      ,JULY         (SeaDayBundle.class)
      ,AUGUST       (MountainDayBundle.class)
      ,SEPTEMBER    (RespectForAgeDayBundle.class,AutumnEquinoxBundle.class)
      ,OCTOBER      (HealthSportsDayBundle.class)
      ,NOVEMBER     (CultureDayBundle.class,LaborThanksDayBundle.class)
      ,DECEMBER     (EmperorBirthDayBundle.class)
      ;
      //
      private Constructor<?>[] constructors;
      MonthBundle(final Class<?>...clss) {
         if (clss.length > 0){
            this.constructors = new Constructor<?>[clss.length];
            for(int i=0;i < clss.length;i++){
               try{
               this.constructors[i] = clss[i].getDeclaredConstructor(JapaneseHoliday.class,int.class);
               }catch(final Exception e){}
            }
         }
      }
      Constructor<?>[] getConstructors(){
         return this.constructors;
      }
   }
   /** 祝日Bundle抽象クラス */
   public abstract class HolidayBundle{
      int year;
      private final LocalDate ld;
      public abstract int getDay();
      public abstract int getMonth();
      public abstract String getDescription();

      /**
       * 対象年を指定するコンストラクタ
       * @param year 西暦４桁
       */
      public HolidayBundle(final int year){
         this.year = year;
         this.ld = LocalDate.of(this.year, this.getMonth(), this.getDay());
      }

      /** 振替休日の存在する場合、振替休日の日を返す.存在しない場合→ -1 を返す.*/
      public int getChangeDay(){
          return this.getWeekDay() == SUNDAY
                  ? LocalDate.of(this.year, this.getMonth(), this.getDay())
                          .plusDays(1L)
                          .getMonth()
                          .getValue()
                  : -1;
      }

      /** 振替休日の存在する場合、振替休日のDateを返す.存在しない場合→ null を返す.*/
      public LocalDate getChangeDate(){
         if (this.getWeekDay() == SUNDAY){
            return LocalDate.of(this.year, getMonth(), getDay()).plusDays(1L);
         }
         return null;
      }

      /** 祝日の曜日を Calendar.DAY_OF_WEEK に従って求める */
      public int getWeekDay(){
         return this.ld.getDayOfWeek().getValue();
      }

      /** 祝日の Date を取得 */
      public LocalDate getDate(){
         return this.ld;
      }
   }

   /**
    * Return specified year and month' holiday dates.
    * @param year
    * @param month
    * @return
    */
   public static IntSet findHolidays(final int year, final int month) {
       return listHolidays(year, month);
   }

   /**
    * 指定年、月の祝日、振替休日、国民の休日、日付(int)Setで返す
    * @param year 西暦４桁
    * @param month 月
    * @return java.util.Calendar.DAY_OF_MONTH である配列
    */
   public static IntSet listHolidays(final int year, final int month){
      if (month < 1 || 12 < month){
         throw new IllegalArgumentException("month parameter Error");
      }
      final MonthBundle mb = MonthBundle.valueOf(MONTH_NAMES[month]);
      final Constructor<?>[] constructors = mb.getConstructors();

      final MutableIntSet set = IntSets.mutable.empty();
      if (constructors == null) {
          return set;
      }
      for(int i=0;i < constructors.length;i++){
         try{
             final HolidayBundle b = (HolidayBundle)constructors[i].newInstance(null,year);
             set.add(b.getDay());
             final int chgday = b.getChangeDay();
             if (chgday > 0) set.add(chgday);
         }catch(final Exception e){
             e.printStackTrace();
         }
      }
      // 現在、国民の休日の発生は９月しかない
      if (month == SEPTEMBER){
         set.addAll(getNationalHoliday(year).collectInt(ld -> ld.getDayOfMonth()));
      }
      return set;
   }

   /**
    * 指定年、月の祝日、振替休日、国民の休日、日付(Date)配列で返す
    * @param year 西暦４桁
    * @param month month
    * @return Date配列
    */
   public static Set<LocalDate> listHoliDayDates(final int year, final int month){
      if (month < 1 || 12 < month){
         throw new IllegalArgumentException("month parameter Error");
      }
      final MonthBundle mb = MonthBundle.valueOf(MONTH_NAMES[month]);
      final Constructor<?>[] constructors = mb.getConstructors();
      if (constructors==null) return null;
      final MutableSet<LocalDate> set = Sets.mutable.empty();
      for(int i=0;i < constructors.length;i++){
         try{
             final HolidayBundle b = (HolidayBundle)constructors[i].newInstance(null,year);
             set.add(b.getDate());
             final LocalDate chgdt = b.getChangeDate();
             if (chgdt != null) {
                 set.add(chgdt);
             }
         }catch(final Exception e){
             e.printStackTrace();
         }
      }
      // 現在、国民の休日の発生は９月しかない
      if (month == SEPTEMBER){
         set.addAll(getNationalHoliday(year).toSet());
      }
      return set.toSortedSet();
   }

   /** 日付フォーマット yyyy/MM/dd */
   private static DateTimeFormatter YMD_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

   /** Calendar.MONTH に沿った月名の配列 */
   public static String[] MONTH_NAMES = {
       "", "JANUARY","FEBRUARY","MARCH","APRIL","MAY","JUNE",
       "JULY","AUGUST","SEPTEMBER","OCTOBER","NOVEMBER","DECEMBER"
       };

   /**
    * 指定日が祝日なら、true を返す.（指定日による祝日、振替休日チェックの為）
    * @param ms Epoch time(ms)
    * @return 祝日名を返す.祝日、振替休日に該当しなければ、null を返す.
    */
   public static boolean isHoliday(final long ms) {
       return queryHoliday(ms) != null;
   }

   /**
    * 指定日が祝日なら、true を返す.（指定日による祝日、振替休日チェックの為）
    * @param ld LocalDate
    * @return 祝日名を返す.祝日、振替休日に該当しなければ、null を返す.
    */
   public static boolean isHoliday(final LocalDate ld) {
       return queryHoliday(ld) != null;
   }

   /**
    * 指定日が祝日なら、祝日名を返す.（指定日による祝日、振替休日チェックの為）
    * @param ms Epoch time(ms)
    * @return 祝日名を返す.祝日、振替休日に該当しなければ、null を返す.
    */
   public static String queryHoliday(final long ms) {
       return queryHoliday(
               LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.systemDefault()).toLocalDate());
   }

   /**
    * 指定日が祝日なら、祝日名を返す.（指定日による祝日、振替休日チェックの為）
    * @param dt 指定日
    * @return 祝日名を返す.祝日、振替休日に該当しなければ、null を返す.
    */
   public static String queryHoliday(final LocalDate ld) {
      final MonthBundle mb = MonthBundle.valueOf(MONTH_NAMES[ld.getMonthValue()]);
      final Constructor<?>[] constructors = mb.getConstructors();
      if (constructors==null){
         return null; // 祝日でない！
      }
      final int targetDay = ld.getDayOfMonth();
      final int targetYear = ld.getYear();

      try {
          for(int i=0;i < constructors.length;i++){
              final HolidayBundle h = (HolidayBundle)constructors[i].newInstance(null, targetYear);
              if (targetDay == h.getDay()) {
                  return h.getDescription();
              }
              if (targetDay == h.getChangeDay()) {
                  return String.format("振替休日（%s）", h.getDescription());
              }
          }
      } catch (final Exception e) {
          e.printStackTrace();
      }
      final String targetDateStr = YMD_FORMAT.format(ld);
      if (getNationalHoliday(targetYear).collect(holiday -> YMD_FORMAT.format(holiday))
              .anySatisfy(targetDateStr::equals)) {
          return "国民の休日";
      }
      return null;
   }
   /** Weekday String form(JA). */
   public static String[] WEEKDAYS_JA = {"月","火","水","木","金","土", "日"};
   /**
    * 曜日String算出.
    * @param ms Epoch time(ms)
    * @return ex: 日
    */
   public static String dateOfWeekJA(final long ms){
      return dateOfWeekJA(CalendarUtil.ms2LocalDate(ms));
   }
   /**
    * 曜日String算出.
    * @param dt Date
    * @return ex: 日
    */
   public static String dateOfWeekJA(final LocalDate dt){
      return WEEKDAYS_JA[dt.getDayOfWeek().getValue() - 1];
   }
   /** Weekday String form. */
   public static String[] WEEKDAYS_SIMPLE = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun" };
   /**
    * 曜日String算出.
    * @param dt Date
    * @return ex: Sun
    */
   public static String dateOfWeekSimple(final LocalDate ld){
      return WEEKDAYS_SIMPLE[ld.getDayOfWeek().getValue() - 1];
   }
   /**
    * 曜日String算出.
    * @param ms Epoch time(ms)
    * @return ex: 木
    */
   public static String dateOfWeekSimple(final long ms){
      return WEEKDAYS_SIMPLE[CalendarUtil.ms2LocalDate(ms).getDayOfWeek().getValue() - 1];
   }

   /**
    * 指定年→国民の休日のみのDate[]の取得.
    * 国民の休日は現在、敬老の日と秋分の日が１日で挟まれた場合のみ.
    * @param year
    * @return 国民の休日のみのDate[], 存在しない場合は empty
    */
   public static ImmutableList<LocalDate> getNationalHoliday(final int year){
      final HolidayBundle k = HolidayType.RESPECT_FOR_AGE_DAY.getBundle(year);
      final HolidayBundle a = HolidayType.AUTUMN_EQUINOX_DAY.getBundle(year);
      final int kday = k.getDay();
      final int aday = a.getDay();
      final int chgday = k.getChangeDay();

      if ((aday - kday) == 2) {
         return Lists.immutable.of(LocalDate.of(year, SEPTEMBER, kday + 1));
      }

      if (chgday > 0 && ((aday - chgday) == 2)) {
         return Lists.immutable.of(LocalDate.of(year, SEPTEMBER, chgday + 1));
      }

      return Lists.immutable.empty();
   }
   // 元旦
   class NewYearDayBundle extends HolidayBundle{
      public NewYearDayBundle(final int year){
         super(year);
      }
      @Override
      public int getDay(){
         return 1;
      }
      @Override
      public int getMonth(){
         return 1;
      }
      @Override
      public String getDescription(){
         return "元旦";
      }
   }
   // 成人の日
   class ComingOfAgeDayBundle extends HolidayBundle{
      public ComingOfAgeDayBundle(final int year){
         super(year);
      }
      /* １月第２月曜日の日付を求める */
      @Override
      public int getDay(){
          final int wday = LocalDate.of(year, 1, 1).getDayOfWeek().getValue();
          return wday > MONDAY ? (7*2+1)-(wday - MONDAY) : 7+1+(MONDAY - wday);
      }
      @Override
      public int getMonth(){
         return 1;
      }
      @Override
      public String getDescription(){
         return "成人の日";
      }
   }
   // 建国記念日
   class NatinalFoundationBundle extends HolidayBundle{
      public NatinalFoundationBundle(final int year){
         super(year);
      }
      @Override
      public int getDay(){
         return 11;
      }
      @Override
      public int getMonth(){
         return 2;
      }
      @Override
      public String getDescription(){
         return "建国記念日";
      }
   }
   // 春分の日
   // 『海上保安庁水路部 暦計算研究会編 新こよみ便利帳』による計算式
   // さらに、1979年以前を無視！～2150年まで有効
   class SpringEquinoxBundle extends HolidayBundle{
      public SpringEquinoxBundle(final int year){
         super(year);
      }
      @Override
      public int getDay(){
         if (super.year <= 2099){
            return (int)(20.8431 + (0.242194 * (super.year - 1980)) - ((super.year - 1980 )/4));
         }
         return (int)(21.851 + (0.242194 * (super.year - 1980)) - ((super.year - 1980)/4));
      }
      @Override
      public int getMonth(){
         return 3;
      }
      @Override
      public String getDescription(){
         return "春分の日";
      }
   }
   // 昭和の日
   class ShowaDayBundle extends HolidayBundle{
      public ShowaDayBundle(final int year){
         super(year);
      }
      @Override
      public int getDay(){
         return 29;
      }
      @Override
      public int getMonth(){
         return 4;
      }
      @Override
      public String getDescription(){
         return "昭和の日";
      }
   }
   // 憲法記念日
   class KenpoukikenDayBundle extends HolidayBundle{
    public KenpoukikenDayBundle(final int year){
         super(year);
      }
      @Override
      public int getDay(){
         return 3;
      }
      @Override
      public int getMonth(){
         return 5;
      }
      // ５月３日＝Sunday の振替は、６日
      @Override
      public int getChangeDay(){
         if (this.getWeekDay()==SUNDAY){
            return 6;
         }
         return -1;
      }
      @Override
      public LocalDate getChangeDate(){
         if (this.getWeekDay() == SUNDAY){
            return LocalDate.of(year, getMonth(), 6);
         }
         return null;
      }
      @Override
      public String getDescription(){
         return "憲法記念日";
      }
   }
   // みどりの日
   class MidoriDayBundle extends HolidayBundle{
      public MidoriDayBundle(final int year){
         super(year);
      }
      @Override
      public int getDay(){
         return 4;
      }
      @Override
      public int getMonth(){
         return 5;
      }
      // ５月４日＝Sunday の振替は、６日
      @Override
      public int getChangeDay(){
         if (this.getWeekDay() == SUNDAY){
            return 6;
         }
         return -1;
      }
      @Override
      public LocalDate getChangeDate(){
         if (this.getWeekDay() == SUNDAY){
            return LocalDate.of(year, getMonth(), 6);
         }
         return null;
      }
      @Override
      public String getDescription(){
         return "みどりの日";
      }
   }
   // こどもの日
   class KodomoDayBundle extends HolidayBundle{
      public KodomoDayBundle(final int year){
         super(year);
      }
      @Override
      public int getDay(){
         return 5;
      }
      @Override
      public int getMonth(){
         return 5;
      }
      @Override
      public String getDescription(){
         return "こどもの日";
      }
   }
   // 海の日
   class SeaDayBundle extends HolidayBundle{
      public SeaDayBundle(final int year){
         super(year);
      }
      /* ７月第３月曜日の日付を求める */
      @Override
      public int getDay(){
         final int wday = LocalDate.of(year, Month.JULY.getValue(), 1).getDayOfWeek().getValue();
         return wday > MONDAY ? (7*3+1)-(wday - MONDAY) : 14+1+(MONDAY - wday);
      }
      @Override
      public int getMonth(){
         return 7;
      }
      @Override
      public String getDescription(){
         return "海の日";
      }
   }
   // 山の日
   class MountainDayBundle extends HolidayBundle{
      public MountainDayBundle(final int year){
         super(year);
      }
      @Override
      public int getDay(){
          if (year <= 2015) {
              throw new IllegalStateException();
          }
          return 11;
      }
      @Override
      public int getMonth(){
         return 8;
      }
      @Override
      public String getDescription(){
         return "山の日";
      }
   }
   // 敬老の日
   class RespectForAgeDayBundle extends HolidayBundle{
    public RespectForAgeDayBundle(final int year){
         super(year);
      }
      /* ９月第３月曜日の日付を求める */
      @Override
      public int getDay(){
         final int wday = LocalDate.of(year, Month.SEPTEMBER.getValue(), 1).getDayOfWeek().getValue();
         return wday > MONDAY ? (7*3+1)-(wday - MONDAY) : 14+1+(MONDAY - wday);
      }
      @Override
      public int getMonth(){
         return 9;
      }
      @Override
      public String getDescription(){
         return "敬老の日";
      }
   }
   // 秋分の日
   // 『海上保安庁水路部 暦計算研究会編 新こよみ便利帳』による計算式
   // さらに、1979年以前を無視！～2150年まで有効
   class AutumnEquinoxBundle extends HolidayBundle{
      public AutumnEquinoxBundle(final int year){
         super(year);
      }
      @Override
      public int getDay(){
         if (super.year <= 2099){
            return (int)(23.2488 + (0.242194 * (super.year - 1980)) - ((super.year - 1980)/4));
         }
         return (int)(24.2488 + (0.242194 * (super.year - 1980)) - ((super.year - 1980)/4));
      }
      @Override
      public int getMonth(){
         return 9;
      }
      @Override
      public String getDescription(){
         return "秋分の日";
      }
   }
   // 体育の日
   class HealthSportsDayBundle extends HolidayBundle{
      public HealthSportsDayBundle(final int year){
         super(year);
      }
      /* １０月第２月曜日の日付を求める */
      @Override
      public int getDay(){
          final int wday = LocalDate.of(year, Month.OCTOBER.getValue(), 1).getDayOfWeek().getValue();
          return wday > MONDAY ? (7*2+1)-(wday - MONDAY) : 7+1+(MONDAY - wday);
      }
      @Override
      public int getMonth(){
         return 10;
      }
      @Override
      public String getDescription(){
         return "体育の日";
      }
   }
   // 文化の日
   class CultureDayBundle extends HolidayBundle{
      public CultureDayBundle(final int year){
         super(year);
      }
      @Override
      public int getDay(){
         return 3;
      }
      @Override
      public int getMonth(){
         return 11;
      }
      @Override
      public String getDescription(){
         return "文化の日";
      }
   }
   // 勤労感謝の日
   class LaborThanksDayBundle extends HolidayBundle{
      public LaborThanksDayBundle(final int year){
         super(year);
      }
      @Override
      public int getDay(){
         return 23;
      }
      @Override
      public int getMonth(){
         return 11;
      }
      @Override
      public String getDescription(){
         return "勤労感謝の日";
      }
   }
   // 天皇誕生日
   class EmperorBirthDayBundle extends HolidayBundle{
      public EmperorBirthDayBundle(final int year){
         super(year);
      }
      @Override
      public int getDay(){
         return 23;
      }
      @Override
      public int getMonth(){
         return 12;
      }
      @Override
      public String getDescription(){
         return "天皇誕生日";
      }
   }
}
