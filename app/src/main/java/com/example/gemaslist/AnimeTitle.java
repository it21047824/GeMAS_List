package com.example.gemaslist;

import android.graphics.Bitmap;

public class AnimeTitle {
    private final int animeID;
    private final String animeTitle;
    private final String description;
    private final Bitmap poster;
    private final int episodes;
    private final String romanji;
    private final float rating;

    public AnimeTitle(int animeID,
                      String animeTitle,
                      String description,
                      Bitmap poster,
                      int episodes,
                      String romanji,
                      float rating
    ) {
        this.animeID = animeID;
        this.animeTitle = animeTitle;
        this.description = description;
        this.poster = poster;
        this.episodes = episodes;
        this.romanji = romanji;
        this.rating = rating;
    }

    public int getAnimeID() {
        return animeID;
    }

    public String getAnimeTitle() {
        return animeTitle;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getPoster() {
        return poster;
    }

    public int getEpisodes() {
        return episodes;
    }

    public String getRomanji() {
        return romanji;
    }

    public float getRating() {
        return rating;
    }
}
