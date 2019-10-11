package com.audiorecorder.voicerecorderhd.editor.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.audiorecorder.voicerecorderhd.editor.model.Audio;

class DBQuerys {

    public static SQLiteDatabase db;
    public DBManager databasehelper;


    public static final String TABLE_NAME = "Audio Recoder";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PATH = "path";
    public static final String SIZE = "size";
    public static final String DATE = "date";
    public static final String DURATION = "duration";
    public static final String IS_CHECK = "isCheck";


    public static final String sqlQuerys = "CREATE TABLE " + TABLE_NAME + " (" +
            ID + " integer primary key AUTOINCREMENT, " +
            NAME + " TEXT, " +
            PATH + " TEXT, " +
            SIZE + " LONG, " +
            DATE + " LONG, " +
            DURATION + " LONG, " +
            IS_CHECK + " BOOLEAN)";

    public DBQuerys(Context context) {
        databasehelper = new DBManager(context);
        db = databasehelper.getWritableDatabase();
    }

    public void insertAudio(Audio audio) {
        ContentValues values = new ContentValues();
        values.put(DBQuerys.NAME, audio.getName());
        values.put(DBQuerys.PATH, audio.getPath());
        values.put(DBQuerys.SIZE, audio.getSize());
        values.put(DBQuerys.DATE, audio.getDate());
        values.put(DBQuerys.DURATION, audio.getDuration());
        values.put(DBQuerys.IS_CHECK, audio.isClick());
        db.insert(DBQuerys.TABLE_NAME, null, values);
    }
}
