package com.example.newsapplicationversion1.models;

import java.util.List;
import java.util.stream.Collectors;

public class UserPreferences {
    private int preferenceId;
    private int userId;
    private List<String> preferredCategories;

    // Default constructor
    public UserPreferences() {
        this.preferenceId = 0;
        this.userId = 0;
    }
    public UserPreferences(int preferenceId, int userId, List<String> preferredCategories) {
        this.preferenceId = preferenceId;
        this.userId = userId;
        this.preferredCategories = preferredCategories;
    }

    // Convenience constructor
    public UserPreferences(int userId, List<String> preferredCategories) {
        this.userId = userId;
        this.preferredCategories = preferredCategories;
    }

    public int getPreferenceId() {
        return preferenceId;
    }
    public void setPreferenceId(int preferenceId) {
        this.preferenceId = preferenceId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public List<String> getPreferredCategories() {
        return preferredCategories;
    }
    public void setPreferredCategories(List<String> preferredCategories) {
        if (preferredCategories != null) {
            // Optional: Add validation for categories
            this.preferredCategories = preferredCategories.stream()
                    .filter(category -> category != null && !category.trim().isEmpty())
                    .collect(Collectors.toList());
        } else {
            this.preferredCategories = null;
        }
    }
}
