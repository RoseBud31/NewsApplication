package com.example.newsapplicationversion1.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;
import com.example.newsapplicationversion1.data.Database;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Stage;

public class LoginController implements Initializable {
    @FXML
    private AnchorPane loginForm;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private Button loginButton;
    @FXML
    private Button close;
    @FXML
    private Button createAccountLink;

    // Database tools
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    // Window
    private double x=0;
    private double y=0;

    public void loginUser() throws SQLException {
        String sql = "SELECT * FROM USERS WHERE email=? AND password=?";
        connect = Database.connectDb();
        try{
            Alert alert;

            prepare = connect.prepareStatement(sql);
            prepare.setString(1, email.getText());
            prepare.setString(2, password.getText());

            result = prepare.executeQuery();

            // Check if fields are empty
            if (email.getText().isEmpty() || password.getText().isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please enter all the fields");
                alert.showAndWait();
            } else {
                if (result.next()) {
                    // Then proceed to dashboard form if client
                    email.getText();

                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully logged in");
                    alert.showAndWait();

                    // To hide the login form
                    loginButton.getScene().getWindow().hide();

                    // Link the dashboard
                    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/newsapplicationversion1/dashboard.fxml")));

                    Stage stage = new Stage();
                    Scene scene = new Scene(root);

                    root.setOnMousePressed((MouseEvent event) -> {
                        x = event.getSceneX();
                        y = event.getSceneY();
                    });

                    root.setOnMouseDragged((MouseEvent event) -> {
                        stage.setX(event.getScreenX() - x);
                        stage.setY(event.getScreenY() - y);
                    });

                    stage.initStyle(StageStyle.TRANSPARENT);
                    stage.setScene(scene);
                    stage.show();
                } else {
                    // Then error message will appear
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Wrong email or password");
                    alert.showAndWait();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());;
        } finally {
            try {
                if (result != null) result.close();
                if (prepare != null) prepare.close();
                if (connect != null) connect.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());;
            }
        }
    }
    public void createAccountLink() throws IOException {
        // Link the createAccountForm
        loginButton.getScene().getWindow().hide();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/newsapplicationversion1/createAccount.fxml")));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        // Design with css

        root.setOnMousePressed((MouseEvent event) ->{
            x = event.getSceneX();
            y = event.getSceneY();
        });

        changeScene(root, stage, scene, x, y);
    }

    public static void changeScene(Parent root, Stage stage, Scene scene, double x, double y) {
        root.setOnMouseDragged((MouseEvent event) ->{
            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);

            stage.setOpacity(.8);
        });

        root.setOnMouseReleased((MouseEvent event) ->{
            stage.setOpacity(1);
        });

        stage.initStyle(StageStyle.TRANSPARENT);

        stage.setScene(scene);
        stage.show();
    }

    public void close(){
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //TODO
    }
}
