package com.example.newsapplicationversion1.controllers;

import com.example.newsapplicationversion1.dao.ArticleDAO;
import com.example.newsapplicationversion1.models.Article;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Button close;
    @FXML
    private Button home;
    @FXML
    private Button readingHistory;
    @FXML
    private Button settings;
    @FXML
    private TilePane newsTiles;

    public void close(){
        System.exit(0);
    }



    public void populateTilePane(TilePane tilePane, List<Article> articles){
        tilePane.setPadding(new Insets(10,10,10,10));
        tilePane.setHgap(10);
        tilePane.setVgap(10);
        tilePane.setPrefWidth(800);
        tilePane.setPrefColumns(4);
        for (Article article : articles){
            VBox tile = new VBox();
            tile.setPrefSize(200, 300);
            tile.setPadding(new Insets(10));
            tile.setSpacing(5);
            tile.setStyle("-fx-background-color: #fff; -fx-border-color: #fff; -fx-border-width: 1px; -fx-border-radius: 5px;");
            // Image
            Image image;
            try {
                image = new Image(Objects.requireNonNull(getClass().getResource("/com/example/newsapplicationversion1/images/" + article.getArticleId() + ".jpg")).toString(), 200, 150, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(150);
                imageView.setFitWidth(200);
                tile.getChildren().add(imageView);
            } catch (Exception e) {
                System.err.println("Failed to load image: " + e.getMessage());
                // You could load a default image or set `image` to null as a fallback
            }


            // Title
            Label titleLabel = new Label(article.getTitle());
            titleLabel.setWrapText(true);
            titleLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 14px");

            // Description
            Label descriptionLabel = new Label(article.getDescription());
            // descriptionLabel.setWrapText(true);
            descriptionLabel.setStyle("-fx-font-size: 12px; -fx-alignment: CENTER-LEFT");

            // Date
            Label dateLabel = new Label(article.getPublishedDate().toString());
            dateLabel.setStyle("fx-font-size: 12px; -fx-alignment: CENTER-RIGHT");

            // Add all the labels and images to the tile
            tile.getChildren().addAll(titleLabel, descriptionLabel, dateLabel);
            tile.setStyle("-fx-background-color: #fff; -fx-pref-width: 300px; -fx-pref-height: 200px");

            // Add new tile to the tilepane
            tilePane.getChildren().add(tile);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateTilePane(newsTiles, Objects.requireNonNull(ArticleDAO.getAllArticles()));
    }
}