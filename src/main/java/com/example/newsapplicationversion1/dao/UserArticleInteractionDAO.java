package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.data.Database;
import com.example.newsapplicationversion1.models.UserArticleInteraction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserArticleInteractionDAO {
    private static Connection connect;
    private static PreparedStatement prepare;
    private static ResultSet resultSet;

    public static void logInteraction(int userId, int articleId, int timeSpent, boolean liked, Date readAt) {
        String sql = "INSERT INTO READINGHISTORY (userID, articleID, timeSpent, liked, readAt) VALUES (?, ?, ?, ?, ?)";
        try{
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userId);
            prepare.setInt(2, articleId);
            prepare.setInt(3, timeSpent);
            prepare.setBoolean(4, liked);
            prepare.setDate(5, readAt);
            prepare.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<UserArticleInteraction> readInteractionsForUser(int userId){
        String sql = "SELECT * FROM READINGHISTORY WHERE userID = ?";
        try{
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userId);
            resultSet = prepare.executeQuery();
            List<UserArticleInteraction> readingHistory = new ArrayList<>();
            while(resultSet.next()){
                readingHistory.add(new UserArticleInteraction(resultSet.getInt("historyID"), resultSet.getInt("userID"), resultSet.getInt("articleID"), resultSet.getBoolean("liked"), resultSet.getInt("timeSpent"), resultSet.getDate("readAt")));
            }
            resultSet.close();
            return readingHistory;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
