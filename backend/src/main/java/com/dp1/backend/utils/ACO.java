package com.dp1.backend.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.sql.results.graph.FetchStyleAccess;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Paquete;
import com.dp1.backend.models.ProgramacionVuelo;
import com.dp1.backend.models.Vuelo;

public class ACO {
    public static double[] minYMaxTiempoVuelo;
    public static double[] minYMaxDistanciaAeropuertos;

    public static void run(HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos,
            HashMap<String, Envio> envios,
            ArrayList<Paquete> paquetes, int numeroIteraciones) {
        // Definir una matriz que defina Vuelo, Costo, Visibilidad() y Fermonas
        // El costo será dinámico para algunas variables: tiempo de vuelo (entre mismas
        // ciudades varia el t de vuelo), capacidades,
        // plazos de entrega,

        HashMap<Integer, Double[]> tabla = new HashMap<>(); // 4 columnas: Costo, Visibilidad, Feromonas
        // Dado que el costo (y por tanto la visibilidad) se definirá en la iteración,
        // entonces esta tabla maestra solo guardará las feromonas. (Duda)
        minYMaxTiempoVuelo = Normalizacion.obtenerMinMaxTiempoVuelo(vuelos);
        minYMaxDistanciaAeropuertos = Normalizacion.obtenerDistanciaExtrema(aeropuertos); // el mínimo no tiene sentido.
                                                                                          // Es 0
        minYMaxDistanciaAeropuertos[0] = 0;
        minYMaxTiempoVuelo[0] = 0;
        // System.out.println("Min tiempo de vuelo: " + minYMaxTiempoVuelo[0]); //129
        // System.out.println("Max tiempo de vuelo: " + minYMaxTiempoVuelo[1]); //890
        // System.out.println("Min distancia entre aeropuertos: " +
        // minYMaxDistanciaAeropuertos[0]); //0km
        // System.out.println("Max distancia entre aeropuertos: " +
        // minYMaxDistanciaAeropuertos[1]); //13463 km

        for (int id : vuelos.keySet()) {
            tabla.put(id, new Double[] { (double) vuelos.get(id).getCapacidad(), 0.0, 0.1 }); // inicializar matrices.
                                                                                              // Los costos serán
                                                                                              // dinámicos, por eso será
                                                                                              // definido en las
                                                                                              // iteraciones

        }
        System.out.println("Numero de paquetes: " + paquetes.size());

        generarArchivoTabla(tabla, "salida");
        // Iteraremos muchas veces para todos los paquetes. Es decir, para cada
        // iteración se tomarán en cuenta todos los paquettes
        int iteracionAux = 1, exito = 0;
        while (iteracionAux <= numeroIteraciones) {

            for (int id : vuelos.keySet()) { // Esto es para inicializar las capacidades de los vuelos en cada iteración
                // double costo = costo(vuelos.get(id), );
                tabla.get(id)[1] = tabla.get(id)[0];// en la 2da columna de mi tabla guardaré la capacidad dinámica,
                                                    // mientra q en la 1ra guardaré la capacidad máxima del vuelo
            }

            for (Paquete paq : paquetes) {

                int i = 0;
                String ciudadActualPaquete;
                while (true) {
                    HashMap<Integer, Double[]> tablaOpcionesVuelos = new HashMap<>();
                    // FIltrar y llenar una tabla con todos los vuelos que salen del origen del
                    // paquete para ver posibles salidas
                    // O su ultima ubicación
                    // validar que no vuelva a una ciudad ya visitada
                    if (paq.getRuta().isEmpty()) {
                        // de acuerdo a su aeropuerto de origen
                        ciudadActualPaquete = envios.get(paq.getCodigoEnvio()).getOrigen();
                    } else {
                        // de acuerdo al aeropuerto destino de su último vuelo
                        ArrayList<Integer> rutaPaquete = paq.getRuta();
                        int idUltimoVuelo = rutaPaquete.get(rutaPaquete.size() - 1);
                        ciudadActualPaquete = vuelos.get(idUltimoVuelo).getDestino();
                    }
                    // A partir de la ciudad actual, llenaremos la tabla de los vuelos que puede
                    // tomar
                    for (int id : tabla.keySet()) {
                        String ciudadOrigenVuelo = vuelos.get(id).getOrigen();
                        if (ciudadActualPaquete.equals(ciudadOrigenVuelo) && tabla.get(id)[1] > 0) { // que el vuelo
                                                                                                     // tenga espacio
                                                                                                     // aún

                            if (envios.get(paq.getCodigoEnvio()).getFechaHoraSalida()
                                    .compareTo(vuelos.get(id).getFechaHoraSalida()) < 0) {
                                // que la fecha que llegó el paquete sea anterior al vuelo que tomará

                                tablaOpcionesVuelos.put(id, new Double[4]); // guardaremos costo, visibilidad,
                                                                            // visibilidad*fermonoas y probabilidad
                            }

                        }
                    }

                    // Definir costo de cada vuelo, visibilidad
                    for (int id : tablaOpcionesVuelos.keySet()) {
                        // tablaOpcionesVuelos.get(id)[0] = costo(vuelos.get(id), paq, envios,
                        // aeropuertos);
                        tablaOpcionesVuelos.get(id)[1] = 1 / tablaOpcionesVuelos.get(id)[0];
                        tablaOpcionesVuelos.get(id)[2] = tablaOpcionesVuelos.get(id)[1] * tabla.get(id)[2];
                    }
                    // Definir la probabilidad
                    double sumaDeProductoVisiXFeromonas = 0.0;
                    for (int id : tablaOpcionesVuelos.keySet()) {
                        sumaDeProductoVisiXFeromonas += tablaOpcionesVuelos.get(id)[2];
                    }
                    for (int id : tablaOpcionesVuelos.keySet()) {
                        tablaOpcionesVuelos.get(id)[3] = tablaOpcionesVuelos.get(id)[2] / sumaDeProductoVisiXFeromonas;
                    }

                    // Escoger un vuelo al azar
                    double[] probabilidades = new double[tablaOpcionesVuelos.size()];
                    int[] vuelosAux = new int[tablaOpcionesVuelos.size()];
                    int index = 0;
                    for (Map.Entry<Integer, Double[]> entry : tablaOpcionesVuelos.entrySet()) {
                        vuelosAux[index] = entry.getKey(); // Guardar la clave en vuelosAux
                        probabilidades[index] = entry.getValue()[3];
                        index++;
                    }
                    int posVueloEscogido = aco_auxiliares.determinarVueloEscogido(probabilidades);
                    int vueloEscogido = vuelosAux[posVueloEscogido];
                    // Registrar el vuelo elegido por el paquete
                    paq.getRuta().add(vueloEscogido);

                    // quitar un slot al vuelo
                    // tabla.get(vueloEscogido)[1]--; // ¿Qué pasaría si ya no hay vuelos por tomar?
                    // Creo que eso no va a pasar

                    System.out.println("                IMPRIMIENDO TABLA DE OPCIONES PARA EL PAQUETE "
                            + paq.getIdPaquete() + " " + envios.get(paq.getCodigoEnvio()).getOrigen() + " "
                            + envios.get(paq.getCodigoEnvio()).getDestino());

                    // generarArchivoTabla(tablaOpcionesVuelos, "salida");
                    imprimirTabla(tablaOpcionesVuelos, vuelos);
                    System.out.println("VUELO ESCOGIDO PAQUETE " + paq.getIdPaquete() + ": " + vueloEscogido);
                    System.out.println("CIUDAD ACTUAL PAQUETE " + vuelos.get(vueloEscogido).getDestino());

                    // Si ya llegamos al destino, salimos del while || si ya nos quedamos sin tiempo
                    // para seguir buscando (creo que en Costo no hay manera de incluir este param)
                    // Comparar el destino del ultimo vuelo tomado con el destino de su envio
                    String destinoVueloElegido = vuelos.get(vueloEscogido).getDestino();
                    String destinoFinalPaquete = envios.get(paq.getCodigoEnvio()).getDestino();
                    if (destinoVueloElegido.equals(destinoFinalPaquete)) {
                        // Estos tiempo se deben calcular para así tener el t que toma todo su viaje
                        // Si no llegamos al destino por quedarnos sin tiempo (2dias o 1 dia), salimos

                        exito++;
                        System.out.println("El paquete " + paq.getIdPaquete() + " llegó al destino");
                        break;
                    } else {

                        System.out.println("El paquete " + paq.getIdPaquete() + " aun no llega al destino");
                    }

                    // if(i==5) break; //hasta que se quede sin tiempo para buscar su destino. Por
                    // ahora maximo visitará 5 aeropuertos
                    // i++;
                }
            }

            // Actualizar mi tabla (feromonas). Aumentar si ha llegado al destino. Restar o
            // no hacer nada si no ha llegado

            // Limpiar los vuelos tomados por el paquete
            iteracionAux++;
        }

        generarArchivoTabla(tabla, "salida");
        System.out.println("Numero de éxitos / numero paquetes: " + exito + " / " + paquetes.size());
        // for (Paquete p : paquetes) {
        // System.out.println(envios.get(p.getIdEnvío()).getDestino() + " " +
        // p.getIdPaquete());
        // }

    }

