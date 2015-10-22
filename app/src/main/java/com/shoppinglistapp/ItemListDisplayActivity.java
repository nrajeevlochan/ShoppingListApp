package com.shoppinglistapp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class ItemListDisplayActivity extends AppCompatActivity implements ItemInputDialog.NoticeDialogListener {

    private static final int ACTION_EDIT = 0;
    private static final int ACTION_DELETE = 1;
    private static final String DIALOG_TAG = "ItemInputDialog";

    private RecyclerView mRecyclerView;
    private ItemAdaptor mItemAdaptor;
    private ArrayList<Item> itemset = null;
    private DialogFragment mItemDialog;
    private int mEditItemIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemDialog.show(getFragmentManager(), DIALOG_TAG);
            }
        });

        itemset = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if (savedInstanceState == null) {
            itemset = new ArrayList<>();
        } else {
            itemset = savedInstanceState.getParcelableArrayList("persistData");
        }

        mItemAdaptor = new ItemAdaptor(this, R.layout.item_layout, itemset);
        mRecyclerView.setAdapter(mItemAdaptor);

        // Create an instance of the dialog fragment and show it
        mItemDialog = new ItemInputDialog();

        // Add onclick listener
        mItemAdaptor.SetOnItemClickListener(new ItemAdaptor.OnItemClickListener() {
            @Override
            public void onItemClick(int actionType, int position) {
                // do something with position
                if (itemset != null && !itemset.isEmpty()) {
                    switch (actionType) {
                        case ACTION_EDIT:
                            mItemDialog.show(getFragmentManager(), DIALOG_TAG);
                            //((EditText)(mItemDialog.getDialog()).findViewById(R.id.dialog_item_name)).setText(itemset.get(position).getName());
                            //((EditText)(mItemDialog.getDialog()).findViewById(R.id.dialog_item_desc)).setText(itemset.get(position).getDescription());
                            setEditItemIndex(position);
                            break;
                        case ACTION_DELETE:
                            itemset.remove(position);
                            mItemAdaptor.notifyDataSetChanged();
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        ItemTouchHelper.Callback callback = new ItemMyTouchHelper(mItemAdaptor);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        ((ItemMyTouchHelper)callback).SetOnItemClickListener(new ItemMyTouchHelper.OnItemSwipeListener() {
            @Override
            public void onItemSwipe(int position) {
                itemset.remove(position);
                mItemAdaptor.notifyDataSetChanged();
                //Toast.makeText(MainActivity.this, "position: " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("persistData", itemset);
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
            mItemDialog.show(getFragmentManager(), DIALOG_TAG);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Dialog dialogView = dialog.getDialog();
        String stringName = ((EditText) dialogView.findViewById(R.id.dialog_item_name)).getText().toString();
        String stringDescription = ((EditText) dialogView.findViewById(R.id.dialog_item_desc)).getText().toString();
        int index = getEditItemIndex();
        if (index == -1) {
            Item item = new Item(stringName, stringDescription);
            itemset.add(item);
        } else {
            itemset.get(index).setName(stringName);
            itemset.get(index).setDescription(stringDescription);
            setEditItemIndex(-1);
        }
        mItemAdaptor.notifyDataSetChanged();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //Just ignore
    }

    private int getEditItemIndex() {
        return mEditItemIndex;
    }

    private void setEditItemIndex(int editItemIndex) {
        this.mEditItemIndex = editItemIndex;
    }
}
