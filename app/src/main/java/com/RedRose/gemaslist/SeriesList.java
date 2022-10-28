package com.RedRose.gemaslist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SeriesList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SeriesList extends Fragment {

    private TextView seriesTitle , seriesDescrip ;
    private ImageView seriesImageView;
    private View view;
    private ArrayList<String> seriesIds;



    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SeriesList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SeriesList.
     */
    // TODO: Rename and change types and number of parameters
    public static SeriesList newInstance(String param1, String param2) {
        SeriesList fragment = new SeriesList();
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


        seriesIds = new ArrayList<>();
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_series_list, container, false);

        LinearLayout serieslayout = view.findViewById(R.id.SeriesListItems);

        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseUtil.getDB()
                .getReference(FirebaseUtil.USERDATA).child(uid).child("series");


//        MaterialButton button = view.findViewById(R.id.button5);
//        button.setOnClickListener(v -> Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
//                .navigate(R.id.action_series_to_series_description));
        // Get a reference to our posts
        //final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference ref = database.getReference(FirebaseUtil.SERIES_PATH).child(title_id);
        reference.addListenerForSingleValueEvent(new ValueEventListener () {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot d: snapshot.getChildren()){
                    seriesIds.add(d.getKey());
                }
                for (String s :seriesIds ){
                    View listItem = inflater.inflate(R.layout.serieslistone,null);
                    listItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle cardBundle = new Bundle();
                            cardBundle.putString("title_id", s);

                            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                                    .navigate(R.id.action_series_to_series_description, cardBundle);
                        }
                    });
                    ImageView seriesListImg = listItem.findViewById(R.id.loopimage);
                    TextView seriesTextView = listItem.findViewById(R.id.looptext);


                    DatabaseReference ref = FirebaseUtil.getDB().getReference(FirebaseUtil.SERIES_PATH).child(s);
                    //get data
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //get all series
                            String title = snapshot.child("title").getValue(String.class);

                            seriesTextView.setText(title);

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference imageRef = storage.getReference().child("series_posters").child(s);
                            imageRef.getDownloadUrl().addOnSuccessListener(uri ->
                                    Picasso.get().load(uri).into(seriesListImg));
                            serieslayout.addView(listItem);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setSubtitle(R.string.seriesList);
    }
    }