    public static void run_v2(HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos,
            HashMap<String, Envio> envios,
            ArrayList<Paquete> paquetes, int numeroIteraciones) {
        // Definir una matriz que defina Vuelo, Costo, Visibilidad() y Fermonas
        // El costo será dinámico para algunas variables: tiempo de vuelo (entre mismas
        // ciudades varia el t de vuelo), capacidades,
        // plazos de entrega,

        HashMap<Integer, Double[]> tabla = new HashMap<>(); // 4 columnas: Costo, Visibilidad, Feromonas
        HashMap<Integer, ProgramacionVuelo> vuelosProgramados = new HashMap<>();
        ArrayList<LocalDate> fechasVuelos = new ArrayList<>();
        // Dado que el costo (y por tanto la visibilidad) se definirá en la iteración,
        // entonces esta tabla maestra solo guardará las feromonas. (Duda)
        minYMaxTiempoVuelo = Normalizacion.obtenerMinMaxTiempoVuelo(vuelos);
        minYMaxDistanciaAeropuertos = Normalizacion.obtenerDistanciaExtrema(aeropuertos); // el mínimo no tiene sentido.
                                                                                          // Es 0
        minYMaxDistanciaAeropuertos[0] = 0;
        minYMaxTiempoVuelo[0] = 0;
        // System.out.println("Min tiempo de vuelo: " + minYMaxTiempoVuelo[0]); //129
        // System.out.println("Max tiempo de vuelo: " + minYMaxTiempoVuelo[1]); //890
        // System.out.println("Min distancia entre aeropuertos: " +
        // minYMaxDistanciaAeropuertos[0]); //0km
        // System.out.println("Max distancia entre aeropuertos: " +
        // minYMaxDistanciaAeropuertos[1]); //13463 km

        // esto se llenará de manera dinámica conforme se necesiten. Y no serán vuelos,
        // sino que serán ProgramacionVuelo
        /*
         * for (int id : vuelos.keySet()) {
         * tabla.put(id, new Double[] { (double) vuelos.get(id).getCapacidad(), 0.0, 0.1
         * }); // inicializar matrices.
         * // Los costos serán
         * // dinámicos, por eso será
         * // definido en las
         * // iteraciones
         * 
         * }
         */
        System.out.println("Numero de paquetes: " + paquetes.size());

        // generarArchivoTabla(tabla, "salida");
        // Iteraremos muchas veces para todos los paquetes. Es decir, para cada
        // iteración se tomarán en cuenta todos los paquettes
        int iteracionAux = 1, exito = 0;

        while (iteracionAux <= numeroIteraciones) {

            for (int id : tabla.keySet()) { // Esto es para inicializar las capacidades de los vuelos en cada iteración
                // double costo = costo(vuelos.get(id), );
                tabla.get(id)[1] = tabla.get(id)[0];// en la 2da columna de mi tabla guardaré la capacidad dinámica,
                // mientra q en la 1ra guardaré la capacidad máxima del vuelo
            }

            for (Paquete paq : paquetes) {
                int i=0;
                // imprimirTabla_v2(tabla, vuelosProgramados,vuelos);

                String ciudadActualPaquete;
                ZonedDateTime fechaActualPaquete;
                while (true) {
                    HashMap<Integer, Double[]> tablaOpcionesVuelos = new HashMap<>();
                    // FIltrar y llenar una tabla con todos los vuelos que salen del origen del
                    // paquete para ver posibles salidas
                    // O su ultima ubicación
                    // validar que no vuelva a una ciudad ya visitada
                    if (paq.getRuta().isEmpty()) {
                        // de acuerdo a su aeropuerto de origen
                        ciudadActualPaquete = envios.get(paq.getCodigoEnvio()).getOrigen();
                        fechaActualPaquete = envios.get(paq.getCodigoEnvio()).getFechaHoraSalida();
                    } else {
                        // de acuerdo al aeropuerto destino de su último vuelo
                        ArrayList<Integer> rutaPaquete = paq.getRuta();
                        int idUltimoVuelo = rutaPaquete.get(rutaPaquete.size() - 1);
                        
                        fechaActualPaquete = paq.getFechaLlegadaUltimoVuelo();
                        ciudadActualPaquete = vuelos.get(vuelosProgramados.get(idUltimoVuelo).getIdVuelo())
                                .getDestino();
                    }
                    
                    // A partir de la ciudad actual, llenaremos la tabla de los vuelos que puede
                    // tomar
                    agregarVuelosRequeridos(fechaActualPaquete, tabla, vuelosProgramados, vuelos, fechasVuelos, aeropuertos);

                    for (int id : tabla.keySet()) {
                        String ciudadOrigenVuelo = vuelos.get(vuelosProgramados.get(id).getIdVuelo()).getOrigen();
                        if (ciudadActualPaquete.equals(ciudadOrigenVuelo) && tabla.get(id)[1] > 0) { // que el vuelo
                                                                                                     // tenga espacio
                                                                                                     // aún
                            // Compararemos que la fecha sea posterior
                            if (fechaActualPaquete.compareTo(vuelosProgramados.get(id).getFechaHoraSalida()) <= 0) {
                                // comparar que date del paquete actual (fecha de su ultima ciudad) con
                                // la fecha del vuelo (horas en Vuelo y date en Programacion vuelo)


                                //long tHastaSalidaVuelo = aco_auxiliares.calcularDiferenciaEnMinutos(fechaActualPaquete, vuelosProgramados.get(id).getFechaHoraSalida());
                                //long tVuelo = aco_auxiliares.calcularDiferenciaEnMinutos(vuelosProgramados.get(id).getFechaHoraSalida(), vuelosProgramados.get(id).getFechaHoraLlegada());
                                //Duration tiempoAGastar = Duration.ofMinutes(tHastaSalidaVuelo + tVuelo);
                                long tiempoAGastar = aco_auxiliares.calcularDiferenciaEnMinutos(fechaActualPaquete, vuelosProgramados.get(id).getFechaHoraLlegada());
                                if((paq.getTiempoRestanteDinamico().toMinutes() - tiempoAGastar) >= 0){
                                    //que el tiempo desde que toma un vuelo hasta que llegue al destino sea menor que el tiempo que le queda restante
                                    System.out.println(paq.getTiempoRestanteDinamico().toMinutes() + "   " + tiempoAGastar);
                                    tablaOpcionesVuelos.put(id, new Double[4]); // guardaremos costo, visibilidad,
                                }

                                                                            // visibilidad*fermonoas y probabilidad
                            }

                        }
                    }
                    // Si no hay vuelos disponibles para el paquete, significa que nos quedamos sin tiempo
                    if(tablaOpcionesVuelos.size() == 0){
                        System.out.println();
                        System.out.println("El paquete " + paq.getIdPaquete() + " NO HA LLEGADO A SU DESTINO");
                        break;
                    }
                    
                    // Definir costo de cada vuelo, visibilidad
                    for (int id : tablaOpcionesVuelos.keySet()) {
                        tablaOpcionesVuelos.get(id)[0] = costo(fechaActualPaquete, vuelosProgramados.get(id),tabla.get(id), paq,
                                envios, aeropuertos, vuelos);
                        tablaOpcionesVuelos.get(id)[1] = 1 / tablaOpcionesVuelos.get(id)[0];
                        tablaOpcionesVuelos.get(id)[2] = tablaOpcionesVuelos.get(id)[1] * tabla.get(id)[2];
                    }
                    // Definir la probabilidad
                    double sumaDeProductoVisiXFeromonas = 0.0;
                    for (int id : tablaOpcionesVuelos.keySet()) {
                        sumaDeProductoVisiXFeromonas += tablaOpcionesVuelos.get(id)[2];
                    }
                    for (int id : tablaOpcionesVuelos.keySet()) {
                        tablaOpcionesVuelos.get(id)[3] = tablaOpcionesVuelos.get(id)[2] / sumaDeProductoVisiXFeromonas;
                    }

                    // Escoger un vuelo al azar
                    double[] probabilidades = new double[tablaOpcionesVuelos.size()];
                    int[] vuelosAux = new int[tablaOpcionesVuelos.size()];
                    int index = 0;
                    for (Map.Entry<Integer, Double[]> entry : tablaOpcionesVuelos.entrySet()) {
                        vuelosAux[index] = entry.getKey(); // Guardar la clave en vuelosAux
                        probabilidades[index] = entry.getValue()[3];
                        index++;
                    }
                    int posVueloEscogido = aco_auxiliares.determinarVueloEscogido(probabilidades);
                    int vueloEscogido = vuelosAux[posVueloEscogido];
                    // Registrar el vuelo elegido por el paquete
                    paq.getRuta().add(vueloEscogido);

                    paq.getFechasRuta().add(vuelosProgramados.get(vueloEscogido).getFechaHoraLlegada());
                    //Tiempo usado por el paquete en el vuelo
                    long tHastaSalidaVuelo = aco_auxiliares.calcularDiferenciaEnMinutos(fechaActualPaquete, vuelosProgramados.get(vueloEscogido).getFechaHoraSalida());
                    long tVuelo = aco_auxiliares.calcularDiferenciaEnMinutos(vuelosProgramados.get(vueloEscogido).getFechaHoraSalida(), vuelosProgramados.get(vueloEscogido).getFechaHoraLlegada());
                    Duration tiempoGastado = Duration.ofMinutes(tHastaSalidaVuelo + tVuelo);
                    paq.setTiempoRestanteDinamico(paq.getTiempoRestanteDinamico().minus(tiempoGastado));

                    // quitar un slot al vuelo
                    tabla.get(vueloEscogido)[1]--; // ¿Qué pasaría si ya no hay vuelos por tomar?
                    // Creo que eso no va a pasar

                    System.out.println("                IMPRIMIENDO TABLA DE OPCIONES PARA EL PAQUETE "
                            + paq.getIdPaquete() + " " + envios.get(paq.getCodigoEnvio()).getOrigen() + " "
                            + envios.get(paq.getCodigoEnvio()).getDestino() + "   Hora actual: " + fechaActualPaquete);

                    // generarArchivoTabla(tablaOpcionesVuelos, "salida");
                    imprimirTabla_v2(tablaOpcionesVuelos, vuelosProgramados, vuelos);
                    System.out.println("VUELO ESCOGIDO PAQUETE " + paq.getIdPaquete() + ": " + vueloEscogido);
                    System.out.println("CIUDAD ACTUAL PAQUETE "
                    + vuelos.get(vuelosProgramados.get(vueloEscogido).getIdVuelo()).getDestino());
                    
                    System.out.println("FECHA ACTUAL PAQUETE " + paq.getIdPaquete() + ": " + paq.getFechaLlegadaUltimoVuelo());
                            
                    // Si ya llegamos al destino, salimos del while || si ya nos quedamos sin tiempo
                    // para seguir buscando (creo que en Costo no hay manera de incluir este param)
                    // Comparar el destino del ultimo vuelo tomado con el destino de su envio
                    String destinoVueloElegido = vuelos.get(vuelosProgramados.get(vueloEscogido).getIdVuelo())
                            .getDestino();
                    String destinoFinalPaquete = envios.get(paq.getCodigoEnvio()).getDestino();
                    if (destinoVueloElegido.equals(destinoFinalPaquete)) {
                        // Estos tiempo se deben calcular para así tener el t que toma todo su viaje
                        // Si no llegamos al destino por quedarnos sin tiempo (2dias o 1 dia), salimos

                        exito++;
                        System.out.println("El paquete " + paq.getIdPaquete() + " llegó al destino");
                        System.out.println("Tiempo restante paquete " + paq.getTiempoRestanteDinamico().toMinutes());
                        break;
                    } else {

                        System.out.println("El paquete " + paq.getIdPaquete() + " aun no llega al destino");
                        System.out.println("Tiempo restante paquete " + paq.getTiempoRestanteDinamico().toMinutes());

                    }

                    //if(i==5) break; //hasta que se quede sin tiempo para buscar su destino. Por ahora maximo visitará 5 aeropuertos
                    //i++;
                }
                //break;
            }
            // imprimirTabla_v2(tabla, vuelosProgramados,vuelos);
            // Actualizar mi tabla (feromonas). Aumentar si ha llegado al destino. Restar o
            // no hacer nada si no ha llegado

            // Limpiar los vuelos tomados por el paquete
            iteracionAux++;
        }

        generarArchivoTabla(tabla, "salida");
        System.out.println("Numero de éxitos / numero paquetes: " + exito + " / " + paquetes.size());

    }

