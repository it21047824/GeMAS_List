package com.RedRose.gemaslist;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AnimeSelect extends Fragment {

    private MaterialTextView averageRatingView, animeTitleView, animeDescriptionView, romanjiView;
    private ImageView animeImageView;
    private TextInputEditText animeProgressView, animeRatingView;
    private CheckBox favouriteCheckBoxView;
    private LinearProgressIndicator loadingIndicator;
    private String title_id;
    private AnimeDataEntry dataEntry;
    private AnimeUserData userData;
    private TextInputLayout progressLayout, ratingLayout, statusLayout;
    private String animeStatus;
    private int animeProgress, animeRating;
    private boolean favourite;
    private int episodes;
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

        animeStatus = null;
        animeProgress = -1;
        animeRating = -1;
        favourite = false;

        if(getArguments() != null){
            title_id = getArguments().getString("title_id");
            userData = AnimeUserData.getAnimeUserData();
            dataEntry = userData.find(title_id);
            if(dataEntry != null){
                switch (dataEntry.status){
                    case FirebaseUtil.WATCHING:
                        animeStatus = "Watching";
                        break;
                    case FirebaseUtil.PLANNING:
                        animeStatus = "Planning";
                        break;
                    case FirebaseUtil.COMPLETED:
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

        Activity activity = getActivity();

        //display information
        DatabaseReference selectRef = FirebaseUtil.getDB().getReference("anime_titles/titles")
                .child(title_id);
        selectRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String title = snapshot.child("title").getValue(String.class);
                //noinspection ConstantConditions
                episodes = Math.toIntExact(snapshot.child("episodes").getValue(Long.class));
                String description = snapshot.child("description").getValue(String.class);
                String romaji = snapshot.child("romaji").getValue(String.class);
                Long raters = snapshot.child("global_rating").child("raters").getValue(Long.class);
                Long ratings = snapshot.child("global_rating").child("ratings").getValue(Long.class);
                float average = (float) 0.0;
                try {
                    //noinspection ConstantConditions
                    if(raters > 0){
                        //noinspection ConstantConditions
                        average = (float) ratings / raters;
                    }
                } catch (Exception e) {
                    Log.e("Select", e.getMessage());
                }

                animeTitleView.setText(title);
                progressLayout.setSuffixText(String.format(Locale.US,
                        "/%d", episodes));
                romanjiView.setText(romaji);
                averageRatingView.setText(String.format(Locale.US, "%.1f", average));
                animeDescriptionView.setText(description);

                loadingIndicator.setVisibility(View.INVISIBLE);
                saveButton.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity,
                        getResources().getString(R.string.network_error),
                        Toast.LENGTH_SHORT).show();
                loadingIndicator.setVisibility(View.INVISIBLE);
                saveButton.setEnabled(true);
            }
        });


        //display image
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReference().child("anime_posters").child(title_id);
        imageRef.getBytes(FirebaseUtil.ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap poster = FirebaseUtil.byteToBitmap(bytes);
            animeImageView.setImageBitmap(poster);
        });

        //display user data
        dropDownMenu.setText(animeStatus, false);
        if(animeProgress != -1){
            animeProgressView.setText(String.format(Locale.US,
                    "%d", animeProgress));
        }
        if(animeRating != -1){
            animeRatingView.setText(String.format(Locale.US,
                    "%d", animeRating));
        }
        favouriteCheckBoxView.setChecked(favourite);


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
                    if(input < 0 || input > episodes){
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
                    status = FirebaseUtil.WATCHING;
                    break;
                case "Planning":
                    status = FirebaseUtil.PLANNING;
                    break;
                case "Completed":
                    status = FirebaseUtil.COMPLETED;
                    break;
            }
            if(status != -1){

                String message = null;
                if(status != dataEntry.status
                        || rating != dataEntry.rating
                        || progress != dataEntry.progress
                        || favourite != dataEntry.favourite)
                {
                    if(rating != dataEntry.rating){
                        dataEntry.rating = rating;
                    }
                    if(favourite != dataEntry.favourite){
                        dataEntry.favourite = favourite;
                    }

                    if(progress != dataEntry.progress){
                        dataEntry.progress = progress;
                    }
                    dataEntry.status = status;

                    //remove entry from current list
                    boolean removeRes = userData.remove(title_id);

                    //add to correct place
                    if(removeRes){
                        message = "Saved";
                    } else {
                        message = "Added to list";
                    }

                    switch(status) {
                        case FirebaseUtil.WATCHING:
                            userData.getWatchingList().addItem(dataEntry);
                            break;
                        case FirebaseUtil.PLANNING:
                            userData.getPlanningList().addItem(dataEntry);
                            break;
                        case FirebaseUtil.COMPLETED:
                            userData.getCompletedList().addItem(dataEntry);
                            break;
                    }
                }

                //upload changes to database
                String uid = FirebaseAuth.getInstance().getUid();
                DatabaseReference reference = FirebaseUtil.getDB()
                        .getReference(FirebaseUtil.USERDATA);

                if (uid != null) {
                    String finalMessage = message;
                    reference.child(uid).child("anime").setValue(
                            FirebaseUtil.userdataToJSON(),
                            (error, ref) -> {
                        if (error == null) {
                            Toast.makeText(activity, finalMessage, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Select", error.getMessage());
                            Toast.makeText(activity,
                                    getResources().getString(R.string.network_error),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                statusLayout.setError("*must select");
            }
            saveButton.setEnabled(true);
            loadingIndicator.setVisibility(View.INVISIBLE);
        });

        //remove from list
        MaterialButton removeButton = view.findViewById(R.id.anime_remove_button);
        removeButton.setOnClickListener(removeView -> {
            removeButton.setEnabled(false);
            loadingIndicator.setVisibility(View.VISIBLE);

            userData.remove(title_id);

            //upload changes to database
            String uid = FirebaseAuth.getInstance().getUid();
            DatabaseReference reference = FirebaseUtil.getDB()
                    .getReference(FirebaseUtil.USERDATA);

            if (uid != null) {
                reference.child(uid).child("anime").setValue(
                        FirebaseUtil.userdataToJSON(),
                        (error, ref) -> {
                    if (error == null) {
                        Toast.makeText(activity, "removed from list", Toast.LENGTH_SHORT).show();

                        dropDownMenu.setText(null);
                        animeProgressView.setText(null);
                        animeRatingView.setText(null);
                        favouriteCheckBoxView.setChecked(false);

                        removeButton.setEnabled(true);
                        loadingIndicator.setVisibility(View.INVISIBLE);
                    } else {
                        Log.e("Firebase", error.getMessage());
                        Toast.makeText(activity,
                                getResources().getString(R.string.network_error),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
    }
}