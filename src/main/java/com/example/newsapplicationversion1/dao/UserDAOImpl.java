package com.example.newsapplicationversion1.dao;

// Create users
// Check if user exists and login
// Logout user
// Retrieve all users
// Get user by email

import com.example.newsapplicationversion1.data.Database;
import com.example.newsapplicationversion1.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAOImpl implements UserDAO {
    private static Connection connect;
    private static PreparedStatement prepare;
    private static ResultSet resultSet;

    public void createUser(String firstName, String lastName, String email, String password) {
        String sql = "INSERT INTO USERS (firstName, lastName, email, password, role, createdAt, lastLogin) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try {
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, firstName);
            prepare.setString(2, lastName);
            prepare.setString(3, email);
            prepare.setString(4, password);
            prepare.setString(5, "Client");
            prepare.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            prepare.setDate(7, new java.sql.Date(System.currentTimeMillis()));
            prepare.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Boolean checkUserExists(String email) {
        try {
            String sql = "SELECT * FROM USERS WHERE email = ?";
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, email);
            resultSet = prepare.executeQuery();
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public User getUserByEmail(String email){
        try {
            String sql = "SELECT * FROM USERS WHERE email = ?";
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, email);
            resultSet = prepare.executeQuery();
            if (resultSet.next()) {
                User user = new User(resultSet.getInt("userID"), resultSet.getString("firstName"), resultSet.getString("lastName"), resultSet.getString("email"), resultSet.getString("password"), resultSet.getString("role"));
                return user;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
