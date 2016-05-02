package com.shoppinglist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ${} on 10/25/15.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    //Database attributes
    public static final String DB_NAME = "item_db";
    public static final int DB_VERSION = 3;

    // Table attributes
    public static final String TABLE_ITEMS = "items";
    public static final String TABLE_STORE = "store";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ARRAY_ID = "arrayid";
    public static final String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ARRAY_ID + " INTEGER, " + KEY_NAME + " TEXT,"
            + KEY_DESCRIPTION + " TEXT" + ")";
    private static final String DROP_ITEMS_TABLE = "DROP TABLE IF EXISTS " + TABLE_ITEMS;

    public static final String CREATE_STORES_TABLE = "CREATE TABLE " + TABLE_STORE + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT" + ")";
    private static final String DROP_STORES_TABLE = "DROP TABLE IF EXISTS " + TABLE_STORE;

    private static DataBaseHelper instance;

    public static synchronized DataBaseHelper getHelper(Context context) {
        if (instance == null)
            instance = new DataBaseHelper(context);
        return instance;
    }

    private DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ITEMS_TABLE);
        db.execSQL(CREATE_STORES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL(DROP_ITEMS_TABLE);
        db.execSQL(DROP_STORES_TABLE);

        // Create tables again
        onCreate(db);
    }
}

