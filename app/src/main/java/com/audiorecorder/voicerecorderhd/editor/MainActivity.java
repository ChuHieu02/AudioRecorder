package com.audiorecorder.voicerecorderhd.editor;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.audiorecorder.voicerecorderhd.editor.activity.LibraryActivity;
import com.audiorecorder.voicerecorderhd.editor.activity.SettingsActivity;
import com.audiorecorder.voicerecorderhd.editor.utils.SimplePermissonListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivBottomLibrary;
    private ImageView ivBottomRecoder;
    private ImageView ivBottomSettings;
    private MediaRecorder mAudioRecorder;
    private ImageView ivRecord , ivPauseResume;
    private TextView tvRecordingStatus;
    private String outputFile;
    private static int recordingStatus = 0;
    private static int pauseStatus = 0;
    private long pauseOffsetChorno;
    private boolean isRunning;
    private Chronometer chronometerTimer;
    public static final String FORMAT_TYPE = "formatType";
    public static final String FORMAT_QUALITY = "formatQuality";
    public static final int SAMPLE_RATE_QUALITY = 1000;
    public static final String DIRECTION_CHOOSER_PATH = "directionPath";
    private SimplePermissonListener simplePermissonListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        simplePermissonListener = new SimplePermissonListener(this);
        requestAllPermission();
        mappingBottomNavigation();
        createFile();
        onRecordAudio();

    }

    private void mappingBottomNavigation() {

        ivBottomLibrary = (ImageView) findViewById(R.id.iv_bottom_library);
        ivBottomRecoder = (ImageView) findViewById(R.id.iv_bottom_recoder);
        ivBottomSettings = (ImageView) findViewById(R.id.iv_bottom_settings);
        chronometerTimer = (Chronometer) findViewById(R.id.chronoTime);
        ivPauseResume =(ImageView) findViewById(R.id.imageViewPauseResume);
        ivRecord =(ImageView) findViewById(R.id.imageViewRecord);
        tvRecordingStatus = (TextView) findViewById(R.id.textView2);
        ivPauseResume.setVisibility(View.INVISIBLE);
        ivPauseResume.setEnabled(false);
        ivBottomSettings.setOnClickListener(this);
        ivBottomLibrary.setOnClickListener(this);

    }

    private void createFile() {
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
                mAudioRecorder.setAudioSamplingRate(16 * SAMPLE_RATE_QUALITY);

            }else if(checkQuality == 22){
                mAudioRecorder.setAudioEncodingBitRate(22);
                mAudioRecorder.setAudioSamplingRate(22 * SAMPLE_RATE_QUALITY);

            }else if(checkQuality == 32){
                mAudioRecorder.setAudioEncodingBitRate(32);
                mAudioRecorder.setAudioSamplingRate(32 * SAMPLE_RATE_QUALITY);

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
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void pauseRecording(){
        if (mAudioRecorder!=null){
            mAudioRecorder.pause();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void resumeRecording(){
            if (mAudioRecorder != null) {
                mAudioRecorder.resume();
            }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void stopRecording(){
        try {
            if (mAudioRecorder!=null){
                mAudioRecorder.stop();
                mAudioRecorder.release();
                mAudioRecorder = null;
                //Toast.makeText(getApplicationContext(), "Audio Recorder successfully", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Null Media File", Toast.LENGTH_SHORT).show();
        }
    }


    private  void onRecordAudio(){
        ivRecord.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if(recordingStatus == 0){
                    startRecording();
                    ivRecord.setImageResource(R.drawable.ic_play_record_pr);
                    recordingStatus =1;
                    pauseStatus = 0;
                    ivPauseResume.setImageResource(R.drawable.ic_home_pause);
                    ivPauseResume.setEnabled(true);
                    resetChoronometer();
                    startChoronometer();
                    ivPauseResume.setVisibility(View.VISIBLE);
                    tvRecordingStatus.setText("Recording...");
                }else if(recordingStatus == 1){
                        stopRecording();
                        ivRecord.setImageResource(R.drawable.ic_home_record);
                        recordingStatus =0;
                        pauseStatus = 0;
                        ivPauseResume.setImageResource(R.drawable.ic_home_pause);
                        ivPauseResume.setEnabled(false);
                        ivPauseResume.setVisibility(View.INVISIBLE);
                        tvRecordingStatus.setText("Stop recording");
                        pauseChoronometer();

                        final AlertDialog.Builder builderDiaglog=  new AlertDialog.Builder(MainActivity.this);
                        builderDiaglog.setTitle("File save at ")
                                      .setMessage(outputFile)
                                      .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                                          @Override
                                          public void onClick(DialogInterface dialog, int which) {
                                              Intent openLibrary = new Intent(getApplicationContext(), LibraryActivity.class);
                                              startActivity(openLibrary);
                                          }
                                      })
                                      .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                          @Override
                                          public void onClick(DialogInterface dialog, int which) {
                                              dialog.dismiss();
                                          }
                                      });
                        builderDiaglog.create().show();



                }else if(recordingStatus == 2){
                      requestAllPermission();
                      recordingStatus = 0;
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
                    tvRecordingStatus.setText("Pause Recording");
                 //   Toast.makeText(getApplicationContext(), "Pause Recording", Toast.LENGTH_SHORT).show();
                }else{
                    if(pauseStatus == 1){
                        resumeRecording();
                        pauseStatus = 0;
                        ivPauseResume.setImageResource(R.drawable.ic_home_pause);
                        startChoronometer();
                        tvRecordingStatus.setText("Recording...");
                      //  Toast.makeText(getApplicationContext(), "Resume Recording", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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

    public void showPermissionRationalbe(final PermissionToken token){
        new AlertDialog.Builder(this).setTitle("We need some Permission")
                .setMessage("Please allow some permissions to record audio !")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            token.continuePermissionRequest();
                            recordingStatus = 0;
                            dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        token.cancelPermissionRequest();
                        recordingStatus = 2;
                        dialog.dismiss();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        recordingStatus = 2;
                        token.cancelPermissionRequest();
                    }
                }).show();
    }


    public void requestAllPermission(){
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE
        ,Manifest.permission.RECORD_AUDIO
        ,Manifest.permission.WRITE_SETTINGS)
                .withListener(simplePermissonListener).check();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecording();
    }

}