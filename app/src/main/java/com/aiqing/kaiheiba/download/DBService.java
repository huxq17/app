package com.aiqing.kaiheiba.download;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aiqing.kaiheiba.personal.download.MyBusinessInfLocal;
import com.huxq17.xprefs.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class DBService {
    private DBHelper dbHelper;
    private static DBService instance;
    private static final String DOWNLOAD_INFO_TABLE = "download_info";
    private static final String DOWNLOAD_GROUP_TABLE = "download_group";

    private DBService(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * @param context
     * @return
     */
    public static DBService getInstance(Context context) {
        if (context == null) {
            throw new RuntimeException("context == null");
        }
        if (!(context instanceof Application)) {
            context = context.getApplicationContext();
        }
        if (instance == null) {
            synchronized (DBService.class) {
                if (instance == null) {
                    instance = new DBService(context);
                    return instance;
                }
            }
        }
        return instance;
    }

    /**
     * 得到下载具体信息
     */
    public synchronized List<DownloadInfo> getInfos(String urlstr) {
        List<DownloadInfo> list = new ArrayList<DownloadInfo>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sql = "select thread_id,startposition,endposition,url from " + DOWNLOAD_INFO_TABLE + " where url=?";
        Cursor cursor = database.rawQuery(sql, new String[]{urlstr});
        while (cursor.moveToNext()) {
            DownloadInfo info = new DownloadInfo(cursor.getInt(0), cursor.getInt(1), cursor.getLong(2),
                    cursor.getString(3));
            list.add(info);
        }
        cursor.close();
        database.close();
        return list;
    }

    public synchronized List<DownloadGroup> getGroups(String group) {
        List<DownloadGroup> groups = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sql = "select url,length ,progress,avatar,download_name,file_path from " + DOWNLOAD_GROUP_TABLE + " where name=?";
        Cursor cursor = database.rawQuery(sql, new String[]{group});
        while (cursor.moveToNext()) {
            String url = cursor.getString(0);
            int length = cursor.getInt(1);
            int progress = cursor.getInt(2);
            String avatar = cursor.getString(3);
            String downloadName = cursor.getString(4);
            String filePath = cursor.getString(5);
            DownloadGroup bean = new DownloadGroup(group, avatar, downloadName, url, length, progress, filePath);
            groups.add(bean);
        }
        cursor.close();
        database.close();
        return groups;
    }

    /**
     * 更新数据库中的下载信息
     */
    public synchronized void updataInfos(int threadId, long startposition, long endposition, String url) {
        long totalCount = getCount(DOWNLOAD_INFO_TABLE);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // 如果存在就更新，不存在就插入
        String sql = "replace into " + DOWNLOAD_INFO_TABLE + "(thread_id,startposition,endposition,url) values(?,?,?,?)";
        Object[] bindArgs = {threadId, startposition, endposition, url};
        database.execSQL(sql, bindArgs);
        database.close();
        LogUtils.d("updataInfos total=" + totalCount + ";threadid=" + threadId + ";startposition=" + startposition + ";endposition=" + endposition);
    }

    public synchronized void insertGroup(DownloadGroup group) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sql = "replace into " + DOWNLOAD_GROUP_TABLE + "(name, url,length,progress,avatar,download_name,file_path) values(?,?,?,?,?,?,?)";
        Object[] bindArgs = {group.name, group.url, group.length, group.progress, group.avatar, group.downloadName, group.filePath};
        database.execSQL(sql, bindArgs);
        database.close();
    }

    public synchronized void insertDownloadId(DownloadGroup group) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sql = "replace into " + DOWNLOAD_GROUP_TABLE + "(name,avatar,download_name, url,file_path,progress) values(?,?,?,?,?,?)";
        Object[] bindArgs = {group.name, group.avatar, group.downloadName, group.url, group.filePath, group.progress};
        database.execSQL(sql, bindArgs);
        database.close();
    }

    public synchronized void updateDownloadId(String id, int progress) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("progress", progress);
        database.update(DOWNLOAD_GROUP_TABLE, values, "name=?", new String[]{id});
        database.close();
    }

    public synchronized List<DownloadGroup> queryDownloadList() {
        List<DownloadGroup> groups = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sql = "select name,url,download_name,avatar,file_path,progress from " + DOWNLOAD_GROUP_TABLE;
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            String url = cursor.getString(1);
            String download_name = cursor.getString(2);
            String avatar = cursor.getString(3);
            String filePath = cursor.getString(4);
            int progress = cursor.getInt(5);
            DownloadGroup bean = new DownloadGroup(id, avatar, download_name, url, filePath, progress);
            groups.add(bean);
        }
        cursor.close();
        database.close();
        return groups;
    }

    public synchronized MyBusinessInfLocal queryDownloadById(String id) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sql = "select url,download_name,avatar from " + DOWNLOAD_GROUP_TABLE + " where name=?";
        Cursor cursor = database.rawQuery(sql, new String[]{id});
        MyBusinessInfLocal myBusinessInfLocal = null;
        while (cursor.moveToNext()) {
            String downloadurl = cursor.getString(0);
            String downName = cursor.getString(1);
            String downIcon = cursor.getString(2);
            myBusinessInfLocal = new MyBusinessInfLocal(
                    downloadurl.hashCode(), downName, downIcon, downloadurl);
        }
        cursor.close();
        database.close();
        return myBusinessInfLocal;
    }

    public synchronized MyBusinessInfLocal queryDownloadByUrl(String url) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sql = "select url,download_name,avatar from " + DOWNLOAD_GROUP_TABLE + " where url=?";
        Cursor cursor = database.rawQuery(sql, new String[]{url});
        MyBusinessInfLocal myBusinessInfLocal = null;
        while (cursor.moveToNext()) {
            String downloadurl = cursor.getString(0);
            String downName = cursor.getString(1);
            String downIcon = cursor.getString(2);
            myBusinessInfLocal = new MyBusinessInfLocal(
                    downloadurl.hashCode(), downName, downIcon, downloadurl);
        }
        cursor.close();
        database.close();
        return myBusinessInfLocal;
    }

    public synchronized void deleteGroup(String url) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        int count = database.delete(DOWNLOAD_GROUP_TABLE, "url=?", new String[]{url});
        database.close();
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

    public synchronized void deleteByIdAndUrl(int id, String url) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        int count = database.delete(DOWNLOAD_INFO_TABLE, "thread_id=? and url=?", new String[]{
                id + "", url});
        Log.i("delete", "delete id=" + id + "," + "count=" + count);
        database.close();
    }
}
