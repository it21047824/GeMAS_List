package com.RedRose.gemaslist;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import androidx.appcompat.widget.LinearLayoutCompat.LayoutParams;
import androidx.appcompat.widget.LinearLayoutCompat;
import android.view.LayoutInflater;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieStats#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieStats extends Fragment {

    private View view;
    private ArrayList<String> movieIds;
    private ArrayList<String> movieRatings;
    private int allMovies = 0;
    private double movieRatingAvg = 0.0;
    private double totalMovieRating = 0.0;
    private TextView TotalMovies;
    private TextView avgRatings;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MovieStats() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MovieStats.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieStats newInstance(String param1, String param2) {
        MovieStats fragment = new MovieStats();
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
        view = inflater.inflate(R.layout.fragment_movie_stats, container, false);
        LinearLayoutCompat barGraphContainer =
                view.findViewById(R.id.movie_stats_bar_graph_container);
        LinearLayoutCompat barGraphView = view.findViewById(R.id.movie_stats_bar_graph);
        TotalMovies = view.findViewById(R.id.TotalMovies);
        avgRatings = view.findViewById(R.id.avgRatings);
        movieIds = new ArrayList<>();
        movieRatings = new ArrayList<>();
        int [] ratingFrequencies = new int[11];
        // Inflate the layout for this fragment

        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseUtil.getDB()
                .getReference(FirebaseUtil.USERDATA).child(uid).child("movies");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d: snapshot.getChildren()){
                    movieIds.add(d.getKey());
                    movieRatings.add(d.child("rating").getValue(String.class));
                }
                allMovies = movieIds.size();
                for(String s:movieRatings){
                    try {
                        totalMovieRating +=Integer.parseInt(s);
                        switch(s){
                            case "0":
                                ratingFrequencies[0]++;
                                break;
                            case "1":
                                ratingFrequencies[1]++;
                                break;
                            case "2":
                                ratingFrequencies[2]++;
                                break;
                            case "3":
                                ratingFrequencies[3]++;
                                break;
                            case "4":
                                ratingFrequencies[4]++;
                                break;
                            case "5":
                                ratingFrequencies[5]++;
                                break;
                            case "6":
                                ratingFrequencies[6]++;
                                break;
                            case "7":
                                ratingFrequencies[7]++;
                                break;
                            case "8":
                                ratingFrequencies[8]++;
                                break;
                            case "9":
                                ratingFrequencies[9]++;
                                break;
                            case "10":
                                ratingFrequencies[10]++;
                                break;
                        }
                    }catch (Exception e){
                        Log.d("MovieStats 104" , e.getMessage());
                    }

                }
                TotalMovies.setText(""+allMovies);
                avgRatings.setText(""+totalMovieRating);

                AnimeStats.createBarGraph(barGraphContainer,barGraphView,ratingFrequencies,R.color.main_theme,1.5f);
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

        view.requestLayout();
    }
}