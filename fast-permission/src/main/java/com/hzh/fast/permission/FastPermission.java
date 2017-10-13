package com.hzh.fast.permission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.hzh.fast.permission.callback.PermissionCallback;
import com.hzh.fast.permission.delegate.PermissionDelegateFinder;
import com.hzh.fast.permission.delegate.PermissionDelegateFragment;

/**
 * Created by Hezihao on 2017/7/10.
 * 权限管理器
 */

public class FastPermission {
    private FastPermission() {
    }

    private static class Singleton {
        private static final FastPermission instance = new FastPermission();
    }

    public static FastPermission getInstance() {
        return Singleton.instance;
    }

    /**
     * 小于6.0则不检查权限
     *
     * @return 返回true代表版本号大于6.0需要检查权限
     */
    private boolean isNeedCheck() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 检查指定权限是否已经获取
     */
    public boolean isAccept(Context context, String permission) {
        return isNeedCheck() && ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 使用Activity申请权限
     *
     * @param activity 要注入权限申请代理的FragmentActivity
     * @param callback 权限申请 成功、失败回调
     * @param perms    权限列表数组
     */
    public void request(@NonNull FragmentActivity activity, @NonNull PermissionCallback callback, @NonNull String[] perms) {
        PermissionDelegateFragment delegate = findDelegate(activity);
        if (delegate != null) {
            delegate.requestPermission(activity, callback, perms);
        }
    }

    /**
     * 使用Fragment申请权限
     *
     * @param fragment 使用的Fragment
     * @param callback 权限申请 成功、失败回调
     * @param perms    权限列表数组
     */
    public void request(@NonNull Fragment fragment, @NonNull PermissionCallback callback, @NonNull String[] perms) {
        FragmentActivity activity = fragment.getActivity();
        if (activity != null && !activity.isFinishing()) {
            PermissionDelegateFragment delegate = findDelegate(activity);
            if (delegate != null) {
                delegate.requestPermission(activity, callback, perms);
            }
        }
    }


    /**
     * 构建申请权限用的隐藏的fragment
     */
    private PermissionDelegateFragment findDelegate(FragmentActivity activity) {
        return PermissionDelegateFinder.getInstance().find(activity);
    }
}