package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.data.Database;
import com.example.newsapplicationversion1.models.Article;
import com.example.newsapplicationversion1.services.RecommendationEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ArticleDAOImpl implements ArticleDAO {
    private final List<Article> articles = new CopyOnWriteArrayList<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10); // Thread pool with 10 threads
    private final RecommendationEngine recommendationEngine = new RecommendationEngine();

    private static Connection connect;
    private static PreparedStatement prepare;
    private static ResultSet resultSet;

    public List<Article> getAllArticles(){
        try {
            String sql = "select * from articles";
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            resultSet = prepare.executeQuery();
            List<Future<?>> futures = new ArrayList<>();
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

                futures.add(executorService.submit(() -> {
                    Article article = new Article(articleId, source, title, author, category, description, publishedDate, content);
                    articles.add(article);
                }));
                taskCount++;
            }
            for (Future<?> future : futures) {
                future.get(); // Wait for all tasks to complete
            }
            System.out.println(taskCount);
        }  catch (SQLException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return articles;
    }
    public Article getArticle(int articleID) {
        try {
            String sql = "select * from articles where articleID=?";
            connect= Database.connectDb();
            prepare= connect.prepareStatement(sql);
            prepare.setInt(1, articleID);
            resultSet = prepare.executeQuery();
            if (resultSet.next()) {
                Article article = new Article(resultSet.getInt("articleID"), resultSet.getString("source"), resultSet.getString("title"), resultSet.getString("author"),resultSet.getString("category"),resultSet.getString("description"),resultSet.getDate("publishedDate"), resultSet.getString("content"));
                return article;
            }
            return null;
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
    // to display recommended articles
    public List<Article> getRecommendedArticles(List<Integer> articleIDs) {
        List<Future<Article>> futures = new ArrayList<>();
        List<Article> recommendedArticles = new CopyOnWriteArrayList<>();
        String sql = "SELECT * FROM articles WHERE articleID=?";
        try (Connection connect = Database.connectDb()) {
            for (Integer articleID : articleIDs) {
                futures.add(executorService.submit(() -> {
                    try (PreparedStatement prepare = connect.prepareStatement(sql)) {
                        prepare.setInt(1, articleID);
                        try (ResultSet resultSet = prepare.executeQuery()) {
                            if (resultSet.next()) {
                                return new Article(
                                        resultSet.getInt("articleID"),
                                        resultSet.getString("source"),
                                        resultSet.getString("title"),
                                        resultSet.getString("author"),
                                        resultSet.getString("category"),
                                        resultSet.getString("description"),
                                        resultSet.getDate("publishedDate"),
                                        resultSet.getString("content")
                                );
                            }
                        }
                    }
                    return null;
                }));
            }

            for (Future<Article> future : futures) {
                Article article = future.get();
                if (article != null) {
                    recommendedArticles.add(article);
                }
            }

        } catch (SQLException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return recommendedArticles;
    }


    public List<Article> getArticlesByCategory(String category) {
        List<Article> articles1= getAllArticles();
        return articles1.stream().filter(a -> a.getCategory().equals(category)).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        ArticleDAOImpl articleDAOImpl = new ArticleDAOImpl();
        List<Integer> articleIDs = new ArrayList<>();
        articleIDs.add(1);
        articleIDs.add(2);
        articleIDs.add(3);
        articleIDs.add(4);
        articleIDs.add(5);
        articleIDs.add(6);
        articleIDs.add(7);
        System.out.println(articleDAOImpl.getRecommendedArticles(articleIDs));

    }

}
