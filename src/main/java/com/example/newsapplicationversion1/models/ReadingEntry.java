package com.example.newsapplicationversion1.models;

import java.time.LocalDateTime;
import java.util.Date;

public class ReadingEntry {
    private Article article;
    private LocalDateTime lastReadDate;
    private double readingTime;
    private boolean rating;

    public ReadingEntry(Article article) {
        this.article = article;
        this.lastReadDate = LocalDateTime.now();
        this.readingTime = 0.0;
        this.rating = false;
    }
    public Article getArticle() {
        return article;
    }

    public LocalDateTime getLastReadDate() {
        return lastReadDate;
    }
    public double getReadingTime() {
        return readingTime;
    }
    public void setReadingTime(double readingTime) {
        this.readingTime = readingTime;
    }
    public void setLastReadDate(LocalDateTime lastReadDate) {
        this.lastReadDate = lastReadDate;
    }
    public void setRating(boolean rating) {
        this.rating = rating;
    }
    public boolean getRating() {
        return rating;
    }
}
