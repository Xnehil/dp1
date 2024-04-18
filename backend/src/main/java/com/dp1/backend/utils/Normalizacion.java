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

    // Función para calcular la distancia entre dos puntos en la superficie de la Tierra
    public static double distanciaEntreAeropuertos(double latitud1, double longitud1, double latitud2, double longitud2) {
        double radioTierra = 6371; // Radio medio de la Tierra en kilómetros
        double dLat = Math.toRadians(latitud2 - latitud1);
        double dLon = Math.toRadians(longitud2 - longitud1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(latitud1)) * Math.cos(Math.toRadians(latitud2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return radioTierra * c;
    }
    
    public static double[] obtenerDistanciaExtrema(HashMap<String, Aeropuerto> aeropuertos) {
        double minDistancia = Double.MAX_VALUE;
        double maxDistancia = Double.MIN_VALUE;
        
        for (Aeropuerto aeropuerto1 : aeropuertos.values()) {
            for (Aeropuerto aeropuerto2 : aeropuertos.values()) {
                if (!aeropuerto1.equals(aeropuerto2)) {
                    double distancia = distanciaEntreAeropuertos(aeropuerto1.getLatitud(), aeropuerto1.getLongitud(),
                    aeropuerto2.getLatitud(), aeropuerto2.getLongitud());
                    minDistancia = Math.min(minDistancia, distancia);
                    maxDistancia = Math.max(maxDistancia, distancia);
                }
            }
        }
        
        return new double[] { minDistancia, maxDistancia };
    }
    
}
