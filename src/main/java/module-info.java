module com.example.newsapplicationversion1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;
    requires java.desktop;
    requires java.dotenv;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires json.simple;
    requires com.fasterxml.jackson.annotation;
    requires stanford.corenlp;
    requires annotations;
    requires edu.stanford.nlp.corenlp;

    opens com.example.newsapplicationversion1 to javafx.fxml;
    exports com.example.newsapplicationversion1;
    exports com.example.newsapplicationversion1.controllers;
    opens com.example.newsapplicationversion1.controllers to javafx.fxml;
    opens com.example.newsapplicationversion1.models to com.fasterxml.jackson.databind;
}