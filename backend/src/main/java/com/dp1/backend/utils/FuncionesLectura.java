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
        String currentContinent = "";
        try (BufferedReader br = Files.newBufferedReader(Paths.get(archivo), Charset.forName("UTF-16"))) {
            String line;
            int lineCount = 0;
            while ((line = br.readLine()) != null) {
                lineCount++;
                if (line.trim().isEmpty() || lineCount <= 7) {
                    continue; // Skip empty lines and headers
                }
                if (line.contains("America del Sur") || line.contains("Europa") || line.contains("ASIA")) {
                    currentContinent = line.trim();
                    if (currentContinent.contains("America")) {
                        currentContinent = "America del Sur";
                    }
                    continue; // Skip continent lines
                }
                String[] parts = line.split("\\s{3,}"); // Split on three or more spaces
                // Eliminar espacios en blanco
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim();
                }
                int number = Integer.parseInt(parts[0]);
                String oaciCode = parts[1];
                String city = parts[2];
                String country = parts[3];
                String shortName = parts[4];
                int gmt = Integer.parseInt(parts[5].replace("+", "").replace("-", ""));
                int capacity = Integer.parseInt(parts[6]);
                double longitud = convertToDecimalDegrees(parts[8]);
                double latitud = convertToDecimalDegrees(parts[7]);

                Aeropuerto aeropuerto = new Aeropuerto(number, oaciCode, city, country, shortName, gmt, capacity);
                aeropuerto.setLongitud(longitud);
                aeropuerto.setLatitud(latitud);
                aeropuerto.setContinente(currentContinent);
                aeropuertos.put(oaciCode, aeropuerto);
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

                LocalDate localDate = LocalDate.now();
                LocalTime origenLocalTime = LocalTime.parse(horaOrigen);
                LocalTime destinoLocalTime = LocalTime.parse(horaDestino);

                ZonedDateTime horaOrigenZoned = ZonedDateTime.of(localDate, origenLocalTime, zonaOrigen.toZoneId());
                ZonedDateTime horaDestinoZoned = ZonedDateTime.of(localDate, destinoLocalTime, zonaDestino.toZoneId());

                int capacidadCarga = Integer.parseInt(parts[4]);

                Vuelo vuelo = new Vuelo(ciudadOrigen, ciudadDestino, horaOrigenZoned, horaDestinoZoned, capacidadCarga);
                vuelo.setIdVuelo(id);
                vuelos.put(id, vuelo);
                id++;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e);
        }
        return vuelos;
    }

    private static double convertToDecimalDegrees(String dms) {
        // Remove more than one space
        dms = dms.replaceAll("\\s+", " ");
        String[] parts = dms.split("\\s");
        // Remove °, ', "
        parts[1] = parts[1].replace("°", "");
        parts[2] = parts[2].replace("'", "");
        parts[3] = parts[3].replace("\"", "");

        double degrees = Double.parseDouble(parts[1]);
        double minutes = Double.parseDouble(parts[2]) / 60;
        double seconds = Double.parseDouble(parts[3]) / 3600;
        double decimalDegrees = degrees + minutes + seconds;
        if (parts[4].equals("S") || parts[4].equals("W")) {
            decimalDegrees = -decimalDegrees; // Invert the sign for south and west coordinates
        }
        return decimalDegrees;
    }

    public static HashMap<String, Envio> leerEnvios(String archivo, HashMap<String, Aeropuerto> aeropuertos, int maxEnvios){
        System.out.println("Leyendo envios desde " + archivo);
        HashMap<String, Envio> envios = new HashMap<>();
        int counter=0;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(archivo), Charset.forName("UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null && counter < maxEnvios) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                String[] parts = line.split("-");
                String ciudadOrigenEnvio = parts[0];
                int envioId = Integer.parseInt(parts[1]);
                LocalDate fechaOrigen = LocalDate.parse(parts[2], DateTimeFormatter.ofPattern("yyyyMMdd"));
                LocalTime horaOrigen = LocalTime.parse(parts[3]);
                String[] destinoParts = parts[4].split(":");
                String ciudadDestino = destinoParts[0];
                int cantidadPaquetes = Integer.parseInt(destinoParts[1]);

                Aeropuerto origen = aeropuertos.getOrDefault(ciudadOrigenEnvio, aeropuertos.get("EKCH"));
                Aeropuerto destino = aeropuertos.getOrDefault(ciudadDestino, aeropuertos.get("EKCH"));

                TimeZone zonaOrigen = origen.getZonaHoraria();
                TimeZone zonaDestino = destino.getZonaHoraria();

                ZonedDateTime horaOrigenZoned = ZonedDateTime.of(fechaOrigen, horaOrigen, zonaOrigen.toZoneId());
                LocalDate fechaDestino;
                ZonedDateTime horaDestinoZoned;

                // El tiempo para enviar será de dos días si es continente distsinto y de un día
                // si es el mismo continente
                if (!origen.getContinente().equals(destino.getContinente())) {
                    fechaDestino = fechaOrigen.plusDays(2);
                } else {
                    fechaDestino = fechaOrigen.plusDays(1);
                }
                horaDestinoZoned = ZonedDateTime.of(fechaDestino, horaOrigen, zonaDestino.toZoneId());
                ArrayList<Paquete> paquetes = new ArrayList<>();
                for (int i = 0; i < cantidadPaquetes; i++) {
                    Paquete paquete = new Paquete();
<<<<<<< HEAD
                    paquete.setIdEnvío(envioId);
                    //Add more properties to the package if needed
                    //paquete.setIdPaquete(100*envioId + (cantidadPaquetes+1));
=======
                    paquete.setIdEnvio(envioId);
                    paquete.setCodigoEnvio(ciudadOrigenEnvio + envioId);
                    // Add more properties to the package if needed
>>>>>>> Harvy
                    paquetes.add(paquete);
                    
                    // Meter paquetes al aeropuerto de origen
                    origen.paqueteEntraReal(horaOrigenZoned.toLocalDateTime());
                }
                Envio nuevoEnvio = new Envio(ciudadOrigenEnvio, ciudadDestino, horaOrigenZoned, cantidadPaquetes, paquetes);
                nuevoEnvio.setIdEnvio(envioId);
                nuevoEnvio.setFechaHoraLlegadaPrevista(horaDestinoZoned);

                String codigo=ciudadOrigenEnvio+envioId;
                envios.put(codigo, nuevoEnvio);
                counter++;
            }
            //System.out.println("Numero de envios: " + counter);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e);
        }
        // for(int id: envios.keySet()){
        //     System.out.println(envios.get(id).getIdEnvio());
        // }
        return envios;
    }

}
