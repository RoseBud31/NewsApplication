package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.models.Article;

import java.util.List;

public interface ArticleDAO {
    List<Article> getAllArticles();
    Article getArticle(int articleId);
    void addArticle(Article article);
    List<Article> getRecommendedArticles(List<Integer> recommendedArticles);
}
