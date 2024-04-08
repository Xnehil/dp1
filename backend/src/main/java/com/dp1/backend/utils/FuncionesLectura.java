package com.dp1.backend.utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.TimeZone;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Paquete;
import com.dp1.backend.models.Vuelo;

import java.time.format.DateTimeFormatter;
import java.time.Duration;


public class FuncionesLectura {

    public static HashMap<String, Aeropuerto> leerAeropuertos(String archivo) {
        System.out.println("Leyendo aeropuertos desde " + archivo);
        HashMap<String, Aeropuerto> aeropuertos = new HashMap<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(archivo), Charset.forName("UTF-8"))) {
            String line;
            int lineCount = 0;
            while ((line = br.readLine()) != null) {
                lineCount++;
                if (line.trim().isEmpty() || line.contains("GMT") || lineCount <= 4 || line.contains("Europa")) {
                    continue; // Skip empty lines and headers
                }
                String[] parts = line.split("\\s{2,}"); // Split on two or more spaces
                //Elminar espacios en blanco
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim();
                }
                int number = Integer.parseInt(parts[0]);
                String oaciCode = parts[1];
                String city = parts[2];
                String country = parts[3];
                String shortName = parts[4];
                int gmt = Integer.parseInt(parts[5]);
                int capacity = Integer.parseInt(parts[6]);

                // System.out.println("Aeropuerto: " + number + " " + oaciCode + " " + city + " " + country + " " + shortName + " " + gmt + " " + capacity);
                aeropuertos.put(oaciCode, new Aeropuerto(number, oaciCode, city, country, shortName, gmt, capacity));
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e);
        }
        return aeropuertos;
    }

    public static HashMap<Integer, Vuelo> leerVuelos(String archivo, HashMap<String, Aeropuerto> aeropuertos) {
        System.out.println("Leyendo vuelos desde " + archivo);
        HashMap<Integer, Vuelo> vuelos = new HashMap<>();
        int id = 1;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(archivo), Charset.forName("UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                String[] parts = line.split("-");
                String ciudadOrigen = parts[0];
                String ciudadDestino = parts[1];
                String horaOrigen = parts[2];
                String horaDestino = parts[3];

                TimeZone zonaOrigen = aeropuertos.get(ciudadOrigen).getZonaHoraria();
                TimeZone zonaDestino = aeropuertos.get(ciudadDestino).getZonaHoraria();

                LocalDate localDate= LocalDate.now();
                LocalTime origenLocalTime = LocalTime.parse(horaOrigen);
                LocalTime destinoLocalTime = LocalTime.parse(horaDestino);

                ZonedDateTime horaOrigenZoned = ZonedDateTime.of(localDate, origenLocalTime, zonaOrigen.toZoneId());
                ZonedDateTime horaDestinoZoned = ZonedDateTime.of(localDate, destinoLocalTime, zonaDestino.toZoneId());


                int capacidadCarga = Integer.parseInt(parts[4]);

                Vuelo vuelo= new Vuelo(ciudadOrigen, ciudadDestino, horaOrigenZoned, horaDestinoZoned, capacidadCarga);
                vuelo.setIdVuelo(id);
                vuelos.put(id, vuelo);
                id++;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e);
        }
        return vuelos;
    }

    public static HashMap<Integer, Envio> leerEnvios(String archivo, HashMap<String, Aeropuerto> aeropuertos) {
        System.out.println("Leyendo envios desde " + archivo);
        HashMap<Integer, Envio> envios = new HashMap<>();
        int id = 1;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(archivo), Charset.forName("UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                String[] parts = line.split("-");
                String ciudadOrigenEnvio = parts[0].substring(0, 4);
                int envioId = Integer.parseInt(parts[0].substring(4));
                LocalDate fechaOrigen = LocalDate.parse(parts[1], DateTimeFormatter.ofPattern("yyyyMMdd"));
                String horaOrigen = parts[2];
                String[] destinoParts = parts[3].split(":");
                String ciudadDestino = destinoParts[0];
                int cantidadPaquetes = Integer.parseInt(destinoParts[1]);

                Aeropuerto origen = aeropuertos.getOrDefault(ciudadOrigenEnvio, aeropuertos.get("EKCH"));
                Aeropuerto destino = aeropuertos.getOrDefault(ciudadDestino, aeropuertos.get("EKCH"));

                TimeZone zonaOrigen = origen.getZonaHoraria();
                TimeZone zonaDestino = destino.getZonaHoraria();
                LocalTime origenLocalTime = LocalTime.parse(horaOrigen);

                ZonedDateTime horaOrigenZoned = ZonedDateTime.of(fechaOrigen, origenLocalTime, zonaOrigen.toZoneId());
                LocalDate fechaDestino;
                ZonedDateTime horaDestinoZoned;

                Duration tiempoEnvio = Duration.ofHours(0);
                //El tiempo para enviar será de dos días si es continente distsinto y de un día si es el mismo continente
                if (origen.getContinente() != destino.getContinente()){
                    fechaDestino = fechaOrigen.plusDays(2);   
                } else {
                    fechaDestino = fechaOrigen.plusDays(1); 
                }
                horaDestinoZoned = ZonedDateTime.of(fechaDestino, origenLocalTime, zonaDestino.toZoneId());
                tiempoEnvio = Duration.between(horaOrigenZoned, horaDestinoZoned);

                ArrayList<Paquete> paquetes= new ArrayList<>();
                for (int i = 0; i < cantidadPaquetes; i++) {
                    Paquete paquete = new Paquete();
                    paquete.setIdEnvío(envioId);
                    paquete.setIdPaquete(id);
                    paquete.setTiempoRestante(tiempoEnvio);
                    paquetes.add(paquete);
                    id++;
                }
                envios.put(envioId, new Envio(ciudadOrigenEnvio, ciudadDestino, horaOrigenZoned, cantidadPaquetes, paquetes));
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e);
        }
        return envios;
    }
    
}
