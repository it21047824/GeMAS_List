package com.example.gemaslist;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AnimeWatching extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    private Context context;
    private LinearLayoutCompat linearLayoutCompat;


    public AnimeWatching() {
        // Required empty public constructor
    }


    public static AnimeWatching newInstance(String param1, String param2) {
        AnimeWatching fragment = new AnimeWatching();
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
        View view = inflater.inflate(R.layout.fragment_anime_watching, container, false);

        context = getContext();
        linearLayoutCompat = view.findViewById(R.id.watching_card_container);

        Activity activity = (Activity) context;
        for(int i=0; i<10; i++) {
            AnimeList.createAnimeCard(
                    activity,
                    linearLayoutCompat,
                    context,
                    "Static Title "+(i+1),
                    "Progress : ",
                    "Rating : ",
                    getResources().getColor(R.color.teal_200)
            );
        }

        return view;
    }

}