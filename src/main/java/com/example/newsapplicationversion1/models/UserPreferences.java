package com.example.newsapplicationversion1.models;

import java.util.List;

public class UserPreferences {
    private int preferenceId;
    private int userId;
    private List<String> preferredCategories;
    private List<String> preferredKeywords;
    private List<Article> readingHistory;
    private List<Article> recommendedArticles;

    public int getPreferenceId() {
        return preferenceId;
    }
    public void setPreferenceId(int preferenceId) {
        this.preferenceId = preferenceId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public List<String> getPreferredCategories() {
        return preferredCategories;
    }
    public void setPreferredCategories(List<String> preferredCategories) {
        this.preferredCategories = preferredCategories;
    }
    public List<String> getPreferredKeywords() {
        return preferredKeywords;
    }
    public void setPreferredKeywords(List<String> preferredKeywords) {
        this.preferredKeywords = preferredKeywords;
    }
    public List<Article> getReadingHistory() {
        return readingHistory;
    }
    public void setReadingHistory(List<Article> readingHistory) {
        this.readingHistory = readingHistory;
    }
    public List<Article> getRecommendedArticles() {
        return recommendedArticles;
    }
    public void setRecommendedArticles(List<Article> recommendedArticles) {
        this.recommendedArticles = recommendedArticles;
    }
}
