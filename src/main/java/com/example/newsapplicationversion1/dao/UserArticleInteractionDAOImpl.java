package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.data.Database;
import com.example.newsapplicationversion1.models.UserArticleInteraction;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserArticleInteractionDAOImpl implements UserArticleInteractionDAO {
    private static Connection connect;
    private static PreparedStatement prepare;
    private static ResultSet resultSet;

    public void logInteraction(int userId, int articleId, int timeSpent, String interactionType, LocalDateTime readAt) {
        String sql = "INSERT INTO READINGHISTORY (userID, articleID, timeSpent, interactionType, readAt) VALUES (?, ?, ?, ?, ?)";
        try{
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userId);
            prepare.setInt(2, articleId);
            prepare.setInt(3, timeSpent);
            prepare.setString(4, interactionType);
            java.sql.Date sqlDate = java.sql.Date.valueOf(readAt.toLocalDate());
            prepare.setDate(5, sqlDate);
            prepare.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<UserArticleInteraction> readInteractionsForUser(int userId) {
        String sql = "SELECT * FROM READINGHISTORY WHERE userID = ?";
        List<UserArticleInteraction> readingHistory = new ArrayList<>();

        // Use try-with-resources to ensure proper resource management
        try (Connection connect = Database.connectDb();
             PreparedStatement prepare = connect.prepareStatement(sql)) {

            prepare.setInt(1, userId);
            try (ResultSet resultSet = prepare.executeQuery()) {
                while (resultSet.next()) {
                    readingHistory.add(new UserArticleInteraction(
                            resultSet.getInt("historyID"),
                            resultSet.getInt("userID"),
                            resultSet.getInt("articleID"),
                            resultSet.getString("interactionType"),
                            resultSet.getInt("timeSpent"),
                            resultSet.getDate("readAt")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception or handle it in another way
            throw new RuntimeException("Error while reading interactions for user", e);
        }
        return readingHistory;
    }


    @Override
    public void setArticleLiked(int userId, int articleId, String interactionType) {
        String sql = "UPDATE READINGHISTORY SET interactionType = ? WHERE articleID = ? && userID = ?";
        try{
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, interactionType);
            prepare.setInt(2, articleId);
            prepare.setInt(3, userId);
            prepare.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getArticleInteractionType(int userId, int articleId) {
        String sql = "SELECT interactionType FROM READINGHISTORY WHERE userID = ? && articleID = ?";
        try{
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userId);
            prepare.setInt(2, articleId);
            resultSet = prepare.executeQuery();
            if(resultSet.next()){
                return resultSet.getString("interactionType");
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public int getArticleInteractionTime(int userId, int articleId) {
        String sql = "SELECT timeSpent FROM READINGHISTORY WHERE userID = ? && articleID = ?";
        try{
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userId);
            prepare.setInt(2, articleId);
            resultSet = prepare.executeQuery();
            if(resultSet.next()){
                resultSet.getInt("timeSpent");
            }
            return 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateArticleTime(int userId, int articleId, int timeSpent) {
        String sql = "UPDATE READINGHISTORY SET timeSpent = ? WHERE articleID = ? && userID = ?";
        try{
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, timeSpent);
            prepare.setInt(2, articleId);
            prepare.setInt(3, userId);
            prepare.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
