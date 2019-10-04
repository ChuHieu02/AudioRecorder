package com.audiorecorder.voicerecorderhd.editor.demoRecord;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.audiorecorder.voicerecorderhd.editor.R;

import java.io.IOException;
import java.util.UUID;

public class demoActivity extends AppCompatActivity {
    private Button playRecord, stopRecord, startRecord,pause,resume,stopPlay;
    private MediaRecorder myAudioRecorder;
    private String outputFile;
    private Chronometer chronometerTimer;
    private TextView tvTimer;
    private boolean isRunning;
    private long pauseOffsetChorno;
    private static String timeSize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        playRecord = (Button) findViewById(R.id.play);
        stopRecord = (Button) findViewById(R.id.stop);
        startRecord = (Button) findViewById(R.id.record);
        pause = (Button) findViewById(R.id.pasue);
        resume = (Button) findViewById(R.id.resume);
        stopPlay = (Button) findViewById(R.id.stopPlay);
        tvTimer = (TextView) findViewById(R.id.tvFileLenght);
        chronometerTimer = (Chronometer) findViewById(R.id.chronometerTime);
        stopRecord.setEnabled(false);
        playRecord.setEnabled(false);
        pause.setEnabled(false);
        resume.setEnabled(false);
        stopPlay.setEnabled(false);
        operateAudio();
    }

    private void operateAudio() {

        startRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ UUID.randomUUID() + "RecorderLAWER.mp3";
                setupMediaRecorder();

                try {
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                    pause.setEnabled(true);
                } catch (IllegalStateException ise) {
                    // make something ...
                } catch (IOException ioe) {
                    // make something
                }
                startRecord.setEnabled(false);
                stopRecord.setEnabled(true);
                playRecord.setEnabled(false);
                stopChoronometer();
                startChoronometer();
                Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
            }
        });

        stopRecord.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder = null;
                startRecord.setEnabled(true);
                stopRecord.setEnabled(false);
                playRecord.setEnabled(true);
                pause.setEnabled(false);
                resume.setEnabled(false);
                pauseChoronometer();
                Toast.makeText(getApplicationContext(), "Audio Recorder successfully", Toast.LENGTH_LONG).show();
            }
        });

        playRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                stopPlay.setEnabled(true);
                playRecord.setEnabled(false);
                startRecord.setEnabled(false);
                try {
                    mediaPlayer.setDataSource(outputFile);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    // make something
                }
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                myAudioRecorder.pause();
                pauseChoronometer();
                resume.setEnabled(true);
                pause.setEnabled(false);
            }
        });

        resume.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                myAudioRecorder.resume();
                startChoronometer();
                resume.setEnabled(false);
                pause.setEnabled(true);
            }
        });

        stopPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecord.setEnabled(false);
                resume.setEnabled(false);
                pause.setEnabled(false);
                playRecord.setEnabled(true);
                stopPlay.setEnabled(false);
                startRecord.setEnabled(true);

                if (myAudioRecorder != null){
                    myAudioRecorder.stop();
                    myAudioRecorder.release();
                    setupMediaRecorder();
                }
            }
        });
    }


    private  void  setupMediaRecorder(){
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
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
    private  void stopChoronometer(){
        chronometerTimer.setBase(SystemClock.elapsedRealtime());
        pauseOffsetChorno = 0;
        timeSize = null;
    }
}
