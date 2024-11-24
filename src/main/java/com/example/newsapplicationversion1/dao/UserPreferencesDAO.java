package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.data.Database;
import com.example.newsapplicationversion1.models.User;
import com.example.newsapplicationversion1.models.UserPreferences;
import com.example.newsapplicationversion1.session.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserPreferencesDAO {
    private static Connection connect;
    private static PreparedStatement prepare;
    private static ResultSet resultSet;

    public static void addUserPreferences(int userId, List<String> preferredCategories) {
        String sql = "INSERT INTO USERPREFERENCES (userID, preferredCategories) VALUES (?,?)";
        try {
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userId);
            String categories = String.join(",", preferredCategories);
            prepare.setString(2, categories);
            prepare.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static UserPreferences getUserPreferences(int userID) {
        String sql = "SELECT * FROM USERPREFERENCES WHERE userID = ?";
        try {
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userID);
            resultSet = prepare.executeQuery();
            if (resultSet.next()) {
                UserPreferences userPreferences = new UserPreferences();
                userPreferences.setUserId(resultSet.getInt("userID"));

                // Convert comma-separated list value back to list
                String categoriesString = resultSet.getString("preferredCategories");
                List<String> preferredCategories = new ArrayList<String>();
                if (categoriesString != null && !categoriesString.isEmpty()) {
                    preferredCategories = Arrays.asList(categoriesString.split(","));
                }
                userPreferences.setPreferredCategories(preferredCategories);
                return userPreferences;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
