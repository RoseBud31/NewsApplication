package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.models.Recommendation;

import java.sql.Date;

public interface RecommendationDAO {
    void recordRecommendation(int userId, int articleId, Date recommendedAt);
    Recommendation getRecommendations(int userId);
}
