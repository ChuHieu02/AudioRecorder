package com.audiorecorder.voicerecorderhd.editor.utils;

import android.media.MediaMetadataRetriever;

import java.text.SimpleDateFormat;

public class CommonUtils {


    public static String fomatDate(long date) {
        String dateRespon = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateRespon = dateFormat.format(date);
        return dateRespon;
    }

    public static String formatSize(long size) {
        String suffix = null;
        if (size >= 1024) {
            suffix = " Kb";
            size /= 1024;
            if (size >= 1024) {
                suffix = " Mb";
                size /= 1024;
                if (size >= 1024) {
                    suffix = " Gb";
                    size /= 1024;
                }
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
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

    public static class TimeAgo {
        private static final int SECOND_MILLIS = 1000;
        private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

        public static final String getTimeAgo(long time) {
            if (time < 1000000000000L) {
                time *= 1000;
            }
            long now = System.currentTimeMillis();
            if (time > now || time <= 0) {
                return null;
            }
            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " minutes ago";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " hours ago";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " days ago";
            }
        }

    }


}
