package com.RedRose.gemaslist;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat.LayoutParams;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddNewTitle extends AppCompatActivity {

    protected ImageView linearPreview, gridPreview;
    protected MaterialButton selectImageButton, addTitleButton, clearButton;
    protected Uri croppedUri;
    protected Uri selectedImage;
    protected TextInputEditText titleInput, episodesInput, descriptionInput, romanjiInput;
    protected AutoCompleteTextView titleType;
    protected LinearProgressIndicator progressIndicator;
    protected TextInputLayout episodeLayout, newTitleLayout, romanjiLayout;
    protected SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_title);
        sp = getSharedPreferences("AddNewTitle", MODE_PRIVATE);

        //find views
        titleInput = findViewById(R.id.new_title_name);
        episodesInput = findViewById(R.id.new_title_episodes);
        descriptionInput = findViewById(R.id.new_title_description);
        episodeLayout = findViewById(R.id.new_title_episodes_layout);
        newTitleLayout = findViewById(R.id.new_title_layout);
        romanjiLayout = findViewById(R.id.new_romanji_layout);
        romanjiInput = findViewById(R.id.new_romanji_name);
        linearPreview = findViewById(R.id.new_image_linear);
        gridPreview = findViewById(R.id.new_image_grid);
        selectImageButton = findViewById(R.id.select_image_button);
        addTitleButton = findViewById(R.id.add_title_button);
        progressIndicator = findViewById(R.id.new_title_progress);
        clearButton = findViewById(R.id.new_anime_clear);

        //dropdown items
        List<String> dropDownItems = new ArrayList<>();
        dropDownItems.add("Game");
        dropDownItems.add("Movie");
        dropDownItems.add("Series");
        dropDownItems.add("Anime");
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(this, R.layout.dropdown_item, dropDownItems);
        titleType = findViewById(R.id.new_title_type);
        titleType.setAdapter(arrayAdapter);

        titleType.setOnItemClickListener((adapterView, view, position, l) -> {
            String selectedType = arrayAdapter.getItem(position);
            if (selectedType.equals("Series")) {
                episodesInput.setVisibility(View.VISIBLE);
                episodesInput.setVisibility(View.VISIBLE);
            } else if (selectedType.equals("Anime")) {
                episodesInput.setVisibility(View.VISIBLE);
                episodesInput.setVisibility(View.VISIBLE);
                romanjiLayout.setVisibility(View.VISIBLE);
            } else {
                episodesInput.setVisibility(View.GONE);
                romanjiLayout.setVisibility(View.GONE);
            }
        });

        Context context = AddNewTitle.this;

        //set preview image params
        LayoutParams linear = new LayoutParams(
                (int) FirebaseUtil.pxFromDp(context,55),
                (int) FirebaseUtil.pxFromDp(context, 77)
        );
        linear.setMargins(16,16,16,16);
        LayoutParams grid = new LayoutParams(
                (int) FirebaseUtil.pxFromDp(context,115),
                (int) FirebaseUtil.pxFromDp(context, 161)
        );
        grid.setMargins(16,16,16,16);

        linearPreview.setLayoutParams(linear);
        gridPreview.setLayoutParams(grid);

        //button on click listeners
        selectImage();

        //initialize inputs from previous
        initializeFromPrevious();

        //add title on click
        addTitleButton.setOnClickListener(this::addNewTitle);

        //clear everything
        clearButton.setOnClickListener(this::clearSelection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences.Editor editor = sp.edit();
        if(titleType.getText() != null) {
            editor.putString("TYPE", titleType.getText().toString());
        }
        if(episodesInput.getText() != null) {
            editor.putString("EPISODES", episodesInput.getText().toString());
        }
        if(titleInput.getText() != null) {
            editor.putString("TITLE", titleInput.getText().toString());
        }
        if(romanjiInput.getText() != null) {
            editor.putString("ROMANJI", romanjiInput.getText().toString());
        }
        if(descriptionInput.getText() != null) {
            editor.putString("DESCRIPTION", descriptionInput.getText().toString());
        }
        if(croppedUri != null) {
            editor.putString("IMAGE", croppedUri.toString());
        }
        editor.apply();
    }

    public void initializeFromPrevious() {
        titleType.setText(sp.getString("TYPE", null), false);
        episodesInput.setText(sp.getString("EPISODES", null));
        titleInput.setText(sp.getString("TITLE", null));
        romanjiInput.setText(sp.getString("ROMANJI", null));
        descriptionInput.setText(sp.getString("DESCRIPTION", null));
        if(sp.getString("IMAGE", null) != null){
            croppedUri = Uri.parse(sp.getString("IMAGE", null));
        }
        if(croppedUri != null){
            linearPreview.setImageURI(croppedUri);
            gridPreview.setImageURI(croppedUri);
        }

        switch (titleType.getText().toString()) {
            case "Series" :
                episodesInput.setVisibility(View.VISIBLE);
                episodesInput.setVisibility(View.VISIBLE);
                break;
            case "Anime" :
                episodesInput.setVisibility(View.VISIBLE);
                episodesInput.setVisibility(View.VISIBLE);
                romanjiLayout.setVisibility(View.VISIBLE);
                break;
            default :
                episodesInput.setVisibility(View.GONE);
                romanjiLayout.setVisibility(View.GONE);
                break;
        }
    }

    public void selectImage() {
        //image crop
        ActivityResultLauncher<Intent> cropImageActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent resultIntent = result.getData();
                    if(resultIntent != null && result.getResultCode() == 100) {
                        String resultString = resultIntent.getStringExtra("RESULT");
                        if(resultString != null) {
                            croppedUri = Uri.parse(resultString);
                            linearPreview.setImageURI(croppedUri);
                            gridPreview.setImageURI(croppedUri);
                        }
                    }
                }
        );

        //image select
        ActivityResultLauncher<Intent> selectImageActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK){
                        Intent resultIntent = result.getData();
                        if(resultIntent != null){
                            selectedImage = resultIntent.getData();

                            Intent cropImageIntent = new Intent(
                                    AddNewTitle.this,
                                    ImageCropper.class
                            );
                            cropImageIntent.putExtra("DATA", selectedImage.toString());
                            cropImageActivity.launch(cropImageIntent);

                        }
                    }
                });

        selectImageButton.setOnClickListener(view -> {
            Intent selectImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
            selectImageIntent.setType("image/*");
            selectImageActivity.launch(selectImageIntent);
        });
        //re-crop image
        linearPreview.setOnClickListener(view -> {
            if(croppedUri != null && selectedImage != null){
                Intent cropImageIntent = new Intent(
                        AddNewTitle.this,
                        ImageCropper.class
                );
                cropImageIntent.putExtra("DATA", selectedImage.toString());
                cropImageActivity.launch(cropImageIntent);
            } else {
                Toast.makeText(view.getContext(),
                        getResources().getString(R.string.image_reselect),
                        Toast.LENGTH_LONG).show();
            }
        });
        gridPreview.setOnClickListener(view -> {
            if(croppedUri != null && selectedImage != null){
                Intent cropImageIntent = new Intent(
                        AddNewTitle.this,
                        ImageCropper.class
                );
                cropImageIntent.putExtra("DATA", selectedImage.toString());
                cropImageActivity.launch(cropImageIntent);
            } else {
                Toast.makeText(view.getContext(),
                        getResources().getString(R.string.image_reselect),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void addNewTitle(View view) {
        progressIndicator.setVisibility(View.VISIBLE);
        addTitleButton.setEnabled(false);

        //get all inputs
        String title = Objects.requireNonNull(titleInput.getText()).toString();
        String description = Objects.requireNonNull(descriptionInput.getText()).toString();
        String episodes = null;
        if(episodesInput.getText() != null) {
            episodes = episodesInput.getText().toString();
        }
        String type = titleType.getText().toString();
        String romaji = null;
        if(romanjiInput.getText() != null) {
            romaji = romanjiInput.getText().toString();
        }
        Context context = view.getContext();

        String finalEpisodes = episodes;
        String finalRomaji = romaji;

        Thread addAnimeTitleThread = new Thread(() -> {
            boolean result = false;

            switch (type) {
                case "Game" :
                    //add a game
                    break;
                case "Movie" :
                    //add a movie
                    break;
                case "Series" :
                    //add a series
                    break;
                case "Anime" : {
                    if(finalEpisodes != null && croppedUri != null){
                        result = FirebaseUtil.addNewAnimeTitle(
                                title,
                                description,
                                croppedUri,
                                Integer.parseInt(finalEpisodes),
                                finalRomaji,
                                context
                        );

                        if(result){
                            SharedPreferences.Editor editor = sp.edit();
                            editor.remove("TYPE");
                            editor.remove("EPISODES");
                            editor.remove("TITLE");
                            editor.remove("ROMANJI");
                            editor.remove("DESCRIPTION");
                            editor.remove("IMAGE");
                            editor.apply();
                        }
                    }
                    break;
                }
            }

            boolean finalResult = result;
            runOnUiThread(() -> {
                if(finalEpisodes == null){
                    episodeLayout.setError("*required");
                }
                if(croppedUri == null){
                    Toast.makeText(context,
                            getResources().getString(R.string.image_select),
                            Toast.LENGTH_LONG).show();
                }
                if (finalResult) {
                    episodeLayout.setError(null);
                    newTitleLayout.setError(null);

                    finish();
                } else {
                    Toast.makeText(context,
                            getResources().getString(R.string.network_error),
                            Toast.LENGTH_SHORT).show();
                }

                progressIndicator.setVisibility(View.INVISIBLE);
                addTitleButton.setEnabled(true);
            });

        });
        addAnimeTitleThread.start();
    }

    public void clearSelection(View view) {
        titleType.setText(null);
        titleInput.setText(null);
        episodesInput.setText(null);
        descriptionInput.setText(null);
        romanjiInput.setText(null);
        linearPreview.setImageResource(R.drawable.placeholder_image);
        gridPreview.setImageResource(R.drawable.placeholder_image);
    }
}