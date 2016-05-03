package com.shoppinglist.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shoppinglist.R;

import java.util.ArrayList;

/**
 * Created by r.nalluru on 10/20/15.
 */
public class ItemAdaptor extends RecyclerView.Adapter<ItemAdaptor.ViewHolder> {

    private static final String LOG_TAG = ItemAdaptor.class.getSimpleName();
    private Context context;
    private ArrayList<Item> itemset;
    private int rowLayout;
    OnItemClickListener mItemClickListener;

    public ItemAdaptor(Context context, int rowLayout, ArrayList<Item>itemset) {
        this.context = context;
        this.rowLayout = rowLayout;
        this.itemset = itemset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (itemset != null && !itemset.isEmpty()) {
            holder.itemName.setText(itemset.get(position).getName());
            holder.itemDescription.setText(itemset.get(position).getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return (itemset == null) ? 0 : itemset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView itemName;
        public TextView itemDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            itemDescription = (TextView)itemView.findViewById(R.id.item_desc);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Action");
            MenuItem myEditAction = menu.add(0, 0, 0, "Edit Item");
            myEditAction.setOnMenuItemClickListener(mOnEditActionClickListener);
            MenuItem myDeleteAction = menu.add(0, 1, 1, "delete Item");
            myDeleteAction.setOnMenuItemClickListener(mOnDeleteActionClickListener);
        }

        private final MenuItem.OnMenuItemClickListener mOnEditActionClickListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(mItemClickListener != null) {
                    mItemClickListener.onItemClick(item.getItemId(), getLayoutPosition());
                }
                return true;
            }
        };

        private final MenuItem.OnMenuItemClickListener mOnDeleteActionClickListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(mItemClickListener != null) {
                    mItemClickListener.onItemClick(item.getItemId(), getLayoutPosition());
                }
                return true;
            }
        };
    }

    public interface OnItemClickListener {
        void onItemClick(int actionType, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
