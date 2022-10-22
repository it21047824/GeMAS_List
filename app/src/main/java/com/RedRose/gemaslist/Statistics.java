package com.RedRose.gemaslist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Statistics extends Fragment {

    public Statistics() {
        // Required empty public constructor
    }

    public static Statistics newInstance() {
        return new Statistics();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        ViewPager2 viewPager = view.findViewById(R.id.statistics_view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        adapter.addFragment(GameStats.newInstance(null, null), "Games");
        adapter.addFragment(MovieStats.newInstance(null, null), "Movies");
        adapter.addFragment(SeriesStats.newInstance(null, null), "Series");
        adapter.addFragment(AnimeStats.newInstance(), "Anime");

        TabLayout tabLayout = view.findViewById(R.id.statistics_tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                ((tab, position) -> tab.setText(adapter.getTitle(position)))).attach();

        return view;
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {

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

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setSubtitle(R.string.statistics);
    }
}