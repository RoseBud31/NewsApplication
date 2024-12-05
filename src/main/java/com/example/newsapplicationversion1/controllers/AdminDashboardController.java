package com.example.newsapplicationversion1.controllers;

import com.example.newsapplicationversion1.dao.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminDashboardController implements Initializable {
    @FXML
    private TextField title, author, source, description, datePublished;
    @FXML
    private TextArea content;
    @FXML
    private Button addArticleButton, home, logout;
    @FXML
    private Label dateTime;
    @FXML
    private Button btnUploadCSV; // Link this with Scene Builder

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

    public void handleUploadCSV(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(btnUploadCSV.getScene().getWindow());

        if (file != null) {
            addBulkArticleCSV(file);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("No file selected.");
            alert.showAndWait();
        }
    }
    public void addBulkArticleCSV(File file) {

        Alert alert;
        ArticleDAO articleDAO = new ArticleDAOImpl();
        String regex = "^\\d{4}-\\d{2}-\\d{2}$";
        Pattern pattern = Pattern.compile(regex);

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] nextLine;
            int successCount = 0;
            int errorCount = 0;

            reader.readNext(); // Skip the header row

            while ((nextLine = reader.readNext()) != null) {
                // Ensure there are exactly 6 fields
                if (nextLine.length != 6) {
                    errorCount++;
                    System.out.println("Error: Incorrect number of fields, expected 6 but got " + nextLine.length);
                    continue;
                }

                String source = nextLine[0].trim();
                String title = nextLine[1].trim();
                String author = nextLine[2].trim();
                String description = nextLine[3].trim();
                String datePublished = nextLine[4].trim();
                String content = nextLine[5].trim();

                // Validate the date format
                Matcher dateMatcher = pattern.matcher(datePublished);
                if (dateMatcher.matches()) {
                    try {
                        articleDAO.addArticle(title, author, source, description, Date.valueOf(datePublished), content);
                        successCount++;
                    } catch (Exception e) {
                        errorCount++;
                        System.err.println("Error adding article: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    errorCount++;
                    System.out.println("Error: Invalid date format for article: " + title);
                }
            }

            // Show success/failure count in an alert
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Bulk Upload Result");
            alert.setHeaderText(null);
            alert.setContentText("Articles added successfully: " + successCount + "\nErrors: " + errorCount);
            alert.showAndWait();

        } catch (IOException | CsvValidationException e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Error reading the file.");
            alert.showAndWait();
            System.err.println("Error reading the file: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public void setDateTime(Label dateTime){
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy");
        String formattedDate = now.format(formatter);
        dateTime.setText(formattedDate);
    }
    public void logout() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");

            Optional<ButtonType> option = alert.showAndWait();

            if (option.get().equals(ButtonType.OK)) {
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
    public void close(){
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDateTime(dateTime);
    }

}
