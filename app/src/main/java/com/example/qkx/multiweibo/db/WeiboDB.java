package com.example.qkx.multiweibo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.qkx.multiweibo.model.User;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by qkx on 15/11/22.
 */
public class WeiboDB {
    public static final String DB_NAME = "weibo_db";
    public static final int VERSION = 1;

    private static WeiboDB weiboDB;

    private SQLiteDatabase db;

    public WeiboDB(Context context) {
        WeiboOpenHelper helper = new WeiboOpenHelper(context,DB_NAME,null,VERSION);
        db = helper.getWritableDatabase();
    }

    public synchronized static WeiboDB getInstance(Context context){
        if (weiboDB == null)
            weiboDB = new WeiboDB(context);
        return weiboDB;
    }

    public void saveUser (User user) {
        if (user != null) {
            ContentValues values = new ContentValues();
            values.put("access_token",user.access_token);
            values.put("uid",user.uid);
            db.insert("user",null,values);
        }
    }

    public List<User> loadUsers() {
        List<User> list = new ArrayList<>();
        Cursor cursor = db.query("user",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                String uid = cursor.getString(cursor.getColumnIndex("uid"));
                user.uid = uid;
                String access_token = cursor.getString(cursor.getColumnIndex("access_token"));
                user.access_token = access_token;
                list.add(user);
            } while (cursor.moveToNext());
        }
        return list;
    }

}
