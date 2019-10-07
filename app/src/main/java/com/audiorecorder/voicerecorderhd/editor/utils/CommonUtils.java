package com.audiorecorder.voicerecorderhd.editor.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class CommonUtils {



    public static String formatToNumber(String data) {
        NumberFormat numberFormat = new DecimalFormat("###,###,###");
        return numberFormat.format(Long.parseLong(data));
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
            timeSecond = "" + seconds;
        }
        finaltimeSting = finaltimeSting + minutes + ":" + timeSecond;
        return finaltimeSting;
    }


}
