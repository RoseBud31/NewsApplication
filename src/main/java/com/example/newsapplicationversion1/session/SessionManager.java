package com.example.newsapplicationversion1.session;

import com.example.newsapplicationversion1.models.User;

public class SessionManager {
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    public static void logout() {
        currentUser = null;
    }

    public int getUserId() {
        return currentUser.getUserId();
    }
}
