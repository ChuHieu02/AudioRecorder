package com.audiorecorder.voicerecorderhd.editor.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.audiorecorder.voicerecorderhd.editor.R;
import com.audiorecorder.voicerecorderhd.editor.adapter.LibraryAdapter;
import com.audiorecorder.voicerecorderhd.editor.data.DBQuerys;
import com.audiorecorder.voicerecorderhd.editor.model.Audio;
import com.audiorecorder.voicerecorderhd.editor.utils.CommonUtils;
import com.audiorecorder.voicerecorderhd.editor.utils.Constants;

import java.io.File;
import java.util.ArrayList;



public class LibraryActivity extends AppCompatActivity {
//    @BindView(R.id.rv_library)
//    RecyclerView rvLibrary;
//    @BindView(R.id.toolbar)
//    Toolbar toolbar;
//    @BindView(R.id.tv_library_empty)
//    Toolbar tvEmpty;
//    @BindView(R.id.prb_library)
//    LinearLayout progressDialog;
    private Toolbar toolbar;
    private RecyclerView rvLibrary;
    private LibraryAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<Audio> audioList = new ArrayList<>();
    private String formatDuration = "";
    private TextView tvEmpty;
    private LinearLayout progressDialog;
    private  DBQuerys dbQuerys;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        mapping();

        new queryFile().execute();
    }

    private class queryFile extends AsyncTask<String, String, ArrayList<Audio>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<Audio> doInBackground(String... strings) {

            SharedPreferences sharedPreferences = getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                String checkFormatType = sharedPreferences.getString(Constants.K_DIRECTION_CHOOSER_PATH,
                        Environment.getExternalStorageDirectory() + File.separator + "Recorder");
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

            return audioList;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

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
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Library");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvEmpty = findViewById(R.id.tv_library_empty);
        rvLibrary = findViewById(R.id.rv_library);
        progressDialog = findViewById(R.id.prb_library);
        rvLibrary.setHasFixedSize(true);
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
