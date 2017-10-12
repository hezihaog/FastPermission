package com.hzh.fast.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.hzh.fast.permission.base.LifecycleFragment;
import com.hzh.fast.permission.base.SimpleFragmentLifecycleAdapter;
import com.hzh.fast.permission.callback.PermissionCallback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Hezihao on 2017/7/10.
 * 权限申请委托fragment
 */

public class PermissionDelegateFragment extends LifecycleFragment {
    //权限回调的标识
    private static final int REQUEST_CODE = 0X0122;

    protected Activity _activity;
    protected Fragment _fragment;
    private WeakReference<PermissionCallback> mCallbackWeak;

    public static PermissionDelegateFragment newInstance() {
        return new PermissionDelegateFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        _activity = activity;
        _fragment = this;
        super.onAttach(activity);
    }

    /**
     * 必须让任务在onAttach后执行
     *
     * @param runnable 任务对象
     */
    private void waitReady(final Runnable runnable) {
        this.getLifecycle().addListener(new SimpleFragmentLifecycleAdapter() {
            @Override
            public void onAttach() {
                super.onAttach();
                runnable.run();
                mCallbackWeak = null;
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
    public void requestPermission(final Context context, PermissionCallback callback, final String[] perms) {
        this.mCallbackWeak = new WeakReference<PermissionCallback>(callback);
        waitReady(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (mCallbackWeak != null && mCallbackWeak.get() != null) {
                        mCallbackWeak.get().onGranted();
                        return;
                    }
                }
                //只申请用户未允许的权限
                List<String> unGrantedList = new ArrayList<String>();
                for (String permission : perms) {
                    if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        unGrantedList.add(permission);
                    }
                }
                if (unGrantedList.size() > 0) {
                    _fragment.requestPermissions(unGrantedList.toArray(new String[]{}), REQUEST_CODE);
                } else {
                    if (mCallbackWeak != null && mCallbackWeak.get() != null) {
                        mCallbackWeak.get().onGranted();
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && (mCallbackWeak != null && mCallbackWeak.get() != null)) {
                    //找出拒绝的权限
                    List<String> deniedList = new ArrayList<String>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedList.add(permission);
                        }
                    }
                    //已全部允许
                    if (deniedList.isEmpty()) {
                        mCallbackWeak.get().onGranted();
                    } else {
                        mCallbackWeak.get().onDenied(deniedList);
                    }
                }
                break;
        }
    }
}
