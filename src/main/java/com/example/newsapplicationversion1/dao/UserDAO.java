package com.example.newsapplicationversion1.dao;

public interface UserDAO {
    void createUser(String firstName, String lastName, String email, String password);
    Boolean checkUserExists(String email);
}
