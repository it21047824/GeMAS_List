package com.RedRose.gemaslist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

//import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

public class MovieDescription extends Fragment {

    private View view;
    private TextView descriptionView;
    private TextView movieTitle;
    private ImageView movieImageView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_movie_description, container, false);

        //get the spinner from the xml.
        Spinner dropdown = view.findViewById(R.id.spinner2);
        //create a list of items for the spinner.
        String[] items = new String[]{"Planing", "Completed", "Watching"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        String title_id = getArguments().getString("title_id");
        DatabaseReference ref = database.getReference(FirebaseUtil.MOVIE_PATH).child(title_id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String title = snapshot.child("title").getValue(String.class);
                String description =  snapshot.child("description").getValue(String.class);//return value

                descriptionView = view.findViewById(R.id.movieDescriptiontextView);
                movieTitle = view.findViewById(R.id.MovieTitletextView);
                movieImageView = view.findViewById(R.id.MovieimageView2);

                descriptionView.setText(description);
                movieTitle.setText(title);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference imageRef = storage.getReference().child("movie_posters").child(title_id);
                imageRef.getDownloadUrl().addOnSuccessListener(uri ->
                        Picasso.get().load(uri).into(movieImageView));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        return view;
    }


}
