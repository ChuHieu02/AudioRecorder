package com.audiorecorder.voicerecorderhd.editor.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.audiorecorder.voicerecorderhd.editor.MainActivity;
import com.audiorecorder.voicerecorderhd.editor.R;
import com.audiorecorder.voicerecorderhd.editor.adapter.LibraryAdapter;
import com.audiorecorder.voicerecorderhd.editor.data.DBQuerys;
import com.audiorecorder.voicerecorderhd.editor.model.Audio;
import com.audiorecorder.voicerecorderhd.editor.utils.CommonUtils;
import com.audiorecorder.voicerecorderhd.editor.utils.Constants;

import java.io.File;
import java.util.ArrayList;


public class LibraryActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rvLibrary;
    private LibraryAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<Audio> audioList = new ArrayList<>();
    private String formatDuration = "";
    private TextView tvEmpty;
    private LinearLayout progressDialog;
    private static final String TAG = "library";
    private ImageView ivBottomLibrary;
    private ImageView ivBottomRecoder;
    private ImageView ivBottomSettings;
    private TextView lbRecoder;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        mapping();
        new queryFile().execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_bottom_recoder:
                startActivity(new Intent(LibraryActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;
            case R.id.iv_bottom_settings:
              startActivity(new Intent(LibraryActivity.this,SettingsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;
        }
    }

    private class queryFile extends AsyncTask<String, String, ArrayList<Audio>> {

        @Override
        protected ArrayList<Audio> doInBackground(String... strings) {

            SharedPreferences sharedPreferences = getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                String checkFormatType = sharedPreferences.getString(Constants.K_DIRECTION_CHOOSER_PATH,
                        Environment.getExternalStorageDirectory() + File.separator + "Recorder");
                final ArrayList<File> audioSong = readAudio(new File(checkFormatType));
                for (int i = audioSong.size() - 1; i >= 0; i--) {
                    File file = audioSong.get(i);
                    String path = file.getAbsolutePath();
                    String name = file.getName();
                    long date = file.lastModified();
                    long size = file.length();
                    formatDuration = CommonUtils.getDuration(file.getPath());
                    String fomatSize = CommonUtils.formatToNumber(CommonUtils.fomatSize(size)) + " kb";

                    Audio audio = new Audio(name, path, fomatSize, CommonUtils.fomatDate(date), formatDuration);
                    audioList.add(audio);
                }
            }
            return audioList;
        }

        @Override
        protected void onPostExecute(ArrayList<Audio> list) {
            super.onPostExecute(list);
            progressDialog.setVisibility(View.GONE);
            rvLibrary.setVisibility(View.VISIBLE);
            setDataAdapter(list);
        }
    }


    private void setDataAdapter(final ArrayList<Audio> audioList) {
        layoutManager = new LinearLayoutManager(this);
        rvLibrary.setLayoutManager(layoutManager);
        if (audioList.size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvLibrary.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvLibrary.setVisibility(View.VISIBLE);
        }
        adapter = new LibraryAdapter(LibraryActivity.this, audioList);
        rvLibrary.setAdapter(adapter);

        adapter.setOnclickItem(new LibraryAdapter.OnclickItem() {
            @Override
            public void onClick(int i) {
                startActivity(new Intent(LibraryActivity.this, DetailActivity.class)
                        .putExtra("position", i)
                        .putParcelableArrayListExtra("list", audioList));
            }
        });
    }

    private void mapping() {
        ivBottomLibrary = (ImageView) findViewById(R.id.iv_bottom_library);
        ivBottomRecoder = (ImageView) findViewById(R.id.iv_bottom_recoder);
        ivBottomSettings = (ImageView) findViewById(R.id.iv_bottom_settings);

        ivBottomRecoder.setOnClickListener(this);
        ivBottomSettings.setOnClickListener(this);

        lbRecoder =  findViewById(R.id.lb_recoder);
        lbRecoder.setText(getResources().getString(R.string.label_library));
        lbRecoder.setTextColor(getResources().getColor(R.color.all_color_black));

        ivBottomLibrary.setImageDrawable(getResources().getDrawable(R.drawable.ic_library_pr));
        tvEmpty = findViewById(R.id.tv_library_empty);
        rvLibrary = findViewById(R.id.rv_library);
        progressDialog = findViewById(R.id.prb_library);
        rvLibrary.setHasFixedSize(true);
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
