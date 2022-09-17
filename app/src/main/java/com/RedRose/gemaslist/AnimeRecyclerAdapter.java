package com.RedRose.gemaslist;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class AnimeRecyclerAdapter extends RecyclerView.Adapter<AnimeRecyclerAdapter.ListViewHolder> {

    private final ArrayList<AnimeDataEntry> items;
    protected MainActivity context;
    protected int color;
    protected LinearLayoutCompat.LayoutParams linear;
    protected LinearLayoutCompat.LayoutParams grid;

    public AnimeRecyclerAdapter(MainActivity context, ArrayList<AnimeDataEntry> items, int color) {
        this.items = items;
        this.context = context;
        this.color = color;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.anime_list_linear_card, parent, false);

        Context context = view.getContext();

        linear = new LinearLayoutCompat.LayoutParams(
                (int) FirebaseUtil.pxFromDp(context,65),
                (int) FirebaseUtil.pxFromDp(context, 91)
                );
        grid = new LinearLayoutCompat.LayoutParams(
                (int) FirebaseUtil.pxFromDp(context,115),
                (int) FirebaseUtil.pxFromDp(context, 161)
        );

        return new ListViewHolder(view);
    }

    @Override
    public synchronized void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        //set data to cards
        DatabaseReference animeRef = FirebaseUtil.getDB()
                .getReference(FirebaseUtil.ANIME_PATH)
                .child("titles").child(items.get(position).title);

        int progress = items.get(position).progress;
        int rating = items.get(position).rating;
        animeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.getAnimeTitle().setText(snapshot.child("title").getValue(String.class));
                @SuppressWarnings("ConstantConditions")
                long ep = snapshot.child("episodes").getValue(Long.class);
                if(progress != -1){
                    holder.getProgress().setText(String.format(Locale.US,
                            "Progress : %d/%s",
                            progress,
                            ep));
                } else if(progress == ep){
                    holder.getProgress().setText(R.string.completed);
                } else {
                    holder.getProgress().setText(R.string.not_started);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.getCard().setCardBackgroundColor(color);

        Thread getImage = new Thread(() -> {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference imageRef = storage.getReference().child("anime_posters")
                    .child(items.get(position).title);
            imageRef.getBytes(FirebaseUtil.ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap poster = FirebaseUtil.byteToBitmap(bytes);
                context.runOnUiThread(() -> holder.getAnimeImage().setImageBitmap(poster));
            });
        });
        getImage.start();

        if(rating != -1){
            holder.getRating().setText(String.format(Locale.US, "Rating : %d/10", rating));
        } else {
            holder.getRating().setText(R.string.unrated);
        }

        holder.getCard().setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("title_id", items.get(position).title);

            context.appBarSubtitleHistory.push((String)
                    Objects.requireNonNull(context.getSupportActionBar()).getSubtitle());

            Objects.requireNonNull(context.getSupportActionBar())
                    .setSubtitle(R.string.select);
            Navigation.findNavController(context, R.id.nav_host_fragment)
                    .navigate(R.id.action_animeList_to_animeSelect, bundle);
        });

        //changes for layout toggle
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

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView card;
        private final MaterialTextView title, progress, rating;
        private final LinearLayoutCompat layout;
        private final ImageView animeImage;

        public ListViewHolder(@NonNull View view) {
            super(view);

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
