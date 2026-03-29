package com.example.demolistviewfile.controllers;

import com.example.demolistviewfile.models.Producto;
import com.example.demolistviewfile.repositories.ProductoRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;

public class AppController {
    @FXML private TableView<Producto> tableProductos;
    @FXML private TableColumn<Producto, String> colCodigo, colNombre, colCat;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;

    @FXML private TextField txtCodigo, txtNombre, txtPrecio, txtStock, txtCategoria, txtBusqueda;
    @FXML private Label lblMsg;

    private ObservableList<Producto> masterData = FXCollections.observableArrayList();
    private ProductoRepository repository = new ProductoRepository();

    @FXML
    public void initialize() {
        // 1. Configurar columnas (Vinculan con los atributos de la clase Producto)
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colCat.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        // 2. Cargar datos iniciales desde el archivo
        masterData.addAll(repository.loadAll());

        // 3. Búsqueda/Filtrado en tiempo real (Requisito 4.D.1)
        FilteredList<Producto> filteredData = new FilteredList<>(masterData, p -> true);
        txtBusqueda.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(p -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                return p.getNombre().toLowerCase().contains(lower) ||
                        p.getCodigo().toLowerCase().contains(lower);
            });
        });

        // 4. Ordenamiento por criterio (Requisito 4.D.2)
        SortedList<Producto> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableProductos.comparatorProperty());
        tableProductos.setItems(sortedData);
    }

    @FXML
    private void onAgregar() {
        try {
            // A) Validar campos vacíos
            if (txtCodigo.getText().isEmpty() || txtNombre.getText().isEmpty() ||
                    txtPrecio.getText().isEmpty() || txtStock.getText().isEmpty()) {
                mostrarAlerta("Error", "Todos los campos son obligatorios.");
                return;
            }

            // B) Validar nombre mínimo 3 caracteres (Requisito 4.C)
            if (txtNombre.getText().length() < 3) {
                mostrarAlerta("Error", "El nombre debe tener al menos 3 caracteres.");
                return;
            }

            // C) Validar código duplicado (Requisito 4.C)
            String nuevoCodigo = txtCodigo.getText();
            boolean existe = masterData.stream().anyMatch(p -> p.getCodigo().equalsIgnoreCase(nuevoCodigo));
            if (existe) {
                mostrarAlerta("Error", "El código del producto ya existe.");
                return;
            }

            // D) Validar números y rangos lógicos (Requisito 4.C)
            double precio = Double.parseDouble(txtPrecio.getText());
            int stock = Integer.parseInt(txtStock.getText());

            if (precio <= 0 || stock < 0) {
                mostrarAlerta("Error", "El precio debe ser > 0 y el stock >= 0.");
                return;
            }

            // E) Crear y Guardar
            Producto p = new Producto(nuevoCodigo, txtNombre.getText(), precio, stock, txtCategoria.getText());
            masterData.add(p);
            repository.saveAll(new ArrayList<>(masterData)); // Persistencia obligatoria

            limpiarCampos();
            lblMsg.setText("Producto agregado con éxito.");

        } catch (NumberFormatException e) {
            // Manejo de excepciones (Requisito 6)
            mostrarAlerta("Error de formato", "Precio y Stock deben ser valores numéricos.");
        }
    }

    @FXML
    private void onEliminar() {
        Producto seleccionado = tableProductos.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {
            // Confirmación obligatoria (Requisito 4.A.4)
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("¿Borrar producto: " + seleccionado.getNombre() + "?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                masterData.remove(seleccionado);
                repository.saveAll(new ArrayList<>(masterData)); // Actualizar archivo
                lblMsg.setText("Producto eliminado.");
            }
        } else {
            mostrarAlerta("Atención", "Selecciona un producto de la tabla.");
        }
    }

    private void limpiarCampos() {
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtStock.clear();
        txtCategoria.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}