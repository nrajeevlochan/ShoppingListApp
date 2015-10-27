package com.shoppinglist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ${} on 10/25/15.
 */
public class DataBaseAdapter {

    protected SQLiteDatabase database;
    private DataBaseHelper dbHelper;

    public DataBaseAdapter(Context context) {
        dbHelper = DataBaseHelper.getHelper(context);
        database = dbHelper.getWritableDatabase();
    }
}
