package com.hzh.fast.permission.base;


import com.hzh.fast.permission.util.CollectionUtil;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Created by Hezihao on 2017/7/10.
 */

public class FragmentLifecycle implements Lifecycle {
    //使用WeakHashMap引用，避免内存泄露，add添加到转换的set集合，遍历时转换到新的list集合，避免高并发异常。
    private final Set<FragmentLifecycleListener> lifecycleListeners = Collections.newSetFromMap(new WeakHashMap<FragmentLifecycleListener, Boolean>());
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
        if (lifecycleListeners != null && lifecycleListeners.size() > 0 && lifecycleListeners.contains(listener)) {
            lifecycleListeners.remove(listener);
        }
    }

    public void onAttach() {
        isAttach = true;
        for (FragmentLifecycleListener listener : CollectionUtil.getSnapshot(lifecycleListeners)) {
            listener.onAttach();
        }
    }

    public void onStart() {
        isStarted = true;
        for (FragmentLifecycleListener listener : CollectionUtil.getSnapshot(lifecycleListeners)) {
            listener.onStart();
        }
    }

    public void onStop() {
        isStarted = false;
        for (FragmentLifecycleListener listener : CollectionUtil.getSnapshot(lifecycleListeners)) {
            listener.onStop();
        }
    }

    public void onDestroy() {
        isDestroyed = true;
        for (FragmentLifecycleListener listener : CollectionUtil.getSnapshot(lifecycleListeners)) {
            listener.onDestroy();
        }
    }

    public void onDetach() {
        isAttach = false;
        for (FragmentLifecycleListener listener : CollectionUtil.getSnapshot(lifecycleListeners)) {
            listener.onDetach();
        }
    }
}
