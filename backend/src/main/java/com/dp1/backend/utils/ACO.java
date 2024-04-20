package com.dp1.backend.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.dp1.backend.models.*;

public class ACO {
    public static double[] minYMaxTiempoVuelo;
    public static double[] minYMaxDistanciaAeropuertos;

    public static void run(HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos,
            HashMap<Integer, Envio> envios,
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
        // System.out.println("Min tiempo de vuelo: " + minYMaxTiempoVuelo[0]); //129
        // min
        // System.out.println("Max tiempo de vuelo: " + minYMaxTiempoVuelo[1]); //890
        // min
        // System.out.println("Min distancia entre aeropuertos: " +
        // minYMaxDistanciaAeropuertos[0]); //0km
        // System.out.println("Max distancia entre aeropuertos: " +
        // minYMaxDistanciaAeropuertos[1]); //13463 km

        for (int id : vuelos.keySet()) {
            String origen = vuelos.get(id).getOrigen();
            String destino = vuelos.get(id).getDestino();
            // double costo = costo(vuelos.get(id), );
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
        int iteracionAux = 1;
        while (iteracionAux <= numeroIteraciones) {

            for (int id : vuelos.keySet()) { // Esto es para inicializar las capacidades de los vuelos en cada iteración
                // double costo = costo(vuelos.get(id), );
                tabla.get(id)[1] = tabla.get(id)[0];// en la 2da columna de mi tabla guardaré la capacidad dinámica,
                                                    // mientra q en la 1ra guardaré la capacidad máxima del vuelo
            }

            for (Paquete paq : paquetes) {

                String ciudadActualPaquete;
                while (true) {
                    HashMap<Integer, Double[]> tablaOpcionesVuelos = new HashMap<>();
                    // FIltrar y llenar una tabla con todos los vuelos que salen del origen del
                    // paquete para ver posibles salidas
                    // O su ultima ubicación
                    // validar que no vuelva a una ciudad ya visitada
                    if (paq.getRuta().isEmpty()) {
                        // de acuerdo a su aeropuerto de origen
                        ciudadActualPaquete = envios.get(paq.getIdEnvío()).getOrigen();
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
                        if (ciudadActualPaquete.equals(ciudadOrigenVuelo) && tabla.get(id)[1] > 0) { // la 2da condición
                                                                                                     // es que aún quede
                            tablaOpcionesVuelos.put(id, new Double[4]); // guardaremos costo, visibilidad,
                                                                        // visibilidad*fermonoas, probabilidad
                        }
                    }
                    // Definir costo de cada vuelo, visibilidad
                    for (int id : tablaOpcionesVuelos.keySet()) {
                        tablaOpcionesVuelos.get(id)[0] = costo(vuelos.get(id), paq, envios, aeropuertos);
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

                    //
                    // System.out.println("Vuelos disponibles para paquete " + paq.getIdPaquete() +
                    //         " " + envios.get(paq.getIdEnvío()).getOrigen() + " " +
                    //         envios.get(paq.getIdEnvío()).getDestino());
                    // for (int idVuelo : tablaOpcionesVuelos.keySet()) {
                    //     System.out.println("idVuelo " + idVuelo + " origen: " +
                    //             vuelos.get(idVuelo).getOrigen() + " destino: " +
                    //             vuelos.get(idVuelo).getDestino());
                    // }
                    //

                    double sumaProb = 0.0;
                    for (int id : tablaOpcionesVuelos.keySet()) {
                        sumaProb += tablaOpcionesVuelos.get(id)[3];
                    }
                    System.out.println("Suma de probabilidades: " + sumaProb);
                    System.out.println("                IMPRIMIENDO TABLA DE OPCIONES PARA EL PAQUETE "
                    + paq.getIdPaquete() + " " + envios.get(paq.getIdEnvío()).getOrigen() + " "
                    + envios.get(paq.getIdEnvío()).getDestino());
                    
                    // generarArchivoTabla(tablaOpcionesVuelos, "salida");
                    imprimirTabla(tablaOpcionesVuelos, vuelos);
                    System.out.println("VUELO ESCOGIDO PAQUETE " + paq.getIdPaquete() + ": " + vueloEscogido);
                    break;
                    // Registrar el vuelo elegido por el paquete
                    // quitar un slot al vuelo

                    // Si ya llegamos al destino, salimos del while

                    // Si no llegamos al destino por quedarnos sin tiempo (2dias o 1 dia), salimos
                }

                if (paq.getIdPaquete() == 5)
                    break;
            }

            // Actualizar mi tabla (feromonas). Aumentar si ha llegado al destino. Restar o
            // no hacer nada si no ha llegado

            // Limpiar los vuelos tomados por el paquete
            iteracionAux++;
        }

        // generarArchivoTabla(tabla, "salida");

        // for (Paquete p : paquetes) {
        // System.out.println(envios.get(p.getIdEnvío()).getDestino() + " " +
        // p.getIdPaquete());
        // }

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

    public static void generarArchivoTabla(HashMap<Integer, Double[]> tabla, String nombreArchivo) {
        try (FileWriter writer = new FileWriter(nombreArchivo)) {
            // Escribir encabezado de la tabla en el archivo
            writer.write("ID\tCosto\tVisibilidad\tFeromonas\n");

            // Iterar sobre cada vuelo en la tabla y escribir los datos en el archivo
            for (Map.Entry<Integer, Double[]> entry : tabla.entrySet()) {
                Integer id = entry.getKey();
                Double[] datos = entry.getValue();

                // Escribir datos del vuelo con formato de 4 decimales en el archivo
                writer.write(id + "\t\t\t");
                for (Double dato : datos) {
                    writer.write(String.format("%.4f\t\t", dato));
                }
                writer.write("\n");
            }

            System.out.println("Archivo generado correctamente: " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al generar el archivo: " + e.getMessage());
        }
    }

    public static double costo(Vuelo vuelo, Paquete paquete, HashMap<Integer, Envio> envios,
            HashMap<String, Aeropuerto> aeropuertos) {
        // Inicialmente será el tiempo que le toma en ir a una próxima ciudad + la
        // distancia que le queda para llegar a la ciudad destino
        // Dado que son 2 magnitudes diferentes, debemos normalizar ambas variables.
        // Para ello debemos calcular el valor minimo y máximo que pueden tomar ambas
        // variables en su dominio

        double tiempoVuelo = vuelo.calcularMinutosDeVuelo();
        // hallar la distancia del destino del vuelo al destino del paquete
        String destinoVueloTomado = vuelo.getDestino();
        String destinoFinalPaquete = envios.get(paquete.getIdEnvío()).getDestino();
        // hallaremos la distancia entre estos aeropuertos
        double distanciaAlDestinoFinal = Normalizacion.obtenerDistanciaEntreAeropuertos(aeropuertos, destinoVueloTomado,
                destinoFinalPaquete);
        
        double tiempoVueloNormalizado = Normalizacion.normalizarTiempoVuelo(tiempoVuelo, minYMaxTiempoVuelo[0],
                minYMaxTiempoVuelo[1]);
        double distanciaDestinoFinalNormalizado = Normalizacion.normalizarDistancia(distanciaAlDestinoFinal,
                minYMaxDistanciaAeropuertos[0], minYMaxDistanciaAeropuertos[1]);
        System.out.println("VERIFICANDO: " + tiempoVueloNormalizado + " " + distanciaDestinoFinalNormalizado);
        return 100 * (tiempoVueloNormalizado + distanciaDestinoFinalNormalizado);
    }
}
