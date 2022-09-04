package com.example.gemaslist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class AnimeSelect extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public AnimeSelect() {
        // Required empty public constructor
    }

    public static AnimeSelect newInstance(String param1, String param2) {
        AnimeSelect fragment = new AnimeSelect();
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
        View view = inflater.inflate(R.layout.fragment_anime_select, container, false);

        //set dropdown menu items
        String[] dropDownItems = new String[]{"Watching", "Planning", "Completed"};
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(getContext(), R.layout.dropdown_item, dropDownItems);
        AutoCompleteTextView dropDownMenu = view.findViewById(R.id.anime_select_status);
        dropDownMenu.setAdapter(arrayAdapter);

        return view;
    }
}