package com.audiorecorder.voicerecorderhd.editor.fragment.listRecording;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.audiorecorder.voicerecorderhd.editor.R;
import com.audiorecorder.voicerecorderhd.editor.activity.DetailAudioActivity;
import com.audiorecorder.voicerecorderhd.editor.adapter.ListAudioAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListRecordingFragment extends Fragment {
    private RecyclerView rvListAudio;

    private File file;
    private String[] itemAudio;
    private ArrayList songList;

    private List<File> audioList = new ArrayList<>();
    private ListAudioAdapter listAudioAdapter;
    private LinearLayoutManager layoutManager;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_recording, container, false);

        MediaPlayer mediaPlayer = new MediaPlayer();
        createFile();


        rvListAudio = view.findViewById(R.id.rv_list_audio);

        layoutManager = new LinearLayoutManager(getContext());
        rvListAudio.setLayoutManager(layoutManager);

        final ArrayList<File> audioSong = readAudio(Environment.getExternalStorageDirectory());

        listAudioAdapter = new ListAudioAdapter(getContext(),audioSong);

        rvListAudio.setAdapter(listAudioAdapter);

        return view;
    }

    private void createFile() {
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Recorder");
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public ArrayList<File> readAudio(File file) {
        ArrayList<File> arrayList = new ArrayList<>();


        File[] files = file.listFiles();

        if (files==null){
            return arrayList;
        }
        for (File invidualFile : files) {
            if (invidualFile.isDirectory() && !invidualFile.isHidden()) {
                arrayList.addAll(readAudio(invidualFile));

            } else {
                if (invidualFile.getName().endsWith(".mp3")) {
                    arrayList.add(invidualFile);
                }
            }
        }
        return arrayList;
    }
}