package com.shoppinglistapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by r.nalluru on 10/21/15.
 */
public class ItemDbAdapter {

    ItemOpenDbHelper helper;

    public ItemDbAdapter(Context context) {
        helper = new ItemOpenDbHelper(context);
    }

    // code to add the new item
    void insertItem(Item item) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ItemOpenDbHelper.KEY_NAME, item.getName());
        values.put(ItemOpenDbHelper.KEY_DESCRIPTION, item.getDescription());

        // Inserting Row
        db.insert(ItemOpenDbHelper.TABLE_ITEMS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to update the single item
    public int updateItem(Item item) {
        SQLiteDatabase db = helper.getWritableDatabase();

        Log.d("ItemDbAdapter", "Update item: " + item.getName() + " " + item.getId());
        ContentValues values = new ContentValues();
        values.put(ItemOpenDbHelper.KEY_NAME, item.getName());
        values.put(ItemOpenDbHelper.KEY_DESCRIPTION, item.getDescription());

        // updating row
        return db.update(ItemOpenDbHelper.TABLE_ITEMS, values, ItemOpenDbHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    // Deleting single item
    public void deleteItem(Item item) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(ItemOpenDbHelper.TABLE_ITEMS, ItemOpenDbHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
        db.close();
    }

    // code to get the single item
    Item getItem(int id) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(ItemOpenDbHelper.TABLE_ITEMS, new String[]{ItemOpenDbHelper.KEY_ID,
                        ItemOpenDbHelper.KEY_NAME, ItemOpenDbHelper.KEY_DESCRIPTION}, ItemOpenDbHelper.KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        Item item = null;
        if (cursor != null) {
            cursor.moveToFirst();

            item = new Item(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2));
        }
        // return contact
        return item;
    }

    // Getting items Count
    public int getItemCount() {
        String countQuery = "SELECT  * FROM " + ItemOpenDbHelper.TABLE_ITEMS;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // code to get all contacts in a list view
    public List<Item> getAllItem() {
        List<Item> itemList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + ItemOpenDbHelper.TABLE_ITEMS;

        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(Integer.parseInt(cursor.getString(0)));
                item.setName(cursor.getString(1));
                item.setDescription(cursor.getString(2));
                // Adding contact to list
                itemList.add(item);
            } while (cursor.moveToNext());
        }

        // return item list
        return itemList;
    }

    class ItemOpenDbHelper extends SQLiteOpenHelper

    {
        //Database attributes
        public static final String DB_NAME = "item_db";
        public static final int DB_VERSION = 1;

        // Table attributes
        public static final String TABLE_ITEMS = "items";
        private static final String KEY_ID = "id";
        private static final String KEY_NAME = "name";
        private static final String KEY_DESCRIPTION = "description";
        private static final String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_DESCRIPTION + " TEXT" + ")";
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_ITEMS;

        public ItemOpenDbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_ITEMS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Drop older table if existed
            db.execSQL(DROP_TABLE);

            // Create tables again
            onCreate(db);
        }
    }
}
