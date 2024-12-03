package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.concurrency.ConcurrencyManager;
import com.example.newsapplicationversion1.data.Database;
import com.example.newsapplicationversion1.models.Article;
import com.example.newsapplicationversion1.services.RecommendationEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ArticleDAOImpl implements ArticleDAO {
    private final ConcurrencyManager concurrencyManager = new ConcurrencyManager();
    private final List<Article> articles = new CopyOnWriteArrayList<Article>(); // Very important for concurrency
    private final List<Article> recommendedArticles = new ArrayList<>();
    private static Connection connect;
    private static PreparedStatement prepare;
    private static ResultSet resultSet;
    RecommendationEngine recommendationEngine = new RecommendationEngine();
    RecommendationDAO recommendationDAO = new RecommendationDAOImpl();

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
        ExecutorService executorService = Executors.newFixedThreadPool(10); // Use a thread pool with a fixed number of threads
        List<Article> recommendedArticles = new CopyOnWriteArrayList<>(); // Thread-safe list
        List<Future<?>> futures = new ArrayList<>(); // To track the futures of the submitted tasks

        try {
            connect = Database.connectDb(); // Single connection for all queries
            for (Integer articleID : articleIDs) {
                String sql = "SELECT * FROM articles WHERE articleID=?";
                prepare = connect.prepareStatement(sql);
                prepare.setInt(1, articleID);
                resultSet = prepare.executeQuery();

                if (resultSet.next()) {
                    final int articleId = resultSet.getInt("articleID");
                    final String source = resultSet.getString("source");
                    final String title = resultSet.getString("title");
                    final String author = resultSet.getString("author");
                    final String category = resultSet.getString("category");
                    final String description = resultSet.getString("description");
                    final Date publishedDate = resultSet.getDate("publishedDate");
                    final String content = resultSet.getString("content");

                    // Submit task to executor service to process the article data asynchronously
                    Future<?> future = executorService.submit(() -> {
                        Article article = new Article(articleId, source, title, author, category, description, publishedDate, content);
                        recommendedArticles.add(article); // Add to the thread-safe list
                    });
                    futures.add(future); // Track the future of this task
                }
            }

            // Wait for all tasks to complete
            for (Future<?> future : futures) {
                future.get(); // Blocks until the task is complete
            }

            // Optionally shut down the executor service
            executorService.shutdown();

        } catch (SQLException | InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            try {
                if (prepare != null) prepare.close(); // Close PreparedStatement
                if (resultSet != null) resultSet.close(); // Close ResultSet
                if (connect != null) connect.close(); // Close Connection
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
