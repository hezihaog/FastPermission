package com.hzh.fast.permission.callback;

import java.util.List;

/**
 * Package: oms.mmc.permissionshelper.callback
 * FileName: IFinalDeniedAfter
 * Date: on 2018/1/16  下午10:33
 * Auther: zihe
 * Descirbe:
 * Email: hezihao@linghit.com
 */

public interface IFinalDeniedAfterAction {
    /**
     * 当弹窗提示用户需要权限，解释了权限后，用户任然点击了取消后要执行的操作
     *
     * @param perms 拒绝的权限
     */
    void onFinalDeniedAfter(List<String> perms);
}
