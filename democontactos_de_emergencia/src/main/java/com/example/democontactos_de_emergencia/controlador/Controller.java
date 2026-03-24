package com.example.democontactos_de_emergencia.controlador;

import com.example.democontactos_de_emergencia.contacto.Contacto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;



public class Controller {
    @FXML
    private Label lblMsg;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtTelefono;
    @FXML
    private ComboBox<String> cbParentesco;
    @FXML
    private ListView<Contacto> lvContactos;


    private ObservableList<Contacto> listaContactos = FXCollections.observableArrayList();


    private String[] opcionesParentesco = {"Padre", "Madre", "Hermano", "Hermana", "Abuelo", "Abuela", "Tío", "Tía"};

    @FXML
    public void initialize() {
        cbParentesco.getItems().addAll(opcionesParentesco);
        lvContactos.setItems(listaContactos);
    }

    @FXML
    private void agregarContacto() {
        String nombre = txtNombre.getText();
        String telefono = txtTelefono.getText();
        String parentesco = cbParentesco.getValue();


        if (nombre.isEmpty() || telefono.isEmpty() || parentesco == null) {
            lblMsg.setText("Error :Todos los campos son obligatorios.");
            return;
        }
        if (telefono.length() != 10) {
            lblMsg.setText("Error : El teléfono debe tener 10 dígitos.");
            return;
        }
        for (Contacto c : listaContactos) {
            if (c.getNombre().equalsIgnoreCase(nombre)) {
                lblMsg.setText("Error : Ya existe un contacto con ese nombre.");
                return;
            }
        }

        listaContactos.add(new Contacto(nombre, telefono, parentesco));
        limpiarCampos();
    }

    @FXML
    private void buscarContacto() {
        String busqueda = txtNombre.getText();
        for (Contacto c : listaContactos) {
            if (c.getNombre().equalsIgnoreCase(busqueda)) {
                txtTelefono.setText(c.getTelefono());
                cbParentesco.setValue(c.getParentesco());
                return;
            }
        }
        lblMsg.setText("No encontrado : El contacto no existe.");
    }

    @FXML
    private void eliminarContacto() {
        String busqueda = txtNombre.getText();
        Contacto aEliminar = null;
        for (Contacto c : listaContactos) {
            if (c.getNombre().equalsIgnoreCase(busqueda)) {
                aEliminar = c;
                break;
            }
        }
        if (aEliminar != null) {
            listaContactos.remove(aEliminar);
            limpiarCampos();
        } else {
            lblMsg.setText("Error : No se encontró el contacto a eliminar.");
        }
    }

    @FXML
    private void actualizarContacto() {
        String nom = txtNombre.getText();
        for (Contacto c : listaContactos) {
            if (c.getNombre().equalsIgnoreCase(nom)) {
                c.setTelefono(txtTelefono.getText());
                c.setParentesco(cbParentesco.getValue());
                return;
            }
        }
    }

    @FXML
    private void limpiarCampos() {
        txtNombre.clear();
        txtTelefono.clear();
        cbParentesco.setValue(null);
    }


}
