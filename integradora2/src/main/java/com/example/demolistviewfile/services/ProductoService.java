package com.example.demolistviewfile.services;

import com.example.demolistviewfile.models.Producto;
import com.example.demolistviewfile.repositories.ProductoRepository;
import javafx.collections.ObservableList;
import java.util.ArrayList;

public class ProductoService {
    private ProductoRepository repository = new ProductoRepository();

    // Carga los productos del archivo
    public void cargarDatos(ObservableList<Producto> lista) {
        lista.clear();
        lista.addAll(repository.loadAll());
    }

    // Guarda la lista completa en el archivo
    public void guardarDatos(ObservableList<Producto> lista) {
        repository.saveAll(new ArrayList<>(lista));
    }

    // Aquí puedes meter la validación de código duplicado
    public boolean esCodigoDuplicado(ObservableList<Producto> lista, String codigo) {
        return lista.stream().anyMatch(p -> p.getCodigo().equalsIgnoreCase(codigo));
    }
}