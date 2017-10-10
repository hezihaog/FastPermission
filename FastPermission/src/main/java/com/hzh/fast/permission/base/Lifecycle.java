package com.hzh.fast.permission.base;

/**
 * Created by Hezihao on 2017/7/10.
 */

public interface Lifecycle {
    void addListener(FragmentLifecycleListener listener);

    void removeListener(FragmentLifecycleListener listener);
}
