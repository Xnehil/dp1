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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Paquete;
import com.dp1.backend.models.ProgramacionVuelo;
import com.dp1.backend.models.Vuelo;
import com.dp1.backend.services.PaqueteService;

@Component
public class ACO {

    @Autowired
    PaqueteService paqueteService;

    public static double[] minYMaxTiempoVuelo;
    public static double[] minYMaxDistanciaAeropuertos;

    public void run(HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos,
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

    public ArrayList<Paquete> run_v2(HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos,
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

        System.out.println("Número de paquetes: " + paquetes.size());

        // generarArchivoTabla(tabla, "salida");
        // Iteraremos muchas veces para todos los paquetes. Es decir, para cada
        // iteración se tomarán en cuenta todos los paquettes
        int exito = 0;
        // Limpiar rutas de los paquetes
        for (Paquete paq : paquetes) {
            paq.setFechasRuta(new ArrayList<ZonedDateTime>());
            paq.setRuta(new ArrayList<Integer>());
            paq.setTiempoRestanteDinamico(paq.getTiempoRestante());
            paq.setLlegoDestino(false);
        }
        // Limpiar numero de exitos (luego lo cambiaremos porque cada paq guarda una
        // variable si ha llegado a su destino)
        exito = 0;
        // Empezamos
        int numPaqEjecutados = 0;
        for (Paquete paq : paquetes) {
            numPaqEjecutados++;
            // if(numPaqEjecutados % 50 == 0)
            // System.out.println("Paquete número: " + numPaqEjecutados + ". Fecha: " +
            // envios.get(paq.getCodigoEnvio()).getFechaHoraSalida());

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
                    // System.out.println("Paquete " + paq.getCodigoEnvio());
                    ciudadActualPaquete = envios.get(paq.getCodigoEnvio()).getOrigen();
                    fechaActualPaquete = envios.get(paq.getCodigoEnvio()).getFechaHoraSalida();
                    // System.out.println("Fecha de salida: " + fechaActualPaquete);
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
                agregarVuelosRequeridos(fechaActualPaquete, tabla, vuelosProgramados, vuelos, fechasVuelos,
                        aeropuertos);

                // ***Imprimieros las fechas de los vuelos que se están generando */
                // System.out.println("Fechas necesarias para generar los vuelos, donde se
                // agregan 1600 aprox cada vez");
                // for (LocalDate ld : fechasVuelos) {
                // System.out.println("Fecha: " + ld);
                // }
                // System.out.println("Numero de vuelos programados creados: " +
                // vuelosProgramados.size());
                // System.out.println("Numero de filas en la tabla: " + tabla.size());

                for (int id : tabla.keySet()) {
                    Vuelo vuelo = vuelos.get(vuelosProgramados.get(id).getIdVuelo());
                    String ciudadOrigenVuelo = vuelo.getOrigen();
                    Boolean espacioVuelo = vuelosProgramados.get(id).getCargaActualPlanificacion() + 1 < vuelo
                            .getCapacidad();
                    if (ciudadActualPaquete.equals(ciudadOrigenVuelo) && espacioVuelo) { // que el vuelo tenga espacio
                                                                                         // aún
                        // Compararemos que la fecha sea posterior
                        if (fechaActualPaquete.isBefore(vuelosProgramados.get(id).getFechaHoraSalida())) {
                            // Recordar que en vuelosProgramados ya están los 3 días en los que el vuelo
                            // puede salir

                            // Verificar que haya espacio en el aeropuerto de destino

                            Aeropuerto aDestino = aeropuertos.get(vuelo.getDestino());
                            Boolean quedaEspacioAlmacen = aDestino.paquetesAEstaHoraPlanificacion(
                                    fechaActualPaquete.toLocalDateTime()) + 1 < aDestino.getCapacidadMaxima();

                            if (!quedaEspacioAlmacen) {
                                // System.out.println("No queda espacio." + aDestino.getCapacidadMaxima() + " "
                                // + aDestino.paquetesAEstaHoraPlanificacion(
                                // fechaActualPaquete.toLocalDateTime()));
                                continue;
                            } else {

                                // System.out.println("Sí queda espacio");
                            }

                            // long tHastaSalidaVuelo =
                            // aco_auxiliares.calcularDiferenciaEnMinutos(fechaActualPaquete,
                            // vuelosProgramados.get(id).getFechaHoraSalida());
                            // long tVuelo =
                            // aco_auxiliares.calcularDiferenciaEnMinutos(vuelosProgramados.get(id).getFechaHoraSalida(),
                            // vuelosProgramados.get(id).getFechaHoraLlegada());
                            // Duration tiempoAGastar = Duration.ofMinutes(tHastaSalidaVuelo + tVuelo);
                            long tiempoAGastar = aco_auxiliares.calcularDiferenciaEnMinutos(fechaActualPaquete,
                                    vuelosProgramados.get(id).getFechaHoraLlegada());
                            if ((paq.getTiempoRestanteDinamico().toMinutes() - tiempoAGastar) >= 0) {
                                // que el tiempo desde que toma un vuelo hasta que llegue al destino sea menor
                                // que el tiempo que le queda restante
                                // System.out.println(paq.getTiempoRestanteDinamico().toMinutes() + " " +
                                // tiempoAGastar);
                                tablaOpcionesVuelos.put(id, new Double[4]); // guardaremos costo, visibilidad,
                            }

                            // visibilidad*fermonoas y probabilidad
                        }

                    }
                }
                // Si no hay vuelos disponibles para el paquete, significa que nos quedamos sin
                // tiempo
                if (tablaOpcionesVuelos.size() == 0) {
                    // if(envios.get(paq.getCodigoEnvio()).getOrigen().equals("VIDP")){
                    // System.out.println();
                    // System.out.println("El paquete " + paq.getIdPaquete() + " NO HA LLEGADO A SU
                    // DESTINO");
                    // System.out.println();
                    // }
                    break;
                }

                // Definir costo de cada vuelo, visibilidad
                for (int id : tablaOpcionesVuelos.keySet()) {
                    tablaOpcionesVuelos.get(id)[0] = costo(fechaActualPaquete, vuelosProgramados.get(id),
                            tabla.get(id), paq,
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
                int idVueloEscogido = vuelosAux[posVueloEscogido];
                Vuelo vueloEscogido = vuelos.get(vuelosProgramados.get(idVueloEscogido).getIdVuelo());
                // Registrar el vuelo elegido por el paquete
                paq.getRuta().add(vuelosProgramados.get(idVueloEscogido).getIdVuelo());
                paq.getFechasRuta().add(vuelosProgramados.get(idVueloEscogido).getFechaHoraLlegada());
                paq.getcostosRuta().add(tablaOpcionesVuelos.get(idVueloEscogido)[0]);

                // Actualizar capacidad planificación del almacén destino y origen
                Aeropuerto aDestino = aeropuertos.get(vueloEscogido.getDestino());
                aDestino.paqueteEntraPlanificacion(
                        vuelosProgramados.get(idVueloEscogido).getFechaHoraLlegada().toLocalDateTime());
                Aeropuerto aOrigen = aeropuertos.get(vueloEscogido.getOrigen());
                aOrigen.paqueteSalePlanificacion(
                        vuelosProgramados.get(idVueloEscogido).getFechaHoraSalida().toLocalDateTime());

                // Tiempo usado por el paquete en el vuelo
                long tHastaSalidaVuelo = aco_auxiliares.calcularDiferenciaEnMinutos(fechaActualPaquete,
                        vuelosProgramados.get(idVueloEscogido).getFechaHoraSalida());
                long tVuelo = aco_auxiliares.calcularDiferenciaEnMinutos(
                        vuelosProgramados.get(idVueloEscogido).getFechaHoraSalida(),
                        vuelosProgramados.get(idVueloEscogido).getFechaHoraLlegada());
                Duration tiempoGastado = Duration.ofMinutes(tHastaSalidaVuelo + tVuelo);
                paq.setTiempoRestanteDinamico(paq.getTiempoRestanteDinamico().minus(tiempoGastado));

                // quitar un slot al vuelo
                // tabla.get(idVueloEscogido)[1] = tabla.get(idVueloEscogido)[1] - 1; // ¿Qué
                // pasaría si ya no hay vuelos por tomar?

                // Actualizar capacidad planificación del vuelo
                vuelosProgramados.get(idVueloEscogido).setCargaActualPlanificacion(
                        vuelosProgramados.get(idVueloEscogido).getCargaActualPlanificacion() + 1);
                // Creo que eso no va a pasar
                // if(envios.get(paq.getCodigoEnvio()).getOrigen().equals("VIDP")){

                // System.out.println(" IMPRIMIENDO TABLA DE OPCIONES PARA EL PAQUETE "
                // + paq.getIdPaquete() + " " + envios.get(paq.getCodigoEnvio()).getOrigen() + "
                // "
                // + envios.get(paq.getCodigoEnvio()).getDestino() + " Hora actual: " +
                // fechaActualPaquete);

                // // generarArchivoTabla(tablaOpcionesVuelos, "salida");
                // imprimirTabla_v2(tablaOpcionesVuelos, vuelosProgramados, vuelos);
                // System.out.println("VUELO ESCOGIDO PAQUETE " + paq.getIdPaquete() + ": " +
                // vueloEscogido);
                // System.out.println("CIUDAD ACTUAL PAQUETE "
                // +
                // vuelos.get(vuelosProgramados.get(vueloEscogido.getId()).getIdVuelo()).getDestino());

                // System.out.println(
                // "FECHA ACTUAL PAQUETE " + paq.getIdPaquete() + ": " +
                // paq.getFechaLlegadaUltimoVuelo());
                // }

                // Si ya llegamos al destino, salimos del while || si ya nos quedamos sin tiempo
                // para seguir buscando (creo que en Costo no hay manera de incluir este param)
                // Comparar el destino del ultimo vuelo tomado con el destino de su envio
                String destinoVueloElegido = vuelos.get(vuelosProgramados.get(idVueloEscogido).getIdVuelo())
                        .getDestino();
                String destinoFinalPaquete = envios.get(paq.getCodigoEnvio()).getDestino();
                if (destinoVueloElegido.equals(destinoFinalPaquete)) {
                    // Estos tiempo se deben calcular para así tener el t que toma todo su viaje
                    // Si no llegamos al destino por quedarnos sin tiempo (2dias o 1 dia), salimos
                    // System.out.println("Exito ++");
                    exito++;
                    // if(envios.get(paq.getCodigoEnvio()).getOrigen().equals("VIDP")){

                    // paq.setLlegoDestino(true);
                    // System.out.println("El paquete " + paq.getIdPaquete() + " llegó al destino");
                    // System.out.println(
                    // "Tiempo restante paquete " + paq.getTiempoRestanteDinamico().toMinutes() + "
                    // minutos");
                    // }
                    break;
                } else {
                    // if(envios.get(paq.getCodigoEnvio()).getOrigen().equals("VIDP")){

                    // System.out.println("El paquete " + paq.getIdPaquete() + " aun no llega al
                    // destino");
                    // System.out.println(
                    // "Tiempo restante paquete " + paq.getTiempoRestanteDinamico().toMinutes() + "
                    // minutos");
                    // }

                }

                // if(i==5) break; //hasta que se quede sin tiempo para buscar su destino. Por
                // ahora maximo visitará 5 aeropuertos
                // i++;
            }
            // break;

        }
        // System.out.println("TABLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
        // " + iteracionAux);
        // imprimirTabla_v2(tabla, vuelosProgramados,vuelos);
        // Actualizar mi tabla (feromonas). Aumentar si ha llegado al destino. Restar o
        // no hacer nada si no ha llegado

        // generarArchivoTabla(tabla, "salida");
        System.out.println("Numero de éxitos / numero paquetes: " + exito + " / " + paquetes.size());
        
        return paquetes;
    }

    public ArrayList<Paquete> run_v3(HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos,
            HashMap<String, Envio> envios, ArrayList<Paquete> paquetes, int numeroIteraciones,
            HashMap<Integer, Double[]> tabla, HashMap<Integer, ProgramacionVuelo> vuelosProgramados,
            ArrayList<LocalDate> fechasVuelos) {
        // Definir una matriz que defina Vuelo, Costo, Visibilidad() y Fermonas
        // El costo será dinámico para algunas variables: tiempo de vuelo (entre mismas
        // ciudades varia el t de vuelo), capacidades,
        // plazos de entrega,

        // HashMap<Integer, Double[]> tabla = new HashMap<>(); // 4 columnas: Costo,
        // Visibilidad, Feromonas
        // HashMap<Integer, ProgramacionVuelo> vuelosProgramados = new HashMap<>();
        // ArrayList<LocalDate> fechasVuelos = new ArrayList<>();
        // Dado que el costo (y por tanto la visibilidad) se definirá en la iteración,
        // entonces esta tabla maestra solo guardará las feromonas. (Duda)
        minYMaxTiempoVuelo = Normalizacion.obtenerMinMaxTiempoVuelo(vuelos);
        minYMaxDistanciaAeropuertos = Normalizacion.obtenerDistanciaExtrema(aeropuertos); // el mínimo no tiene sentido.
                                                                                          // Es 0
        minYMaxDistanciaAeropuertos[0] = 0;
        minYMaxTiempoVuelo[0] = 0;

        System.out.println("Número de paquetes: " + paquetes.size());

        // generarArchivoTabla(tabla, "salida");
        // Iteraremos muchas veces para todos los paquetes. Es decir, para cada
        // iteración se tomarán en cuenta todos los paquettes
        int exito = 0;
        // Limpiar rutas de los paquetes
        for (Paquete paq : paquetes) {
            paq.setFechasRuta(new ArrayList<ZonedDateTime>());
            paq.setRuta(new ArrayList<Integer>());
            paq.setTiempoRestanteDinamico(paq.getTiempoRestante());
            paq.setLlegoDestino(false);
        }
        // Limpiar numero de exitos (luego lo cambiaremos porque cada paq guarda una
        // variable si ha llegado a su destino)
        exito = 0;
        // Empezamos
        int numPaqEjecutados = 0;
        for (Paquete paq : paquetes) {
            numPaqEjecutados++;
            if (numPaqEjecutados % 50 == 0)
                System.out.println("Paquete número: " + numPaqEjecutados + ". Fecha: "
                        + envios.get(paq.getCodigoEnvio()).getFechaHoraSalida());

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
                    // System.out.println("Paquete " + paq.getCodigoEnvio());
                    ciudadActualPaquete = envios.get(paq.getCodigoEnvio()).getOrigen();
                    fechaActualPaquete = envios.get(paq.getCodigoEnvio()).getFechaHoraSalida();
                    // System.out.println("Fecha de salida: " + fechaActualPaquete);
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
                agregarVuelosRequeridos(fechaActualPaquete, tabla, vuelosProgramados, vuelos, fechasVuelos,
                        aeropuertos);

                // ***Imprimieros las fechas de los vuelos que se están generando */
                // System.out.println("Fechas necesarias para generar los vuelos, donde se
                // agregan 1600 aprox cada vez");
                // for (LocalDate ld : fechasVuelos) {
                // System.out.println("Fecha: " + ld);
                // }
                // System.out.println("Numero de vuelos programados creados: " +
                // vuelosProgramados.size());
                // System.out.println("Numero de filas en la tabla: " + tabla.size());

                for (int id : tabla.keySet()) {
                    Vuelo vuelo = vuelos.get(vuelosProgramados.get(id).getIdVuelo());
                    String ciudadOrigenVuelo = vuelo.getOrigen();
                    Boolean espacioVuelo = vuelosProgramados.get(id).getCargaActualPlanificacion() + 1 < vuelo
                            .getCapacidad();
                    if (ciudadActualPaquete.equals(ciudadOrigenVuelo) && espacioVuelo) { // que el vuelo tenga espacio
                                                                                         // aún
                        // Compararemos que la fecha sea posterior
                        if (fechaActualPaquete.isBefore(vuelosProgramados.get(id).getFechaHoraSalida())) {
                            // Recordar que en vuelosProgramados ya están los 3 días en los que el vuelo
                            // puede salir

                            // Verificar que haya espacio en el aeropuerto de destino

                            Aeropuerto aDestino = aeropuertos.get(vuelo.getDestino());
                            Boolean quedaEspacioAlmacen = aDestino.paquetesAEstaHoraPlanificacion(
                                    fechaActualPaquete.toLocalDateTime()) + 1 < aDestino.getCapacidadMaxima();

                            if (!quedaEspacioAlmacen) {
                                // System.out.println("No queda espacio." + aDestino.getCapacidadMaxima() + " "
                                // + aDestino.paquetesAEstaHoraPlanificacion(
                                // fechaActualPaquete.toLocalDateTime()));
                                continue;
                            } else {

                                // System.out.println("Sí queda espacio");
                            }

                            // long tHastaSalidaVuelo =
                            // aco_auxiliares.calcularDiferenciaEnMinutos(fechaActualPaquete,
                            // vuelosProgramados.get(id).getFechaHoraSalida());
                            // long tVuelo =
                            // aco_auxiliares.calcularDiferenciaEnMinutos(vuelosProgramados.get(id).getFechaHoraSalida(),
                            // vuelosProgramados.get(id).getFechaHoraLlegada());
                            // Duration tiempoAGastar = Duration.ofMinutes(tHastaSalidaVuelo + tVuelo);
                            long tiempoAGastar = aco_auxiliares.calcularDiferenciaEnMinutos(fechaActualPaquete,
                                    vuelosProgramados.get(id).getFechaHoraLlegada());
                            if ((paq.getTiempoRestanteDinamico().toMinutes() - tiempoAGastar) >= 0) {
                                // que el tiempo desde que toma un vuelo hasta que llegue al destino sea menor
                                // que el tiempo que le queda restante
                                // System.out.println(paq.getTiempoRestanteDinamico().toMinutes() + " " +
                                // tiempoAGastar);
                                tablaOpcionesVuelos.put(id, new Double[4]); // guardaremos costo, visibilidad,
                            }

                            // visibilidad*fermonoas y probabilidad
                        }

                    }
                }
                // Si no hay vuelos disponibles para el paquete, significa que nos quedamos sin
                // tiempo
                if (tablaOpcionesVuelos.size() == 0) {
                    // if(envios.get(paq.getCodigoEnvio()).getOrigen().equals("VIDP")){
                    // System.out.println();
                    // System.out.println("El paquete " + paq.getIdPaquete() + " NO HA LLEGADO A SU
                    // DESTINO");
                    // System.out.println();
                    // }
                    break;
                }

                // Definir costo de cada vuelo, visibilidad
                for (int id : tablaOpcionesVuelos.keySet()) {
                    tablaOpcionesVuelos.get(id)[0] = costo(fechaActualPaquete, vuelosProgramados.get(id),
                            tabla.get(id), paq,
                            envios, aeropuertos, vuelos);
                    tablaOpcionesVuelos.get(id)[1] = 1 / tablaOpcionesVuelos.get(id)[0];
                    tablaOpcionesVuelos.get(id)[2] = tablaOpcionesVuelos.get(id)[1] * tabla.get(id)[2];// usamos las
                                                                                                       // feromonas de
                                                                                                       // los vuelos
                                                                                                       // programados.
                                                                                                       // Falta update
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
                int idVueloEscogido = vuelosAux[posVueloEscogido];
                Vuelo vueloEscogido = vuelos.get(vuelosProgramados.get(idVueloEscogido).getIdVuelo());
                // Registrar el vuelo elegido por el paquete
                paq.getRuta().add(vuelosProgramados.get(idVueloEscogido).getIdVuelo());
                paq.getFechasRuta().add(vuelosProgramados.get(idVueloEscogido).getFechaHoraLlegada());
                paq.getcostosRuta().add(tablaOpcionesVuelos.get(idVueloEscogido)[0]);

                // Actualizar capacidad planificación del almacén destino y origen
                Aeropuerto aDestino = aeropuertos.get(vueloEscogido.getDestino());
                aDestino.paqueteEntraPlanificacion(
                        vuelosProgramados.get(idVueloEscogido).getFechaHoraLlegada().toLocalDateTime());
                Aeropuerto aOrigen = aeropuertos.get(vueloEscogido.getOrigen());
                aOrigen.paqueteSalePlanificacion(
                        vuelosProgramados.get(idVueloEscogido).getFechaHoraSalida().toLocalDateTime());

                // Tiempo usado por el paquete en el vuelo
                long tHastaSalidaVuelo = aco_auxiliares.calcularDiferenciaEnMinutos(fechaActualPaquete,
                        vuelosProgramados.get(idVueloEscogido).getFechaHoraSalida());
                long tVuelo = aco_auxiliares.calcularDiferenciaEnMinutos(
                        vuelosProgramados.get(idVueloEscogido).getFechaHoraSalida(),
                        vuelosProgramados.get(idVueloEscogido).getFechaHoraLlegada());
                Duration tiempoGastado = Duration.ofMinutes(tHastaSalidaVuelo + tVuelo);
                paq.setTiempoRestanteDinamico(paq.getTiempoRestanteDinamico().minus(tiempoGastado));

                // quitar un slot al vuelo
                // tabla.get(idVueloEscogido)[1] = tabla.get(idVueloEscogido)[1] - 1; // ¿Qué
                // pasaría si ya no hay vuelos por tomar?

                // Actualizar capacidad planificación del vuelo
                vuelosProgramados.get(idVueloEscogido).setCargaActualPlanificacion(
                        vuelosProgramados.get(idVueloEscogido).getCargaActualPlanificacion() + 1);
                // Creo que eso no va a pasar
                // if(envios.get(paq.getCodigoEnvio()).getOrigen().equals("VIDP")){

                // System.out.println(" IMPRIMIENDO TABLA DE OPCIONES PARA EL PAQUETE "
                // + paq.getIdPaquete() + " " + envios.get(paq.getCodigoEnvio()).getOrigen() + "
                // "
                // + envios.get(paq.getCodigoEnvio()).getDestino() + " Hora actual: " +
                // fechaActualPaquete);

                // // generarArchivoTabla(tablaOpcionesVuelos, "salida");
                // imprimirTabla_v2(tablaOpcionesVuelos, vuelosProgramados, vuelos);
                // System.out.println("VUELO ESCOGIDO PAQUETE " + paq.getIdPaquete() + ": " +
                // vueloEscogido);
                // System.out.println("CIUDAD ACTUAL PAQUETE "
                // +
                // vuelos.get(vuelosProgramados.get(vueloEscogido.getId()).getIdVuelo()).getDestino());

                // System.out.println(
                // "FECHA ACTUAL PAQUETE " + paq.getIdPaquete() + ": " +
                // paq.getFechaLlegadaUltimoVuelo());
                // }

                // Si ya llegamos al destino, salimos del while || si ya nos quedamos sin tiempo
                // para seguir buscando (creo que en Costo no hay manera de incluir este param)
                // Comparar el destino del ultimo vuelo tomado con el destino de su envio
                String destinoVueloElegido = vuelos.get(vuelosProgramados.get(idVueloEscogido).getIdVuelo())
                        .getDestino();
                String destinoFinalPaquete = envios.get(paq.getCodigoEnvio()).getDestino();
                if (destinoVueloElegido.equals(destinoFinalPaquete)) {
                    // Estos tiempo se deben calcular para así tener el t que toma todo su viaje
                    // Si no llegamos al destino por quedarnos sin tiempo (2dias o 1 dia), salimos
                    // System.out.println("Exito ++");
                    exito++;
                    // if(envios.get(paq.getCodigoEnvio()).getOrigen().equals("VIDP")){

                    // paq.setLlegoDestino(true);
                    // System.out.println("El paquete " + paq.getIdPaquete() + " llegó al destino");
                    // System.out.println(
                    // "Tiempo restante paquete " + paq.getTiempoRestanteDinamico().toMinutes() + "
                    // minutos");
                    // }
                    break;
                } else {
                    // if(envios.get(paq.getCodigoEnvio()).getOrigen().equals("VIDP")){

                    // System.out.println("El paquete " + paq.getIdPaquete() + " aun no llega al
                    // destino");
                    // System.out.println(
                    // "Tiempo restante paquete " + paq.getTiempoRestanteDinamico().toMinutes() + "
                    // minutos");
                    // }

                }

                // if(i==5) break; //hasta que se quede sin tiempo para buscar su destino. Por
                // ahora maximo visitará 5 aeropuertos
                // i++;
            }
            // break;

        }
        // System.out.println("TABLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
        // " + iteracionAux);
        // imprimirTabla_v2(tabla, vuelosProgramados,vuelos);
        // Actualizar mi tabla (feromonas). Aumentar si ha llegado al destino. Restar o
        // no hacer nada si no ha llegado

        // generarArchivoTabla(tabla, "salida");
        System.out.println("Numero de éxitos / numero paquetes: " + exito + " / " + paquetes.size());
        return paquetes;
    }

    public static void agregarVuelosRequeridos(ZonedDateTime fechaPaquete, HashMap<Integer, Double[]> tabla,
            HashMap<Integer, ProgramacionVuelo> vuelosProgramados, HashMap<Integer, Vuelo> vuelos,
            ArrayList<LocalDate> fechasVuelos, HashMap<String, Aeropuerto> aeropuertos) {
        // Agregaremos a tabla todos los vuelos para la fecha del paquete. Esto ayudará
        // que sea dinámico los vuelos que se estarán programanado
        // conforme aumentan la cantidad de paquetes para la simulación
        int numeroVuelos = tabla.size();
        for (int i = 0; i < 3; i++) {
            LocalDate ld = fechaPaquete.toLocalDate().plusDays(i);
            if (!fechasVuelos.contains(ld)) {
                for (int idVuelo : vuelos.keySet()) {
                    Vuelo vuelo = vuelos.get(idVuelo);

                    ZonedDateTime fechaHoraSalida = vuelo.getFechaHoraSalida().with(ld);
                    numeroVuelos++;
                    ZonedDateTime fechaHoraLlegada = vuelo.getFechaHoraLlegada().with(ld);
                    fechaHoraLlegada = fechaHoraLlegada.plusDays(vuelo.getCambioDeDia()); // la variable "cambio de dia"
                                                                                          // me causa ruido.

                    ProgramacionVuelo pv = new ProgramacionVuelo(numeroVuelos, idVuelo, fechaHoraSalida,
                            fechaHoraLlegada);
                    // tabla: guardará para cada vuelo su información
                    tabla.put(numeroVuelos, new Double[] { (double) vuelos.get(pv.getIdVuelo()).getCapacidad(),
                            (double) vuelos.get(pv.getIdVuelo()).getCapacidad(), 0.1 });
                    vuelosProgramados.put(numeroVuelos, pv);
                }

                fechasVuelos.add(ld);
            }
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
        // if (distanciaDestinoFinalNormalizado == 0) {
        // return 1;
        // }

        // return (25 * tiempoVueloNormalizado + 75 * distanciaDestinoFinalNormalizado)
        // *
        // (1 - (paquete.getTiempoRestanteDinamico().toMinutes() - tiempoVuelo)
        // / paquete.getTiempoRestante().toMinutes());
        return 1 + 100000 * (tiempoVueloNormalizado * distanciaDestinoFinalNormalizado) * tablaValores[2]
                * (100 + ((tablaValores[0] - tablaValores[1]) / tablaValores[0])) / 100;
        // mientras más tiempo tenga, los caminos más largos
    }

    public static void actualizarFeromonas(HashMap<Integer, Double[]> tabla, ArrayList<Paquete> paquetes,
            double tasaEvaporacion, double aprendizaje) {
        // Actualizamos primero segun la tasa de evaporacion
        for (int idVuelo : tabla.keySet()) {
            tabla.get(idVuelo)[2] = tabla.get(idVuelo)[2] * (1 - tasaEvaporacion);
        }
        // Actualizamos segun si el paquete tomó el vuelo
        for (Paquete paq : paquetes) {
            if (paq.getLlegoDestino()) {
                for (int vueloTomado : paq.getRuta()) {
                    // Me parece que tengo que acumular el costo en cada decisión para así
                    // System.out.println(paq.costoTotalRuta());
                    tabla.get(vueloTomado)[2] = tabla.get(vueloTomado)[2] + aprendizaje / (paq.costoTotalRuta());
                }
            }
        }
        // ¿Haremos algo con la ruta si el paquete no llegó al destino? Fátima
        // Invertimos la cantidad de feromonas
        // double suma = 0;
        // for(int id : tabla.keySet()){
        // suma = suma + 1 / tabla.get(id)[2];
        // }

        // for(int id : tabla.keySet()){
        // tabla.get(id)[2] = (1 / tabla.get(id)[2]) / suma;
        // }
        // for(int id : tabla.keySet()){
        // tabla.get(id)[2] = 1 / tabla.get(id)[2];
        // }

    }
}