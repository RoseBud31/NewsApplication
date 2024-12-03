package com.example.newsapplicationversion1.controllers;

import com.example.newsapplicationversion1.dao.*;
import com.example.newsapplicationversion1.models.Article;
import com.example.newsapplicationversion1.session.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminDashboardController implements Initializable {
    @FXML
    private TextField title, author, source, description, datePublished;
    @FXML
    private TextArea content;
    @FXML
    private Button addArticleButton;

    private double x = 0 ;
    private double y = 0;

    public void addArticle() {
        Alert alert;
        ArticleDAO articleDAO = new ArticleDAOImpl();
        String regex = "^\\d{4}-\\d{2}-\\d{2}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(datePublished.getText());
        if (title.getText().isEmpty() || author.getText().isEmpty() || source.getText().isEmpty() || description.getText().isEmpty() || datePublished.getText().isEmpty() || content.getText().isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please enter all the fields");
            alert.showAndWait();
        } else{
            if (matcher.matches()) {
                articleDAO.addArticle(title.getText(), author.getText(), source.getText(), description.getText(), Date.valueOf(datePublished.getText()), content.getText());

                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText(null);
                alert.setContentText("Successful Article Creation");
                alert.showAndWait();
                title.clear(); author.clear(); source.clear(); description.clear(); datePublished.clear(); content.clear();
            } else {
                // Then error message will appear
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Date in the wrong format");
                alert.showAndWait();
            }
        }
    }
    public void close(){
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}
