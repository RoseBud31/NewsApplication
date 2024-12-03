package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.models.Article;

import java.util.List;

public interface RecommendationDAO {
    void recordRecommendations(List<Article> articles);
    List<Article> getRecommendations(int userId);
    boolean checkForDuplicate(int userId, int articleId);
}
