package com.dp1.backend.utils;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;

public class aco_auxiliares {

    public static int determinarVueloEscogido(double[] probabilidades) {
        Random random = new Random();
        double numeroAleatorio = random.nextDouble();

        // Escoger un camino basado en el número aleatorio generado
        double acumulador = 0.0;
        int caminoSeleccionado = 0;
        for (int i = 0; i < probabilidades.length; i++) {
            acumulador += probabilidades[i];
            if (numeroAleatorio < acumulador) {
                caminoSeleccionado = i;
                break;
            }
        }
        return caminoSeleccionado;
    }
    
    public static long calcularDiferenciaEnMinutos(ZonedDateTime zonedDateTime1, ZonedDateTime zonedDateTime2) {
        // Convertir ambos ZonedDateTime a UTC
        ZonedDateTime utcDateTime1 = zonedDateTime1.withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime utcDateTime2 = zonedDateTime2.withZoneSameInstant(ZoneId.of("UTC"));

        // Calcular la diferencia de tiempo en minutos
        Duration duration = Duration.between(utcDateTime1, utcDateTime2);

        // Obtener la diferencia en minutos
        long minutos = duration.toMinutes();

        return minutos;
    }

}
