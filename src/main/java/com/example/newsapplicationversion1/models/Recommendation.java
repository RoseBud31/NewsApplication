package com.example.newsapplicationversion1.models;

import java.util.List;

public class Recommendation {
    private int userId;
    private List<Article> recommendedArticles;

    public Recommendation(int userId, List<Article> recommendedArticles) {
        this.userId = userId;
        this.recommendedArticles = recommendedArticles;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public List<Article> getRecommendedArticles() {
        return recommendedArticles;
    }
    public void setRecommendedArticles(List<Article> recommendedArticles) {
        this.recommendedArticles = recommendedArticles;
    }
}


