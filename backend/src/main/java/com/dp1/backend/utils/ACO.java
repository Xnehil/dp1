package com.dp1.backend.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.dp1.backend.models.*;

public class ACO {
    public static void run(HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos,
            HashMap<Integer, Envio> envios,
            ArrayList<Paquete> paquetes) {
        // Definir una matriz que defina Vuelo, Costo, Visibilidad() y Fermonas
        // El costo será dinámico para algunas variables: tiempo de vuelo (entre mismas
        // ciudades varia el t de vuelo), capacidades,
        // plazos de entrega,
        
        HashMap<Integer, Double[]> tabla = new HashMap<>(); // 4 columnas: Costo, Visibilidad, Feromonas
        
        for(int id: vuelos.keySet()){
            double costo = costo(vuelos.get(id));
            tabla.put(id, new Double[]{costo, 1/costo, 0.1});
        }
        generarArchivoTabla(tabla, "salida");


        // for (Paquete p : paquetes) {
        // System.out.println(envios.get(p.getIdEnvío()).getDestino() + " " +
        // p.getIdPaquete());
        // }

    }

    public static void imprimirTabla(HashMap<Integer, Double[]> tabla) {
        System.out.println("ID\tCosto\tVisibilidad\tFeromonas");

        // Iterar sobre cada vuelo en la tabla
        for (Map.Entry<Integer, Double[]> entry : tabla.entrySet()) {
            Integer id = entry.getKey();
            Double[] datos = entry.getValue();

            // Imprimir datos del vuelo con formato de 4 decimales
            System.out.print(id + "\t");
            for (Double dato : datos) {
                System.out.printf("%.4f\t\t", dato);
            }
            System.out.println();
        }
    }
    public static void generarArchivoTabla(HashMap<Integer, Double[]> tabla, String nombreArchivo) {
        try (FileWriter writer = new FileWriter(nombreArchivo)) {
            // Escribir encabezado de la tabla en el archivo
            writer.write("ID\tCosto\tVisibilidad\tFeromonas\n");

            // Iterar sobre cada vuelo en la tabla y escribir los datos en el archivo
            for (Map.Entry<Integer, Double[]> entry : tabla.entrySet()) {
                Integer id = entry.getKey();
                Double[] datos = entry.getValue();

                // Escribir datos del vuelo con formato de 4 decimales en el archivo
                writer.write(id + "\t");
                for (Double dato : datos) {
                    writer.write(String.format("%.4f\t\t", dato));
                }
                writer.write("\n");
            }

            System.out.println("Archivo generado correctamente: " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al generar el archivo: " + e.getMessage());
        }
    }
    public static double costo(Vuelo vuelo) {
        return vuelo.calcularMinutosDeVuelo();
    }
}
