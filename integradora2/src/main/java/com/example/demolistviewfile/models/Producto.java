package com.example.demolistviewfile.models;

public class Producto {
    private String codigo;
    private String nombre;
    private double precio;
    private int stock;
    private String categoria;

    public Producto(String codigo, String nombre, double precio, int stock, String categoria) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }

    // Getters y Setters (IMPORTANTE: Sin getters el TableView no mostrará nada)
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
    public String getCategoria() { return categoria; }

    // Para escribir en el archivo
    @Override
    public String toString() {
        return codigo + "," + nombre + "," + precio + "," + stock + "," + categoria;
    }
}