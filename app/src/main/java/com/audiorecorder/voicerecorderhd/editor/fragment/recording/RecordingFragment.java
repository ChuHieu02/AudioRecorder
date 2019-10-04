package com.audiorecorder.voicerecorderhd.editor.fragment.recording;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.audiorecorder.voicerecorderhd.editor.R;
import com.squareup.picasso.Picasso;


public class RecordingFragment extends Fragment {
//    private ImageView iv_bg_home;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recording, container, false);



        return view;
    }


}