package com.example.gemaslist;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnimeList extends Fragment {

    private AnimeCardList watchingList;
    private AnimeCardList planningList;
    private AnimeCardList completedList;
    public static ViewPager2 viewPager;

    public AnimeList() {
        // Required empty public constructor
    }

    public static AnimeList newInstance() {
        return new AnimeList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get colors for cards
        TypedValue typedValue = new TypedValue();
        int watchingColor = 0;
        int planningColor = 0;
        int completedColor = 0;

        if (getContext() != null){
            TypedArray a = getContext().obtainStyledAttributes(typedValue.data, new int[]{
                    R.attr.animeWatching,
                    R.attr.animePlanning,
                    R.attr.animeCompleted
            });
            watchingColor = a.getColor(0,0);
            planningColor = a.getColor(1,0);
            completedColor = a.getColor(2,0);
            a.recycle();
        }

        AnimeUserData userData = AnimeUserData.getAnimeUserData();

        watchingList = AnimeCardList.newInstance(watchingColor, userData.getWatchingList());
        planningList = AnimeCardList.newInstance(planningColor, userData.getPlanningList());
        completedList = AnimeCardList.newInstance(completedColor, userData.getCompletedList());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_anime_list, container, false);

        viewPager = view.findViewById(R.id.anime_view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        viewPagerAdapter.addFragment(watchingList, "Watching");
        viewPagerAdapter.addFragment(planningList, "Planning");
        viewPagerAdapter.addFragment(completedList, "Completed");

        TabLayout tabLayout = view.findViewById(R.id.anime_tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                ((tab, position) -> tab.setText(viewPagerAdapter.getTitle(position)))).attach();

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_baseline_play_arrow_24);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_baseline_event_available_24);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_baseline_check_24);

        return view;
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter{

        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitles = new ArrayList<>();

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
}