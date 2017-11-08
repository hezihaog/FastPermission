package com.hzh.fast.permission.lifecycle;

/**
 * Created by Hezihao on 2017/7/10.
 * Fragment生命周期回调接口
 */

public interface FragmentLifecycleListener extends LifecycleListener {
    void onAttach();

    void onStart();

    void onStop();

    void onDestroy();

    void onDetach();
}
