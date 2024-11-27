package com.example.newsapplicationversion1.models;

import java.util.Date;

public class UserArticleInteraction {
    private int interactionId;
    private int userId;
    private int articleId;
    private String interactionType;
    private int timeSpentSeconds;
    private Date dateRead;

    public UserArticleInteraction(int interactionId, int userId, int articleId, String interactionType, int timeSpentSeconds, Date dateRead) {
        this.interactionId = interactionId;
        this.userId = userId;
        this.articleId = articleId;
        this.interactionType = interactionType;
        this.timeSpentSeconds = timeSpentSeconds;
        this.dateRead = dateRead;
    }
    public int getInteractionId() {
        return interactionId;
    }
    public void setInteractionId(int interactionId) {
        this.interactionId = interactionId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getArticleId() {
        return articleId;
    }
    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }
    public String interactionType() {
        return interactionType;
    }
    public void setInteractionType(String interactionType) {
        this.interactionType = interactionType;
    }
    public int getTimeSpentSeconds() {
        return timeSpentSeconds;
    }
    public void setTimeSpentSeconds(int timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }
    public Date getDateRead() {
        return dateRead;
    }
    public void setDateRead(Date dateRead) {
        this.dateRead = dateRead;
    }

}
