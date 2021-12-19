package com.example.broadcasttest1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver2 extends BroadcastReceiver {
    private static final String TAG = "MyReceiver2";

    @Override
    public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: received my broadcast2");
            Toast.makeText(context,"received in Another Receiver",Toast.LENGTH_SHORT).show();

    }
}