package com.shoppinglistapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by r.nalluru on 10/20/15.
 */
public class ItemAdaptor extends RecyclerView.Adapter<ItemAdaptor.ViewHolder> {

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView itemName;
        public TextView itemDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            itemDescription = (TextView)itemView.findViewById(R.id.item_desc);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getLayoutPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
