package com.shoppinglist.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by r.nalluru on 10/23/15.
 */
public class Store implements Parcelable {
    private int id;
    private String name;

    public Store() {}

    public Store(String name) {
        this.name = name;
    }

    public Store(int id, String name) {
        this.id = id;
        this.name = name;
    }

    protected Store(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<Store> CREATOR = new Creator<Store>() {
        @Override
        public Store createFromParcel(Parcel in) {
            return new Store(in);
        }

        @Override
        public Store[] newArray(int size) {
            return new Store[size];
        }
    };

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeString(getName());
    }
}
