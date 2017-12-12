package com.icodeman.baselib.utils;

import java.util.Calendar;

/**
 * 时间处理工具类
 * <p>
 *
 * @author ICodeMan
 * @date 2017/11/24
 */

public class TimeUtil {

    public static final long ONE_HOURS = 1000 * 60 * 60 * 1;
    public static final long ONE_DAY = 24 * ONE_HOURS;
    public static final long ONE_WEEK = 7 * ONE_DAY;

    public static class TimeInfo {
        public long time;
        public int year;
        public int month;//从0开始
        public int week;//从0开始
        public int day;//从1开始
        public int quarter;//从0开始
        public int hour;//24小时制
        public int minute;
        public int second;
    }

    public static Calendar getCalendar(){
        return Calendar.getInstance();
    }

    public static int getYear(long time) {
        Calendar calendar = getCalendar();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth(long time) {
        Calendar calendar = getCalendar();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.MONTH);
    }

    public static int getDay(long time) {
        Calendar calendar = getCalendar();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取一个时间的信息
     * @param time 时间戳
     * @return
     */
    public static TimeInfo getTimeInfo(long time) {
        return getTimeInfo(time,false);
    }

    /**
     * 获取一个时间的信息
     *
     * @param time       时间戳
     * @param needCNWeek true：拿到的周信息从星期一开始，对应的月份可能发生变化（规则为该周的星期一在几月份，这一周就属于几月份）
     *                   false：按照时间选择器的标准返回（本周和上周跨月份的情况，时间所在的周可能没有7天）
     * @return 时间信息
     */
    public static TimeInfo getTimeInfo(long time, boolean needCNWeek) {
        Calendar calendar = getCalendar();
        TimeInfo info = new TimeInfo();
        calendar.setTimeInMillis(time);
        info.time = time;
        if (!needCNWeek) {
            info.year = calendar.get(Calendar.YEAR);
            info.month = calendar.get(Calendar.MONTH);
            info.week = calendar.get(Calendar.WEEK_OF_MONTH);
        }else {
            int[] cnInfo = getCNWeekOfMonthInfo(time);
            info.year = cnInfo[0];
            info.month = cnInfo[1];
            info.week = cnInfo[2];
        }
        int[] dayInfo = getHoursDetailInfo(time);
        info.day = calendar.get(Calendar.DAY_OF_MONTH);
        info.hour = dayInfo[0];
        info.minute = dayInfo[1];
        info.second = dayInfo[2];

        info.quarter = info.month / 3;
        return info;
    }

    /**
     * 获取准确的24小时制时间信息（小时、分钟，秒）
     * @param time 时间戳
     * @return {hour,minute,second}
     */
    public static int[] getHoursDetailInfo(long time){
        int[] info = new int[3];
        Calendar calendar = getCalendar();
        calendar.setTimeInMillis(time);
        info[0] = calendar.get(Calendar.HOUR_OF_DAY);
        info[1] = calendar.get(Calendar.MINUTE);
        info[2] = calendar.get(Calendar.SECOND);
        return info;
    }

    /**
     * 获取以星期一为周开始的周时间信息{年，月，周}
     * @param time 时间戳
     * @return {year,month,week}
     */
    public static int[] getCNWeekOfMonthInfo(long time){
        Calendar calendar = getCalendar();
        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        if (dayOfWeek == Calendar.SUNDAY) {
            weekOfMonth -= 1;
        }
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (firstDayOfWeek != Calendar.MONDAY && firstDayOfWeek != Calendar.SUNDAY) {
            weekOfMonth -= 1;
        }
        if (weekOfMonth == 0) {
            month -= 1;
            if (month < 0) {
                month = 11;
                year -= 1;
            }
            Calendar calTwo = getCalendar();
            calTwo.setTimeInMillis(time - dayOfMonth * ONE_DAY);
            weekOfMonth = calTwo.get(Calendar.WEEK_OF_MONTH);
            calTwo.set(Calendar.DAY_OF_MONTH, 1);
            int tFirstDayOfWeek = calTwo.get(Calendar.DAY_OF_WEEK);
            if (tFirstDayOfWeek > Calendar.MONDAY) {
                weekOfMonth -= 1;
            }
        }
        return new int[]{year,month,weekOfMonth};
    }

}
