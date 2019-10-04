package com.audiorecorder.voicerecorderhd.editor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_list_recording, R.id.navigation_recording, R.id.navigation_setting)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(navView, navController);

    }

//    public void testComit() {
//        /// test commit
//
//        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Recorder");
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        final MediaPlayer mediaPlayer = new MediaPlayer();
//        File[] files = file.listFiles();
//        for (File file1 : files) {
//            if (file1.getPath().toLowerCase().endsWith(".mp3")) {
//                try {
//
//                    mediaPlayer.setDataSource(file1.getAbsolutePath());
//                    mediaPlayer.prepare();
//                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mp) {
//
//                        }
//                    });
//                    mediaPlayer.start();
//                } catch (Exception ex) {
//
//                }
//                break;
//            }
//        }
//    }


}
