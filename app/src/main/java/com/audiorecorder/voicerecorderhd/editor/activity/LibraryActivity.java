package com.audiorecorder.voicerecorderhd.editor.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.audiorecorder.voicerecorderhd.editor.R;
import com.audiorecorder.voicerecorderhd.editor.adapter.LibraryAdapter;
import com.audiorecorder.voicerecorderhd.editor.model.Audio;
import com.audiorecorder.voicerecorderhd.editor.utils.CommonUtils;

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


    private boolean openAndroidPermissionsWriteSetting() {
        boolean retVal = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(this);
            if (retVal) {
//                Toast.makeText(this, "Write allowed :-)", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                startActivity(intent);
//                Toast.makeText(this, "Write not allowed :-(", Toast.LENGTH_LONG).show();
            }
        }
        return retVal;
    }

    private void openAndroidPermissionsWriteStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {

            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        mappingToolbar();

        mapping();

        openAndroidPermissionsWriteSetting();

        openAndroidPermissionsWriteStorage();


        final ArrayList<File> audioSong = readAudio(new File(Environment.getExternalStorageDirectory() + File.separator + "Recorder"));
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        for (int i = 0; i < audioSong.size(); i++) {
            File file = audioSong.get(i);
            String path = file.getAbsolutePath();
            String name = file.getName();
            long date = file.lastModified();
            long size = file.length();
            formatDuration = CommonUtils.GetDuration(file.getPath());
            String fomatSize = CommonUtils.formatToNumber(CommonUtils.fomatSize(size)) + " kb";

            Audio audio = new Audio(name, path, CommonUtils.fomatDate(date), formatDuration, fomatSize);
            audioList.add(audio);
            metaRetriever.release();

        }

        setDataAdapter();
        adapter.setOnclickItem(new LibraryAdapter.OnclickItem() {
            @Override
            public void onClick(int i) {
                startActivity(new Intent(LibraryActivity.this, DetailAudioActivity.class).putExtra("position", i).putParcelableArrayListExtra("list", audioList));
            }
        });
        adapter.setOnclickItemRefesh(new LibraryAdapter.OnclickItemRefesh() {
            @Override
            public void onClick(int i) {

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

    private void getAudio() {
        ContentResolver cr = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cur = cr.query(uri, null, null, null, null);
        int count = 0;
        if (cur != null) {
            count = cur.getCount();
            if (count > 0) {
                while (cur.moveToNext()) {
                    String data = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String duration = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    String name = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String date = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
                    String size = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.SIZE));

                    String fomartDate = CommonUtils.fomatDate(Long.parseLong(date) * 1000);
                    String formatTime = CommonUtils.formatTime(Long.parseLong(duration));
                    int formatSize = Integer.parseInt(size) / 1024;

                    if (data.endsWith(".mp3") || data.endsWith(".wav")) {
//                       Audio audioFile = new Audio(name, data, String.valueOf(fomartDate), "", formatSize);
//                       audioList.add(audioFile);
                        Log.e("date", fomartDate);
                        Log.e("size", formatSize + "");
                        Log.e("time", formatTime);
                        Log.e("path", data);
                        Log.e("name", name);
                    }
                }
            }
        }
        if (cur == null) {
            return;
        }
        cur.close();
    }
}
