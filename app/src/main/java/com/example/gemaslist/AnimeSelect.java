package com.example.gemaslist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        List<String> dropDownItems = new ArrayList<>();
        dropDownItems.add("Watching");
        dropDownItems.add("Planning");
        dropDownItems.add("Completed");
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(getContext(), R.layout.dropdown_item, dropDownItems);
        AutoCompleteTextView dropDownMenu = view.findViewById(R.id.anime_select_status);
        dropDownMenu.setAdapter(arrayAdapter);

        //set dynamic content
        String animeTitle = "Title";
        String animeStatus = "Planning";
        String animeProgress = String.format(Locale.US, "%d",0);
        String animeRating = String.format(Locale.US,"%d",0);
        boolean favourite = false;
        String animeDescription = getString(R.string.lorem);
        String averageRating = String.format(Locale.US,"%.1f", 0.00);

        MaterialTextView animeTitleView = view.findViewById(R.id.anime_select_title);
        ImageView animeImageView = view.findViewById(R.id.anime_select_image);
        TextInputEditText animeProgressView = view.findViewById(R.id.anime_select_progress);
        TextInputEditText animeRatingView = view.findViewById(R.id.anime_select_rating);
        CheckBox favouriteCheckBoxView = view.findViewById(R.id.anime_select_favourite);
        MaterialTextView animeDescriptionView = view.findViewById(R.id.anime_select_description);
        MaterialTextView averageRatingView = view.findViewById(R.id.anime_select_average_rating);

        animeTitleView.setText(animeTitle);
        animeImageView.setImageResource(R.drawable.placeholder_image);
        dropDownMenu.setText(animeStatus, false);
        animeProgressView.setText(animeProgress);
        animeRatingView.setText(animeRating);
        favouriteCheckBoxView.setChecked(favourite);
        animeDescriptionView.setText(animeDescription);
        averageRatingView.setText(averageRating);

        return view;
    }
}