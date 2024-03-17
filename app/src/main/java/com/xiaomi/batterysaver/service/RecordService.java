package com.xiaomi.batterysaver.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

public class RecordService extends Service {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final String TAG = "RecordService";

    private MediaRecorder mediaRecorder;
    private static final long RECORD_INTERVAL = 60 * 1000; // 1 minute interval
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Starting recording...");
            if (hasPermissions()) {
                startRecording();
            } else {
                Log.d(TAG, "Permissions not granted, recording cannot start.");
                // Request permissions from the user by starting the PermissionActivity
                Intent intent = new Intent(RecordService.this, PermissionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            handler.postDelayed(this, RECORD_INTERVAL);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, buildNotification());
        handler.post(recordRunnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "RecordService onStartCommand()");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        handler.removeCallbacks(recordRunnable);
        Log.d(TAG, "RecordService onDestroy()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ObsoleteSdkInt")
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
        Log.d(TAG, "RecordService createNotificationChannel()");
    }

    private Notification buildNotification() {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Recording audio")
                .setContentIntent(pendingIntent)
                .setContentText("Audio is being recorded in the background")
                .setSmallIcon(android.R.drawable.ic_btn_speak_now) // Change to your custom icon
                .build();
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void startRecording() {
        File filesDirectory = getFilesDir();
        String fileName = "audio_record_" + System.currentTimeMillis() + ".3gp";
        File audioFile = new File(filesDirectory, fileName);
        String filePath = audioFile.getAbsolutePath();

        mediaRecorder = new MediaRecorder(this); // Use the Context constructor
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(filePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start(); // Start recording immediately after prepare
            Log.d(TAG, "Recording started");
        } catch (IOException e) {
            Log.e(TAG, "Error preparing media recorder", e);
        }
    }


}
