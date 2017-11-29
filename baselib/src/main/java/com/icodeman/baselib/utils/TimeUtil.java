package com.icodeman.baselib.utils;

import java.util.Calendar;

/**
 * 时间处理工具类
 * <p>
 * @author ICodeMan
 * @date 2017/11/24
 */

public class TimeUtil {

    public static final int WEEKS_OF_YEAR = 12;

    public static final long ONE_HOURS = 1000 * 60 * 60 * 1;
    public static final long ONE_DAY = 24 * ONE_HOURS;
    public static final long ONE_WEEK = 7 * ONE_DAY;

    private static Calendar calOne = Calendar.getInstance();
    private static Calendar calTwo = Calendar.getInstance();

    public synchronized static int getYear(long time) {
        calOne.setTimeInMillis(time);
        return calOne.get(Calendar.YEAR);
    }

    public synchronized static int getMonth(long time) {
        calOne.setTimeInMillis(time);
        return calOne.get(Calendar.MONTH);
    }

    public synchronized static int getDay(long time) {
        calOne.setTimeInMillis(time);
        return calOne.get(Calendar.DAY_OF_MONTH);
    }

    public synchronized static int[] getTimeInfo(long time) {
        int[] info = new int[3];
        calOne.setTimeInMillis(time);
        info[0] = calOne.get(Calendar.YEAR);
        info[1] = calOne.get(Calendar.MONTH);
        info[2] = calOne.get(Calendar.DAY_OF_MONTH);
        return info;
    }
}
