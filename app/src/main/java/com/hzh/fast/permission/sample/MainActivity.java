package com.hzh.fast.permission.sample;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hzh.fast.permission.PermissionManager;
import com.hzh.fast.permission.callback.PermissionCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String[] PermissionList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            PermissionList = new String[]
                    {Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE};
        } else {
            PermissionList = new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE,};
        }

        Button request = (Button) findViewById(R.id.request);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionManager.getInstance().request(MainActivity.this, new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        Toast.makeText(MainActivity.this, "申请权限成功，可进行下一步操作", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDenied(List<String> perms) {
                        for (int i = 0; i < perms.size(); i++) {
                            Log.d(TAG, " permission onDenied ::: ".concat(perms.get(i)));
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("申请权限")
                                .setMessage("申请权限被拒绝，请允许")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        builder.create().show();
                    }
                }, PermissionList);
            }
        });
    }
}
