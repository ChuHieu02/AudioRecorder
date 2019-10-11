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
    private RadioGroup rgFormatType, rgSetQuality;
    private RadioButton rbMp3, rbWav;
    private RadioButton rb16kHz, rb22kHz, rb32kHz, rb44kHz;
    private Button btChooseFolder;
    private TextView textView;
    private TextView tvPathSetting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mappingToolbar();
        settingAudio();
        loadAudioSetting();
        SharedPreferences sharedPreferences = this.getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            String path = sharedPreferences.getString(Constants.K_DIRECTION_CHOOSER_PATH, Constants.K_DEFAULT_PATH);
            tvPathSetting.setText(path);
        } else {
            tvPathSetting.setText(Constants.K_DEFAULT_PATH);

        }

    }

    private void mappingToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Library");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvPathSetting = findViewById(R.id.tv_path_setting);

        rgFormatType = findViewById(R.id.rg_FormatType);
        rgSetQuality = findViewById(R.id.rg_SetQuality);
        rbMp3 = findViewById(R.id.rb_Mp3);
        rbWav = findViewById(R.id.rb_Wav);
        rb16kHz = findViewById(R.id.rb_16kHz);
        rb22kHz = findViewById(R.id.rb_22kHz);
        rb32kHz = findViewById(R.id.rb_32kHz);
        rb44kHz = findViewById(R.id.rb_44kHz);
        btChooseFolder = findViewById(R.id.bt_Choose_Folder);
        textView = findViewById(R.id.textView);
        btChooseFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
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
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constants.K_DIRECTION_CHOOSER_PATH, path);
                        editor.apply();
                        tvPathSetting.setText(path);

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
        SharedPreferences sharedPreferences = this.getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        rbWav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(Constants.K_FORMAT_TYPE, 1);
                editor.apply();
            }
        });

        rbMp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(Constants.K_FORMAT_TYPE, 0);
                editor.apply();
            }
        });

        rb16kHz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(Constants.K_FORMAT_QUALITY, 16);
                editor.apply();

            }

        });

        rb22kHz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(Constants.K_FORMAT_QUALITY, 22);
                editor.apply();

            }
        });

        rb32kHz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(Constants.K_FORMAT_QUALITY, 32);
                editor.apply();
            }
        });

        rb44kHz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(Constants.K_FORMAT_QUALITY, 44);
                editor.apply();
            }
        });
    }

    private void loadAudioSetting() {

        SharedPreferences sharedPreferences = this.getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            int checkFormatType = sharedPreferences.getInt(Constants.K_FORMAT_TYPE, 0);
            if (checkFormatType == 0) {
                rbMp3.setChecked(true);
                rbWav.setChecked(false);
            }else if(checkFormatType == 1){
                rbMp3.setChecked(false);
                rbWav.setChecked(true);
            }

            int checkQuality = sharedPreferences.getInt(Constants.K_FORMAT_QUALITY, 16);
            if (checkQuality == 16) {
                rb16kHz.setChecked(true);

            } else if (checkQuality == 22) {
                rb22kHz.setChecked(true);

            } else if (checkQuality == 32) {
                rb32kHz.setChecked(true);

            } else if (checkQuality == 44) {
                rb44kHz.setChecked(true);

            }
        }

    }
}