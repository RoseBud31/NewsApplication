package com.example.newsapplicationversion1.session;

import com.example.newsapplicationversion1.models.User;

public class SessionManager {
    public static User currentUser;

    public SessionManager(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }
    public void setCurrentUser(User user) {
        currentUser = user;
    }
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    public void logout() {
        currentUser = null;
    }

    public int getUserId() {
        return currentUser.getUserId();
    }
}
