package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.models.Article;

import java.sql.Date;
import java.util.List;

public interface ArticleDAO {
    List<Article> getAllArticles();
    Article getArticle(int articleId);
    void addArticle(String title, String author, String source, String description, Date publishedDate, String content);
    List<Article> getRecommendedArticles(List<Integer> recommendedArticles);
    List<Article> getArticlesByCategory(String category);
    List<Article> getArticleHistory(int userId);
    void addArticlesBulk(List<Article> articles);
}
