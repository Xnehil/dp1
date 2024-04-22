package com.dp1.backend.utils;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.special.Gamma;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Paquete;
import com.dp1.backend.models.Vuelo;

public class Auxiliares {
    public static double[] levy(int n, double beta) {
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
            HashMap<Integer, Vuelo> vuelos, HashMap<Integer, Envio> envios, ArrayList<Paquete> paquetes, int minVuelo,
            int maxVuelo) {
        // Aquí tengo la solución de todos los paquetes, cada n elementos es un paquete
        int n = aeropuertos.size();
        double fitnessTotal = 0;
        double fitness;
        for (int i = 0; i < solucion.length; i += n) {
            // Enviar a fitnessUnPaquete la solución de un paquete. Es decir, los n
            // elementos de la solución
            int start = i;
            int end = i + n;
            fitness = fitnessUnPaquete(solucion, aeropuertos, vuelos, envios, paquetes.get(i / n), start, end, minVuelo,
                    maxVuelo);
            fitnessTotal += fitness;
        }
        return fitnessTotal;
    }

    public static double fitnessTotalv2(int[] solucion, HashMap<String, Aeropuerto> aeropuertos,
            HashMap<Integer, Vuelo> vuelos, HashMap<Integer, Envio> envios, ArrayList<Paquete> paquetes, int minVuelo,
            int maxVuelo, double[][] fitnessMatrix, int individuo) {
        // Aquí tengo la solución de todos los paquetes, cada n elementos es un paquete
        int n = solucion.length / paquetes.size();
        double fitnessTotal = 0;
        double fitness;
        for (int i : vuelos.keySet()) {
            vuelos.get(i).setCargaPorDia(new HashMap<>());
        }
        for (String i : aeropuertos.keySet()) {
            aeropuertos.get(i).setSalidas(new TreeMap<>());
            aeropuertos.get(i).setEntradas(new TreeMap<>());
        }

        for (int i = 0; i < solucion.length; i += n) {
            // Enviar a fitnessUnPaquete la solución de un paquete. Es decir, los n
            // elementos de la solución
            int start = i;
            int end = i + n;
            fitness = fitnessUnPaquete(solucion, aeropuertos, vuelos, envios, paquetes.get(i / n), start, end, minVuelo,
                    maxVuelo);
            fitnessTotal += fitness;
            fitnessMatrix[individuo][i / n] = fitness;
        }
        return fitnessTotal;
    }

