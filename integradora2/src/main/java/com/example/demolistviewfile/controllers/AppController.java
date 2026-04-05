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
import javafx.scene.control.ComboBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppController {
    @FXML private TableView<Producto> tableProductos;
    @FXML private TableColumn<Producto, String> colCodigo, colNombre, colCat;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;

    @FXML private TextField txtCodigo, txtNombre, txtPrecio, txtStock, txtCategoria, txtBusqueda;
    @FXML private Label lblMsg;
    @FXML
    private ComboBox<String> cmbCategoria;

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

        // Definimos las opciones del ComboBox
        ObservableList<String> opciones = FXCollections.observableArrayList(
                "Lácteos",
                "Limpieza",
                "Frutas y Verduras",
                "Carnes"
        );
        // Asignamos las opciones al ComboBox
        cmbCategoria.setItems(opciones);

        // 2. Cargar datos iniciales desde el archivo
        masterData.addAll(repository.loadAll());

        // 3. Búsqueda/Filtrado en tiempo real
        FilteredList<Producto> filteredData = new FilteredList<>(masterData, p -> true);
        txtBusqueda.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(p -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                return p.getNombre().toLowerCase().contains(lower) ||
                        p.getCodigo().toLowerCase().contains(lower);
            });
        });

        // 4. Ordenamiento y vinculación con la tabla
        SortedList<Producto> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableProductos.comparatorProperty());

        // IMPORTANTE: Aquí se separan las dos acciones
        tableProductos.setItems(sortedData);
        tableProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // 'newSelection' es el objeto
                txtNombre.setText(newSelection.getNombre());
                txtCodigo.setText(newSelection.getCodigo());
                txtPrecio.setText(String.valueOf(newSelection.getPrecio()));
                txtStock.setText(String.valueOf(newSelection.getStock()));

                // Para el ComboBox que acabas de crear:
                cmbCategoria.setValue(newSelection.getCategoria());
            }
        });
    }

    @FXML
    private void onAgregar() {
        try {
            // Validar campos vacíos
            if (txtCodigo.getText().isEmpty() || txtNombre.getText().isEmpty() ||
                    txtPrecio.getText().isEmpty() || txtStock.getText().isEmpty()) {
                mostrarAlerta("Error", "Todos los campos son obligatorios.");
                return;
            }

            // Validar nombre
            if (txtNombre.getText().length() < 3) {
                mostrarAlerta("Error", "El nombre debe tener al menos 3 caracteres.");
                return;
            }

            //  Validar duplicado
             String nuevoCodigo = txtCodigo.getText();
            boolean existe = masterData.stream().anyMatch(p -> p.getCodigo().equalsIgnoreCase(nuevoCodigo));
            if (existe) {
                mostrarAlerta("Error", "El código del producto ya existe.");
                return;
            }

            // Validar números
            double precio = Double.parseDouble(txtPrecio.getText());
            int stock = Integer.parseInt(txtStock.getText());

            if (precio <= 0 || stock < 0) {
                mostrarAlerta("Error", "El precio debe ser > 0 y el stock >= 0.");
                return;
            }

            //Guardar
            Producto p = new Producto(nuevoCodigo, txtNombre.getText(), precio, stock, cmbCategoria.getValue());
            masterData.add(p);
            repository.saveAll(new ArrayList<>(masterData)); // Persistencia obligatoria = tiene que estar para guardarlo todo

            limpiarCampos();
            lblMsg.setText("Producto agregado con éxito.");

        } catch (NumberFormatException e) {
            //excepciones
            mostrarAlerta("Error de formato", "Precio y Stock deben ser valores numéricos.");
        }
    }

    @FXML
    private void onEliminar() {
        Producto seleccionado = tableProductos.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {
            // Confirmación obligatoria = PREGUNTA SOLICITADA POR EL PROGRAMA
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
    @FXML
    private void onEditar() {
            Producto newproducto = tableProductos.getSelectionModel().getSelectedItem();

            if (newproducto == null) {
                mostrarAlerta("Atención", "Selecciona un producto de la tabla para editar.");
                return;
            }

            try {
                if (txtNombre.getText().isEmpty() || txtPrecio.getText().isEmpty() || txtStock.getText().isEmpty()) {
                    mostrarAlerta("Error", "Los campos no pueden estar vacíos.");
                    return;
                }

                double nuevoPrecio = Double.parseDouble(txtPrecio.getText());
                int nuevoStock = Integer.parseInt(txtStock.getText());
                // Modificamos los atributos del objeto que ya está en la lista masterData
                newproducto.setNombre(txtNombre.getText());
                newproducto.setPrecio(nuevoPrecio);
                newproducto.setStock(nuevoStock);
                newproducto.setCategoria(cmbCategoria.getValue());

                // 4. Refrescar y Persistir
                tableProductos.refresh();
                repository.saveAll(new ArrayList<>(masterData));

                limpiarCampos();
                txtCodigo.setEditable(true);
                lblMsg.setText("Producto actualizado con éxito.");

            } catch (NumberFormatException e) {
                mostrarAlerta("Error de formato", "Precio y Stock deben ser numéricos.");
            }
        }

    private void limpiarCampos() {
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtStock.clear();
        cmbCategoria.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}