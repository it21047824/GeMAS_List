package com.example.gemaslist;

import android.os.Parcel;
import android.os.Parcelable;

public class CustomLinkList implements Parcelable {
    private AnimeDataEntry first;

    public CustomLinkList() {
        first = null;
    }

    protected CustomLinkList(Parcel in) {
    }

    public static final Creator<CustomLinkList> CREATOR = new Creator<>() {
        @Override
        public CustomLinkList createFromParcel(Parcel in) {
            return new CustomLinkList(in);
        }

        @Override
        public CustomLinkList[] newArray(int size) {
            return new CustomLinkList[size];
        }
    };

    public void addItem(AnimeDataEntry input) {
        if(first == null) {
            first = input;
        }
        AnimeDataEntry temp = first;
        while(temp != null) {
            if(temp.rating < input.rating){
                if(temp == first){
                    first = input;
                }
                input.prev = temp.prev;
                input.next = temp;
                temp.prev = input;
            } else if (temp.rating > input.rating) {
                temp = temp.next;
            } else {
                input.prev = temp;
                input.next = temp.next;
                temp.next = input;
                break;
            }
            temp = temp.next;
        }
    }

    public int size() {
        int size = 0;
        AnimeDataEntry temp = first;
        while(temp != null) {
            size++;
            temp = temp.next;
        }
        return size;
    }

    public AnimeDataEntry getItem(int position) {
        AnimeDataEntry temp = first;
        for(int i=0; i<position; i++){
            temp = temp.next;
        }
        return temp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
