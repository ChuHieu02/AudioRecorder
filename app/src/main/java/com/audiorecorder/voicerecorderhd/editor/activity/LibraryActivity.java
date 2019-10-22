package com.audiorecorder.voicerecorderhd.editor.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.audiorecorder.voicerecorderhd.editor.MainActivity;
import com.audiorecorder.voicerecorderhd.editor.R;
import com.audiorecorder.voicerecorderhd.editor.adapter.LibraryAdapter;
import com.audiorecorder.voicerecorderhd.editor.adapter.RecyclerItemClickListener;
import com.audiorecorder.voicerecorderhd.editor.data.DBQuerys;
import com.audiorecorder.voicerecorderhd.editor.model.Audio;
import com.audiorecorder.voicerecorderhd.editor.utils.CommonUtils;
import com.audiorecorder.voicerecorderhd.editor.utils.Constants;

import java.io.File;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.sql.Types.TIMESTAMP;


public class LibraryActivity extends AppCompatActivity implements View.OnClickListener, ActionMode.Callback {

    private RecyclerView rvLibrary;
    private LibraryAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<Audio> audioList = new ArrayList<>();
    private String formatDuration = "";
    private TextView tvEmpty;
    private LinearLayout progressDialog;
    private static final String TAG = "library";
    private ImageView ivBottomLibrary;
    private ImageView ivBottomRecoder;
    private ImageView ivBottomSettings;
    private ActionMode actionMode;
    private boolean isMultiSelect = false;
    private List<Integer> selectedIds = new ArrayList<>();
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        mapping();

//        ActionBar bar = getActionBar();
//        bar.setBackgroundDrawable(new ColorDrawable());
        new queryFile().execute();