    public static void agregarVuelosRequeridos(ZonedDateTime fechaPaquete, HashMap<Integer, Double[]> tabla,
            HashMap<Integer, ProgramacionVuelo> vuelosProgramados, HashMap<Integer, Vuelo> vuelos,
            ArrayList<LocalDate> fechasVuelos, HashMap<String, Aeropuerto> aeropuertos) {
        // Agregaremos a tabla todos los vuelos para la fecha del paquete. Esto ayudará
        // que sea dinámico los vuelos que se estarán programanado
        // conforme aumentan la cantidad de paquetes para la simulación
        LocalDate ld1 = fechaPaquete.toLocalDate();
        LocalDate ld2 = fechaPaquete.toLocalDate().plusDays(1);
        LocalDate ld3 = fechaPaquete.toLocalDate().plusDays(2);

        int numeroVuelos = tabla.size();
        if (!fechasVuelos.contains(ld1)) {
            for (int idVuelo : vuelos.keySet()) {
                LocalTime horaSalida = vuelos.get(idVuelo).getFechaHoraSalida().toLocalTime();
                ZoneId origenZoneId = aeropuertos.get(vuelos.get(idVuelo).getOrigen()).getZoneId();
                ZonedDateTime fechaHoraSalida = ZonedDateTime.of(ld1, horaSalida, origenZoneId);

                // Calcularemos los minutos de vuelo
                LocalTime horaLlegada = vuelos.get(idVuelo).getFechaHoraLlegada().toLocalTime();
                ZoneId destinoZoneId = aeropuertos.get(vuelos.get(idVuelo).getDestino()).getZoneId();
                LocalTime horaSalidaEnZoneIdDestino = fechaHoraSalida.withZoneSameInstant(destinoZoneId).toLocalTime();
                Duration duracionVuelo = Duration.between(horaSalidaEnZoneIdDestino, horaLlegada);
                int duracionMinutos;
                if (duracionVuelo.isNegative()) {
                    duracionMinutos = 1440 + (int) duracionVuelo.toMinutes();
                } else {
                    duracionMinutos = (int) duracionVuelo.toMinutes();
                }

                numeroVuelos++;
                //
                ZonedDateTime fechaHoraLlegada = fechaHoraSalida.withZoneSameInstant(destinoZoneId)
                        .plusMinutes(duracionMinutos);

                // System.out.println(fechaHoraSalida + " " + fechaHoraLlegada);

                ProgramacionVuelo pv = new ProgramacionVuelo(numeroVuelos, idVuelo, fechaHoraSalida, fechaHoraLlegada);
                // tabla: guardará para cada vuelo su información
                tabla.put(numeroVuelos, new Double[] { (double) vuelos.get(pv.getIdVuelo()).getCapacidad(),
                        (double) vuelos.get(pv.getIdVuelo()).getCapacidad(), 0.1 });
                vuelosProgramados.put(numeroVuelos, pv);
            }

            fechasVuelos.add(ld1);
        }
        if (!fechasVuelos.contains(ld2)) {
            for (int idVuelo : vuelos.keySet()) {
                LocalTime horaSalida = vuelos.get(idVuelo).getFechaHoraSalida().toLocalTime();
                ZoneId origenZoneId = aeropuertos.get(vuelos.get(idVuelo).getOrigen()).getZoneId();
                ZonedDateTime fechaHoraSalida = ZonedDateTime.of(ld2, horaSalida, origenZoneId);

                // Calcularemos los minutos de vuelo
                LocalTime horaLlegada = vuelos.get(idVuelo).getFechaHoraLlegada().toLocalTime();
                ZoneId destinoZoneId = aeropuertos.get(vuelos.get(idVuelo).getDestino()).getZoneId();
                LocalTime horaSalidaEnZoneIdDestino = fechaHoraSalida.withZoneSameInstant(destinoZoneId).toLocalTime();
                Duration duracionVuelo = Duration.between(horaSalidaEnZoneIdDestino, horaLlegada);
                int duracionMinutos;
                if (duracionVuelo.isNegative()) {
                    duracionMinutos = 1440 + (int) duracionVuelo.toMinutes();
                } else {
                    duracionMinutos = (int) duracionVuelo.toMinutes();
                }

                // System.out.println(fechaHoraSalida + " " + horaLlegada + " "+ destinoZoneId);

                numeroVuelos++;
                //
                ZonedDateTime fechaHoraLlegada = fechaHoraSalida.withZoneSameInstant(destinoZoneId)
                        .plusMinutes(duracionMinutos);

                ProgramacionVuelo pv = new ProgramacionVuelo(numeroVuelos, idVuelo, fechaHoraSalida, fechaHoraLlegada);
                // tabla: guardará para cada vuelo su información
                tabla.put(numeroVuelos, new Double[] { (double) vuelos.get(pv.getIdVuelo()).getCapacidad(),
                        (double) vuelos.get(pv.getIdVuelo()).getCapacidad(), 0.1 });
                vuelosProgramados.put(numeroVuelos, pv);
            }

            fechasVuelos.add(ld2);
        }
        if (!fechasVuelos.contains(ld3)) {
            for (int idVuelo : vuelos.keySet()) {
                LocalTime horaSalida = vuelos.get(idVuelo).getFechaHoraSalida().toLocalTime();
                ZoneId origenZoneId = aeropuertos.get(vuelos.get(idVuelo).getOrigen()).getZoneId();
                ZonedDateTime fechaHoraSalida = ZonedDateTime.of(ld3, horaSalida, origenZoneId);

                // Calcularemos los minutos de vuelo
                LocalTime horaLlegada = vuelos.get(idVuelo).getFechaHoraLlegada().toLocalTime();
                ZoneId destinoZoneId = aeropuertos.get(vuelos.get(idVuelo).getDestino()).getZoneId();
                LocalTime horaSalidaEnZoneIdDestino = fechaHoraSalida.withZoneSameInstant(destinoZoneId).toLocalTime();
                Duration duracionVuelo = Duration.between(horaSalidaEnZoneIdDestino, horaLlegada);
                int duracionMinutos;
                if (duracionVuelo.isNegative()) {
                    duracionMinutos = 1440 + (int) duracionVuelo.toMinutes();
                } else {
                    duracionMinutos = (int) duracionVuelo.toMinutes();
                }

                // System.out.println(fechaHoraSalida + " " + horaLlegada + " "+ destinoZoneId);

                numeroVuelos++;
                //
                ZonedDateTime fechaHoraLlegada = fechaHoraSalida.withZoneSameInstant(destinoZoneId)
                        .plusMinutes(duracionMinutos);

                ProgramacionVuelo pv = new ProgramacionVuelo(numeroVuelos, idVuelo, fechaHoraSalida, fechaHoraLlegada);
                // tabla: guardará para cada vuelo su información
                tabla.put(numeroVuelos, new Double[] { (double) vuelos.get(pv.getIdVuelo()).getCapacidad(),
                        (double) vuelos.get(pv.getIdVuelo()).getCapacidad(), 0.1 });
                vuelosProgramados.put(numeroVuelos, pv);
            }

            fechasVuelos.add(ld3);
        }

    }

