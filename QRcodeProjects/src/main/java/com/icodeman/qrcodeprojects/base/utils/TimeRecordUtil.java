package com.icodeman.qrcodeprojects.base.utils;

import com.icodeman.qrcodeprojects.base.ZxingLog;
/**
 * @author ICodeMan
 * @github https://github.com/LMW-ICodeMan
 * @date 2018/1/11
 */
public class TimeRecordUtil {
    private static final String TAG = "ZXing_TimeRecordUtil";

    private static long lastTime = 0;

    public static void updateTime(String tag){
        long cTime = System.currentTimeMillis();
        if(lastTime > 0){
            ZxingLog.d(TAG,"("+tag+") 耗时 "+(cTime - lastTime));
        }
        lastTime = cTime;
    }

    public static void resetTime(){
        lastTime = 0;
    }
}
