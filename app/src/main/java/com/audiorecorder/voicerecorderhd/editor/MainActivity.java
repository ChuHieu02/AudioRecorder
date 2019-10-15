package com.audiorecorder.voicerecorderhd.editor;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
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
import com.audiorecorder.voicerecorderhd.editor.service.RecordService;
import com.audiorecorder.voicerecorderhd.editor.utils.AudioRecordSetup;
import com.audiorecorder.voicerecorderhd.editor.utils.Constants;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivBottomLibrary;
    private ImageView ivBottomRecoder;
    private ImageView ivBottomSettings;
    private ImageView ivRecord , ivPauseResume;
    private TextView tvRecordingStatus;
    private String outputFile;
    private static int recordingStatus = 2;
    private static int pauseStatus = 0;
    private long pauseOffsetChorno;
    private boolean isRunning;
    private Chronometer chronometerTimer;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private ServiceConnection serviceConnection;
    private RecordService recordService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mappingBottomNavigation();
        recordService = new RecordService();

        if(checkPermissionsResult()) {
            if(!isMyServiceRunning(recordService.getClass())){
                ivRecord.setImageResource(R.drawable.ic_home_record);
                recordingStatus = 0;
                onRecordAudio();
            }
            else {
                ivRecord.setImageResource(R.drawable.ic_play_record_pr);
                tvRecordingStatus.setText("Recording...");
                recordingStatus = 1;
                onRecordAudio();
            }

        }else {
            requestPermissions();

        }
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

    private  void onRecordAudio(){
        ivRecord.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                if(recordingStatus == 0 && checkPermissionsResult()){
                    ivRecord.setImageResource(R.drawable.ic_play_record_pr);
                    pauseStatus = 0;
                    ivPauseResume.setImageResource(R.drawable.ic_home_pause);
                    ivPauseResume.setEnabled(true);
                    resetChoronometer();
                    startChoronometer();
                    ivPauseResume.setVisibility(View.VISIBLE);
                    tvRecordingStatus.setText("Recording...");
                    Intent intentService = new Intent(MainActivity.this, RecordService.class);
                    intentService.putExtra("inputExtra", "Recording...");
                    ContextCompat.startForegroundService(MainActivity.this, intentService);
                    recordingStatus =1;

                }else if(recordingStatus == 1){
                    ivRecord.setImageResource(R.drawable.ic_home_record);
                    recordingStatus =0;
                    pauseStatus = 0;
                    ivPauseResume.setImageResource(R.drawable.ic_home_pause);
                    ivPauseResume.setEnabled(false);
                    ivPauseResume.setVisibility(View.INVISIBLE);
                    tvRecordingStatus.setText("Stop recording");
                    creatCompleteDiaglog();
                    pauseChoronometer();
                    Intent intentService = new Intent(MainActivity.this, RecordService.class);
                    stopService(intentService);

                }
                else if(recordingStatus == 2 && !checkPermissionsResult() ){
                    tvRecordingStatus.setText("Please go to setting and permisson");
                    creatSettingActivityDialog();
                }

            }
        });

        ivPauseResume.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if(pauseStatus == 0) {

                    pauseStatus = 1;
                    ivPauseResume.setImageResource(R.drawable.ic_home_play);
                    pauseChoronometer();
                    tvRecordingStatus.setText("Pause Recording");

                }else{
                    if(pauseStatus == 1){

                        pauseStatus = 0;
                        ivPauseResume.setImageResource(R.drawable.ic_home_pause);
                        startChoronometer();
                        tvRecordingStatus.setText("Recording...");

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


    @Override
    protected void onRestart() {
        super.onRestart();
        if(checkPermissionsResult()) {
            tvRecordingStatus.setText("Tab to recording");
            recordingStatus = 0;
        }else {
            requestPermissions();

        }
    }

    private void creatCompleteDiaglog(){
        SharedPreferences sharedPreferences= this.getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
        if(sharedPreferences!= null){
            outputFile = sharedPreferences.getString(Constants.K_DIRECTION_CHOOSER_PATH,Constants.K_DEFALT_PATH);
        }
        final AlertDialog.Builder builderDiaglog=  new AlertDialog.Builder(MainActivity.this);
        builderDiaglog.setTitle("File save at :")
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

    private void creatSettingActivityDialog(){
        final AlertDialog.Builder builderDiaglog=  new AlertDialog.Builder(MainActivity.this);
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
                if (grantResults.length> 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] ==  PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        recordingStatus = 0;
                        onRecordAudio();
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Permission Denied :"+recordingStatus,Toast.LENGTH_LONG).show();
                        tvRecordingStatus.setText("You need go to setting and perrmisson to record");
                        recordingStatus = 2;
                        onRecordAudio();
                    }
                }
                break;
        }
    }

    public boolean checkPermissionsResult() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
