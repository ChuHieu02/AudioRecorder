package com.audiorecorder.voicerecorderhd.editor.utils;

import android.os.Environment;

import java.io.File;

public class Constants {
    public static final String K_FORMAT_TYPE = "formatType";
    public static final String K_DIRECTION_CHOOSER_PATH = "directionPath";
    public static final String K_FORMAT_QUALITY = "formatQuality";
    public static final String K_AUDIO_SETTING = "audioSetting";
    public static final int K_SAMPLE_RATE_QUALITY = 1000;
    public static final String PAUSE_ACTION = "PAUSE_ACTION";
    public static final String RESUME_ACTION = "RESUME_ACTION";
    public static final String STOP_ACTION = "STOP_ACTION";
    public static final String START_ACTION = "START_ACTION";
    public static final String SEND_TIME = "SEND_TIMER";
    public static final String TIME_COUNT = "time_count";
    public static final String K_FORMAT_TYPE_WAV = "Wav";
    public static final String K_FORMAT_TYPE_MP3 = "Mp3";
    public static final String K_DEFAULT_PATH = Environment.getExternalStorageDirectory() + File.separator + "Recorder";
    public static final String K_QUALITY_16 = "16 khz";
    public static final String K_QUALITY_22 = "22 khz";
    public static final String K_QUALITY_32 = "32 khz";
    public static final String K_QUALITY_44 = "44 khz";


}
