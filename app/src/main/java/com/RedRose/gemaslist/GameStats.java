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
 * Use the {@link GameStats#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameStats extends Fragment {
    private View view;
    private ArrayList<String> gameIds;
    private ArrayList<String> gameRatings;
    private int allgame = 0;
    private double gameRatingAvg = 0.0;
    private double totalgameRating = 0.0;
    private TextView Totalgame;
    private TextView avgRatings;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GameStats() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GameStats.
     */
    // TODO: Rename and change types and number of parameters
    public static GameStats newInstance(String param1, String param2) {
        GameStats fragment = new GameStats();
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
        view = inflater.inflate(R.layout.fragment_game_stats, container, false);

        LinearLayoutCompat barGraphContainer =
                view.findViewById(R.id.game_stats_bar_graph_container);
        LinearLayoutCompat barGraphView = view.findViewById(R.id.movie_stats_bar_graph);
        Totalgame = view.findViewById(R.id.Totalgames);
        avgRatings = view.findViewById(R.id.avgRatings);
        gameIds = new ArrayList<>();
        gameRatings = new ArrayList<>();
        int [] ratingFrequencies = new int[11];
        // Inflate the layout for this fragment

        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseUtil.getDB()
                .getReference(FirebaseUtil.USERDATA).child(uid).child("games");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d: snapshot.getChildren()){
                    gameIds.add(d.getKey());
                    gameRatings.add(d.child("rating").getValue(String.class));
                }
                allgame = gameIds.size();
                for(String s:gameRatings){
                    try {
                        totalgameRating +=Integer.parseInt(s);
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
                        Log.d("gameStats 104" , e.getMessage());
                    }

                }
                Totalgame.setText(""+allgame);
                avgRatings.setText(""+totalgameRating);

                createBarGraph(barGraphContainer,barGraphView,ratingFrequencies,R.color.main_theme,1.5f);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    public static void createBarGraph(
            LinearLayoutCompat container,
            @NonNull LinearLayoutCompat layout,
            int[] data,
            int barColor,
            float scale
    ) {

        layout.post( () -> {
            //get info
            int width = layout.getWidth();
            int height = layout.getHeight();

            int bars = data.length;
            int totalFrequency = 0;

            for (int frequency : data){
                totalFrequency += frequency;
            }

            //width of a single bar
            float barWidth = (float) (width-bars-(10*bars))/bars;

            //list of bar heights
            List<Float> barHeightList = new ArrayList<>();
            for (int frequency : data) {
                barHeightList.add(((float)frequency/totalFrequency)*height*scale);
            }

            //create a layout to put the graph
            LinearLayoutCompat barGraphLayout = new LinearLayoutCompat(layout.getContext());

            LinearLayoutCompat.LayoutParams graphLayoutParams =
                    new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
            barGraphLayout.setLayoutParams(graphLayoutParams);
            barGraphLayout.setPadding(8,8,8,0);
            barGraphLayout.setOrientation(LinearLayoutCompat.HORIZONTAL);
            barGraphLayout.setGravity(Gravity.BOTTOM);

            //create a layout for graph legend
            LinearLayoutCompat barGraphLegend = new LinearLayoutCompat(container.getContext());

            LinearLayoutCompat.LayoutParams graphLegendParams =
                    new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            barGraphLegend.setLayoutParams(graphLegendParams);
            barGraphLegend.setPadding(8,0,8,0);
            barGraphLegend.setOrientation(LinearLayoutCompat.HORIZONTAL);
            barGraphLegend.setGravity(Gravity.CENTER);

            //add bars to created layout
            int index = 0;
            for (float barHeight : barHeightList) {
                LinearLayoutCompat bar = new LinearLayoutCompat(layout.getContext());
                TextView legend = new TextView(container.getContext());

                LinearLayoutCompat.LayoutParams barLayout = new LinearLayoutCompat.LayoutParams((int) barWidth, (int) barHeight);
                barLayout.setMargins(5,0,5,0);
                LinearLayoutCompat.LayoutParams legendLayout = new LinearLayoutCompat.LayoutParams((int) barWidth, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                legendLayout.setMargins(5,0,5,0);

                bar.setLayoutParams(barLayout);
                bar.setBackgroundColor(barColor);

                legend.setLayoutParams(legendLayout);
                legend.setText(String.format(Locale.US, "%d", index));
                legend.setTextSize(12);
                legend.setGravity(Gravity.CENTER);

                barGraphLayout.addView(bar);
                barGraphLegend.addView(legend);

                index++;
            }

            //create divider
            LinearLayoutCompat divider = new LinearLayoutCompat(layout.getContext());
            LinearLayoutCompat.LayoutParams dividerLayoutParams =
                    new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1);
            divider.setLayoutParams(dividerLayoutParams);

            TypedValue typedValue = new TypedValue();
            TypedArray a = layout.getContext().obtainStyledAttributes(typedValue.data,
                    new int[]{R.attr.customColor1});
            int dividerColor = a.getColor(0,0);
            a.recycle();

            divider.setBackgroundColor(dividerColor);

            //add graph to given layout
            layout.addView(barGraphLayout);
            container.addView(divider);
            container.addView(barGraphLegend);

        });


    }

    @Override
    public void onStart() {
        super.onStart();

        view.requestLayout();
    }
}