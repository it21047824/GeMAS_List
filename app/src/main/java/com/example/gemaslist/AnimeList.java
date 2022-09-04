package com.example.gemaslist;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.LinearLayoutCompat.LayoutParams;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnimeList extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private TabLayout tabLayout;

    public AnimeList() {
        // Required empty public constructor
    }

    public static AnimeList newInstance(String param1, String param2) {
        AnimeList fragment = new AnimeList();
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

        View view = inflater.inflate(R.layout.fragment_anime_list, container, false);

        ViewPager2 viewPager = (ViewPager2) view.findViewById(R.id.anime_view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        viewPagerAdapter.addFragment(new AnimeWatching(), "Watching");
        viewPagerAdapter.addFragment(new AnimePlanning(), "Planning");
        viewPagerAdapter.addFragment(new AnimeCompleted(), "Completed");

        tabLayout = view.findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager,
                ((tab, position) -> tab.setText(viewPagerAdapter.getTitle(position)))).attach();

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_baseline_play_arrow_24);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_baseline_event_available_24);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_baseline_check_24);
        // Inflate the layout for this fragment
        return view;
    }

    private class ViewPagerAdapter extends FragmentStateAdapter{

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitles = new ArrayList<>();

        public ViewPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        public void addFragment(Fragment fr, String title){
            fragments.add(fr);
            fragmentTitles.add(title);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }

        public CharSequence getTitle(int position) {
            return fragmentTitles.get(position);
        }
    }

    public static void createAnimeCard(
            Activity activity,
            LinearLayoutCompat linearLayoutCompat,
            Context context,
            String title,
            String progress,
            String rating,
            int color
    ) {
        //create card
        LayoutParams cardLayoutParams =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        cardLayoutParams.setMargins(8,16,16,8);

        MaterialCardView cardView = new MaterialCardView(context);
        cardView.setLayoutParams(cardLayoutParams);
        cardView.setPadding(8,8,8,8);
        cardView.setElevation(5);
        cardView.setClickable(true);
        cardView.setFocusable(true);
        cardView.setCardBackgroundColor(color);

        //card view on click listener
        cardView.setOnClickListener((View view) -> {
            Navigation.findNavController(activity, R.id.nav_host_fragment)
                    .navigate(R.id.action_animeList_to_animeSelect);
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
        LayoutParams imageParams = new LayoutParams(250, 300);

        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.placeholder_image);
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

}