package com.RedRose.gemaslist;

import android.app.Activity;
import android.app.MediaRouteButton;
import android.os.Bundle;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link series_description#newInstance} factory method to
 * create an instance of this fragment.
 */
public class series_description extends Fragment {
    private View view;
    private TextView seriesTitle , seriesDescrip ;
    private ImageView seriesImageView;
    private Button saveButton , removeButton;
    private int status , progress , rating ,MovieRating ;
    private EditText SeriesRatingView;
    private Spinner dropDown;
    private  AnimeDataEntry dataEntry;
    private boolean favourite;
    private AnimeUserData userData;
    private String title_id;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public series_description() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment series_description.
     */
    // TODO: Rename and change types and number of parameters
    public static series_description newInstance(String param1, String param2) {
        series_description fragment = new series_description();
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

        view = inflater.inflate(R.layout.fragment_series_description, container, false);

        removeButton = view.findViewById(R.id.RemoveButton);


        String title_id = getArguments().getString("title_id");

        // Get a reference to our posts
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseUtil.SERIES_PATH).child(title_id);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String description = dataSnapshot.child("description").getValue(String.class);
                String title = dataSnapshot.child("title").getValue(String.class);

                seriesDescrip = view.findViewById(R.id.textViewSeries);
                seriesDescrip.setText(description);

                seriesTitle = view.findViewById(R.id.seriestile);
                seriesTitle.setText(title);

                seriesImageView = view.findViewById(R.id.imageViewSeies);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference imageRef = storage.getReference().child("/series_posters").child(title_id);
                imageRef.getDownloadUrl().addOnSuccessListener(uri ->
                        Picasso.get().load(uri).into(seriesImageView));

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

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = FirebaseAuth.getInstance().getUid();
                DatabaseReference reference = FirebaseUtil.getDB()
                        .getReference(FirebaseUtil.USERDATA).child(uid).child("series").child(title_id);
                reference.removeValue();
            }
        });


        // Inflate the layout for this fragment
        return view;}


}