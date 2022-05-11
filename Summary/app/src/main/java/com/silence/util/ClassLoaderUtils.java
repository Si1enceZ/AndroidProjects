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
        // 优化目录

        File optFile = new File(context.getFilesDir(), "opt_dex");
        // 依赖库目录 , 用于存放 so 文件
        File libFile = new File(context.getFilesDir(), "lib_path");

        // 初始化 DexClassLoader
        DexClassLoader dexClassLoader = new DexClassLoader(
                dexFilePath,                    // Dex 字节码文件路径
                optFile.getAbsolutePath(),      // 优化目录
                libFile.getAbsolutePath(),      // 依赖库目录
                context.getClassLoader()        // 父节点类加载器
        );

        //------------------------------------------------------------------------------------------
        // 下面开始替换 LoadedApk 中的 ClassLoader

        // I. 获取 ActivityThread 实例对象


        // 获取 ActivityThread 字节码类 , 这里可以使用自定义的类加载器加载
        // 原因是 基于 双亲委派机制 , 自定义的 DexClassLoader 无法加载 , 但是其父类可以加载
        // 即使父类不可加载 , 父类的父类也可以加载
        Class<?> ActivityThreadClass = null;
        try {
            ActivityThreadClass = dexClassLoader.loadClass(
                    "android.app.ActivityThread");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // 获取 ActivityThread 中的 sCurrentActivityThread 成员
        // 获取的字段如下 :
        // private static volatile ActivityThread sCurrentActivityThread;
        // 获取字段的方法如下 :
        // public static ActivityThread currentActivityThread() {return sCurrentActivityThread;}
        Method currentActivityThreadMethod = null;
        try {
            currentActivityThreadMethod = ActivityThreadClass.getDeclaredMethod(
                    "currentActivityThread");

            // 设置可访问性 , 所有的 方法 , 字段 反射 , 都要设置可访问性
            currentActivityThreadMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        // 执行 ActivityThread 的 currentActivityThread() 方法 , 传入参数 null
        Object activityThreadObject = null;
        try {
            activityThreadObject = currentActivityThreadMethod.invoke(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        // II. 获取 LoadedApk 实例对象


        // 获取 ActivityThread 实例对象的 mPackages 成员
        // final ArrayMap<String, WeakReference<LoadedApk>> mPackages = new ArrayMap<>();
        Field mPackagesField = null;
        try {
            mPackagesField = ActivityThreadClass.getDeclaredField("mPackages");

            // 设置可访问性 , 所有的 方法 , 字段 反射 , 都要设置可访问性
            mPackagesField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        // 从 ActivityThread 实例对象 activityThreadObject 中
        // 获取 mPackages 成员
        ArrayMap mPackagesObject = null;
        try {
            mPackagesObject = (ArrayMap) mPackagesField.get(activityThreadObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // 获取 WeakReference<LoadedApk> 弱引用对象
        WeakReference weakReference = (WeakReference) mPackagesObject.get(context.getPackageName());
        // 获取 LoadedApk 实例对象
        Object loadedApkObject = weakReference.get();


        // III. 替换 LoadedApk 实例对象中的 mClassLoader 类加载器


        // 加载 android.app.LoadedApk 类
        Class LoadedApkClass = null;
        try {
            LoadedApkClass = dexClassLoader.loadClass("android.app.LoadedApk");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // 通过反射获取 private ClassLoader mClassLoader; 类加载器对象
        Field mClassLoaderField = null;
        try {
            mClassLoaderField = LoadedApkClass.getDeclaredField("mClassLoader");

            // 设置可访问性
            mClassLoaderField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        // 替换 mClassLoader 成员
        try {
            mClassLoaderField.set(loadedApkObject, dexClassLoader);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        //------------------------------------------------------------------------------------------

        // 加载 com.example.dex_demo.DexTest 类
        // 该类中有可执行方法 test()
        Class<?> clazz = null;
        try {
            clazz = dexClassLoader.loadClass("com.mrctf.android2022.MainActivity");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // 启动 com.example.dex_demo.MainActivity2 组件
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
