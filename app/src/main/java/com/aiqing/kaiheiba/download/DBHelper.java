package com.aiqing.kaiheiba.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    // download.db-->数据库名
    public DBHelper(Context context) {
//        super(context, "download.db", null, 1);
        this(context, "download.db");
    }

    public DBHelper(Context context, String dbName) {
        super(context, dbName, null, 1);
    }

    /**
     * 删除数据库
     *
     * @param context
     * @param dbName
     * @return
     */
    public boolean deleteDatabase(Context context, String dbName) {
        return context.deleteDatabase(dbName);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table download_info(thread_id integer, "
                + "startposition integer,endposition integer,url char," +
                "primary key(thread_id,url))");
        db.execSQL("create table download_group(name char, url char primary key,length integer,progress integer," +
                "avatar char,download_name char,file_path char)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS download_info");
        db.execSQL("DROP TABLE IF EXISTS download_group");
        onCreate(db);
    }

}
