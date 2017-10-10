package com.hzh.fast.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.hzh.fast.permission.callback.PermissionCallback;
import com.hzh.fast.permission.delegate.PermissionDelegateFinder;

/**
 * Created by Hezihao on 2017/7/10.
 * 权限管理者
 */

public class PermissionManager {
    private PermissionManager() {
    }

    private static class Singleton {
        private static final PermissionManager instance = new PermissionManager();
    }

    public static PermissionManager getInstance() {
        return Singleton.instance;
    }

    /**
     * 检查指定权限是否已经获取
     */
    public boolean isAccept(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else {
            return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * 申请权限
     */
    public void request(FragmentActivity activity, PermissionCallback callback, String[] perms) {
        PermissionDelegateFragment delegate = findDelegate(activity);
        if (delegate != null) {
            delegate.requestPermission(activity, callback, perms);
        }
    }

    /**
     * 构建申请权限用的隐藏的fragment
     */
    private PermissionDelegateFragment findDelegate(FragmentActivity activity) {
        return PermissionDelegateFinder.getInstance().find(activity);
    }
}