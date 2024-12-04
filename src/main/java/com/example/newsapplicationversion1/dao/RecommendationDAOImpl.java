package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.data.Database;
import com.example.newsapplicationversion1.models.Article;
import com.example.newsapplicationversion1.models.User;
import com.example.newsapplicationversion1.session.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecommendationDAOImpl implements RecommendationDAO {
    private static Connection connect;
    private static PreparedStatement prepare;
    private static ResultSet resultSet;
    User currentUser = SessionManager.currentUser;

    // Method to record a recommendation
    public void recordRecommendation(int userId, int articleId, Date recommendedAt) {
        String sql = "INSERT INTO RECOMMENDEDARTICLES (userID, articleID, recommendedAt) VALUES (?, ?, ?)";
        try {
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userId);
            prepare.setInt(2, articleId);
            prepare.setDate(3, recommendedAt);
            prepare.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error executing recordRecommendation: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            closeResources();
        }
    }
    // Helper method to close resources
    private void closeResources() {
        try {
            if (resultSet != null) resultSet.close();
            if (prepare != null) prepare.close();
            if (connect != null) connect.close();
        } catch (SQLException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }

    @Override
    public void recordRecommendations(List<Article> articles, User currentUser) {
        for (Article article : articles) {
            long millis=System.currentTimeMillis();
            Date date=new Date(millis);
            if (checkForDuplicate(currentUser.getUserId(), article.getArticleId())) {
                System.err.println("duplicate article id: " + article.getArticleId());
                continue;
            }
            recordRecommendation(currentUser.getUserId(), article.getArticleId(), date);
        }
    }


    public boolean checkForDuplicate(int userId, int articleId) {
        String sql = "SELECT * FROM RECOMMENDEDARTICLES WHERE userID = ? AND articleID = ?";
        try {
            connect = Database.connectDb();
            if (connect == null) {
                throw new SQLException("Failed to connect to the database.");
            }
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userId);
            prepare.setInt(2, articleId);
            resultSet = prepare.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Error checking for duplicate: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            closeResources();
        }
    }

    @Override
    public List<Integer> getRecommendations(int userId) {
        List<Integer> recommendations = new ArrayList<>();
        String sql = "SELECT * FROM RECOMMENDEDARTICLES WHERE userID = ?";
        try {
            connect = Database.connectDb();
            if (connect == null) {
                throw new SQLException("Failed to connect to the database.");
            }
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userId);
            resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                int articleId = resultSet.getInt("articleID");
                recommendations.add(articleId);
            }
            return recommendations;
        } catch (SQLException e) {
            System.err.println("Error checking for duplicate: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
