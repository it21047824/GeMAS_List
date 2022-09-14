package com.RedRose.gemaslist;

import android.os.Parcel;
import android.os.Parcelable;

public class CustomLinkList implements Parcelable {
    private static final Object LOCK = new Object();
    private AnimeDataEntry first;

    public CustomLinkList() {
        first = null;
    }

    public boolean addItem(AnimeDataEntry input) {
        synchronized (LOCK){
            if(first == null) {
                first = input;
                return true;
            } else {
                AnimeDataEntry temp = first;
                while(true) {
                    if(input.rating > temp.rating){
                        input.prev = temp.prev;
                        input.next = temp;
                        if(temp.prev != null){
                            temp.prev.next = input;
                        }
                        temp.prev = input;

                        return true;
                    } else {
                        if(temp.next == null){
                            input.prev = temp;
                            temp.next = input;
                            return true;
                        } else {
                            temp = temp.next;
                        }
                    }
                }
            }
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
        if(first != null) {
            AnimeDataEntry temp = first;
            for(int i=0; i<position; i++){
                temp = temp.next;
            }
            return temp;
        }
        return null;
    }

    public AnimeDataEntry find(int title) {
        AnimeDataEntry temp = first;
        while(temp!=null){
            if(temp.title == title){
                return temp;
            }
            temp = temp.next;
        }
        return null;
    }

    public boolean removeItem(int title) {
        synchronized (LOCK){
            AnimeDataEntry temp = first;
            while(temp!=null){
                if(temp.title == title){
                    if(temp.prev != null){
                        temp.prev.next = temp.next;
                    } else {
                        first = temp.next;
                    }
                    if(temp.next != null) {
                        temp.next.prev = temp.prev;
                    }
                    temp.prev = null;
                    temp.next = null;
                    return true;
                }
                temp = temp.next;
            }
            return false;
        }
    }




    //parcelable implementations
    protected CustomLinkList(Parcel in) {
        in.recycle();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
