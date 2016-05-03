package com.shoppinglist.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.shoppinglist.app.Store;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${} on 10/25/15.
 */
public class StoreDbAdapter {

    private static final String TABLE_STORE = "store";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String TABLE_ITEMS = "items";
    private static final String KEY_ARRAY_ID = "arrayid";
    public static final String DROP_STORES_TABLE = "DROP TABLE IF EXISTS " + TABLE_STORE;
    private static StoreDbAdapter mInstance = null;

    private static final int KEY_INDEX = 0;
    private static final int NAME_INDEX = 1;

    private StoreDbAdapter() {
    }

    public static StoreDbAdapter getInstance() {
        if (mInstance == null) {
            mInstance = new StoreDbAdapter();
        }
        return mInstance;
    }

    public static String createTable(){
        return "CREATE TABLE " + TABLE_STORE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT" + ")";
    }

    // code to add the new item
    public long insertStore(Store store) {
        SQLiteDatabase db = DataBaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, store.getName());
        // Inserting Row
        long rowId = db.insert(TABLE_STORE, null, values);
        DataBaseManager.getInstance().closeDatabase();
        return rowId;
    }

    // code to update the single item
    public int updateStore(Store store) {
        SQLiteDatabase db = DataBaseManager.getInstance().openDatabase();
        Log.d("ItemDbAdapter", "Update item: " + store.getName() + " " + store.getId());
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, store.getName());

        // updating row
         int rowCount = db.update(TABLE_STORE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(store.getId())});
        DataBaseManager.getInstance().closeDatabase();
        return rowCount;
    }

    // Deleting single item
    public void deleteStore(Store store) {
        SQLiteDatabase db = DataBaseManager.getInstance().openDatabase();
        deleteItems(store.getId());
        db.delete(TABLE_STORE, KEY_ID + " = ?",
                new String[]{String.valueOf(store.getId())});
        DataBaseManager.getInstance().closeDatabase();
    }

    // code to get the single item
    public Store getStore(int id) {
        SQLiteDatabase db = DataBaseManager.getInstance().openDatabase();
        Cursor cursor = db.query(TABLE_STORE, new String[]{KEY_ID,
                        KEY_NAME}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        Store store = null;
        if (cursor != null) {
            cursor.moveToFirst();

            store = new Store(Integer.parseInt(cursor.getString(KEY_INDEX)),
                    cursor.getString(NAME_INDEX));
        }
        DataBaseManager.getInstance().closeDatabase();
        // return contact
        return store;
    }

    // Getting items Count
    public int getStoreCount() {
        SQLiteDatabase db = DataBaseManager.getInstance().openDatabase();
        String countQuery = "SELECT  * FROM " + TABLE_STORE;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        DataBaseManager.getInstance().closeDatabase();
        // return count
        return count;
    }

    // code to get all contacts in a list view
    public List<Store> getAllStore() {
        SQLiteDatabase db = DataBaseManager.getInstance().openDatabase();
        List<Store> storeList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_STORE;

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Store store = new Store();
                store.setId(Integer.parseInt(cursor.getString(KEY_INDEX)));
                store.setName(cursor.getString(NAME_INDEX));
                // Adding contact to list
                storeList.add(store);
            } while (cursor.moveToNext());
        }
        DataBaseManager.getInstance().closeDatabase();
        // return item list
        return storeList;
    }

    public void deleteItems(long index) {
        SQLiteDatabase db = DataBaseManager.getInstance().openDatabase();
        String[] args = {String.valueOf(index)};
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ITEMS + " WHERE " + KEY_ARRAY_ID + " = ?";

        Cursor cursor = db.rawQuery(selectQuery, args);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                db.delete(TABLE_ITEMS, KEY_ARRAY_ID + " = ?",
                        new String[]{String.valueOf(index)});
            } while (cursor.moveToNext());
        }
        DataBaseManager.getInstance().closeDatabase();
    }
}
