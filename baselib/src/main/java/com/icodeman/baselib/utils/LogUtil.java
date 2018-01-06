package com.icodeman.baselib.utils;


import android.util.Log;

public class LogUtil {

    public static void e(String model,String clazz,String msg){
        StringBuilder sb = new StringBuilder();
        sb.append("["+model+"]");
        sb.append("["+clazz+"]");
        LogUtil.e(sb.toString(),msg);
    }

    public static void e(String tag,String msg){
        Log.e(tag,msg);
    }

    public static void i(String model,String clazz,String msg){
        StringBuilder sb = new StringBuilder();
        sb.append("["+model+"]");
        sb.append("["+clazz+"]");
        LogUtil.i(sb.toString(),msg);
    }

    public static void i(String tag,String msg){
        Log.i(tag,msg);
    }


    public static void d(String model,String clazz,String msg){
        StringBuilder sb = new StringBuilder();
        sb.append("["+model+"]");
        sb.append("["+clazz+"]");
        LogUtil.d(sb.toString(),msg);
    }

    public static void d(String tag,String msg){
        Log.d(tag,msg);
    }
}
