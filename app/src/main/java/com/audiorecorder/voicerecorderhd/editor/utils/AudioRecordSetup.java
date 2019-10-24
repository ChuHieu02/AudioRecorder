package com.audiorecorder.voicerecorderhd.editor.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;

public class AudioRecordSetup implements BaseAudioRecord {

    public  MediaRecorder mAudioRecorder;
    public  String outputFile;
    public  String pathFile;
    public  long dateTime;
    public  long fileSize;
    public  String audioName;

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

    @Override
    public void creatFile(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            int checkStatus = sharedPreferences.getInt(Constants.K_FORMAT_TYPE, 0);
            String pathDirector = sharedPreferences.getString(Constants.K_DIRECTION_CHOOSER_PATH, Environment.getExternalStorageDirectory() + File.separator + "Recorder");
            //pathFile = pathDirector;
            setPathFile(pathDirector);
         //   dateTime = System.currentTimeMillis();
            setDateTime(System.currentTimeMillis());
            File file = new File(pathDirector);
            if (checkStatus == 0) {
               // outputFile =  file.getAbsolutePath() + "/RecordFile" + System.currentTimeMillis() + ".mp3";
                setOutputFile(file.getAbsolutePath() + "/RecordFile" + System.currentTimeMillis() + ".mp3");
               // audioName = "RecordFile" + System.currentTimeMillis() + ".mp3";
                setAudioName("RecordFile" + System.currentTimeMillis() + ".mp3");
            } else if (checkStatus == 1) {
                //outputFile =  file.getAbsolutePath() + "/RecordFile" + System.currentTimeMillis() + ".wav";
                setOutputFile(file.getAbsolutePath() + "/RecordFile" + System.currentTimeMillis() + ".wav");
                //audioName = "RecordFile" + System.currentTimeMillis() + ".wav";
                setAudioName("RecordFile" + System.currentTimeMillis() + ".wav");
            }
            if (!file.exists()) {
                file.mkdirs();
            }
        }

    }

    @Override
    public void setupMediaRecorder(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
        mAudioRecorder = new MediaRecorder();
        if(sharedPreferences!= null){
            int checkStatus = sharedPreferences.getInt(Constants.K_FORMAT_TYPE,0);
            mAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            if(checkStatus == 0){
                mAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.MPEG_4);

            }else if(checkStatus == 1){
                mAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.MPEG_4);

            }
            int checkQuality = sharedPreferences.getInt(Constants.K_FORMAT_QUALITY,16);
            if(checkQuality == 16){
                mAudioRecorder.setAudioEncodingBitRate(16);
                mAudioRecorder.setAudioSamplingRate(16 * Constants.K_SAMPLE_RATE_QUALITY);

            }else if(checkQuality == 22){
                mAudioRecorder.setAudioEncodingBitRate(22);
                mAudioRecorder.setAudioSamplingRate(22 * Constants.K_SAMPLE_RATE_QUALITY);

            }else if(checkQuality == 32){
                mAudioRecorder.setAudioEncodingBitRate(32);
                mAudioRecorder.setAudioSamplingRate(32 * Constants.K_SAMPLE_RATE_QUALITY);

            }else if(checkQuality == 44){
                mAudioRecorder.setAudioEncodingBitRate(44);
                mAudioRecorder.setAudioSamplingRate(44100);

            }
        }
        mAudioRecorder.setOutputFile(getOutputFile());

    }

    @Override
    public void startRecord() {
        try {
            mAudioRecorder.prepare();
            mAudioRecorder.start();
        } catch (IllegalStateException ise) {
            // make something ...
        } catch (IOException ioe) {
            // make something
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void pasueRecord() {
        if (mAudioRecorder!=null){
            mAudioRecorder.pause();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void resumeRecord() {
        if (mAudioRecorder != null) {
            mAudioRecorder.resume();
        }
    }

    @Override
    public void stopRecord(Context context) {
        try {
            if (mAudioRecorder != null) {
                mAudioRecorder.stop();
                File file = new File(outputFile);
              //  fileSize = file.length();
                setFileSize(file.length());
                mAudioRecorder.release();
                mAudioRecorder = null;
            }
        } catch (Exception e) {
            Toast.makeText(context, "Null Media File", Toast.LENGTH_SHORT).show();
        }
    }
}
