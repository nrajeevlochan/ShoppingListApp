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
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.shoppinglist.R;
import com.shoppinglist.db.ItemDbAdapter;

import java.util.ArrayList;

public class ItemListDisplayActivity extends AppCompatActivity implements PopUpInputDialog.NoticeDialogListener {

    private static final String LOG_TAG = "ItemListDisplayActivity";
    private static final int ACTION_EDIT = 0;
    private static final int ACTION_DELETE = 1;
    private static final int RESULT_SPEECH_OUTPUT = 0;

    private RecyclerView mRecyclerView;
    private ItemAdaptor mItemAdaptor;
    private ArrayList<Item> itemset = null;
    private int mEditItemIndex = -1;
    private ItemDbAdapter mItemDbAdapter;
    private int mArrayIndex = -1;

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
                Utils.showDialog(ItemListDisplayActivity.this, Constants.ITEM_INPUT_DIALOG, null);
            }
        });

        FloatingActionButton fabVoice = (FloatingActionButton) findViewById(R.id.fab_voice);
        fabVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                startActivityForResult(intent, RESULT_SPEECH_OUTPUT);
            }
        });

        mArrayIndex = getIntent().getIntExtra("array", -1);

        mItemDbAdapter = new ItemDbAdapter(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if (mItemDbAdapter.getItemCount(mArrayIndex) == 0) {
            itemset = new ArrayList<>();
        } else {
            itemset = (ArrayList<Item>) mItemDbAdapter.getAllItem(mArrayIndex);

        }

        mItemAdaptor = new ItemAdaptor(this, R.layout.item_layout, itemset);
        mRecyclerView.setAdapter(mItemAdaptor);

        // Add onclick listener
        mItemAdaptor.SetOnItemClickListener(new ItemAdaptor.OnItemClickListener() {
            @Override
            public void onItemClick(int actionType, int position) {
                // do something with position
                if (itemset != null && !itemset.isEmpty()) {
                    switch (actionType) {
                        case ACTION_EDIT:
                            Utils.showDialog(ItemListDisplayActivity.this, Constants.ITEM_INPUT_DIALOG, itemset.get(position));
                            setEditItemIndex(position);
                            break;
                        case ACTION_DELETE:
                            mItemDbAdapter.deleteItem(itemset.get(position));
                            itemset.remove(position);
                            mItemAdaptor.notifyDataSetChanged();
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        ItemTouchHelper.Callback callback = new ItemMyTouchHelper();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        ((ItemMyTouchHelper)callback).SetOnItemClickListener(new ItemMyTouchHelper.OnItemSwipeListener() {
            @Override
            public void onItemSwipe(int position) {
                mItemDbAdapter.deleteItem(itemset.get(position));
                itemset.remove(position);
                mItemAdaptor.notifyDataSetChanged();
            }
        });
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
            Utils.showDialog(this, Constants.ITEM_INPUT_DIALOG, null);
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
            Item item = new Item(mArrayIndex, stringName, stringDescription);
            mItemDbAdapter.insertItem(item);
            itemset.add(item);
        } else {
            itemset.get(index).setName(stringName);
            itemset.get(index).setDescription(stringDescription);
            mItemDbAdapter.updateItem(itemset.get(index));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String stringName;
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case RESULT_SPEECH_OUTPUT:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    stringName = text.get(0);
                    if (stringName != null) {
                        Item item = new Item(stringName, "");
                        mItemDbAdapter.insertItem(item);
                        itemset.add(item);
                        mItemAdaptor.notifyDataSetChanged();
                    }
                }
                break;
            default:
                Log.w(LOG_TAG, "Not handled speech resultCode");
                break;
        }
    }
}
