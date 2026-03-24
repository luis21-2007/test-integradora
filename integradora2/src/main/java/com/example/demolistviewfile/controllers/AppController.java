package com.example.demolistviewfile.controllers;

import com.example.demolistviewfile.models.Person; // Asegúrate de importar tu clase Person
import com.example.demolistviewfile.services.PersonService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;

public class AppController {

    @FXML
    private Label lblMsg;

    // 1. Cambiamos ListView por TableView
    @FXML
    private TableView<Person> tableView;

    // 2. Definimos las columnas (Deben estar en tu FXML)
    @FXML
    private TableColumn<Person, String> colNombre;
    @FXML
    private TableColumn<Person, String> colEmail;
    @FXML
    private TableColumn<Person, Integer> colEdad;

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtedad;

    // 3. La lista ahora guarda objetos Person
    private ObservableList<Person> data = FXCollections.observableArrayList();
    private PersonService service = new PersonService();

    @FXML
    public void initialize() {
        // 4. Configurar cómo cada columna obtiene los datos del objeto Person
        // El String dentro de PropertyValueFactory debe ser el nombre exacto del atributo en la clase Person
        colNombre.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));

        tableView.setItems(data);
        loadFromFile();
    }

    @FXML
    public void onReload() {
        loadFromFile();
    }

    @FXML
    public void onAddPerson() {
        try {
            String edadt = txtedad.getText();
            String name = txtNombre.getText();
            String email = txtEmail.getText();

            // Creamos el objeto para enviarlo al servicio (si actualizaste el servicio para recibir Person)
            // O seguimos enviando Strings si tu servicio aún los recibe así:
            service.addPerson(name, email, edadt);

            lblMsg.setText("Usuario creado correctamente");
            lblMsg.setStyle("-fx-text-fill: green");

            txtNombre.clear();
            txtEmail.clear();
            txtedad.clear();
            loadFromFile();
        } catch (IOException e) {
            lblMsg.setText("Error de archivo: " + e.getMessage());
            lblMsg.setStyle("-fx-text-fill: red");
        } catch (IllegalArgumentException e) {
            lblMsg.setText("Error de datos: " + e.getMessage());
            lblMsg.setStyle("-fx-text-fill: red");
        }
    }

    private void loadFromFile() {
        try {
            // 5. El servicio ahora nos da una lista de objetos Person
            List<Person> items = service.loadForTableView();
            data.setAll(items);

            lblMsg.setText("Datos cargados correctamente");
            lblMsg.setStyle("-fx-text-fill: green");
        } catch (IOException e) {
            lblMsg.setText("Error: " + e.getMessage());
            lblMsg.setStyle("-fx-text-fill: red");
        }
    }
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}