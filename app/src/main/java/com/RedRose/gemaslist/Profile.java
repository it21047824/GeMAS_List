package com.RedRose.gemaslist;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class Profile extends Fragment {

    private MaterialButton editBanner;
    private ImageView profileBanner;
    private Uri profileUri, croppedUri, selectedImage;
    private String uid;


    public Profile() {
        // Required empty public constructor
    }

    public static Profile newInstance() {
        return new Profile();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        uid = user.getUid();


        if(user.getPhotoUrl() != null){
            String uriString = user.getPhotoUrl().toString();
            profileUri = Uri.parse(uriString.replace("=s96-c", "=s400-c"));
        }

        String userName = user.getDisplayName();
        String userEmail = user.getEmail();

        //get views from layout
        MaterialTextView usernameView = view.findViewById(R.id.profile_name);
        MaterialTextView emailView = view.findViewById(R.id.profile_email);

        editBanner = view.findViewById(R.id.banner_edit_button);

        ImageView profileImage = view.findViewById(R.id.profile_image);

        profileBanner = view.findViewById(R.id.profile_banner);

        //set values
        usernameView.setText(userName);
        emailView.setText(userEmail);

        Picasso.get().load(profileUri).into(profileImage);

        StorageReference bannerRef = FirebaseStorage.getInstance().getReference()
                .child("user_banners").child(uid);
        bannerRef.getDownloadUrl().addOnSuccessListener(uri ->
                Picasso.get().load(uri).into(profileBanner));

        selectImage();

        return view;
    }

    private void selectImage() {
        ActivityResultLauncher<Intent> cropImageActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent resultIntent = result.getData();
                    if(resultIntent != null && result.getResultCode() == 100) {
                        String resultString = resultIntent.getStringExtra("RESULT");
                        if(resultString != null) {
                            croppedUri = Uri.parse(resultString);

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference uploadRef = storage.getReference()
                                    .child("user_banners").child(uid);
                            uploadRef.putFile(croppedUri).addOnSuccessListener(taskSnapshot -> {
                                NavigationView nav = requireActivity().findViewById(R.id.navDrawer);
                                View header = nav.getHeaderView(0);
                                ImageView banner = header.findViewById(R.id.nav_banner_image);
                                StorageReference bannerRef = FirebaseStorage.getInstance().getReference()
                                        .child("user_banners").child(uid);
                                bannerRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    Picasso.get().load(uri).into(profileBanner);
                                    Picasso.get().load(uri).into(banner);
                                });
                            });
                        }
                    }
                }
        );

        ActivityResultLauncher<Intent> selectImageActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK){
                        Intent resultIntent = result.getData();
                        if(resultIntent != null){
                            selectedImage = resultIntent.getData();

                            Intent cropImageIntent = new Intent(
                                    requireActivity(),
                                    ImageCropper.class
                            );
                            cropImageIntent.putExtra("DATA", selectedImage.toString());
                            cropImageIntent.putExtra("TYPE", "banner");
                            cropImageActivity.launch(cropImageIntent);

                        }
                    }
                }
        );

        editBanner.setOnClickListener(view -> {
            Intent selectImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
            selectImageIntent.setType("image/*");
            selectImageActivity.launch(selectImageIntent);
        });



    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setSubtitle(R.string.profile);
    }
}