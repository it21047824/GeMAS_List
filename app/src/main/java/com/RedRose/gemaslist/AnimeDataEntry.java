package com.RedRose.gemaslist;

public class AnimeDataEntry {
    public String title;
    public int progress;
    public int status;
    public int rating;
    public boolean favourite;
    public AnimeDataEntry prev;
    public AnimeDataEntry next;

    public AnimeDataEntry(String title, int status, int progress, int rating, boolean favourite) {
        this.title = title;
        this.status = status;
        this.progress = progress;
        this.rating = rating;
        this.favourite = favourite;
        prev = null;
        next = null;
    }

}
