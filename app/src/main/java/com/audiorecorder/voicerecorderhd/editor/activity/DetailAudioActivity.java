package com.audiorecorder.voicerecorderhd.editor.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.audiorecorder.voicerecorderhd.editor.R;
import com.audiorecorder.voicerecorderhd.editor.adapter.SectionsPagerAdapter;
import com.audiorecorder.voicerecorderhd.editor.fragment.FragmentDetailInformation;
import com.audiorecorder.voicerecorderhd.editor.fragment.FragmentDetailListAudio;
import com.audiorecorder.voicerecorderhd.editor.model.Audio;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;


public class DetailAudioActivity extends AppCompatActivity implements View.OnClickListener, FragmentDetailListAudio.FragmentDetailListListener {
    private ImageView iv_detail_play_audio, iv_detail_next1_audio, iv_detail_next2_audio, iv_detail_prev1_audio, iv_detail_prev2_audio;
    private MediaPlayer mediaPlayer;
    private int position;
    private ArrayList<Audio> listAudio;
    private SeekBar seekBarDetail;
    private Audio audio;
    private Bundle bundle;
    private Thread thread;
    private SimpleDateFormat fomatTime = new SimpleDateFormat("mm:ss");
    private TextView tv_detail_dration_start, tv_detail_dration_stop;
    private FragmentDetailInformation fragmentDetailInformation;
    private FragmentDetailListAudio fragmentDetailListAudio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_library);


        bundle = getIntent().getExtras();

        if (bundle != null) {
            listAudio = bundle.getParcelableArrayList("list");
            position = bundle.getInt("position");
        }
        audio = listAudio.get(position);

        this.fragmentDetailInformation = new FragmentDetailInformation().setArguments(audio);
        this.fragmentDetailListAudio = new FragmentDetailListAudio().setArguments(listAudio);

        List<Fragment> dataFragment = new ArrayList<>();
        dataFragment.add(fragmentDetailInformation);
        dataFragment.add(fragmentDetailListAudio);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), dataFragment);
        ViewPager viewPager = findViewById(R.id.vp_detail);
        viewPager.setAdapter(sectionsPagerAdapter);
        CircleIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        this.mediaPlayer = MediaPlayer.create(this, Uri.fromFile(new File(audio.getPath())));

        mappingTv();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int curenposition = 0;
                while (curenposition < totalDuration) {
                    try {
                        thread.sleep(1000);
                        curenposition = mediaPlayer.getCurrentPosition();
                        seekBarDetail.setProgress(curenposition);
                        tv_detail_dration_start.setText(fomatTime.format(curenposition));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mediaPlayer != null) {
                    iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_filled_black_24dp));
                    seekBarDetail.setMax(mediaPlayer.getDuration());
                    mediaPlayer.start();
                    thread.start();
                }
            }
        });

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

        seekBarDetail.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBarDetail.getProgress());
            }
        });
        iv_detail_play_audio.setOnClickListener(this);
        iv_detail_next1_audio.setOnClickListener(this);
        iv_detail_next2_audio.setOnClickListener(this);
        iv_detail_prev1_audio.setOnClickListener(this);
        iv_detail_prev2_audio.setOnClickListener(this);

    }


    private void mappingTv() {
        seekBarDetail = findViewById(R.id.seekBar_detail);

        iv_detail_play_audio = findViewById(R.id.iv_detail_play_audio);
        iv_detail_next1_audio = findViewById(R.id.iv_detail_next1_audio);
        iv_detail_next2_audio = findViewById(R.id.iv_detail_next2_audio);
        iv_detail_prev1_audio = findViewById(R.id.iv_detail_prev1_audio);
        iv_detail_prev2_audio = findViewById(R.id.iv_detail_prev2_audio);


        tv_detail_dration_start = findViewById(R.id.tv_detail_dration_start);
        tv_detail_dration_stop = findViewById(R.id.tv_detail_dration_stop);

        tv_detail_dration_stop.setText(fomatTime.format(mediaPlayer.getDuration()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        } catch (Exception e) {
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_detail_play_audio:
                if (this.mediaPlayer.isPlaying()) {
                    iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                    this.mediaPlayer.pause();
                } else {
                    iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_filled_black_24dp));
                    this.mediaPlayer.start();
                }
                this.mediaPlayer.seekTo(seekBarDetail.getProgress());
                break;

            case R.id.iv_detail_next2_audio:
                this.position++;
                if (this.position > listAudio.size() - 1) {
                    this.position = 0;
                }
                this.audio = listAudio.get(position);
                try {
                    if (this.mediaPlayer.isPlaying()) {
                        this.mediaPlayer.stop();
                        this.mediaPlayer.release();
                    }
                    this.mediaPlayer = MediaPlayer.create(this, Uri.fromFile(new File(audio.getPath())));

                    if (this.mediaPlayer != null) {
                        this.mediaPlayer.start();
                        tv_detail_dration_stop.setText(fomatTime.format(mediaPlayer.getDuration()));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (this.mediaPlayer == null) {
                    Toast.makeText(DetailAudioActivity.this, "Play audio fail !", Toast.LENGTH_SHORT).show();
                    return;
                }

                iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_filled_black_24dp));

                fragmentDetailInformation.updateFragInfor(audio);

                break;

            case R.id.iv_detail_prev2_audio:
                this.position--;

                if (this.position < 0) {
                    this.position = listAudio.size() - 1;
                }
                this.audio = listAudio.get(position);
                try {

                    if (this.mediaPlayer != null && mediaPlayer.isPlaying()) {
                        this.mediaPlayer.stop();
                        this.mediaPlayer.release();
                    }
                    this.mediaPlayer = MediaPlayer.create(this, Uri.fromFile(new File(audio.getPath())));

                    if (this.mediaPlayer != null) {
                        this.mediaPlayer.start();
                        tv_detail_dration_stop.setText(fomatTime.format(mediaPlayer.getDuration()));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (this.mediaPlayer == null) {
                    Toast.makeText(DetailAudioActivity.this, "Play audio fail !", Toast.LENGTH_SHORT).show();
                    return;
                }


                iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_filled_black_24dp));
                fragmentDetailInformation.updateFragInfor(audio);

                break;
//
//            case R.id.iv_detail_next1_audio:
//                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+5000);
//                break;
//            case R.id.iv_detail_prev1_audio:
//                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-5000);
//                break;
        }
    }


    @Override
    public void sendPosition(int i) {
        this.position = i;
        this.audio = listAudio.get(position);
        try {
            if (this.mediaPlayer.isPlaying()) {
                this.mediaPlayer.stop();
                this.mediaPlayer.release();
            }
            this.mediaPlayer = MediaPlayer.create(this, Uri.fromFile(new File(audio.getPath())));

            if (this.mediaPlayer != null) {
                this.mediaPlayer.start();
                tv_detail_dration_stop.setText(fomatTime.format(mediaPlayer.getDuration()));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.mediaPlayer == null) {
            Toast.makeText(DetailAudioActivity.this, "Play audio fail !", Toast.LENGTH_SHORT).show();
            return;
        }

        iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_filled_black_24dp));

        fragmentDetailInformation.updateFragInfor(audio);
    }
}

