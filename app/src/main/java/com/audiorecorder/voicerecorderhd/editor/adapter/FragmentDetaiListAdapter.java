package com.audiorecorder.voicerecorderhd.editor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.audiorecorder.voicerecorderhd.editor.R;
import com.audiorecorder.voicerecorderhd.editor.model.Audio;

import java.util.List;

public class FragmentDetaiListAdapter extends RecyclerView.Adapter<FragmentDetaiListAdapter.ViewHolder> {
    private List<Audio> audioList;
    private Context context;
    private onClickItemFragmentDetaiAdapter onClickItemFragmentDetaiAdapter;

    public interface onClickItemFragmentDetaiAdapter{
        void onClick(int i);
    }

    public void setOnClickItemFragmentDetaiAdapter(FragmentDetaiListAdapter.onClickItemFragmentDetaiAdapter onClickItemFragmentDetaiAdapter) {
        this.onClickItemFragmentDetaiAdapter = onClickItemFragmentDetaiAdapter;
    }

    public FragmentDetaiListAdapter(Context context , List<Audio> audioList ) {
        this.audioList = audioList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_detail_list_fragment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (audioList.get(position).getName()!=null){
            holder.tv_name_detail_item_fragment.setText(audioList.get(position).getName());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItemFragmentDetaiAdapter.onClick(position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name_detail_item_fragment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name_detail_item_fragment = itemView.findViewById(R.id.tv_name_detail_item_fragment);
        }
    }
}
