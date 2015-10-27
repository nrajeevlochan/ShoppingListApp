package com.shoppinglist.app;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        intent.putExtra("array", position);
        startActivity(intent);
    }
}
