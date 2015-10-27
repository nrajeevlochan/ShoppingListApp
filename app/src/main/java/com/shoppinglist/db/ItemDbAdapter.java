package com.shoppinglist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.shoppinglist.app.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by r.nalluru on 10/21/15.
 */
public class ItemDbAdapter extends DataBaseAdapter {

    public ItemDbAdapter(Context context) {
        super(context);
    }

    // code to add the new item
    public void insertItem(Item item) {
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.KEY_NAME, item.getName());
        values.put(DataBaseHelper.KEY_ARRAY_ID, item.getArrayId());
        values.put(DataBaseHelper.KEY_DESCRIPTION, item.getDescription());

        // Inserting Row
        database.insert(DataBaseHelper.TABLE_ITEMS, null, values);
        //2nd argument is String containing nullColumnHack
        //database.close(); // Closing database connection
    }

    // code to update the single item
    public int updateItem(Item item) {
        Log.d("ItemDbAdapter", "Update item: " + item.getName() + " " + item.getId());
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.KEY_NAME, item.getName());
        values.put(DataBaseHelper.KEY_ARRAY_ID, item.getArrayId());
        values.put(DataBaseHelper.KEY_DESCRIPTION, item.getDescription());

        // updating row
        return database.update(DataBaseHelper.TABLE_ITEMS, values, DataBaseHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    // Deleting single item
    public void deleteItem(Item item) {
        database.delete(DataBaseHelper.TABLE_ITEMS, DataBaseHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
        //database.close();
    }

    // code to get the single item
    Item getItem(int id) {
        Cursor cursor = database.query(DataBaseHelper.TABLE_ITEMS, new String[]{DataBaseHelper.KEY_ID,
                        DataBaseHelper.KEY_ARRAY_ID, DataBaseHelper.KEY_NAME, DataBaseHelper.KEY_DESCRIPTION},
                        DataBaseHelper.KEY_ID + "=?",
                        new String[]{String.valueOf(id)}, null, null, null, null);
        Item item = null;
        if (cursor != null) {
            cursor.moveToFirst();

            item = new Item(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)),
                    cursor.getString(2), cursor.getString(3));
        }
        // return contact
        return item;
    }

    // Getting items Count
    public int getItemCount(int index) {
        String[] args = {String.valueOf(index)};
        String countQuery = "SELECT  * FROM " + DataBaseHelper.TABLE_ITEMS + " WHERE " + DataBaseHelper.KEY_ARRAY_ID + " = ?";
        Cursor cursor = database.rawQuery(countQuery, args);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // code to get all contacts in a list view
    public List<Item> getAllItem(int index) {
        List<Item> itemList = new ArrayList<>();
        String[] args = {String.valueOf(index)};
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DataBaseHelper.TABLE_ITEMS + " WHERE " + DataBaseHelper.KEY_ARRAY_ID + " = ?";

        Cursor cursor = database.rawQuery(selectQuery, args);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(Integer.parseInt(cursor.getString(0)));
                item.setArrayId(Integer.parseInt(cursor.getString(1)));
                item.setName(cursor.getString(2));
                item.setDescription(cursor.getString(3));
                // Adding contact to list
                itemList.add(item);
            } while (cursor.moveToNext());
        }

        // return item list
        return itemList;
    }
}
