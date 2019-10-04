package com.audiorecorder.voicerecorderhd.editor.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.audiorecorder.voicerecorderhd.editor.R;

public class DetailAudioActivity extends AppCompatActivity {
    private TextView tv_name_audio,tv_path_audio,tv_size_audio;
    private ImageView iv_detail_play_audio , iv_detail_next1_audio , iv_detail_next2_audio , iv_detail_prev1_audio, iv_detail_prev2_audio;
    private Bundle bundle;
    MediaPlayer mediaPlayer;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_audio);

        intent = getIntent();
        mappingTv();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Uri uri = Uri.parse(intent.getStringExtra("path"));
        mediaPlayer = mediaPlayer.create(this, uri);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_play));

            }
        });
        iv_detail_play_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_play));
                    mediaPlayer.pause();
                } else {
                    iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_pause));
                    mediaPlayer.start();
                }

            }
        });
    }

    private void mappingTv() {
        iv_detail_play_audio = findViewById(R.id.iv_detail_play_audio);

        tv_name_audio = (TextView) findViewById(R.id.tv_name_audio);
        tv_path_audio = (TextView) findViewById(R.id.tv_path_audio);
        tv_size_audio = (TextView) findViewById(R.id.tv_size_audio);

        tv_name_audio.setText(intent.getStringExtra("name"));
        tv_size_audio.setText(intent.getStringExtra("size")+" kb");
        tv_path_audio.setText(intent.getStringExtra("path"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.pause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer.start();
    }
}

