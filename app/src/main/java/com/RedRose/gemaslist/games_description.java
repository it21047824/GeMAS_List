package com.RedRose.gemaslist;

import android.app.Activity;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link games_description#newInstance} factory method to
// * create an instance of this fragment.
// */
public class games_description extends Fragment {

    private View view;
    private TextView descriptionView;
    private TextView gameTitle, TextV;
    private CheckBox gamebox;
    private EditText gameEdit;
    private ImageView gameImageView;
    private Spinner spinner;
    private Button saveBtn, removeButton;
    private EditText gameRating;
    private boolean favourite;
    private String title_id;

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public games_description() {
//        // Required empty public constructor
//    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment games_description.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static games_description newInstance(String param1, String param2) {
//        games_description fragment = new games_description();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_games_description, container, false);
        view = view.findViewById(R.id.divider9);
        view = view.findViewById(R.id.divider8);
        gameRating = view.findViewById(R.id.editRating);
        removeButton = view.findViewById(R.id.button16);
        TextV = view.findViewById(R.id.textView31);
        TextV = view.findViewById(R.id.textView33);
        TextV = view.findViewById(R.id.textView34);
        descriptionView = view.findViewById(R.id.textView27);
        spinner = view.findViewById(R.id.spinner4);
        gameEdit = view.findViewById(R.id.editTextNumberDecimal);
        gamebox = view.findViewById(R.id.checkBox2);
        gameImageView = view.findViewById(R.id.imageView15);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        String title_id = getArguments().getString("title_id");
        DatabaseReference ref = database.getReference(FirebaseUtil.GAME_PATH).child(title_id);
        //get data
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get all movies -> snapshot.getChildren()
                String title = snapshot.child("title").getValue(String.class);
                String description = snapshot.child("description").getValue(String.class);//return value

                descriptionView.setText(description);
                gameTitle.setText(title);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference imageRef = storage.getReference().child("movie_posters").child(title_id);
                imageRef.getDownloadUrl().addOnSuccessListener(uri ->
                        Picasso.get().load(uri).into(gameImageView));
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseUtil.getDB()
                .getReference(FirebaseUtil.USERDATA).child(uid).child("games").child(title_id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String rating = snapshot.child("rating").getValue(String.class);
                gameRating.setText(rating);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        saveBtn = view.findViewById(R.id.button15);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = FirebaseAuth.getInstance().getUid();
                DatabaseReference reference = FirebaseUtil.getDB()
                        .getReference(FirebaseUtil.USERDATA).child(uid).child("games").child(title_id);
                reference.setValue(title_id);
                String rating = gameRating.getText().toString();
                reference.child("rating").setValue(rating);


            }


        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = FirebaseAuth.getInstance().getUid();
                DatabaseReference reference = FirebaseUtil.getDB()
                        .getReference(FirebaseUtil.USERDATA).child(uid).child("games").child(title_id);
                reference.removeValue();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setSubtitle(R.string.games);
    }
}


