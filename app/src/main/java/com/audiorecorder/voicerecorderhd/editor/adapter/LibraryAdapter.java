package com.audiorecorder.voicerecorderhd.editor.adapter;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.audiorecorder.voicerecorderhd.editor.R;
import com.audiorecorder.voicerecorderhd.editor.activity.DetailAudioActivity;
import com.audiorecorder.voicerecorderhd.editor.model.Audio;

import java.io.File;
import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {
    private Context context;
    private List<Audio> audioList;
    private AlertDialog dialog;

    private boolean isMp3;

    public LibraryAdapter(Context context, List<Audio> audioList) {
        this.context = context;
        this.audioList = audioList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_library, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Audio audio = audioList.get(position);
        holder.tv_name_item_audio.setText(audio.getName());
        holder.tv_time_item_audio.setText(audio.getDate());
        holder.tv_size_item_audio.setText(audio.getSize() + " | " + audio.getDuration());



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailAudioActivity.class);
                intent.putExtra("path", audio.getPath());
                intent.putExtra("name", audio.getName());
                intent.putExtra("size", String.valueOf(audio.getSize()));
                intent.putExtra("date", audio.getDate());
                intent.putExtra("position", position);
                intent.putExtra("duration", audio.getDuration());

                context.startActivity(intent);
            }
        });
        holder.iv_setting_item_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "" + audio.getDuration(), Toast.LENGTH_SHORT).show();

                PopupMenu popup = new PopupMenu(context, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.pp_delete_item_library:
                                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Are you sure to Delete ?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                boolean checkDel = new File(audio.getPath()).delete();
                                                if (checkDel) {
                                                    audioList.remove(position);
                                                    notifyDataSetChanged();
                                                    Toast.makeText(context, "Delete success", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(context, "Delete fail", Toast.LENGTH_SHORT).show();

                                                }
                                                notifyDataSetChanged();
                                            }
                                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                builder.create().show();
                                break;
                            case R.id.pp_share_item_library:

                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.SEND");
                                intent.setType("audio/*");
                                intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(audio.getPath())));
                                context.startActivity(Intent.createChooser(intent, ""));

                                break;
                            case R.id.pp_edit_item_library:
                                final AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                                final View viewDialog = LayoutInflater.from(context).inflate(R.layout.dialog_rename_library, null);
                                builder2.setView(viewDialog);

                                final EditText ed_name_item_library;
                                Button bt_yes, bt_no;

                                ed_name_item_library = viewDialog.findViewById(R.id.ed_name_item_library);
                                bt_no = viewDialog.findViewById(R.id.bt_no);
                                bt_yes = viewDialog.findViewById(R.id.bt_yes);

                                ed_name_item_library.setText(audio.getName().substring(0, audio.getName().lastIndexOf(".")));


                                bt_no.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                bt_yes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        isMp3 = audio.getName().endsWith(".mp3");
                                        if (isMp3) {
                                            File file = new File(audio.getPath());
                                            File file2 = new File(file.getParent() + File.separator + ed_name_item_library.getText().toString() + ".mp3");
                                            boolean success = file.renameTo(file2);
                                            if (success) {
                                                file.delete();
                                                notifyDataSetChanged();
                                                dialog.dismiss();
                                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                                            } else {
                                                dialog.dismiss();
                                                Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {

                                            File file = new File(audio.getPath());
                                            File file2 = new File(file.getParent() + File.separator + ed_name_item_library.getText().toString() + ".wav");
                                            boolean success = file.renameTo(file2);
                                            if (success) {
                                                file.delete();
                                                notifyDataSetChanged();
                                                dialog.dismiss();
                                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                                            } else {
                                                dialog.dismiss();
                                                Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                                builder2.create();
                                dialog = builder2.show();

                                break;
                            case R.id.pp_editContent_item_library:
                                break;
                            case R.id.pp_setRingTone_item_library:

                                ContentValues values = new ContentValues();
                                values.put(MediaStore.MediaColumns.DATA, audio.getPath());
                                values.put(MediaStore.MediaColumns.TITLE, audio.getName());
                                values.put(MediaStore.MediaColumns.SIZE, audio.getDuration());
                                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
                                values.put(MediaStore.Audio.AudioColumns.ARTIST, context.getString(R.string.app_name));
                                values.put(MediaStore.Audio.AudioColumns.IS_RINGTONE, true);
                                values.put(MediaStore.Audio.AudioColumns.IS_NOTIFICATION, false);
                                values.put(MediaStore.Audio.AudioColumns.IS_ALARM, false);
                                values.put(MediaStore.Audio.AudioColumns.IS_MUSIC, false);

                                Uri uri_ringtone = MediaStore.Audio.Media.getContentUriForPath(audio.getPath());
                                context.getContentResolver().

                                        delete(uri_ringtone, MediaStore.MediaColumns.DATA + "=\"" + audio.getPath() + "\"", null);

                                Uri newUri = context.getContentResolver().insert(uri_ringtone, values);

                                RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);

                                break;
                        }
                        return false;
                    }
                });
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
        private FrameLayout iv_setting_item_audio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name_item_audio = itemView.findViewById(R.id.tv_name_item_audio);
            tv_size_item_audio = itemView.findViewById(R.id.tv_size_item_audio);
            tv_time_item_audio = itemView.findViewById(R.id.tv_time_item_audio);
            iv_setting_item_audio = itemView.findViewById(R.id.iv_setting_item_audio);
        }
    }

}

