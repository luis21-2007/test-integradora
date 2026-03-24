package com.example.demolistviewfile.services;

import com.example.demolistviewfile.models.Person; // Asegúrate de tener este import
import com.example.demolistviewfile.repositories.PersonFileRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PersonService {

    private final PersonFileRepository repo = new PersonFileRepository();

    // CAMBIO: Ahora devuelve List<Person> en lugar de List<String>
    public List<Person> loadForTableView() throws IOException {
        List<String> lines = repo.readAllLines();
        List<Person> result = new ArrayList<>(); // Lista de objetos Person

        for (String line : lines) {
            if (line == null || line.isBlank()) continue;

            String[] parts = line.split(",");
            if (parts.length >= 3) {
                try {
                    String name = parts[0].trim();
                    String email = parts[1].trim();
                    int edad = Integer.parseInt(parts[2].trim());

                    // CREAMOS EL OBJETOS: Aquí es donde "rompemos" el String
                    // para que el TableView pueda usar cada parte por separado
                    result.add(new Person(name, email, edad));

                } catch (NumberFormatException e) {
                    // Ignorar líneas con datos de edad corruptos
                }
            }
        }
        return result;
    }

    public void addPerson(String name, String email, String edadt) throws IOException {
        validarPerson(name, email, edadt);
        // Guardamos en el archivo como CSV (formato plano)
        repo.addNewLine(name + "," + email + "," + edadt);
    }

    private void validarPerson(String name, String email, String edad) {
        if (name == null || name.isBlank() || name.length() < 3) {
            throw new IllegalArgumentException("El nombre es demasiado corto");
        }

        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("El correo es incorrecto");
        }

        try {
            int edadnum = Integer.parseInt(edad);
            if (edadnum < 18) throw new IllegalArgumentException("Solo mayores de edad");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La edad debe ser numérica");
        }
    }
}