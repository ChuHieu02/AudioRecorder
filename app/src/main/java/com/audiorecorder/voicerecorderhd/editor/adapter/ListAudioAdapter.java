package com.audiorecorder.voicerecorderhd.editor.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.audiorecorder.voicerecorderhd.editor.R;
import com.audiorecorder.voicerecorderhd.editor.activity.DetailAudioActivity;

import java.io.File;
import java.util.Date;
import java.util.List;

public class ListAudioAdapter extends RecyclerView.Adapter<ListAudioAdapter.ViewHolder> {
    private Context context;
    private List<File> audioList;

    public ListAudioAdapter(Context context, List<File> audioList) {
        this.context = context;
        this.audioList = audioList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_audio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final File file = audioList.get(position);
        final long size = file.length() / 1024;
        Date lastModDate = new Date(file.lastModified());

        holder.tv_name_item_audio.setText(file.getName());
        holder.tv_size_item_audio.setText(String.valueOf(size) + " kb");
        holder.tv_time_item_audio.setText(String.valueOf(file.lastModified()));
        holder.view_item_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailAudioActivity.class);
                intent.putExtra("path", file.getPath());
                intent.putExtra("name", file.getName());
                intent.putExtra("size", String.valueOf(size));
                context.startActivity(intent);
            }
        });
        holder.iv_setting_item_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.popup_menu, popup.getMenu());
                popup.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name_item_audio;
        private TextView tv_time_item_audio;
        private TextView tv_size_item_audio;
        private ConstraintLayout view_item_audio;
        private ImageView iv_setting_item_audio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name_item_audio = itemView.findViewById(R.id.tv_name_item_audio);
            tv_size_item_audio = itemView.findViewById(R.id.tv_size_item_audio);
            tv_time_item_audio = itemView.findViewById(R.id.tv_time_item_audio);
            view_item_audio = itemView.findViewById(R.id.view_item_audio);
            iv_setting_item_audio = itemView.findViewById(R.id.iv_setting_item_audio);
        }
    }
}
