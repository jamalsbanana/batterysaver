package com.xiaomi.batterysaver.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            Log.i("ScreenStateReceiver", "Screen off detected");
            // Handle screen off event
        } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            Log.i("ScreenStateReceiver", "Screen on detected");
            // Handle screen on event
        }
    }
}