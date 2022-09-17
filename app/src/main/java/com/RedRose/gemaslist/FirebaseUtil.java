package com.RedRose.gemaslist;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class FirebaseUtil {
    private static final String URL = "https://gemas-list-1662485803384-default-rtdb" +
            ".asia-southeast1.firebasedatabase.app/";
    public static final long ONE_MEGABYTE = 1024*1024;
    public static final String USERDATA = "userdata";
    public static final String ANIME_PATH = "anime_titles";
    private static final String TAG = "FirebaseUtil";
    public static final int WATCHING = 0;
    public static final int PLANNING = 1;
    public static final int COMPLETED = 2;
    private static FirebaseDatabase firebaseDatabase;

    private FirebaseUtil () {
        // empty
    }

    public static FirebaseDatabase getDB() {
        if(firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance(URL);
        }
        return firebaseDatabase;
    }

    public static boolean addNewAnimeTitle(String title,
                                           String description,
                                           Uri croppedURI,
                                           int episodes,
                                           String romaji,
                                           Context context) {
        DatabaseReference reference = getDB().getReference(ANIME_PATH);
        DatabaseReference pushRef = reference.child("titles").push();
        pushRef.child("title").setValue(title);
        pushRef.child("description").setValue(description);
        pushRef.child("episodes").setValue(episodes);
        pushRef.child("romaji").setValue(romaji);
        pushRef.child("global_rating").child("raters").setValue(0);
        pushRef.child("global_rating").child("ratings").setValue(0);

        if (pushRef.getKey() != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference animeRef = storage.getReference().child("anime_posters");
            StorageReference uploadRef = animeRef.child(pushRef.getKey());
            uploadRef.putFile(croppedURI);
            Activity activity = (Activity) context;
            activity.runOnUiThread(() -> Toast.makeText(context,
                    "Successful", Toast.LENGTH_SHORT).show());
        } else {
            return false;
        }

        return true;
    }


    public static void getAnimeTitle(String titleID, Context context, LinearLayoutCompat layout) {

    }


    public static boolean getAnimeUserData() {
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = getDB().getReference(USERDATA);


        ValueEventListener animeUserDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AnimeUserData.clearData();
                String json = snapshot.getValue(String.class);
                if (json != null){
                    JSONStringToUserdata(json);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, error.getMessage());
            }
        };

        if (uid != null) {
            reference.child(uid).child("anime")
                    .addListenerForSingleValueEvent(animeUserDataListener);
            return true;
        }
        return false;
    }






    //util methods
    //code snippet from
    //https://colinyeoh.wordpress.com/2012/05/18/android-convert-image-uri-to-byte-array/
    public static byte[] uriToBytes(Context context, Uri uri) {
        InputStream inputStream;
        byte[] data = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            data = outputStream.toByteArray();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
    //end of code snippet

    public static Bitmap byteToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static float pxFromDp(Context context, float dip) {
        Resources resources = context.getResources();
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                resources.getDisplayMetrics());
    }

    public static String userdataToJSON() {
        AnimeUserData currentData = AnimeUserData.getAnimeUserData();
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

    public static boolean JSONStringToUserdata(String json) {
        JSONObject retrievedData;
        JSONArray dataArray;
        AnimeUserData userData = AnimeUserData.getAnimeUserData();
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
}
