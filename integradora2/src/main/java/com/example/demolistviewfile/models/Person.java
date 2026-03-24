package com.example.demolistviewfile.models;

public class Person {
    private String name;
    private String email;
    private int edad;

    // Constructor: Para crear la persona desde el Service
    public Person(String name, String email, int edad) {
        this.name = name;
        this.email = email;
        this.edad = edad;
    }

    // GETTERS: El TableView los usa para saber qué escribir en cada columna
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getEdad() {
        return edad;
    }

    // Setters (opcionales por ahora, pero buenos tenerlos)
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setEdad(int edad) { this.edad = edad; }
}