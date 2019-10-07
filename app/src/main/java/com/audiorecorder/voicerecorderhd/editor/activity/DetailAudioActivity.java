package com.audiorecorder.voicerecorderhd.editor.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.audiorecorder.voicerecorderhd.editor.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DetailAudioActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_name_detail, tv_path_detail, tv_size_detail, tv_time_detail, tv_duration_detail;
    private ImageView iv_detail_play_audio, iv_detail_next1_audio, iv_detail_next2_audio, iv_detail_prev1_audio, iv_detail_prev2_audio;
    Intent intent;
    MediaPlayer mediaPlayer;
    private int position;
    private ArrayList<File> audioSong;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SeekBar seekBarDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_library);

        Bundle bundle = getIntent().getExtras();
        audioSong = readAudio(new File(Environment.getExternalStorageDirectory() + File.separator + "Recorder"));


        intent = getIntent();

        mappingTv();
        mediaPlayer = MediaPlayer.create(this, Uri.fromFile(audioSong.get(position)));
//        mediaPlayer = MediaPlayer.create(this, Uri.parse(intent.getStringExtra("path")));
        if (mediaPlayer != null) {
            mediaPlayer.start();
            iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_filled_black_24dp));
        }
        if (mediaPlayer == null) {
            Toast.makeText(DetailAudioActivity.this, "Play audio fail !", Toast.LENGTH_SHORT).show();
            return;
        }


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));

            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });


        iv_detail_play_audio.setOnClickListener(this);
        iv_detail_next1_audio.setOnClickListener(this);
        iv_detail_next2_audio.setOnClickListener(this);
        iv_detail_prev1_audio.setOnClickListener(this);
        iv_detail_prev2_audio.setOnClickListener(this);

    }

    private void mappingTv() {
        seekBarDetail = (SeekBar) findViewById(R.id.seekBar_detail);

        iv_detail_play_audio = findViewById(R.id.iv_detail_play_audio);
        iv_detail_next1_audio = findViewById(R.id.iv_detail_next1_audio);
        iv_detail_next2_audio = findViewById(R.id.iv_detail_next2_audio);
        iv_detail_prev1_audio = findViewById(R.id.iv_detail_prev1_audio);
        iv_detail_prev2_audio = findViewById(R.id.iv_detail_prev2_audio);
        tv_duration_detail = findViewById(R.id.tv_duration_detail);


        tv_name_detail = (TextView) findViewById(R.id.tv_name_detail);
        tv_path_detail = (TextView) findViewById(R.id.tv_path_detail);
        tv_size_detail = (TextView) findViewById(R.id.tv_size_detail);
        tv_time_detail = (TextView) findViewById(R.id.tv_time_detail);

        tv_name_detail.setText(intent.getStringExtra("name"));
        tv_size_detail.setText(intent.getStringExtra("size"));
        tv_path_detail.setText(intent.getStringExtra("path"));
        tv_time_detail.setText(intent.getStringExtra("date"));
        tv_duration_detail.setText(intent.getStringExtra("duration"));
        position = intent.getIntExtra("position", 0);


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_detail_play_audio:
                if (mediaPlayer.isPlaying()) {
                    iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                    mediaPlayer.pause();
                } else {
                    iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_filled_black_24dp));
                    mediaPlayer.start();
                }
                break;

            case R.id.iv_detail_next2_audio:
                position++;
                if (position > audioSong.size() - 1) {
                    position = 0;
                }

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer = MediaPlayer.create(this, Uri.fromFile(audioSong.get(position)));
                if (mediaPlayer!=null){
                    mediaPlayer.start();
                }
                if (mediaPlayer == null) {
                    Toast.makeText(DetailAudioActivity.this, "Play audio fail !", Toast.LENGTH_SHORT).show();
                    return;
                }
                tv_name_detail.setText(audioSong.get(position).getName());
                tv_path_detail.setText(audioSong.get(position).getPath());

//                tv_duration_detail.setText(simpleDateFormat.format(mediaPlayer.getDuration()));

                String date = dateFormat.format(audioSong.get(position).lastModified());
                tv_time_detail.setText(date);
                iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_filled_black_24dp));

                break;

            case R.id.iv_detail_prev2_audio:
                position--;
                if (position < 0) {
                    position = audioSong.size() - 1;
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer = MediaPlayer.create(this, Uri.fromFile(audioSong.get(position)));
                if (mediaPlayer!=null){
                    mediaPlayer.start();
                }
                if (mediaPlayer == null) {
                    Toast.makeText(DetailAudioActivity.this, "Play audio fail !", Toast.LENGTH_SHORT).show();
                    return;
                }
                tv_name_detail.setText(audioSong.get(position).getName());
                tv_path_detail.setText(audioSong.get(position).getPath());
//                tv_duration_detail.setText(simpleDateFormat.format(mediaPlayer.getDuration()));
                String date2 = dateFormat.format(audioSong.get(position).lastModified());
                tv_time_detail.setText(date2);
                iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_filled_black_24dp));

                break;
        }
    }

    public ArrayList<File> readAudio(File file) {
        ArrayList<File> arrayList = new ArrayList<>();


        File[] files = file.listFiles();

        if (files == null) {
            return arrayList;
        }
        for (File invidualFile : files) {
            if (invidualFile.isDirectory() && !invidualFile.isHidden()) {
                arrayList.addAll(readAudio(invidualFile));

            } else {
                if (invidualFile.getName().endsWith(".mp3") || invidualFile.getName().endsWith(".wav")) {
                    arrayList.add(invidualFile);
                }
            }
        }
        return arrayList;
    }
}

