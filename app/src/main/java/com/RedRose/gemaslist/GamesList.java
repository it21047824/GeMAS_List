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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.google.firebase.database.DataSnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GamesList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GamesList extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView descriptionView;
    private TextView gameTitle;
    private ImageView gameImageView;
    private View view;
    private ArrayList<String> gameIds;


    public GamesList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GamesList.
     */
    // TODO: Rename and change types and number of parameters
    public static GamesList newInstance(String param1, String param2) {
        GamesList fragment = new GamesList();
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
        gameIds = new ArrayList<>();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_games_list, container, false);
        LinearLayout gameLayout = view.findViewById(R.id.gameListItems);

        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseUtil.getDB()
                .getReference(FirebaseUtil.USERDATA).child(uid).child("game");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d: snapshot.getChildren()){
                    gameIds.add(d.getKey());
                }
                for(String s: gameIds){
                    View listItem = inflater.inflate(R.layout.new_games_list,null);
                    ImageView gameListImg = listItem.findViewById(R.id.gameListImage);
                    TextView gameListTxt = listItem.findViewById(R.id.gameListTitle);

                    listItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle cardBundle = new Bundle();
                            cardBundle.putString("title_id", s);

                            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                                    .navigate(R.id.action_games_to_games_description, cardBundle);
                        }
                    });

                    DatabaseReference ref = FirebaseUtil.getDB().getReference(FirebaseUtil.GAME_PATH).child(s);
                    //get data
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //get all movies -> snapshot.getChildren()
                            String title = snapshot.child("title").getValue(String.class);

                            gameListTxt.setText(title);

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference imageRef = storage.getReference().child("game_posters").child(s);
                            imageRef.getDownloadUrl().addOnSuccessListener(uri ->
                                    Picasso.get().load(uri).into(gameListImg));
                            gameLayout.addView(listItem);
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
                .setSubtitle(R.string.gameList);
    }
}