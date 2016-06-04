package com.ilnar.sandbox.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.ilnar.sandbox.dictionary.DictionaryRecord;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ilnar on 31.05.16.
 */
public class RecentQueryDBHelper extends SQLiteOpenHelper {
    private static final String DB_FILE_NAME = "recent_record.db";

    private static final int DB_VERSION = 3;

    private static volatile RecentQueryDBHelper instance;

    public static RecentQueryDBHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (RecentQueryDBHelper.class) {
                if (instance == null) {
                    instance = new RecentQueryDBHelper(context);
                }
            }
        }
        return instance;
    }

    private final Context context;

    private RecentQueryDBHelper(Context context) {
        super(context, DB_FILE_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "create db: " + RecentQueryContract.QueriesTable.CREATE_TABLE);
        db.execSQL(RecentQueryContract.QueriesTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "onUpgrade");
        db.execSQL(RecentQueryContract.QueriesTable.DROP_TABLE);
        onCreate(db);
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private ContentValues getContentValues(DictionaryRecord query) {
        ContentValues result = new ContentValues();
        result.put(RecentQueryContract.RecentQueryColumns.COLUMN_NAME_WORD, query.getWord());
        result.put(RecentQueryContract.RecentQueryColumns.COLUMN_NAME_TRANSLATION, query.getTranslation());
        result.put(RecentQueryContract.RecentQueryColumns.COLUMN_NAME_DATE, getCurrentTimestamp());
        return result;
    }

    private DictionaryRecord getDictionaryRecord(Cursor c) {
        int wordId = c.getColumnIndexOrThrow(RecentQueryContract.RecentQueryColumns.COLUMN_NAME_WORD);
        int translationId = c.getColumnIndexOrThrow(RecentQueryContract.RecentQueryColumns.COLUMN_NAME_TRANSLATION);
        return new DictionaryRecord(c.getString(wordId), c.getString(translationId));
    }

    public void clearHistory() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(RecentQueryContract.QueriesTable.DROP_TABLE);
        onCreate(db);
    }

    public void saveRecentQuery(final DictionaryRecord query) {
        if (query == null || TextUtils.isEmpty(query.getWord())) {
            return;
        }
        Log.d(LOG_TAG, "saveQuery " + query.getWord());
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getWritableDatabase();
                String q = RecentQueryContract.RecentQueryColumns.COLUMN_NAME_WORD + "=?";
                Cursor c = db.query(RecentQueryContract.QueriesTable.TABLE,
                        null,
                        q,
                        new String[]{query.getWord()},
                        null,
                        null,
                        null
                );
                if (c != null && c.moveToFirst()) {
                    ContentValues values = new ContentValues();
                    values.put(RecentQueryContract.RecentQueryColumns.COLUMN_NAME_DATE, getCurrentTimestamp());
                    db.update(RecentQueryContract.QueriesTable.TABLE,
                            values,
                            q,
                            new String[]{query.getWord()});
                } else {
                    db.insert(RecentQueryContract.QueriesTable.TABLE, null, getContentValues(query));
                }
                if (c != null) {
                    c.close();
                }
            }
        }).start();
    }

    public List<DictionaryRecord> getRecentQueries() {
        List<DictionaryRecord> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Log.d(LOG_TAG, "getRecent");
        Cursor c = db.query(RecentQueryContract.QueriesTable.TABLE, null, null, null, null, null,
                RecentQueryContract.RecentQueryColumns.COLUMN_NAME_DATE + " DESC");
        if (c != null && c.moveToFirst()) {
            while (!c.isAfterLast()) {
                result.add(getDictionaryRecord(c));
                c.moveToNext();
            }
        }
        for (DictionaryRecord e : result) {
            Log.d(LOG_TAG, "el=" + e.getWord());
        }
        if (c != null) {
            c.close();
        }
        return result;
    }

    private static final String LOG_TAG = RecentQueryDBHelper.class.getName();

}
