package com.shoppinglist.app;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
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
import com.shoppinglist.db.ItemDbAdapter;
import com.shoppinglist.db.StoreDbAdapter;

import java.util.ArrayList;

public class StoreListActivity extends AppCompatActivity implements PopUpInputDialog.NoticeDialogListener, StoreAdapter.OnItemClickListener {

    private static final String LOG_TAG = "StoreListActivitytype";
    private ArrayList<Store> storeList;
    private StoreAdapter mStoreAdaptor;
    private DialogFragment mStoreDialog;
    private int editIndex = -1;
    private static final int DIAPLAY_ITEM_LIST = 0;
    private StoreDbAdapter mStoreDbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RecyclerView recyclerView;
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

        mStoreDbAdapter = new StoreDbAdapter(this);

        recyclerView = (RecyclerView) findViewById(R.id.store_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        storeList = new ArrayList<>();
        if (mStoreDbAdapter.getStoreCount() == 0) {
            storeList = new ArrayList<>();
        } else {
            storeList = (ArrayList<Store>) mStoreDbAdapter.getAllStore();
        }

        mStoreAdaptor = new StoreAdapter(this, R.layout.store_layout, storeList);
        recyclerView.setAdapter(mStoreAdaptor);

        // Create an instance of the dialog fragment and show it
        mStoreAdaptor.SetOnItemClickListener(this);

        ItemTouchHelper.Callback callback = new ItemMyTouchHelper();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        ((ItemMyTouchHelper)callback).SetOnItemClickListener(new ItemMyTouchHelper.OnItemSwipeListener() {
            @Override
            public void onItemSwipe(int position) {
                mStoreDbAdapter.deleteStore(storeList.get(position));
                storeList.remove(position);
                mStoreAdaptor.notifyDataSetChanged();
                Toast.makeText(StoreListActivity.this, "position: " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_store_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_store) {
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
                mStoreDbAdapter.insertStore(store);
                storeList.add(store);
            }
        } else {
            storeList.get(editIndex).setName(stringName);
            mStoreDbAdapter.updateStore(storeList.get(editIndex));
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
        if (storeList != null && !storeList.isEmpty()) {
            Utils.showDialog(StoreListActivity.this, Constants.STORE_INPUT_DIALOG, storeList.get(position));
            editIndex = position;
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, ItemListDisplayActivity.class);
        Log.d(LOG_TAG, "onItemClick" + storeList.get(position).getId());
        intent.putExtra("array", storeList.get(position).getId());
        intent.putExtra("title", storeList.get(position).getName());
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
                        storeList.add(store);
                        mStoreAdaptor.notifyDataSetChanged();
                    }
                }
                break;
            default:
                Log.w(LOG_TAG, "Not handled speech resultCode");
                break;
        }
    }
}
