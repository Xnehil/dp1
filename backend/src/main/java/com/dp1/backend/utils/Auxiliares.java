package com.dp1.backend.utils;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.special.Gamma;

import java.time.ZonedDateTime;
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

    public static double fitnessTotalv2(int[] solucion, HashMap<String, Aeropuerto> aeropuertos,
            HashMap<Integer, Vuelo> vuelos, HashMap<Integer, Envio> envios, ArrayList<Paquete> paquetes, int minVuelo, int maxVuelo, double[][] fitnessMatrix, int individuo) {
        // Aquí tengo la solución de todos los paquetes, cada n elementos es un paquete
        int n = solucion.length / paquetes.size();
        double fitnessTotal = 0;
        double fitness;
        for (int i = 0; i < solucion.length; i += n) {
            // Enviar a fitnessUnPaquete la solución de un paquete. Es decir, los n elementos de la solución
            int start = i;
            int end = i + n;
            fitness = fitnessUnPaquete(solucion, aeropuertos, vuelos, envios, paquetes.get(i/n), start, end, minVuelo, maxVuelo);
            fitnessTotal += fitness;
            fitnessMatrix[individuo][i/n]=fitness;
        }
        return fitnessTotal;
    }

    public static double fitnessUnPaquete(int[] solucion, HashMap<String, Aeropuerto> aeropuertos,
            HashMap<Integer, Vuelo> vuelos, HashMap<Integer, Envio> envios, Paquete paquete, int start, int end, int minVuelo, int maxVuelo) {
        // Aquí tengo la solución de un solo paquete
        double fitness=0;
        Envio envio=envios.get(paquete.getIdEnvío());
        String ciudadActual=envio.getOrigen();
        String ciudadDestino=envio.getDestino();
        Boolean rutaValida=true;
        Aeropuerto destino=aeropuertos.get(ciudadDestino);
        Aeropuerto actual=aeropuertos.get(ciudadActual);

        ZonedDateTime fechaHoraActual=envio.getFechaHoraSalida();
        ZonedDateTime fechaHoraSiguiente;
        int dias = actual.getContinente().equals(destino.getContinente()) ? 1 : 2;
        ZonedDateTime fechaHoraLimite = envio.getFechaHoraSalida().plusDays(dias).withZoneSameLocal(destino.getZonaHoraria().toZoneId());


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

            fechaHoraSiguiente=vuelo.getFechaHoraSalida();
            //Se cambia solo la fecha, no las horas. Esto porque los vuelos no tienen fechas, se realizan todos los días.
            fechaHoraSiguiente = fechaHoraSiguiente.with(fechaHoraActual.toLocalDate());



            Boolean ubicacionValida = vuelo.getOrigen().equals(ciudadActual);
            Boolean tiempoValido = fechaHoraActual.isBefore(fechaHoraSiguiente);
            Boolean espacioValido;

            if (ubicacionValida && tiempoValido) {      
                fitness += 4;
                ciudadActual = vuelo.getDestino();
                Boolean cambioDeDia= vuelo.getCambioDeDia();                
                fechaHoraActual=vuelo.getFechaHoraLlegada().with(fechaHoraActual.toLocalDate());
                fechaHoraActual = cambioDeDia ? fechaHoraActual.plusDays(1) : fechaHoraActual;
                fechaHoraActual.plusMinutes(5);
            } else {
                // Penalización por no ser una ruta válida
                fitness -= (!ubicacionValida ? 6 : 0) + (!tiempoValido ? 2 : 0);
                rutaValida=false;

            }

            //Bonficaciones en ruta válida
            if(rutaValida){
                //Por llegar
                if (ciudadActual.equals(ciudadDestino)) {
                    fitness += 100;
                    if(fechaHoraActual.isBefore(fechaHoraLimite)){
                        fitness += 100;
                    } else {
                        fitness -= 50;
                    }
                    break;
                }
                //Por distancia
                // Before the flight
                double oldDistance = Math.pow(actual.getLatitud() - destino.getLatitud(), 2) + Math.pow(actual.getLongitud() - destino.getLongitud(), 2);

                // After the flight
                actual = aeropuertos.get(ciudadActual);
                double newDistance = Math.pow(actual.getLatitud() - destino.getLatitud(), 2) + Math.pow(actual.getLongitud() - destino.getLongitud(), 2);

                // Normalize the fitness change to be between -3 and 3
                fitness += ((oldDistance - newDistance) / 162000) * 6 - 3;
            }


            //Fitness un poquito negativo dependiendo de la capacidad del avión/aeropuerto

            //Añadir bonificación si el paquete llega a tiempo
            //Añadir penalización grave si el paquete no llega a tiempo
            //Añadir bonificación si el paquete llega a su destino
            //Añadir penalización grave si el paquete no llega a su destino
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

    public Boolean solucionValida(int[] solucion, HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos, HashMap<Integer, Envio> envios, ArrayList<Paquete> paquetes){
        //Función de validar solución -> Incluye secuencia de ubicaciones, secuencia de tiempo y respetar el plazo. Basado en función fitness
        //Secuencia de ubicaciones -> Partir siempre de la ubicación en la que estoy
        return true;
    }

}