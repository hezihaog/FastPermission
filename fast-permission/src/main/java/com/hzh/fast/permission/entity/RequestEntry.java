package com.hzh.fast.permission.entity;

import com.hzh.fast.permission.callback.PermissionCallback;

/**
 * Created by Hezihao on 2017/10/13.
 * 包裹请求的Callback和runnable
 */

public class RequestEntry {
    private  PermissionCallback callback;
    private Runnable runnable;

    private RequestEntry() {
    }

    public RequestEntry newInstance(Builder builder) {
        this.callback = builder.callback;
        this.runnable = builder.runnable;
        return this;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public PermissionCallback getCallback() {
        return callback;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public static class Builder {
        private  PermissionCallback callback;
        private Runnable runnable;

        public Builder setCallback(PermissionCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder setRunnable(Runnable runnable) {
            this.runnable = runnable;
            return this;
        }

        public RequestEntry build() {
            RequestEntry entry = new RequestEntry();
            return entry.newInstance(this);
        }
    }
}
