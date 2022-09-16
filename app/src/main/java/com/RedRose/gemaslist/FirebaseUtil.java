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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class FirebaseUtil {
    private static final String URL = "https://gemas-list-1662485803384-default-rtdb" +
            ".asia-southeast1.firebasedatabase.app/";
    public static final long ONE_MEGABYTE = 1024*1024;
    private static final String USERDATA = "userdata";
    private static final String ANIME_PATH = "anime_titles";
    private static final String TAG = "FirebaseUtil";
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

    //TODO: get anime title
    public static void getAnimeTitle(String titleID, Context context, LinearLayoutCompat layout) {
        DatabaseReference titleRef = getDB().getReference(ANIME_PATH);
        DatabaseReference selectedRef = titleRef.child("titles").child(titleID);
        Log.e("Firebase", "looking for " +selectedRef.getKey());

        selectedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("Firebase", "found " +snapshot.getKey());
                //get image from cloud storage
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference animeRef = storage.getReference()
                        .child("anime_posters").child(titleID);

                final Bitmap[] image = new Bitmap[1];

                animeRef.getBytes(ONE_MEGABYTE).addOnSuccessListener
                        (bytes -> image[0] = byteToBitmap(bytes));

                //calculate global average rating
                DataSnapshot rating = snapshot.child("global_rating");
                float average;
                try {
                    average = (float) Integer.parseInt(Objects.requireNonNull
                            (rating.child("ratings").getValue(String.class)))
                            / Integer.parseInt(Objects.requireNonNull
                            (rating.child("raters").getValue(String.class)));
                } catch (Exception e) {
                    //TODO: change value after testing
                    average = -11.0F;
                }


                AnimeTitle selectedTitle = new AnimeTitle(snapshot.getKey(),
                        snapshot.child("title").getValue(String.class),
                        snapshot.child("description").getValue(String.class),
                        image[0],
                        Math.toIntExact(snapshot.child("episodes").getValue(Long.class)),
                        snapshot.child("romaji").getValue(String.class),
                        average);
                Log.e("Firebase", "downloaded -> "+snapshot.getKey());

                MainActivity activity = (MainActivity) context;
                Bundle bundle = new Bundle();
                bundle.putString("title_id", selectedTitle.getAnimeID());
                Search.createAnimeCard(activity, layout, context, selectedTitle.getAnimeTitle(), "12", "5", selectedTitle.getPoster(), bundle);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", error.getMessage());
            }
        });
    }


    //TODO: get anime user data
    public static boolean getAnimeUserData() {
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = getDB().getReference(USERDATA);


        ValueEventListener animeUserDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AnimeUserData currentData = AnimeUserData.getAnimeUserData();
                AnimeUserData updatedData = snapshot.getValue(AnimeUserData.class);
                if (updatedData != null){
                    currentData.setWatchingList(updatedData.getWatchingList());
                    currentData.setPlanningList(updatedData.getPlanningList());
                    currentData.setCompletedList(updatedData.getCompletedList());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, error.getMessage());
            }
        };

        if (uid != null) {
            reference.child("anime").child(uid).addValueEventListener(animeUserDataListener);
            return true;
        }
        return false;
    }

    //TODO: save anime user data
    public static void  saveAnimeUserData() {
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = getDB().getReference(USERDATA);

        //get current data
        if (uid != null) {
            AnimeUserData userData = AnimeUserData.getAnimeUserData();
            reference.child("anime").child(uid).setValue(userData);
        }
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
}
