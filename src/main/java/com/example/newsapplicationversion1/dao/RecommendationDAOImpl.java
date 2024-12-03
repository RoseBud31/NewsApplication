package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.data.Database;
import com.example.newsapplicationversion1.models.Article;
import com.example.newsapplicationversion1.models.User;
import com.example.newsapplicationversion1.session.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecommendationDAOImpl implements RecommendationDAO {
    private static Connection connect;
    private static PreparedStatement prepare;
    private static ResultSet resultSet;
    User currentUser = SessionManager.currentUser;

    public void recordRecommendation(int userId, int articleId, Date recommendedAt) {
        String sql = "INSERT INTO RECOMMENDEDARTICLES (userID, articleID, recommendedAt) VALUES (?, ?, ?)";
        try{
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userId);
            prepare.setInt(2, articleId);
            prepare.setDate(3, recommendedAt);
            prepare.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void recordRecommendations(List<Article> articles) {
        for (Article article : articles) {
            long millis=System.currentTimeMillis();
            final Date date=new Date(millis);
            if (checkForDuplicate(currentUser.getUserId(), article.getArticleId())) {
                System.err.println("duplicate article id: " + article.getArticleId());
                continue;
            }
            recordRecommendation(currentUser.getUserId(), article.getArticleId(), date);
        }
    }

    public List<Article> getRecommendations(int userId) {
        String sql = "SELECT * FROM RECOMMENDEDARTICLES WHERE userID = ?";

        try{
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userId);
            resultSet = prepare.executeQuery();
            List<Article> articles = new ArrayList<>();
            ArticleDAO articleDAO = new ArticleDAOImpl();
            while (resultSet.next()) {
                Article article = articleDAO.getArticle(resultSet.getInt("articleID"));
                articles.add(article);
            }
            return articles;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean checkForDuplicate(int userId, int articleId) {
        String sql = "SELECT * FROM RECOMMENDEDARTICLES WHERE userID = ? AND articleID = ?";
        try{
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userId);
            prepare.setInt(2, articleId);
            resultSet = prepare.executeQuery();
            return resultSet.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
