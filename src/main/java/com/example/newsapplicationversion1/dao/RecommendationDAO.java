package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.models.Article;
import com.example.newsapplicationversion1.models.User;

import java.util.List;

public interface RecommendationDAO {
    void recordRecommendations(List<Article> articles, User currentUser);
    boolean checkForDuplicate(int userId, int articleId);
}
