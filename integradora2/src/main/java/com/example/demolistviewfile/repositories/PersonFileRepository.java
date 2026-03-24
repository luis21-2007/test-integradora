package com.example.demolistviewfile.repositories;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class PersonFileRepository {

    // Ruta del archivo CSV
    private final Path pathFile = Paths.get("data", "persons.csv");

    /**
     * Verifica que la carpeta 'data' y el archivo 'persons.csv' existan.
     * Si no existen, los crea automáticamente.
     */
    private void ensureFile() throws IOException {
        if (pathFile.getParent() != null && Files.notExists(pathFile.getParent())) {
            Files.createDirectories(pathFile.getParent());
        }
        if (Files.notExists(pathFile)) {
            Files.createFile(pathFile);
        }
    }

    /**
     * Lee todas las líneas del archivo.
     * El Service se encargará de convertirlas en objetos Person.
     */
    public List<String> readAllLines() throws IOException {
        ensureFile();
        return Files.readAllLines(pathFile, StandardCharsets.UTF_8);
    }

    /**
     * Agrega una nueva línea al final del archivo.
     * Recibe el formato CSV (ej: "Juan,juan@mail.com,25")
     */
    public void addNewLine(String line) throws IOException {
        ensureFile();
        Files.writeString(
                pathFile,
                line + System.lineSeparator(),
                StandardCharsets.UTF_8,
                StandardOpenOption.APPEND
        );
    }

    /**
     * Sobrescribe todo el archivo.
     * Útil si en el TableView implementas un botón de "Eliminar"
     * o "Editar" y necesitas guardar la lista completa de nuevo.
     */
    public void saveAllLines(List<String> lines) throws IOException {
        ensureFile();
        Files.write(pathFile, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }
}