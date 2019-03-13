package com.icodeman.baselib.utils;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.icodeman.baselib.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限相关的工具类
 *
 * @author lmw
 * @date 2019/3/11.
 */
public class PermissionUtils {

    public static boolean hasPermission(BaseActivity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 检查权限，如果有权限，返回true，如果没有权限，返回false，弹出授权提示
     *
     * @param activity
     * @param requestCode
     * @param permission
     * @return
     */
    public static boolean checkAndRequestPermission(BaseActivity activity, int requestCode, String permission) {
        String[] permissions = new String[]{permission};
        return checkAndRequestPermission(activity,requestCode,permissions);
    }

    /**
     * 检查权限，如果有权限，返回true，如果没有权限，返回false，弹出授权提示
     *
     * @param activity
     * @param requestCode
     * @param permission
     * @return
     */
    public static boolean checkAndRequestPermission(BaseActivity activity, int requestCode, String[] permission) {
        List<String> losePermission = new ArrayList<>();
        for (String per : permission) {
            if (!hasPermission(activity, per)) {
                losePermission.add(per);
            }
        }
        if (losePermission.size() > 0) {
            String[] temps = new String[losePermission.size()];
            losePermission.toArray(temps);
            ActivityCompat.requestPermissions(activity, temps, requestCode);
            return false;
        } else {
            return true;
        }
    }

}
