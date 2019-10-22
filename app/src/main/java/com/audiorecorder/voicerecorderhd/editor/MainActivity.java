package com.audiorecorder.voicerecorderhd.editor;

import android.app.ActivityManager;
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
import android.provider.Settings;
import android.util.Log;
import android.view.View;
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


import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
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
    private TimeCountReceiver timeCountReceiver = new TimeCountReceiver();
    private NotificationReceiver notificationReceiver = new NotificationReceiver();
    private int seconds;
    private int minutes;
    private int hours;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initReceiver();

//        dbQuerys = new DBQuerys(this);
//        dbQuerys.insertAudioString("lac troi","fgholkkl.mp3",124566,564132,5465365);

        mappingBottomNavigation();
        recordService = new RecordService();
        updateViewStage();

        sendBroadcast(new Intent(Constants.ACTION_CHECK_TIME));

    }

    private void mappingBottomNavigation() {

        ivBottomLibrary = findViewById(R.id.iv_bottom_library);
        ivBottomRecoder = findViewById(R.id.iv_bottom_recoder);
        ivBottomSettings = findViewById(R.id.iv_bottom_settings);
        ivPauseResume = findViewById(R.id.iv_pauseResume);
        ivRecord = findViewById(R.id.iv_recoder);
        tvRecordingStatus = findViewById(R.id.textView2);
        tvTimeRecord =(TextView) findViewById(R.id.tv_time_record);
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
//                    if(isMyServiceRunning(recordService.getClass())){
//                        onReadyStart();
//                    }
                    // onReadyStart();
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

    @Override
    protected void onResume() {
        super.onResume();
        updateViewStage();

    }

    private void onStartRecording(){

        Intent intentService = new Intent(MainActivity.this, RecordService.class);
        ContextCompat.startForegroundService(MainActivity.this, intentService);

        recordService.setPauseStatus(0);
        recordService.setRecordingStatus(1);

    }

    private void  onReadyStart(){
        Intent intentService = new Intent(Constants.START_ACTION);
        sendBroadcast(intentService);
    }

    private void onStopRecording(){
        Intent intentStop = new Intent(Constants.STOP_ACTION);
        sendBroadcast(intentStop);
        Intent intentService = new Intent(MainActivity.this, RecordService.class);
        stopService(intentService);

    }

    private void onActionResume(){
        Intent intentService = new Intent(Constants.RESUME_ACTION);
        sendBroadcast(intentService);
        recordService.setPauseStatus(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(notificationReceiver);
            unregisterReceiver(timeCountReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  void onActionPasuse(){
        Intent intentService = new Intent(Constants.PAUSE_ACTION);
        recordService.setPauseStatus(1);
        sendBroadcast(intentService);


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
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.RESUME_ACTION);
            filter.addAction(Constants.PAUSE_ACTION);
            filter.addAction(Constants.STOP_ACTION);
            filter.addAction(Constants.START_ACTION);
            filter.addAction(Constants.SEND_TIME);
            filter.addAction(Constants.ACTION_UPDATE_TIME);
            registerReceiver(notificationReceiver, filter);
            registerReceiver(timeCountReceiver,filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public  class NotificationReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //  Log.e("Test", action: 1212" );
            if (Constants.PAUSE_ACTION.equals(action) ) {

                updateIconResume();
                try {
                    unregisterReceiver(timeCountReceiver);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (Constants.STOP_ACTION.equals(action) ) {

                updateIconRecord();
//                unregisterReceiver(notificationReceiver);
//                if(pauseStatus == 1){
//                    unregisterReceiver(timeCountReceiver);
//                }


            } else if(Constants.RESUME_ACTION.equals(action) ){
                try {
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(Constants.SEND_TIME);
                    registerReceiver(timeCountReceiver, filter);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if(Constants.START_ACTION.equals(action)){

//                IntentFilter filter = new IntentFilter();
//                filter.addAction(Constants.SEND_TIME);
//                registerReceiver(timeCountReceiver, filter);
                updateIconPause();
                updateIconStopRecord();

            } else if(Constants.ACTION_UPDATE_TIME.equals(action)){
                long currentTime = intent.getLongExtra(Constants.EXTRA_CURRENT_TIME, 0L);
                Log.i("datfit", "onReceive: " + currentTime);
            }
        }
    }


    public class TimeCountReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            long time_count = intent.getLongExtra(Constants.TIME_COUNT, 0);
            seconds = (int) (time_count / 1000) % 60 ;
            minutes = (int) ((time_count/ (1000*60)) % 60);
            hours   = (int) ((time_count / (1000*60*60)) % 24);
            tvTimeRecord.setText((hours>0 ? String.format("%d:", hours) : "")
                    + ((minutes<10 && hours > 0)? "0"
                    + String.format("%d:", minutes) :  String.format("%d:", minutes))
                    + (seconds<10 ? "0" + seconds: seconds));
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
//        if(pauseStatus == 1){
        try {
            unregisterReceiver(timeCountReceiver);
            unregisterReceiver(notificationReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }
//        if(recordingStatus == 1) unregisterReceiver(notificationReceiver);
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
        SharedPreferences sharedPreferences= getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
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

                int checkRecordingStatus = recordService.getRecordingStatus();
                if(checkRecordingStatus == 0){

                    updateIconRecord();


                }else if(checkRecordingStatus == 1){

                    updateIconStopRecord();
                }

                int checkPauseStatus =  recordService.getPauseStatus();
                if(checkPauseStatus == 0){
                    updateIconPause();

                }else if(checkPauseStatus == 1){

                    updateIconResume();
                }
                onRecordAudio();
            }
        }else {
            requestPermissions();

        }
    }

}