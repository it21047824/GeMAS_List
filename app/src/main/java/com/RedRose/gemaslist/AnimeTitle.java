package com.RedRose.gemaslist;

import android.graphics.Bitmap;

//TODO: Remove deprecated class
public class AnimeTitle {
    private final String animeID;
    private final String animeTitle;
    private final String description;
    private final Bitmap poster;
    private final int episodes;
    private final String romaji;
    private final float rating;

    public AnimeTitle(String animeID,
                      String animeTitle,
                      String description,
                      int episodes,
                      String romaji,
                      float rating
    ) {
        this.animeID = animeID;
        this.animeTitle = animeTitle;
        this.description = description;
        this.poster = null;
        this.episodes = episodes;
        this.romaji = romaji;
        this.rating = rating;
    }

    public String getAnimeID() {
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

    public String getRomaji() {
        return romaji;
    }

    public float getRating() {
        return rating;
    }
}
