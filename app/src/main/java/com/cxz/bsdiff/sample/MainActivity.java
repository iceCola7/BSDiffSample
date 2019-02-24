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

import com.cxz.bsdiff.sample.utils.UriParseUtils;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // 用于在应用程序启动时，加载本地的lib库
    static {
        System.loadLibrary("native-lib");
    }

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

    /**
     * 合成安装包
     *
     * @param oldApk 旧版本安装包，如1.1.1版本
     * @param patch  差分包，Patch文件
     * @param output 合成后新版本apk的输出路径
     */
    public native void bsPath(String oldApk, String patch, String output);

    public void update(View view) {
        // 从服务器下载 patch 到用户手机， SDCard 里面
        new AsyncTask<Void, Void, File>() {
            @Override
            protected File doInBackground(Void... voids) {
                // 获取旧版本路径（正在运行的apk路径）
                String oldApk = getApplicationInfo().sourceDir;
                String patch = new File(Environment.getExternalStorageDirectory(), "patch").getAbsolutePath();
                String output = createNewApk().getAbsolutePath();
                Log.e("oldApk---->>",oldApk);
                Log.e("patch---->>",patch);
                Log.e("output---->>",output);
                bsPath(oldApk, patch, output);
                return new File(output);
            }

            @Override
            protected void onPostExecute(File file) {
                super.onPostExecute(file);
                // 已经合成了，调用该方法，安装新版本apk
                UriParseUtils.installApk(MainActivity.this, file);
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
