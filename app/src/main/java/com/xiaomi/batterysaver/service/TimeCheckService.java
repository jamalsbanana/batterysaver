package com.xiaomi.batterysaver.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class TimeCheckService extends Service {

    private static final String TAG = "TimeCheckService";
    private static final long CHECK_INTERVAL = 60000; // 1 minute interval
    private Handler handler;
    private Runnable checkRunnable;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        checkRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Checking time...");
                // Put your time-checking logic here
                // For demonstration, let's print the current time
                Log.d(TAG, "Current time: " + System.currentTimeMillis());
                handler.postDelayed(this, CHECK_INTERVAL);
            }
        };
        handler.post(checkRunnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "TimeCheckService onStartCommand()");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(checkRunnable);
        Log.d(TAG, "TimeCheckService onDestroy()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
