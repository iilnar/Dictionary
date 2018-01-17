package com.antat.dictionary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.antat.dictionary.dictionary.DictionaryRecord;

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

    private static final int DB_VERSION = 4;

    private static volatile RecentQueryDBHelper[] instance = new RecentQueryDBHelper[2];

    public static RecentQueryDBHelper getInstance(Context context, int sectionNumber) {
        synchronized (RecentQueryDBHelper.class) {
            if (instance[sectionNumber] == null) {
                instance[sectionNumber] = new RecentQueryDBHelper(context, sectionNumber);
            }
        }
        return instance[sectionNumber];
    }

    private RecentQueryDBHelper(Context context, int sectionNumber) {
        super(context, sectionNumber + "_" + DB_FILE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RecentQueryContract.QueriesTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
        result.put(RecentQueryContract.RecentQueryColumns.COLUMN_NAME_TRANSLATION, query.getTranslation()[0]);
        result.put(RecentQueryContract.RecentQueryColumns.COLUMN_NAME_DATE, getCurrentTimestamp());
        return result;
    }

    private DictionaryRecord getDictionaryRecord(Cursor c) {
        int wordId = c.getColumnIndexOrThrow(RecentQueryContract.RecentQueryColumns.COLUMN_NAME_WORD);
        int translationId = c.getColumnIndexOrThrow(RecentQueryContract.RecentQueryColumns.COLUMN_NAME_TRANSLATION);
        return new DictionaryRecord(c.getString(wordId), null, new String[]{c.getString(translationId)}, new String[]{});
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
                            new String[]{query.getWord()}
                    );
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
        Cursor c = db.query(RecentQueryContract.QueriesTable.TABLE, null, null, null, null, null,
                RecentQueryContract.RecentQueryColumns.COLUMN_NAME_DATE + " DESC");
        if (c != null && c.moveToFirst()) {
            while (!c.isAfterLast()) {
                result.add(getDictionaryRecord(c));
                c.moveToNext();
            }
        }
        if (c != null) {
            c.close();
        }
        return result;
    }

    private static final String TAG = "RecentQueryDBHelper";
}
