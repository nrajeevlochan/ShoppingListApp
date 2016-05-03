package com.shoppinglist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shoppinglist.app.App;

/**
 * Created by ${} on 10/25/15.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = DataBaseHelper.class.getName();
    //Database attributes
    public static final String DB_NAME = "item_db";
    public static final int DB_VERSION = 3;

    public DataBaseHelper() {
        super(App.getContext(), DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(StoreDbAdapter.createTable());
        db.execSQL(ItemDbAdapter.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL(StoreDbAdapter.DROP_STORES_TABLE);
        db.execSQL(ItemDbAdapter.DROP_ITEMS_TABLE);

        // Create tables again
        onCreate(db);
    }
}

