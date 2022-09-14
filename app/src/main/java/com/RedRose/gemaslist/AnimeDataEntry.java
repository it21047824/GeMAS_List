package com.RedRose.gemaslist;

import android.os.Parcel;
import android.os.Parcelable;

public class AnimeDataEntry implements Parcelable {
    public int title;
    public int progress;
    public int status;
    public int rating;
    public boolean favourite;
    public AnimeDataEntry prev;
    public AnimeDataEntry next;

    public AnimeDataEntry(int title, int status, int progress, int rating, boolean favourite) {
        this.title = title;
        this.status = status;
        this.progress = progress;
        this.rating = rating;
        this.favourite = favourite;
        prev = null;
        next = null;
    }





    //parcelable implementations
    protected AnimeDataEntry(Parcel in) {
        title = in.readInt();
        progress = in.readInt();
        status = in.readInt();
        rating = in.readInt();
        prev = in.readParcelable(AnimeDataEntry.class.getClassLoader());
        next = in.readParcelable(AnimeDataEntry.class.getClassLoader());
    }

    public static final Creator<AnimeDataEntry> CREATOR = new Creator<>() {
        @Override
        public AnimeDataEntry createFromParcel(Parcel in) {
            return new AnimeDataEntry(in);
        }

        @Override
        public AnimeDataEntry[] newArray(int size) {
            return new AnimeDataEntry[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(title);
        parcel.writeInt(progress);
        parcel.writeInt(status);
        parcel.writeInt(rating);
        parcel.writeParcelable(prev, i);
        parcel.writeParcelable(next, i);
    }
}
