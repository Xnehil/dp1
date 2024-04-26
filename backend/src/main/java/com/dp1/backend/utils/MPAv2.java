package com.dp1.backend.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Paquete;
import com.dp1.backend.models.Vuelo;

public class MPAv2 {
    //Marine predator algorithm
    public static int[] run(HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos, HashMap<String, Envio> envios,
                                ArrayList<Paquete> paquetes, int maxIter, int popSize, int tamanioUnaSolucion){
        //La dimensión de la solución
        int dim = paquetes.size()*tamanioUnaSolucion;
        int minVuelo = vuelos.keySet().stream().min(Integer::compare).get();
        int maxVuelo = vuelos.keySet().stream().max(Integer::compare).get();
        //Generar población inicial
        int [][] Presa = new int[popSize][dim];
        int [][] Elite = new int[popSize][dim];
        double[][] fitness = new double[popSize][paquetes.size()];
        // Paq1     Paq2
        // 3 4 2    5 1 2 
        // 1 2 4    2 5 1

        //Para memoria 
        double[][] fitnessOld = new double[popSize][paquetes.size()];
        int[][] PresaOld = new int[popSize][dim];
        
        double topPredatorFitness = 0;

        for (int i = 0; i < popSize; i++) {
            Presa[i] = inicializar(tamanioUnaSolucion, paquetes.size(), vuelos.size(), paquetes, vuelos, envios, aeropuertos);        
        }


        int masApta=0;
        double FADs =0.3;

        for (int i = 0; i < maxIter; i++) {
            // System.out.println("Iteración: " + i);
            //Evaluar población
            masApta =solucionMasApta(Presa, aeropuertos, vuelos, envios, paquetes, topPredatorFitness, fitness, minVuelo, maxVuelo);

            // System.out.println("Fitness más apta previo: " + fitness[masApta]+ " iteración: " + i);

            //Actualizar memoria
            memorySaving(Presa, fitness, PresaOld, fitnessOld, i, tamanioUnaSolucion);

            /*Inicialización de parámetros */

            //Elite copia de la mejor solución
            for (int w = 0; w < popSize; w++) {
                Elite[w] = Arrays.copyOf(Presa[masApta], Presa[masApta].length);
            }
            //Vector Levy
            double[] levy= Auxiliares.levy(dim, 1.5); 
            //Vector brown (normal distribution)
            double[] brown = Auxiliares.brown(dim);
            Random rand = new Random();
            double P = 0.5;
            double CF = Math.pow((1 - (double)i/maxIter), (2 * (double)i/maxIter));
            double[][] stepsize = new double[popSize][dim];

            /*Fin de inicialización de parámetros */

            /*Las fases */

            for (int j = 0; j < popSize; j++) {
                for (int k = 0; k < Presa[j].length; k++) {
                    double R = rand.nextDouble();
                    // Phase 1 (Eq.12)
                    if (i < maxIter / 3) {
                        stepsize[j][k] = brown[k] * (Elite[j][k] - Presa[j][k]*brown[k]);
                        Presa[j][k] = (int)Math.round(Presa[j][k] + P*R*stepsize[j][k]);
                    }
                    // Phase 2 (Eqs. 13 & 14)
                    else if (i > maxIter / 3 && i < 2* maxIter / 3) {
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
            /*Fin de las fases */

            //Evaluar población
            masApta=solucionMasApta(Presa, aeropuertos, vuelos, envios, paquetes, topPredatorFitness, fitness, minVuelo, maxVuelo);

            // System.out.println("Fitness más apta luego: " + fitness[masApta]+ " iteración: " + i);

            //Actualizar memoria
            memorySaving(Presa, fitness, PresaOld, fitnessOld, i+1, tamanioUnaSolucion);

            //Aplicar FAD 
            fadEffect(PresaOld, FADs, popSize, minVuelo, maxVuelo, CF, rand);
        }
        masApta=solucionMasApta(Presa, aeropuertos, vuelos, envios, paquetes, topPredatorFitness, fitness, minVuelo, maxVuelo);
        return Presa[masApta];
    }

    public static int[] inicializar(int tamanioUnaSolucion, int paquetes, int numVuelos, ArrayList<Paquete> paquetesList, HashMap<Integer, Vuelo> vuelos, HashMap<String, Envio> envios, HashMap<String, Aeropuerto> aeropuertos){
        //Una solución inicial es un arreglo de n*numPaquetes elementos, donde n es el número de aeropuertos
        int max=numVuelos;
        int min=1;
        int[] inicial= new int[paquetes*tamanioUnaSolucion];
        for (int i = 0; i < inicial.length; i++) {
            //Cada numAeropuertos elementos corresponden a la ruta de un paquete. El primer elemento de dicha ruta será un vuelo que salga de su ciudad
            //WIP
            // if(i%tamanioUnaSolucion==0){
            //     inicial[i]=Vuelo.getVueloRandomDesde(vuelos, envios.get(paquetesList.get(i/tamanioUnaSolucion).getCodigoEnvio()));
            // } else {
            //     inicial[i]=(int)(Math.random()*(max-min+1)+min);
            // }
            inicial[i]=(int)(Math.random()*(max-min+1)+min);
        }
        return inicial;
    }
    /*
      1 2 4      2 5 1      3 2 1
     */

    public static int solucionMasApta(int[][] poblacion, HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos, HashMap<String, Envio> envios,
                                        ArrayList<Paquete> paquetes, double topPredatorFitness, double[][] fitness,int minVuelo, int maxVuelo){
        //Cada fila de población es una solución
        //Cada solución tiene las rutas de todos los paquetes
        //Cada n elementos de la solución corresponden a la ruta de un paquete
        //Dicho n es igual al número de aeropuertos
        int masApta=0;
        double aptitudMax=0;
        double aptitud;
        for (int i = 0; i < poblacion.length; i++) {
            aptitud=Auxiliares.fitnessTotalv2(poblacion[i], aeropuertos, vuelos, envios, paquetes, minVuelo, maxVuelo, fitness, i);
            if (aptitud>aptitudMax) {
                aptitudMax=aptitud;
                masApta=i;
            }
        }
        if(aptitudMax>topPredatorFitness){
            topPredatorFitness=aptitudMax;
        }
        return masApta;
    }

    public static void memorySaving(int[][] Presa, double[][] fitness, int[][] PresaOld, double[][] fitnessOld, int currIter, int solutionSize){
        if(currIter==0){
            for (int i = 0; i < Presa.length; i++) {
                PresaOld[i]=Arrays.copyOf(Presa[i], Presa[i].length);
                fitnessOld[i]=Arrays.copyOf(fitness[i], fitness[i].length);
            }
            return;
        }

        int numPaquetes=fitness[0].length;

        for (int i = 0; i < Presa.length; i++) {
            //Voy a ir paquete por paquete
            for (int j=0; j<numPaquetes;j++){
                if(fitnessOld[i][j] > fitness[i][j]){
                    //Copiar la ruta anterior a la actual
                    for (int k=0; k< solutionSize;k++){
                        Presa[i][j*solutionSize+k] = PresaOld[i][j*solutionSize+k];
                        fitness[i][j] = fitnessOld[i][j];
                    }
                }else{
                    for (int k=0; k< solutionSize; k++){
                        PresaOld[i][j*solutionSize+k] = Presa[i][j*solutionSize+k];
                        fitnessOld[i][j] = fitness[i][j];
                    }
                }
            }
        }
    }

    public static void fadEffect(int[][] Presa, double FADs, int SearchAgents_no, int Xmin, int Xmax, double CF, Random rand) {
        if (rand.nextDouble() < FADs) {
            for (int i = 0; i < SearchAgents_no; i++) {
                for (int j = 0; j < Presa[i].length; j++) {
                    if (rand.nextDouble() > FADs) { //Esto es el U. Si el random es mayor a 0.2, se actualiza. Si no, no
                        Presa[i][j] += CF * ((Xmin + rand.nextDouble() * (Xmax- Xmin)));
                    }
                }
            }
        } else {
            double r = rand.nextDouble();
            int Rs = Presa.length;
            int[] perm1 = rand.ints(0, Rs).distinct().limit(Rs).toArray();
            int[] perm2 = rand.ints(0, Rs).distinct().limit(Rs).toArray();
            for (int i = 0; i < Rs; i++) {
                for (int j = 0; j < Presa[i].length; j++) {
                    double stepsize = (FADs * (1 - r) + r) * (Presa[perm1[i]][j] - Presa[perm2[i]][j]);
                    Presa[i][j] += stepsize;
                }
            }
        }
    }



}
