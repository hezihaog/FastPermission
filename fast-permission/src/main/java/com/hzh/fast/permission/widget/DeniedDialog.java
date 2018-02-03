package com.hzh.fast.permission.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.hzh.fast.permission.R;
import com.hzh.fast.permission.util.ScreenUtil;


/**
 * Created by Administrator on 2016/12/29.
 * 拒绝权限解释弹窗
 */

public class DeniedDialog extends Dialog {
    private OnDialogClickListener listener;
    private View rootLayout;

    public DeniedDialog(Context context) {
        super(context);
        init(context);
    }

    public DeniedDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    private void init(Context context) {
        rootLayout = View.inflate(context, R.layout.view_permission_denied_dialog, null);
        setContentView(rootLayout);
        rootLayout.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickOk();
                }
            }
        });
        rootLayout.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickCancel();
                }
            }
        });
        //处理弹窗宽度
        float screenWidth = ScreenUtil.getScreenWidth(getContext());
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = (int) (screenWidth - ScreenUtil.dip2px(getContext(), 15f));
        getWindow().setAttributes(layoutParams);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    /**
     * 设置标题
     */
    public void setTitle(String title) {
        TextView titleTv = (TextView) rootLayout.findViewById(R.id.title);
        titleTv.setText(title);
    }

    /**
     * 设置彩色文字为内容文字
     */
    public void setContentText(CharSequence charSequence) {
        TextView contentTv = (TextView) rootLayout.findViewById(R.id.content);
        contentTv.setText(charSequence);
    }

    /**
     * 设置内容文字
     */
    public void setContentText(String contentText) {
        TextView contentTv = (TextView) rootLayout.findViewById(R.id.content);
        contentTv.setText(contentText);
    }

    /**
     * 设置确认按钮的文字
     */
    public void setOkButtonText(String okText) {
        Button okBtn = (Button) rootLayout.findViewById(R.id.btn_ok);
        okBtn.setText(okText);
    }

    /**
     * 设置取消按钮的文字
     */
    public void setCancelButtonText(String cancelText) {
        Button cancelBtn = (Button) rootLayout.findViewById(R.id.btn_cancel);
        cancelBtn.setText(cancelText);
    }

    /**
     * 点击回调
     */
    public interface OnDialogClickListener {
        /**
         * 点击确定时回调
         */
        void onClickOk();

        /**
         * 点击取消时回调
         */
        void onClickCancel();
    }

    /**
     * 设置点击监听
     *
     * @param listener 监听器对象
     */
    public void setOnDialogClickListener(OnDialogClickListener listener) {
        this.listener = listener;
    }
}
