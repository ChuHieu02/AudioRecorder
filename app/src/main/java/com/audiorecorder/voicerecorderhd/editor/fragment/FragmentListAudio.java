package com.audiorecorder.voicerecorderhd.editor.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.audiorecorder.voicerecorderhd.editor.R;
import com.audiorecorder.voicerecorderhd.editor.adapter.FragmenListAdapter;
import com.audiorecorder.voicerecorderhd.editor.model.Audio;
import com.audiorecorder.voicerecorderhd.editor.utils.MyDividerItemDecoration;

import java.util.ArrayList;

public class FragmentListAudio extends Fragment {
    private FragmentDetailListListener listener;
    private RecyclerView rvFragmentListLibrary;
    private FragmenListAdapter detaiListAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList audioList = new ArrayList<>();
    private int i;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_list_audio, container, false);

        rvFragmentListLibrary = view.findViewById(R.id.rv_fragment_list_library);

        Bundle arguments = getArguments();
        try {
            audioList = arguments.getParcelableArrayList("listAudio");
            this.i = arguments.getInt("position");
        } catch (Exception e) { e.printStackTrace(); }
        mapping();


        detaiListAdapter.setOnClickItemFragmentDetaiAdapter(new FragmenListAdapter.onClickItemFragmentDetaiAdapter() {
            @Override
            public void onClick(int i) {
                listener.sendPosition(i);

            }
        });

        return view;
    }

    private void mapping() {
        layoutManager = new LinearLayoutManager(getContext());
        rvFragmentListLibrary.setLayoutManager(layoutManager);
        detaiListAdapter = new FragmenListAdapter(getContext(), audioList);
        rvFragmentListLibrary.addItemDecoration(new MyDividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL, 8));
        rvFragmentListLibrary.setAdapter(detaiListAdapter);
    }

    public FragmentListAudio setArguments(ArrayList<Audio> listAudio, int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("listAudio", listAudio);
        bundle.putInt("position", position);
        setArguments(bundle);
        return this;
    }



    public interface FragmentDetailListListener {
        void sendPosition(int i);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentDetailListListener) {
            listener = (FragmentDetailListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentDetailListListener");
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
