package com.shoppinglist.app;

import android.app.Application;
import android.content.Context;

import com.shoppinglist.db.DataBaseHelper;
import com.shoppinglist.db.DataBaseManager;

/**
 * Created by ${} on 5/2/16.
 */
public class App extends Application {
    private static Context context;
    private static DataBaseHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        dbHelper = new DataBaseHelper();
        DataBaseManager.initializeInstance(dbHelper);
    }

    public static Context getContext() {
        return context;
    }
}
