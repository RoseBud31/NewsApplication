package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.models.UserArticleInteraction;

import java.time.LocalDateTime;
import java.util.List;

public interface UserArticleInteractionDAO {
    void logInteraction(int userId, int articleId, int timeSpent, String interactionType, LocalDateTime readAt);
    List<UserArticleInteraction> readInteractionsForUser(int userId);
    void setArticleLiked(int userId, int articleId, String userInteractionType);
    String getArticleInteractionType(int userId, int articleId);
}
