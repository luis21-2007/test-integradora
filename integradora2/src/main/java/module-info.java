module com.example.demolistviewfile {
    requires javafx.controls;
    requires javafx.fxml;

    // Permite que FXML cargue tus vistas y controladores
    opens com.example.demolistviewfile to javafx.fxml;
    opens com.example.demolistviewfile.controllers to javafx.fxml;

    // ESTA LÍNEA ES LA QUE SOLUCIONA EL ERROR DE LA TABLA:
    // Permite que el motor de TableView acceda a los Getters de Producto
    opens com.example.demolistviewfile.models to javafx.base;

    exports com.example.demolistviewfile;
    exports com.example.demolistviewfile.services;
    exports com.example.demolistviewfile.controllers;
    exports com.example.demolistviewfile.models;
    exports com.example.demolistviewfile.repositories;
}