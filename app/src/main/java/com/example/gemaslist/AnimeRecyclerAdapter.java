package com.example.gemaslist;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class AnimeRecyclerAdapter extends RecyclerView.Adapter<AnimeRecyclerAdapter.ListViewHolder> {

    private final ArrayList<Integer> items;
    protected MainActivity context;
    protected int color;
    protected LinearLayoutCompat.LayoutParams linear;
    protected LinearLayoutCompat.LayoutParams grid;

    public AnimeRecyclerAdapter(MainActivity context, ArrayList<Integer> items, int color) {
        this.items = items;
        this.context = context;
        this.color = color;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.anime_list_linear_card, parent, false);

        linear = new LinearLayoutCompat.LayoutParams(250,300);
        grid = new LinearLayoutCompat.LayoutParams(330,370);

        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        //set data to cards
        holder.getCard().setCardBackgroundColor(color);
        holder.getAnimeImage().setImageResource(R.drawable.placeholder_image);
        holder.getAnimeTitle().setText(String.format(Locale.US, "Static Anime Title %d", items.get(position)));
        holder.getProgress().setText(String.format(Locale.US, "Progress : %d/12", 0));
        holder.getRating().setText(String.format(Locale.US, "Rating : %d/10", 0));

        //changes with layout toggle
        switch (AnimeCardList.currentLayoutManagerType){
            case LINEAR_LAYOUT_MANAGER:
                holder.getAnimeImage().setLayoutParams(linear);
                holder.getLayout().setOrientation(LinearLayoutCompat.HORIZONTAL);
                holder.getLayout().setGravity(Gravity.START);
                holder.getAnimeTitle().setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                holder.getProgress().setVisibility(View.VISIBLE);
                holder.getRating().setVisibility(View.VISIBLE);
                break;
            case GRID_LAYOUT_MANAGER:
                holder.getAnimeImage().setLayoutParams(grid);
                holder.getLayout().setOrientation(LinearLayoutCompat.VERTICAL);
                holder.getLayout().setGravity(Gravity.CENTER_HORIZONTAL);
                holder.getAnimeTitle().setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                holder.getProgress().setVisibility(View.GONE);
                holder.getRating().setVisibility(View.GONE);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView card;
        private final MaterialTextView title, progress, rating;
        private final LinearLayoutCompat layout;
        private final ImageView animeImage;

        public ListViewHolder(@NonNull View view) {
            super(view);

            //card on click event
            view.setOnClickListener(v -> {
                context.appBarSubtitleHistory.push((String)
                        Objects.requireNonNull(context.getSupportActionBar()).getSubtitle());

                Objects.requireNonNull(context.getSupportActionBar())
                        .setSubtitle(R.string.select);
                Navigation.findNavController(context, R.id.nav_host_fragment)
                        .navigate(R.id.action_animeList_to_animeSelect);
            });

            //initialize views
            card = view.findViewById(R.id.anime_list_card);
            layout = view.findViewById(R.id.anime_list_card_content);
            title = view.findViewById(R.id.anime_card_title);
            progress = view.findViewById(R.id.anime_card_progress);
            rating = view.findViewById(R.id.anime_card_rating);
            animeImage = view.findViewById(R.id.anime_card_image);
        }

        public MaterialCardView getCard() {
            return card;
        }

        public LinearLayoutCompat getLayout() {
            return layout;
        }

        public MaterialTextView getAnimeTitle() {
            return title;
        }

        public MaterialTextView getProgress() {
            return progress;
        }

        public MaterialTextView getRating() {
            return rating;
        }

        public ImageView getAnimeImage() {
            return animeImage;
        }
    }

}
