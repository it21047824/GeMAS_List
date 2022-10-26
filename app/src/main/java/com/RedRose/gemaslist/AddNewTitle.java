package com.RedRose.gemaslist;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat.LayoutParams;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddNewTitle extends AppCompatActivity {

    protected ImageView linearPreview, gridPreview;
    protected MaterialButton selectImageButton, addTitleButton, clearButton, deleteButton;
    protected Uri croppedUri;
    protected Uri selectedImage;
    protected TextInputEditText titleInput, episodesInput, descriptionInput, romajiInput;
    protected AutoCompleteTextView titleType;
    protected LinearProgressIndicator progressIndicator;
    protected TextInputLayout episodeLayout, newTitleLayout, romajiLayout, titleTypeLayout;
    protected SharedPreferences sp;
    private String title_ID, editType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_title);
        sp = getSharedPreferences("AddNewTitle", MODE_PRIVATE);

        //find views
        titleInput = findViewById(R.id.new_title_name);
        titleTypeLayout = findViewById(R.id.add_title_type_layout);
        episodesInput = findViewById(R.id.new_title_episodes);
        descriptionInput = findViewById(R.id.new_title_description);
        episodeLayout = findViewById(R.id.new_title_episodes_layout);
        newTitleLayout = findViewById(R.id.new_title_layout);
        romajiLayout = findViewById(R.id.new_romanji_layout);
        romajiInput = findViewById(R.id.new_romanji_name);
        linearPreview = findViewById(R.id.new_image_linear);
        gridPreview = findViewById(R.id.new_image_grid);
        selectImageButton = findViewById(R.id.select_image_button);
        addTitleButton = findViewById(R.id.add_title_button);
        progressIndicator = findViewById(R.id.new_title_progress);
        clearButton = findViewById(R.id.new_anime_clear);
        deleteButton = findViewById(R.id.delete_title_button);

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
                episodeLayout.setVisibility(View.VISIBLE);
                romajiLayout.setVisibility(View.VISIBLE);
            } else if (selectedType.equals("Anime")) {
                episodeLayout.setVisibility(View.VISIBLE);
                romajiLayout.setVisibility(View.VISIBLE);
            } else {
                episodeLayout.setVisibility(View.GONE);
                romajiLayout.setVisibility(View.GONE);
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

        deleteButton.setOnClickListener(deleteView -> new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.are_you_sure)
                .setMessage(R.string.delete_title_warning)
                .setPositiveButton("Confirm", (dialogInterface, i) -> {

                    DatabaseReference deleteRef = FirebaseUtil.getDB()
                            .getReference(FirebaseUtil.ANIME_PATH)
                            .child("titles").child(title_ID);
                    deleteRef.removeValue((error, ref) -> {
                        if(error == null) {
                            Snackbar.make(deleteButton, "Removed Permanently", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Snackbar.make(deleteButton, getString(R.string.error), Toast.LENGTH_SHORT).show();
                        }
                    });

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference imageRef = storage.getReference().child("anime_posters").child(title_ID);
                    imageRef.delete();

                })
                .setNeutralButton("Cancel", (dialogInterface, i) -> {/*do nothing*/})
                .show());
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
        if(romajiInput.getText() != null) {
            editor.putString("ROMANJI", romajiInput.getText().toString());
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
        Intent editIntent = getIntent();
        title_ID = editIntent.getStringExtra("title_ID");
        editType = editIntent.getStringExtra("type");

        if(title_ID != null && editType.equals("anime")){
            DatabaseReference animeRef = FirebaseUtil.getDB().getReference(FirebaseUtil.ANIME_PATH)
                    .child("titles").child(title_ID);
            animeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    addTitleButton.setText(R.string.edit_anime_title);
                    titleType.setText(getString(R.string.anime), false);
                    titleTypeLayout.setVisibility(View.INVISIBLE);
                    episodesInput.setText(String.format(Locale.US, "%d",
                            snapshot.child("episodes").getValue(Long.class)));
                    titleInput.setText(snapshot.child("title").getValue(String.class));
                    romajiLayout.setVisibility(View.VISIBLE);
                    romajiInput.setText(snapshot.child("romaji").getValue(String.class));
                    descriptionInput.setText(snapshot.child("description").getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference imageRef = storage.getReference().child("anime_posters").child(title_ID);
            imageRef.getBytes(FirebaseUtil.ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap poster = FirebaseUtil.byteToBitmap(bytes);
                linearPreview.setImageBitmap(poster);
                gridPreview.setImageBitmap(poster);
            });
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            titleType.setText(sp.getString("TYPE", null), false);
            episodesInput.setText(sp.getString("EPISODES", null));
            titleInput.setText(sp.getString("TITLE", null));
            romajiInput.setText(sp.getString("ROMANJI", null));
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
                    episodeLayout.setVisibility(View.VISIBLE);
                    romajiLayout.setVisibility(View.GONE);
                    break;
                case "Anime" :
                    episodeLayout.setVisibility(View.VISIBLE);
                    romajiLayout.setVisibility(View.VISIBLE);
                    break;
                default :
                    episodeLayout.setVisibility(View.GONE);
                    romajiLayout.setVisibility(View.GONE);
                    break;
            }
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
                Snackbar.make(selectImageButton,
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
                Snackbar.make(selectImageButton,
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
        if(romajiInput.getText() != null) {
            romaji = romajiInput.getText().toString();
        }
        Context context = view.getContext();

        String finalEpisodes = episodes;
        String finalRomaji = romaji;

        Thread addAnimeTitleThread = new Thread(() -> {
            boolean result = false;

            if(title_ID != null && editType.equals("anime")) {
                DatabaseReference updateRef = FirebaseUtil.getDB()
                        .getReference(FirebaseUtil.ANIME_PATH).child("titles").child(title_ID);
                updateRef.child("title").setValue(title);
                updateRef.child("description").setValue(description);
                if(finalEpisodes != null) {
                    updateRef.child("episodes").setValue(Integer.parseInt(finalEpisodes));
                }
                updateRef.child("romaji").setValue(finalRomaji);
                result = true;

                if (croppedUri != null) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference animeRef = storage.getReference().child("anime_posters");
                    StorageReference uploadRef = animeRef.child(title_ID);
                    uploadRef.putFile(croppedUri);
                }

            } else {
                switch (type) {
                    case "Game" :
                        if(croppedUri != null){
                            result = FirebaseUtil.addNewGameTitle(
                                    title,
                                    description,
                                    croppedUri,
                                    context,
                                    addTitleButton
                            );
                        }
                        break;
                    case "Movie" :
                        if(croppedUri != null){
                            result = FirebaseUtil.addNewMovieTitle(
                                    title,
                                    description,
                                    croppedUri,
                                    context,
                                    addTitleButton
                            );
                        }
                        break;
                    case "Series" :
                        if(finalEpisodes != null && croppedUri != null){
                            result = FirebaseUtil.addNewSeriesTitle(
                                    title,
                                    description,
                                    croppedUri,
                                    Integer.parseInt(finalEpisodes),
                                    context,
                                    addTitleButton
                            );
                        }
                        break;
                    case "Anime" :
                        if(finalEpisodes != null && !finalEpisodes.equals("") && croppedUri != null){
                            result = FirebaseUtil.addNewAnimeTitle(
                                    title,
                                    description,
                                    croppedUri,
                                    Integer.parseInt(finalEpisodes),
                                    finalRomaji,
                                    context,
                                    addTitleButton
                            );
                        }
                        break;
                }
            }

            boolean finalResult = result;
            runOnUiThread(() -> {
                if((type.equals("Anime")||type.equals("Series")) && finalEpisodes == null){
                    episodeLayout.setError("*required");
                }
                if(croppedUri == null){
                    Snackbar.make(addTitleButton,
                            getResources().getString(R.string.image_select),
                            Toast.LENGTH_LONG).show();
                } else if (titleInput.getText() == null){
                    Snackbar.make(addTitleButton,
                            getResources().getString(R.string.enter_title),
                            Toast.LENGTH_SHORT).show();
                }
                else if (finalResult) {
                    episodeLayout.setError(null);
                    newTitleLayout.setError(null);
                    Toast.makeText(context,
                            getResources().getString(R.string.successful),
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Snackbar.make(addTitleButton,
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
        romajiInput.setText(null);
        linearPreview.setImageResource(R.drawable.placeholder_image);
        gridPreview.setImageResource(R.drawable.placeholder_image);
    }
}