    public static void imprimirTabla(HashMap<Integer, Double[]> tabla, HashMap<Integer, Vuelo> vuelos) {
        System.out.println("ID\tCosto\tVisibilidad\tFeromonas");

        // Iterar sobre cada vuelo en la tabla
        for (Map.Entry<Integer, Double[]> entry : tabla.entrySet()) {
            Integer id = entry.getKey();
            Double[] datos = entry.getValue();

            // Imprimir datos del vuelo con formato de 4 decimales
            System.out.print(id + "\t" + vuelos.get(id).getOrigen() + "\t" + vuelos.get(id).getDestino() + "\t");
            for (Double dato : datos) {
                System.out.printf("%.4f\t\t", dato);
            }
            System.out.println();
        }
    }

    public static void imprimirTabla_v2(HashMap<Integer, Double[]> tablaOpcionesVuelo,
            HashMap<Integer, ProgramacionVuelo> vuelosProgramados, HashMap<Integer, Vuelo> vuelos) {
        System.out.println("ID\tCosto\tVisibilidad\tFeromonas");

        // Iterar sobre cada vuelo en la tabla
        for (Map.Entry<Integer, Double[]> entry : tablaOpcionesVuelo.entrySet()) {
            Integer id = entry.getKey();
            Double[] datos = entry.getValue();

            // Imprimir datos del vuelo con formato de 4 decimales
            System.out.print(id + "\t" + vuelos.get(vuelosProgramados.get(id).getIdVuelo()).getOrigen() +
                    "\t" + vuelos.get(vuelosProgramados.get(id).getIdVuelo()).getDestino() + "   "
                    + vuelosProgramados.get(id).getFechaHoraSalida() + "  "
                    + vuelosProgramados.get(id).getFechaHoraLlegada()
                    + "\t");
            for (Double dato : datos) {
                System.out.printf("%.4f\t\t", dato);
            }
            System.out.println();
        }
    }

