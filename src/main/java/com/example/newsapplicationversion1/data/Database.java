package com.example.newsapplicationversion1.data;

import com.example.newsapplicationversion1.config.Config;

import java.sql.*;


public class Database {

    public static Connection connectDb() throws SQLException {
        String DbUser = Config.getDbUser();
        String DbPassword = Config.getDbPassword();
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/news_db",
                DbUser,
                DbPassword
        );
        return connection;
    }
}
