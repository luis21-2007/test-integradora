module com.example.demolistviewfile {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.demolistviewfile to javafx.fxml;
    opens com.example.demolistviewfile.services to javafx.fxml;
    opens com.example.demolistviewfile.repositories to javafx.fxml;
    opens com.example.demolistviewfile.controllers to javafx.fxml;
    exports com.example.demolistviewfile;
    exports com.example.demolistviewfile.services;
    exports com.example.demolistviewfile.controllers;
    exports com.example.demolistviewfile.repositories;

}