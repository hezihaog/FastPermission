package com.hzh.fast.permission.base;


import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Hezihao on 2017/7/10.
 */

public class FragmentLifecycle implements Lifecycle {
    //读写分离，避免遍历的同时add进集合，抛出高并发异常。
    private final CopyOnWriteArrayList<FragmentLifecycleListener> lifecycleListeners = new CopyOnWriteArrayList<FragmentLifecycleListener>();
    private boolean isAttach;
    private boolean isStarted;
    private boolean isDestroyed;

    @Override
    public void addListener(FragmentLifecycleListener listener) {
        lifecycleListeners.add(listener);
        if (isAttach) {
            listener.onAttach();
        } else if (isDestroyed) {
            listener.onDestroy();
        } else if (isStarted) {
            listener.onStart();
        } else if (isStarted == false) {
            listener.onStop();
        } else {
            listener.onDetach();
        }
    }

    @Override
    public void removeListener(FragmentLifecycleListener listener) {
        if (lifecycleListeners.size() > 0 && lifecycleListeners.contains(listener)) {
            lifecycleListeners.remove(listener);
        }
    }

    @Override
    public void removeAllListener() {
        if (lifecycleListeners.size() > 0) {
            lifecycleListeners.clear();
        }
    }

    public void onAttach() {
        isAttach = true;
        for (FragmentLifecycleListener listener : lifecycleListeners) {
            listener.onAttach();
        }
    }

    public void onStart() {
        isStarted = true;
        for (FragmentLifecycleListener listener : lifecycleListeners) {
            listener.onStart();
        }
    }

    public void onStop() {
        isStarted = false;
        for (FragmentLifecycleListener listener : lifecycleListeners) {
            listener.onStop();
        }
    }

    public void onDestroy() {
        isDestroyed = true;
        for (FragmentLifecycleListener listener : lifecycleListeners) {
            listener.onDestroy();
        }
    }

    public void onDetach() {
        isAttach = false;
        for (FragmentLifecycleListener listener : lifecycleListeners) {
            listener.onDetach();
        }
    }
}
