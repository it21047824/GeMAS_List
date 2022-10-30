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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SeriesStats#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SeriesStats extends Fragment {
    private View view;
    private ArrayList<String> seriesId;
    private  ArrayList <String> seriesRatings;
    private int allSeries =0 ;
    private double seriesratingAvg =  0.0;
    private double totalSereisRating = 0.0;
    private TextView TotalSeries, AvgSeries;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SeriesStats() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SeriesStats.
     */
    // TODO: Rename and change types and number of parameters
    public static SeriesStats newInstance(String param1, String param2) {
        SeriesStats fragment = new SeriesStats();
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
        view = inflater.inflate(R.layout.fragment_series_stats, container, false);

        LinearLayoutCompat barGraphContainer =
                view.findViewById(R.id.series_StatbargraphContainer);
        LinearLayoutCompat barGraphView = view.findViewById(R.id.SeriesStatBarGraph);

        TotalSeries = view.findViewById(R.id.SeriesTotal);
        AvgSeries = view.findViewById(R.id.SeriesAVG);
        seriesId = new ArrayList<>();
        seriesRatings = new ArrayList<>();
        int [] ratingFrequency = new int[11];
        //Inflate the layout for this fragment

        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseUtil.getDB()
                .getReference(FirebaseUtil.USERDATA).child(uid).child("series");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot d: snapshot.getChildren()){
                    seriesId.add(d.getKey());
                    seriesRatings.add(d.child("rating").getValue(String.class));

                }
                allSeries = seriesId.size();
                for (String s:seriesRatings){
                    try {
                        totalSereisRating += Integer.parseInt(s);

                        switch (s){
                            case "0":
                                ratingFrequency[0]++;
                                break;
                            case "1":
                                ratingFrequency[1]++;
                                break;
                            case "2":
                                ratingFrequency[2]++;
                                break;
                            case "3":
                                ratingFrequency[3]++;
                                break;
                            case "4":
                                ratingFrequency[4]++;
                                break;
                            case "5":
                                ratingFrequency[5]++;
                                break;
                            case "6":
                                ratingFrequency[6]++;
                                break;
                            case "7":
                                ratingFrequency[7]++;
                                break;
                            case "8":
                                ratingFrequency[8]++;
                                break;
                            case "9":
                                ratingFrequency[9]++;
                                break;
                            case "10":
                                ratingFrequency[10]++;
                                break;
                        }
                    }catch (Exception e){
                        Log.d("Series Stats Not Found" , e.getMessage());
                    }
                }

                TotalSeries.setText(""+ allSeries);
                AvgSeries.setText(""+totalSereisRating);




                //create bar graph method call

                AnimeStats.createBarGraph(barGraphContainer,barGraphView,ratingFrequency,R.color.main_theme,1.5f);



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