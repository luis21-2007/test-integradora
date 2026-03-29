module com.example.demoobjetos_perdidios {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.demoobjetos_perdidios to javafx.fxml;
    exports com.example.demoobjetos_perdidios;
}