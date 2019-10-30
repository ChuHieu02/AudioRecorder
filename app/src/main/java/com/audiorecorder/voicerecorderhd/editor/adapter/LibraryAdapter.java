package com.audiorecorder.voicerecorderhd.editor.adapter;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.audiorecorder.voicerecorderhd.editor.R;
import com.audiorecorder.voicerecorderhd.editor.activity.EditActivity;
import com.audiorecorder.voicerecorderhd.editor.data.DBQuerys;
import com.audiorecorder.voicerecorderhd.editor.interfaces.LongClickItemLibrary;
import com.audiorecorder.voicerecorderhd.editor.interfaces.OnClickDeleteAudio;
import com.audiorecorder.voicerecorderhd.editor.interfaces.OnclickItemLibrary;
import com.audiorecorder.voicerecorderhd.editor.model.Audio;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> implements Filterable {
    private Context context;
    private ArrayList<Audio> audioList;
    private ArrayList<Audio> audioListFillter;
    private AlertDialog dialog;
    private OnclickItemLibrary onclickItem;
    private LongClickItemLibrary longClickItemLibrary;
    private boolean isMp3;
    private List<String> selectedIds = new ArrayList<>();
    private DBQuerys dbQuerys;
    private OnClickDeleteAudio onClickDeleteAudio;

    public void setOnClickDeleteAudio(OnClickDeleteAudio onClickDeleteAudio) {
        this.onClickDeleteAudio = onClickDeleteAudio;
    }

    public void setOnclickItem(OnclickItemLibrary onclickItem) {
        this.onclickItem = onclickItem;
    }

    public void setLongClickItemLibrary(LongClickItemLibrary longClickItemLibrary) {
        this.longClickItemLibrary = longClickItemLibrary;
    }

    public LibraryAdapter(Context context, ArrayList<Audio> audioList) {
        this.context = context;
        this.audioList = audioList;
        this.audioListFillter = audioList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_library, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Audio audio = audioListFillter.get(position);


        if (selectedIds.contains(audio.getPath())) {
            holder.imgItemMusicLibrary.setImageResource(R.drawable.ic_check_circle_black_24dp);
            holder.itemView.setBackground(new ColorDrawable(ContextCompat.getColor(context, R.color.type_bkgnd_unsupported)));
            holder.iv_setting.setVisibility(View.GONE);

        } else {
            holder.imgItemMusicLibrary.setImageResource(R.drawable.ic_music_note_black_24dp);
            holder.iv_setting.setVisibility(View.VISIBLE);
            holder.itemView.setBackground(new ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)));

        }

        if (audio.getName() != null) {
            holder.tv_name.setText(audio.getName());
        }
        if (audio.getDate() != null) {
            holder.tv_time.setText(audio.getDate());
        }
        if (audio.getSize() != null && audio.getDuration() != null) {
            holder.tv_size.setText(audio.getDuration() + " | " + audio.getSize());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickItem.onClick(position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickItemLibrary.longClick(position);

                return false;
            }


        });


        holder.iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(context, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.popup_menu, popup.getMenu());
                try {
                    Field[] fields = popup.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popup);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.pp_delete_item_library:
                                onClickDeleteAudio.onclickDelete(audio.getPath(),position);
                                break;
                            case R.id.pp_share_item_library:
                                shareAudio(audio);
                                break;
                            case R.id.pp_edit_item_library:
                                renameAudio(audio, position);
                                break;
                            case R.id.pp_editContent_item_library:
                                editContentAudio(audio);
                                break;
                            case R.id.pp_setRingTone_item_library:
                                setRingtone(audio);
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

    }

    public Audio getItem(int position) {
        return audioListFillter.get(position);
    }

    public void setSelectedIds(List<String> selectedIds) {
        this.selectedIds = selectedIds;
        notifyDataSetChanged();
    }

    private void editContentAudio(Audio audio) {
        try {
            Intent intentEditContent = new Intent(context, EditActivity.class);
            intentEditContent.putExtra("fileAudioName", audio.getPath());

            context.startActivity(intentEditContent);
            Log.e("Ringdroid", audio.getPath());
        } catch (Exception e) {
            Log.e("Ringdroid", "Couldn't start editor");
            e.printStackTrace();
        }
    }

    private void shareAudio(Audio audio) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.setType("audio/*");
        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(audio.getPath())));
        context.startActivity(Intent.createChooser(intent, ""));
    }

    private void renameAudio(final Audio audio, final int position) {
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(context);


        final View viewDialog = LayoutInflater.from(context).inflate(R.layout.dialog_rename_library, null);
        builder2.setView(viewDialog);

        final EditText ed_name_item_library;
        TextView bt_yes, bt_no;

        ed_name_item_library = viewDialog.findViewById(R.id.ed_name_item_library);
        bt_no = viewDialog.findViewById(R.id.bt_no);
        bt_yes = viewDialog.findViewById(R.id.bt_yes);

        ed_name_item_library.setText(audio.getName().substring(0, audio.getName().lastIndexOf(".")));
//        ed_name_item_library.setText(audio.getName());
        ed_name_item_library.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    checkRename(ed_name_item_library, audio, position);
                    return true;
                }
                return false;
            }
        });
        bt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRename(ed_name_item_library, audio, position);
            }
        });
        ed_name_item_library.requestFocus();
        dialog = builder2.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();

    }

    private void setRingtone(Audio audio) {
        boolean retVal;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(context);
            if (retVal) {
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
                context.getContentResolver().delete(uri_ringtone, MediaStore.MediaColumns.DATA + "=\"" + audio.getPath() + "\"", null);

                Uri newUri = context.getContentResolver().insert(uri_ringtone, values);

                RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
                showToast("Set ringtone success !");
            } else {
                Intent intentPermission = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intentPermission.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intentPermission);
            }
        }
    }

    @Override
    public int getItemCount() {
        return audioListFillter.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    audioListFillter = audioList;
                } else {
                    ArrayList<Audio> filteredList = new ArrayList<>();
                    for (Audio row : audioList) {

                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    audioListFillter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = audioListFillter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                audioListFillter = (ArrayList<Audio>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private TextView tv_time;
        private TextView tv_size;
        private FrameLayout iv_setting;
        private ImageView imgItemMusicLibrary;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_size = itemView.findViewById(R.id.tv_size);
            tv_time = itemView.findViewById(R.id.tv_time);
            iv_setting = itemView.findViewById(R.id.iv_setting);
            imgItemMusicLibrary = (ImageView) itemView.findViewById(R.id.img_item_music_library);

        }
    }

    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void updateList(ArrayList<Audio> list) {
        audioList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearList(){
        audioList.clear();
        notifyDataSetChanged();
    }

    private void checkRename(final EditText ed_name_item_library, final Audio audio, final int position) {
        isMp3 = audio.getName().endsWith(".mp3");
        if (isMp3) {
            File file = new File(audio.getPath());
            File file2 = new File(file.getParent() + File.separator + ed_name_item_library.getText().toString() + ".mp3");
            if (file2.exists()) {
                showToast("Audio name exist");
            } else {
                if (ed_name_item_library.getText().toString().length() != 0) {

                    boolean success = file.renameTo(file2);
                    if (success) {
                        dbQuerys = new DBQuerys(context);
                        audio.setPath(file.getParent() + File.separator + ed_name_item_library.getText().toString() + ".mp3");
                        audio.setName(ed_name_item_library.getText().toString() + ".mp3");
                        dbQuerys.Update(String.valueOf(audio.getId()), ed_name_item_library.getText().toString() + ".mp3", file.getParent() + File.separator + ed_name_item_library.getText().toString() + ".mp3");
                        notifyItemChanged(position);

                        dialog.dismiss();
                        showToast("Success");
                    } else {
                        dialog.dismiss();
                        showToast("Fail");
                    }
                } else {
                    showToast("Enter name");
                }
            }

        } else {

            File file = new File(audio.getPath());
            File file2 = new File(file.getParent() + File.separator + ed_name_item_library.getText().toString() + ".wav");
            if (file2.exists()) {
                showToast("The name exist");
            } else {
                if (ed_name_item_library.getText().toString().length() != 0) {

                    boolean success = file.renameTo(file2);
                    if (success) {
                        dbQuerys = new DBQuerys(context);
                        audio.setPath(file.getParent() + File.separator + ed_name_item_library.getText().toString() + ".wav");
                        audio.setName(ed_name_item_library.getText().toString() + ".wav");
                        dbQuerys.Update(String.valueOf(audio.getId()), ed_name_item_library.getText().toString() + ".wav", file.getParent() + File.separator + ed_name_item_library.getText().toString() + ".wav");

                        notifyItemChanged(position);
                        dialog.dismiss();
                        showToast("Success");
                    } else {
                        dialog.dismiss();
                        showToast("Fail");
                    }
                } else {
                    showToast("Enter name");

                }
            }
        }
        InputMethodManager inputMethodManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(ed_name_item_library.getWindowToken(), 0);


    }


}
