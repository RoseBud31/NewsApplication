package com.example.newsapplicationversion1.models;

import java.util.ArrayList;
import java.util.List;

public class Client extends User {
    private UserPreferences preferences;
    private ArrayList<Article> recommendationList;

    public Client(int userID, String firstName, String lastName, String email, String password, String role, UserPreferences preferences) {
        super(userID, firstName, lastName, email, password, role);
        this.preferences = preferences;
        recommendationList = new ArrayList<Article>();
    }
    public Client(int userID, String firstName, String lastName, String email, String password, String role) {
        super(userID, firstName, lastName, email, password, role);
        this.preferences = new UserPreferences();
        recommendationList = new ArrayList<Article>();
    }

}
