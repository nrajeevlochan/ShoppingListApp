package com.shoppinglist.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.shoppinglist.app.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by r.nalluru on 10/21/15.
 */
public class ItemDbAdapter {

    public static final String TABLE_ITEMS = "items";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ARRAY_ID = "arrayid";
    public static final String DROP_ITEMS_TABLE = "DROP TABLE IF EXISTS " + TABLE_ITEMS;

    private static final int KEY_INDEX = 0;
    private static final int ARRAY_INDEX = 1;
    private static final int NAME_INDEX = 2;
    private static final int DESCRIPTION_INDEX = 3;

    public ItemDbAdapter() {
    }

    public static String createTable(){
        return "CREATE TABLE " + TABLE_ITEMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ARRAY_ID + " INTEGER, " + KEY_NAME + " TEXT,"
                + KEY_DESCRIPTION + " TEXT" + ")";
    }

    // code to add the new item
    public void insertItem(Item item) {
        SQLiteDatabase db = DataBaseAdapter.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, item.getName());
        values.put(KEY_ARRAY_ID, item.getArrayId());
        values.put(KEY_DESCRIPTION, item.getDescription());

        // Inserting Row
        db.insert(TABLE_ITEMS, null, values);
    }

    // code to update the single item
    public int updateItem(Item item) {
        SQLiteDatabase db = DataBaseAdapter.getInstance().openDatabase();
        Log.d("ItemDbAdapter", "Update item: " + item.getName() + " " + item.getId());
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, item.getName());
        values.put(KEY_ARRAY_ID, item.getArrayId());
        values.put(KEY_DESCRIPTION, item.getDescription());

        // updating row
        return db.update(TABLE_ITEMS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    // Deleting single item
    public void deleteItem(Item item) {
        SQLiteDatabase db = DataBaseAdapter.getInstance().openDatabase();
        db.delete(TABLE_ITEMS, KEY_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    // code to get the single item
    Item getItem(int id) {
        SQLiteDatabase db = DataBaseAdapter.getInstance().openDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, new String[]{KEY_ID,
                        KEY_ARRAY_ID, KEY_NAME, KEY_DESCRIPTION},
                        KEY_ID + "=?",
                        new String[]{String.valueOf(id)}, null, null, null, null);
        Item item = null;
        if (cursor != null) {
            cursor.moveToFirst();

            item = new Item(Integer.parseInt(cursor.getString(KEY_INDEX)), Integer.parseInt(cursor.getString(ARRAY_INDEX)),
                    cursor.getString(NAME_INDEX), cursor.getString(DESCRIPTION_INDEX));
        }
        // return contact
        return item;
    }

    // Getting items Count
    public int getItemCount(long index) {
        SQLiteDatabase db = DataBaseAdapter.getInstance().openDatabase();
        String[] args = {String.valueOf(index)};
        String countQuery = "SELECT  * FROM " + TABLE_ITEMS + " WHERE " + KEY_ARRAY_ID + " = ?";
        Cursor cursor = db.rawQuery(countQuery, args);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // code to get all contacts in a list view
    public List<Item> getAllItem(long index) {
        SQLiteDatabase db = DataBaseAdapter.getInstance().openDatabase();
        List<Item> itemList = new ArrayList<>();
        String[] args = {String.valueOf(index)};
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ITEMS + " WHERE " + KEY_ARRAY_ID + " = ?";

        Cursor cursor = db.rawQuery(selectQuery, args);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(Integer.parseInt(cursor.getString(KEY_INDEX)));
                item.setArrayId(Integer.parseInt(cursor.getString(ARRAY_INDEX)));
                item.setName(cursor.getString(NAME_INDEX));
                item.setDescription(cursor.getString(DESCRIPTION_INDEX));
                // Adding contact to list
                itemList.add(item);
            } while (cursor.moveToNext());
        }

        // return item list
        return itemList;
    }
}