    public static double fitnessUnPaquete(int[] solucion, HashMap<String, Aeropuerto> aeropuertos,
            HashMap<Integer, Vuelo> vuelos, HashMap<Integer, Envio> envios, Paquete paquete, int start, int end,
            int minVuelo, int maxVuelo) {
        // Aquí tengo la solución de un solo paquete
        double fitness = 0;
        Envio envio = envios.get(paquete.getIdEnvío());
        String ciudadActual = envio.getOrigen();
        String ciudadDestino = envio.getDestino();
        Boolean rutaValida = true;
        Aeropuerto destino = aeropuertos.get(ciudadDestino);
        Aeropuerto actual = aeropuertos.get(ciudadActual);

        ZonedDateTime fechaHoraActual = envio.getFechaHoraSalida();
        ZonedDateTime fechaHoraSiguienteSalida;
        ZonedDateTime fechaHoraSiguienteLlegada;
        int dias = actual.getContinente().equals(destino.getContinente()) ? 1 : 2;
        ZonedDateTime fechaHoraLimite = envio.getFechaHoraSalida().plusDays(dias)
                .withZoneSameLocal(destino.getZonaHoraria().toZoneId());

        for (int i = start; i < end; i++) {
            // Evitar out of bounds en el arreglo de solución
            if (solucion[i] < minVuelo) {
                solucion[i] = minVuelo;
            }
            if (solucion[i] > maxVuelo) {
                solucion[i] = maxVuelo;
            }
            if (rutaValida == false) {
                continue;
            }
            Vuelo vuelo = vuelos.get(solucion[i]);
            Aeropuerto destinoDeEsteVuelo = aeropuertos.get(vuelo.getDestino());

            fechaHoraSiguienteSalida = vuelo.getFechaHoraSalida();
            // Se cambia solo la fecha, no las horas. Esto porque los vuelos no tienen
            // fechas, se realizan todos los días.
            fechaHoraSiguienteSalida = fechaHoraSiguienteSalida.with(fechaHoraActual.toLocalDate());
            Boolean ubicacionValida = vuelo.getOrigen().equals(ciudadActual);
            // Boolean tiempoValido = fechaHoraActual.isBefore(fechaHoraSiguiente);
            // Modificación, tiempoValido no es necesario. Si la hora de salida es menor a
            // la hora de llegada, se considera que el vuelo sale al día siguiente
            if (fechaHoraActual.isAfter(fechaHoraSiguienteSalida)) {
                fechaHoraSiguienteSalida = fechaHoraSiguienteSalida.plusDays(1);
            }
            LocalDate fechaAuxiliar = fechaHoraSiguienteSalida.toLocalDate();
            fechaHoraSiguienteLlegada = vuelo.getFechaHoraLlegada().with(fechaAuxiliar);
            fechaHoraSiguienteLlegada = vuelo.getCambioDeDia() ? fechaHoraSiguienteLlegada.plusDays(1)
                    : fechaHoraSiguienteLlegada;

            // Se verifica que haya espacio en el vuelo en tal día. Esto es nuevo
            int cargaAuxiliarVuelo;
            if (vuelo.getCargaPorDia().containsKey(fechaAuxiliar)) {
                cargaAuxiliarVuelo = vuelo.getCargaPorDia().get(fechaAuxiliar);
            } else {
                cargaAuxiliarVuelo = 0;
                vuelo.getCargaPorDia().put(fechaHoraSiguienteSalida.toLocalDate(), 0);
            }

            // Verificamos que haya espacio en el almacén a esa hora
            // int cargaAuxiliarAeropuerto = destinoDeEsteVuelo.cargaAEstaHora(fechaHoraSiguienteSalida.toLocalDateTime());

            Boolean espacioEnVuelo = (vuelo.getCapacidad() > cargaAuxiliarVuelo + 1);
            // Boolean espacioEnAlmacen = (destinoDeEsteVuelo.getCapacidadMaxima() > cargaAuxiliarAeropuerto + 1);

            if (ubicacionValida && espacioEnVuelo ) {
                fitness += 4;
                // Cambio de ciudad
                ciudadActual = vuelo.getDestino();

                // Cambio de fecha y hora
                fechaHoraActual = fechaHoraSiguienteLlegada;
                fechaHoraActual.plusMinutes(5);

                // Actualizar carga por día del vuelo
                vuelo.getCargaPorDia().put(fechaAuxiliar, cargaAuxiliarVuelo + 1);

                // Actualizar carga auxiliar del aeropuerto
                // actual.getSalidas().put(fechaHoraSiguienteSalida.toLocalDateTime(),
                //         actual.getSalidas().getOrDefault(fechaHoraSiguienteSalida.toLocalDateTime(), 0) + 1);
                // destinoDeEsteVuelo.getEntradas().put(fechaHoraSiguienteLlegada.toLocalDateTime(),
                //         destinoDeEsteVuelo.getEntradas().getOrDefault(fechaHoraSiguienteLlegada.toLocalDateTime(),0) + 1);
            } else {
                // Penalización por no ser una ruta válida
                fitness -= (!ubicacionValida ? 6 : 0);
                fitness -= !espacioEnVuelo ? 4 : 0;
                // fitness -= !espacioEnAlmacen ? 4 : 0;
                rutaValida = false;
            }

            // Bonficaciones en ruta válida
            if (rutaValida) {
                // Por llegar
                if (ciudadActual.equals(ciudadDestino)) {
                    fitness += 100;
                    if (fechaHoraActual.isBefore(fechaHoraLimite)) {
                        fitness += 100;
                    } else {
                        fitness -= 50;
                    }
                    break;
                }
                // Por distancia
                // Before the flight
                double oldDistance = Math.pow(actual.getLatitud() - destino.getLatitud(), 2)
                        + Math.pow(actual.getLongitud() - destino.getLongitud(), 2);
                // After the flight
                actual = aeropuertos.get(ciudadActual);
                double newDistance = Math.pow(actual.getLatitud() - destino.getLatitud(), 2)
                        + Math.pow(actual.getLongitud() - destino.getLongitud(), 2);
                // Normalize the fitness change to be between -3 and 3
                fitness += ((oldDistance - newDistance) / 162000) * 2 - 1;

                // Get the ratio of the current load to the maximum load
                // double penalization = (actual.getCargaActual() +
                // actual.getCargaAuxiliarParaFitness()) / actual.getCapacidadMaxima();
                // penalization += (vuelo.getCargaActual() +
                // vuelo.getCargaAuxiliarParaFitness()) / vuelo.getCapacidad();

                // Subtract the penalization from the fitness
                // fitness -= penalization;
            }
        }
        return fitness;
    }

