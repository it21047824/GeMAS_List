package com.RedRose.gemaslist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link series_description#newInstance} factory method to
 * create an instance of this fragment.
 */
public class series_description extends Fragment {
    private View view;
    private TextView seriesTitle , seriesDescrip ;
    private ImageView seriesImageView;
    private Button saveButton;
    private String title_id;

    public series_description() {
        // Required empty public constructor
    }

    public static series_description newInstance() {
        return new series_description();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_series_description, container, false);
        seriesImageView = view.findViewById(R.id.imageViewSeries);
        seriesDescrip = view.findViewById(R.id.series_description_text);
        seriesTitle = view.findViewById(R.id.seriestile);

        title_id = getArguments().getString("title_id");

        // Get a reference to our posts
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseUtil.SERIES_PATH).child(title_id);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String description = dataSnapshot.child("description").getValue(String.class);
                String title = dataSnapshot.child("title").getValue(String.class);

                seriesDescrip.setText(description);
                seriesTitle.setText(title);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference imageRef = storage.getReference().child("series_posters").child(title_id);
                imageRef.getDownloadUrl().addOnSuccessListener(uri ->{
                    Log.e("SeriesDesc83", uri.toString());
                    Picasso.get().load(uri).into(seriesImageView);
                });
        }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        saveButton = view.findViewById(R.id.savebuttonseries);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = FirebaseAuth.getInstance().getUid();
                DatabaseReference reference = FirebaseUtil.getDB()
                        .getReference(FirebaseUtil.USERDATA).child(uid).child("series").child(title_id);
                reference.setValue(title_id);
            }
        });


        // Inflate the layout for this fragment
        return view;}

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setSubtitle(R.string.series);
    }
}