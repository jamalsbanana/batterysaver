package com.xiaomi.batterysaver.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Received intent with action: " + intent.getAction());
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.i(TAG, "Boot completed event received");
            // Here, you can start your service or perform other actions upon boot completion
            Log.d(TAG, "onReceive: Initiating actions post boot completion");
            // Example: Starting RecordService
            // Intent serviceIntent = new Intent(context, RecordService.class);
            // context.startService(serviceIntent);
            // Log.d(TAG, "RecordService has been started after boot");
        } else {
            Log.w(TAG, "Received unexpected intent action: " + intent.getAction());
        }
    }
}