    public static Boolean solucionValidav2(HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos,
            HashMap<Integer, Envio> envios, Paquete paquete, Boolean verbose) {
        Envio envio = envios.get(paquete.getIdEnvío());
        String ciudadActual = envio.getOrigen();
        String ciudadDestino = envio.getDestino();
        ZonedDateTime fechaHoraActual = envio.getFechaHoraSalida();
        ZonedDateTime fechaHoraLimite = fechaHoraActual.plusDays(
                aeropuertos.get(ciudadActual).getContinente().equals(aeropuertos.get(ciudadDestino).getContinente()) ? 1
                        : 2);

        boolean rutaValida = true;

        // Limpiar carga por día
        for (int i : vuelos.keySet()) {
            vuelos.get(i).setCargaPorDia(new HashMap<>());
        }

        if (verbose)
            System.out.println("\nPaquete saliente de " + ciudadActual + " con destino a " + ciudadDestino + " a las "
                    + fechaHoraActual.toLocalTime());
        for (int codVuelo : paquete.getRuta()) {
            Vuelo vuelo = vuelos.get(codVuelo);
            if (vuelo == null) {
                if (verbose)
                    System.out.println("El vuelo con código " + codVuelo + " no existe.");
                rutaValida = false;
                break;
            }
            if (verbose)
                System.out.println("Vuelo " + codVuelo + " de " + vuelo.getOrigen() + " a " + vuelo.getDestino()
                        + " a las " + vuelo.getFechaHoraSalida().toLocalTime() + " y llega a las "
                        + vuelo.getFechaHoraLlegada().toLocalTime());
            if (!vuelo.getOrigen().equals(ciudadActual)) {
                if (verbose)
                    System.out.println("El vuelo " + codVuelo + " no parte de " + ciudadActual + ".");
                rutaValida = false;
                break;
            }

            // ZonedDateTime fechaHoraVuelo =
            // vuelo.getFechaHoraSalida().withZoneSameInstant(aeropuertos.get(ciudadActual).getZonaHoraria().toZoneId());
            ZonedDateTime fechaHoraVuelo = vuelo.getFechaHoraSalida().with(fechaHoraActual.toLocalDate());
            if (!fechaHoraActual.isBefore(fechaHoraVuelo)) {
                if (verbose)
                    System.out.println("El vuelo " + codVuelo
                            + " tiene una hora de salida que no es cronológicamente lógica. Se considera que sale el siguiente día");
                fechaHoraVuelo = fechaHoraVuelo.plusDays(1);
            }

            // Esto es nuevo, es para controlar la carga por día
            int cargaAuxiliarVuelo;
            if (vuelo.getCargaPorDia().containsKey(fechaHoraVuelo.toLocalDate())) {
                cargaAuxiliarVuelo = vuelo.getCargaPorDia().get(fechaHoraVuelo.toLocalDate());
            } else {
                cargaAuxiliarVuelo = 0;
                vuelo.getCargaPorDia().put(fechaHoraVuelo.toLocalDate(), 0);
            }

            if (vuelo.getCapacidad() <= cargaAuxiliarVuelo + 1) {
                if (verbose)
                    System.out.println("El vuelo " + codVuelo + " no tiene capacidad suficiente.");
                rutaValida = false;
                break;
            }

            // Actualizar carga por día
            vuelo.getCargaPorDia().put(fechaHoraVuelo.toLocalDate(), cargaAuxiliarVuelo + 1);

            // Actualizar ciudad y fecha/hora actual
            ciudadActual = vuelo.getDestino();
            // fechaHoraActual =
            // vuelo.getFechaHoraLlegada().withZoneSameInstant(aeropuertos.get(ciudadActual).getZonaHoraria().toZoneId());
            fechaHoraActual = vuelo.getFechaHoraLlegada().with(fechaHoraVuelo.toLocalDate());
            if (vuelo.getCambioDeDia()) {
                fechaHoraActual = fechaHoraActual.plusDays(1);
            }

            // Verificar llegada a destino
            if (ciudadActual.equals(ciudadDestino)) {
                if (fechaHoraActual.isAfter(fechaHoraLimite)) {
                    if (verbose)
                        System.out.println("El paquete llega después del tiempo límite a " + ciudadDestino + ".");
                    rutaValida = false;
                } else {
                    if (verbose)
                        System.out.println("El paquete llega a tiempo a " + ciudadDestino + ".");
                    break;
                }

            }
        }

        if (!rutaValida) {
            if (verbose)
                System.out.println("La ruta no es válida en términos de secuencia de vuelos o capacidad.");
            return false;
        }

        if (!ciudadActual.equals(ciudadDestino)) {
            if (verbose) {
                System.out.println("La ruta no termina en el destino correcto. Se esperaba terminar en " + ciudadDestino
                        + ", pero el último destino de la ruta es " + ciudadActual);
                System.out.println("Número de vuelos en la ruta: " + paquete.getRuta().size());
                System.out.println("Ciudades de la ruta:");
            }
            for (int codVuelo : paquete.getRuta()) {
                if (!vuelos.containsKey(codVuelo)) {
                    if (verbose)
                        System.out.println("El vuelo con código " + codVuelo
                                + " no se encuentra en la lista de vuelos disponibles.");
                    continue;
                }
                Vuelo vuelo = vuelos.get(codVuelo);
                if (verbose)
                    System.out.println("  " + vuelo.getOrigen() + " -> " + vuelo.getDestino());
            }
            return false;
        }

        if (verbose)
            System.out.println("Todas las validaciones pasaron.");
        return true;
    }

    public static int verificacionTotal(int[] solucion, HashMap<String, Aeropuerto> aeropuertos,
            HashMap<Integer, Vuelo> vuelos, HashMap<Integer, Envio> envios, ArrayList<Paquete> paquetes,
            int solutionSize) {
        int n = paquetes.size();
        Boolean esSolucionValida;
        int paquetesEntregados = 0;
        // Verifico todos los paquetes
        for (int j = 0; j < n; j++) {
            ArrayList<Integer> solucionPaquete = new ArrayList<Integer>();
            // Recoger solución para un paquete
            for (int k = 0; k < solutionSize; k++) {
                solucionPaquete.add(solucion[j * solutionSize + k]);
            }
            // Asignar solución a paquete
            Paquete paquete = paquetes.get(j);
            paquete.setRuta(solucionPaquete);
            esSolucionValida = solucionValidav2(aeropuertos, vuelos, envios, paquete, false);
            if (esSolucionValida) {
                paquetesEntregados++;
            }
        }
        return paquetesEntregados;
    }

}