package com.audiorecorder.voicerecorderhd.editor.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.audiorecorder.voicerecorderhd.editor.MainActivity;
import com.audiorecorder.voicerecorderhd.editor.R;
import com.audiorecorder.voicerecorderhd.editor.utils.CommonUtils;
import com.audiorecorder.voicerecorderhd.editor.utils.Constants;
import com.codekidlabs.storagechooser.StorageChooser;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private RadioGroup rgFormatType, rgSetQuality;
    private ImageView ivBottomLibrary;
    private ImageView ivBottomRecoder;
    private ImageView ivBottomSettings;
    private LinearLayout viewChooseFolder;
    private LinearLayout viewChooseFileType;
    private LinearLayout viewChooseQuality;
    private AlertDialog dialog;
    private SharedPreferences sharedPreferences;
    private TextView tvResponFileTypeSetting, tvResponQualitySetting, locationFileSetting;
    private ConstraintLayout viewCbStopRecordCalling;
    private AppCompatCheckBox cbStopIsCalling;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getResources().getString(R.string.label_setting));
        mapping();

        sharedPreferences = this.getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            getKeyQuality(sharedPreferences);
            getKeyPath(sharedPreferences);
            getKeyFileType(sharedPreferences);
            getKeyStopCalling(sharedPreferences);
        }

    }

    private void getKeyStopCalling(SharedPreferences sharedPreferences) {
        cbStopIsCalling.setChecked(sharedPreferences.getBoolean(Constants.K_STOP_IS_CALLING, false));
//        Toast.makeText(this, ""+sharedPreferences.getBoolean(Constants.K_STOP_IS_CALLING, false), Toast.LENGTH_SHORT).show();
    }

    private void getKeyFileType(SharedPreferences s) {
        int checkFormatType = s.getInt(Constants.K_FORMAT_TYPE, 0);
        if (checkFormatType == 0) {
            tvResponFileTypeSetting.setText(Constants.K_FORMAT_TYPE_MP3);
            return;
        }
        tvResponFileTypeSetting.setText(Constants.K_FORMAT_TYPE_WAV);
    }

    private void getKeyPath(SharedPreferences s) {
        if (s != null) {
            String path = s.getString(Constants.K_DIRECTION_CHOOSER_PATH, Constants.K_DEFAULT_PATH);
            locationFileSetting.setText(path);
            return;
        }
        locationFileSetting.setText(Constants.K_DEFAULT_PATH);
    }

    @SuppressLint("SetTextI18n")
    private void getKeyQuality(SharedPreferences s) {
        int checkQuality = s.getInt(Constants.K_FORMAT_QUALITY, 16);
        tvResponQualitySetting.setText(checkQuality + " kHz");
    }

    private void mapping() {
        viewCbStopRecordCalling = findViewById(R.id.view_cb_stop_record_calling);
        cbStopIsCalling = findViewById(R.id.cb_stop_is_calling);

        ivBottomLibrary = findViewById(R.id.iv_bottom_library);
        ivBottomRecoder = findViewById(R.id.iv_bottom_recoder);
        ivBottomSettings = findViewById(R.id.iv_bottom_settings);

        viewChooseFolder = findViewById(R.id.view_choose_folder_setting);
        viewChooseFileType = findViewById(R.id.view_choose_filetype_setting);
        viewChooseQuality = findViewById(R.id.view_choose_quality_setting);

        locationFileSetting = findViewById(R.id.tv_respon_choose_file_setting);
        tvResponQualitySetting = findViewById(R.id.tv_respon_quality_setting);
        tvResponFileTypeSetting = findViewById(R.id.tv_respon_file_type_setting);

        ivBottomSettings.setImageDrawable(getResources().getDrawable(R.drawable.ic_settings_pr));
        ivBottomLibrary.setOnClickListener(this);
        ivBottomRecoder.setOnClickListener(this);
        viewChooseFolder.setOnClickListener(this);
        viewChooseFileType.setOnClickListener(this);
        viewChooseQuality.setOnClickListener(this);
        viewCbStopRecordCalling.setOnClickListener(this);
        cbStopIsCalling.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor_stop_is_calling = preferences.edit();
                if (!isChecked) {
                    editor_stop_is_calling.putBoolean(Constants.K_STOP_IS_CALLING, false);
                    editor_stop_is_calling.apply();
                } else {
                    editor_stop_is_calling.putBoolean(Constants.K_STOP_IS_CALLING, true);
                    editor_stop_is_calling.apply();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_bottom_recoder:
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                break;
            case R.id.iv_bottom_library:
                startActivity(new Intent(SettingsActivity.this, LibraryActivity.class));
                break;
            case R.id.view_choose_folder_setting:
                chooseFolder();
                break;
            case R.id.view_choose_quality_setting:
                chooseQuality();
                break;
            case R.id.view_choose_filetype_setting:
                chooseFileType();
                break;
            case R.id.view_cb_stop_record_calling:
                checkCalling();
                break;

        }
    }

    private void checkCalling() {
        SharedPreferences preferences = this.getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor_stop_is_calling = preferences.edit();

        if (cbStopIsCalling.isChecked()) {
            cbStopIsCalling.setChecked(false);
            editor_stop_is_calling.putBoolean(Constants.K_STOP_IS_CALLING, false);
            editor_stop_is_calling.apply();
        } else {
            cbStopIsCalling.setChecked(true);
            editor_stop_is_calling.putBoolean(Constants.K_STOP_IS_CALLING, true);

            editor_stop_is_calling.apply();
        }

    }

    private void chooseFolder() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
        StorageChooser chooser = new StorageChooser.Builder()
                .withActivity(SettingsActivity.this)
                .withPredefinedPath(Constants.STATIC_PATH)
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
                locationFileSetting.setText(path);

            }
        });
    }

    private void chooseFileType() {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        final View viewDialog2 = LayoutInflater.from(this).inflate(R.layout.dialog_setting_file_type, null);
        builder2.setView(viewDialog2);

        RadioButton rbMp3, rbWav;

        rgFormatType = findViewById(R.id.rg_FormatType);
        rbMp3 = viewDialog2.findViewById(R.id.rb_Mp3);
        rbWav = viewDialog2.findViewById(R.id.rb_Wav);

        if (this.sharedPreferences != null) {
            int checkFormatType = this.sharedPreferences.getInt(Constants.K_FORMAT_TYPE, 0);
            if (checkFormatType == 0) {
                rbMp3.setChecked(true);
                rbWav.setChecked(false);
            } else if (checkFormatType == 1) {
                rbMp3.setChecked(false);
                rbWav.setChecked(true);
            }

        }
        final SharedPreferences.Editor editor2 = this.sharedPreferences.edit();
        final SharedPreferences.Editor editor_memory_size = this.sharedPreferences.edit();

        rbWav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor2.putInt(Constants.K_FORMAT_TYPE, 1);
                editor2.apply();
                dialog.dismiss();
                tvResponFileTypeSetting.setText(Constants.K_FORMAT_TYPE_WAV);
                editor_memory_size.putInt(Constants.K_MEMORY_FREE, 93);
                editor_memory_size.apply();
            }
        });

        rbMp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor2.putInt(Constants.K_FORMAT_TYPE, 0);
                editor2.apply();
                dialog.dismiss();
                tvResponFileTypeSetting.setText(Constants.K_FORMAT_TYPE_MP3);
                editor_memory_size.putInt(Constants.K_MEMORY_FREE, 15);
                editor_memory_size.apply();
            }
        });
        builder2.create();
        dialog = builder2.show();
    }

    private void chooseQuality() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View viewDialog = LayoutInflater.from(this).inflate(R.layout.dialog_setting_quality, null);
        builder.setView(viewDialog);

        RadioButton rb16kHz, rb22kHz, rb32kHz, rb44kHz;

        rgSetQuality = viewDialog.findViewById(R.id.rg_SetQuality);
        rb16kHz = viewDialog.findViewById(R.id.rb_16kHz);
        rb22kHz = viewDialog.findViewById(R.id.rb_22kHz);
        rb32kHz = viewDialog.findViewById(R.id.rb_32kHz);
        rb44kHz = viewDialog.findViewById(R.id.rb_44kHz);


        if (this.sharedPreferences != null) {

            int checkQuality = this.sharedPreferences.getInt(Constants.K_FORMAT_QUALITY, 16);

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


        final SharedPreferences.Editor editor = this.sharedPreferences.edit();
        rb16kHz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(Constants.K_FORMAT_QUALITY, 16);
                editor.apply();
                dialog.dismiss();

                tvResponQualitySetting.setText(Constants.K_QUALITY_16);

            }

        });

        rb22kHz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(Constants.K_FORMAT_QUALITY, 22);
                editor.apply();
                dialog.dismiss();

                tvResponQualitySetting.setText(Constants.K_QUALITY_22);


            }
        });

        rb32kHz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(Constants.K_FORMAT_QUALITY, 32);
                editor.apply();
                dialog.dismiss();

                tvResponQualitySetting.setText(Constants.K_QUALITY_32);

            }
        });

        rb44kHz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(Constants.K_FORMAT_QUALITY, 44);
                editor.apply();
                dialog.dismiss();

                tvResponQualitySetting.setText(Constants.K_QUALITY_44);

            }
        });

        builder.create();
        dialog = builder.show();
    }

    @SuppressLint("WrongConstant")
    public void setTitle(String title) {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setTextSize(25);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.color_white));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(textView);
    }


}