package com.antat.dictionary.database;

/**
 * Created by ilnar on 31.05.16.
 */
public final class RecentQueryContract {
    public interface RecentQueryColumns {
        String COLUMN_NAME_WORD = "word";
        String COLUMN_NAME_TRANSLATION = "translation";
        String COLUMN_NAME_DATE = "date";
    }

    public static final class QueriesTable implements RecentQueryColumns {
        public static final String TABLE = "recent_records";

        static final String CREATE_TABLE = "CREATE TABLE " + TABLE
                + " ("
                + COLUMN_NAME_WORD + " TEXT PRIMARY KEY, "
                + COLUMN_NAME_TRANSLATION + " TEXT, "
                + COLUMN_NAME_DATE + " TEXT"
                + " )";

        static final String DROP_TABLE = "DROP TABLE IF EXISTS " + RecentQueryContract.QueriesTable.TABLE;

    }

    private RecentQueryContract() {
    }
}
