package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.concurrency.ConcurrencyManager;
import com.example.newsapplicationversion1.data.Database;
import com.example.newsapplicationversion1.models.Article;
import com.example.newsapplicationversion1.services.RecommendationEngine;

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
    RecommendationEngine recommendationEngine = new RecommendationEngine();

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
    public void addArticle(String title, String author, String source, String description, Date publishedDate, String content) {
        try{
            String sql = "INSERT INTO ARTICLES (source, title, author, category, description, publishedDate, content) VALUES (?, ?, ?, ?, ?, ?, ?)";
            String category = recommendationEngine.categorizeArticle(content);
            connect= Database.connectDb();
            prepare= connect.prepareStatement(sql);
            prepare.setString(1, source);
            prepare.setString(2, title);
            prepare.setString(3, author);
            prepare.setString(4, category);
            prepare.setString(5, description);
            prepare.setDate(6, publishedDate);
            prepare.setString(7, content);
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

    public static void main(String[] args) {
        ArticleDAO articleDAO = new ArticleDAOImpl();
        articleDAO.addArticle("Global Markets Surge", "James Monroe","Herald", "Global stock markets have recently been experiencing a robust surge", Date.valueOf("2024-02-02"), "A newly released family movie titled 'The Enchanted Forest' is captivating children and families alike with its magical storyline and beautiful animation. Sophia Grant, a film critic, describes the film as a whimsical adventure that follows a young girl as she journeys through a mystical forest to save her village from an ancient curse.\n\nThe movie’s themes of bravery, teamwork, and the power of friendship resonate with young audiences, and parents appreciate its wholesome content. With breathtaking visuals and a heartwarming storyline, 'The Enchanted Forest' is quickly becoming a must-watch for families looking for engaging, age-appropriate entertainment.\n\nThe film’s success is attributed to its balance of excitement and meaningful messages, making it a perfect addition to family movie nights");
    }
}
