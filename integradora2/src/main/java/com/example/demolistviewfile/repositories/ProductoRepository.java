package com.example.demolistviewfile.repositories;

import com.example.demolistviewfile.models.Producto;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepository {
    private final String FOLDER_NAME = "data";
    private final String FILE_NAME = FOLDER_NAME + "/persons.csv";


    public void saveAll(List<Producto> productos) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Producto p : productos) {
                writer.println(p.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Producto> loadAll() {
        List<Producto> lista = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split(",");
                lista.add(new Producto(d[0], d[1], Double.parseDouble(d[2]), Integer.parseInt(d[3]), d[4]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}