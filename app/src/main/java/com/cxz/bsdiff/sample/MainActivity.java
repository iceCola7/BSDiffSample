package com.cxz.bsdiff.sample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cxz.bsdiff.sample.utils.UriParseUtils;
import com.cxz.bspatchlib.BsPatcher;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv_version = findViewById(R.id.tv_version);
        tv_version.setText(BuildConfig.VERSION_NAME);

        // 运行时权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String perms[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms, 1000);
            }
        }

    }

    public void update(View view) {
        // 从服务器下载 patch 到用户手机， SDCard 里面
        new AsyncTask<Void, Void, File>() {
            @Override
            protected File doInBackground(Void... voids) {
                // 获取旧版本路径（正在运行的apk路径）
                String oldApk = getApplicationInfo().sourceDir;
                String patch = new File(Environment.getExternalStorageDirectory(), "patch").getAbsolutePath();
                String output = createNewApk().getAbsolutePath();
                Log.e("oldApk---->>", oldApk);
                Log.e("patch----->>", patch);
                Log.e("output---->>", output);
                if (!new File(patch).exists()) {
                    return null;
                }
                BsPatcher.bsPatch(oldApk, patch, output);
                return new File(output);
            }

            @Override
            protected void onPostExecute(File file) {
                super.onPostExecute(file);
                // 已经合成了，调用该方法，安装新版本apk
                if (file != null) {
                    UriParseUtils.installApk(MainActivity.this, file);
                } else {
                    Toast.makeText(MainActivity.this, "差分包不存在！", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    /**
     * 创建合成后的新版本apk文件
     *
     * @return
     */
    private File createNewApk() {
        File newApk = new File(Environment.getExternalStorageDirectory(), "bsdiff.apk");
        if (!newApk.exists()) {
            try {
                newApk.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return newApk;
    }
}
