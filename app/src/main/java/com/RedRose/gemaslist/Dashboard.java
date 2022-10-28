package com.RedRose.gemaslist;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class Dashboard extends Fragment {

    private Context context;
    private LinearLayoutCompat layout;
    private LinearProgressIndicator loader;
    private ArrayList<String> animeTitleIDs;
    private ArrayList<String> movieTitleIDs;
    private ArrayList<String> gameTitleIDs;
    private ArrayList<String> seriesTitleIDs;

    public Dashboard() {
        // Required empty public constructor
    }

    public static Dashboard newInstance() {
        return new Dashboard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        context = getContext();
        MainActivity activity = (MainActivity) context;

        layout = view.findViewById(R.id.show_anime);
        loader = view.findViewById(R.id.loader);
        loader.setVisibility(View.INVISIBLE);

        animeTitleIDs = new ArrayList<>();
        movieTitleIDs = new ArrayList<>();
        gameTitleIDs = new ArrayList<>();
        seriesTitleIDs = new ArrayList<>();

        initialize();

        return view;
    }

    private void initialize() {
        DatabaseReference animeListRef = FirebaseUtil.getDB()
                .getReference();
        animeListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot anime = snapshot.child(FirebaseUtil.ANIME_PATH).child("titles");
                DataSnapshot movies = snapshot.child(FirebaseUtil.MOVIE_PATH);
                DataSnapshot series = snapshot.child(FirebaseUtil.SERIES_PATH);
                DataSnapshot games = snapshot.child(FirebaseUtil.GAME_PATH);

                for(DataSnapshot d : anime.getChildren()){
                    animeTitleIDs.add(d.getKey());
                }
                for(DataSnapshot d : movies.getChildren()){
                    movieTitleIDs.add(d.getKey());
                }
                for(DataSnapshot d : series.getChildren()){
                    seriesTitleIDs.add(d.getKey());
                }
                for(DataSnapshot d : games.getChildren()){
                    gameTitleIDs.add(d.getKey());
                }
                //create cards
                download();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("dashboard144", error.getMessage());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setSubtitle(R.string.dashboard);
    }

    public void download(){
        int iterations=1;
        FirebaseStorage st = FirebaseStorage.getInstance();
        StorageReference anime = st.getReference().child("anime_posters");
        StorageReference movie = st.getReference().child("movie_posters");
        StorageReference series = st.getReference().child("series_posters");
        StorageReference game = st.getReference().child("game_posters");

        DatabaseReference ref = FirebaseUtil.getDB().getReference();

        for (int i=0; i<iterations; i++){
            FirebaseUtil.createTitleCard(
                    layout,
                    context,
                    (animeTitleIDs.size()-1-i),
                    ref.child(FirebaseUtil.ANIME_PATH).child("titles"),
                    anime, animeTitleIDs, R.id.action_dashboard_to_animeSelect);
        }
        for (int i=0; i<iterations; i++){
            FirebaseUtil.createTitleCard(
                    layout,
                    context,
                    (movieTitleIDs.size()-1-i),
                    ref.child(FirebaseUtil.MOVIE_PATH),
                    movie, movieTitleIDs, R.id.action_dashboard_to_movieDescription);
        }
        for (int i=0; i<iterations; i++){
            FirebaseUtil.createTitleCard(
                    layout,
                    context,
                    (seriesTitleIDs.size()-1-i),
                    ref.child(FirebaseUtil.SERIES_PATH),
                    series, seriesTitleIDs, R.id.action_dashboard_to_series_description);
        }
        for (int i=0; i<iterations; i++){
            FirebaseUtil.createTitleCard(
                    layout,
                    context,
                    (gameTitleIDs.size()-1-i),
                    ref.child(FirebaseUtil.GAME_PATH),
                    game, gameTitleIDs, R.id.action_dashboard_to_games_description);
        }
        loader.setVisibility(View.INVISIBLE);
    }
}