package com.hzh.fast.permission.delegate;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.hzh.fast.permission.PermissionDelegateFragment;


/**
 * Created by Hezihao on 2017/7/10.
 * 权限fragment生成、查找类
 */

public class PermissionDelegateFinder {
    private static final String DELEGATE_FRAGMENT_TAG = PermissionDelegateFragment.class.getSimpleName() + "Tag";

    private static class Singleton {
        private static final PermissionDelegateFinder instance = new PermissionDelegateFinder();
    }

    public static PermissionDelegateFinder getInstance() {
        return Singleton.instance;
    }

    /**
     * 添加隐藏权限fragment
     */
    public PermissionDelegateFragment find(@NonNull FragmentActivity activity) {
        PermissionDelegateFragment fragment = null;
        if (activity != null && !activity.isFinishing()) {
            FragmentManager fm = activity.getSupportFragmentManager();
            fragment = (PermissionDelegateFragment) fm.findFragmentByTag(DELEGATE_FRAGMENT_TAG);
            if (fragment == null) {
                fragment = PermissionDelegateFragment.newInstance();
                fm.beginTransaction()
                        .add(fragment, DELEGATE_FRAGMENT_TAG)
                        .commitAllowingStateLoss();
            }
        }
        return fragment;
    }
}
