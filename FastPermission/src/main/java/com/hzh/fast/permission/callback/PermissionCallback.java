package com.hzh.fast.permission.callback;

import java.util.List;

/**
 * Created by Hezihao on 2017/7/11.
 * 权限回调接口
 */

public interface PermissionCallback {
    /**
     * 权限允许
     */
    void onGranted();

    /**
     * 权限拒绝
     * @param perms 被拒绝的权限集合
     */
    void onDenied(List<String> perms);
}