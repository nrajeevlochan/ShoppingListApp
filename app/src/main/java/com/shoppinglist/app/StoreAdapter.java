package com.shoppinglist.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shoppinglist.R;

import java.util.ArrayList;

/**
 * Created by r.nalluru on 10/23/15.
 */
public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    private static final String LOG_TAG = StoreAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<Store> storeList;
    //private HashMap<String, ArrayList<Item>> storeListItems;
    private int rowLayout;
    //private static OnItemLongClickListener mItemLongClickListener;
    private static OnItemClickListener mItemClickListener;

    public StoreAdapter(Context context, int rowLayout, ArrayList<Store> storeList) {
        this.context = context;
        this.rowLayout = rowLayout;
        this.storeList = storeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (storeList != null && !storeList.isEmpty()) {
            Log.d("StoreAdapter", "onBindViewHolder " + holder.storeName);
            holder.storeName.setText(storeList.get(position).getName());
        }
    }

    @Override
    public int getItemCount() {
        return (storeList==null) ? 0 : storeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        public TextView storeName;

        public ViewHolder(View storeView) {
            super(storeView);
            storeName = (TextView) storeView.findViewById(R.id.store_name);
            storeView.setOnLongClickListener(this);
            storeView.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            mItemClickListener.onItemLongClick(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View v) {
            Log.d("StoreAdapter", "What happend...?");
            mItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemLongClick(int position);
        void onItemClick(int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;

    }
}
