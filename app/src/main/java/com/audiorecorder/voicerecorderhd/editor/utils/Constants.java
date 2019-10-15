package com.audiorecorder.voicerecorderhd.editor.utils;

import android.os.Environment;

import java.io.File;

public class Constants {
    public static final String K_FORMAT_TYPE = "formatType";
    public static final String K_DIRECTION_CHOOSER_PATH = "directionPath";
    public static final String K_FORMAT_QUALITY = "formatQuality";
    public static final String K_AUDIO_SETTING = "audioSetting";
    public static final String K_DEFALT_PATH = Environment.getExternalStorageDirectory() + File.separator + "Recorder";
    public static final int K_SAMPLE_RATE_QUALITY = 1000;
}
