package com.example.newsapplicationversion1.services;

import com.example.newsapplicationversion1.dao.*;
import com.example.newsapplicationversion1.models.Article;
import com.example.newsapplicationversion1.models.User;
import com.example.newsapplicationversion1.models.UserArticleInteraction;
import com.example.newsapplicationversion1.models.UserPreferences;
import com.example.newsapplicationversion1.session.SessionManager;

import java.util.*;
import java.util.stream.Collectors;

public class RecommendationEngine {

    User currentUser = SessionManager.currentUser;
    UserPreferencesDAO userPreferencesDAO = new UserPreferencesDAOImpl();
    UserArticleInteractionDAO userArticleInteractionDAO = new UserArticleInteractionDAOImpl();
    ArticleDAO articleDAO = new ArticleDAOImpl();

    public List<Article> recommendArticles(int userId){
        // Get the liked articles and the unread articles
        List<UserArticleInteraction> userHistory= userArticleInteractionDAO.readInteractionsForUser(userId);
        // Liked articles
        List<Integer> articlesRead = new ArrayList<>();
        List<Integer> articlesLiked = new ArrayList<>();
        for (UserArticleInteraction userArticleInteraction : userHistory) {
            articlesRead.add(userArticleInteraction.getArticleId());
            if ("liked".equals(userArticleInteraction.getInteractionType())){
                articlesLiked.add(userArticleInteraction.getArticleId());
            }
        }
        List<Article> articlesAvailable = articleDAO.getAllArticles();

        return rankArticles(articlesAvailable, userPreferencesDAO.getUserPreferences(currentUser.getUserId()));
    }

    private List<Article> rankArticles(List<Article> articles, UserPreferences userPreferences) {
        return articles.stream()
                .sorted(Comparator.comparingDouble(article -> calculateRelevanceScore(article, userPreferences)))
                .collect(Collectors.toList());
    }

    private double calculateRelevanceScore(Article article, UserPreferences userPreferences){
        double relevanceScore = 0;
        // Match categories
        if (userPreferences.getPreferredCategories().contains(article.getCategory())){
            relevanceScore += 3;
        }
        // Keyword Similarity
        StanfordNLP stanfordNLP = new StanfordNLP();
        List<String> articleKeywords = stanfordNLP.extractKeywords(article.getContent());
        for (String keyword : articleKeywords) {
            if (userPreferences.getPreferredKeywords().contains(keyword)){
                relevanceScore += 0.5;
            }
        }
        return relevanceScore;
    }
}
