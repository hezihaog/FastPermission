package com.hzh.fast.permission.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Package: com.hzh.fast.permission.util
 * FileName: VersionUtil
 * Date: on 2017/11/26  下午5:36
 * Auther: zihe
 * Descirbe: 工具类
 * Email: hezihao@linghit.com
 */

public class Util {
    /**
     * 判断是否需要检查权限。运行版本大于6.0并且使用该库的targetSdkVersion大于23时才检查
     *
     * @return 返回true代表版本号大于6.0需要检查权限
     */
    public static boolean isNeedCheck(Context context) {
        int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
        return Build.VERSION.SDK_INT >= 23 && targetSdkVersion >= 23;
    }

    /**
     * 检查指定权限是否已经获取
     */
    public static boolean isAccept(Context context, String permission) {
        if (!Util.isNeedCheck(context)) {
            return true;
        } else {
            return Util.isNeedCheck(context)
                    && ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }
}
