package com.icodeman.qrcodeprojects.base.utils;


import java.util.concurrent.locks.ReentrantLock;
/**
 * @author ICodeMan
 * @github https://github.com/LMW-ICodeMan
 * @date 2018/1/11
 */
public class BarcodeLockUtil {
    private static final String TAG = "ZXing_BarcodeLockUtil";

    private static final ReentrantLock lock = new ReentrantLock();

    public static boolean requestLock(){
        if(lock.isLocked()){
            return false;
        }else {
            lock.lock();
            return true;
        }
    }

    public static void releaseLock(){
        if(lock.isLocked()){
            lock.unlock();
        }
    }
}
