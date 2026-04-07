package com.example.demolistviewfile.services;

import com.example.demolistviewfile.models.Producto;
import com.example.demolistviewfile.repositories.ProductoRepository;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.ArrayList;
import java.util.List;
public class ProductoService {
    private final ProductoRepository repository = new ProductoRepository();


    public void cargarDatos(ObservableList<Producto> lista) {
        lista.setAll(repository.loadAll());
    }
    /**
     * Valida y procesa la lógica de guardado.
     * Lanza IllegalArgumentException con un mensaje claro si falla la validación.
     */
    public void registrarProducto(Producto nuevo, ObservableList<Producto> listaActual) throws IllegalArgumentException {
        // 1. Validar nombre
        if (nuevo.getNombre().length() < 3) {
            throw new IllegalArgumentException("El nombre debe tener al menos 3 caracteres.");
        }

        // 2. Validar duplicado
        if (listaActual.stream().anyMatch(p -> p.getCodigo().equalsIgnoreCase(nuevo.getCodigo()))) {
            throw new IllegalArgumentException("El código '" + nuevo.getCodigo() + "' ya existe.");
        }

        // 3. Validar valores numéricos
        if (nuevo.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
        if (nuevo.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }

        // 4. Si todo está bien, agregar a la lista y persistir
        listaActual.add(nuevo);
        repository.saveAll(new ArrayList<>(listaActual));


    }

    public void eliminarProducto(Producto producto, ObservableList<Producto> listaActual) {
        if (producto != null) {
            listaActual.remove(producto);
            // Persistimos el cambio inmediatamente
            repository.saveAll(new ArrayList<>(listaActual));
        }
    }
    public void configurarFiltro(FilteredList<Producto> filteredData, String textoBusqueda) {

        filteredData.setPredicate(producto -> {
            // Si el buscador está vacío, mostramos todo
            if (textoBusqueda == null || textoBusqueda.isEmpty()) {
                return true;
            }

            String term = textoBusqueda.toLowerCase();

            // Lógica de coincidencia: Nombre o Código
            return producto.getNombre().toLowerCase().contains(term) ||
                    producto.getCodigo().toLowerCase().contains(term);
        });
    }
    public void actualizarProducto(Producto existente, String nombre, String precioStr, int stock, String categoria, ObservableList<Producto> listaActual) throws Exception {
        // 1. Validaciones básicas
        if (nombre.length() < 3) {
            throw new Exception("El nombre debe tener al menos 3 caracteres.");
        }

        double precio;
        try {
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            throw new Exception("El precio debe ser un número válido.");
        }

        if (precio <= 0 || stock < 0) {
            throw new Exception("El precio debe ser > 0 y el stock >= 0.");
        }

        // 2. Si las validaciones pasan, actualizamos el objeto
        existente.setNombre(nombre);
        existente.setPrecio(precio);
        existente.setStock(stock);
        existente.setCategoria(categoria);

        // 3. Persistimos el cambio
        repository.saveAll(new ArrayList<>(listaActual));
    }
}
