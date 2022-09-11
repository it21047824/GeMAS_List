package com.example.gemaslist;

public class AnimeDataEntry {
    public int title;
    public int progress;
    public int status;
    public int rating;
    public AnimeDataEntry prev;
    public AnimeDataEntry next;

    public AnimeDataEntry(int title, int status, int progress, int rating) {
        this.title = title;
        this.status = status;
        this.progress = progress;
        this.rating = rating;
        prev = null;
        next = null;
    }
}
