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
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.audiorecorder.voicerecorderhd.editor.MainActivity;
import com.audiorecorder.voicerecorderhd.editor.R;
import com.audiorecorder.voicerecorderhd.editor.data.DBQuerys;
import com.audiorecorder.voicerecorderhd.editor.utils.Constants;

import java.io.File;
import java.io.IOException;
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
        Log.e("SQL", "insertSQL: "+ audioName );
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
            IntentFilter quickPOFF = new IntentFilter("android.intent.action.ACTION_SHUTDOWN");
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

    public void createFile() {
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

        }
    }

    public void setupMediaRecorder() {

        SharedPreferences sharedPreferences = this.getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            int checkStatus = sharedPreferences.getInt(Constants.K_FORMAT_TYPE, 0);
            mAudioRecorder = new MediaRecorder();
            mAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            if (checkStatus == 0) {
                mAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                mAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            } else if (checkStatus == 1) {
                mAudioRecorder.setOutputFormat(AudioFormat.CHANNEL_OUT_MONO);
                mAudioRecorder.setAudioEncoder(AudioFormat.ENCODING_PCM_16BIT);

            }
            mAudioRecorder.setAudioChannels(1);
            int checkQuality = sharedPreferences.getInt(Constants.K_FORMAT_QUALITY, 16);
            if (checkQuality == 16) {
                mAudioRecorder.setAudioEncodingBitRate(16);
                mAudioRecorder.setAudioSamplingRate(16 * Constants.K_SAMPLE_RATE_QUALITY);

            } else if (checkQuality == 22) {
                mAudioRecorder.setAudioEncodingBitRate(24);
                mAudioRecorder.setAudioSamplingRate(24 * Constants.K_SAMPLE_RATE_QUALITY);

            } else if (checkQuality == 32) {
                mAudioRecorder.setAudioEncodingBitRate(32);
                mAudioRecorder.setAudioSamplingRate(32 * Constants.K_SAMPLE_RATE_QUALITY);

            } else if (checkQuality == 44) {
                mAudioRecorder.setAudioEncodingBitRate(192000);
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
                mAudioRecorder.reset();
                mAudioRecorder.release();
                mAudioRecorder = null;
                recordFile = null;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Null Media File", Toast.LENGTH_SHORT).show();
        }
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
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentTitle("Recording")
                .setLocalOnly(true)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_record, 1)
                .setCustomContentView(remoteViews)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, mBuilder);

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
                try {
                    unregisterReceiver(notificationReceiver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stopSelf();

            } else if (Constants.RESUME_ACTION.equals(action) && isRunning == false) {

                isRunning = true;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

                    stopForeground(true);
                }
                createNotification();
                resumeRecording();
                setPauseStatus(0);
                continueCouter();

            } else if (action.equals("android.intent.action.ACTION_SHUTDOWN") && isRunning) {
                Log.e("Test", "onReadyStart: " + action);
                //Do something here
                stopRecording();
                stopCounter();
                getAudioFileSize();
                insertSQL();
            }
        }
    }
    public class InterceptCall extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)){
                    Toast.makeText(context, "is calling", Toast.LENGTH_SHORT).show();
                }
                if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)){
                    Toast.makeText(context, "end calling", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){}
        }
    }

}
