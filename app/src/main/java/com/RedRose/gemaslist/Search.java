package com.RedRose.gemaslist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class Search extends Fragment {
    private View view;
    private LinearLayoutCompat animeLayout, movieLayout, seriesLayout, gameLayout;
    private ArrayList<String> animeTitleIDs;
    private ArrayList<String> movieTitleIDs;
    private ArrayList<String> gameTitleIDs;
    private ArrayList<String> seriesTitleIDs;
    private LinearProgressIndicator searchProgress;

    public Search() {
        // Required empty public constructor
    }

    public static Search newInstance() {
        return new Search();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animeTitleIDs = new ArrayList<>();
        movieTitleIDs = new ArrayList<>();
        seriesTitleIDs = new ArrayList<>();
        gameTitleIDs = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);

        TextInputEditText searchInput = view.findViewById(R.id.search_text_input);
        animeLayout = view.findViewById(R.id.anime_search_result);
        movieLayout = view.findViewById(R.id.movie_search_result);
        seriesLayout = view.findViewById(R.id.series_search_result);
        gameLayout = view.findViewById(R.id.game_search_result);
        searchProgress = view.findViewById(R.id.search_loader);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence,
                                          int i, int i1, int i2) {/*do nothing*/}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getResults(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {/*do nothing*/}
        });

        return view;
    }

    private void getResults(CharSequence input) {
        animeLayout.removeAllViews();
        movieLayout.removeAllViews();
        seriesLayout.removeAllViews();
        gameLayout.removeAllViews();

        animeTitleIDs.clear();
        movieTitleIDs.clear();
        seriesTitleIDs.clear();
        gameTitleIDs.clear();

        if(input.length()>0){
            searchProgress.setVisibility(View.VISIBLE);
            String search = input.toString().toLowerCase(Locale.US);



            //get results
            DatabaseReference animeListRef = FirebaseUtil.getDB()
                    .getReference();
            animeListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DataSnapshot animeSnap = snapshot.child(FirebaseUtil.ANIME_PATH).child("titles");
                    DataSnapshot moviesSnap = snapshot.child(FirebaseUtil.MOVIE_PATH);
                    DataSnapshot seriesSnap = snapshot.child(FirebaseUtil.SERIES_PATH);
                    DataSnapshot gamesSnap = snapshot.child(FirebaseUtil.GAME_PATH);

                    for(DataSnapshot d : animeSnap.getChildren()){
                        if(d.child("title").getValue(String.class) != null) {
                            @SuppressWarnings("ConstantConditions")
                            String title = d.child("title").getValue(String.class).toLowerCase(Locale.US);
                            if(title.contains(search)){
                                animeTitleIDs.add(d.getKey());
                            }
                        }
                    }
                    for(DataSnapshot d : moviesSnap.getChildren()){
                        if(d.child("title").getValue(String.class) != null) {
                            @SuppressWarnings("ConstantConditions")
                            String title = d.child("title").getValue(String.class).toLowerCase(Locale.US);
                            if(title.contains(search)){
                                movieTitleIDs.add(d.getKey());
                            }
                        }
                    }
                    for(DataSnapshot d : seriesSnap.getChildren()){
                        if(d.child("title").getValue(String.class) != null) {
                            @SuppressWarnings("ConstantConditions")
                            String title = d.child("title").getValue(String.class).toLowerCase(Locale.US);
                            if(title.contains(search)){
                                seriesTitleIDs.add(d.getKey());
                            }
                        }
                    }
                    for(DataSnapshot d : gamesSnap.getChildren()){
                        if(d.child("title").getValue(String.class) != null) {
                            @SuppressWarnings("ConstantConditions")
                            String title = d.child("title").getValue(String.class).toLowerCase(Locale.US);
                            if(title.contains(search)){
                                gameTitleIDs.add(d.getKey());
                            }
                        }
                    }
                    //create cards
                    FirebaseStorage st = FirebaseStorage.getInstance();
                    StorageReference anime = st.getReference().child("anime_posters");
                    StorageReference movie = st.getReference().child("movie_posters");
                    StorageReference series = st.getReference().child("series_posters");
                    StorageReference game = st.getReference().child("game_posters");

                    DatabaseReference ref = FirebaseUtil.getDB().getReference();

                    for (int i=0; i < animeTitleIDs.size(); i++){
                        FirebaseUtil.createTitleCard(
                                animeLayout,
                                getContext(),
                                i,
                                ref.child(FirebaseUtil.ANIME_PATH).child("titles"),
                                anime,
                                animeTitleIDs,
                                R.id.action_search_to_animeSelect);
                    }
                    for (int i=0; i < movieTitleIDs.size(); i++){
                        FirebaseUtil.createTitleCard(
                                movieLayout,
                                getContext(),
                                i,
                                ref.child(FirebaseUtil.MOVIE_PATH),
                                movie,
                                movieTitleIDs,
                                R.id.action_search_to_movieDescription);
                    }
                    for (int i=0; i < seriesTitleIDs.size(); i++){
                        FirebaseUtil.createTitleCard(
                                seriesLayout,
                                getContext(),
                                i,
                                ref.child(FirebaseUtil.SERIES_PATH),
                                series,
                                seriesTitleIDs,
                                R.id.action_search_to_series_description);
                    }
                    for (int i=0; i < gameTitleIDs.size(); i++){
                        FirebaseUtil.createTitleCard(
                                gameLayout,
                                getContext(),
                                i,
                                ref.child(FirebaseUtil.GAME_PATH),
                                game,
                                gameTitleIDs,
                                R.id.action_search_to_games_description);
                    }
                    searchProgress.setVisibility(View.INVISIBLE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Search206", error.getMessage());
                }
            });
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        view.requestLayout();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setSubtitle(R.string.search);
        requireActivity().findViewById(R.id.appbar_search_button).setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        requireActivity().findViewById(R.id.appbar_search_button).setVisibility(View.VISIBLE);
    }
}