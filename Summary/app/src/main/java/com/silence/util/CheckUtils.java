package com.silence.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class CheckUtils {
    /**
     * 一些常用的检测姿势，反调试
     */
    private static final String TAG = "CheckUtil";
    public static boolean CheckRoot(){
        return (CheckSu() || whichSu());
    }
    public static boolean CheckSu(){
        /*
            通过在常见的su文件路径创建文件，来判断是否存在使用root用户
         */
        String[] suPaths = new String[]{"/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su", "/system/usr/we-need-root/su", "/system/bin/.ext/su"};
        for(String path:suPaths){
            if(new File(path).exists()){
                Log.e(TAG, "CheckSu: " + path + "Exists");
                return true;
            }
        }
        return  false;
    }

    public static boolean whichSu(){
        /*
            通过判断读取执行“which su”的结果是否为空来判断是否有“su”
         */
        Process process = null;
        try{
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which","su"});
            boolean result = new BufferedReader(new InputStreamReader(process.getInputStream())).readLine()!=null;
            process.destroy();
            return result;
        } catch (IOException e) {
            if (process!=null){
                process.destroy();
            }
            return false;
        }

    }
}
