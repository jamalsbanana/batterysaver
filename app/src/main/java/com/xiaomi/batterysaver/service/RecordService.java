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
    public static final String ACTION_START_RECORDING = "com.xiaomi.batterysaver.action.START_RECORDING";
    public static final String ACTION_STOP_RECORDING = "com.xiaomi.batterysaver.action.STOP_RECORDING";
    private MediaRecorder mediaRecorder;

    @Override
    public void onCreate() {
        super.onCreate();
        if (hasPermissions()) {
            setupMediaRecorder();
        }
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

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        File outputFile = getExternalFilesDir(null);
        if (outputFile != null) {
            String outputFilePath = outputFile.getAbsolutePath() + "/audio_record_" + System.currentTimeMillis() + ".mp4";
            mediaRecorder.setOutputFile(outputFilePath);
        } else {
            Log.e("RecordService", "External files dir is null");
        }
    }

    private void startRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                Log.e("RecordService", "Error starting media recorder", e);
            }
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
            } catch (RuntimeException stopException) {
                Log.e("RecordService", "Error stopping media recorder", stopException);
            } finally {
                mediaRecorder = null;
            }
        }
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }
}