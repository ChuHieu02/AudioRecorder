package com.audiorecorder.voicerecorderhd.editor.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.audiorecorder.voicerecorderhd.editor.R;
import com.audiorecorder.voicerecorderhd.editor.model.Audio;

public class FragmentDetailInformation extends Fragment {
    private TextView tv_name_audio, tv_path_audio, tv_size_audio, tv_time_audio, tv_duration_audio;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_information, container, false);

        Bundle arguments = getArguments();

        Audio audio = arguments.getParcelable("audio");

        tv_name_audio = view.findViewById(R.id.tv_name_audio);
        tv_duration_audio = view.findViewById(R.id.tv_duration_audio);
        tv_path_audio = view.findViewById(R.id.tv_path_audio);
        tv_size_audio = view.findViewById(R.id.tv_size_audio);
        tv_time_audio = view.findViewById(R.id.tv_time_audio);

        tv_name_audio.setText(audio.getName());
        tv_duration_audio.setText(audio.getDuration());
        tv_path_audio.setText(audio.getPath());
        tv_size_audio.setText(audio.getSize());
        tv_time_audio.setText(audio.getDate());

        return view;
    }





    public FragmentDetailInformation setArguments(Audio audio) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("audio", audio);
        setArguments(bundle);
        return this;
    }

    public void updateFragInfor(Audio audio) {
        tv_name_audio.setText(audio.getName());
        tv_duration_audio.setText(audio.getDuration());
        tv_path_audio.setText(audio.getPath());
        tv_size_audio.setText(audio.getSize());
        tv_time_audio.setText(audio.getDate());
    }


}
