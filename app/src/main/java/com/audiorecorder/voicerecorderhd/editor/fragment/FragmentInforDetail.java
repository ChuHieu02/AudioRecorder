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
    private TextView tvName, tvPath, tvSize, tvTime, tvDuration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_information, container, false);

        Bundle arguments = getArguments();

        Audio audio = arguments.getParcelable("audio");

        tvName = view.findViewById(R.id.tv_name);
        tvDuration = view.findViewById(R.id.tv_duration);
        tvPath = view.findViewById(R.id.tv_path);
        tvSize = view.findViewById(R.id.tv_size);
        tvTime = view.findViewById(R.id.tv_time);

        tvName.setText(audio.getName());
        tvDuration.setText(audio.getDuration());
        tvPath.setText(audio.getPath());
        tvSize.setText(audio.getSize());
        tvTime.setText(audio.getDate());

        return view;
    }


    public FragmentInforDetail setArguments(Audio audio) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("audio", audio);
        setArguments(bundle);
        return this;
    }

    public void updateFragInfor(Audio audio) {
        tvName.setText(audio.getName());
        tvDuration.setText(audio.getDuration());
        tvPath.setText(audio.getPath());
        tvSize.setText(audio.getSize());
        tvTime.setText(audio.getDate());
    }


}
