package com.xiaomi.batterysaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.xiaomi.batterysaver.service.RecordService;

public class ScreenStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            Intent startIntent = new Intent(context, RecordService.class);
            startIntent.setAction(RecordService.ACTION_START_RECORDING);
            context.startService(startIntent);
        } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            Intent stopIntent = new Intent(context, RecordService.class);
            stopIntent.setAction(RecordService.ACTION_STOP_RECORDING);
            context.startService(stopIntent);
        }
    }
}