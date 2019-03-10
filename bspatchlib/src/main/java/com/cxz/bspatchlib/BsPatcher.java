package com.cxz.bspatchlib;

/**
 * @author chenxz
 * @date 2019/2/24
 * @desc
 */
public class BsPatcher {

    // 用于在应用程序启动时，加载本地的lib库
    static {
        System.loadLibrary("bspatcher");
    }

    /**
     * 合成安装包
     *
     * @param oldApk 旧版本安装包，如1.1.1版本
     * @param patch  差分包，Patch文件
     * @param output 合成后新版本apk的输出路径
     */
    public static native void bsPatch(String oldApk, String patch, String output);

}
