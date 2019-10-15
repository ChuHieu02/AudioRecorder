package com.audiorecorder.voicerecorderhd.editor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.audiorecorder.voicerecorderhd.editor.activity.LibraryActivity;
import com.audiorecorder.voicerecorderhd.editor.activity.SettingsActivity;
import com.audiorecorder.voicerecorderhd.editor.utils.Constants;

import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivBottomLibrary;
    private ImageView ivBottomRecoder;
    private ImageView ivBottomSettings;
    private MediaRecorder mAudioRecorder;
    private ImageView ivRecord, ivPauseResume;
    private TextView tvRecordingStatus;
    private String outputFile;
    private static int recordingStatus = 2;
    private static int pauseStatus = 0;
    private long pauseOffsetChorno;
    private boolean isRunning;
    private Chronometer chronometerTimer;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mappingBottomNavigation();
        if (checkPermissionsResult()) {
            createFile();
            onRecordAudio();
            recordingStatus = 0;
        } else {
            requestPermissions();

        }
    }

    private void mappingBottomNavigation() {

        ivBottomLibrary = findViewById(R.id.iv_bottom_library);
        ivBottomRecoder = findViewById(R.id.iv_bottom_recoder);
        ivBottomSettings = findViewById(R.id.iv_bottom_settings);
        chronometerTimer = findViewById(R.id.chronoTime);
        ivPauseResume = findViewById(R.id.ivPauseResume);
        ivRecord = findViewById(R.id.iv_recoder);
        tvRecordingStatus = findViewById(R.id.textView2);
        ivPauseResume.setVisibility(View.INVISIBLE);
        ivPauseResume.setEnabled(false);
        ivBottomSettings.setOnClickListener(this);
        ivBottomLibrary.setOnClickListener(this);

    }

    private void createFile() {
//        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Recorder");
        SharedPreferences sharedPreferences = this.getSharedPreferences("audioSetting", Context.MODE_PRIVATE);
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

    private void setupMediaRecorder() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("audioSetting", Context.MODE_PRIVATE);
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

    private void startRecording() {
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
    private void pauseRecording() {
        if (mAudioRecorder != null) {
            mAudioRecorder.pause();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void resumeRecording() {
        if (mAudioRecorder != null) {
            mAudioRecorder.resume();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void stopRecording() {
        try {
            if (mAudioRecorder != null) {
                mAudioRecorder.stop();
                mAudioRecorder.release();
                mAudioRecorder = null;
                //Toast.makeText(getApplicationContext(), "Audio Recorder successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Null Media File", Toast.LENGTH_SHORT).show();
        }
    }


    private void onRecordAudio() {
        ivRecord.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if (recordingStatus == 0 && checkPermissionsResult()) {
                    startRecording();
                    ivRecord.setImageResource(R.drawable.ic_play_record_pr);
                    pauseStatus = 0;
                    ivPauseResume.setImageResource(R.drawable.ic_home_pause);
                    ivPauseResume.setEnabled(true);
                    resetChoronometer();
                    startChoronometer();
                    ivPauseResume.setVisibility(View.VISIBLE);
                    tvRecordingStatus.setText("Recording...");
                    recordingStatus = 1;
                } else if (recordingStatus == 1) {
                    stopRecording();
                    ivRecord.setImageResource(R.drawable.ic_home_record);
                    recordingStatus = 0;
                    pauseStatus = 0;
                    ivPauseResume.setImageResource(R.drawable.ic_home_pause);
                    ivPauseResume.setEnabled(false);
                    ivPauseResume.setVisibility(View.INVISIBLE);
                    tvRecordingStatus.setText("Stop recording");
                    pauseChoronometer();
                    creatCompleteDiaglog();
                } else if (recordingStatus == 2 && !checkPermissionsResult()) {
                    tvRecordingStatus.setText("Please go to setting and permisson");
                    creatSettingActivityDialog();
                }

            }
        });
        ivPauseResume.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if (pauseStatus == 0) {
                    pauseRecording();
                    pauseStatus = 1;
                    ivPauseResume.setImageResource(R.drawable.ic_home_play);
                    pauseChoronometer();
                    tvRecordingStatus.setText("Pause Recording");
                    //   Toast.makeText(getApplicationContext(), "Pause Recording", Toast.LENGTH_SHORT).show();
                } else {
                    if (pauseStatus == 1) {
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

    private void startChoronometer() {
        if (!isRunning) {
            chronometerTimer.setBase(SystemClock.elapsedRealtime() - pauseOffsetChorno);
            chronometerTimer.start();
            isRunning = true;
        }
    }

    private void pauseChoronometer() {
        if (isRunning) {
            chronometerTimer.stop();
            pauseOffsetChorno = SystemClock.elapsedRealtime() - chronometerTimer.getBase();
            isRunning = false;
        }
    }

    private void resetChoronometer() {
        chronometerTimer.setBase(SystemClock.elapsedRealtime());
        pauseOffsetChorno = 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecording();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (checkPermissionsResult()) {
            tvRecordingStatus.setText("Tab to recording");
            recordingStatus = 0;
        } else {
            requestPermissions();

        }
    }

    private void creatCompleteDiaglog() {
        final AlertDialog.Builder builderDiaglog = new AlertDialog.Builder(MainActivity.this);
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
    }


    private void creatSettingActivityDialog() {
        final AlertDialog.Builder builderDiaglog = new AlertDialog.Builder(MainActivity.this);
        builderDiaglog.setTitle("You need go to setting and perrmission for recording")
                .setMessage(outputFile)
                .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // intent.setData(Uri.parse("package:" + packageName));
                        intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                        startActivity(intent);
                        //recordingStatus = 0;

                    }
                })
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recordingStatus = 2;
                        dialog.dismiss();
                    }
                });
        builderDiaglog.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        recordingStatus = 0;
                        onRecordAudio();
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied :" + recordingStatus, Toast.LENGTH_LONG).show();
                        tvRecordingStatus.setText("You need go to setting and perrmisson to record");
                        recordingStatus = 2;
                        onRecordAudio();
                    }
                }
                break;
        }
    }

    public boolean checkPermissionsResult() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
        //    recordingStatus =1;
    }

}