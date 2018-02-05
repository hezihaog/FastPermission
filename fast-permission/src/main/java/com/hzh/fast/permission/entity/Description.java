package com.hzh.fast.permission.entity;

import android.content.Context;

/**
 * Package: oms.mmc.permissionshelper.entity
 * FileName: DescriptionWrapper
 * Date: on 2018/1/16  下午7:04
 * Auther: zihe
 * Descirbe:被拒绝时，弹窗需要使用的权限数据类
 * Email: hezihao@linghit.com
 */

public class Description {
    /**
     * 权限名
     */
    private String permissionName;
    /**
     * 解释
     */
    private String description;

    public Description(String permissionName, String description) {
        this.permissionName = permissionName;
        this.description = description;
    }

    public Description(Context context, int permissionNameResId, int descriptionResId) {
        this.permissionName = context.getResources().getString(permissionNameResId);
        this.description = context.getResources().getString(descriptionResId);
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}