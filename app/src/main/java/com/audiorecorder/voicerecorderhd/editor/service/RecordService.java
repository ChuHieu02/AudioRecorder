package com.audiorecorder.voicerecorderhd.editor.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.audiorecorder.voicerecorderhd.editor.MainActivity;
import com.audiorecorder.voicerecorderhd.editor.R;
import com.audiorecorder.voicerecorderhd.editor.utils.Constants;

import java.io.File;
import java.io.IOException;

public class RecordService extends Service {

    private MediaRecorder mAudioRecorder;
    private String outputFile;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static int pauseStatus;
    public static int recordingStatus;
    public static boolean isRunning = false;
    private NotificationManager mNotificationManager;
    private Notification mBuilder;
    private long startTime = 0;
    private long millis = 0;
    private long countTimeRecord = 0;
    private  Handler handler = new Handler();
    private NotificationReceiver notificationReceiver = new NotificationReceiver();


    public RecordService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotificationChannel();
        createNotification();
        startRecording();
        startCounter();
        setRecordingStatus(1);
        isRunning = true;
        initReceiver();
        startForeground(1, mBuilder);
        return START_STICKY;
    }


    private void initReceiver() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.START_ACTION);
            filter.addAction(Constants.RESUME_ACTION);
            filter.addAction(Constants.PAUSE_ACTION);
            filter.addAction(Constants.STOP_ACTION);
            registerReceiver(notificationReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  class NotificationReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("Test", "onReadyStart: "+action );
            if (Constants.PAUSE_ACTION.equals(action) ) {

                pauseRecording();
                setIsRunning(false);
                setPauseStatus(1);
                stopCounter();

            } else if (Constants.STOP_ACTION.equals(action)) {

                setRecordingStatus(0);
                stopRecording();
                stopCounter();
                stopSelf();
                unregisterReceiver(notificationReceiver);

            } else if(Constants.RESUME_ACTION.equals(action) ){

                resumeRecording();
                setPauseStatus(0);
                continueCouter();

            } else if(Constants.START_ACTION.equals(action)){

                Log.e("Test", "onReadyStart: 1212" );
//                startRecording();
//                startCounter();
//                setRecordingStatus(1);

            }
        }
    }

    private void sendTimeToReceiver(){
        Intent intentTimer = new Intent();
        intentTimer.setAction(Constants.SEND_TIME);
        intentTimer.putExtra(Constants.TIME_COUNT, millis);
        sendBroadcast(intentTimer);
    }

    public void startCounter(){
        startTime = System.currentTimeMillis();
        countTimeRecord = 0;
        handler.postDelayed(serviceRunnable, 0);

    }
    public void continueCouter(){
        startTime = System.currentTimeMillis() - countTimeRecord;
        handler.postDelayed(serviceRunnable, 0);
    }

    public void stopCounter(){
        handler.removeCallbacks(serviceRunnable);
    }

    Runnable serviceRunnable = new Runnable() {
        @Override
        public void run() {
            millis = System.currentTimeMillis() - startTime;
            countTimeRecord += 1000;
            sendTimeToReceiver();
            handler.postDelayed(this, 1000);
        }
    };
    public static boolean isIsRunning() {
        return isRunning;
    }

    public static void setIsRunning(boolean isRunning) {
        RecordService.isRunning = isRunning;
    }

    public static int getPauseStatus() {
        return pauseStatus;
    }

    public static void setPauseStatus(int pauseStatus) {
        RecordService.pauseStatus = pauseStatus;
    }

    public static int getRecordingStatus() {
        return recordingStatus;
    }

    public static void setRecordingStatus(int recordingStatus) {
        RecordService.recordingStatus = recordingStatus;
    }

    public void createFile() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            int checkStatus = sharedPreferences.getInt(Constants.K_FORMAT_TYPE, 0);
            String pathDirector = sharedPreferences.getString(Constants.K_DIRECTION_CHOOSER_PATH, Environment.getExternalStorageDirectory() + File.separator + "Recorder");
            File file = new File(pathDirector);
            if (checkStatus == 0) {
                outputFile = "/" + file.getAbsolutePath() + "/RecordFile" + System.currentTimeMillis() + ".mp3";
            } else if (checkStatus == 1) {
                outputFile = "/" + file.getAbsolutePath() + "/RecordFile" + System.currentTimeMillis() + ".wav";
            }
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    public void setupMediaRecorder() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            int checkStatus = sharedPreferences.getInt(Constants.K_FORMAT_TYPE, 0);
            mAudioRecorder = new MediaRecorder();
            mAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            if (checkStatus == 0) {
                mAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.MPEG_4);

            } else if (checkStatus == 1) {
                mAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.MPEG_4);

            }
            int checkQuality = sharedPreferences.getInt(Constants.K_FORMAT_QUALITY, 16);
            if (checkQuality == 16) {
                mAudioRecorder.setAudioEncodingBitRate(16);
                mAudioRecorder.setAudioSamplingRate(16 * Constants.K_SAMPLE_RATE_QUALITY);

            } else if (checkQuality == 22) {
                mAudioRecorder.setAudioEncodingBitRate(22);
                mAudioRecorder.setAudioSamplingRate(22 * Constants.K_SAMPLE_RATE_QUALITY);

            } else if (checkQuality == 32) {
                mAudioRecorder.setAudioEncodingBitRate(32);
                mAudioRecorder.setAudioSamplingRate(32 * Constants.K_SAMPLE_RATE_QUALITY);

            } else if (checkQuality == 44) {
                mAudioRecorder.setAudioEncodingBitRate(44);
                mAudioRecorder.setAudioSamplingRate(44100);

            }
        }
        mAudioRecorder.setOutputFile(outputFile);
    }

    public void startRecording() {
        createFile();
        setupMediaRecorder();
        try {
            mAudioRecorder.prepare();
            mAudioRecorder.start();
        } catch (IllegalStateException ise) {
            // make something ...
        } catch (IOException ioe) {
            // make something
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pauseRecording() {
        if (mAudioRecorder != null) {
            mAudioRecorder.pause();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void resumeRecording() {
        if (mAudioRecorder != null) {
            mAudioRecorder.resume();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void stopRecording() {
        try {
            if (mAudioRecorder != null) {
                mAudioRecorder.stop();
                mAudioRecorder.release();
                mAudioRecorder = null;
            }
        } catch (Exception e) {
           Toast.makeText(getApplicationContext(), "Null Media File", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDestroy() {
        stopRecording();
//        unregisterReceiver(notificationReceiver);
        super.onDestroy();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            mNotificationManager= getSystemService(NotificationManager.class);
            mNotificationManager.createNotificationChannel(serviceChannel);
        }

    }

    private void createNotification(){

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2019, notificationIntent, 0);

        Intent pauseReceive = new Intent(Constants.PAUSE_ACTION);
        PendingIntent pendingIntentPause = PendingIntent.getBroadcast(this, 2019, pauseReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent resumeReceive = new Intent(Constants.RESUME_ACTION);
        PendingIntent pendingIntentResume = PendingIntent.getBroadcast(this, 2019, resumeReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopReceive = new Intent(Constants.STOP_ACTION);
        PendingIntent pendingIntentStop = PendingIntent.getBroadcast(this, 2019, stopReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Recording")
                .setLocalOnly(true)
                .addAction(R.drawable.ic_play_pause, "pause", pendingIntentPause)
                .addAction(R.drawable.ic_play_play, "resume", pendingIntentResume)
                .addAction(R.drawable.ic_play_record_pr, "stop", pendingIntentStop)
                .setSmallIcon(R.drawable.ic_record, 4)
                .setContentIntent(pendingIntent)
                .build();

    }

}
