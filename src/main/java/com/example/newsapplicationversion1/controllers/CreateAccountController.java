package com.example.newsapplicationversion1.controllers;

import com.example.newsapplicationversion1.data.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.newsapplicationversion1.controllers.LoginController.changeScene;

public class CreateAccountController implements Initializable {
    public TextField firstName;
    public TextField lastName;
    public TextField email;
    public PasswordField confirmPassword;
    public PasswordField password;
    public CheckBox general;
    public CheckBox entertainment;
    public CheckBox science;
    public CheckBox technology;
    public CheckBox sports;
    public CheckBox health;
    public CheckBox business;
    @FXML
    private Button createAccountButton;

    private double x = 0 ;
    private double y = 0;

    // Database tools
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet resultSet;
    private PreparedStatement checkEmail;

    public void createClientAccount() throws SQLException {

        String sql = "INSERT INTO USERS (firstName, lastName, email, password, role, createdAt, lastLogin) VALUES(?, ?, ?, ?, ?, ?, ?)";
        String checkSql = "SELECT * FROM USERS WHERE email=?";
        connect = Database.connectDb();

        try {
            Alert alert;

            checkEmail = connect.prepareStatement(checkSql);
            checkEmail.setString(1, email.getText());

            prepare = connect.prepareStatement(sql);
            prepare.setString(1, firstName.getText());
            prepare.setString(2, lastName.getText());
            prepare.setString(3, email.getText());
            prepare.setString(4, password.getText());
            prepare.setString(5, "Client");
            prepare.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            prepare.setDate(7, new java.sql.Date(System.currentTimeMillis()));

            resultSet = checkEmail.executeQuery();

            if (resultSet.next()) {
                // Then inform client already exists and redirect to login form
                email.getText();

                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText(null);
                alert.setContentText("Email already exists");
                alert.showAndWait();

                loginAccountLink();
                return;
            }
            String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
            //Compile regular expression to get the pattern
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email.getText());

            if (firstName.getText().isEmpty() || lastName.getText().isEmpty() || email.getText().isEmpty() || password.getText().isEmpty() || confirmPassword.getText().isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please enter all the fields");
                alert.showAndWait();
            } else {
                if (matcher.matches()) {
                    if (password.getText().equals(confirmPassword.getText())) {

                        // Then proceed to dashboard form if client
                        email.getText();

                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information Message");
                        alert.setHeaderText(null);
                        alert.setContentText("Successful account creation");
                        alert.showAndWait();
                        prepare.executeUpdate();

                        // To hide the createAccount form
                        createAccountButton.getScene().getWindow().hide();

                        // Link the dashboard
                        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/newsapplicationversion1/dashboard.fxml")));

                        Stage stage = new Stage();
                        Scene scene = new Scene(root);

                        root.setOnMousePressed((MouseEvent event) -> {
                            x = event.getSceneX();
                            y = event.getSceneY();
                        });

                        root.setOnMousePressed((MouseEvent event) ->{
                            x = event.getSceneX();
                            y = event.getSceneY();
                        });

                        changeScene(root, stage, scene, x, y);
                    }
                    else {
                        // Then error message will appear
                        alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error Message");
                        alert.setHeaderText(null);
                        alert.setContentText("Passwords do not match");
                        alert.showAndWait();
                    }

                } else {
                    // Then error message will appear
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Email is not valid");
                    alert.showAndWait();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());;
        } finally {
            try {
                if (prepare != null) prepare.close();
                if (connect != null) connect.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());;
            }
        }
    }

    public void loginAccountLink() throws IOException {
        // Link the createAccountForm
        createAccountButton.getScene().getWindow().hide();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/newsapplicationversion1/login.fxml")));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        // Design with css

        root.setOnMousePressed((MouseEvent event) ->{
            x = event.getSceneX();
            y = event.getSceneY();
        });

        changeScene(root, stage, scene, x, y);
    }
    public void close(){
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //TODO
    }
}