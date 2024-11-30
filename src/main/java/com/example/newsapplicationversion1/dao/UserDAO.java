package com.example.newsapplicationversion1.dao;

import com.example.newsapplicationversion1.models.User;

public interface UserDAO {
    void createUser(String firstName, String lastName, String email, String password);
    Boolean checkUserExists(String email);
    User getUserByEmail(String email);
}
