package com.dp1.backend.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Paquete;
import com.dp1.backend.models.Vuelo;

public class MPA {
    //Marine predator algorithm
    public static int[][] run(HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos, HashMap<Integer, Envio> envios,
                                ArrayList<Paquete> paquetes, int maxIter, int popSize){
        //La dimensión de la solución
        int dim = paquetes.size()*aeropuertos.size();
        //Generar población inicial
        int [][] Presa = new int[popSize][dim];
        int [][] Elite = new int[popSize][dim];

        for (int i = 0; i < popSize; i++) {
            Presa[i] = inicializar(aeropuertos.size(), paquetes.size(), vuelos.size());
        }

        int masApta=solucionMasApta(Presa, aeropuertos, vuelos, envios, paquetes);

        for (int i = 0; i < popSize; i++) {
            Elite[i]=Presa[masApta];
        }

        double FADs =0.2;


        for (int i = 0; i < maxIter; i++) {
            //Evaluar población
            //Seleccionar mejor individuo

            //Actualizar memoria

            //Vector Levy
            double[] levy= Auxiliares.levy(dim, 1.5); 
            //Vector brown (normal distribution)
            double[] brown = Auxiliares.brown(dim);
            Random rand = new Random();
            double P = 0.5;
            double CF = Math.pow((1 - (double)i/maxIter), (2 * (double)i/maxIter));

            double[][] stepsize = new double[popSize][dim];
            for (int j = 0; j < popSize; j++) {
                for (int k = 0; k < Presa[j].length; k++) {
                    double R = rand.nextDouble();
                    // Phase 1 (Eq.12)
                    if (i < maxIter / 3) {
                        stepsize[j][k] = brown[k] * (Elite[j][k] - Presa[j][k]*brown[k]);
                        Presa[j][k] = (int)Math.round(Presa[j][k] + P*R*stepsize[j][k]);
                    }
                    // Phase 2 (Eqs. 13 & 14)
                    else if (i > maxIter / 3 && i < 2 * maxIter / 3) {
                        if (j < Presa.length / 2) {
                            stepsize[j][k] = levy[k] * (Elite[j][k] - levy[k] * Presa[j][k]);
                            Presa[j][k] = (int)Math.round(Presa[j][k] + P * R * stepsize[j][k]);
                            
                        } else {
                            stepsize[j][k] = brown[k] * (brown[k] * Elite[j][k] - Presa[j][k]);
                            Presa[j][k] = (int)Math.round(Elite[j][k] + P * CF * stepsize[j][k]);
                        }
                    }
                    // Phase 3 (Eq. 15)
                    else {
                        stepsize[j][k] = levy[k] * (levy[k] * Elite[j][k] - Presa[j][k]);
                        Presa[j][k] = (int)Math.round(Elite[j][k] + P * CF * stepsize[j][k]);
                    }
                }

            }
            //Actualizar elite y chequear bounds de la solución



            //Actualizar memoria

            //Aplicar FAD 
        }

        return new int[1][1];
    }

    public static int[] inicializar(int numAeropuertos, int paquetes, int numVuelos){
        //Una solución inicial es un arreglo de n*numPaquetes elementos, donde n es el número de aeropuertos
        int max=numVuelos;
        int min=1;
        int[] inicial= new int[paquetes*numAeropuertos];
        for (int i = 0; i < inicial.length; i++) {
            inicial[i]=(int)(Math.random()*(max-min+1)+min);
        }
        return inicial;
    }
    /*
      1 2 4      2 5 1      3 2 1
     */

    public static int solucionMasApta(int[][] poblacion, HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos, HashMap<Integer, Envio> envios,
                                        ArrayList<Paquete> paquetes){
        //Cada fila de población es una solución
        //Cada solución tiene las rutas de todos los paquetes
        //Cada n elementos de la solución corresponden a la ruta de un paquete
        //Dicho n es igual al número de aeropuertos
        int masApta=0;
        double aptitudMax=0;
        double aptitud;
        for (int i = 0; i < poblacion.length; i++) {
            aptitud=Auxiliares.fitnessTotal(poblacion[i], aeropuertos, vuelos, envios, paquetes);
            if (aptitud>aptitudMax) {
                aptitudMax=aptitud;
                masApta=i;
            }
        }
        return masApta;
    }



}
