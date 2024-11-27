package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.data.Database;
import com.example.newsapplicationversion1.models.Article;
import com.example.newsapplicationversion1.models.Recommendation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecommendationDAOImpl implements RecommendationDAO {
    private static Connection connect;
    private static PreparedStatement prepare;
    private static ResultSet resultSet;

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
    public Recommendation getRecommendations(int userId) {
        String sql = "SELECT * FROM RECOMMENDEDARTICLES WHERE userID = ?";

        try{
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            resultSet = prepare.executeQuery();
            List<Article> articles = new ArrayList<>();
            ArticleDAO articleDAO = new ArticleDAOImpl();
            while (resultSet.next()) {
                Article article = articleDAO.getArticle(resultSet.getInt("articleID"));
                articles.add(article);
            }
            return new Recommendation(userId, articles);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
