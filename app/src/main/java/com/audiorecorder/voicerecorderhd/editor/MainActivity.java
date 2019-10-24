package com.audiorecorder.voicerecorderhd.editor;

import android.app.ActivityManager;
import android.app.Dialog;
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
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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
import com.audiorecorder.voicerecorderhd.editor.data.DBQuerys;
import com.audiorecorder.voicerecorderhd.editor.service.RecordService;
import com.audiorecorder.voicerecorderhd.editor.utils.Constants;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView ivBottomLibrary;
    private ImageView ivBottomRecoder;
    private ImageView ivBottomSettings;
    private ImageView ivRecord , ivPauseResume;
    private TextView tvRecordingStatus, tvTimeRecord, lbRecoder;
    private String outputFile;
    private static int recordingStatus = 2;
    private static int pauseStatus = 0;
    private static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private ServiceConnection serviceConnection;
    private RecordService recordService;
    private TimeCountReceiver timeCountReceiver = new TimeCountReceiver();
    private NotificationReceiver notificationReceiver = new NotificationReceiver();
    private DBQuerys dbQuerys;
    private int seconds;
    private int minutes;
    private int hours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mappingBottomNavigation();
        recordService = new RecordService();
        updateViewStage();
    }

    private void mappingBottomNavigation() {
        lbRecoder = findViewById(R.id.lb_recoder);
        lbRecoder.setText(getResources().getString(R.string.label_recoder));
        ivBottomLibrary = findViewById(R.id.iv_bottom_library);
        ivBottomRecoder = findViewById(R.id.iv_bottom_recoder);
        ivBottomRecoder.setImageDrawable(getResources().getDrawable(R.drawable.ic_record_pr));
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
                    updateIconStopRecord();
                    hanlderSpamClickRecord();
                }else if(recordingStatus == 1){
                    onStopRecording();
                    updateIconRecord();
                    creatSetNameRecordFileDialog();
                    hanlderSpamClickRecord();
                }
                else if(recordingStatus == 2 && !checkPermissionsResult() ){
                    tvRecordingStatus.setText(R.string.tv_recording_status_on_denied_permission);
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
        initReceiver();
        updateViewStage();

    }

    private void onStartRecording(){
        Intent intentService = new Intent(MainActivity.this, RecordService.class);
        ContextCompat.startForegroundService(MainActivity.this, intentService);
        recordService.setPauseStatus(0);
        recordService.setRecordingStatus(1);
        Intent intentStart = new Intent(Constants.START_ACTION);
        sendBroadcast(intentStart);
    }

    private void onStopRecording(){
        Intent intentStop = new Intent(Constants.STOP_ACTION);
        sendBroadcast(intentStop);
        Intent intentService = new Intent(MainActivity.this, RecordService.class);
        stopService(intentService);
    }

    private void onActionResume(){
        Intent intentResume = new Intent(Constants.RESUME_ACTION);
        sendBroadcast(intentResume);
        recordService.setPauseStatus(0);
    }

