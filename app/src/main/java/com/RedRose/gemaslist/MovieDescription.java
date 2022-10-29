package com.RedRose.gemaslist;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

//import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

public class MovieDescription extends Fragment {

    private View view;
    private TextView descriptionView;
    private TextView movieTitle;
    private ImageView movieImageView;
    private Button saveBtn, removeButton;
    private EditText MovieRating;
    private boolean favourite;
    private String title_id;
    private String rating;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_movie_description, container, false);
        MovieRating = view.findViewById(R.id.editRating);
        removeButton = view.findViewById(R.id.removeBtn);
        descriptionView = view.findViewById(R.id.movieDescriptiontextView);
        movieTitle = view.findViewById(R.id.MovieTitletextView);
        movieImageView = view.findViewById(R.id.MovieimageView2);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        String title_id = getArguments().getString("title_id");
        DatabaseReference ref = database.getReference(FirebaseUtil.MOVIE_PATH).child(title_id);
        //get data
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get all movies -> snapshot.getChildren()
                String title = snapshot.child("title").getValue(String.class);
                String description = snapshot.child("description").getValue(String.class);//return value

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

        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseUtil.getDB()
                .getReference(FirebaseUtil.USERDATA).child(uid).child("movies").child(title_id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String rating = snapshot.child("rating").getValue(String.class);
                MovieRating.setText(rating);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        saveBtn = view.findViewById(R.id.Savebutton5);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating = MovieRating.getText().toString();
                try{
                    int ratingInt = Integer.parseInt(rating);
                    boolean validate = validateInfo(ratingInt);
                    if(validate){
                        String uid = FirebaseAuth.getInstance().getUid();
                        DatabaseReference reference = FirebaseUtil.getDB()
                                .getReference(FirebaseUtil.USERDATA).child(uid).child("movies").child(title_id);
                        reference.setValue(title_id);
                        Context context = getActivity(); //The context to use. Usually your Application or Activity object
                        CharSequence message = "Saved";//Display string
                        int duration = Toast.LENGTH_SHORT; //How long the toast message will lasts
                        Toast toast = Toast.makeText(context,message,duration);
                        toast.show();
                        reference.child("rating").setValue(rating);
                    }
                    else{
                        Toast.makeText(getActivity(),"Enter 0 to 10 value",Toast.LENGTH_LONG).show();
                    }
                }catch(Exception e){

                }
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = FirebaseAuth.getInstance().getUid();
                DatabaseReference reference = FirebaseUtil.getDB()
                        .getReference(FirebaseUtil.USERDATA).child(uid).child("movies").child(title_id);
                reference.removeValue();

                Context context = getActivity(); //The context to use. Usually your Application or Activity object
                CharSequence message = "Deleted";//Display string
                int duration = Toast.LENGTH_SHORT; //How long the toast message will lasts
                Toast toast = Toast.makeText(context,message,duration);
                toast.show();

            }
        });

        return view;
    }
    boolean validateInfo(int movieRating) {
        if(movieRating<0 || movieRating>10){
            return false;
        }
        else{

            return true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setSubtitle(R.string.movies);
    }
}
