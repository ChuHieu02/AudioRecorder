package com.audiorecorder.voicerecorderhd.editor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.audiorecorder.voicerecorderhd.editor.R;
import com.audiorecorder.voicerecorderhd.editor.model.Audio;

import java.util.List;

public class FragmenListAdapter extends RecyclerView.Adapter<FragmenListAdapter.ViewHolder> {
    private List<Audio> audioList;
    private Context context;
    private onClickItemFragmentDetaiAdapter onClickItemFragmentDetaiAdapter;
    private int checkedPosition = 0;

    public interface onClickItemFragmentDetaiAdapter {
        void onClick(int i);
    }
    public void setOnClickItemFragmentDetaiAdapter(FragmenListAdapter.onClickItemFragmentDetaiAdapter onClickItemFragmentDetaiAdapter) {
        this.onClickItemFragmentDetaiAdapter = onClickItemFragmentDetaiAdapter;
    }
    public FragmenListAdapter(Context context, List<Audio> audioList) {
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bind(audioList.get(position) , position);
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name_detail_item_fragment;
        private ImageView iv_check_item_detail_list;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name_detail_item_fragment = itemView.findViewById(R.id.tv_name_detail_item_fragment);
            iv_check_item_detail_list = itemView.findViewById(R.id.iv_check_item_detail_list);
        }

        void bind(final Audio audio, final int position) {
            if (audio.getName()!=null){
                tv_name_detail_item_fragment.setText(audio.getName());
            }
            if (checkedPosition == -1) {
                tv_name_detail_item_fragment.setSelected(false);
                iv_check_item_detail_list.setVisibility(View.GONE);
            } else {
                if (checkedPosition == getAdapterPosition()) {
                    tv_name_detail_item_fragment.setSelected(true);
                    iv_check_item_detail_list.setVisibility(View.VISIBLE);
                } else {
                    tv_name_detail_item_fragment.setSelected(false);
                    iv_check_item_detail_list.setVisibility(View.GONE);
                }
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickItemFragmentDetaiAdapter.onClick(position);
                    iv_check_item_detail_list.setVisibility(View.VISIBLE);
                    tv_name_detail_item_fragment.setSelected(true);
                    if (checkedPosition != getAdapterPosition()) {
                        notifyItemChanged(checkedPosition);
                        checkedPosition = getAdapterPosition();
                    }
                }
            });
        }
    }
    public Audio getSelected() {
        if (checkedPosition != -1) {
            return audioList.get(checkedPosition);
        }
        return null;
    }

}
