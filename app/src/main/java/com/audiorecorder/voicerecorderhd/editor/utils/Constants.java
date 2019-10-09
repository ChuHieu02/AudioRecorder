package com.audiorecorder.voicerecorderhd.editor.utils;

import android.os.Environment;

import java.io.File;

public class Constants {
    public static final String FORMAT_TYPE = "formatType";
    public static final String DIRECTION_CHOOSER_PATH = "directionPath";
    public static final String FORMAT_QUALITY = "formatQuality";
    public static final String AUDIO_SETTING = "audioSetting";
    public static final String DEFALT_PATH = Environment.getExternalStorageDirectory() + File.separator + "Recorder";
}
