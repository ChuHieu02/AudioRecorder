package com.audiorecorder.voicerecorderhd.editor.service;

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
import android.content.res.Resources;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.audiorecorder.voicerecorderhd.editor.MainActivity;
import com.audiorecorder.voicerecorderhd.editor.R;
import com.audiorecorder.voicerecorderhd.editor.activity.LibraryActivity;
import com.audiorecorder.voicerecorderhd.editor.data.DBQuerys;
import com.audiorecorder.voicerecorderhd.editor.utils.AudioRecorder;
import com.audiorecorder.voicerecorderhd.editor.utils.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordService extends Service  {


    public static final String DATE_TIME_FORMAT = "HH:mm:ss_d_MM_yyyy";
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static int pauseStatus;
    public static int recordingStatus;
    public static boolean isRunning ;
    private MediaRecorder mAudioRecorder;
    private File recordFile;
    private String outputFile;
    private NotificationManager mNotificationManager;
    private Notification mBuilder;
    private Notification mCompleteNotifi;
    private long startTime = 0;
    private long millis = 0;
    private long countTimeRecord = 0;
    private DBQuerys dbQuerys;
    private String pathFile;
    private long dateTime;
    private long fileSize;
    private static String audioName;
    private static long extraCurrentTime;
    private NotificationReceiver notificationReceiver = new NotificationReceiver();
    private Handler handler = new Handler();
    private AudioRecorder audioRecorder;
    Runnable serviceRunnable = new Runnable() {
        @Override
        public void run() {
            millis = System.currentTimeMillis() - startTime;
            handler.postDelayed(this, 1000);
            countTimeRecord += 1000;
            sendTimeToReceiver();
        }
    };

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public RecordService() { }

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

    public static long getExtraCurrentTime() {
        return extraCurrentTime;
    }

    public static void setExtraCurrentTime(long extraCurrentTime) {
        RecordService.extraCurrentTime = extraCurrentTime;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    private void insertSQL(){
        dbQuerys = new DBQuerys(getApplicationContext());
        dbQuerys.insertAudioString(audioName,outputFile,getFileSize(),dateTime,countTimeRecord -200);

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
        isRunning = true;
        createNotification();
        audioRecorder = new AudioRecorder();
        startRecording();
        startCounter();
        setRecordingStatus(1);
        initReceiver();
        return START_STICKY;
    }

    private void initReceiver() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.START_ACTION);
            filter.addAction(Constants.RESUME_ACTION);
            filter.addAction(Constants.PAUSE_ACTION);
            filter.addAction(Constants.STOP_ACTION);
            filter.addAction(Constants.COMMING_PHONE_CALL_ACTION);
            filter.addAction(Constants.OPEN_ACTION);
            filter.addAction(Constants.STOP_SERVICE_ACTION);
            IntentFilter quickPOFF = new IntentFilter(Constants.POWER_OFF_ACTION);
            registerReceiver(notificationReceiver, filter);
            registerReceiver(notificationReceiver,quickPOFF);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendTimeToReceiver() {
        Intent intentTimer = new Intent(Constants.SEND_TIME);
        intentTimer.putExtra(Constants.TIME_COUNT, millis);
        sendBroadcast(intentTimer);
    }

    public void startCounter() {
        startTime = System.currentTimeMillis();
        countTimeRecord = 0;
        handler.postDelayed(serviceRunnable, 0);
    }

    public void continueCouter() {
        startTime = System.currentTimeMillis() -countTimeRecord ;
        handler.postDelayed(serviceRunnable, 350);
    }

    public void stopCounter() {
        handler.removeCallbacksAndMessages(null);
    }

    public void startRecording() {

        SharedPreferences sharedPreferences = this.getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
        if (sharedPreferences != null ) {
            int checkStatus = sharedPreferences.getInt(Constants.K_FORMAT_TYPE, 0);
            String pathDirector = sharedPreferences.getString(Constants.K_DIRECTION_CHOOSER_PATH, Environment.getExternalStorageDirectory() + File.separator + "Recorder");
            pathFile = pathDirector;
            dateTime = System.currentTimeMillis();
            recordFile = new File(pathDirector);
            Date date_Formater = new Date(System.currentTimeMillis());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
            String formatDatetime = simpleDateFormat.format(date_Formater);
            if (!recordFile.exists()) {
                recordFile.mkdirs();
            }
            if (checkStatus == 0) {
                outputFile =  recordFile.getAbsolutePath() + "/Audio-" + System.currentTimeMillis() + ".mp3";
                audioName = "Audio-" +formatDatetime + ".mp3";
                setAudioName("Audio-" +formatDatetime+".mp3");
            } else if (checkStatus == 1) {
                outputFile =  recordFile.getAbsolutePath() + "/Audio-" + System.currentTimeMillis() + ".wav";
                audioName = "Audio-" + formatDatetime + ".wav";
                setAudioName("Audio-" + formatDatetime+".wav");
            }
            int checkFormat = sharedPreferences.getInt(Constants.K_FORMAT_TYPE, 0);
            int checkBitRate = sharedPreferences.getInt(Constants.K_FORMAT_QUALITY, 16);
            audioRecorder.setupMediaRecorder(outputFile,checkBitRate,checkFormat);
            audioRecorder.startRecord();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pauseRecording() {
        audioRecorder.pasueRecord();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void resumeRecording() {
       audioRecorder.resumeRecord();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void stopRecording() {
        audioRecorder.stopRecord();
    }

    public void getAudioFileSize(){
        try {
            File file = new File(outputFile);
            setFileSize(file.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!isRunning) {
            try {
                unregisterReceiver(notificationReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void createNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );

            mNotificationManager = getSystemService(NotificationManager.class);
            mNotificationManager.createNotificationChannel(serviceChannel);
        }


        RemoteViews remoteViews = new RemoteViews(getPackageName()
                , isRunning ?  R.layout.custom_notification_action_pause : R.layout.custom_notification_aciton_resume);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2019, notificationIntent, 0);

        Intent pauseReceive = new Intent(Constants.PAUSE_ACTION);
        PendingIntent pendingIntentPause = PendingIntent.getBroadcast(this, 2019, pauseReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent resumeReceive = new Intent(Constants.RESUME_ACTION);
        PendingIntent pendingIntentResume = PendingIntent.getBroadcast(this, 2019, resumeReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopReceive = new Intent(Constants.STOP_ACTION);
        PendingIntent pendingIntentStop = PendingIntent.getBroadcast(this, 2019, stopReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.iv_notifi_pause_resume, isRunning ? pendingIntentPause : pendingIntentResume);
        remoteViews.setOnClickPendingIntent(R.id.iv_notifi_stop,pendingIntentStop);

        mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                //.setDefaults(NotificationCompat.D)
                .setContentTitle("Recording")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_record, 1)
                .setCustomContentView(remoteViews)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, mBuilder);

    }

    public void creatCompleteRecordNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            mNotificationManager = getSystemService(NotificationManager.class);
            mNotificationManager.createNotificationChannel(serviceChannel);
        }
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_complete_record_notification );

        Intent openLibararyReceive = new Intent(Constants.OPEN_ACTION);
        PendingIntent pendingIntentOpen = PendingIntent.getBroadcast(this, 2019, openLibararyReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopServiceReceive = new Intent(Constants.STOP_SERVICE_ACTION);
        PendingIntent pendingIntentStopService = PendingIntent.getBroadcast(this, 2019, stopServiceReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.iv_notifi_close, pendingIntentStopService);
        remoteViews.setOnClickPendingIntent(R.id.iv_notifi_open, pendingIntentOpen);

            mCompleteNotifi = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_record, 1)
                    .setCustomContentView(remoteViews)
                    .build();
        startForeground(1, mCompleteNotifi);
    }

    public void savePowerOffStatus(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.K_AUTO_SAVE_STATUS,true);
            editor.apply();
    }

    public class NotificationReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (Constants.PAUSE_ACTION.equals(action) && isRunning == true) {

                isRunning = false;
                pauseRecording();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    stopForeground(true);
                }
                createNotification();
                setPauseStatus(1);
                stopCounter();
                setExtraCurrentTime(countTimeRecord );

            } else if (Constants.STOP_ACTION.equals(action)) {

                isRunning = false;
                setRecordingStatus(0);
                stopRecording();
                stopCounter();
                getAudioFileSize();
                insertSQL();
                setExtraCurrentTime(0);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    stopForeground(true);
                }
                creatCompleteRecordNotification();

            } else if (Constants.RESUME_ACTION.equals(action) && isRunning == false) {

                isRunning = true;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    stopForeground(true);
                }
                createNotification();
                resumeRecording();
                setPauseStatus(0);
                continueCouter();

            } else if (Constants.POWER_OFF_ACTION.equals(action) && isRunning) {
                stopRecording();
                stopCounter();
                getAudioFileSize();
                insertSQL();
                savePowerOffStatus();
            } else if(Constants.COMMING_PHONE_CALL_ACTION.equals(action) && isRunning ){
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
                if (sharedPreferences != null) {
                    boolean checkPhoneAction = sharedPreferences.getBoolean(Constants.K_STOP_IS_CALLING,false);
                    if(checkPhoneAction){
                        isRunning = false;
                        pauseRecording();
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

                            stopForeground(true);
                        }
                        createNotification();
                        setPauseStatus(1);
                        stopCounter();
                        setExtraCurrentTime(countTimeRecord );
                    }
                }
            } else if(Constants.OPEN_ACTION.equals(action)){
                startActivity(new Intent(RecordService.this , LibraryActivity.class));
                try {
                    unregisterReceiver(notificationReceiver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stopSelf();

            } else if(Constants.STOP_SERVICE_ACTION.equals(action)){
                try {
                    unregisterReceiver(notificationReceiver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stopSelf();
            }
        }
    }

}
