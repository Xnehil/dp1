package com.dp1.backend.utils;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.special.Gamma;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.ColeccionRuta;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.ItemRutaPosible;
import com.dp1.backend.models.Paquete;
import com.dp1.backend.models.RutaPosible;
import com.dp1.backend.models.Vuelo;
import com.dp1.backend.services.DatosEnMemoriaService;

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
            HashMap<Integer, Vuelo> vuelos, HashMap<String, Envio> envios, ArrayList<Paquete> paquetes, int minVuelo,
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
            HashMap<Integer, Vuelo> vuelos, HashMap<String, Envio> envios, ArrayList<Paquete> paquetes, int minVuelo,
            int maxVuelo, double[][] fitnessMatrix, int individuo) {
        // Aquí tengo la solución de todos los paquetes, cada n elementos es un paquete
        int n = solucion.length / paquetes.size();
        double fitnessTotal = 0;
        double fitness;
        for (int i : vuelos.keySet()) {
            vuelos.get(i).setCargaPorDia(new HashMap<>());
        }
        for (String i : aeropuertos.keySet()) {
            aeropuertos.get(i).setCantPaqParaPlanificacion(new TreeMap<>());
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
            HashMap<Integer, Vuelo> vuelos, HashMap<String, Envio> envios, Paquete paquete, int start, int end,
            int minVuelo, int maxVuelo) {
        // Aquí tengo la solución de un solo paquete
        double fitness = 0;
        Envio envio = envios.get(paquete.getCodigoEnvio());
        String ciudadActual = envio.getOrigen();
        String ciudadDestino = envio.getDestino();
        Boolean rutaValida = true;
        Aeropuerto destino = aeropuertos.get(ciudadDestino);
        Aeropuerto actual;
        Aeropuerto destinoDeEsteVuelo;
        ArrayList<String> yaVisitados = new ArrayList<>();

        ZonedDateTime fechaHoraActual = envio.getFechaHoraSalida();
        ZonedDateTime fechaHoraSiguienteSalida;
        ZonedDateTime fechaHoraSiguienteLlegada;
        ZonedDateTime fechaHoraLimite = envio.getFechaHoraLlegadaPrevista();

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
            yaVisitados.add(ciudadActual);

            Vuelo vuelo = vuelos.get(solucion[i]);
            actual = aeropuertos.get(ciudadActual);
            destinoDeEsteVuelo = aeropuertos.get(vuelo.getDestino());

            // Penalización por visitar un aeropuerto ya visitado
            if (yaVisitados.contains(vuelo.getDestino())) {
                fitness -= 15;
            }

            fechaHoraSiguienteSalida = vuelo.getFechaHoraSalida();
            // Se cambia solo la fecha, no las horas. Esto porque los vuelos no tienen
            // fechas, se realizan todos los días.
            fechaHoraSiguienteSalida = fechaHoraSiguienteSalida.with(fechaHoraActual.toLocalDate());

            Boolean ubicacionValida = vuelo.getOrigen().equals(ciudadActual);
            // Modificación, tiempoValido no es necesario. Si la hora de salida es menor a
            // la hora de llegada, se considera que el vuelo sale al día siguiente
            if (fechaHoraActual.isAfter(fechaHoraSiguienteSalida)) {
                fechaHoraSiguienteSalida = fechaHoraSiguienteSalida.plusDays(1);
                // Penalización por cambio de día
                // fitness -= 4;
            }
            LocalDate fechaAuxiliar = fechaHoraSiguienteSalida.toLocalDate();
            fechaHoraSiguienteLlegada = vuelo.getFechaHoraLlegada().with(fechaAuxiliar);
            fechaHoraSiguienteLlegada = fechaHoraSiguienteLlegada.plusDays(vuelo.getCambioDeDia());
            // Penalización por tiempo de espera
            Duration espera = Duration.between(fechaHoraActual, fechaHoraSiguienteSalida);
            fitness -= espera.toHours() / 12;

            if (fechaHoraSiguienteLlegada.isAfter(fechaHoraLimite)) {
                fitness -= 5;
            }

            // Se verifica que haya espacio en el vuelo en tal día.
            int cargaAuxiliarVuelo;
            if (vuelo.getCargaPorDia().containsKey(fechaAuxiliar)) {
                cargaAuxiliarVuelo = vuelo.getCargaPorDia().get(fechaAuxiliar);
            } else {
                cargaAuxiliarVuelo = 0;
                vuelo.getCargaPorDia().put(fechaHoraSiguienteSalida.toLocalDate(), 0);
            }

            // Verificamos que haya espacio en el almacén a esa hora de llegada
            int cargaAuxiliarAeropuerto = destinoDeEsteVuelo
                    .paquetesAEstaHoraPlanificacion(fechaHoraSiguienteLlegada.toLocalDateTime());

            Boolean espacioEnVuelo = (vuelo.getCapacidad() > cargaAuxiliarVuelo + 1);
            Boolean espacioEnAlmacen = (destinoDeEsteVuelo.getCapacidadMaxima() > cargaAuxiliarAeropuerto + 1);

            if (ubicacionValida && espacioEnVuelo && espacioEnAlmacen) {
                fitness += 8;
                // Cambio de ciudad
                ciudadActual = vuelo.getDestino();

                // Cambio de fecha y hora
                fechaHoraActual = fechaHoraSiguienteLlegada;
                fechaHoraActual.plusMinutes(5);

                // Actualizar carga por día del vuelo
                vuelo.getCargaPorDia().put(fechaAuxiliar, cargaAuxiliarVuelo + 1);

                // Actualizar carga del aeropuerto luego de salir
                actual.paqueteSalePlanificacion(fechaHoraSiguienteSalida.toLocalDateTime());
                // Actualizar carga del aeropuerto luego de llegar
                destinoDeEsteVuelo.paqueteEntraPlanificacion(fechaHoraSiguienteLlegada.toLocalDateTime());
            } else {
                // Penalización por no ser una ruta válida
                fitness -= (!ubicacionValida ? 14 : 0);
                fitness -= !espacioEnVuelo ? 5 : 0;
                fitness -= !espacioEnAlmacen ? 5 : 0;
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
                        fitness -= 95;
                    }
                    break;
                }

                // Por distancia
                // Before the flight
                double oldDistance = calculateHaversineDistance(actual, destino);
                // After the flight
                actual = aeropuertos.get(ciudadActual);
                double newDistance = calculateHaversineDistance(actual, destino);
                // Normalizar la distancia para que esté entre -2 y 2
                // fitness += ((oldDistance - newDistance) / 403074761) *2 - 1;
                // fitness += ((oldDistance - newDistance) / 162000) *4 - 2;

                fitness += ((oldDistance - newDistance) / 20015) * 2 - 1;

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

    public static double calculateHaversineDistance(Aeropuerto a, Aeropuerto b) {
        final int R = 6371; // Radio de la Tierra en km
        double latDistance = Math.toRadians(b.getLatitud() - a.getLatitud());
        double lonDistance = Math.toRadians(b.getLongitud() - a.getLongitud());
        double aVal = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(a.getLatitud())) * Math.cos(Math.toRadians(b.getLatitud()))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(aVal), Math.sqrt(1 - aVal));
        return R * c;
    }

    private static double calculateEquirectangularApproximation(Aeropuerto a, Aeropuerto b) {
        final int R = 6371; // Radio de la Tierra en km
        double lat1 = Math.toRadians(a.getLatitud());
        double lat2 = Math.toRadians(b.getLatitud());
        double lon1 = Math.toRadians(a.getLongitud());
        double lon2 = Math.toRadians(b.getLongitud());
        double x = (lon2 - lon1) * Math.cos((lat1 + lat2) / 2);
        double y = lat2 - lat1;
        return (x * x + y * y) * R;
    }

    public static double calculateEuclideanDistance(Aeropuerto a, Aeropuerto b) {
        double x = a.getLatitud() - b.getLatitud();
        double y = a.getLongitud() - b.getLongitud();
        return Math.sqrt(x * x + y * y);
    }

    public static Boolean solucionValidav2(HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos,
            HashMap<String, Envio> envios, Paquete paquete, Boolean verbose) {
        Envio envio = envios.get(paquete.getCodigoEnvio());
        String ciudadActual = envio.getOrigen();
        String ciudadDestino = envio.getDestino();
        ZonedDateTime fechaHoraActual = envio.getFechaHoraSalida();
        ZonedDateTime fechaHoraLimite = envio.getFechaHoraLlegadaPrevista();
        Aeropuerto aeropuertoActual = aeropuertos.get(ciudadActual);
        Aeropuerto aeropuertoDestinoVuelo;

        boolean rutaValida = true;

        if (verbose) {
            System.out.println("\nPaquete saliente de " + ciudadActual + " con destino a " + ciudadDestino + " a las "
                    + fechaHoraActual.toLocalTime() + " del día " + fechaHoraActual.toLocalDate());
            System.out.println("Fecha límite de entrega: " + fechaHoraLimite.toLocalDate() + " a las "
                    + fechaHoraLimite.toLocalTime());
        }
        for (int codVuelo : paquete.getRuta()) {
            Vuelo vuelo = vuelos.get(codVuelo);

            if (vuelo == null) {
                if (verbose)
                    System.out.println("El vuelo con código " + codVuelo + " no existe.");
                rutaValida = false;
                break;
            }

            if (!vuelo.getOrigen().equals(ciudadActual)) {
                if (verbose)
                    System.out.println("El vuelo " + codVuelo + " no parte de " + ciudadActual + ".");
                rutaValida = false;
                break;
            }

            // ZonedDateTime fechaHoraVuelo =
            // vuelo.getFechaHoraSalida().withZoneSameInstant(aeropuertos.get(ciudadActual).getZonaHoraria().toZoneId());
            ZonedDateTime fechaHoraVuelo = vuelo.getFechaHoraSalida().with(fechaHoraActual.toLocalDate());
            ZonedDateTime fechaHoraLlegada = vuelo.getFechaHoraLlegada().with(fechaHoraActual.toLocalDate());
            fechaHoraLlegada.plusDays(vuelo.getCambioDeDia());
            if (verbose) {
                System.out.println("Vuelo " + codVuelo + " de " + vuelo.getOrigen() + " a " + vuelo.getDestino()
                        + " a las " + fechaHoraVuelo.toLocalTime() + " del día " + fechaHoraVuelo.toLocalDate() +
                        " y llega a las " + fechaHoraLlegada.toLocalTime() + " del día "
                        + fechaHoraLlegada.toLocalDate());
            }
            if (!fechaHoraActual.isBefore(fechaHoraVuelo)) {

                fechaHoraVuelo = fechaHoraVuelo.plusDays(1);
                fechaHoraLlegada = fechaHoraLlegada.plusDays(1);
                if (verbose) {
                    System.out.println("El vuelo " + codVuelo
                            + " tiene una hora de salida que no es cronológicamente lógica. Se considera que sale el siguiente día");
                    System.out.println("Vuelo " + codVuelo + " de " + vuelo.getOrigen() + " a " + vuelo.getDestino()
                            + " a las " + fechaHoraVuelo.toLocalTime() + " del día " + fechaHoraVuelo.toLocalDate()
                            + " y llega a las " + fechaHoraLlegada.toLocalTime() + " del día "
                            + fechaHoraLlegada.toLocalDate());
                }
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

            // Controlar la carga del aeropuerto
            aeropuertoDestinoVuelo = aeropuertos.get(vuelo.getDestino());
            int cargaAuxiliarAeropuerto = aeropuertoDestinoVuelo
                    .paquetesAEstaHoraPlanificacion(fechaHoraLlegada.toLocalDateTime());
            if (aeropuertoDestinoVuelo.getCapacidadMaxima() <= cargaAuxiliarAeropuerto + 1) {
                if (verbose)
                    System.out.println("El aeropuerto " + vuelo.getDestino() + " no tiene capacidad suficiente.");
                rutaValida = false;
                break;
            }

            // Actualizar carga por día
            vuelo.getCargaPorDia().put(fechaHoraVuelo.toLocalDate(), cargaAuxiliarVuelo + 1);

            // Actualizar carga del aeropuerto luego de salir
            aeropuertoActual.paqueteSalePlanificacion(fechaHoraVuelo.toLocalDateTime());
            // Actualizar carga del aeropuerto luego de llegar
            aeropuertoDestinoVuelo.paqueteEntraPlanificacion(fechaHoraLlegada.toLocalDateTime());

            // Actualizar ciudad y fecha/hora actual
            ciudadActual = vuelo.getDestino();
            // fechaHoraActual =
            // vuelo.getFechaHoraLlegada().withZoneSameInstant(aeropuertos.get(ciudadActual).getZonaHoraria().toZoneId());
            fechaHoraActual = vuelo.getFechaHoraLlegada().with(fechaHoraVuelo.toLocalDate());
            fechaHoraActual = fechaHoraActual.plusDays(vuelo.getCambioDeDia());
            // Verificar llegada a destino
            if (ciudadActual.equals(ciudadDestino)) {
                if (fechaHoraActual.isAfter(fechaHoraLimite)) {
                    if (verbose) {
                        System.out.println("El paquete llega después del tiempo límite a " + ciudadDestino + ".");
                    }
                    rutaValida = false;
                } else {
                    if (verbose) {
                        System.out.println("El paquete llega a tiempo a " + ciudadDestino + ".");
                    }
                }
                break;

            }
        }

        if (!rutaValida) {
            if (verbose) {
                System.out.println("La ruta no es válida en términos de secuencia de vuelos o capacidad.");
            }
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
                    if (verbose) {
                        System.out.println("El vuelo con código " + codVuelo
                                + " no se encuentra en la lista de vuelos disponibles.");
                    }
                    continue;
                }
                Vuelo vuelo = vuelos.get(codVuelo);
                if (verbose) {
                    System.out.println("  " + vuelo.getOrigen() + " -> " + vuelo.getDestino());
                }
            }
            return false;
        }

        if (verbose)
            System.out.println("Todas las validaciones pasaron.");
        return true;
    }

    public static int verificacionTotal(int[] solucion, HashMap<String, Aeropuerto> aeropuertos,
            HashMap<Integer, Vuelo> vuelos, HashMap<String, Envio> envios, ArrayList<Paquete> paquetes,
            int solutionSize) {
        int n = paquetes.size();
        Boolean esSolucionValida;
        int paquetesEntregados = 0;

        // Limpiar carga por día
        for (int i : vuelos.keySet()) {
            vuelos.get(i).setCargaPorDia(new HashMap<>());
        }

        // Limpiar carga por día de los aeropuertos
        for (String i : aeropuertos.keySet()) {
            aeropuertos.get(i).setCantPaqParaPlanificacion(new TreeMap<>());
        }

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

        // Limpiar carga por día de los aeropuertos
        for (String i : aeropuertos.keySet()) {
            aeropuertos.get(i).setCantPaqParaPlanificacion(new TreeMap<>());
        }
        // Limpiar carga por día
        for (int i : vuelos.keySet()) {
            vuelos.get(i).setCargaPorDia(new HashMap<>());
        }

        return paquetesEntregados;
    }

    public static int verificacionTotalPaquetes(HashMap<String, Aeropuerto> aeropuertos,
            HashMap<Integer, Vuelo> vuelos, HashMap<String, Envio> envios, ArrayList<Paquete> paquetes,
            DatosEnMemoriaService datosEnMemoriaService) {
        int n = paquetes.size();
        int rutasNuevas = 0;
        Boolean esSolucionValida;
        int paquetesEntregados = 0;
        int paquetesRutasSalvadas = 0;

        // Limpiar carga por día
        for (int i : vuelos.keySet()) {
            vuelos.get(i).setCargaPorDia(new HashMap<>());
        }

        // Limpiar carga por día de los aeropuertos
        for (String i : aeropuertos.keySet()) {
            aeropuertos.get(i).setCantPaqParaPlanificacion(new TreeMap<>());
        }

        // Verifico todos los paquetes
        for (int j = 0; j < n; j++) {
            // Asignar solución a paquete
            Paquete paquete = paquetes.get(j);
            esSolucionValida = solucionValidav2(aeropuertos, vuelos, envios, paquete, false);
            if (esSolucionValida) {
                Envio envio = envios.get(paquete.getCodigoEnvio());
                String origen = envio.getOrigen();
                String destino = envio.getDestino();
                String cadenaABuscar = envio.getOrigen() + envio.getDestino();
                for (int i : paquete.getRuta()) {
                    cadenaABuscar += ("-" + i);
                }
                if (!datosEnMemoriaService.seTieneruta(cadenaABuscar)) {
                    // Por ahora no se inserta en la base de datos
                    datosEnMemoriaService.insertarRuta(envio, paquete);
                    rutasNuevas++;
                } else {
                    ColeccionRuta rutasDisponibles = datosEnMemoriaService.getRutasPosibles().get(origen + destino);
                    for (RutaPosible rp : rutasDisponibles.getRutasPosibles()) {
                        ArrayList<Integer> rutaPaquete = paquete.getRuta();
                        List<ItemRutaPosible> rutaPosibleStruct = rp.getFlights();
                        Collections.sort(rutaPosibleStruct, new Comparator<ItemRutaPosible>() {
                            @Override
                            public int compare(ItemRutaPosible irp1, ItemRutaPosible irp2) {
                                return Integer.compare(irp1.getDiaRelativo(), irp2.getDiaRelativo());
                            }
                        });
                        // Crear un ArrayList de idVuelo ordenado por diaRelativo
                        ArrayList<Integer> idVuelosOrdenados = new ArrayList<>();
                        for (ItemRutaPosible irp : rutaPosibleStruct) {
                            idVuelosOrdenados.add(irp.getIdVuelo());
                        }

                        if (rutaPaquete.equals(idVuelosOrdenados)) {
                            paquete.setRutaPosible(rp);
                            break;
                        } else {
                            //siguiente, no deberia haber caso que no encuentre la ruta buscada
                        }

                    }
                }
                paquete.setLlegoDestino(true);
                paquetesEntregados++;
            } else {
                // Buscar la ruta en la base de datos
                Envio envio = envios.get(paquete.getCodigoEnvio());
                String cadenaABuscar = envio.getOrigen() + envio.getDestino();
                try {
                    List<RutaPosible> rutasPosibles = datosEnMemoriaService.getRutasPosibles().get(cadenaABuscar)
                            .getRutasPosibles();
                    // int random = (int) (Math.random() * rutasPosibles.size());
                    ArrayList<Integer> ruta = new ArrayList<Integer>();
                    ArrayList<ZonedDateTime> fechas = new ArrayList<ZonedDateTime>();
                    RutaPosible rutaPosible = rutasPosibles.get(buscarRutaPosibleApta(aeropuertos, vuelos, envios, paquete, rutasPosibles));
                    // System.out.println("Funcion verificar ruta. rp inf: " + rutaPosible.getId() + " "
                            // + rutaPosible.getFlights());
                    for (int i = 0; i < rutaPosible.getFlights().size(); i++) {
                        ruta.add(rutaPosible.getFlights().get(i).getIdVuelo());
                        int unixTimestampSeconds = rutaPosible.getFlights().get(i).getDiaRelativo();
                        fechas.add((ZonedDateTime.ofInstant(Instant.ofEpochSecond(unixTimestampSeconds),
                                ZoneId.systemDefault())));
                    }
                    paquete.setRuta(ruta);
                    paquete.setFechasRuta(fechas);
                    paquete.setLlegoDestino(true);
                    paquete.setRutaPosible(rutaPosible);// añadí esto, a ver si funciona
                    paquetesRutasSalvadas++;
                } catch (Exception e) {
                    System.out.println("No se encontró la ruta en las rutas posibles cargadas en memoria.");
                    System.out.println("Ruta no encontrada: " + cadenaABuscar);
                }
            }
        }

        System.out.println("Rutas nuevas: " + rutasNuevas);
        System.out.println("Rutas salvadas: " + paquetesRutasSalvadas);
        System.out.println("Paquetes no entregados: " + (n - paquetesEntregados - paquetesRutasSalvadas));

        // Limpiar carga por día de los aeropuertos
        for (String i : aeropuertos.keySet()) {
            aeropuertos.get(i).setCantPaqParaPlanificacion(new TreeMap<>());
        }
        // Limpiar carga por día
        for (int i : vuelos.keySet()) {
            vuelos.get(i).setCargaPorDia(new HashMap<>());
        }

        return paquetesEntregados + paquetesRutasSalvadas;
    }

    public static int buscarRutaPosibleApta(HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos,
    HashMap<String, Envio> envios, Paquete paquete,  List<RutaPosible> rutasPosibles) {
        Collections.shuffle(rutasPosibles);
       
        for (int i = 0; i < rutasPosibles.size(); i++) {
            RutaPosible rutaPosible = rutasPosibles.get(i);
            ArrayList<Integer> ruta = new ArrayList<Integer>();
            for (int j = 0; j < rutaPosible.getFlights().size(); j++) {
                ruta.add(rutaPosible.getFlights().get(j).getIdVuelo());
            }
            paquete.setRuta(ruta);
            if (solucionValidav2(aeropuertos, vuelos, envios, paquete, false)) {
                return i;
            }
        }
        return 0;
    }

    public static int verificacionTotalPaquetesSimulacion(HashMap<String, Aeropuerto> aeropuertos,
            HashMap<Integer, Vuelo> vuelos, HashMap<String, Envio> envios, ArrayList<Paquete> paquetes,
            DatosEnMemoriaService datosEnMemoriaService) {
        
                int n = paquetes.size();
        int rutasNuevas = 0;
        Boolean esSolucionValida;
        int paquetesEntregados = 0;
        int paquetesRutasSalvadas = 0;

        // Limpiar carga por día
        for (int i : vuelos.keySet()) {
            vuelos.get(i).setCargaPorDia(new HashMap<>());
        }

        // Limpiar carga por día de los aeropuertos
        for (String i : aeropuertos.keySet()) {
            aeropuertos.get(i).setCantPaqParaPlanificacion(new TreeMap<>());
        }

        // Verifico todos los paquetes
        for (int j = 0; j < n; j++) {
            // Asignar solución a paquete
            Paquete paquete = paquetes.get(j);
            esSolucionValida = solucionValidav2(aeropuertos, vuelos, envios, paquete, false);
            if (esSolucionValida) {
                Envio envio = envios.get(paquete.getCodigoEnvio());
                String cadenaABuscar = envio.getOrigen() + envio.getDestino();
                for (int i : paquete.getRuta()) {
                    cadenaABuscar += ("-" + i);
                }
                if (!datosEnMemoriaService.seTieneruta(cadenaABuscar)) {
                    // Por ahora no se inserta en la base de datos
                    // datosEnMemoriaService.insertarRuta(envio, paquete);
                    // rutasNuevas++;
                }
                paquete.setLlegoDestino(true);
                paquetesEntregados++;
            } else {
                // Buscar la ruta en la base de datos
                Envio envio = envios.get(paquete.getCodigoEnvio());
                String cadenaABuscar = envio.getOrigen() + envio.getDestino();
                try {
                    List<RutaPosible> rutasPosibles = datosEnMemoriaService.getRutasPosibles().get(cadenaABuscar)
                            .getRutasPosibles();
                    int random = (int) (Math.random() * rutasPosibles.size());
                    ArrayList<Integer> ruta = new ArrayList<Integer>();
                    ArrayList<ZonedDateTime> fechas = new ArrayList<ZonedDateTime>();
                    RutaPosible rutaPosible = rutasPosibles.get(random);
                    for (int i = 0; i < rutaPosible.getFlights().size(); i++) {
                        ruta.add(rutaPosible.getFlights().get(i).getIdVuelo());
                        int unixTimestampSeconds = rutaPosible.getFlights().get(i).getDiaRelativo();
                        fechas.add((ZonedDateTime.ofInstant(Instant.ofEpochSecond(unixTimestampSeconds), ZoneId.systemDefault())));
                    }
                    paquete.setRuta(ruta);
                    paquete.setFechasRuta(fechas);
                    paquete.setLlegoDestino(true);
                    paquetesRutasSalvadas++;
                } catch (Exception e) {
                    System.out.println("No se encontró la ruta en las rutas posibles cargadas en memoria.");
                    System.out.println("Ruta no encontrada: " + cadenaABuscar);
                }
            }
        }

        System.out.println("Rutas nuevas: " + rutasNuevas);
        System.out.println("Rutas salvadas: " + paquetesRutasSalvadas);
        System.out.println("Paquetes no entregados: " + (n - paquetesEntregados- paquetesRutasSalvadas));



        // Limpiar carga por día de los aeropuertos
        for (String i : aeropuertos.keySet()) {
            aeropuertos.get(i).setCantPaqParaPlanificacion(new TreeMap<>());
        }
        // Limpiar carga por día
        for (int i : vuelos.keySet()) {
            vuelos.get(i).setCargaPorDia(new HashMap<>());
        }

        return paquetesEntregados + paquetesRutasSalvadas;
    }

}