        rvLibrary.addOnItemTouchListener(new RecyclerItemClickListener(this, rvLibrary, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect) {
                    multiSelect(position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    selectedIds = new ArrayList<>();
                    isMultiSelect = true;

                    if (actionMode == null) {
                        actionMode = startActionMode(LibraryActivity.this); //show ActionMode.

                    } else {
                    }
                }

                multiSelect(position);
            }
        }));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_bottom_recoder:
                startActivity(new Intent(LibraryActivity.this, MainActivity.class));

                break;
            case R.id.iv_bottom_settings:
                startActivity(new Intent(LibraryActivity.this, SettingsActivity.class));

                break;
        }
    }

    private void multiSelect(int position) {
        Audio data = adapter.getItem(position);
        if (data != null) {
            if (actionMode != null) {
                if (selectedIds.contains(position))
                    selectedIds.remove(Integer.valueOf(position));
                else
                    selectedIds.add(position);
//                Log.e("size",selectedIds+""+position);

                if (selectedIds.size() > 0)
                    actionMode.setTitle(String.valueOf(selectedIds.size())); //show selected item count on action mode.
                else {
                    actionMode.setTitle("");
                    actionMode.finish(); //hide action mode.

                }
                adapter.setSelectedIds(selectedIds);

            }

        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_select, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_delete_item_library:

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.question_delete)
                        .setPositiveButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j) {
                                dialog.dismiss();
                            }
                        }).setNegativeButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        for (int i = 0; i < selectedIds.size(); i++) {
                            String path = audioList.get(selectedIds.get(i)).getPath();
                            new File(String.valueOf(Uri.parse(path))).delete();
//                    Log.e("nameTotal", audioList.get(selectedIds.get(i)).getPath() + "  ");
                        }
                        Toast.makeText(LibraryActivity.this, getResources().getString(R.string.success_delete), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create();
                dialog = builder.show();

//                for (int i = 0; i < selectedIds.size(); i++) {
//                    String path = audioList.get(selectedIds.get(i)).getPath();
//                    new File(String.valueOf(Uri.parse(path))).delete();
////                    Log.e("nameTotal", audioList.get(selectedIds.get(i)).getPath() + "  ");
//                }
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
        isMultiSelect = false;
        selectedIds = new ArrayList<>();
        adapter.setSelectedIds(new ArrayList<Integer>());
    }

    private class queryFile extends AsyncTask<String, String, ArrayList<Audio>> {

        @Override
        protected ArrayList<Audio> doInBackground(String... strings) {

            SharedPreferences sharedPreferences = getSharedPreferences(Constants.K_AUDIO_SETTING, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                String checkFormatType = sharedPreferences.getString(Constants.K_DIRECTION_CHOOSER_PATH,
                        Environment.getExternalStorageDirectory() + File.separator + "Recorder");
                final ArrayList<File> audioSong = readAudio(new File(checkFormatType));
                for (int i = audioSong.size() - 1; i >= 0; i--) {
                    File file = audioSong.get(i);
                    String path = file.getAbsolutePath();
                    String name = file.getName();
                    long date = file.lastModified();
                    long size = file.length();
                    formatDuration = CommonUtils.getDuration(file.getPath());
                    String fomatSize = CommonUtils.formatToNumber(CommonUtils.fomatSize(size)) + " kb";

                    Audio audio = new Audio(name, path, fomatSize, CommonUtils.fomatDate(date), formatDuration);
                    audioList.add(audio);
                }
            }
            return audioList;
        }

        @Override
        protected void onPostExecute(ArrayList<Audio> list) {
            super.onPostExecute(list);
            progressDialog.setVisibility(View.GONE);
            rvLibrary.setVisibility(View.VISIBLE);
            setDataAdapter(list);
        }
    }

    private void setDataAdapter(final ArrayList<Audio> audioList) {
        layoutManager = new LinearLayoutManager(this);
        rvLibrary.setLayoutManager(layoutManager);
        if (audioList.size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvLibrary.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvLibrary.setVisibility(View.VISIBLE);
        }
        adapter = new LibraryAdapter(LibraryActivity.this, audioList);
        rvLibrary.setAdapter(adapter);
        adapter.setOnclickItem(new LibraryAdapter.OnclickItem() {
            @Override
            public void onClick(int i) {
                startActivity(new Intent(LibraryActivity.this, DetailActivity.class)
                        .putExtra("position", i)
                        .putParcelableArrayListExtra("list", audioList));
            }
        });
    }

    private void mapping() {
        ivBottomLibrary = (ImageView) findViewById(R.id.iv_bottom_library);
        ivBottomRecoder = (ImageView) findViewById(R.id.iv_bottom_recoder);
        ivBottomSettings = (ImageView) findViewById(R.id.iv_bottom_settings);

        ivBottomRecoder.setOnClickListener(this);
        ivBottomSettings.setOnClickListener(this);


//        tvLabelLibrary =  findViewById(R.id.tv_label_library);
//        tvLabelLibrary.setText(getResources().getString(R.string.label_library));
//        tvLabelLibrary.setTextColor(getResources().getColor(R.color.all_color_black));

        ivBottomLibrary.setImageDrawable(getResources().getDrawable(R.drawable.ic_library_pr));
        tvEmpty = findViewById(R.id.tv_library_empty);
        rvLibrary = findViewById(R.id.rv_library);
        progressDialog = findViewById(R.id.prb_library);
        rvLibrary.setHasFixedSize(true);
    }

    public ArrayList<File> readAudio(File file) {
        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();

        if (files == null) {
            return arrayList;
        }
        for (File invidualFile : files) {
            if (invidualFile.isDirectory() && !invidualFile.isHidden()) {
                arrayList.addAll(readAudio(invidualFile));

            } else {
                if (invidualFile.getName().endsWith(".mp3") || invidualFile.getName().endsWith(".wav")) {
                    arrayList.add(invidualFile);
                }
            }
        }
        return arrayList;
    }

    private void deleteAudio(final String path) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.question_delete)
                .setPositiveButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create();
        dialog = builder.show();
    }

}
