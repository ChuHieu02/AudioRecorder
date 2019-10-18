package com.audiorecorder.voicerecorderhd.editor.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Build;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;

public class AudioRecordSetup {

    public  MediaRecorder mAudioRecorder;
    public static String outputFile;

    public  void  setupMediaRecorder(Context context){

        SharedPreferences sharedPreferences= context.getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
        if(sharedPreferences!= null){
            int checkStatus = sharedPreferences.getInt(Constants.K_FORMAT_TYPE,0);
            String pathDirector = sharedPreferences.getString(Constants.K_DIRECTION_CHOOSER_PATH,Constants.K_DEFALT_PATH);
            File file = new File(pathDirector);
            if(checkStatus == 0){
                outputFile ="/"+ file.getAbsolutePath()+"/RecordFile"+System.currentTimeMillis()+".mp3";
            }else if(checkStatus == 1){
                outputFile ="/"+ file.getAbsolutePath()+"/RecordFile"+System.currentTimeMillis()+".wav";
            }
            if (!file.exists()) {
                file.mkdirs();
            }
        }
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
        mAudioRecorder.setOutputFile(outputFile);
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
    public void pauseRecording(){
        if (mAudioRecorder!=null){
            mAudioRecorder.pause();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public  void resumeRecording(){
        if (mAudioRecorder != null) {
            mAudioRecorder.resume();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public  void stopRecording(){
        try {
            if (mAudioRecorder != null) {
                mAudioRecorder.stop();
                mAudioRecorder.release();
                mAudioRecorder = null;
            }
        }catch (NullPointerException e){

        }
    }
}
