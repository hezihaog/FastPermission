package com.hzh.fast.permission.callback;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import com.hzh.fast.permission.FastPermission;
import com.hzh.fast.permission.R;
import com.hzh.fast.permission.entity.Description;
import com.hzh.fast.permission.util.Util;

import java.util.List;

/**
 * Package: oms.mmc.permissionshelper.callback
 * FileName: SimpleDeniedCallback
 * Date: on 2018/1/16  下午7:17
 * Auther: zihe
 * Descirbe:支持拒绝弹窗的Callback
 * Email: hezihao@linghit.com
 */

public abstract class SimpleDeniedCallback implements PermissionCallback, IFinalDeniedAfterAction {
    private FragmentActivity activity;
    private String titleText;
    private Description[] descriptions;

    /**
     * 不设置dialog标题
     *
     * @param descriptions 权限拒绝，权限名和解释的对象数组
     */
    public SimpleDeniedCallback(FragmentActivity activity, Description[] descriptions) {
        this(activity, null, descriptions);
    }

    /**
     * 可以给dialog设置标题
     */
    public SimpleDeniedCallback(FragmentActivity activity, String titleText, Description[] descriptions) {
        this.activity = activity;
        this.titleText = titleText;
        this.descriptions = descriptions;
    }

    /**
     * 已加final，该方法不能被重写，要做被拒绝的操作，请重写finalDeniedAfter方法
     *
     * @param perms 被拒绝的权限集合
     */
    @Override
    public final void onDenied(final List<String> perms) {
        //用户拒绝，弹出解释弹窗，引导用户点确认再次允许
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (titleText != null) {
            builder.setTitle(titleText);
        }
        builder.setMessage(buildTipTextWithColor(descriptions));
        builder.setPositiveButton(activity.getString(R.string.md_dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //用户同意允许，则再次请求权限
                requestAgain(activity, perms, SimpleDeniedCallback.this);
            }
        });
        builder.setNegativeButton(activity.getString(R.string.md_dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //用户还是拒绝，弹窗提示可以去到去设置界面开启权限
                dialog.dismiss();
                showGoToSettingDialog(activity, perms);
            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    /**
     * 显示跳转到去设置界面弹窗
     */
    private void showGoToSettingDialog(final Activity activity, final List<String> perms) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.md_dialog_go_to_setting_title));
        builder.setMessage(activity.getResources().getString(R.string.md_dialog_go_to_setting_tip));
        builder.setPositiveButton(activity.getResources().getString(R.string.md_dialog_go_to_setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //用户点击了去设置，跳转设置界面
                Util.goToAppDetailSetting(activity);
            }
        });
        builder.setNegativeButton(activity.getResources().getString(R.string.md_dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //用户点击取消，执行最终的拒绝操作
                onFinalDeniedAfter(perms);
            }
        });
        builder.create().show();
    }

    /**
     * 拼接解释文字
     */
    private CharSequence buildTipTextWithColor(Description[] descriptions) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        int start = 0;
        for (int i = 0; i < descriptions.length; i++) {
            Description wrapper = descriptions[i];
            String name = wrapper.getPermissionName();
            String description = wrapper.getDescription();
            builder.append(name);
            ForegroundColorSpan span = new ForegroundColorSpan(activity.getApplicationContext()
                    .getResources().getColor(R.color.fast_permission_black));
            builder.setSpan(span, start, start + name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append("：");
            builder.append(description);
            start = start + name.length() + 2 + description.length();
            if (i != descriptions.length - 1) {
                builder.append("\n");
            }
        }
        return builder;
    }

    /**
     * 再次请求
     */
    private void requestAgain(final FragmentActivity activity, List<String> perms, final SimpleDeniedCallback callback) {
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
