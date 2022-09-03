package com.example.gemaslist;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AnimeCompleted extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context context;
    private LinearLayoutCompat linearLayoutCompat;

    public AnimeCompleted() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AnimeCompleted newInstance(String param1, String param2) {
        AnimeCompleted fragment = new AnimeCompleted();
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
        View view = inflater.inflate(R.layout.fragment_anime_completed, container, false);

        context = getContext();
        linearLayoutCompat = view.findViewById(R.id.completed_card_container);

        for(int i=0; i<10; i++) {
            AnimeList.createAnimeCard(
                    linearLayoutCompat,
                    context,
                    "Static Title "+(i+1),
                    getString(R.string.progress),
                    getString(R.string.rating),
                    getResources().getColor(R.color.completed)
            );
        }

        return view;
    }
}