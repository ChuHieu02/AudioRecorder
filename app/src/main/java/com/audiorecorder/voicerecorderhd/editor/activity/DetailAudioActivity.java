package com.audiorecorder.voicerecorderhd.editor.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
    private int totalDuration;
    private int curenposition;


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

        this.mediaPlayer = null;

            this.mediaPlayer = MediaPlayer.create(this, Uri.fromFile(new File(audio.getPath())));


        mappingTv();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    totalDuration = mediaPlayer.getDuration();
                } else {
                    mediaPlayer = null;
                    totalDuration = mediaPlayer.getDuration();
                }
                 curenposition = 0;
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
                    iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_pause));
                    seekBarDetail.setMax(mediaPlayer.getDuration());
                    thread.start();
                    mediaPlayer.start();
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
                if (mediaPlayer != null) {
                    seekBarDetail.setMax(0);
                    mp.release();
                    mp = null;
                }
                iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_play));

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
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(seekBarDetail.getProgress());
                }
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

        try {
            tv_detail_dration_stop.setText(fomatTime.format(mediaPlayer.getDuration()));
        } catch (Exception e) {
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }

        } catch (Exception e) {
        }
//        mediaPlayer.release();
//        mediaPlayer = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (this.mediaPlayer != null && !mediaPlayer.isPlaying()) {
                this.mediaPlayer.start();
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
            case R.id.iv_detail_play_audio:
                try {
                    if (this.mediaPlayer != null && this.mediaPlayer.isPlaying()) {
                        iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_play));
                        this.mediaPlayer.pause();

                    } else if (this.mediaPlayer != null) {
                        mediaPlayer.start();
                        iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_pause));
                    } else if (mediaPlayer == null) {
                        this.mediaPlayer = MediaPlayer.create(this, Uri.fromFile(new File(this.audio.getPath())));
                        mediaPlayer.start();
                        iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_pause));
                    }
                } catch (Exception e) {
                }

                break;

            case R.id.iv_detail_next2_audio:
                this.position++;
                if (this.position > listAudio.size() - 1) {
                    this.position = 0;
                }

                this.audio = listAudio.get(position);
                try {
                    if (this.mediaPlayer != null) {
                        iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_play));
                        this.mediaPlayer.release();
                        this.mediaPlayer = null;
                        this.mediaPlayer = MediaPlayer.create(this, Uri.fromFile(new File(audio.getPath())));
                        this.mediaPlayer.start();

                        this.curenposition = mediaPlayer.getCurrentPosition();
                        seekBarDetail.setProgress(curenposition);

                        tv_detail_dration_stop.setText(fomatTime.format(this.mediaPlayer.getDuration()));
                        iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_pause));
                    } else if (this.mediaPlayer == null) {
                        this.mediaPlayer = MediaPlayer.create(this, Uri.fromFile(new File(audio.getPath())));
                        this.mediaPlayer.start();

                        this.curenposition = mediaPlayer.getCurrentPosition();
                        seekBarDetail.setProgress(curenposition);

                        tv_detail_dration_stop.setText(fomatTime.format(this.mediaPlayer.getDuration()));
                        iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_pause));
                        Log.e("media", mediaPlayer + "");
                    }
                    fragmentDetailInformation.updateFragInfor(audio);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.iv_detail_prev2_audio:
                this.position--;
                if (this.position < 0) {
                    this.position = listAudio.size() - 1;
                }
                this.audio = listAudio.get(position);
                try {
                    if (this.mediaPlayer != null) {
                        iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_play));
                        this.mediaPlayer.release();
                        this.mediaPlayer = null;
                        this.mediaPlayer = MediaPlayer.create(this, Uri.fromFile(new File(audio.getPath())));
                        this.mediaPlayer.start();

                        tv_detail_dration_stop.setText(fomatTime.format(this.mediaPlayer.getDuration()));
                        iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_pause));
                    } else if (this.mediaPlayer == null) {
                        this.mediaPlayer = MediaPlayer.create(this, Uri.fromFile(new File(audio.getPath())));
                        this.mediaPlayer.start();


                        tv_detail_dration_stop.setText(fomatTime.format(this.mediaPlayer.getDuration()));
                        iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_pause));
                        Log.e("media", mediaPlayer + "");
                    }
                    fragmentDetailInformation.updateFragInfor(audio);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
//
            case R.id.iv_detail_next1_audio:
                this.mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
                break;
            case R.id.iv_detail_prev1_audio:
                this.mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
                break;
        }
    }


    @Override
    public void sendPosition(int i) {
        this.position = i;
        this.audio = listAudio.get(position);
        try {
            if (this.mediaPlayer != null) {
                this.mediaPlayer.release();
                mediaPlayer = null;
                this.mediaPlayer = MediaPlayer.create(this, Uri.fromFile(new File(audio.getPath())));
                this.mediaPlayer.start();
                tv_detail_dration_stop.setText(fomatTime.format(this.mediaPlayer.getDuration()));
                iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_pause));
            } else {
                this.mediaPlayer = null;
                this.mediaPlayer = MediaPlayer.create(this, Uri.fromFile(new File(audio.getPath())));
                this.mediaPlayer.start();
                tv_detail_dration_stop.setText(fomatTime.format(this.mediaPlayer.getDuration()));
                iv_detail_play_audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_pause));
                Log.e("media", mediaPlayer + "");
            }
            fragmentDetailInformation.updateFragInfor(audio);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

