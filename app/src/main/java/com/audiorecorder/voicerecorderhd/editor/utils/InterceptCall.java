//package com.audiorecorder.voicerecorderhd.editor.utils;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.telephony.TelephonyManager;
//import android.widget.Toast;
//
//public class InterceptCall extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//        try {
//            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
//            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)){
//                Toast.makeText(context, "is calling", Toast.LENGTH_SHORT).show();
////                intent.putExtra("ring","is calling ");
//                Intent intentService = new Intent(Constants.PAUSE_ACTION);
//              //  sendBroadcast(intentService);
//            }
//            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)){
//                Toast.makeText(context, "end calling", Toast.LENGTH_SHORT).show();
//            }
//        }catch (Exception e){}
//    }
//}
