package com.xiaomi.batterysaver.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.util.Log;
import java.io.File;
import java.io.IOException;

public class RecordService extends Service {
    private MediaRecorder mediaRecorder;
    private String outputFilePath;
    public static final String ACTION_START_RECORDING = "com.xiaomi.batterysaver.action.START_RECORDING";
    public static final String ACTION_STOP_RECORDING = "com.xiaomi.batterysaver.action.STOP_RECORDING";
    private static final String TAG = "RecordService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate");
        if (hasPermissions()) {
            setupMediaRecorder();
        } else {
            Log.e(TAG, "Recording permissions are not granted");
        }
    }

    private void setupMediaRecorder() {
        Log.d(TAG, "Setting up MediaRecorder");
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        File outputFile = new File(getExternalFilesDir(null), "audio_record_" + System.currentTimeMillis() + ".mp4");
        outputFilePath = outputFile.getAbsolutePath();
        mediaRecorder.setOutputFile(outputFilePath);
        Log.d(TAG, "MediaRecorder setup complete. Output file: " + outputFilePath);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String action = intent.getAction();
        Log.d(TAG, "onStartCommand received with action: " + action);
        if (ACTION_START_RECORDING.equals(action)) {
            startRecording();
        } else if (ACTION_STOP_RECORDING.equals(action)) {
            stopRecording();
        }
        return START_STICKY;
    }

    private void startRecording() {
        Log.d(TAG, "Attempting to start recording");
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            Log.d(TAG, "Recording started successfully");
        } catch (IOException e) {
            Log.e(TAG, "Error starting media recorder", e);
        }
    }

    private void stopRecording() {
        Log.d(TAG, "Attempting to stop recording");
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            Log.d(TAG, "Recording stopped and MediaRecorder resources released");
        } else {
            Log.d(TAG, "MediaRecorder was null when attempting to stop recording");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind called");
        return null;
    }

    private boolean hasPermissions() {
        boolean hasPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        Log.d(TAG, "Checking permissions: " + (hasPermission ? "Granted" : "Denied"));
        return hasPermission;
    }
}