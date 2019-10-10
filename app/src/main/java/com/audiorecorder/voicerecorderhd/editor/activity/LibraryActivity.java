package com.audiorecorder.voicerecorderhd.editor.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.audiorecorder.voicerecorderhd.editor.R;
import com.audiorecorder.voicerecorderhd.editor.adapter.LibraryAdapter;
import com.audiorecorder.voicerecorderhd.editor.model.Audio;
import com.audiorecorder.voicerecorderhd.editor.utils.CommonUtils;
import com.audiorecorder.voicerecorderhd.editor.utils.Constants;

import java.io.File;
import java.util.ArrayList;

public class LibraryActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView rvLibrary;
    private LibraryAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<Audio> audioList = new ArrayList<>();
    private static final int PERMISSION_REQUEST_CODE = 1000;
    private String formatDuration = "";
    private TextView tv_library_empty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        mappingToolbar();

        mapping();
        SharedPreferences sharedPreferences = this.getSharedPreferences(Constants.AUDIO_SETTING, Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            String checkFormatType = sharedPreferences.getString(Constants.DIRECTION_CHOOSER_PATH, Environment.getExternalStorageDirectory() + File.separator + "Recorder");
            final ArrayList<File> audioSong = readAudio(new File(checkFormatType));
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            for (int i = audioSong.size() - 1; i >= 0; i--) {
                File file = audioSong.get(i);
                String path = file.getAbsolutePath();
                String name = file.getName();
                long date = file.lastModified();
                long size = file.length();
                formatDuration = CommonUtils.GetDuration(file.getPath());
                String fomatSize = CommonUtils.formatToNumber(CommonUtils.fomatSize(size)) + " kb";

                Audio audio = new Audio(name, path, CommonUtils.fomatDate(date), formatDuration, fomatSize);
                audioList.add(audio);
            }
            metaRetriever.release();
        }

        setDataAdapter();
        adapter.setOnclickItem(new LibraryAdapter.OnclickItem() {
            @Override
            public void onClick(int i) {
                startActivity(new Intent(LibraryActivity.this, DetailAudioActivity.class)
                        .putExtra("position", i)
                        .putParcelableArrayListExtra("list", audioList));
            }
        });

    }

    private void setDataAdapter() {
        layoutManager = new LinearLayoutManager(this);
        rvLibrary.setLayoutManager(layoutManager);
        adapter = new LibraryAdapter(this, audioList);
        if (audioList.size() == 0) {
            tv_library_empty.setVisibility(View.VISIBLE);
            rvLibrary.setVisibility(View.GONE);
        } else {
            tv_library_empty.setVisibility(View.GONE);
            rvLibrary.setVisibility(View.VISIBLE);

        }
        rvLibrary.setAdapter(adapter);
    }


    private void mapping() {
        tv_library_empty = findViewById(R.id.tv_library_empty);
        rvLibrary = (RecyclerView) findViewById(R.id.rv_library);
        rvLibrary.setHasFixedSize(true);
    }

    private void mappingToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Library");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
