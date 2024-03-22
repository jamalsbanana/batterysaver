package com.xiaomi.batterysaver.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.Calendar;

public class TimeCheckService extends Service {

    private AlarmManager alarmManager;
    private PendingIntent startPendingIntent;
    private PendingIntent stopPendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent startIntent = new Intent(this, RecordService.class);
        startPendingIntent = PendingIntent.getService(this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent(this, RecordService.class);
        stopIntent.setAction("STOP_RECORDING");
        stopPendingIntent = PendingIntent.getService(this, 1, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        scheduleRecording();
    }

    private void scheduleRecording() {
        long startAt = calculateNextStartTime();
        long stopAt = calculateNextStopTime();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    // Handle the case where the app cannot schedule exact alarms
                    // You might want to notify the user or request permission
                    requestScheduleExactAlarmPermission();
                    return;
                }
            }
            try {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startAt, startPendingIntent);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, stopAt, stopPendingIntent);
            } catch (SecurityException e) {
                // Handle the SecurityException if the app lacks the necessary permission
                // This is a fallback in case the permission check fails or is bypassed
            }
        } else {
            try {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, startAt, startPendingIntent);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, stopAt, stopPendingIntent);
            } catch (SecurityException e) {
                // Handle the SecurityException for versions below Android S
            }
        }
    }

    private void requestScheduleExactAlarmPermission() {
        Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scheduleRecording();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        alarmManager.cancel(startPendingIntent);
        alarmManager.cancel(stopPendingIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private long calculateNextStartTime() {
        Calendar nextStart = Calendar.getInstance();
        nextStart.set(Calendar.HOUR_OF_DAY, 21); // 9 PM
        nextStart.set(Calendar.MINUTE, 0);
        nextStart.set(Calendar.SECOND, 0);
        nextStart.set(Calendar.MILLISECOND, 0);
        if (nextStart.before(Calendar.getInstance())) {
            nextStart.add(Calendar.DATE, 1); // Add a day if it's already past 9 PM
        }
        return nextStart.getTimeInMillis();
    }

    private long calculateNextStopTime() {
        Calendar nextStop = Calendar.getInstance();
        nextStop.set(Calendar.HOUR_OF_DAY, 8); // 8 AM
        nextStop.set(Calendar.MINUTE, 0);
        nextStop.set(Calendar.SECOND, 0);
        nextStop.set(Calendar.MILLISECOND, 0);
        if (nextStop.before(Calendar.getInstance())) {
            nextStop.add(Calendar.DATE, 1); // Ensure it's always in the future!
        }
        return nextStop.getTimeInMillis();
    }
}