package com.example.newsapplicationversion1.services;

import com.example.newsapplicationversion1.controllers.LoginController;
import com.example.newsapplicationversion1.dao.*;
import com.example.newsapplicationversion1.models.Article;
import com.example.newsapplicationversion1.models.User;
import com.example.newsapplicationversion1.models.UserArticleInteraction;
import com.example.newsapplicationversion1.models.UserPreferences;
import com.example.newsapplicationversion1.session.SessionManager;

import java.util.*;
import java.util.stream.Collectors;

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

        // Get the liked articles and the unread articles
        List<UserArticleInteraction> userHistory= userArticleInteractionDAO.readInteractionsForUser(userId);
        // Liked articles
        List<Integer> articlesRead = new ArrayList<Integer>();
        List<Integer> articlesLiked = new ArrayList<Integer>();
        for (UserArticleInteraction userArticleInteraction : userHistory) {
            articlesRead.add(userArticleInteraction.getArticleId());
            if (Objects.equals(userArticleInteraction.getInteractionType(), "liked")){
                articlesLiked.add(userArticleInteraction.getArticleId());
            }
        }
        List<Article> articlesAvailable = articleDAO.getAllArticles();


        Map<String, Double> categoryRanking = new HashMap<>();
        for (Article article : articlesAvailable) {
            double score = 0;
            int timeSpent = userArticleInteractionDAO.getArticleInteractionTime(currentUser.getUserId(), article.getArticleId());
            if (articlesLiked.contains(article.getArticleId())){
                score += 10;
                score += (double) timeSpent /120;
            }
            else if (articlesRead.contains(article.getArticleId())){
                score += 2;
            }
            if (categoryRanking.containsKey(article.getCategory())){
                Double value = categoryRanking.get(article.getCategory());
                categoryRanking.put(article.getCategory(), value + score);
            } else{
                categoryRanking.put(article.getCategory(), score);
            }
        }
        Double maxValue = Collections.max(categoryRanking.values());
        List<String> maxThreeCategories = categoryRanking.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()).limit(3).map(Map.Entry::getKey).toList();
        userPreferencesDAO.addUserPreferences(currentUser.getUserId(), maxThreeCategories);

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
