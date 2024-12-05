
package com.example.newsapplicationversion1.controllers;

import com.example.newsapplicationversion1.dao.*;
import com.example.newsapplicationversion1.models.Article;
import com.example.newsapplicationversion1.models.User;
import com.example.newsapplicationversion1.models.UserArticleInteraction;
import com.example.newsapplicationversion1.services.RecommendationEngine;
import com.example.newsapplicationversion1.session.SessionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;


public class DashboardController implements Initializable {
    private Boolean liked;
    User currentUser = SessionManager.currentUser;
    UserDAO userDAO = new UserDAOImpl();
    private double x = 0 ;
    private double y = 0;
    ArticleDAO articleDAO = new ArticleDAOImpl();
    UserArticleInteractionDAO userArticleInteractionDAO = new UserArticleInteractionDAOImpl();
    long timeStarted = new SessionManager(System.currentTimeMillis()).getTimeStarted();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @FXML
    private Button close;
    @FXML
    private Button home;
    @FXML
    private Button readingHistory;
    @FXML
    private Button explore;
    @FXML
    private TilePane newsTiles;
    @FXML
    private ScrollPane mainScrollPane;
    @FXML
    private AnchorPane mainNewsPane;
    @FXML
    private Button logout;
    @FXML
    private Label dateTime;



