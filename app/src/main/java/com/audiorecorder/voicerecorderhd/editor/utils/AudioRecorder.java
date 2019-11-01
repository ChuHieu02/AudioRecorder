package com.audiorecorder.voicerecorderhd.editor.utils;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import java.io.IOException;

public class AudioRecorder implements BaseAudioRecord {

    private static MediaRecorder mediaRecorder;


    @Override
    public void setupMediaRecorder(String outputFile, int bitRate,int outputFormat) {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setAudioChannels(1);
        if(outputFormat == 0){

            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }else if(outputFormat == 1){

            mediaRecorder.setOutputFormat(AudioFormat.CHANNEL_CONFIGURATION_MONO);
            mediaRecorder.setAudioEncoder(AudioFormat.ENCODING_PCM_16BIT);
        }
        if (bitRate == 16) {
            mediaRecorder.setAudioEncodingBitRate(16);
            mediaRecorder.setAudioSamplingRate(16 * Constants.K_SAMPLE_RATE_QUALITY);

        } else if (bitRate == 22) {
            mediaRecorder.setAudioEncodingBitRate(24);
            mediaRecorder.setAudioSamplingRate(24 * Constants.K_SAMPLE_RATE_QUALITY);

        } else if (bitRate == 32) {
            mediaRecorder.setAudioEncodingBitRate(32);
            mediaRecorder.setAudioSamplingRate(32 * Constants.K_SAMPLE_RATE_QUALITY);

        } else if (bitRate == 44) {
            mediaRecorder.setAudioEncodingBitRate(192000);
            mediaRecorder.setAudioSamplingRate(44100);
        }
        mediaRecorder.setOutputFile(outputFile);

    }

    @Override
    public void startRecord() {
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();

        } catch (IllegalStateException ise) {
            // make something ...
        } catch (IOException ioe) {
            // make something
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void pasueRecord() {
        if (mediaRecorder != null) {
            mediaRecorder.pause();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void resumeRecord() {
        if (mediaRecorder != null) {
            mediaRecorder.resume();
        }

    }

    @Override
    public void stopRecord() {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;

            }
        } catch (Exception e) {
            Log.e("Media", "Null Media File" );
        }
    }
}
