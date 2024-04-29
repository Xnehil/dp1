package com.dp1.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Paquete;
import com.dp1.backend.models.Vuelo;
import com.dp1.backend.utils.ACO;
import com.dp1.backend.utils.Auxiliares;
import com.dp1.backend.utils.FuncionesLectura;
import com.dp1.backend.utils.MPAv2;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        // SpringApplication.run(BackendApplication.class, args);
        HashMap<String, Aeropuerto> aeropuertos = FuncionesLectura.leerAeropuertos("data/Aeropuerto.husos.v2.txt");
        HashMap<Integer, Vuelo> vuelos = FuncionesLectura.leerVuelos("data/planes_vuelo.v3.txt", aeropuertos);
        String rutaArchivos = "data/pack_enviado_";
        String[] ciudades = { "SKBO", "SEQM", "SUAA", "SCEL", "SABE", "EBCI", "EHAM", "WMKK", "VIDP", "ZBAA" };
        HashMap<String, Envio> envios = new HashMap<String, Envio>();
        for (int i = 0; i < ciudades.length; i++) {
            envios.putAll(FuncionesLectura.leerEnvios(rutaArchivos + ciudades[i] + ".txt", aeropuertos, 9));
        }
        ArrayList<Paquete> paquetes = new ArrayList<Paquete>();
        for (Envio e : envios.values()) {
            paquetes.addAll(e.getPaquetes());
        }
        System.out.println("Paquetes: " + paquetes.size());

        // ACO.run(aeropuertos, vuelos, envios, paquetes, 2);

        /* MPA */
        int tamanioSolucion = 5;
        // Initialize the owo
        int[] owo = new int[tamanioSolucion * paquetes.size()];
        try {
            FileWriter writer = new FileWriter("output/results_chiquito_"+LocalDate.now()+".csv");
        
            writer.write("Iteracion,Tiempo de ejecucion (ms),Paquetes entregados,Porcentaje de paquetes entregados,Paquetes no entregados\n");
            double promedio=0;
            int iteraciones=1;
            for (int i = 0; i < iteraciones; i++) {
                Long startTime = System.currentTimeMillis();
                owo=MPAv2.run(aeropuertos, vuelos, envios, paquetes, 500, 120, tamanioSolucion);
                long endTime = System.currentTimeMillis();
                long executionTime = endTime - startTime;
                int paquetesEntregados = Auxiliares.verificacionTotal(owo, aeropuertos, vuelos, envios, paquetes,
                        tamanioSolucion);
                int paquetesNoEntregados = paquetes.size() - paquetesEntregados;
                double porcentajeEntregados = (double) (paquetesEntregados * 100) / paquetes.size();
                promedio += porcentajeEntregados;
                String resultLine = String.format("%d,%d,%d,%.2f%%,%d\n", i, executionTime, paquetesEntregados,
                        porcentajeEntregados, paquetesNoEntregados);
                writer.write(resultLine);
            }
            promedio /= iteraciones;
            // writer.write("\nPromedio,,," + promedio + "%,\n");
            System.out.println("Promedio: " + promedio);
        
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Asignar rutas a los paquetes
        for (int i = 0; i < paquetes.size(); i++) {
            ArrayList<Integer> ruta = new ArrayList<Integer>();
            for (int j = i * tamanioSolucion; j < tamanioSolucion * (i + 1); j++) {
                ruta.add(owo[j]);
            }
            paquetes.get(i).setRuta(ruta);
        }

        ArrayList<Paquete> noEntregados = new ArrayList<Paquete>();
        for (Paquete p : paquetes) {
            if (Auxiliares.solucionValidav2(aeropuertos, vuelos, envios, p, false) == false) { //llamar a archivo
                noEntregados.add(p);
                //verificar si existe, creo, else append
                //analizar la tendencia de errores (espacio capacidad/vuelo, plazo, secuencia geograficA) 
            }
        }



        //Una solución
        int verPaquete=0;        
        //System.out.println("L solucion es valida: " + esSolucionValida);
        System.out.println("Funcion validación:  ");
        Paquete auxPaquete = noEntregados.get(verPaquete);
        Boolean esSolucionValida = Auxiliares.solucionValidav2(aeropuertos, vuelos, envios, auxPaquete, true);
        System.out.println("La solución es valida: " + esSolucionValida);
         
    }

}