//    private void onSaveCurrenTime(){
//        Intent intentSaveCurrenTime = new Intent(Constants.ACTION_SAVE_CURRENT_TIME);
//        sendBroadcast(intentSaveCurrenTime);
//    }

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
            IntentFilter timeReceiveFilter = new IntentFilter();
            filter.addAction(Constants.RESUME_ACTION);
            filter.addAction(Constants.PAUSE_ACTION);
            filter.addAction(Constants.STOP_ACTION);
            filter.addAction(Constants.START_ACTION);
            timeReceiveFilter.addAction(Constants.SEND_TIME);
            registerReceiver(notificationReceiver, filter);
            registerReceiver(timeCountReceiver,timeReceiveFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  class NotificationReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.PAUSE_ACTION.equals(action) ) {
                try {
                    unregisterReceiver(timeCountReceiver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                updateIconResume();
            } else if (Constants.STOP_ACTION.equals(action) ) {
                updateIconRecord();
            } else if(Constants.RESUME_ACTION.equals(action) ){
                try {
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(Constants.SEND_TIME);
                    registerReceiver(timeCountReceiver, filter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                updateIconPause();
            } else if(Constants.START_ACTION.equals(action)){
                updateIconPause();
                try {
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(Constants.SEND_TIME);
                    registerReceiver(timeCountReceiver, filter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                updateIconStopRecord();
            }
        }
    }

    public class TimeCountReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            long time_count = intent.getLongExtra(Constants.TIME_COUNT, 0);
            updateTimeRecord(time_count);
        }
    }

    private void updateTimeRecord(long time_count){
        seconds = (int) (time_count / 1000) % 60 ;
        minutes = (int) ((time_count/ (1000*60)) % 60);
        hours   = (int) ((time_count / (1000*60*60)) % 24);
        tvTimeRecord.setText((hours>0 ? String.format("%d:", hours) : "")
                + ((minutes<10 && hours > 0)? "0"
                + String.format("%d:", minutes) :  String.format("%d:", minutes))
                + (seconds<10 ? "0" + seconds: seconds));

    }
    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(timeCountReceiver);
            unregisterReceiver(notificationReceiver);
        } catch (Exception e) {
            e.printStackTrace();
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

    private void creatSetNameRecordFileDialog(){

       final Dialog setNameDialog = new Dialog(this);
       setNameDialog.setContentView(R.layout.dialog_named_record_file);
       final EditText  edSetNameRecordFile = (EditText) setNameDialog.findViewById(R.id.ed_set_name_record_file);
       edSetNameRecordFile.setText(recordService.getAudioName());

       Button btDefault = (Button) setNameDialog.findViewById(R.id.bt_default);
       Button btConfirm = (Button) setNameDialog.findViewById(R.id.bt_confirm);

       btDefault.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               setNameDialog.dismiss();
           }
       });

       btConfirm.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               String newName = edSetNameRecordFile.getText().toString();
               dbQuerys = new DBQuerys(getApplicationContext());
               boolean checkFile = dbQuerys.isExitsInDB(newName);

               if(newName == null || checkFile == true ){
                   Log.e("CheckDb", "onReadyStart: " + checkFile +"  "+ newName);
                   Toast.makeText(getApplicationContext(), R.string.set_name_dialog, Toast.LENGTH_SHORT).show();

               } else if(newName != null && checkFile == false){

                   Log.e("CheckDb", "onReadyStart: " + checkFile+"  "+ newName);
                   dbQuerys.updateNameRecordFile(newName,recordService.getAudioName());
                   setNameDialog.dismiss();
               }
           }
       });

       edSetNameRecordFile.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           @Override
           public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
               if(actionId == EditorInfo.IME_ACTION_DONE){

                   String newName = edSetNameRecordFile.getText().toString();
                   dbQuerys = new DBQuerys(getApplicationContext());
                   boolean checkFile = dbQuerys.isExitsInDB(newName);

                   if(newName == null || checkFile == true ){
                       Log.e("CheckDb", "onReadyStart: " + checkFile +"  "+ newName);
                       Toast.makeText(getApplicationContext(), R.string.set_name_dialog, Toast.LENGTH_SHORT).show();

                   } else if(newName != null && checkFile == false){

                       Log.e("CheckDb", "onReadyStart: " + checkFile+"  "+ newName);
                       dbQuerys.updateNameRecordFile(newName,recordService.getAudioName());
                       setNameDialog.dismiss();
                   }
                   return  true;
               }
               return false;
           }
       });
       edSetNameRecordFile.requestFocus();
       setNameDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
       setNameDialog.show();

    }



    private void creatSettingActivityDialog(){
        final AlertDialog.Builder builderDiaglog=  new AlertDialog.Builder(MainActivity.this);
        builderDiaglog.setTitle(R.string.setting_activity_dialog)
                .setMessage(outputFile)
                .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                        startActivity(intent);
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
        ActivityCompat.requestPermissions(MainActivity.this
                , new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
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

    private void hanlderSpamClickRecord(){

        ivRecord.setClickable(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ivRecord.setClickable(true);
            }
        } , 1000);

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
                    updateTimeRecord(recordService.getExtraCurrentTime());
                    updateIconResume();
                }
                onRecordAudio();
            }
        }else {
            requestPermissions();
        }
    }
}
