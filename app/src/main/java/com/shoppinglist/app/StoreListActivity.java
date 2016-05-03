package com.shoppinglist.app;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.shoppinglist.R;
import com.shoppinglist.db.StoreDbAdapter;

import java.util.ArrayList;
import java.util.List;

public class StoreListActivity extends AppCompatActivity implements
        PopUpInputDialog.NoticeDialogListener, StoreAdapter.OnItemClickListener, LoaderManager.LoaderCallbacks {

    private static final String LOG_TAG = StoreListActivity.class.getSimpleName();
    private ArrayList<Store> mStoreList;
    private StoreAdapter mStoreAdaptor;
    private DialogFragment mStoreDialog;
    private int editIndex = -1;
    private static final int DIAPLAY_ITEM_LIST = 0;
    private StoreDbAdapter mStoreDbAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        Toolbar toolbar = (Toolbar) findViewById(R.id.store_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showDialog(StoreListActivity.this, Constants.STORE_INPUT_DIALOG, null);
            }
        });

        mStoreDbAdapter =  StoreDbAdapter.getInstance();

        getSupportLoaderManager().initLoader(1, null, this).forceLoad();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_list_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            //Utils.showDialog(this, Constants.ITEM_INPUT_DIALOG, null);
            Intent intent = new Intent(
                    RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
            startActivityForResult(intent, Constants.RESULT_SPEECH_OUTPUT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Dialog dialogView = dialog.getDialog();
        String stringName = ((EditText) dialogView.findViewById(R.id.dialog_store_name)).getText().toString();
        if (editIndex == -1) {
            if (!stringName.isEmpty()) {
                Store store = new Store(stringName);
                long rowId = mStoreDbAdapter.insertStore(store);
                store.setId(rowId);
                mStoreList.add(store);
            }
        } else {
            mStoreList.get(editIndex).setName(stringName);
            mStoreDbAdapter.updateStore(mStoreList.get(editIndex));
            editIndex = -1;
        }

        if (!stringName.isEmpty()) {
            mStoreAdaptor.notifyDataSetChanged();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // Ignore this event for now
    }

    @Override
    public void onItemLongClick(int position) {
        if (mStoreList != null && !mStoreList.isEmpty()) {
            Utils.showDialog(StoreListActivity.this, Constants.STORE_INPUT_DIALOG, mStoreList.get(position));
            editIndex = position;
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, ItemListActivity.class);
        Log.d(LOG_TAG, "onItemClick" + mStoreList.get(position).getId());
        //intent.putExtra("array", storeList.get(position).getId());
        //intent.putExtra("title", storeList.get(position).getName());
        intent.putExtra(Constants.STORE_ITEM_KEY, mStoreList.get(position));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String stringName;
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case Constants.RESULT_SPEECH_OUTPUT:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    stringName = text.get(0);
                    if (stringName != null && !stringName.isEmpty()) {
                        Store store = new Store(stringName);
                        long rowId = mStoreDbAdapter.insertStore(store);
                        Log.d(LOG_TAG, "Insert ret val: " + rowId);
                        store.setId(rowId);
                        mStoreList.add(store);
                        mStoreAdaptor.notifyDataSetChanged();
                    }
                }
                break;
            default:
                Log.w(LOG_TAG, "Not handled speech resultCode");
                break;
        }
    }

    @Override
    public Loader<List<Store>> onCreateLoader(int id, Bundle args) {
        return new StoreLoader(StoreListActivity.this);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        recyclerView = (RecyclerView) findViewById(R.id.store_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mStoreList = (ArrayList<Store>) data;
        mStoreAdaptor = new StoreAdapter(this, R.layout.store_layout, mStoreList);
        recyclerView.setAdapter(mStoreAdaptor);

        // Create an instance of the dialog fragment and show it
        mStoreAdaptor.SetOnItemClickListener(this);

        ItemTouchHelper.Callback callback = new ListItemTouchHelper();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        ((ListItemTouchHelper)callback).SetOnItemClickListener(new ListItemTouchHelper.OnItemSwipeListener() {
            @Override
            public void onItemSwipe(int position) {
                mStoreDbAdapter.deleteStore(mStoreList.get(position));
                mStoreList.remove(position);
                mStoreAdaptor.notifyDataSetChanged();
                Toast.makeText(StoreListActivity.this, "position: " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLoaderReset(Loader loader) {
        recyclerView.setAdapter(null);
    }

    public static class StoreLoader extends AsyncTaskLoader<List<Store>> {

        public StoreLoader(Context context) {
            super(context);
        }

        @Override
        public List<Store> loadInBackground() {
            return (StoreDbAdapter.getInstance().getStoreCount() == 0) ? new ArrayList<Store>()
                    :(ArrayList<Store>) StoreDbAdapter.getInstance().getAllStore();
        }
    }
}
