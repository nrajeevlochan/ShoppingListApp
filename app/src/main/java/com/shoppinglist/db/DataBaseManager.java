package com.shoppinglist.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ${} on 10/25/15.
 */
public class DataBaseManager {

    private static final String LOG_TAG = DataBaseManager.class.getName();
    private Integer mOpenCounter = 0;
    private static DataBaseManager mInstance;
    private SQLiteDatabase mDatabase;
    private static SQLiteOpenHelper mDbHelper;

    public static synchronized void initializeInstance(SQLiteOpenHelper helper) {
        if (mInstance == null) {
            mInstance = new DataBaseManager();
            mDbHelper = helper;
        }
    }

    // getInstance to maintain Singletone
    public static synchronized DataBaseManager getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException(DataBaseManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }

        return mInstance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        mOpenCounter+=1;
        if(mOpenCounter == 1) {
            // Opening new database
            mDatabase = mDbHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        mOpenCounter-=1;
        if(mOpenCounter == 0) {
            // Closing database
            mDatabase.close();

        }
    }
}
