package com.RedRose.gemaslist;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.LinearLayoutCompat.LayoutParams;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Objects;

public class Search extends Fragment {

    private static final Object lock = new Object();
    private ArrayList<AnimeTitle> titles;
    private MainActivity activity;
    private Context context;
    private LinearLayoutCompat layout;
    private LinearProgressIndicator loader;

    public Search() {
        // Required empty public constructor
    }

    public static Search newInstance() {
        return new Search();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        context = getContext();
        activity = (MainActivity) context;

        layout = view.findViewById(R.id.search_view);
        loader = view.findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);

        //create cards
        Thread thread = new Thread(() -> {

            titles = new ArrayList<>();

            download();

            assert activity != null;
            activity.runOnUiThread(this::createViews);
        });
        thread.start();

        return view;
    }

    public static void createAnimeCard(
            MainActivity activity,
            LinearLayoutCompat linearLayoutCompat,
            Context context,
            String title,
            String progress,
            String rating,
            Bitmap image,
            Bundle bundle
    ) {
        //create card
        LayoutParams cardLayoutParams =
                new LinearLayoutCompat.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        cardLayoutParams.setMargins(8,16,16,8);

        MaterialCardView cardView = new MaterialCardView(context);
        cardView.setLayoutParams(cardLayoutParams);
        cardView.setPadding(8,8,8,8);
        cardView.setElevation(5);
        cardView.setClickable(true);
        cardView.setFocusable(true);

        //card view on click listener
        cardView.setOnClickListener((View view) -> {
            activity.appBarSubtitleHistory.push((String)
                    Objects.requireNonNull(activity.getSupportActionBar()).getSubtitle());

            Objects.requireNonNull(activity.getSupportActionBar())
                    .setSubtitle(R.string.select);
            Navigation.findNavController(activity, R.id.nav_host_fragment)
                    .navigate(R.id.action_search_to_animeSelect, bundle);
        });

        //create title
        LayoutParams textLayoutParams =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        TextView titleTextView = new TextView(context);
        titleTextView.setLayoutParams(textLayoutParams);
        titleTextView.setText(title);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        titleTextView.setGravity(Gravity.CENTER);
        titleTextView.setPadding(20,10,5,20);

        //create progress text
        TextView progressTextView = new TextView(context);
        progressTextView.setLayoutParams(textLayoutParams);
        progressTextView.setText(progress);
        progressTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        progressTextView.setGravity(Gravity.CENTER);
        progressTextView.setPadding(20,5,5,10);

        //create rating text
        TextView ratingTextView = new TextView(context);
        ratingTextView.setLayoutParams(textLayoutParams);
        ratingTextView.setText(rating);
        ratingTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        ratingTextView.setGravity(Gravity.CENTER);
        ratingTextView.setPadding(20,5,5,10);

        //create image
        LayoutParams imageParams = new LayoutParams(
                (int) Azure.pxFromDp(context,65),
                (int) Azure.pxFromDp(context, 91)
        );

        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(image);
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        //create text layout
        LinearLayoutCompat textLayout = new LinearLayoutCompat(context);
        textLayout.setLayoutParams(textLayoutParams);
        textLayout.setOrientation(LinearLayoutCompat.VERTICAL);
        textLayout.addView(titleTextView);
        textLayout.addView(progressTextView);
        textLayout.addView(ratingTextView);

        //create card layout
        LayoutParams cardContentParams =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        LinearLayoutCompat cardContent = new LinearLayoutCompat(context);
        cardContent.setLayoutParams(cardContentParams);

        cardContent.addView(imageView);
        cardContent.addView(textLayout);

        cardView.addView(cardContent);

        linearLayoutCompat.addView(cardView);
    }

    public void createViews(){
        synchronized (lock){
            int i = 0;

            while(i<13){
                Bundle bundle = new Bundle();
                bundle.putInt("title_id", 1000+i);

                if(titles.get(i) != null){
                    createAnimeCard(activity, layout, context, titles.get(i).getAnimeTitle(), "12", "5", titles.get(i).getPoster(), bundle);
                }

                i++;
            }
        }
        loader.setVisibility(View.INVISIBLE);
    }
    public void download(){
        synchronized (lock){
            int i = 1000;
            while (i<1013){

                titles.add(Azure.getAnimeTitle(i));

                i++;
            }
        }
    }
}