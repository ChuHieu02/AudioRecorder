package com.audiorecorder.voicerecorderhd.editor.utils;

import android.content.Context;

public interface BaseAudioRecord {

    public void setupMediaRecorder(String outputFile , int bitRate, int outPutFormat);

    public void startRecord();

    public void pasueRecord();

    public void resumeRecord();

    public void stopRecord();

}
