module com.example.newsapplicationversion1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.newsapplicationversion1 to javafx.fxml;
    exports com.example.newsapplicationversion1;
}