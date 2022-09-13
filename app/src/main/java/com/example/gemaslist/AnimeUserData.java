package com.example.gemaslist;

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

    public static AnimeUserData getAnimeUserData() {
        if(data == null) {
            synchronized (AnimeUserData.class){
                data = new AnimeUserData();
            }
        }
        return data;
    }

    public AnimeDataEntry find(int title) {
        AnimeDataEntry temp = watchingList.find(title);
        if(temp == null) {
            temp = planningList.find(title);
            if(temp == null){
                temp = completedList.find(title);
            }
        }
        return temp;
    }

    public boolean remove(int title) {
        boolean temp = false;
        if(watchingList.size()>0){
            temp = watchingList.removeItem(title);
        }
        if(!temp) {
            if(planningList.size()>0){
                temp = planningList.removeItem(title);
            }
        }
        if(!temp){
            if(completedList.size()>0){
                temp = completedList.removeItem(title);
            }
        }
        return temp;
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
