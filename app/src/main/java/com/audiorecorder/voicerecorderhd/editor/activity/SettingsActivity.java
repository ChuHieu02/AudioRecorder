package com.audiorecorder.voicerecorderhd.editor.activity;

import android.content.Context;
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

import com.audiorecorder.voicerecorderhd.editor.R;
import com.audiorecorder.voicerecorderhd.editor.utils.Constants;
import com.codekidlabs.storagechooser.StorageChooser;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {
    private static final String STATIC_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private Toolbar toolbar;
    private RadioGroup radioGroupFormatType, radioGroupSetQuality;
    private RadioButton radioButtonMp3, radioButtonWav;
    private RadioButton radioButton16kHz, radioButton22kHz, radioButton32kHz, radioButton44kHz;
    private Button buttonChooseFolder;
    private TextView textView;
    private TextView tv_path_setting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mappingToolbar();
        settingAudio();
        loadAudioSetting();
        SharedPreferences sharedPreferences = this.getSharedPreferences(Constants.AUDIO_SETTING, Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            String path = sharedPreferences.getString(Constants.DIRECTION_CHOOSER_PATH, Constants.DEFALT_PATH);
            tv_path_setting.setText(path);
        } else {
            tv_path_setting.setText(Constants.DEFALT_PATH);

        }

    }

    private void mappingToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Library");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tv_path_setting = findViewById(R.id.tv_path_setting);

        radioGroupFormatType = findViewById(R.id.rgFormatType);
        radioGroupSetQuality = findViewById(R.id.rgSetQuality);
        radioButtonMp3 = findViewById(R.id.rbMp3);
        radioButtonWav = findViewById(R.id.rbWav);
        radioButton16kHz = findViewById(R.id.rb16kHz);
        radioButton22kHz = findViewById(R.id.rb22kHz);
        radioButton32kHz = findViewById(R.id.rb32kHz);
        radioButton44kHz = findViewById(R.id.rb44kHz);
        buttonChooseFolder = findViewById(R.id.btChooseFolder);
        textView = findViewById(R.id.textView);
        buttonChooseFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.AUDIO_SETTING, Context.MODE_PRIVATE);
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
                        .skipOverview(true)
                        .build();
                chooser.show();
                chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
                    @Override
                    public void onSelect(String path) {
                        Log.e("SELECTED_PATH", path);
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.AUDIO_SETTING, Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constants.DIRECTION_CHOOSER_PATH, path);
                        editor.apply();
                        tv_path_setting.setText(path);

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

    private void settingAudio() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(Constants.AUDIO_SETTING, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        radioButtonWav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(Constants.FORMAT_TYPE, 1);
                editor.apply();
            }
        });

        radioButtonMp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(Constants.FORMAT_TYPE, 0);
                editor.apply();
            }
        });

        radioButton16kHz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(Constants.FORMAT_QUALITY, 16);
                editor.apply();

            }

        });

        radioButton22kHz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(Constants.FORMAT_QUALITY, 22);
                editor.apply();

            }
        });

        radioButton32kHz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(Constants.FORMAT_QUALITY, 32);
                editor.apply();
            }
        });

        radioButton44kHz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(Constants.FORMAT_QUALITY, 44);
                editor.apply();
            }
        });
    }

    private void loadAudioSetting() {

        SharedPreferences sharedPreferences = this.getSharedPreferences(Constants.AUDIO_SETTING, Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            int checkFormatType = sharedPreferences.getInt(Constants.FORMAT_TYPE, 0);
            if (checkFormatType == 0) {
                radioButtonMp3.setChecked(true);
                radioButtonWav.setChecked(false);
            }else if(checkFormatType == 1){
                radioButtonMp3.setChecked(false);
                radioButtonWav.setChecked(true);
            }

            int checkQuality = sharedPreferences.getInt(Constants.FORMAT_QUALITY, 16);
            if (checkQuality == 16) {
                radioButton16kHz.setChecked(true);

            } else if (checkQuality == 22) {
                radioButton22kHz.setChecked(true);

            } else if (checkQuality == 32) {
                radioButton32kHz.setChecked(true);

            } else if (checkQuality == 44) {
                radioButton44kHz.setChecked(true);

            }
        }

    }
}