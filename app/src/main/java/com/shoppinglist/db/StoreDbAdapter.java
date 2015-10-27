package com.shoppinglist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.shoppinglist.app.Store;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${} on 10/25/15.
 */
public class StoreDbAdapter extends DataBaseAdapter {

    public StoreDbAdapter(Context context) {
        super(context);
    }

    // code to add the new item
    public void insertStore(Store store) {
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.KEY_NAME, store.getName());

        // Inserting Row
        database.insert(DataBaseHelper.TABLE_STORE, null, values);
        //2nd argument is String containing nullColumnHack
        //database.close(); // Closing database connection
    }

    // code to update the single item
    public int updateStore(Store store) {
        Log.d("ItemDbAdapter", "Update item: " + store.getName() + " " + store.getId());
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.KEY_NAME, store.getName());
        //values.put(DataBaseHelper.KEY_DESCRIPTION, item.getDescription());

        // updating row
        return database.update(DataBaseHelper.TABLE_STORE, values, DataBaseHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(store.getId())});
    }

    // Deleting single item
    public void deleteStore(Store store) {
        deleteItems(store.getId());
        database.delete(DataBaseHelper.TABLE_STORE, DataBaseHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(store.getId())});

        //database.close();
    }

    // code to get the single item
    Store getStore(int id) {
        Cursor cursor = database.query(DataBaseHelper.TABLE_STORE, new String[]{DataBaseHelper.KEY_ID,
                        DataBaseHelper.KEY_NAME}, DataBaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        Store store = null;
        if (cursor != null) {
            cursor.moveToFirst();

            store = new Store(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1));
        }
        // return contact
        return store;
    }

    // Getting items Count
    public int getStoreCount() {
        String countQuery = "SELECT  * FROM " + DataBaseHelper.TABLE_STORE;
        Cursor cursor = database.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // code to get all contacts in a list view
    public List<Store> getAllStore() {
        List<Store> storeList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DataBaseHelper.TABLE_STORE;

        Cursor cursor = database.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Store store = new Store();
                store.setId(Integer.parseInt(cursor.getString(0)));
                store.setName(cursor.getString(1));
                // Adding contact to list
                storeList.add(store);
            } while (cursor.moveToNext());
        }

        // return item list
        return storeList;
    }

    public void deleteItems(int index) {
        String[] args = {String.valueOf(index)};
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DataBaseHelper.TABLE_ITEMS + " WHERE " + DataBaseHelper.KEY_ARRAY_ID + " = ?";

        Cursor cursor = database.rawQuery(selectQuery, args);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                database.delete(DataBaseHelper.TABLE_ITEMS, DataBaseHelper.KEY_ARRAY_ID + " = ?",
                        new String[]{String.valueOf(index)});
            } while (cursor.moveToNext());
        }
    }
}
