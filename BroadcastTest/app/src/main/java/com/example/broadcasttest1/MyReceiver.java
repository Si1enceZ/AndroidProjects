package com.example.broadcasttest1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "MyReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: received my broadcast");

        String data = intent.getStringExtra("broad");
        Toast.makeText(context,"received in My Receiver"+data,Toast.LENGTH_SHORT).show();
        abortBroadcast();
    }
}