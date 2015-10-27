package com.shoppinglist.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ${} on 10/20/15.
 */
public class Item implements Parcelable {
    private int id;
    private long array_id;
    private String name;
    private String description;

    public Item(){}

    public Item(int id, long array_id, String name, String description) {
        this.id = id;
        this.array_id = array_id;
        this.name = name;
        this.description = description;
    }

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Item(long array_id, String name, String description) {
        this.array_id = array_id;
        this.name = name;
        this.description = description;
    }

    public Item(Parcel parcel) {
        this.id = parcel.readInt();
        this.name = parcel.readString();
        this.description = parcel.readString();
    }

    public int getId() { return id; }

    public long getArrayId() { return array_id; }

    public void setId(int id) { this.id = id; }

    public void setArrayId(long array_id) { this.array_id = array_id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeString(getName());
        dest.writeString(getDescription());
    }

    public static final Parcelable.Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
