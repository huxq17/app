package com.taobao.weex.cache;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CacheDBService {
    private CacheDBHelper dbHelper;
    private static CacheDBService instance;
    private static final String DOWNLOAD_INFO_TABLE = "download_info";
    private static final String DOWNLOAD_GROUP_TABLE = "download_group";

    private CacheDBService(Context context) {
        dbHelper = new CacheDBHelper(context);
    }

    /**
     * @param context
     * @return
     */
    public static CacheDBService getInstance(Context context) {
        if (context == null) {
            throw new RuntimeException("context == null");
        }
        if (!(context instanceof Application)) {
            context = context.getApplicationContext();
        }
        if (instance == null) {
            synchronized (CacheDBService.class) {
                if (instance == null) {
                    instance = new CacheDBService(context);
                    return instance;
                }
            }
        }
        return instance;
    }

    public synchronized void updateCache(String url, String md5, String content, String ims) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("url", url);
        values.put("md5", md5);
        values.put("ims", ims);
        values.put("content", content);

        database.replace(CacheDBHelper.JS_CACHE_TABLE_NAME, null, values);
        database.close();
    }


    public synchronized CacheBean getCachebyUrl(String url) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sql = "select url,md5,ims,content from " + CacheDBHelper.JS_CACHE_TABLE_NAME + " where url=?";
        Cursor cursor = database.rawQuery(sql, new String[]{url});
        CacheBean cacheBean = null;
        while (cursor.moveToNext()) {
            String md5 = cursor.getString(1);
            String ims = cursor.getString(2);
            String content = cursor.getString(3);
            cacheBean = new CacheBean(url, md5, content, ims);
        }
        cursor.close();
        database.close();
        return cacheBean;
    }

    /**
     * 关闭数据库
     */
    public void closeDb() {
        dbHelper.close();
    }

    /**
     * 下载完成后删除数据库中的数据
     */
    public synchronized void delete(String url) {
        long totalCount = getCount(DOWNLOAD_INFO_TABLE);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        int count = database.delete(DOWNLOAD_INFO_TABLE, "url=?", new String[]{url});
//        Log.i("delete", "delete total=" + totalCount + ";delete count=" + count + ";url=" + url);
        database.close();
    }

    private long getCount(String table) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select count(*)from " + table, null);
        cursor.moveToFirst();
        long result = cursor.getLong(0);
        cursor.close();
        database.close();
        return result;
    }

    public void saveOrUpdateInfos() {

    }
}
