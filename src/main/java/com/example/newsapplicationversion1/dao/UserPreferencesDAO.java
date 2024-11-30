package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.models.UserPreferences;

import java.util.List;

public interface UserPreferencesDAO {
    void addUserPreferences(int userId, List<String> preferredCategories);
    UserPreferences getUserPreferences(int userID);
    void updateUserKeywords(int userId, List<String> newKeywords);
}
