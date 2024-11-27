package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.models.UserArticleInteraction;

import java.sql.Date;
import java.util.List;

public interface UserArticleInteractionDAO {
    void logInteraction(int userId, int articleId, int timeSpent, String interactionType, Date readAt);
    List<UserArticleInteraction> readInteractionsForUser(int userId);
}
