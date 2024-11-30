package com.example.newsapplicationversion1.controllers;

import com.example.newsapplicationversion1.dao.UserDAO;
import com.example.newsapplicationversion1.dao.UserDAOImpl;
import com.example.newsapplicationversion1.dao.UserPreferencesDAO;
import com.example.newsapplicationversion1.dao.UserPreferencesDAOImpl;
import com.example.newsapplicationversion1.data.Database;
import com.example.newsapplicationversion1.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.newsapplicationversion1.controllers.LoginController.changeScene;

public class CreateAccountController implements Initializable {
    @FXML
    public TextField firstName;
    @FXML
    public TextField lastName;
    @FXML
    public TextField email;
    @FXML
    public PasswordField confirmPassword;
    @FXML
    public PasswordField password;
    @FXML
    public CheckBox general;
    @FXML
    public CheckBox entertainment;
    @FXML
    public CheckBox science;
    @FXML
    public CheckBox technology;
    @FXML
    public CheckBox sports;
    @FXML
    public CheckBox health;
    @FXML
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


    public SessionManager createClientAccount() throws SQLException {
        Alert alert;
        UserDAO userDAO = new UserDAOImpl();
        UserPreferencesDAO userPreferencesDAO = new UserPreferencesDAOImpl();
        try {
            if (userDAO.checkUserExists(email.getText())){
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText(null);
                alert.setContentText("Email already exists");
                alert.showAndWait();
                loginAccountLink();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
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
                        userDAO.createUser(firstName.getText(), lastName.getText(), email.getText(), password.getText());
                        List<String> preferredCategories = getCategories();
                        userPreferencesDAO.addUserPreferences(userDAO.getUserByEmail(email.getText()).getUserId(), preferredCategories);
                        // Then proceed to dashboard form if client
                        SessionManager  currentUser = new SessionManager(userDAO.getUserByEmail(email.getText()));

                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information Message");
                        alert.setHeaderText(null);
                        alert.setContentText("Successful account creation");
                        alert.showAndWait();

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
                        return currentUser;
                    }
                    else {
                        // Then error message will appear
                        alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error Message");
                        alert.setHeaderText(null);
                        alert.setContentText("Passwords do not match");
                        alert.showAndWait();
                        return null;
                    }

                } else {
                    // Then error message will appear
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Email is not valid");
                    alert.showAndWait();
                    return null;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());;
        }
        return null;
    }

    @NotNull
    private List<String> getCategories() {
        List<String> preferredCategories = new ArrayList<>();
        if (health.isSelected()) {
            preferredCategories.add("Health");
        }
        if (business.isSelected()) {
            preferredCategories.add("Business");
        }
        if (science.isSelected()) {
            preferredCategories.add("Science");
        }
        if (technology.isSelected()) {
            preferredCategories.add("Technology");
        }
        if (sports.isSelected()) {
            preferredCategories.add("Sports");
        }
        if (general.isSelected()) {
            preferredCategories.add("General");
        }
        if (entertainment.isSelected()) {
            preferredCategories.add("Entertainment");
        }
        return preferredCategories;
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
