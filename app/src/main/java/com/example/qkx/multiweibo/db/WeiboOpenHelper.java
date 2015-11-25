package com.example.qkx.multiweibo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by qkx on 15/11/22.
 */
public class WeiboOpenHelper extends SQLiteOpenHelper{
    public static final String USER_CREAT = "create table user(id integer primary key autoincrement,uid text,access_token text)";

    public WeiboOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_CREAT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
