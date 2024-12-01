package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.data.Database;
import com.example.newsapplicationversion1.models.UserPreferences;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserPreferencesDAOImpl implements UserPreferencesDAO {
    private static Connection connect;
    private static PreparedStatement prepare;
    private static ResultSet resultSet;

    public void addUserPreferences(int userId, List<String> preferredCategories) {
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
    public UserPreferences getUserPreferences(int userID) {
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

                // Convert comma-separated list value back to list
                String keywordsString = resultSet.getString("preferredKeywords");
                List<String> preferredKeywords = new ArrayList<String>();
                if (keywordsString != null && !keywordsString.isEmpty()) {
                    preferredKeywords = Arrays.asList(keywordsString.split(","));
                }
                userPreferences.setPreferredCategories(preferredCategories);
                userPreferences.setPreferredKeywords(preferredKeywords);
                return userPreferences;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateUserKeywords(int userId, List<String> newKeywords) {
        String sql = "SELECT preferredKeywords FROM USERPREFERENCES WHERE userID = ?";
        try{
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userId);
            resultSet = prepare.executeQuery();
            if (resultSet.next()) {
                String existingKeywords = resultSet.getString("preferredKeywords");
                List<String> updatedKeywords = mergeKeywords(existingKeywords, newKeywords);

                String sqlUpdate = "UPDATE USERPREFERENCES SET preferredKeywords = ? WHERE userID = ?";
                try{
                    prepare = connect.prepareStatement(sqlUpdate);
                    prepare.setString(1, String.join(",", updatedKeywords));
                    prepare.setInt(2, userId);
                    prepare.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    private List<String> mergeKeywords(String existingKeywords, List<String> newKeywords) {
        Set<String> keywords = new HashSet<>();
        if (existingKeywords != null && !existingKeywords.isEmpty()) {
            keywords.addAll(Arrays.asList(existingKeywords.split(",")));
        }
        keywords.addAll(newKeywords);
        return new ArrayList<>(keywords);
    }
}