    public DashboardController() throws SQLException {
    }
    public void close(){
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to exit?");
            Optional<ButtonType> option = alert.showAndWait();

            if (option.get().equals(ButtonType.OK)) {
                recommendArticlesOnInteract();
                userDAO.logoutUser(currentUser.getEmail());
                currentUser = null;
                System.exit(0);
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDateTime(Label dateTime){
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy");
        String formattedDate = now.format(formatter);
        dateTime.setText(formattedDate);
    }

    public void populateTilePane(TilePane tilePane, List<Article> articles){
        tilePane.getChildren().clear();
        tilePane.setPadding(new Insets(10,10,10,10));
        tilePane.setHgap(10);
        tilePane.setVgap(10);
        tilePane.setPrefWidth(720);
        tilePane.setPrefColumns(4);
        tilePane.setStyle("-fx-background-color: #fff");
        for (Article article : articles){
            VBox tile = new VBox();
            tile.setPrefSize(200, 250);
            tile.setMaxWidth(200);
            tile.setMaxHeight(250);
            tile.setPadding(new Insets(10));
            tile.setSpacing(5);
            tile.setStyle(" -fx-border-width: 1px; -fx-border-radius: 5px;-fx-arc-width: 30px; -fx-arc-height: 30px;-fx-effect: shadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);-fx-cursor: hand");
            // Image
            Image image;
            try {
                image = new Image(Objects.requireNonNull(getClass().getResource("/com/example/newsapplicationversion1/images/" + ((int) article.getArticleId())%35 + ".jpg")).toString(), 200, 150, true, true);
                Rectangle rectangle = new Rectangle(0, 0, 200, 150);
                rectangle.setArcWidth(15.0);   // Corner radius
                rectangle.setArcHeight(15.0);
                ImagePattern imagePattern = new ImagePattern(image);
                rectangle.setFill(imagePattern);
                rectangle.setEffect(new DropShadow(10, Color.BLACK));  // Shadow
                tile.getChildren().add(rectangle);
            } catch (Exception e) {
                System.err.println("Failed to load image: " + e.getMessage());
                // You could load a default image or set `image` to null as a fallback
            }


            // Title
            Label titleLabel = new Label(article.getTitle());
            titleLabel.setWrapText(true);
            titleLabel.setMaxWidth(200);
            titleLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 14px");

            // Tooltip for title
            Tooltip tooltip = new Tooltip(article.getTitle());
            Tooltip.install(titleLabel, tooltip);

            // Description
            Label categoryLabel = new Label(article.getCategory().toUpperCase());
            categoryLabel.setWrapText(true);
            categoryLabel.setStyle("-fx-font-size: 12px; -fx-alignment: CENTER-LEFT");

            // Date
            Label dateLabel = new Label(article.getPublishedDate().toString());
            dateLabel.setStyle("fx-font-size: 12px; -fx-alignment: CENTER-RIGHT");

            HBox infoLabel = new HBox();
            infoLabel.getChildren().addAll(categoryLabel, dateLabel);
            infoLabel.setSpacing(80); // Optional: Adds spacing between the labels
            infoLabel.setMaxWidth(200);
            infoLabel.setAlignment(Pos.CENTER_LEFT);
            // Ensures the category label pushes date label to the right
            HBox.setHgrow(categoryLabel, Priority.ALWAYS);


            // Add all the labels and images to the tile
            tile.getChildren().addAll(titleLabel, infoLabel);
            tile.setStyle("-fx-background-color: #fff;");
            tile.setOnMouseClicked(event -> {
                loadArticle(article);
                timeStarted = System.currentTimeMillis();
                if (userArticleInteractionDAO.getArticleInteractionType(currentUser.getUserId(), article.getArticleId()) == null){
                    userArticleInteractionDAO.logInteraction(currentUser.getUserId(), article.getArticleId(), 0, "read", LocalDateTime.now());
                };

            });

            // Add new tile to the tilepane
            tilePane.getChildren().add(tile);
        }
    }

    private void trackTimeAndUpdate(long timeStarted, Article article) {
        long exitTime = System.currentTimeMillis();
        int timeSpent = (int) ((exitTime - timeStarted) / 1000); // Time in seconds
        //System.out.println("Time spent on article: " + timeSpent + " seconds");
        userArticleInteractionDAO.updateArticleTime(currentUser.getUserId(), article.getArticleId(), timeSpent);
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
        articleDetailsPane.setPrefWidth(700);
        articleDetailsPane.setPadding(new Insets(10));
        articleDetailsPane.setAlignment(Pos.TOP_LEFT);
        articleDetailsPane.setStyle("-fx-background-color: #fff;");


        // Add event handlers to buttons
        home.setOnAction(event -> {
            trackTimeAndUpdate(timeStarted, article);
            onHomeClicked(event);
        });
        readingHistory.setOnAction(event -> {
            trackTimeAndUpdate(timeStarted, article);
            onReadingHistoryClicked(event);
        });
        explore.setOnAction(event -> {
            trackTimeAndUpdate(timeStarted, article);
            onExploreClicked(event);
        });
        logout.setOnAction(event -> {
            trackTimeAndUpdate(timeStarted, article);
            recommendArticlesOnInteract();
            logout();
        });
        close.setOnAction(event -> {
            trackTimeAndUpdate(timeStarted, article);
            recommendArticlesOnInteract();
            close();
        });

        // Article title
        Label titleLabel = new Label(article.getTitle());
        titleLabel.setWrapText(true);
        titleLabel.setStyle("-fx-text-fill: black; -fx-font-size: 24px; -fx-font-weight: bold");
        titleLabel.setMaxWidth(650);

        // Article author
        Label authorLabel = new Label(article.getAuthor());
        authorLabel.setWrapText(true);
        authorLabel.setStyle("-fx-text-fill: black; -fx-font-family: Arial; -fx-alignment: CENTER-LEFT");
        authorLabel.setMaxWidth(650);

        // Article Description
        Label descriptionLabel = new Label(article.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-text-fill: gray; -fx-font-family: Arial;");
        descriptionLabel.setMaxWidth(650);

        // Date published
        Label dateLabel = new Label(article.getPublishedDate().toString());
        dateLabel.setWrapText(true);
        dateLabel.setStyle("-fx-text-fill: black; -fx-font-family: Arial; -fx-alignment: CENTER-RIGHT");
        dateLabel.setMaxWidth(650);

        // Like button
        Button likeButton = new Button("Like");
        likeButton.setAlignment(Pos.CENTER_LEFT);
        likeButton.setStyle("-fx-background-color: #fff;-fx-border-color: black;-fx-border-width: 1px;-fx-font-weight: bold; -fx-border-radius: 5px; -fx-font-family: Arial; -fx-alignment: CENTER-LEFT");
        UserArticleInteractionDAO userArticleInteractionDAO = new UserArticleInteractionDAOImpl();
        String likeStatus = userArticleInteractionDAO.getArticleInteractionType(currentUser.getUserId(), article.getArticleId());
        liked = Objects.equals(likeStatus, "liked");
        // Initial state of like button
        if (liked) {
            likeButton.setStyle("-fx-text-fill: green; -fx-border-color: green;-fx-background-color: #fff;-fx-border-width: 1px;-fx-font-weight: bold;-fx-border-radius: 5px; -fx-font-family: Arial");
        }
        // Single event handler for like button
        likeButton.setOnAction(event -> {
            liked = !liked;
            setLiked(liked);
            setInteraction(article.getArticleId(), liked);
            if (liked){
                likeButton.setStyle("-fx-text-fill: green; -fx-border-color: green;-fx-background-color: #fff;-fx-border-width: 1px;-fx-font-weight: bold;-fx-border-radius: 5px; -fx-font-family: Arial");
            } else {
                likeButton.setStyle("-fx-background-color: #fff;-fx-border-color: black;-fx-border-width: 1px;-fx-font-weight: bold; -fx-border-radius: 5px; -fx-font-family: Arial; -fx-alignment: CENTER-LEFT");
            };
        });


        // Image
        Image image;
        try {
            image = new Image(Objects.requireNonNull(getClass().getResource("/com/example/newsapplicationversion1/images/" + ((int) article.getArticleId())%35 + ".jpg")).toString(), 600, 450, true, true);
            Rectangle rectangle = new Rectangle(0, 0, 650, 450);
            rectangle.setArcWidth(15.0);   // Corner radius
            rectangle.setArcHeight(15.0);
            ImagePattern imagePattern = new ImagePattern(image);
            rectangle.setFill(imagePattern);
            rectangle.setEffect(new DropShadow(10, Color.BLACK));  // Shadow
            articleDetailsPane.getChildren().add(rectangle);
            articleDetailsPane.setAlignment(Pos.CENTER);
        } catch (Exception e) {
            System.err.println("Failed to load image: " + e.getMessage());
            // load a default image or set `image` to null as a fallback
        }

        // Content
        Label contentLabel = new Label(article.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(650);

        articleDetailsPane.getChildren().addAll(titleLabel, authorLabel, likeButton, dateLabel, descriptionLabel, contentLabel);

        // Wrap the details pane in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(articleDetailsPane);
        scrollPane.setPrefWidth(newsTiles.getPrefWidth());
        scrollPane.setFitToWidth(true); // Disable horizontal scrolling
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // No horizontal scrollbar
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scrollbar as needed

        scrollPane.setStyle("-fx-background-color: transparent;"); // Optional styling

        mainScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainScrollPane.setStyle("-fx-background-color: transparent;");

        scrollPane.prefWidthProperty().bind(newsTiles.widthProperty().subtract(0));

        newsTiles.getChildren().add(scrollPane);
//
    }

    public void setLiked(boolean liked){
        this.liked = liked;
    }
    public void setInteraction(int articleId, boolean liked){
        int userId = currentUser.getUserId();
        String userInteractionType;
        if (liked){
            userInteractionType = "liked";
        } else {
            userInteractionType = "read";
        }

        UserArticleInteractionDAO userArticleInteractionDAO = new UserArticleInteractionDAOImpl();
        userArticleInteractionDAO.setArticleLiked(currentUser.getUserId(), articleId, userInteractionType);
    }

    public void logout(){
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");

            Optional<ButtonType> option = alert.showAndWait();

            if (option.get().equals(ButtonType.OK)) {
                recommendArticlesOnInteract();
                userDAO.logoutUser(currentUser.getEmail());
                currentUser = null;
                //HIDE YOUR DASHBOARD FORM
                logout.getScene().getWindow().hide();

                //LINK YOUR LOGIN FORM
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/newsapplicationversion1/login.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);

                root.setOnMousePressed((MouseEvent event) -> {
                    x = event.getSceneX();
                    y = event.getSceneY();
                });

                root.setOnMouseDragged((MouseEvent event) -> {
                    stage.setX(event.getScreenX() - x);
                    stage.setY(event.getScreenY() - y);

                    stage.setOpacity(.8);
                });

                root.setOnMouseReleased((MouseEvent event) -> {
                    stage.setOpacity(1);
                });

                stage.initStyle(StageStyle.TRANSPARENT);

                stage.setScene(scene);
                stage.show();

            } else {
                return;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private void recommendArticlesOnInteract() {
        RecommendationEngine recommendationEngine = new RecommendationEngine();
        Future<List<Article>> recArticles = recommendationEngine.recommendArticles(30, currentUser);

        try {
            List<Article> articles = recArticles.get();  // Blocking call to fetch results
            RecommendationDAO recommendationDAO = new RecommendationDAOImpl();
            recommendationDAO.recordRecommendations(articles, currentUser);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void onHomeClicked(ActionEvent event) {
        try {
            List<Article> articlesToShow;

            // Fetch history or recommended articles
            List<Article> historyArticles = articleDAO.getArticleHistory(currentUser.getUserId()); // This will never return null
            if (historyArticles.isEmpty()) {
                articlesToShow = articleDAO.getAllArticles();
            } else {
                articlesToShow = articleDAO.getRecommendedArticles(recArticleIds());
            }

            // Update UI in a safe way
            Platform.runLater(() -> {
                newsTiles.getChildren().clear();
                populateTilePane(newsTiles, articlesToShow);
            });

        } catch (Exception e) {
            // Handle exception and fall back to default articles
            Platform.runLater(() -> {
            });
        }

        // Update styles
        home.setStyle("-fx-background-color:linear-gradient(to bottom right, #b6b6b6, #e1e1e1);-fx-alignment: CENTER-LEFT");
        readingHistory.setStyle("-fx-background-color: transparent;-fx-alignment: CENTER-LEFT");
        explore.setStyle("-fx-background-color: transparent; -fx-alignment: CENTER-LEFT");
    }

    public void onReadingHistoryClicked(ActionEvent event) {
        List<Article> historyArticles = articleDAO.getArticleHistory(currentUser.getUserId());

        // Update UI in a safe way
        Platform.runLater(() -> {
            newsTiles.getChildren().clear();
            populateTilePane(newsTiles, historyArticles);
        });

        // Update styles
        home.setStyle("-fx-background-color: transparent; -fx-alignment: CENTER-LEFT");
        explore.setStyle("-fx-background-color: transparent; -fx-alignment: CENTER-LEFT");
        readingHistory.setStyle("-fx-background-color:linear-gradient(to bottom right, #b6b6b6, #e1e1e1);-fx-alignment: CENTER-LEFT");
    }
    public void onExploreClicked(ActionEvent event) {
        List<Article> allArticles = articleDAO.getAllArticles();
        // Update UI in a safe way
        Platform.runLater(() -> {
            newsTiles.getChildren().clear();
            populateTilePane(newsTiles, allArticles);
        });
        // Update styles
        home.setStyle("-fx-background-color: transparent; -fx-alignment: CENTER-LEFT");
        readingHistory.setStyle("-fx-background-color: transparent; -fx-alignment: CENTER-LEFT");
        explore.setStyle("-fx-background-color:linear-gradient(to bottom right, #b6b6b6, #e1e1e1);-fx-alignment: CENTER-LEFT");
    }


    public List<Integer> recArticleIds(){
        RecommendationDAO recommendationDAO = new RecommendationDAOImpl();
        List<Integer> articlesRecommended = new ArrayList<>();
        articlesRecommended.addAll(recommendationDAO.getRecommendations(currentUser.getUserId()));
        return articlesRecommended;
    }



    public void setUI() {
        setDateTime(dateTime);

        executorService.submit(() -> {
            try {
                List<Article> articlesToShow;

                // Fetch history and recommended articles in parallel if necessary
                if (articleDAO.getArticleHistory(currentUser.getUserId()).isEmpty()) {
                    articlesToShow = articleDAO.getAllArticles(); // No history, fetch all articles
                } else {
                    articlesToShow = articleDAO.getRecommendedArticles(recArticleIds());
                }

                // Update the UI on the JavaFX thread
                Platform.runLater(() -> {
                    newsTiles.getChildren().clear();
                    populateTilePane(newsTiles, articlesToShow);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    newsTiles.getChildren().clear();;
                    populateTilePane(newsTiles, articleDAO.getAllArticles());
                });
            }
        });

        home.setOnAction(this::onHomeClicked);
        readingHistory.setOnAction(this::onReadingHistoryClicked);
        explore.setOnAction(this::onExploreClicked);
        logout.setOnAction(event -> {
            // Handle logout functionality
            logout();
        });

        close.setOnAction(event -> {
            close();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(this::setUI);
    }
}
