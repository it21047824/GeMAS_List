package com.RedRose.gemaslist;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieUserData {
    private static MovieUserData data;
    private CustomLinkList watchingList;
    private CustomLinkList planningList;
    private CustomLinkList completedList;

    private MovieUserData(){
        watchingList = new CustomLinkList();
        planningList = new CustomLinkList();
        completedList = new CustomLinkList();
    }

    public static boolean dataInitialized(){
        return data != null;
    }

    public static MovieUserData getMovieUserData() {
        if(data == null) {
            synchronized (MovieUserData.class){
                data = new MovieUserData();
            }
        }
        return data;
    }

    public AnimeDataEntry find(String title) {
        AnimeDataEntry temp = watchingList.find(title);
        if(temp == null) {
            temp = planningList.find(title);
            if(temp == null){
                temp = completedList.find(title);
            }
        }
        return temp;
    }

    public boolean remove(String title) {
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

    public void setWatchingList(CustomLinkList watchingList) {
        this.watchingList = watchingList;
    }

    public void setPlanningList(CustomLinkList planningList) {
        this.planningList = planningList;
    }

    public void setCompletedList(CustomLinkList completedList) {
        this.completedList = completedList;
    }

    public static boolean JSONStringToUserdata(String json) {
        JSONObject retrievedData;
        JSONArray dataArray;
        MovieUserData userData = MovieUserData.getMovieUserData();
        CustomLinkList watching = userData.getWatchingList();
        CustomLinkList planning = userData.getPlanningList();
        CustomLinkList completed = userData.getCompletedList();

        try {
            retrievedData = new JSONObject(json);
            dataArray = retrievedData.getJSONArray("DATA");

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject obj = dataArray.getJSONObject(i);
                String title = obj.getString("TITLE");
                int status = obj.getInt("STATUS");
                int rating = obj.getInt("RATING");
                int progress = obj.getInt("PROGRESS");
                boolean favourite = obj.getBoolean("FAVOURITE");

                switch (status) {
                    case FirebaseUtil.WATCHING:
                        watching.addItem(new AnimeDataEntry(title, status, progress, rating, favourite));
                        break;
                    case FirebaseUtil.PLANNING:
                        planning.addItem(new AnimeDataEntry(title, status, progress, rating, favourite));
                        break;
                    case FirebaseUtil.COMPLETED:
                        completed.addItem(new AnimeDataEntry(title, status, progress, rating, favourite));
                        break;
                }
            }
            return true;
        } catch (JSONException e) {
            Log.e("Firebase", e.getMessage());
        }
        return false;

    }


    public static String userdataToJSON() {
        MovieUserData currentData = MovieUserData.getMovieUserData();
        JSONArray dataArray = new JSONArray();

        CustomLinkList watching = currentData.getWatchingList();
        CustomLinkList planning = currentData.getPlanningList();
        CustomLinkList completed = currentData.getCompletedList();

        //get all data from watching list
        for(int i=0; i<watching.size(); i++){
            JSONObject obj = new JSONObject();
            try {
                obj.put("TITLE", watching.getItem(i).title);
                obj.put("STATUS", watching.getItem(i).status);
                obj.put("RATING", watching.getItem(i).rating);
                obj.put("PROGRESS", watching.getItem(i).progress);
                obj.put("FAVOURITE", watching.getItem(i).favourite);
                dataArray.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //get all data from planning list
        for(int i=0; i<planning.size(); i++){
            JSONObject obj = new JSONObject();
            try {
                obj.put("TITLE", planning.getItem(i).title);
                obj.put("STATUS", planning.getItem(i).status);
                obj.put("RATING", planning.getItem(i).rating);
                obj.put("PROGRESS", planning.getItem(i).progress);
                obj.put("FAVOURITE", planning.getItem(i).favourite);
                dataArray.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //get all data from completed list
        for(int i=0; i<completed.size(); i++){
            JSONObject obj = new JSONObject();
            try {
                obj.put("TITLE", completed.getItem(i).title);
                obj.put("STATUS", completed.getItem(i).status);
                obj.put("RATING", completed.getItem(i).rating);
                obj.put("PROGRESS", completed.getItem(i).progress);
                obj.put("FAVOURITE", completed.getItem(i).favourite);
                dataArray.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Azure", e.getMessage());
            }
        }

        JSONObject finalData = new JSONObject();
        String finalDataString = null;
        try {
            finalData.put("DATA", dataArray);
            finalDataString = finalData.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return finalDataString;
    }

}
