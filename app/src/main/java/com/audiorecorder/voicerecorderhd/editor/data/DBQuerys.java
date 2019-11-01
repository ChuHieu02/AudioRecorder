package com.audiorecorder.voicerecorderhd.editor.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.audiorecorder.voicerecorderhd.editor.model.Audio;
import com.audiorecorder.voicerecorderhd.editor.utils.CommonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DBQuerys {

    private static SQLiteDatabase db;
    private DBManager databasehelper;

    public static final String TABLE_NAME = "AudioRecoder";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PATH = "path";
    private static final String SIZE = "size";
    private static final String DATE = "date";
    private static final String DURATION = "duration";

    public static final String sqlQuerys = "CREATE TABLE " + TABLE_NAME + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME + " TEXT, " +
            PATH + " TEXT, " +
            SIZE + " LONG, " +
            DATE + " LONG, " +
            DURATION + " LONG " +
            ")";

    public DBQuerys(Context context) {
        databasehelper = new DBManager(context);
        db = databasehelper.getWritableDatabase();
    }


    public void insertAudioString(String name, String path, long size, long date, long duration) {
        ContentValues values = new ContentValues();
        values.put(DBQuerys.NAME, name);
        values.put(DBQuerys.PATH, path);
        values.put(DBQuerys.SIZE, size);
        values.put(DBQuerys.DATE, date);
        values.put(DBQuerys.DURATION, duration);

        db.insert(DBQuerys.TABLE_NAME, null, values);
        db.close();

    }

    public ArrayList<Audio> getList() {
        ArrayList<Audio> audioList = new ArrayList<>();
        String selectQuery = " SELECT  * FROM " + DBQuerys.TABLE_NAME + " ORDER BY id DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (db != null && cursor.moveToFirst()) {
            do {
                Audio audio = new Audio();
//
                audio.setId(cursor.getInt(0));
                audio.setName(cursor.getString(1));
                audio.setPath(cursor.getString(2));
                audio.setSize(CommonUtils.formatSize(cursor.getLong(3)));
                audio.setDate(CommonUtils.fomatDate(cursor.getLong(4)));
                audio.setDuration(CommonUtils.formatTime(cursor.getLong(5)));

                File file = new File(cursor.getString(2));
                if (file.exists()) {
                    audioList.add(audio);
                }else {
                    int id = audio.getId();
                    db.delete(DBQuerys.TABLE_NAME,DBQuerys.ID +" =? " ,new String[]{String.valueOf(id)} );
                }

            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return audioList;
    }

    public boolean Update(final String Id, String name, String path) {

        ContentValues values = new ContentValues();
        values.put(ID, Id);
        values.put(NAME, name);
        values.put(PATH, path);

        db.update(DBQuerys.TABLE_NAME, values, DBQuerys.ID +" =? ", new String[]{Id});
        return true;
    }


    public  void updateNameRecordFile(String newName,String oldName){

        String selectByName = "SELECT * FROM "+DBQuerys.TABLE_NAME+" WHERE "+DBQuerys.NAME+" = '"+oldName+"'" ;

        Cursor cursor = db.rawQuery(selectByName, null);
        int id = 0;

        if (db != null && cursor.moveToFirst()) {
             id = cursor.getInt(0);

        }
          cursor.close();
        ContentValues values = new ContentValues();
        values.put(ID, id);
        values.put(NAME, newName);


        db.update(DBQuerys.TABLE_NAME, values, DBQuerys.ID +" =? ", new String[]{String.valueOf(id)});

    }

    public static boolean isExitsInDB(String newName){

        String selectByName = "SELECT * FROM "+DBQuerys.TABLE_NAME+" WHERE "+DBQuerys.NAME+" = '"+newName+"'" ;

        Cursor cursor = db.rawQuery(selectByName, null);
        if( cursor != null && cursor.moveToFirst()){
            cursor.close();
            return  true;
        } else {
           cursor.close();
            return false;
        }


    }


}
