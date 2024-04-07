package com.dp1.backend.utils;

import java.util.ArrayList;
import java.util.HashMap;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Paquete;
import com.dp1.backend.models.Vuelo;

public class MPA {
    //Marine predator algorithm
    public static int[][] run(HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos, HashMap<Integer, Envio> envios,
                                ArrayList<Paquete> paquetes, int maxIter, int popSize){
        //Generar población inicial

        for (int i = 0; i < maxIter; i++) {
            //Evaluar población
            //Seleccionar mejor individuo

            //Actualizar memoria

            if (i < maxIter/3) {
                //Exploración
            } else if (i < 2*maxIter/3) {
                //Explotación
            } else {
                //Exploración y explotación
            }

            //Actualizar elite

            //Actualizar memoria

            //Aplicar FAD 
        }

        return new int[1][1];
    }

    public static int[][] inicializar(int numAeropuertos, int paquetes){
        int[][] poblacion = new int[paquetes][numAeropuertos];
        for (int i = 0; i < paquetes; i++) {
            for (int j = 0; j < numAeropuertos; j++) {
                poblacion[i][j] = (int) (Math.random() * 2);
            }
        }

        return poblacion;
    }



}
