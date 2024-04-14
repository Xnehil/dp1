package com.dp1.backend.utils;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.special.Gamma;

import java.util.ArrayList;
import java.util.HashMap;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Paquete;
import com.dp1.backend.models.Vuelo;

public class Auxiliares {
    public static double[]levy(int n,  double beta) {
        double num = Gamma.gamma(1 + beta) * Math.sin(Math.PI * beta / 2);
        double den = Gamma.gamma((1 + beta) / 2) * beta * Math.pow(2, (beta - 1) / 2);
        double sigma_u = Math.pow(num / den, 1 / beta);

        NormalDistribution distU = new NormalDistribution(0, sigma_u);
        NormalDistribution distV = new NormalDistribution(0, 1);

        double[] z = new double[n];
        for (int i = 0; i < n; i++) {
                double u = distU.sample();
                double v = distV.sample();
                z[i] = u / Math.pow(Math.abs(v), 1 / beta);
        }
        return z;
    }

    public static double[] brown(int n) {
        NormalDistribution dist = new NormalDistribution(0, 1);
        double[] brown = new double[n];
        for (int i = 0; i < n; i++) {
            brown[i] = dist.sample();
        }
        return brown;
    }

    public static double fitnessTotal(int[] solucion, HashMap<String, Aeropuerto> aeropuertos,
            HashMap<Integer, Vuelo> vuelos, HashMap<Integer, Envio> envios, ArrayList<Paquete> paquetes, int minVuelo, int maxVuelo) {
        // Aquí tengo la solución de todos los paquetes, cada n elementos es un paquete
        int n = aeropuertos.size();
        double fitnessTotal = 0;
        double fitness;
        for (int i = 0; i < solucion.length; i += n) {
            // Enviar a fitnessUnPaquete la solución de un paquete. Es decir, los n elementos de la solución
            int start = i;
            int end = i + n;
            fitness = fitnessUnPaquete(solucion, aeropuertos, vuelos, envios, paquetes.get(i/n), start, end, minVuelo, maxVuelo);
            fitnessTotal += fitness;
        }
        return fitnessTotal;
    }

    public static double fitnessUnPaquete(int[] solucion, HashMap<String, Aeropuerto> aeropuertos,
            HashMap<Integer, Vuelo> vuelos, HashMap<Integer, Envio> envios, Paquete paquete, int start, int end, int minVuelo, int maxVuelo) {
        // Aquí tengo la solución de un solo paquete
        double fitness=0;
        String ciudadActual=envios.get(paquete.getIdEnvío()).getOrigen();
        String ciudadDestino=envios.get(paquete.getIdEnvío()).getDestino();
        Boolean rutaValida=true;
        Aeropuerto destino=aeropuertos.get(ciudadDestino);
        Aeropuerto actual=aeropuertos.get(ciudadActual);


        for (int i = start; i < end; i++) {
            //Evitar out of bounds en el arreglo de solución
            if(solucion[i] < minVuelo){
                solucion[i] = minVuelo;
            }
            if(solucion[i] > maxVuelo){
                solucion[i] = maxVuelo;
            }
            if (rutaValida==false){
                

                continue;
            }
            Vuelo vuelo = vuelos.get(solucion[i]);

            if (vuelo.getOrigen().equals(ciudadActual)) {      
                fitness += 2;
            } else {
                // Penalización por no ser una ruta válida
                fitness -= 0.1;
                rutaValida=false;
                //Premio por qué tan cerca está ese el origen del vuelo de la ciudad actual
                Aeropuerto vueloOrigen = aeropuertos.get(vuelo.getOrigen());
                double distance = Math.sqrt(Math.pow(actual.getLatitud() - vueloOrigen.getLatitud(), 2) + Math.pow(actual.getLongitud() - vueloOrigen.getLongitud(), 2));
                fitness += 1/distance;

            }

            //Bonficaciones en ruta válida
            if(rutaValida){
                //Por llegar
                if (ciudadActual.equals(ciudadDestino)) {
                    fitness += 5;
                    break;
                }
                //Por distancia
                // Before the flight
                double oldDistance = Math.sqrt(Math.pow(actual.getLatitud() - destino.getLatitud(), 2) + Math.pow(actual.getLongitud() - destino.getLongitud(), 2));

                // After the flight
                ciudadActual = vuelo.getDestino();
                actual = aeropuertos.get(ciudadActual);
                double newDistance = Math.sqrt(Math.pow(actual.getLatitud() - destino.getLatitud(), 2) + Math.pow(actual.getLongitud() - destino.getLongitud(), 2));

                // If the distance to the destination decreased, add a reward to the fitness
                if (newDistance < oldDistance) {
                    fitness += (oldDistance - newDistance);
                }
            }


            //Fitness un poquito negativo dependiendo de la capacidad del avión/aeropuerto

            //Añadir al fitness la distancia entre los aeropuertos de la solución
            //Añadir bonificación si el paquete llega a tiempo
            //Añadir penalización grave si el paquete no llega a tiempo
            //Añadir bonificación si el paquete llega a su destino
            //Añadir penalización grave si el paquete no llega a su destino

            //Fátima André
        }
        return fitness;
    }

    public static double fitnessUnPaqueteFatima(int[] solucion, HashMap<String, Aeropuerto> aeropuertos,
            HashMap<Integer, Vuelo> vuelos, HashMap<Integer, Envio> envios, ArrayList<Paquete> paquetes, int start, int end) {
        // Aquí tengo la solución de un solo paquete
        double fitness=0;
        for (int i = start; i < end; i++) {
            //Añadir al fitness la distancia entre los aeropuertos de la solución
            //Añadir bonificación si el paquete llega a tiempo
            //Añadir penalización grave si el paquete no llega a tiempo
            //Añadir bonificación si el paquete llega a su destino
            //Añadir penalización grave si el paquete no llega a su destino

            //Fátima André

            fitness+=1;
        }
        return 0;
    }

    public static double fitnessUnPaqueteAndre(int[] solucion, HashMap<String, Aeropuerto> aeropuertos,
            HashMap<Integer, Vuelo> vuelos, HashMap<Integer, Envio> envios, ArrayList<Paquete> paquetes, int start, int end) {
        // Aquí tengo la solución de un solo paquete
        double fitness=0;
        for (int i = start; i < end; i++) {
            //Añadir al fitness la distancia entre los aeropuertos de la solución
            //Añadir bonificación si el paquete llega a tiempo
            //Añadir penalización grave si el paquete no llega a tiempo
            //Añadir bonificación si el paquete llega a su destino
            //Añadir penalización grave si el paquete no llega a su destino

            //Fátima André

            fitness+=1;
        }
        return 0;
    }

    //Función de validar solución - Mauricio
}