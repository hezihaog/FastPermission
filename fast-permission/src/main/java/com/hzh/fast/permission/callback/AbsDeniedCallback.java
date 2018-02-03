package com.hzh.fast.permission.callback;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import com.hzh.fast.permission.FastPermission;
import com.hzh.fast.permission.R;
import com.hzh.fast.permission.entity.DescriptionWrapper;
import com.hzh.fast.permission.util.Util;
import com.hzh.fast.permission.widget.DeniedDialog;

import java.util.List;

/**
 * Package: oms.mmc.permissionshelper.callback
 * FileName: SimpleDeniedCallback
 * Date: on 2018/1/16  下午7:17
 * Auther: zihe
 * Descirbe:支持拒绝弹窗的Callback
 * Email: hezihao@linghit.com
 */

public abstract class AbsDeniedCallback implements PermissionCallback, IFinalDeniedAfterAction {
    private FragmentActivity activity;
    private String titleText;
    private DescriptionWrapper[] descriptionWrappers;

    /**
     * 不设置dialog标题
     *
     * @param descriptionWrappers 权限拒绝，权限名和解释的对象数组
     */
    public AbsDeniedCallback(FragmentActivity activity, DescriptionWrapper[] descriptionWrappers) {
        this(activity, null, descriptionWrappers);
    }

    /**
     * 可以给dialog设置标题
     */
    public AbsDeniedCallback(FragmentActivity activity, String titleText, DescriptionWrapper[] descriptionWrappers) {
        this.activity = activity;
        this.titleText = titleText;
        this.descriptionWrappers = descriptionWrappers;
    }

    /**
     * 已加final，该方法不能被重写，要做被拒绝的操作，请重写finalDeniedAfter方法
     *
     * @param perms 被拒绝的权限集合
     */
    @Override
    public final void onDenied(final List<String> perms) {
        //用户拒绝，弹出解释弹窗，引导用户点确认再次允许
        final DeniedDialog dialog = new DeniedDialog(activity);
        if (titleText != null) {
            dialog.setTitle(titleText);
        }
        dialog.setContentText(buildTipTextWithColor(descriptionWrappers));
        dialog.setOnDialogClickListener(new DeniedDialog.OnDialogClickListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                //用户同意允许，则再次请求权限
                requestAgain(activity, perms, AbsDeniedCallback.this);
            }

            @Override
            public void onClickCancel() {
                //用户还是拒绝，弹窗提示可以去到去设置界面开启权限
                dialog.dismiss();
                showGoToSettingDialog(activity, perms);
            }
        });
        dialog.show();
    }

    /**
     * 显示跳转到去设置界面弹窗
     */
    private void showGoToSettingDialog(final Activity activity, final List<String> perms) {
        final DeniedDialog dialog = new DeniedDialog(activity);
        dialog.setTitle(activity.getResources().getString(R.string.md_dialog_go_to_setting_title));
        dialog.setContentText(activity.getResources().getString(R.string.md_dialog_go_to_setting_tip));
        dialog.setOkButtonText(activity.getResources().getString(R.string.md_dialog_go_to_setting));
        dialog.setCancelButtonText(activity.getResources().getString(R.string.md_dialog_cancel));
        dialog.setOnDialogClickListener(new DeniedDialog.OnDialogClickListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                //用户点击了去设置，跳转设置界面
                Util.goToAppDetailSetting(activity);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
                //用户点击取消，执行最终的拒绝操作
                onFinalDeniedAfter(perms);
            }
        });
        dialog.show();
    }

    /**
     * 拼接解释文字
     */
    private CharSequence buildTipTextWithColor(DescriptionWrapper[] descriptionWrappers) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        int start = 0;
        for (int i = 0; i < descriptionWrappers.length; i++) {
            DescriptionWrapper wrapper = descriptionWrappers[i];
            String name = wrapper.getPermissionName();
            String description = wrapper.getDescription();
            builder.append(name);
            ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#37ADA4"));
            builder.setSpan(span, start, start + name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append("：");
            builder.append(description);
            start = start + name.length() + 2 + description.length();
            if (i != descriptionWrappers.length - 1) {
                builder.append("\n");
            }
        }
        return builder;
    }

    /**
     * 再次请求
     */
    private void requestAgain(final FragmentActivity activity, List<String> perms, final AbsDeniedCallback callback) {
        //重新组装权限数组
        String[] permissionList = new String[perms.size()];
        for (int i = 0; i < perms.size(); i++) {
            permissionList[i] = perms.get(i);
        }
        FastPermission.request(activity, new PermissionCallback() {
            @Override
            public void onGranted() {
                //允许回调最外层的允许
                callback.onGranted();
            }

            @Override
            public void onDenied(List<String> perms) {
                //虽然用户点击了允许重新权限，但是还是被点拒绝，则弹出去设置弹窗
                showGoToSettingDialog(activity, perms);
            }
        }, permissionList);
    }
}
