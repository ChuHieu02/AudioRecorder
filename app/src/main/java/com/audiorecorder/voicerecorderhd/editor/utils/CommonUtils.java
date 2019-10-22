package com.audiorecorder.voicerecorderhd.editor.utils;

import android.media.MediaMetadataRetriever;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class CommonUtils {



    public static String fomatDate(long date) {
        String dateRespon = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateRespon = dateFormat.format(date);
        return dateRespon;
    }

    public static long fomatSize(long size) {
        long sizeRespon ;
        sizeRespon = (size / 1024);
        return sizeRespon;
    }

    public static String formatToNumber(long data) {
        NumberFormat numberFormat = new DecimalFormat("###,###,###");
        return numberFormat.format((data));
    }

    public static String formatTime(long miliseconds) {
        String finaltimeSting = "";
        String timeSecond;

        int hourse = (int) (miliseconds / (1000 * 60 * 60));
        int minutes = (int) (miliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) (miliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000;

        if (hourse > 0) {
            finaltimeSting = hourse + " : ";
        }
        if (seconds < 10) {
            timeSecond = "0" + seconds;

        } else {
            timeSecond = "" + seconds ;
        }
        finaltimeSting = finaltimeSting + minutes + ":" + timeSecond;
        return finaltimeSting;
    }

    public static String getDuration(String path) {
        String durationRespon = "";
        String duration;
        try {
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();

            metaRetriever.setDataSource(path);
            duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long dur = Long.parseLong(duration);

            metaRetriever.release();
            durationRespon = formatTime(dur);

        } catch (Exception e) {

        }
        return durationRespon;
    }


}
