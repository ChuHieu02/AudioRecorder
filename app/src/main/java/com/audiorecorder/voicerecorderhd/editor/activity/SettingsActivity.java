package com.audiorecorder.voicerecorderhd.editor.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.audiorecorder.voicerecorderhd.editor.MainActivity;
import com.audiorecorder.voicerecorderhd.editor.R;
import com.codekidlabs.storagechooser.StorageChooser;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;

import lib.folderpicker.FolderPicker;

public class SettingsActivity extends AppCompatActivity {
    private static final String STATIC_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() ;
    private Toolbar toolbar;
    private RadioGroup radioGroupFormatType,radioGroupSetQuality;
    private RadioButton radioButtonMp3, radioButtonWav;
    private RadioButton radioButton16kHz,radioButton22kHz,
            radioButton32kHz,radioButton44kHz;
    private Button buttonChooseFolder;
    private TextView textView;
    public static final String FORMAT_TYPE = "formatType";
    public static final String FORMAT_QUALITY = "formatQuality";
    public static final String AUDIO_SETTING = "audioSetting";
    public static final String DIRECTION_CHOOSER_PATH = "directionPath";
    private String pathDirection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mappingToolbar();
        settingAudio();
        loadAudioSetting();

    }
    private void mappingToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        radioGroupFormatType = (RadioGroup) findViewById(R.id.rgFormatType);
        radioGroupSetQuality = (RadioGroup) findViewById(R.id.rgSetQuality);
        radioButtonMp3 = (RadioButton) findViewById(R.id.rbMp3);
        radioButtonWav = (RadioButton) findViewById(R.id.rbWav);
        radioButton16kHz = (RadioButton) findViewById(R.id.rb16kHz);
        radioButton22kHz = (RadioButton) findViewById(R.id.rb22kHz);
        radioButton32kHz = (RadioButton) findViewById(R.id.rb32kHz);
        radioButton44kHz = (RadioButton) findViewById(R.id.rb44kHz);
        buttonChooseFolder = (Button) findViewById(R.id.btChooseFolder);
        textView = (TextView) findViewById(R.id.textView);
        buttonChooseFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences= getSharedPreferences(AUDIO_SETTING, Context.MODE_PRIVATE);
                StorageChooser chooser = new StorageChooser.Builder()
                        .withActivity(SettingsActivity.this)
                        .withPredefinedPath(STATIC_PATH)
                        .actionSave(true)
                        .withFragmentManager(getFragmentManager())
                        .allowCustomPath(true)
                        .allowAddFolder(true)
                        .withPreference(sharedPreferences)
                        .setType(StorageChooser.DIRECTORY_CHOOSER)
                        .withMemoryBar(true)
                        .build()
                        ;
                chooser.show();
                chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
                    @Override
                    public void onSelect(String path) {
                        Log.e("SELECTED_PATH", path);
                        SharedPreferences sharedPreferences= getSharedPreferences(AUDIO_SETTING, Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(DIRECTION_CHOOSER_PATH,path);
                        editor.apply();
                        Toast.makeText(getApplicationContext(), path, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void settingAudio(){
        SharedPreferences sharedPreferences= this.getSharedPreferences(AUDIO_SETTING, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        radioButtonWav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(FORMAT_TYPE,1);
                editor.apply();
            }
        });

        radioButtonMp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(FORMAT_TYPE,0);
                editor.apply();
            }
        });

        radioButton16kHz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(FORMAT_QUALITY,16);
                editor.apply();

            }

        });

        radioButton22kHz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(FORMAT_QUALITY,22);
                editor.apply();

            }
        });

        radioButton32kHz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(FORMAT_QUALITY,32);
                editor.apply();
            }
        });

        radioButton44kHz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(FORMAT_QUALITY,44);
                editor.apply();
            }
        });
    }

    private void loadAudioSetting()  {

        SharedPreferences sharedPreferences= this.getSharedPreferences(AUDIO_SETTING, Context.MODE_PRIVATE);
        if(sharedPreferences!= null) {
            int checkFormatType = sharedPreferences.getInt(FORMAT_TYPE,0);
            if(checkFormatType == 0){
                radioButtonMp3.setChecked(true);
                radioButtonWav.setChecked(false);
            }else if(checkFormatType == 1){
                radioButtonMp3.setChecked(false);
                radioButtonWav.setChecked(true);
            }

            int checkQuality = sharedPreferences.getInt(FORMAT_QUALITY,16);
            if(checkQuality == 16){
                radioButton16kHz.setChecked(true);

            } else if(checkQuality == 22){
                radioButton22kHz.setChecked(true);

            } else if(checkQuality == 32){
                radioButton32kHz.setChecked(true);

            } else if(checkQuality == 44){
                radioButton44kHz.setChecked(true);

            }
        }

    }
}