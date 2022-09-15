package com.RedRose.gemaslist;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AnimeSelect extends Fragment {

    private MaterialTextView averageRatingView, animeTitleView, animeDescriptionView, romanjiView;
    private ImageView animeImageView;
    private TextInputEditText animeProgressView, animeRatingView;
    private CheckBox favouriteCheckBoxView;
    private LinearProgressIndicator loadingIndicator;
    private int title_id;
    private AnimeDataEntry dataEntry;
    private AnimeUserData userData;
    private TextInputLayout progressLayout, ratingLayout, statusLayout;
    private String animeStatus;
    private int animeProgress, animeRating;
    private boolean favourite;
    private AnimeTitle title;
    private SharedPreferences sp;
    private AutoCompleteTextView dropDownMenu;

    public AnimeSelect() {
        // Required empty public constructor
    }

    public static AnimeSelect newInstance() {
        return new AnimeSelect();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = requireContext().getSharedPreferences("Login", Context.MODE_PRIVATE);

        animeStatus = null;
        animeProgress = -1;
        animeRating = -1;
        favourite = false;

        if(getArguments() != null){
            title_id = getArguments().getInt("title_id");
            userData = AnimeUserData.getAnimeUserData();
            dataEntry = userData.find(title_id);
            if(dataEntry != null){
                switch (dataEntry.status){
                    case Azure.WATCHING:
                        animeStatus = "Watching";
                        break;
                    case Azure.PLANNING:
                        animeStatus = "Planning";
                        break;
                    case Azure.COMPLETED:
                        animeStatus = "Completed";
                        break;
                }
                animeProgress = dataEntry.progress;
                animeRating = dataEntry.rating;
                favourite = dataEntry.favourite;
            } else {
                dataEntry = new AnimeDataEntry(title_id, -1, -1, -1, false);
            }
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
        dropDownMenu = view.findViewById(R.id.anime_select_status);
        dropDownMenu.setAdapter(arrayAdapter);

        animeTitleView = view.findViewById(R.id.anime_select_title);
        animeImageView = view.findViewById(R.id.anime_select_image);
        animeProgressView = view.findViewById(R.id.anime_select_progress);
        animeRatingView = view.findViewById(R.id.anime_select_rating);
        favouriteCheckBoxView = view.findViewById(R.id.anime_select_favourite);
        animeDescriptionView = view.findViewById(R.id.anime_select_description);
        averageRatingView = view.findViewById(R.id.anime_select_average_rating);
        romanjiView = view.findViewById(R.id.anime_romanji_name);
        progressLayout = view.findViewById(R.id.anime_progress_layout);
        ratingLayout = view.findViewById(R.id.anime_rating_layout);
        statusLayout = view.findViewById(R.id.anime_status_layout);
        loadingIndicator = view.findViewById(R.id.anime_select_loading_indicator);
        MaterialButton saveButton = view.findViewById(R.id.anime_save_button);
        loadingIndicator.setVisibility(View.VISIBLE);
        saveButton.setEnabled(false);

        dropDownMenu.setOnItemClickListener(((adapterView, view1, i, l) -> statusLayout.setError(null)));
        favouriteCheckBoxView.setOnCheckedChangeListener((compoundButton, b) -> favourite = b);

        animeRatingView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0){
                    int input = Integer.parseInt(charSequence.toString());
                    if(input < 0 || input > 10){
                        ratingLayout.setError("*invalid");
                    } else {
                        ratingLayout.setError(null);
                    }
                } else {
                    ratingLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
        animeProgressView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0){
                    int input = Integer.parseInt(charSequence.toString());
                    if(input < 0 || input > title.getEpisodes()){
                        progressLayout.setError("*invalid");
                    } else {
                        progressLayout.setError(null);
                    }
                } else {
                    progressLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        Activity activity = getActivity();

        Thread getDataThread = new Thread(() -> {
            title = Azure.getAnimeTitle(title_id);

            if (activity != null) {
                activity.runOnUiThread(() -> {
                    if (title != null) {
                        animeTitleView.setText(title.getAnimeTitle());
                        animeImageView.setImageBitmap(title.getPoster());
                        progressLayout.setSuffixText(String.format(Locale.US,
                                "/%d", title.getEpisodes()));
                        dropDownMenu.setText(animeStatus, false);
                        if(animeProgress != -1){
                            animeProgressView.setText(String.format(Locale.US,
                                    "%d", animeProgress));
                        }
                        if(animeRating != -1){
                            animeRatingView.setText(String.format(Locale.US,
                                    "%d", animeRating));
                        }
                        romanjiView.setText(title.getRomanji());
                        favouriteCheckBoxView.setChecked(favourite);
                        animeDescriptionView.setText(title.getDescription());
                        averageRatingView.setText("8.8");
                    } else {
                        Toast.makeText(activity,
                                getResources().getString(R.string.network_error),
                                Toast.LENGTH_SHORT).show();
                    }

                    loadingIndicator.setVisibility(View.INVISIBLE);
                    saveButton.setEnabled(true);
                });
            }
        });
        getDataThread.start();

        //save data on button click
        saveButton.setOnClickListener(buttonView -> {
            saveButton.setEnabled(false);
            loadingIndicator.setVisibility(View.VISIBLE);

            //save changes to userdata (local)
            int status = -1;
            int progress = animeProgress;
            int rating = animeRating;

            if(animeProgressView.getText() != null){
                try {
                    progress = Integer.parseInt(animeProgressView.getText().toString());
                } catch (NumberFormatException e){/*ignore*/}
            }
            if(animeRatingView.getText() != null){
                try {
                    rating = Integer.parseInt(animeRatingView.getText().toString());
                } catch (NumberFormatException e){/*ignore*/}
            }

            String statusInput = dropDownMenu.getText().toString();
            switch(statusInput){
                case "Watching":
                    status = Azure.WATCHING;
                    break;
                case "Planning":
                    status = Azure.PLANNING;
                    break;
                case "Completed":
                    status = Azure.COMPLETED;
                    break;
            }
            if(status != -1){
                //upload changes to database
                int finalRating = rating;
                int finalStatus = status;
                int finalProgress = progress;
                Thread saveDataThread = new Thread(() -> {
                    String message = null;
                    if(finalStatus != dataEntry.status
                            || finalRating != dataEntry.rating
                            || finalProgress != dataEntry.progress
                            || favourite != dataEntry.favourite)
                    {
                        if(finalRating != dataEntry.rating){
                            dataEntry.rating = finalRating;
                        }
                        if(favourite != dataEntry.favourite){
                            dataEntry.favourite = favourite;
                        }

                        if(finalProgress != dataEntry.progress){
                            dataEntry.progress = finalProgress;
                        }
                        dataEntry.status = finalStatus;

                        //remove entry from current list
                        boolean removeRes = userData.remove(title_id);

                        //add to correct place
                        if(removeRes){
                            message = "Saved";
                        } else {
                            message = "Added to list";
                        }

                        switch(finalStatus) {
                            case Azure.WATCHING:
                                userData.getWatchingList().addItem(dataEntry);
                                break;
                            case Azure.PLANNING:
                                userData.getPlanningList().addItem(dataEntry);
                                break;
                            case Azure.COMPLETED:
                                userData.getCompletedList().addItem(dataEntry);
                                break;
                        }
                    }


                    int userId = sp.getInt(getString(R.string.user_id), 0);

                    Azure.Validity success = Azure.saveAnimeUserData(userId);

                    String finalMessage = message;
                    requireActivity().runOnUiThread(() -> {
                        switch (success){
                            case QUERY_FAILED:
                                Toast.makeText(activity,
                                        getResources().getString(R.string.network_error),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case QUERY_SUCCESSFUL:
                                Toast.makeText(activity, finalMessage, Toast.LENGTH_SHORT).show();
                        }
                        saveButton.setEnabled(true);
                        loadingIndicator.setVisibility(View.INVISIBLE);
                    });

                });
                saveDataThread.start();
            } else {
                statusLayout.setError("*must select");
                saveButton.setEnabled(true);
                loadingIndicator.setVisibility(View.INVISIBLE);
            }
        });

        MaterialButton removeButton = view.findViewById(R.id.anime_remove_button);
        removeButton.setOnClickListener(removeView -> {
            removeButton.setEnabled(false);
            loadingIndicator.setVisibility(View.VISIBLE);

            userData.remove(title_id);

            Thread removeAnimeThread = new Thread(() -> {
                int userId = sp.getInt(getString(R.string.user_id), 0);
                Azure.Validity result = Azure.saveAnimeUserData(userId);

                requireActivity().runOnUiThread(() -> {
                    switch (result){
                        case QUERY_FAILED:
                            Toast.makeText(activity,
                                    getResources().getString(R.string.network_error),
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case QUERY_SUCCESSFUL:
                            Toast.makeText(activity, "Saved", Toast.LENGTH_SHORT).show();
                    }

                    dropDownMenu.setText(null);
                    animeProgressView.setText(null);
                    animeRatingView.setText(null);
                    favouriteCheckBoxView.setChecked(false);

                    removeButton.setEnabled(true);
                    loadingIndicator.setVisibility(View.INVISIBLE);
                });

            });
            removeAnimeThread.start();
        });

        return view;
    }
}