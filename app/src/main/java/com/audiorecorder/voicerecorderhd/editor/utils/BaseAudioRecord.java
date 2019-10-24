package com.audiorecorder.voicerecorderhd.editor.utils;

import android.content.Context;

public interface BaseAudioRecord {

    public void creatFile(Context context);

    public void setupMediaRecorder(Context context);

    public void startRecord();

    public void pasueRecord();

    public void resumeRecord();

    public void stopRecord(Context context);

}
