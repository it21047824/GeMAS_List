package com.example.gemaslist;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AnimePlanning extends Fragment {

    private View view;

    public AnimePlanning() {
        // Required empty public constructor
    }

    public static AnimePlanning newInstance() {
        return new AnimePlanning();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_anime_planning, container, false);

        Context context = getContext();
        LinearLayoutCompat linearLayoutCompat = view.findViewById(R.id.planning_card_container);

        MainActivity activity = (MainActivity) context;

        //get theme color
        TypedValue typedValue = new TypedValue();
        int color = 0;

        if (getContext() != null){
            TypedArray a = getContext().obtainStyledAttributes(typedValue.data, new int[]{R.attr.animePlanning});

            color = a.getColor(0,0);

            a.recycle();
        }

        for(int i=0; i<10; i++) {
            AnimeList.createAnimeCard(
                    activity,
                    linearLayoutCompat,
                    context,
                    "Static Title "+(i+1),
                    "Progress : ",
                    "Rating : ",
                    color
            );
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //fix variable length in viewpager 2
        view.requestLayout();
    }

}