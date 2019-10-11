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

public class FragmentInforDetail extends Fragment {
    private TextView tv_name, tv_path, tv_size, tv_time, tv_duration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_information, container, false);

        Bundle arguments = getArguments();

        Audio audio = arguments.getParcelable("audio");

        tv_name = view.findViewById(R.id.tv_name);
        tv_duration = view.findViewById(R.id.tv_duration);
        tv_path = view.findViewById(R.id.tv_path);
        tv_size = view.findViewById(R.id.tv_size);
        tv_time = view.findViewById(R.id.tv_time);

        tv_name.setText(audio.getName());
        tv_duration.setText(audio.getDuration());
        tv_path.setText(audio.getPath());
        tv_size.setText(audio.getSize());
        tv_time.setText(audio.getDate());

        return view;
    }





    public FragmentInforDetail setArguments(Audio audio) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("audio", audio);
        setArguments(bundle);
        return this;
    }

    public void updateFragInfor(Audio audio) {
        tv_name.setText(audio.getName());
        tv_duration.setText(audio.getDuration());
        tv_path.setText(audio.getPath());
        tv_size.setText(audio.getSize());
        tv_time.setText(audio.getDate());
    }


}
