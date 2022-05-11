package com.silence.summary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.silence.summary.databinding.ActivityMainBinding;
import com.silence.util.ClassLoaderUtils;
import com.silence.util.WifiUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import dalvik.system.DexClassLoader;
import dalvik.system.InMemoryDexClassLoader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Used to load the 'summary' library on application startup.
    static {
        System.loadLibrary("summary");
    }


    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Example of a call to a native method
        Button wifiTest = findViewById(R.id.wifi);
        wifiTest.setOnClickListener(this);
        Button classLoaderTest = findViewById(R.id.classLoader);
        classLoaderTest.setOnClickListener(this);
    }

    /**
     * A native method that is implemented by the 'summary' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.wifi:
                WifiUtils wifiUtils = new WifiUtils(MainActivity.this);
                wifiUtils.scanCheck();
                wifiUtils.getWifiList();
                break;

            case R.id.classLoader:
                Log.d(TAG, "Before: " + this.getClassLoader());
                try{
                    InputStream is = this.getAssets().open("build.dex");

                    int size = is.available();
                    byte[] dex = new byte[size];
                    is.read(dex);
                    is.close();
                    String savePath = this.getApplicationContext().getCodeCacheDir().getAbsolutePath()+ File.separator+"build.dex";
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(savePath));
                    bos.write(dex,0,size);
//                    bos.flush();
                    bos.close();
                    InMemoryDexClassLoader classLoader1 = new InMemoryDexClassLoader(ByteBuffer.wrap(dex),this.getClassLoader());
//                    DexClassLoader classLoader1 = new DexClassLoader(savePath,this.getApplicationContext().getCodeCacheDir().getAbsolutePath(),null,this.getClassLoader());
                    Log.d(TAG, "classLoader1: " + classLoader1);
                    ClassLoaderUtils.getParents(classLoader1);
                    Class clz1 = classLoader1.loadClass("com.mrctf.android2022.MainActivity");

                    if(ClassLoaderUtils.setClassLoader(this.getApplicationContext(),classLoader1)){
                        Log.d(TAG, "onClick: setClassLoader Successfully");
                    }else{
                        Log.d(TAG, "onClick: setClassLoader Failed");
                    }
                    Log.d(TAG, "After : " + this.getClassLoader());
                    this.getApplicationContext().startActivity(new Intent(this.getApplicationContext(),clz1));
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;

        }

    }



}