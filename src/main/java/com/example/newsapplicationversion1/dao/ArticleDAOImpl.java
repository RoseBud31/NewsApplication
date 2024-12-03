package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.concurrency.ConcurrencyManager;
import com.example.newsapplicationversion1.data.Database;
import com.example.newsapplicationversion1.models.Article;
import com.example.newsapplicationversion1.services.RecommendationEngine;
import com.example.newsapplicationversion1.services.StanfordNLP;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ArticleDAOImpl implements ArticleDAO {
    private final ConcurrencyManager concurrencyManager = new ConcurrencyManager();
    private final List<Article> articles = new CopyOnWriteArrayList<Article>(); // Very important for concurrency
    private final List<Article> recommendedArticles = new CopyOnWriteArrayList<Article>();
    private static Connection connect;
    private static PreparedStatement prepare;
    private static ResultSet resultSet;
    private RecommendationEngine recommendationEngine;

    public List<Article> getAllArticles(){
        try {
            String sql = "select * from articles";
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            resultSet = prepare.executeQuery();
            int taskCount = 0;
            while (resultSet.next()) {
                final int articleId = resultSet.getInt("articleID");
                final String source = resultSet.getString("source");
                final String title = resultSet.getString("title");
                final String author = resultSet.getString("author");
                final String category = resultSet.getString("category");
                final String description = resultSet.getString("description");
                final Date publishedDate = resultSet.getDate("publishedDate");
                final String content = resultSet.getString("content");
                concurrencyManager.submit(() -> {
                    Article article = new Article(articleId, source, title, author, category, description, publishedDate, content);
                    articles.add(article);
                });
                taskCount++;
            }
            System.out.println(taskCount);
            return articles;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public Article getArticle(int articleID) {
        try {
            String sql = "select * from articles where articleID=?";
            connect= Database.connectDb();
            prepare= connect.prepareStatement(sql);
            resultSet = prepare.executeQuery();
            Article article = new Article(resultSet.getInt("articleID"), resultSet.getString("source"), resultSet.getString("title"), resultSet.getString("author"),resultSet.getString("category"),resultSet.getString("description"),resultSet.getDate("publishedDate"), resultSet.getString("content"));
            return article;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addArticle(Article article) {
        try{
            String sql = "INSERT INTO ARTICLES (source, title, author, category, description, publishedDate, content) VALUES (?, ?, ?, ?, ?, ?, ?)";
            String category = recommendationEngine.categorizeArticle(article.getContent());
            connect= Database.connectDb();
            prepare= connect.prepareStatement(sql);
            prepare.setString(1, article.getSource());
            prepare.setString(2, article.getTitle());
            prepare.setString(3, article.getAuthor());
            prepare.setString(4, category);
            prepare.setString(5, article.getDescription());
            prepare.setDate(6, article.getPublishedDate());
            prepare.setString(7, article.getContent());
            prepare.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return;
        }
    }
    public List<Article> getRecommendedArticles(List<Integer> recommendedArticles) {
        try{
            String sql = "select * from articles where articleID in recommendedArticles ";
            connect= Database.connectDb();
            prepare= connect.prepareStatement(sql);
            resultSet = prepare.executeQuery();
            int taskCount = 0;
            while (resultSet.next()) {
                final int articleId = resultSet.getInt("articleID");
                final String source = resultSet.getString("source");
                final String title = resultSet.getString("title");
                final String author = resultSet.getString("author");
                final String category = resultSet.getString("category");
                final String description = resultSet.getString("description");
                final Date publishedDate = resultSet.getDate("publishedDate");
                final String content = resultSet.getString("content");
                concurrencyManager.submit(() -> {
                    Article article = new Article(articleId, source, title, author, category, description, publishedDate, content);
                    articles.add(article);
                });
                taskCount++;
            }
            System.out.println(taskCount);
            return articles;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public List<Article> getArticlesByCategory(String category) {
        List<Article> articles1= getAllArticles();
        return articles1.stream().filter(a -> a.getCategory().equals(category)).collect(Collectors.toList());
    }
}
