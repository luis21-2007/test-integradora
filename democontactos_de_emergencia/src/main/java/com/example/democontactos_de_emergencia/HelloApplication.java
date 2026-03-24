package com.example.democontactos_de_emergencia;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("vista_contactos.fxml"));
        primaryStage.setTitle("Contactos de Emergencia");
        primaryStage.setScene(new Scene(root, 620, 440));
        primaryStage.show();
    }

}

