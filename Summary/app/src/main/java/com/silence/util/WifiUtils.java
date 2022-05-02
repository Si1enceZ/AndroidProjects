package com.silence.util;

import static androidx.core.app.ActivityCompat.requestPermissions;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

public class WifiUtils {
    private WifiManager wifiManager = null;
    private static final String TAG = "WifiUtils";
    public WifiUtils(Activity targetActivity){
        String[] PERMS_INITIAL = {Manifest.permission.ACCESS_FINE_LOCATION};
        requestPermissions(targetActivity,PERMS_INITIAL,127);
        wifiManager = (WifiManager) targetActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    List<ScanResult> results = wifiManager.getScanResults();
                    if (results != null) {
                        Log.d(TAG, "onReceive: " + results.size());
                    }
                }
            }
        };
        targetActivity.registerReceiver(mReceiver,filter);
    }

    public boolean scan(){
        boolean scanResult = wifiManager.startScan();
        Log.d(TAG, "scanResult: " + scanResult);
        return scanResult;
    }
}
