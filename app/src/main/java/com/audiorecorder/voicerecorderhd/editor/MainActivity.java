package com.audiorecorder.voicerecorderhd.editor;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.audiorecorder.voicerecorderhd.editor.utils.Constants;

import java.util.Locale;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivBottomLibrary;
    private ImageView ivBottomRecoder;
    private ImageView ivBottomSettings;
    private ImageView ivRecord , ivPauseResume;
    private TextView tvRecordingStatus, tvTimeRecord;
    private String outputFile;
    private static int recordingStatus = 2;
    private static int pauseStatus = 0;
    private static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private ServiceConnection serviceConnection;
    private RecordService recordService;
    private Chronometer cnTimeRecord;
    private long pauseOffsetChorno;
    private static boolean isRunning ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mappingBottomNavigation();
        recordService = new RecordService();
        updateViewStage();

    }

    private void mappingBottomNavigation() {

        ivBottomLibrary = findViewById(R.id.iv_bottom_library);
        ivBottomRecoder = findViewById(R.id.iv_bottom_recoder);
        ivBottomSettings = findViewById(R.id.iv_bottom_settings);
        ivPauseResume = findViewById(R.id.imageViewPauseResume);
        ivRecord = findViewById(R.id.imageViewRecord);
        tvRecordingStatus = findViewById(R.id.textView2);
        cnTimeRecord =(Chronometer) findViewById(R.id.cn_time_record);
        ivPauseResume.setVisibility(View.INVISIBLE);
        ivBottomSettings.setOnClickListener(this);
        ivBottomLibrary.setOnClickListener(this);

    }

    private  void onRecordAudio(){
        ivRecord.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if(recordingStatus == 0 && checkPermissionsResult()){
                    onStartRecording();
                    updateIconStopRecord();
                }else if(recordingStatus == 1){

                    onStopRecording();
                    updateIconRecord();
                    creatCompleteDiaglog();
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

                    onActionPasuse();
                    updateIconResume();
                }else{
                    if(pauseStatus == 1){

                        onActionResume();
                        updateIconPause();
                    }
                }
            }
        });
    }

    public void startChoronometer() {
        if (!isRunning) {
            cnTimeRecord.setBase(SystemClock.elapsedRealtime() - recordService.getPauseOffsetChorno());
            cnTimeRecord.start();
            isRunning = true;
        }
    }

    public void  continueChronometer(){
        if (!isRunning) {
            cnTimeRecord.setBase(recordService.getPauseOffsetChorno());
            cnTimeRecord.start();
            isRunning = true;
        }
    }

    public void pauseChoronometer() {
        if (isRunning) {
            cnTimeRecord.stop();
            recordService.setPauseOffsetChorno(SystemClock.elapsedRealtime() - cnTimeRecord.getBase());
            isRunning = false;
        }
    }

    public void resetChoronometer() {
        cnTimeRecord.setBase(SystemClock.elapsedRealtime());
        recordService.setPauseOffsetChorno(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initReceiver();
        updateViewStage();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recordService.setPauseOffsetChorno(SystemClock.elapsedRealtime() - cnTimeRecord.getBase());
    }

    private void onStartRecording(){

        Intent intentService = new Intent(MainActivity.this, RecordService.class);
        ContextCompat.startForegroundService(MainActivity.this, intentService);

        Intent startReceive = new Intent(Constants.START_ACTION);
        PendingIntent pendingIntentStart = PendingIntent.getBroadcast(MainActivity.this
                , 12345
                , startReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        recordService.setPauseStatus(0);
        recordService.setRecordingStatus(1);

        startChoronometer();
    }

    private void onStopRecording(){
        Intent intentService = new Intent(MainActivity.this, RecordService.class);
        stopService(intentService);

        resetChoronometer();
        pauseChoronometer();
    }

    private void onActionResume(){
        Intent pauseReceive = new Intent(Constants.PAUSE_ACTION);
        PendingIntent pendingIntentPause = PendingIntent.getBroadcast(MainActivity.this
                , 12345
                , pauseReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        recordService.setPauseStatus(0);

        startChoronometer();
    }

    private  void onActionPasuse(){
        Intent resumeReceive = new Intent(Constants.RESUME_ACTION);
        PendingIntent pendingIntentResume = PendingIntent.getBroadcast(MainActivity.this
                , 12345
                , resumeReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        recordService.setPauseStatus(1);

        pauseChoronometer();
    }

    private void updateIconPause(){

        pauseStatus = 0;
        ivPauseResume.setVisibility(View.VISIBLE);
        ivPauseResume.setImageResource(R.drawable.ic_home_pause);
        tvRecordingStatus.setText("Recording...");

    }

    private void updateIconResume(){

        pauseStatus = 1;
        ivPauseResume.setVisibility(View.VISIBLE);
        ivPauseResume.setImageResource(R.drawable.ic_home_play);
        tvRecordingStatus.setText("Pause Recording");

    }

    private void updateIconRecord(){

        ivPauseResume.setEnabled(false);
        recordingStatus = 0;
        pauseStatus = 0;
        ivPauseResume.setVisibility(View.INVISIBLE);
        ivRecord.setImageResource(R.drawable.ic_home_record);
        tvRecordingStatus.setText("Tab to recording");

    }

    private void updateIconStopRecord(){

        recordingStatus =1;
        ivRecord.setImageResource(R.drawable.ic_play_record_pr);
        pauseStatus = 0;
        ivPauseResume.setImageResource(R.drawable.ic_home_pause);
        ivPauseResume.setEnabled(true);
        ivPauseResume.setVisibility(View.VISIBLE);
        tvRecordingStatus.setText("Recording...");

    }

    public void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.RESUME_ACTION);
        filter.addAction(Constants.PAUSE_ACTION);
        filter.addAction(Constants.STOP_ACTION);
        registerReceiver(new NotificationReceiver(), filter);
    }

    public  class NotificationReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Toast.makeText(context, action, Toast.LENGTH_SHORT).show();
            if (Constants.PAUSE_ACTION.equals(action)) {
                pauseChoronometer();
                updateIconResume();

            } else if (Constants.STOP_ACTION.equals(action)) {

                resetChoronometer();
                pauseChoronometer();
                updateIconRecord();

            } else if(Constants.RESUME_ACTION.equals(action)){
                startChoronometer();
                updateIconPause();

            } else if(Constants.START_ACTION.equals(action)){

                startChoronometer();
                updateIconStopRecord();

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateViewStage();
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

    @Override
    protected void onRestart() {
        super.onRestart();
        updateViewStage();
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

    private void updateViewStage(){
        if(checkPermissionsResult()) {
            if(!isMyServiceRunning(recordService.getClass())){
                ivRecord.setImageResource(R.drawable.ic_home_record);
                recordingStatus = 0;
                onRecordAudio();
            }
            else {
                ivRecord.setImageResource(R.drawable.ic_play_record_pr);
             //   tvRecordingStatus.setText("Recording...");
              //  recordingStatus = 1;

                int checkRecordingStatus = recordService.getRecordingStatus();
                if(checkRecordingStatus == 0){

                    updateIconRecord();
                    //startChoronometer();
                    continueChronometer();

                }else if(checkRecordingStatus == 1){

                    updateIconStopRecord();
                    pauseChoronometer();
                }

                int checkPauseStatus =  recordService.getPauseStatus();
                if(checkPauseStatus == 0){
                    startChoronometer();
                    updateIconPause();

                }else if(checkPauseStatus == 1){
                   pauseChoronometer();
                   updateIconResume();
                }
                onRecordAudio();
            }
        }else {
            requestPermissions();

        }
    }

}
