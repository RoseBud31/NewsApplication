package com.example.newsapplicationversion1.services;

import com.example.newsapplicationversion1.controllers.LoginController;
import com.example.newsapplicationversion1.dao.*;
import com.example.newsapplicationversion1.models.Article;
import com.example.newsapplicationversion1.models.User;
import com.example.newsapplicationversion1.models.UserArticleInteraction;
import com.example.newsapplicationversion1.session.SessionManager;

import java.util.*;

public class RecommendationEngine {
    // Checks prefers categories
    // Checks preferred keywords
    // Higher priority for articles that are liked
    // Higher priority for articles with longer reading time
    // Filters out unread articles to suggest
    // Based on similarity

    User currentUser = SessionManager.currentUser;
    UserPreferencesDAO userPreferencesDAO = new UserPreferencesDAOImpl();
    UserArticleInteractionDAO userArticleInteractionDAO = new UserArticleInteractionDAOImpl();
    ArticleDAO articleDAO = new ArticleDAOImpl();

    public List<Article> recommendArticles(int userId){
        List<String> preferredCategories = userPreferencesDAO.getUserPreferences(userId).getPreferredCategories();
        List<String> preferredKeywords = userPreferencesDAO.getUserPreferences(userId).getPreferredKeywords();
        List<Article> recommendedArticles = new ArrayList<Article>();

        // Get the liked articles and the unread articles
        List<UserArticleInteraction> userHistory= userArticleInteractionDAO.readInteractionsForUser(userId);
        // Liked articles
        List<Integer> articlesLiked = new ArrayList<Integer>();
        for (UserArticleInteraction userArticleInteraction : userHistory) {
            if (Objects.equals(userArticleInteraction.getInteractionType(), "liked")){
                articlesLiked.add(userArticleInteraction.getArticleId());
            }
        }
        // Articles not yet read
        List<Integer> articlesRead = new ArrayList<Integer>();
        for (UserArticleInteraction userArticleInteraction : userHistory) {
            articlesRead.add(userArticleInteraction.getArticleId());
        }
        List<Article> articlesAvailable = articleDAO.getAllArticles();
        List<Integer> articlesUnread = new ArrayList<>();
        for (Article article : articlesAvailable) {
            if (!articlesRead.contains(article.getArticleId())){
                articlesUnread.add(article.getArticleId());
            }
        }

        Map<Integer, Integer> articleRanking = new HashMap<Integer, Integer>();
        return null;
    }
}