    public static void generarArchivoTabla(HashMap<Integer, Double[]> tabla, String nombreArchivo) {
        try (FileWriter writer = new FileWriter(nombreArchivo)) {
            // Escribir encabezado de la tabla en el archivo
            writer.write("ID\tCosto\tVisibilidad\tFeromonas\n");
            int suma = 0;
            // Iterar sobre cada vuelo en la tabla y escribir los datos en el archivo
            for (Map.Entry<Integer, Double[]> entry : tabla.entrySet()) {
                Integer id = entry.getKey();
                Double[] datos = entry.getValue();

                // Escribir datos del vuelo con formato de 4 decimales en el archivo
                writer.write(id + "\t\t\t");
                for (Double dato : datos) {
                    writer.write(String.format("%.4f\t\t", dato));
                }
                writer.write(String.format("%.4f\t\t", datos[0] - datos[1]));
                suma += datos[0] - datos[1];
                writer.write("\n");
            }

            System.out.println("Archivo generado correctamente: " + nombreArchivo);
            System.out.println("Archivo generado correctamente - suma de asientos ocupados en vuelos: " + suma);
        } catch (IOException e) {
            System.err.println("Error al generar el archivo: " + e.getMessage());
        }
    }

    public static double costo(ZonedDateTime fechaActualPaquete, ProgramacionVuelo vueloProgramado,
            Double[] tablaValores, Paquete paquete, HashMap<String, Envio> envios,
            HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos) {
        // Tabla de valores: capacidad actual en [1]

        // Inicialmente será el tiempo que le toma en ir a una próxima ciudad + la
        // distancia que le queda para llegar a la ciudad destino
        // Dado que son 2 magnitudes diferentes, debemos normalizar ambas variables.
        // Para ello debemos calcular el valor minimo y máximo que pueden tomar ambas
        // variables en su dominio

        // IMPORTANTE: ademá del tiempo de vuelo, creo que deberiamos añadirle el tiempo
        // en que el avión recién estará listo para partir, el tiempo que estará
        // esperando en el aero-
        // puerto (creo que esto es insignificante, no se debería tomar en cuenta)

        double tiempoVuelo = aco_auxiliares.calcularDiferenciaEnMinutos(fechaActualPaquete,
                vueloProgramado.getFechaHoraSalida()) +
                aco_auxiliares.calcularDiferenciaEnMinutos(vueloProgramado.getFechaHoraSalida(),
                        vueloProgramado.getFechaHoraLlegada());
        // hallar la distancia del destino del vuelo al destino del paquete
        String destinoVueloTomado = vuelos.get(vueloProgramado.getIdVuelo()).getDestino();
        String destinoFinalPaquete = envios.get(paquete.getCodigoEnvio()).getDestino();
        // hallaremos la distancia entre estos aeropuertos
        double distanciaAlDestinoFinal = Normalizacion.obtenerDistanciaEntreAeropuertos(aeropuertos, destinoVueloTomado,
                destinoFinalPaquete);

        double tiempoVueloNormalizado = Normalizacion.normalizarTiempoVuelo(tiempoVuelo, minYMaxTiempoVuelo[0],
                minYMaxTiempoVuelo[1]);
        double distanciaDestinoFinalNormalizado = Normalizacion.normalizarDistancia(distanciaAlDestinoFinal,
                minYMaxDistanciaAeropuertos[0], minYMaxDistanciaAeropuertos[1]);
        // System.out.println("VERIFICANDO: " + tiempoVueloNormalizado + " " +
        // distanciaDestinoFinalNormalizado);
        if (distanciaDestinoFinalNormalizado == 0) {
            return 1;
        }
        
        return (25 * tiempoVueloNormalizado + 75 * distanciaDestinoFinalNormalizado)*
                    (1 - (paquete.getTiempoRestanteDinamico().toMinutes()-tiempoVuelo)/paquete.getTiempoRestante().toMinutes());
                    //mientras más tiempo tenga, los caminos más largos 
    }
}
