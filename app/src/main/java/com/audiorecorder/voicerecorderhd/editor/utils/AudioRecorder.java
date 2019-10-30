package com.audiorecorder.voicerecorderhd.editor.utils;
import android.media.MediaRecorder;

public class AudioRecorder {

    public  MediaRecorder mAudioRecorder;
    public  String outputFile;
    public  String pathFile;
    public  long dateTime;
    public  long fileSize;
    public  String audioName;
    BaseAudioRecord callback;
    public AudioRecorder(BaseAudioRecord callback) {
        this.callback = callback;
    }

    public MediaRecorder getmAudioRecorder() {
        return mAudioRecorder;
    }

    public void setmAudioRecorder(MediaRecorder mAudioRecorder) {
        this.mAudioRecorder = mAudioRecorder;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getPathFile() {
        return pathFile;
    }

    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }


}
