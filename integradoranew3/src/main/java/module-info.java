module com.example.integradoranew3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.integradoranew3 to javafx.fxml;
    exports com.example.integradoranew3;
}