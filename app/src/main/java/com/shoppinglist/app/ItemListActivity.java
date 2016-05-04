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
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.shoppinglist.R;
import com.shoppinglist.db.ItemDbAdapter;

import java.util.ArrayList;
import java.util.List;

public class ItemListActivity extends AppCompatActivity implements
        PopUpInputDialog.NoticeDialogListener, LoaderManager.LoaderCallbacks {

    private static final String LOG_TAG = ItemListActivity.class.getSimpleName();
    private static final int ACTION_EDIT = 0;
    private static final int ACTION_DELETE = 1;
    private static final int DEFAULT_INDEX = -1;

    private ItemAdaptor mItemAdaptor;
    private ArrayList<Item> mItemList = null;
    private int mEditItemIndex = -1;
    private ItemDbAdapter mItemDbAdapter;
    private long mArrayIndex = DEFAULT_INDEX;
    private RecyclerView recyclerView;
    private static final int LOADER_ID = 2;

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
                Utils.showDialog(ItemListActivity.this, Constants.ITEM_INPUT_DIALOG, null);
            }
        });

        Bundle bundle = getIntent().getExtras();
        Store store = (Store) bundle.getParcelable(Constants.STORE_ITEM_KEY);
        mArrayIndex = (store == null) ? DEFAULT_INDEX : store.getId();
        setTitle((store == null) ? "" : store.getName());

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mItemDbAdapter = ItemDbAdapter.getInstance();

        getSupportLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
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
            Intent intent = new Intent(
                    RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
            startActivityForResult(intent, Constants.RESULT_SPEECH_OUTPUT);
            return true;
        } else if (id == android.R.id.home) {
            finish();
            //NavUtils.navigateUpFromSameTask();
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
            mItemList.add(item);
        } else {
            mItemList.get(index).setName(stringName);
            mItemList.get(index).setDescription(stringDescription);
            mItemDbAdapter.updateItem(mItemList.get(index));
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
            case Constants.RESULT_SPEECH_OUTPUT:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    stringName = text.get(0);
                    if (stringName != null && !stringName.isEmpty()) {
                        Item item = new Item(mArrayIndex, stringName, "");
                        mItemDbAdapter.insertItem(item);
                        mItemList.add(item);
                        mItemAdaptor.notifyDataSetChanged();
                    }
                }
                break;
            default:
                Log.w(LOG_TAG, "Not handled speech resultCode");
                break;
        }
    }

    @Override
    public Loader<List<Item>> onCreateLoader(int id, Bundle args) {
        return new ItemLoader(ItemListActivity.this, mArrayIndex);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mItemList = (ArrayList<Item>) data;

        mItemAdaptor = new ItemAdaptor(this, R.layout.item_layout, mItemList);
        recyclerView.setAdapter(mItemAdaptor);

        // Add onclick listener
        mItemAdaptor.SetOnItemClickListener(new ItemAdaptor.OnItemClickListener() {
            @Override
            public void onItemClick(int actionType, int position) {
                // do something with position
                if (mItemList != null && !mItemList.isEmpty()) {
                    switch (actionType) {
                        case ACTION_EDIT:
                            Utils.showDialog(ItemListActivity.this, Constants.ITEM_INPUT_DIALOG, mItemList.get(position));
                            setEditItemIndex(position);
                            break;
                        case ACTION_DELETE:
                            mItemDbAdapter.deleteItem(mItemList.get(position));
                            mItemList.remove(position);
                            mItemAdaptor.notifyDataSetChanged();
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        ItemTouchHelper.Callback callback = new ListItemTouchHelper();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        ((ListItemTouchHelper)callback).SetOnItemClickListener(new ListItemTouchHelper.OnItemSwipeListener() {
            @Override
            public void onItemSwipe(int position) {
                mItemDbAdapter.deleteItem(mItemList.get(position));
                mItemList.remove(position);
                mItemAdaptor.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onLoaderReset(Loader loader) {
        recyclerView.setAdapter(null);
    }

    public static class ItemLoader extends AsyncTaskLoader<List<Item>> {
        long index;

        public ItemLoader(Context context, long index) {
            super(context);
            this.index = index;
        }

        @Override
        public List<Item> loadInBackground() {
            return (ItemDbAdapter.getInstance().getItemCount(index) == 0) ? new ArrayList<Item>()
                    :(ArrayList<Item>) ItemDbAdapter.getInstance().getAllItem(index);
        }
    }
}
