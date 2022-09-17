package com.RedRose.gemaslist;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.LinearLayoutCompat.LayoutParams;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class Search extends Fragment {

    private MainActivity activity;
    private Context context;
    private LinearLayoutCompat layout;
    private LinearProgressIndicator loader;
    private View view;

    public Search() {
        // Required empty public constructor
    }

    public static Search newInstance() {
        return new Search();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);
        context = getContext();
        activity = (MainActivity) context;

        layout = view.findViewById(R.id.search_view);
        loader = view.findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);

        //create cards
        download();

        return view;
    }

    public static final String[] titleIDs= {
            "-NC4RseeJPBok0Xaz0ng",
            "-NC8sUEdaMLIWnKXSkkv",
            "-NC8tjA86I00zq_LV-Mc",
            "-NC8zELRqN_N10FkeOf8",
            "-NC8zvQaFBMHUb2_y2wS",
            "-NC9-SXWMPRDrSzAWgDt",
            "-NC9-qTCKdluCO2S-mBp",
            "-NC90CaPUKfbPhI--bib",
            "-NC90ZVN2FGUFi_--fel",
            "-NC90oHQmzlc64Js7ZY3",
            "-NC9152H1YUVUCamsYNp",
            "-NC91Mwm9SFwrFgLLFp4",
            "-NC91sMFqxppaE0KrpGv"
    };

    public static void createAnimeCard(
            MainActivity activity,
            LinearLayoutCompat linearLayoutCompat,
            Context context,
            int position
    ) {
        //create card
        LayoutParams cardLayoutParams =
                new LinearLayoutCompat.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        cardLayoutParams.setMargins(8,16,16,8);

        MaterialCardView cardView = new MaterialCardView(context);
        cardView.setLayoutParams(cardLayoutParams);
        cardView.setPadding(8,8,8,8);
        cardView.setElevation(5);
        cardView.setClickable(true);
        cardView.setFocusable(true);

        //create title
        LayoutParams textLayoutParams =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        TextView titleTextView = new TextView(context);
        titleTextView.setLayoutParams(textLayoutParams);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        titleTextView.setGravity(Gravity.CENTER);
        titleTextView.setPadding(20,10,5,20);

        //create progress text
        TextView progressTextView = new TextView(context);
        progressTextView.setLayoutParams(textLayoutParams);
        progressTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        progressTextView.setGravity(Gravity.CENTER);
        progressTextView.setPadding(20,5,5,10);

        //create rating text
        TextView ratingTextView = new TextView(context);
        ratingTextView.setLayoutParams(textLayoutParams);
        ratingTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        ratingTextView.setGravity(Gravity.CENTER);
        ratingTextView.setPadding(20,5,5,10);

        //create image
        LayoutParams imageParams = new LayoutParams(
                (int) FirebaseUtil.pxFromDp(context,65),
                (int) FirebaseUtil.pxFromDp(context, 91)
        );

        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        //create text layout
        LinearLayoutCompat textLayout = new LinearLayoutCompat(context);
        textLayout.setLayoutParams(textLayoutParams);
        textLayout.setOrientation(LinearLayoutCompat.VERTICAL);
        textLayout.addView(titleTextView);
        textLayout.addView(progressTextView);
        textLayout.addView(ratingTextView);

        //create card layout
        LayoutParams cardContentParams =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        LinearLayoutCompat cardContent = new LinearLayoutCompat(context);
        cardContent.setLayoutParams(cardContentParams);

        cardContent.addView(imageView);
        cardContent.addView(textLayout);

        cardView.addView(cardContent);

        linearLayoutCompat.addView(cardView);

        DatabaseReference titleRef = FirebaseUtil.getDB().getReference(FirebaseUtil.ANIME_PATH);
        DatabaseReference selectedRef = titleRef.child("titles").child(titleIDs[position]);

        selectedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //calculate global average rating
                DataSnapshot rating = snapshot.child("global_rating");
                float average;
                try {
                    average = (float) Integer.parseInt(Objects.requireNonNull
                            (rating.child("ratings").getValue(String.class)))
                            / Integer.parseInt(Objects.requireNonNull
                            (rating.child("raters").getValue(String.class)));
                } catch (Exception e) {
                    average = 0.0F;
                }


                @SuppressWarnings("ConstantConditions") AnimeTitle selectedTitle = new AnimeTitle(snapshot.getKey(),
                        snapshot.child("title").getValue(String.class),
                        snapshot.child("description").getValue(String.class),
                        Math.toIntExact(snapshot.child("episodes").getValue(Long.class)),
                        snapshot.child("romaji").getValue(String.class),
                        average);

                MainActivity activity = (MainActivity) context;
                Bundle cardBundle = new Bundle();
                cardBundle.putString("title_id", selectedTitle.getAnimeID());

                //set values
                titleTextView.setText(selectedTitle.getAnimeTitle());
                progressTextView.setText("0");
                ratingTextView.setText("0");

                //card view on click listener
                cardView.setOnClickListener((View view) -> {
                    activity.appBarSubtitleHistory.push((String)
                            Objects.requireNonNull(activity.getSupportActionBar()).getSubtitle());

                    Objects.requireNonNull(activity.getSupportActionBar())
                            .setSubtitle(R.string.select);
                    Navigation.findNavController(activity, R.id.nav_host_fragment)
                            .navigate(R.id.action_search_to_animeSelect, cardBundle);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", error.getMessage());
            }
        });

        //get image from cloud storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference animeRef = storage.getReference()
                .child("anime_posters").child(titleIDs[position]);

        animeRef.getBytes(FirebaseUtil.ONE_MEGABYTE).addOnSuccessListener
                (bytes -> imageView.setImageBitmap(FirebaseUtil.byteToBitmap(bytes)));

    }


    public void download(){
        int i = 0;
        while (i<13){

            createAnimeCard(activity, layout, context, i);

            i++;
        }
        loader.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        view.requestLayout();
    }
}