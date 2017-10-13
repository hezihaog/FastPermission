package com.hzh.fast.permission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.SparseArrayCompat;

import com.hzh.fast.permission.base.LifecycleFragment;
import com.hzh.fast.permission.base.SimpleFragmentLifecycleAdapter;
import com.hzh.fast.permission.callback.PermissionCallback;
import com.hzh.fast.permission.entity.RequestEntry;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Hezihao on 2017/7/10.
 * 权限申请委托fragment
 */

public class PermissionDelegateFragment extends LifecycleFragment {
    //权限回调的标识
    private static final int REQUEST_CODE = 0X0122;
    private SparseArrayCompat<RequestEntry> callbacks = new SparseArrayCompat<RequestEntry>();

    public static PermissionDelegateFragment newInstance() {
        return new PermissionDelegateFragment();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getLifecycle().removeAllListener();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //内存重启移除掉该fragment
        if (getActivity() != null) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            fm.beginTransaction().remove(this).commitAllowingStateLoss();
        }
    }

    /**
     * 必须让任务在onAttach后执行
     *
     * @param entry 回调监听
     */
    private void PopDownTask(final RequestEntry entry) {
        callbacks.put(entry.hashCode(), entry);
        this.getLifecycle().addListener(new SimpleFragmentLifecycleAdapter() {
            @Override
            public void onAttach() {
                super.onAttach();
                callbacks.get(entry.hashCode()).getRunnable().run();
                getLifecycle().removeListener(this);
            }
        });
    }

    /**
     * 批量申请权限
     *
     * @param context  上下文
     * @param callback 权限允许、拒绝回调
     * @param perms    要申请的权限数组
     */
    public void requestPermission(final Context context, final PermissionCallback callback, final String[] perms) {
        if (callback != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            callback.onGranted();
            return;
        }
        PopDownTask(RequestEntry.newBuilder().setCallback(callback).setRunnable(new Runnable() {
            @Override
            public void run() {
                //只申请用户未允许的权限
                List<String> unGrantedList = new ArrayList<String>();
                for (String permission : perms) {
                    if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        unGrantedList.add(permission);
                    }
                }
                if (unGrantedList.size() > 0) {
                    PermissionDelegateFragment.this.requestPermissions(unGrantedList.toArray(new String[]{}), REQUEST_CODE);
                } else {
                    if (callback != null) {
                        callback.onGranted();
                    }
                }
            }
        }).build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && callbacks != null && callbacks.size() > 0) {
                    for (int i = 0; i < callbacks.size(); i++) {
                        RequestEntry entry = callbacks.valueAt(i);
                        PermissionCallback callback = entry.getCallback();
                        //找出拒绝的权限
                        List<String> deniedList = new ArrayList<String>();
                        for (int j = 0; j < grantResults.length; j++) {
                            int grantResult = grantResults[j];
                            String permission = permissions[j];
                            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                                deniedList.add(permission);
                            }
                        }
                        //已全部允许
                        if (deniedList.isEmpty()) {
                            callback.onGranted();
                        } else {
                            callback.onDenied(deniedList);
                        }
                        callbacks.remove(entry.hashCode());
                    }
                }
                break;
        }
    }
}
