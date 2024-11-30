package com.example.newsapplicationversion1;

import com.example.newsapplicationversion1.controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;


import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    private double x = 0 ;
    private double y = 0;
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
        Scene scene = new Scene(root);
        // Design with css

        root.setOnMousePressed((MouseEvent event) ->{
            x = event.getSceneX();
            y = event.getSceneY();
        });

        LoginController.changeScene(root, stage, scene, x, y);
    }

    public static void main(String[] args) {
        launch();
    }
}