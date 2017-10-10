package com.hzh.fast.permission.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by Hezihao on 2017/7/10.
 */

public class LifecycleFragment extends Fragment {
    private FragmentLifecycle lifecycle;

    public LifecycleFragment() {
        this(new FragmentLifecycle());
    }

    @SuppressLint("ValidFragment")
    public LifecycleFragment(FragmentLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public FragmentLifecycle getLifecycle() {
        return lifecycle;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        lifecycle.onAttach();
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycle.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        lifecycle.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycle.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        lifecycle.onDetach();
    }
}
