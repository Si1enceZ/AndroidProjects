package com.silence.util;

import android.content.Context;
import android.content.Intent;
import android.util.ArrayMap;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class ClassLoaderUtils {
    private static final String TAG = "ClassLoaderUtils";
    public static boolean setClassLoader(Context context, ClassLoader classLoader){
        try{
            Class ActivityThreadClass = classLoader.loadClass("android.app.ActivityThread");
            Method currentThread = ActivityThreadClass.getDeclaredMethod("currentActivityThread");
            Object activityThread = currentThread.invoke(null);

            Field packages = ActivityThreadClass.getDeclaredField("mPackages");
            packages.setAccessible(true);
            ArrayMap mPackagesMap = (ArrayMap) packages.get(activityThread);
            WeakReference weakReference = (WeakReference) mPackagesMap.get(context.getPackageName());
            Object loadedApk = weakReference.get();

            Class<?> loadedApkClass = classLoader.loadClass("android.app.LoadedApk");
            Field clz = loadedApkClass.getDeclaredField("mClassLoader");

            clz.setAccessible(true);
            Log.d(TAG, "packageName: " + loadedApkClass);
            Log.d(TAG, "classLoader: " + classLoader);
            Log.d(TAG, "setClassLoader Before: " + clz);
            clz.set(loadedApk,classLoader);

            Log.d(TAG, "setClassLoader After : " + clz);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean replaceClassLoader(Context context, ClassLoader classLoader) {
        Class<?> ActivityThreadClass = null;
        try {
            ActivityThreadClass = classLoader.loadClass("android.app.ActivityThread");
            Method currentThread = ActivityThreadClass.getDeclaredMethod("currentActivityThread");
            Object activityThread = currentThread.invoke(null);
            Field mPackages = ActivityThreadClass.getDeclaredField("mPackages");
            mPackages.setAccessible(true);
            ArrayMap mPackagesMap = (ArrayMap) mPackages.get(activityThread);
            assert mPackagesMap != null;
            WeakReference weakReference = (WeakReference) mPackagesMap.get(context.getPackageName());
            assert weakReference != null;
            Object loadedApk = weakReference.get();
            Class<?> loadedApkClass = classLoader.loadClass("android.app.LoadedApk");
            Field mClassLoader = loadedApkClass.getDeclaredField("mClassLoader");
            Log.d(TAG, "packageName: " + loadedApkClass);
            Log.d(TAG, "classLoader: " + classLoader);
            Log.d(TAG, "setClassLoader Before: " + mClassLoader);
            mClassLoader.setAccessible(true);
            mClassLoader.set(loadedApk,classLoader);
            Log.d(TAG, "setClassLoader After : " + mClassLoader);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | NoSuchFieldException e) {

            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void startDexActivityWithoutReplacedClassLoader(Context context, String dexFilePath) {
        // ????????????

        File optFile = new File(context.getFilesDir(), "opt_dex");
        // ??????????????? , ???????????? so ??????
        File libFile = new File(context.getFilesDir(), "lib_path");

        // ????????? DexClassLoader
        DexClassLoader dexClassLoader = new DexClassLoader(
                dexFilePath,                    // Dex ?????????????????????
                optFile.getAbsolutePath(),      // ????????????
                libFile.getAbsolutePath(),      // ???????????????
                context.getClassLoader()        // ?????????????????????
        );

        //------------------------------------------------------------------------------------------
        // ?????????????????? LoadedApk ?????? ClassLoader

        // I. ?????? ActivityThread ????????????


        // ?????? ActivityThread ???????????? , ????????????????????????????????????????????????
        // ????????? ?????? ?????????????????? , ???????????? DexClassLoader ???????????? , ???????????????????????????
        // ???????????????????????? , ??????????????????????????????
        Class<?> ActivityThreadClass = null;
        try {
            ActivityThreadClass = dexClassLoader.loadClass(
                    "android.app.ActivityThread");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // ?????? ActivityThread ?????? sCurrentActivityThread ??????
        // ????????????????????? :
        // private static volatile ActivityThread sCurrentActivityThread;
        // ??????????????????????????? :
        // public static ActivityThread currentActivityThread() {return sCurrentActivityThread;}
        Method currentActivityThreadMethod = null;
        try {
            currentActivityThreadMethod = ActivityThreadClass.getDeclaredMethod(
                    "currentActivityThread");

            // ?????????????????? , ????????? ?????? , ?????? ?????? , ????????????????????????
            currentActivityThreadMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        // ?????? ActivityThread ??? currentActivityThread() ?????? , ???????????? null
        Object activityThreadObject = null;
        try {
            activityThreadObject = currentActivityThreadMethod.invoke(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        // II. ?????? LoadedApk ????????????


        // ?????? ActivityThread ??????????????? mPackages ??????
        // final ArrayMap<String, WeakReference<LoadedApk>> mPackages = new ArrayMap<>();
        Field mPackagesField = null;
        try {
            mPackagesField = ActivityThreadClass.getDeclaredField("mPackages");

            // ?????????????????? , ????????? ?????? , ?????? ?????? , ????????????????????????
            mPackagesField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        // ??? ActivityThread ???????????? activityThreadObject ???
        // ?????? mPackages ??????
        ArrayMap mPackagesObject = null;
        try {
            mPackagesObject = (ArrayMap) mPackagesField.get(activityThreadObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // ?????? WeakReference<LoadedApk> ???????????????
        WeakReference weakReference = (WeakReference) mPackagesObject.get(context.getPackageName());
        // ?????? LoadedApk ????????????
        Object loadedApkObject = weakReference.get();


        // III. ?????? LoadedApk ?????????????????? mClassLoader ????????????


        // ?????? android.app.LoadedApk ???
        Class LoadedApkClass = null;
        try {
            LoadedApkClass = dexClassLoader.loadClass("android.app.LoadedApk");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // ?????????????????? private ClassLoader mClassLoader; ??????????????????
        Field mClassLoaderField = null;
        try {
            mClassLoaderField = LoadedApkClass.getDeclaredField("mClassLoader");

            // ??????????????????
            mClassLoaderField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        // ?????? mClassLoader ??????
        try {
            mClassLoaderField.set(loadedApkObject, dexClassLoader);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        //------------------------------------------------------------------------------------------

        // ?????? com.example.dex_demo.DexTest ???
        // ??????????????????????????? test()
        Class<?> clazz = null;
        try {
            clazz = dexClassLoader.loadClass("com.mrctf.android2022.MainActivity");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // ?????? com.example.dex_demo.MainActivity2 ??????
        if (clazz != null) {
            context.startActivity(new Intent(context, clazz));
        }
    }


    public static void getParents(ClassLoader classLoader){
        Log.d(TAG, "getParents: 0\t"+classLoader);
        Log.d(TAG, "getParents: 1\t"+classLoader.getParent());
        Log.d(TAG, "getParents: 2\t"+classLoader.getParent().getParent());
    }
}
