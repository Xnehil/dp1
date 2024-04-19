package com.dp1.backend.utils;

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


}
