package com.example.newsapplicationversion1.config;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv dotenv = Dotenv.load();

    public static String getAPIKey() {
        return dotenv.get("API_KEY");
    }
    public static String getDbUser() {
        return dotenv.get("MYSQL_USER");
    }
    public static String getDbPassword() {
        return dotenv.get("MYSQL_PASSWORD");
    }

}
