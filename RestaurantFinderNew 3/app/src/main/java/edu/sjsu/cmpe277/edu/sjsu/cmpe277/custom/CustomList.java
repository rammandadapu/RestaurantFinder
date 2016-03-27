package edu.sjsu.cmpe277.edu.sjsu.cmpe277.custom;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by ram.mandadapu on 3/26/16.
 */
public class CustomList extends ArrayList implements Parcelable {

    protected CustomList(Parcel in) {
    }

    public static final Creator<CustomList> CREATOR = new Creator<CustomList>() {
        @Override
        public CustomList createFromParcel(Parcel in) {
            return new CustomList(in);
        }

        @Override
        public CustomList[] newArray(int size) {
            return new CustomList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
