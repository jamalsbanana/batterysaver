package com.xiaomi.batterysaver.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;

public class ScreenStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            if (isWithinTimeRange()) {
                // Start RecordService if within the specified time range
                Intent startIntent = new Intent(context, RecordService.class);
                startIntent.setAction(RecordService.ACTION_START_RECORDING);
                context.startService(startIntent);
            }
        }
    }

    private boolean isWithinTimeRange() {
        // Define your time range here
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // 24-hour format
        // Example: Start recording between 9 PM (21) and 5 AM (5)
        return (hour >= 21 || hour <= 5);
    }
}