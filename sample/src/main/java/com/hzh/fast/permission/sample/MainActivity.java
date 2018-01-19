package com.hzh.fast.permission.sample;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hzh.fast.permission.FastPermission;
import com.hzh.fast.permission.callback.PermissionCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String[] permissionList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            permissionList = new String[]
                    {Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE};
        } else {
            permissionList = new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE};
        }

        Button request = (Button) findViewById(R.id.request);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission(permissionList);
            }
        });
    }

    /**
     * 请求权限
     *
     * @param perms 需要申请的权限数组
     */
    private void requestPermission(String[] perms) {
        FastPermission.request(MainActivity.this, new PermissionCallback() {
            @Override
            public void onGranted() {
                Toast.makeText(MainActivity.this, "申请权限成功，可进行下一步操作", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(final List<String> perms) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("申请权限")
                        .setMessage("请允许app申请的所有权限，以便正常使用")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //再次请求未允许的权限
                                String[] againPerms = new String[perms.size()];
                                for (int j = 0; j < perms.size(); j++) {
                                    againPerms[j] = perms.get(j);
                                }
                                requestPermission(againPerms);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create().show();
            }
        }, perms);
    }
}
