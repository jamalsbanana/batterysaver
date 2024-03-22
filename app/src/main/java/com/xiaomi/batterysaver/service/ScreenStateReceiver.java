package com.xiaomi.batterysaver.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.Calendar;

public class ScreenStateReceiver extends BroadcastReceiver {

    private static final String TAG = "ScreenStateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Received intent with action: " + intent.getAction());
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            Log.d(TAG, "onReceive: Screen turned off");
            if (isWithinTimeRange()) {
                Log.d(TAG, "onReceive: Within specified time range, starting RecordService");
                Intent startIntent = new Intent(context, RecordService.class);
                startIntent.setAction(RecordService.ACTION_START_RECORDING);
                context.startService(startIntent);
            } else {
                Log.d(TAG, "onReceive: Not within specified time range, not starting RecordService");
            }
        }
    }

    private boolean isWithinTimeRange() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // 24-hour format
        // Example: Start recording between 9 PM (21) and 5 AM (5)
        boolean withinRange = (hour >= 21 || hour <= 5);
        Log.d(TAG, "isWithinTimeRange: Current hour is " + hour + ", within range: " + withinRange);
        return withinRange;
    }
}