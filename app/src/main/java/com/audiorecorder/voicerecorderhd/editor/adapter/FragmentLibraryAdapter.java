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

public class FragmentLibraryAdapter extends RecyclerView.Adapter<FragmentLibraryAdapter.ViewHolder>  {
    private List<Audio> audioList;
    private Context context;
    private onClickItemFragmentDetaiAdapter onClickItemFragmentDetaiAdapter;
    private int checkedPosition = 0;


    public interface onClickItemFragmentDetaiAdapter {
        void onClick(int i);
    }

    public void setOnClickItemFragmentDetaiAdapter(FragmentLibraryAdapter.onClickItemFragmentDetaiAdapter onClickItemFragmentDetaiAdapter) {
        this.onClickItemFragmentDetaiAdapter = onClickItemFragmentDetaiAdapter;
    }

    public FragmentLibraryAdapter(Context context, List<Audio> audioList) {
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
        holder.bind(audioList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNameDetailItemFragment;
        private ImageView ivCheckItemDetailList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameDetailItemFragment = itemView.findViewById(R.id.tv_name_detail_item_fragment);
            ivCheckItemDetailList = itemView.findViewById(R.id.iv_check_item_detail_list);
        }

        void bind(final Audio audio, final int position) {
            if (audio.getName() != null) {
                tvNameDetailItemFragment.setText(audio.getName());
            }
            if (checkedPosition == -1) {
                tvNameDetailItemFragment.setSelected(false);
                ivCheckItemDetailList.setVisibility(View.GONE);
            } else {
                if (checkedPosition == getAdapterPosition()) {
                    tvNameDetailItemFragment.setSelected(true);
                    ivCheckItemDetailList.setVisibility(View.VISIBLE);
                } else {
                    tvNameDetailItemFragment.setSelected(false);
                    ivCheckItemDetailList.setVisibility(View.GONE);
                }
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickItemFragmentDetaiAdapter.onClick(position);
                    ivCheckItemDetailList.setVisibility(View.VISIBLE);
                    tvNameDetailItemFragment.setSelected(true);
                    if (checkedPosition != getAdapterPosition()) {
                        notifyItemChanged(checkedPosition);
                        checkedPosition = getAdapterPosition();
                    }
                }
            });
        }
    }
    

}
