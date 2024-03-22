package com.xiaomi.batterysaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.xiaomi.batterysaver.service.RecordService;
import java.util.Calendar;

public class ScreenStateReceiver extends BroadcastReceiver {
    private static final String TAG = "ScreenStateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            Log.d(TAG, "Screen ON");
            if (isWithinRecordingTime()) {
                Log.d(TAG, "Within recording time window. Starting RecordService.");
                Intent serviceIntent = new Intent(context, RecordService.class);
                context.startService(serviceIntent);
            } else {
                Log.d(TAG, "Outside recording time window.");
            }
        }
    }

    private boolean isWithinRecordingTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        // Assuming you want to record between 9 PM (21) and 8 AM (8)
        return hour >= 21 || hour < 8;
    }
}