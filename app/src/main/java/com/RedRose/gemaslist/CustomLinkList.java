package com.RedRose.gemaslist;

import android.util.Log;

public class CustomLinkList {
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
                        Log.e("customlink 23", temp.prev==null? "prev is first":"prev is not first");
                        input.next = temp;
                        if(temp.prev != null){
                            input.prev = temp.prev;
                            temp.prev.next = input;
                        } else {
                            first = input;
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

    public AnimeDataEntry find(String title) {
        AnimeDataEntry temp = first;
        while(temp!=null){
            if(temp.title.equals(title)){
                return temp;
            }
            temp = temp.next;
        }
        return null;
    }

    public boolean removeItem(String title) {
        synchronized (LOCK){
            AnimeDataEntry temp = first;
            while(temp!=null){
                if(temp.title.equals(title)){
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
}
