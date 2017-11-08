package com.hzh.fast.permission.lifecycle;

/**
 * Created by Hezihao on 2017/7/10.
 */

public interface Lifecycle<T extends LifecycleListener> {
    /**
     * 添加生命周期回调监听器
     *
     * @param listener
     */
    void addListener(T listener);

    /**
     * 移除生命周期回调监听器
     *
     * @param listener
     */
    void removeListener(T listener);

    /**
     * 移除所有生命周期回调监听器
     */
    void removeAllListener();
}