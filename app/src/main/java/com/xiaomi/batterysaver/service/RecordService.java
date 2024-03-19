package com.xiaomi.batterysaver.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;

import java.io.IOException;

public class RecordService extends Service {
    public static final String ACTION_START_RECORDING = "com.xiaomi.batterysaver.action.START_RECORDING";
    public static final String ACTION_STOP_RECORDING = "com.xiaomi.batterysaver.action.STOP_RECORDING";
    private MediaRecorder mediaRecorder;
    private final Handler mHandler = new Handler(Looper.getMainLooper()); // Updated to use Looper.getMainLooper()

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
        String outputFile = null;
        if (getExternalFilesDir(null) != null) {
            outputFile = getExternalFilesDir(null).getAbsolutePath() + "/audio_record_" + System.currentTimeMillis() + ".mp4";
        }
        if (outputFile != null) {
            mediaRecorder.setOutputFile(outputFile);
        }
    }

    private void startRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
            } catch (RuntimeException stopException) {
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