package com.example.gemaslist;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

public class AnimeCardList extends Fragment {

    private static final String ARG_CARD_COLOR = "cardColor";
    private static final String ARG_ITEMS = "listItems";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final String KEY_LAYOUT = "layoutBoolean";
    private static final int SPAN_COUNT = 3;

    public enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }
    public static LayoutManagerType currentLayoutManagerType;

    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;
    protected AnimeRecyclerAdapter adapter;
    protected int cardColor;
    protected ArrayList<Integer> items;
    protected SwitchMaterial switchMaterial;

    private View view;
    private SharedPreferences sp;

    public AnimeCardList() {
        // Required empty public constructor
    }

    public static AnimeCardList newInstance(int color, ArrayList<Integer> items) {
        AnimeCardList animeCardList = new AnimeCardList();
        Bundle args = new Bundle();
        args.putInt(ARG_CARD_COLOR, color);
        args.putIntegerArrayList(ARG_ITEMS, items);
        animeCardList.setArguments(args);
        return animeCardList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = requireActivity().getSharedPreferences(KEY_LAYOUT_MANAGER, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //get arguments
        if (getArguments() != null) {
            cardColor = getArguments().getInt(ARG_CARD_COLOR);
            items = getArguments().getIntegerArrayList(ARG_ITEMS);
        } else {
            if (getContext() != null){
                TypedValue typedValue = new TypedValue();
                TypedArray a = getContext().obtainStyledAttributes(typedValue.data,
                        new int[]{com.google.android.material.R.attr.cardBackgroundColor});
                cardColor = a.getColor(0,0);
                a.recycle();
            }
        }

        if(sp.getBoolean(KEY_LAYOUT, false)){
            currentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
        } else {
            currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_anime_card_list, container, false);
        MainActivity context = (MainActivity) getContext();

        //get views
        recyclerView = view.findViewById(R.id.anime_card_container);
        layoutManager = new LinearLayoutManager(getActivity());
        switchMaterial = view.findViewById(R.id.anime_list_layout_switch);

        //set view values
        switchMaterial.setChecked(currentLayoutManagerType ==
                LayoutManagerType.GRID_LAYOUT_MANAGER);
        setRecyclerViewLayoutManager(currentLayoutManagerType);

        adapter = new AnimeRecyclerAdapter(context, items, cardColor);
        recyclerView.setAdapter(adapter);

        switchMaterial.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            SharedPreferences.Editor editor = sp.edit();
            if(isChecked){
                setRecyclerViewLayoutManager(LayoutManagerType.GRID_LAYOUT_MANAGER);
                editor.putBoolean(KEY_LAYOUT, true);
            } else {
                setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);
                editor.putBoolean(KEY_LAYOUT, false);
            }
            editor.apply();
        });

        return view;
    }

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        if(recyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        if (layoutManagerType == LayoutManagerType.GRID_LAYOUT_MANAGER) {
            layoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
            currentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
        } else if (layoutManagerType == LayoutManagerType.LINEAR_LAYOUT_MANAGER) {
            layoutManager = new LinearLayoutManager(getActivity());
            currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onResume() {
        super.onResume();
        //fix variable length in viewpager 2
        switchMaterial.setChecked(currentLayoutManagerType ==
                LayoutManagerType.GRID_LAYOUT_MANAGER);
        view.requestLayout();
    }
}