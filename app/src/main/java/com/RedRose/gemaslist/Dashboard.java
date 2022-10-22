package com.RedRose.gemaslist;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Dashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Dashboard extends Fragment {

    private MainActivity activity;
    private Context context;
    private LinearLayoutCompat layout;
    private LinearProgressIndicator loader;
    private View view;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Dashboard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Dashboard.
     */
    // TODO: Rename and change types and number of parameters
    public static Dashboard newInstance(String param1, String param2) {
        Dashboard fragment = new Dashboard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        context = getContext();
        activity = (MainActivity) context;

        layout = view.findViewById(R.id.show_anime);
        loader = view.findViewById(R.id.loader);
        loader.setVisibility(View.INVISIBLE);

        DatabaseReference animeListRef = FirebaseUtil.getDB()
                .getReference(FirebaseUtil.ANIME_PATH)
                .child("titles");
        animeListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren()){
                    titleIDs.add(d.getKey());
                    Log.e("dashboard107", d.getKey());
                }
                //create cards
                download();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setSubtitle(R.string.dashboard);
    }

    public static final ArrayList<String> titleIDs = new ArrayList<>();

    public static void createAnimeCard(
            MainActivity activity,
            LinearLayoutCompat linearLayoutCompat,
            Context context,
            int position
    ) {
        //create card
        LayoutParams cardLayoutParams =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
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
        DatabaseReference selectedRef = titleRef.child("titles").child(titleIDs.get(position));

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

                long episodes = 0;

                try {
                    //noinspection ConstantConditions
                    episodes = snapshot.child("episodes").getValue(Long.class);
                } catch (Exception e) {
                    //do nothing
                }


                AnimeTitle selectedTitle = new AnimeTitle(snapshot.getKey(),
                        snapshot.child("title").getValue(String.class),
                        snapshot.child("description").getValue(String.class),
                        Math.toIntExact(episodes),
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
                            .navigate(R.id.action_dashboard_to_animeSelect, cardBundle);
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
                .child("anime_posters").child(titleIDs.get(position));

        animeRef.getBytes(FirebaseUtil.ONE_MEGABYTE).addOnSuccessListener
                (bytes -> imageView.setImageBitmap(FirebaseUtil.byteToBitmap(bytes)));

    }


    public void download(){
        int i = 0;
        while (i < titleIDs.size()){

            createAnimeCard(activity, layout, context, i);

            i++;
        }
        loader.setVisibility(View.INVISIBLE);
    }
}