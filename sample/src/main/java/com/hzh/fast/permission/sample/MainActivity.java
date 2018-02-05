package com.hzh.fast.permission.sample;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hzh.fast.permission.FastPermission;
import com.hzh.fast.permission.callback.SimpleDeniedCallback;
import com.hzh.fast.permission.callback.PermissionCallback;
import com.hzh.fast.permission.entity.Description;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onFindView();
    }

    private void onFindView() {
        Button getPermissionBtn = (Button) findViewById(R.id.getPermission);
        Button getPermissionListBtn = (Button) findViewById(R.id.getPermissionList);
        Button getPermissionWithTipBtn = (Button) findViewById(R.id.getPermissionWithTip);
        Button requestPermissionListWithTipBtn = (Button) findViewById(R.id.requestPermissionListWithTip);
        Button callPhoneBtn = (Button) findViewById(R.id.callPhoneBtn);
        //设置点击监听
        getPermissionBtn.setOnClickListener(this);
        getPermissionListBtn.setOnClickListener(this);
        getPermissionWithTipBtn.setOnClickListener(this);
        requestPermissionListWithTipBtn.setOnClickListener(this);
        callPhoneBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.getPermission:
                getPermission();
                break;
            case R.id.getPermissionList:
                getPermissionList();
                break;
            case R.id.getPermissionWithTip:
                getPermissionWithTip();
                break;
            case R.id.requestPermissionListWithTip:
                getPermissionListWithTip();
                break;
            case R.id.callPhoneBtn:
                callPhone();
                break;
            default:
                break;
        }
    }

    /**
     * 一次申请单个权限，被拒绝时不提示弹窗
     */
    public void getPermission() {
        FastPermission.request(this, new PermissionCallback() {
            @Override
            public void onGranted() {
                Toast.makeText(MainActivity.this, "已允许单个权限", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(List<String> perms) {
                for (int i = 0; i < perms.size(); i++) {
                    Toast.makeText(MainActivity.this, perms.get(i) + " 权限被拒绝", Toast.LENGTH_SHORT).show();
                }
            }
        }, new String[]{Manifest.permission.READ_CONTACTS});
    }

    /**
     * 一次申请多个权限，被拒绝时不弹窗
     */
    public void getPermissionList() {
        FastPermission.request(this, new PermissionCallback() {
            @Override
            public void onGranted() {
                Toast.makeText(MainActivity.this, "所有权限都被同意", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(List<String> perms) {
                for (String perm : perms) {
                    Toast.makeText(MainActivity.this, perm + " 权限被拒绝", Toast.LENGTH_SHORT).show();
                }
            }
        }, new String[]{Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.READ_CALENDAR, Manifest.permission.ACCESS_FINE_LOCATION});
    }

    /**
     * 一次申请单个权限，被拒绝时弹窗
     */
    public void getPermissionWithTip() {
        //组装解释对象数组，要和权限请求的顺序相同
        Description[] descriptions = new Description[]{
                new Description("相机", "相机权限才能拍照喔")};
        FastPermission.request(this, new SimpleDeniedCallback(this, descriptions) {
            @Override
            public void onFinalDeniedAfter(List<String> perms) {
                for (String perm : perms) {
                    Toast.makeText(MainActivity.this, perm + " 权限被拒绝", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onGranted() {
                Toast.makeText(MainActivity.this, "所有权限都被同意", Toast.LENGTH_SHORT).show();
            }
        }, new String[]{Manifest.permission.CAMERA});
    }

    /**
     * 一次申请多个权限，被拒绝时弹窗
     */
    public void getPermissionListWithTip() {
        Description[] descriptions = new Description[]{
                new Description("录音", "允许才能录音喔"),
                new Description("短信", "允许才能看短信喔"),
                new Description("存储", "允许才能存储喔"),
                new Description("打电话", "允许才能打电话呢"),
                new Description("位置", "允许才能看位置呢")
        };
        FastPermission.request(this, new SimpleDeniedCallback(this, descriptions) {

            @Override
            public void onGranted() {
                Toast.makeText(MainActivity.this, "所有权限都被同意", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinalDeniedAfter(List<String> perms) {
                for (String perm : perms) {
                    Toast.makeText(MainActivity.this, perm + " 权限被拒绝", Toast.LENGTH_SHORT).show();
                }
            }
        }, new String[]{
                Manifest.permission.RECORD_AUDIO
                , Manifest.permission.READ_SMS
                , Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_CALENDAR
                , Manifest.permission.ACCESS_FINE_LOCATION});
    }

    /**
     * 申请打电话权限
     */
    public void callPhone() {
        FastPermission.request(this, new PermissionCallback() {
            @Override
            public void onGranted() {
                Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + "15814871500"));
                startActivity(phoneIntent);
            }

            @Override
            public void onDenied(List<String> perms) {
                for (String perm : perms) {
                    Toast.makeText(MainActivity.this, perm + " 权限被拒绝", Toast.LENGTH_SHORT).show();
                }
            }
        }, new String[]{Manifest.permission.CALL_PHONE});
    }
}
