package com.example.newsapplicationversion1.models;

import java.util.ArrayList;
import java.util.List;

public class UserPreferences {
    private List<String> preferredCategories;
    private List<ReadingEntry> readingEntries;

    public UserPreferences() {
        this.preferredCategories = new ArrayList<>();
        this.readingEntries = new ArrayList<>();
    }
    public List<String> getPreferredCategories() {
        return preferredCategories;
    }
    public void setPreferredCategories(List<String> preferredCategories) {
        this.preferredCategories = preferredCategories;
    }
    public List<ReadingEntry> getReadingEntries() {
        return readingEntries;
    }
    public void setReadingEntries(List<ReadingEntry> readingEntries) {
        this.readingEntries = readingEntries;
    }
}
