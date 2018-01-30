package com.icodeman.qrcodeprojects.base;


import com.icodeman.baselib.utils.LogUtil;
/**
 * @author ICodeMan
 * @github https://github.com/LMW-ICodeMan
 * @date 2018/1/11
 */
public class ZxingLog {

    public static void e(String clazz,String msg){
        LogUtil.e(ZXingConstants.TAG_MODEL,clazz,msg);
    }


    public static void i(String clazz,String msg){
        LogUtil.i(ZXingConstants.TAG_MODEL,clazz,msg);
    }


    public static void d(String clazz,String msg){
        LogUtil.d(ZXingConstants.TAG_MODEL,clazz,msg);
    }

}
