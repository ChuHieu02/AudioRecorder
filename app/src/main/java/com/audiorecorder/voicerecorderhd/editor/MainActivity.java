package com.audiorecorder.voicerecorderhd.editor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.audiorecorder.voicerecorderhd.editor.activity.LibraryActivity;
import com.audiorecorder.voicerecorderhd.editor.activity.SettingsActivity;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private ImageView ivBottomLibrary;
    private ImageView ivBottomRecoder;
    private ImageView ivBottomSettings;
    private MediaRecorder mAudioRecorder;
    private ImageView ivRecord , ivPauseResume;
    private String outputFile;
    private int recordingStatus;
    private int pauseStatus;
    private FrameLayout frameLayout;
    private long pauseOffsetChorno;
    private boolean isRunning;
    private Chronometer chronometerTimer;
    private int formatType;
    private long duration;
    public static final String FORMAT_TYPE = "formatType";
    public static final String FORMAT_QUALITY = "formatQuality";
    public static final  String RECORDER_FOLDER = "DemoRecorderApp";
    public static final int SAMPLE_RATR_QUALITY = 1000;
    public static final String DIRECTION_CHOOSER_PATH = "directionPath";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mappingBottomNavigation();
        createFile();
        recordingStatus = 0;
        pauseStatus = 0;
        onRecordAudio();

    }

    private void mappingBottomNavigation() {

        ivBottomLibrary = (ImageView) findViewById(R.id.iv_bottom_library);
        ivBottomRecoder = (ImageView) findViewById(R.id.iv_bottom_recoder);
        ivBottomSettings = (ImageView) findViewById(R.id.iv_bottom_settings);
        chronometerTimer = (Chronometer) findViewById(R.id.chronoTime);
        ivPauseResume =(ImageView) findViewById(R.id.imageViewPauseResume);
        ivRecord =(ImageView) findViewById(R.id.imageViewRecord);
        ivPauseResume.setEnabled(false);
        ivBottomSettings.setOnClickListener(this);
        ivBottomLibrary.setOnClickListener(this);

    }

    private void createFile() {
//        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Recorder");
        SharedPreferences sharedPreferences= this.getSharedPreferences("audioSetting", Context.MODE_PRIVATE);
        if(sharedPreferences!= null){
            int checkStatus = sharedPreferences.getInt(FORMAT_TYPE,0);
            String pathDirector = sharedPreferences.getString(DIRECTION_CHOOSER_PATH,Environment.getExternalStorageDirectory() + File.separator + "Recorder");
            File file = new File(pathDirector);
            if(checkStatus == 0){
                outputFile ="/"+ file.getAbsolutePath()+"/RecordFile"+System.currentTimeMillis()+".mp3";
            }else if(checkStatus == 1){
                outputFile ="/"+ file.getAbsolutePath()+"/RecordFile"+System.currentTimeMillis()+".wav";
            }
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    private  void  setupMediaRecorder(){
        SharedPreferences sharedPreferences= this.getSharedPreferences("audioSetting", Context.MODE_PRIVATE);
        if(sharedPreferences!= null){
            int checkStatus = sharedPreferences.getInt(FORMAT_TYPE,0);
            mAudioRecorder = new MediaRecorder();
            mAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            if(checkStatus == 0){
                mAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.MPEG_4);

            }else if(checkStatus == 1){
                mAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

            }
            int checkQuality = sharedPreferences.getInt(FORMAT_QUALITY,16);
            if(checkQuality == 16){
                mAudioRecorder.setAudioEncodingBitRate(16);
                mAudioRecorder.setAudioSamplingRate(16 * SAMPLE_RATR_QUALITY);

            }else if(checkQuality == 22){
                mAudioRecorder.setAudioEncodingBitRate(22);
                mAudioRecorder.setAudioSamplingRate(22 * SAMPLE_RATR_QUALITY);

            }else if(checkQuality == 32){
                mAudioRecorder.setAudioEncodingBitRate(32);
                mAudioRecorder.setAudioSamplingRate(32 * SAMPLE_RATR_QUALITY);

            }else if(checkQuality == 44){
                mAudioRecorder.setAudioEncodingBitRate(44);
                mAudioRecorder.setAudioSamplingRate(44100);

            }
        }
        mAudioRecorder.setOutputFile(outputFile);
    }

    private  void startRecording(){
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
        Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void pauseRecording(){
        if (mAudioRecorder!=null){
            mAudioRecorder.pause();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void resumeRecording(){
        if (mAudioRecorder!=null){
            mAudioRecorder.resume();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void stopRecording(){
        if (mAudioRecorder!=null){
            mAudioRecorder.stop();
            mAudioRecorder.release();
            mAudioRecorder = null;
            Toast.makeText(getApplicationContext(), "Audio Recorder successfully", Toast.LENGTH_LONG).show();
        }
    }

    private void playRecodingResult(){
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // make something
        }
    }

    private  void onRecordAudio(){
        ivRecord.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if(recordingStatus == 0){
                    startRecording();
                    ivRecord.setImageResource(R.drawable.ic_home_pause);
                    recordingStatus =1;
                    pauseStatus = 0;
                    ivPauseResume.setImageResource(R.drawable.ic_home_pause);
                    ivPauseResume.setEnabled(true);
                    resetChoronometer();
                    startChoronometer();
                }else {
                    if(recordingStatus == 1){
                        stopRecording();
                        ivRecord.setImageResource(R.drawable.ic_home_record);
                        recordingStatus =0;
                        pauseStatus = 0;
                        ivPauseResume.setImageResource(R.drawable.ic_home_pause);
                        ivPauseResume.setEnabled(false);
                        pauseChoronometer();
                    }
                }
            }
        });
        ivPauseResume.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if(pauseStatus == 0) {
                    pauseRecording();
                    pauseStatus = 1;
                    ivPauseResume.setImageResource(R.drawable.ic_home_play);
                    pauseChoronometer();
                    Toast.makeText(getApplicationContext(), "Pause Recording", Toast.LENGTH_LONG).show();
                }else{
                    if(pauseStatus == 1){
                        resumeRecording();
                        pauseStatus = 0;
                        ivPauseResume.setImageResource(R.drawable.ic_home_pause);
                        startChoronometer();
                        Toast.makeText(getApplicationContext(), "Resume Recording", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }


    private String formatTime(long miliseconds) {
        String finaltimeSting = "";
        String timeSecond;

        int hourse = (int) (miliseconds / (1000 * 60 * 60));
        int minutes = (int) (miliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) (miliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000;

        if (hourse > 0) {
            finaltimeSting = hourse + ":";
        }
        if (seconds < 10) {
            timeSecond = "0" + seconds;

        } else {
            timeSecond = "" + seconds;
        }
        finaltimeSting = finaltimeSting + minutes + ":" + timeSecond;
        return finaltimeSting;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bottom_library:
                startActivity(new Intent(this, LibraryActivity.class));
                break;
            case R.id.iv_bottom_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }

    }

    private  void startChoronometer(){
        if(!isRunning){
            chronometerTimer.setBase(SystemClock.elapsedRealtime() - pauseOffsetChorno);
            chronometerTimer.start();
            isRunning= true;
        }
    }
    private  void pauseChoronometer(){
        if(isRunning){
            chronometerTimer.stop();
            pauseOffsetChorno = SystemClock.elapsedRealtime() - chronometerTimer.getBase();
            isRunning = false;
        }
    }
    private  void resetChoronometer(){
        chronometerTimer.setBase(SystemClock.elapsedRealtime());
        pauseOffsetChorno = 0;
    }

}