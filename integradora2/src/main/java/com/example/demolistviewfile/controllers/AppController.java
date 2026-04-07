package com.example.demolistviewfile.controllers;

import com.example.demolistviewfile.models.Producto;
import com.example.demolistviewfile.repositories.ProductoRepository;
import com.example.demolistviewfile.services.ProductoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ComboBox;
import java.util.ArrayList;

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
    private ProductoService productoService = new ProductoService();


    @FXML
    public void initialize() {
        // 1. Configurar columnas
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colCat.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        // 2. Configurar ComboBox
        cmbCategoria.setItems(FXCollections.observableArrayList(
                "Lácteos", "Limpieza", "Frutas y Verduras", "Carnes"
        ));

        // 3. Cargar datos usando el SERVICIO (limpia la lista y carga)
        productoService.cargarDatos(masterData);

        // 4. Configurar Filtrado y Ordenamiento
        FilteredList<Producto> filteredData = new FilteredList<>(masterData, p -> true);

        // OPCIONAL: Esto permite que la tabla se pueda ordenar por columnas (clic en cabecera)
        SortedList<Producto> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableProductos.comparatorProperty());

        tableProductos.setItems(sortedData);

        // 5. Listener de Búsqueda (Llamando al servicio)
        txtBusqueda.textProperty().addListener((obs, oldVal, newVal) -> {
            productoService.configurarFiltro(filteredData, newVal);
        });

        // 6. Listener de Selección (Para llenar los campos al hacer clic en la fila)
        tableProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtNombre.setText(newSelection.getNombre());
                txtCodigo.setText(newSelection.getCodigo());
                txtPrecio.setText(String.valueOf(newSelection.getPrecio()));
                txtStock.setText(String.valueOf(newSelection.getStock()));
                cmbCategoria.setValue(newSelection.getCategoria());
            }
        });
    }

    @FXML
    private void onAgregar() {
        try {
            // 1. Validación básica de campos vacíos (UI)
            if (txtCodigo.getText().isEmpty() || txtNombre.getText().isEmpty() ||
                    txtPrecio.getText().isEmpty() || cmbCategoria.getValue() == null) {
                mostrarAlerta("Error", "Todos los campos son obligatorios.");
                return;
            }

            // 2. Crear el objeto temporal con los datos de la vista
            // (El manejo de errores numéricos se queda en el try-catch)
            Producto p = new Producto(
                    txtCodigo.getText(),
                    txtNombre.getText(),
                    Double.parseDouble(txtPrecio.getText()),
                    Integer.parseInt(txtStock.getText()),
                    cmbCategoria.getValue()
            );

            // 3. Delegar TODA la lógica al servicio
            productoService.registrarProducto(p, masterData);

            // 4. Feedback de éxito
            limpiarCampos();
            lblMsg.setText("Producto agregado con éxito.");

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "Precio y Stock deben ser valores numéricos válidos.");
        } catch (IllegalArgumentException e) {
            // Aquí capturamos los mensajes que enviamos desde el Service
            mostrarAlerta("Error de validación", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta("Error inesperado", "Ocurrió un error al guardar: " + e.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        // 1. Obtener selección
        Producto seleccionado = tableProductos.getSelectionModel().getSelectedItem();

        // 2. Validar selección
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un producto de la tabla.");
            return;
        }

        // 3. Confirmación (Responsabilidad de la UI)
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Borrar producto: " + seleccionado.getNombre() + "?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                // 4. Delegar la eliminación y persistencia al servicio
                productoService.eliminarProducto(seleccionado, masterData);

                lblMsg.setText("Producto eliminado con éxito.");
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo eliminar el producto: " + e.getMessage());
            }
        }
    }
    @FXML
    private void onEditar() {
        Producto seleccionado = tableProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un producto de la tabla para editar.");
            return;
        }

        try {
            // Validamos vacíos rápido en la UI
            if (txtNombre.getText().isEmpty() || txtPrecio.getText().isEmpty()) {
                mostrarAlerta("Error", "Los campos no pueden estar vacíos.");
                return;
            }

            // Llamamos al servicio para procesar el cambio
            productoService.actualizarProducto(
                    seleccionado,
                    txtNombre.getText(),
                    txtPrecio.getText(),
                    Integer.parseInt(txtStock.getText()),
                    cmbCategoria.getValue(),
                    masterData
            );

            // Actualizamos la interfaz
            tableProductos.refresh(); // Refresca visualmente la fila editada
            limpiarCampos();
            txtCodigo.setEditable(true); // Desbloqueamos el código si estaba bloqueado
            lblMsg.setText("Producto actualizado con éxito.");

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El stock debe ser un número entero.");
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
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