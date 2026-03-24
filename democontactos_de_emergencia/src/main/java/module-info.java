module com.example.democontactos_de_emergencia {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.example.democontactos_de_emergencia;
    opens com.example.democontactos_de_emergencia.controlador to javafx.fxml;
}