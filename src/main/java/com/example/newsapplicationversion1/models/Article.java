package com.example.newsapplicationversion1.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;
import java.util.Objects;


public class Article {
    @JsonProperty
    private int articleId;
    @JsonProperty
    private String source;
    @JsonProperty
    private String title;
    @JsonProperty
    private String author;
    @JsonProperty
    private String category;
    @JsonProperty
    private String description;
    @JsonProperty
    private Date publishedDate;
    @JsonProperty
    private String content;
    @JsonProperty
    private String imageUrl;

    public Article(int articleId, String source, String title, String author, String category, String description, Date publishedDate, String content) {
        this.articleId = articleId;
        this.source = source;
        this.title = title;
        this.author = author;
        this.category = category;
        this.description = description;
        this.publishedDate = publishedDate;
        this.content = content;
        this.imageUrl = "/com/example/newsapplicationversion1/images/" + articleId%20 + ".jpg";
    }
    public Article(String source, String title, String author, String category, String description, Date publishedDate, String content) {
        this.source = source;
        this.title = title;
        this.author = author;
        this.category = category;
        this.description = description;
        this.publishedDate = publishedDate;
        this.content = content;
    }
    public Article() {
        this.articleId = 0;
        this.source = "";
        this.title = "";
        this.author = "";
        this.category = "";
        this.description = "";
        long millis=System.currentTimeMillis();
        this.publishedDate = new Date(millis);
        this.content = "";
        this.imageUrl = null;
    }
    public int getArticleId() {
        return articleId;
    }
    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public java.sql.Date getPublishedDate() {
        return (java.sql.Date) publishedDate;
    }
    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = "/com/example/newsapplicationversion1/images/" + articleId + ".jpg";
    }

    public String toString(){
        return "Article [articleId=" + articleId + ", source=" + source + ", title=" + title + ", author=" + author + ", category=" + category + ", description=" + description + ", publishedDate=" + publishedDate + "]";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return articleId == article.articleId; // Use articleId for uniqueness
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleId);  // Ensure consistent hashing by articleId
    }


}
