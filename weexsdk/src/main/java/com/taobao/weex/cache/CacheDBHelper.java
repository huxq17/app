package com.taobao.weex.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CacheDBHelper extends SQLiteOpenHelper {
    public static final String JS_CACHE_TABLE_NAME = "js_cache_table";

    public CacheDBHelper(Context context) {
//        super(context, "download.db", null, 1);
        this(context, "weex_cache.db");
    }

    public CacheDBHelper(Context context, String dbName) {
        super(context, dbName, null, 2);
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
        db.execSQL("create table " + JS_CACHE_TABLE_NAME + "(url char primary key,md5 char,ims char,content char)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + JS_CACHE_TABLE_NAME);
        onCreate(db);
    }

}
