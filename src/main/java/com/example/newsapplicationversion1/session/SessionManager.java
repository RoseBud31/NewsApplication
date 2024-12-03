package com.example.newsapplicationversion1.session;

import com.example.newsapplicationversion1.models.User;

public class SessionManager {
    public static User currentUser;
    public static int timeStarted;

    public SessionManager(User user) {
        this.currentUser = user;
    }
    public SessionManager(long timeStarted) {this.timeStarted = (int) timeStarted;}

    public long getTimeStarted() {return timeStarted;}
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
