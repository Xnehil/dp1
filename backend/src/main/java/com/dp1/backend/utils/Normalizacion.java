package com.dp1.backend.utils;

import java.util.HashMap;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Vuelo;

public class Normalizacion {

    // Función para normalizar tiempo de vuelo en minutos
    public static double normalizarTiempoVuelo(double tiempoVuelo, double minTiempoVuelo, double maxTiempoVuelo) {
        return (tiempoVuelo - minTiempoVuelo) / (maxTiempoVuelo - minTiempoVuelo);
    }

    // Función para normalizar distancia a la ciudad destino en kilómetros
    public static double normalizarDistancia(double distancia, double minDistancia, double maxDistancia) {
        return (distancia - minDistancia) / (maxDistancia - minDistancia);
    }

    public static double[] obtenerMinMaxTiempoVuelo(HashMap<Integer, Vuelo> vuelos) {
        double minTiempoVuelo = Double.MAX_VALUE;
        double maxTiempoVuelo = Double.MIN_VALUE;

        for (Vuelo vuelo : vuelos.values()) {
            double tiempoVuelo = vuelo.calcularMinutosDeVuelo();
            minTiempoVuelo = Math.min(minTiempoVuelo, tiempoVuelo);
            maxTiempoVuelo = Math.max(maxTiempoVuelo, tiempoVuelo);
        }

        return new double[] { minTiempoVuelo, maxTiempoVuelo };
    }

    // Función para calcular la distancia entre dos puntos en la superficie de la
    // Tierra
    public static double distanciaEntreAeropuertos(double latitud1, double longitud1, double latitud2,
            double longitud2) {
        double radioTierra = 6371; // Radio medio de la Tierra en kilómetros
        double dLat = Math.toRadians(latitud2 - latitud1);
        double dLon = Math.toRadians(longitud2 - longitud1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(latitud1)) * Math.cos(Math.toRadians(latitud2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return radioTierra * c;
    }

    // public static double[] obtenerDistanciaExtrema(HashMap<String, Aeropuerto>
    // aeropuertos) {
    // double minDistancia = Double.MAX_VALUE;
    // double maxDistancia = Double.MIN_VALUE;

    // for (Aeropuerto aeropuerto1 : aeropuertos.values()) {
    // for (Aeropuerto aeropuerto2 : aeropuertos.values()) {
    // if (!aeropuerto1.equals(aeropuerto2)) {
    // double distancia = distanciaEntreAeropuertos(aeropuerto1.getLatitud(),
    // aeropuerto1.getLongitud(),
    // aeropuerto2.getLatitud(), aeropuerto2.getLongitud());
    // minDistancia = Math.min(minDistancia, distancia);
    // maxDistancia = Math.max(maxDistancia, distancia);
    // }
    // }
    // }
    // return new double[] { minDistancia, maxDistancia };
    // }

    public static double[] obtenerDistanciaExtrema(HashMap<String, Aeropuerto> aeropuertos) {
        double minDistancia = Double.MAX_VALUE;
        double maxDistancia = Double.MIN_VALUE;
        Aeropuerto aeropuertoMin1 = null;
        Aeropuerto aeropuertoMin2 = null;
        Aeropuerto aeropuertoMax1 = null;
        Aeropuerto aeropuertoMax2 = null;

        for (Aeropuerto aeropuerto1 : aeropuertos.values()) {
            for (Aeropuerto aeropuerto2 : aeropuertos.values()) {
                if (!aeropuerto1.equals(aeropuerto2)) {
                    double distancia = distanciaEntreAeropuertos(aeropuerto1.getLatitud(), aeropuerto1.getLongitud(),
                            aeropuerto2.getLatitud(), aeropuerto2.getLongitud());
                    if (distancia < minDistancia) {
                        minDistancia = distancia;
                        aeropuertoMin1 = aeropuerto1;
                        aeropuertoMin2 = aeropuerto2;
                    }
                    if (distancia > maxDistancia) {
                        maxDistancia = distancia;
                        aeropuertoMax1 = aeropuerto1;
                        aeropuertoMax2 = aeropuerto2;
                    }
                }
            }
        }
        System.out.println("Distancia minima: " + aeropuertoMin1.getCiudad() + " - " + aeropuertoMin2.getCiudad());
        System.out.println("Distancia maxima: " + aeropuertoMax1.getCiudad() + " - " + aeropuertoMax2.getCiudad());

        return new double[] { minDistancia, maxDistancia };
    }

    public static double obtenerDistanciaEntreAeropuertos(HashMap<String, Aeropuerto> aeropuertos, String codigoOACI1,
            String codigoOACI2) {
        Aeropuerto aeropuerto1 = null;
        Aeropuerto aeropuerto2 = null;

        if(codigoOACI1.equals(codigoOACI2)) return 0.0;

        // Buscar el primer aeropuerto con el código OACI1
        for (Aeropuerto aeropuerto : aeropuertos.values()) {
            if (aeropuerto.getCodigoOACI().equals(codigoOACI1)) {
                aeropuerto1 = aeropuerto;
                break; // Salir del bucle una vez que se encuentra el aeropuerto
            }
        }

        // Buscar el segundo aeropuerto con el código OACI2
        for (Aeropuerto aeropuerto : aeropuertos.values()) {
            if (aeropuerto.getCodigoOACI().equals(codigoOACI2)) {
                aeropuerto2 = aeropuerto;
                break; // Salir del bucle una vez que se encuentra el aeropuerto
            }
        }

        // Verificar si se encontraron ambos aeropuertos
        if (aeropuerto1 != null && aeropuerto2 != null) {
            return distanciaEntreAeropuertos(aeropuerto1.getLatitud(), aeropuerto1.getLongitud(),
                    aeropuerto2.getLatitud(), aeropuerto2.getLongitud());
        } else {
            // Manejo de errores si no se encuentran los aeropuertos
            System.out.println("Error: No se encontraron los aeropuertos con los códigos OACI proporcionados." + "-"+codigoOACI1+"-"+codigoOACI2);
            return -1; // Valor de distancia inválido
        }
    }
}
