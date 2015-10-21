package com.shoppinglistapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by r.nalluru on 10/20/15.
 */
public class ItemAdaptor extends RecyclerView.Adapter<ItemAdaptor.ViewHolder> {

    private Context context;
    private String[] items;
    private String[] descriptions;
    private int rowLayout;
    OnItemClickListener mItemClickListener;

    public ItemAdaptor(Context context, int rowLayout, String[] items, String[] description) {
        this.context = context;
        this.rowLayout = rowLayout;
        this.items = items;
        this.descriptions = description;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemName.setText(items[position]);
        holder.itemDescription.setText(descriptions[position]);


    }

    @Override
    public int getItemCount() {
        return items.length;
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
                mItemClickListener.onItemClick(v, getPosition());
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
