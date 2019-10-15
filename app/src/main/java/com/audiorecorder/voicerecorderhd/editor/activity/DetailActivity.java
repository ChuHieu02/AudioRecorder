package com.audiorecorder.voicerecorderhd.editor.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.audiorecorder.voicerecorderhd.editor.fragment.FragmentInforDetail;
import com.audiorecorder.voicerecorderhd.editor.fragment.FragmentListAudio;
import com.audiorecorder.voicerecorderhd.editor.model.Audio;
import com.audiorecorder.voicerecorderhd.editor.utils.CommonUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;


public class DetailActivity extends AppCompatActivity implements View.OnClickListener, FragmentListAudio.FragmentDetailListListener {
    private ImageView ivPlay, ivNext1, ivNext2, ivPrev1, ivPrev2;
    private MediaPlayer mediaPlayer;
    private int position;
    private ArrayList<Audio> listAudio;
    private SeekBar seekBar;
    private Audio audio;
    private Bundle bundle;
    private SimpleDateFormat fomatTime = new SimpleDateFormat("mm:ss");
    private TextView tvStartDuration, tv_StopDuration;
    private FragmentInforDetail fragmentDetailInformation;
    private FragmentListAudio fragmentDetailListAudio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_library);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            listAudio = bundle.getParcelableArrayList("list");
            position = bundle.getInt("position");
            this.audio = listAudio.get(position);
        }

        this.fragmentDetailInformation = new FragmentInforDetail().setArguments(audio);
        this.fragmentDetailListAudio = new FragmentListAudio().setArguments(listAudio);

        List<Fragment> dataFragment = new ArrayList<>();
        dataFragment.add(fragmentDetailInformation);
        dataFragment.add(fragmentDetailListAudio);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), dataFragment);
        ViewPager viewPager = findViewById(R.id.vp_detail);
        viewPager.setAdapter(sectionsPagerAdapter);
        CircleIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        mappingTv();
        pLayAudio();

        if (mediaPlayer == null) {
            Toast.makeText(DetailActivity.this, "Play audio fail !", Toast.LENGTH_SHORT).show();
        }else {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mediaPlayer != null) {
                        mp.release();
                        ivPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_play));
                    }
                }
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    try {
                        mediaPlayer.seekTo(seekBar.getProgress());
                    } catch (Exception e) {
                    }
                }
            });
        }


        ivPlay.setOnClickListener(this);
        ivNext1.setOnClickListener(this);
        ivNext2.setOnClickListener(this);
        ivPrev1.setOnClickListener(this);
        ivPrev2.setOnClickListener(this);
    }

    private void setMaxTime() {
        this.seekBar.setMax(this.mediaPlayer.getDuration());
        tv_StopDuration.setText(CommonUtils.formatTime(this.mediaPlayer.getDuration()));
    }

    private void UpdateTime() {
        final Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    tvStartDuration.setText(CommonUtils.formatTime(mediaPlayer.getCurrentPosition()));
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    mHandler.postDelayed(this, 1000);
                } catch (Exception ex) {
                }
            }
        }, 100);
    }

    private void mappingTv() {
        seekBar = findViewById(R.id.seekbar);
        ivPlay = findViewById(R.id.iv_play);
        ivNext1 = findViewById(R.id.iv_next1);
        ivNext2 = findViewById(R.id.iv_next2);
        ivPrev1 = findViewById(R.id.iv_prev1);
        ivPrev2 = findViewById(R.id.iv_prev2);

        tvStartDuration = findViewById(R.id.tv_start_duration);
        tv_StopDuration = findViewById(R.id.tv_stop_duration);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }

        } catch (Exception e) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (this.mediaPlayer != null) {
                if (!mediaPlayer.isPlaying()) {
                    this.mediaPlayer.start();
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mediaPlayer.release();
            mediaPlayer = null;
        } catch (Exception e) {
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_play:
                try {
                    if (this.mediaPlayer != null) {
                        if (this.mediaPlayer.isPlaying()) {
                            ivPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_play));
                            this.mediaPlayer.pause();
                        } else {
                            this.mediaPlayer.start();
                            ivPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_pause));
                        }
                    } else {
                        createMedia();
                        ivPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_pause));
                    }
                } catch (Exception e) {
                }
                break;
            case R.id.iv_next2:
                this.position++;
                if (this.position > listAudio.size() - 1) {
                    this.position = 0; }
                this.audio = listAudio.get(position);
                pLayAudio();
                break;

            case R.id.iv_prev2:
                this.position--;
                if (this.position < 0) {
                    this.position = listAudio.size() - 1; }
                this.audio = listAudio.get(position);
                //TODO: review va optimize lai doan nay
                pLayAudio();
                break;

            case R.id.iv_next1:
                try {
                    this.mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
                    Log.e("zxcvbn", mediaPlayer.getCurrentPosition() + "");
                } catch (Exception e) {
                }
                break;
            case R.id.iv_prev1:
                try {
                    this.mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
                } catch (Exception e) {
                }
                break;
        }
    }

    private void createMedia() {
        this.mediaPlayer = MediaPlayer.create(this, Uri.fromFile(new File(audio.getPath())));
        this.mediaPlayer.start();
    }

    @Override
    public void sendPosition(int i) {
        this.position = i;
        this.audio = listAudio.get(position);
        pLayAudio();
        tv_StopDuration.setText(fomatTime.format(this.mediaPlayer.getDuration()));
    }

    private void pLayAudio() {
        try {
            if (this.mediaPlayer != null) {
                this.mediaPlayer.release();
                mediaPlayer = null;
                this.mediaPlayer = MediaPlayer.create(this, Uri.fromFile(new File(audio.getPath())));
                this.mediaPlayer.start();
                setMaxTime();
                UpdateTime();
                ivPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_pause));
            } else {
                this.mediaPlayer = MediaPlayer.create(this, Uri.fromFile(new File(audio.getPath())));
                this.mediaPlayer.start();
                setMaxTime();
                UpdateTime();
                ivPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_pause));
            }

            fragmentDetailInformation.updateFragInfor(audio);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

