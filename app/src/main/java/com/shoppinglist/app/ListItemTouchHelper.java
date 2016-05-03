package com.shoppinglist.app;

import android.support.v7.widget.RecyclerView;

/**
 * Created by r.nalluru on 10/19/15.
 */
public class ListItemTouchHelper extends android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback {

    private static final String LOG_TAG = ListItemTouchHelper.class.getSimpleName();
    //private ItemAdaptor mItemAdaptor;
    private OnItemSwipeListener mItemSwipeListener;
    /**
     * Creates a Callback for the given drag and swipe allowance. These values serve as
     * defaults
     * and if you want to customize behavior per ViewHolder, you can override
     *
     * and / or .
     *
     */
    public ListItemTouchHelper() {
        super(0, android.support.v7.widget.helper.ItemTouchHelper.LEFT | android.support.v7.widget.helper.ItemTouchHelper.RIGHT);
        //this.mItemAdaptor = itemAdaptor;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mItemSwipeListener.onItemSwipe(viewHolder.getLayoutPosition());
    }

    public interface OnItemSwipeListener {
        void onItemSwipe(int position);
    }

    public void SetOnItemClickListener(final OnItemSwipeListener mItemSwipeListener) {
        this.mItemSwipeListener = mItemSwipeListener;
    }
}
