package com.example.newsapplicationversion1.controllers;

import com.example.newsapplicationversion1.dao.ArticleDAO;
import com.example.newsapplicationversion1.models.Article;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
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
    @FXML
    private ScrollPane mainScrollPane;
    @FXML
    private AnchorPane mainNewsPane;

    public void close(){
        System.exit(0);
    }



    public void populateTilePane(TilePane tilePane, List<Article> articles){
        tilePane.getChildren().clear();
        tilePane.setPadding(new Insets(10,10,10,10));
        tilePane.setHgap(10);
        tilePane.setVgap(10);
        tilePane.setPrefWidth(720);
        tilePane.setPrefColumns(4);
        for (Article article : articles){
            VBox tile = new VBox();
            tile.setPrefSize(300, 220);
            tile.setPadding(new Insets(10));
            tile.setSpacing(5);
            tile.setStyle("-fx-background-color: #fff; -fx-border-color: #fff; -fx-border-width: 1px; -fx-border-radius: 5px;");
            // Image
            Image image;
            try {
                image = new Image(Objects.requireNonNull(getClass().getResource("/com/example/newsapplicationversion1/images/" + ((int) article.getArticleId())%20 + ".jpg")).toString(), 200, 150, true, true);
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
            // Label descriptionLabel = new Label(article.getDescription());
            // descriptionLabel.setWrapText(true);
            // descriptionLabel.setStyle("-fx-font-size: 12px; -fx-alignment: CENTER-LEFT");

            // Date
            Label dateLabel = new Label(article.getPublishedDate().toString());
            dateLabel.setStyle("fx-font-size: 12px; -fx-alignment: CENTER-RIGHT");

            // Add all the labels and images to the tile
            tile.getChildren().addAll(titleLabel, dateLabel);
            tile.setStyle("-fx-background-color: #fff; -fx-pref-width: 300px; -fx-pref-height: 200px");
            tile.setOnMouseClicked(event -> loadArticle(article));

            // Add new tile to the tilepane
            tilePane.getChildren().add(tile);
        }
    }
    public void loadArticle(Article article){
        newsTiles.getChildren().clear();
        newsTiles.setPrefColumns(1);
        newsTiles.setPadding(new Insets(0,0,0,0));
        newsTiles.setStyle("-fx-alignment: TOP_LEFT;"); // Center content
        newsTiles.setPrefWidth(700);


        // Load the articles detail UI
        VBox articleDetailsPane = new VBox();
        articleDetailsPane.setSpacing(20);
        articleDetailsPane.setPrefWidth(600);
        articleDetailsPane.setPadding(new Insets(10));
        articleDetailsPane.setAlignment(Pos.TOP_LEFT);

        // Article title
        Label titleLabel = new Label(article.getTitle());
        titleLabel.setWrapText(true);
        titleLabel.setStyle("-fx-text-fill: black; -fx-font-size: 24px; -fx-font-weight: bold");
        titleLabel.setMaxWidth(600);

        // Article author
        Label authorLabel = new Label(article.getAuthor());
        authorLabel.setWrapText(true);
        authorLabel.setStyle("-fx-text-fill: black; -fx-font-family: Arial; -fx-alignment: CENTER-LEFT");
        authorLabel.setMaxWidth(600);

        // Article Description
        Label descriptionLabel = new Label(article.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-text-fill: gray; -fx-font-family: Arial;");
        descriptionLabel.setMaxWidth(600);

        // Date published
        Label dateLabel = new Label(article.getPublishedDate().toString());
        dateLabel.setWrapText(true);
        dateLabel.setStyle("-fx-text-fill: black; -fx-font-family: Arial; -fx-alignment: CENTER-RIGHT");
        dateLabel.setMaxWidth(600);

        // Image
        Image image;
        try {
            image = new Image(Objects.requireNonNull(getClass().getResource("/com/example/newsapplicationversion1/images/" + ((int) article.getArticleId())%20 + ".jpg")).toString(), 600, 450, true, true);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(450);
            imageView.setFitWidth(600);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setStyle("-fx-alignment: CENTER-LEFT");
            articleDetailsPane.getChildren().add(imageView);
        } catch (Exception e) {
            System.err.println("Failed to load image: " + e.getMessage());
            // load a default image or set `image` to null as a fallback
            
        }

        // Content
        Label contentLabel = new Label(article.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(600);

        articleDetailsPane.getChildren().addAll(titleLabel, authorLabel,  dateLabel, descriptionLabel, contentLabel);

        // Wrap the details pane in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(articleDetailsPane);
        scrollPane.setPrefWidth(newsTiles.getPrefWidth());
        scrollPane.setFitToWidth(true); // Disable horizontal scrolling
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // No horizontal scrollbar
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scrollbar as needed

        scrollPane.setStyle("-fx-background-color: transparent;"); // Optional styling

        mainScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Adjust width dynamically
        Platform.runLater(() -> {
            double width = newsTiles.getWidth();
            scrollPane.setPrefWidth(width);
            articleDetailsPane.setPrefWidth(width - 40); // Leave padding for scroll bar
            System.out.println("Updated widths -> newsTiles: " + width + ", scrollPane: " + scrollPane.getPrefWidth() + ", articleDetailsPane: "+ articleDetailsPane.getPrefWidth());
        });

        newsTiles.getChildren().add(scrollPane);
        System.out.println("newsTiles width: " + newsTiles.getWidth());
        System.out.println("scrollPane width: " + scrollPane.getWidth());
        System.out.println("articleDetailsPane width: " + articleDetailsPane.getWidth());

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateTilePane(newsTiles, Objects.requireNonNull(ArticleDAO.getAllArticles()));
    }
}