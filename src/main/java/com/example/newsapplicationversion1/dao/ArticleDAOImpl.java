package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.data.Database;
import com.example.newsapplicationversion1.models.Article;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArticleDAOImpl implements ArticleDAO {
    private static Connection connect;
    private static PreparedStatement prepare;
    private static ResultSet resultSet;

    public List<Article> getAllArticles(){
        try {
            String sql = "select * from articles";
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            resultSet = prepare.executeQuery();
            List<Article> articles = new ArrayList<Article>();
            while (resultSet.next()) {
                Article article = new Article(resultSet.getInt("articleID"), resultSet.getString("source"), resultSet.getString("title"), resultSet.getString("author"),resultSet.getString("category"),resultSet.getString("description"),resultSet.getDate("publishedDate"), resultSet.getString("content"));
                articles.add(article);
            }
            return articles;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public Article getArticle(int articleID) {
        try {
            String sql = "select * from articles where articleID=?";
            connect= Database.connectDb();
            prepare= connect.prepareStatement(sql);
            resultSet = prepare.executeQuery();
            Article article = new Article(resultSet.getInt("articleID"), resultSet.getString("source"), resultSet.getString("title"), resultSet.getString("author"),resultSet.getString("category"),resultSet.getString("description"),resultSet.getDate("publishedDate"), resultSet.getString("content"));
            return article;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addArticle(Article article) {
        try{
            String sql = "INSERT INTO ARTICLES (source, title, author, category, description, publishedDate, content) VALUES (?, ?, ?, ?, ?, ?, ?)";
            connect= Database.connectDb();
            prepare= connect.prepareStatement(sql);
            prepare.setString(1, article.getSource());
            prepare.setString(2, article.getTitle());
            prepare.setString(3, article.getAuthor());
            prepare.setString(4, article.getCategory());
            prepare.setString(5, article.getDescription());
            prepare.setDate(6, article.getPublishedDate());
            prepare.setString(7, article.getContent());
            prepare.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return;
        }
    }
    public List<Article> getRecommendedArticles(List<Integer> recommendedArticles) {
        try{
            String sql = "select * from articles where articleID in recommendedArticles ";
            connect= Database.connectDb();
            prepare= connect.prepareStatement(sql);
            resultSet = prepare.executeQuery();
            List<Article> articles = new ArrayList<Article>();
            while (resultSet.next()) {
                Article article = new Article(resultSet.getInt("articleID"), resultSet.getString("source"), resultSet.getString("title"), resultSet.getString("author"),resultSet.getString("category"),resultSet.getString("description"),resultSet.getDate("publishedDate"), resultSet.getString("content"));
                articles.add(article);
            }
            return articles;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
