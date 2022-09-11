package com.example.gemaslist;

import java.util.ArrayList;

public class AnimeUserData {

    private static AnimeUserData data;
    private CustomLinkList watchingList;
    private CustomLinkList planningList;
    private CustomLinkList completedList;

    private AnimeUserData(){
        watchingList = new CustomLinkList();
        planningList = new CustomLinkList();
        completedList = new CustomLinkList();
    }

    public static synchronized AnimeUserData getAnimeUserData() {
        if(data == null) {
            data = new AnimeUserData();
        }
        return data;
    }

    public void setWatchingList(CustomLinkList watchingList) {
        this.watchingList = watchingList;
    }

    public void setPlanningList(CustomLinkList planningList) {
        this.planningList = planningList;
    }

    public void setCompletedList(CustomLinkList completedList) {
        this.completedList = completedList;
    }

    public CustomLinkList getWatchingList() {
        return watchingList;
    }

    public CustomLinkList getPlanningList() {
        return planningList;
    }

    public CustomLinkList getCompletedList() {
        return completedList;
    }
}
