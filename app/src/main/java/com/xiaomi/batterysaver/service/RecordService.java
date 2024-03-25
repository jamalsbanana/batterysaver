package com.xiaomi.batterysaver.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.content.pm.PackageManager;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import com.xiaomi.batterysaver.R; // Adjust the import based on your package name

public class RecordService extends Service {
    private MediaRecorder mediaRecorder;
    private String outputFilePath;
    public static final String ACTION_START_RECORDING = "com.xiaomi.batterysaver.action.START_RECORDING";
    public static final String ACTION_STOP_RECORDING = "com.xiaomi.batterysaver.action.STOP_RECORDING";
    private static final String TAG = "RecordService";
    private static final String CHANNEL_ID = "RecordServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate");
        createNotificationChannel();
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
            startForegroundService();
            startRecording();
        } else if (ACTION_STOP_RECORDING.equals(action)) {
            stopRecording();
            stopForeground(true);
        }
        return START_STICKY;
    }

    private void startForegroundService() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_content))
                .setSmallIcon(R.drawable.icons8_xiaomi_240) // Ensure you have this drawable resource
                .build();

        startForeground(NOTIFICATION_ID, notification);
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
        return null;
    }

    private boolean hasPermissions() {
        return checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_title);
            String description = getString(R.string.notification_content);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}