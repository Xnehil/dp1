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

import ch.qos.logback.core.util.Duration;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        // SpringApplication.run(BackendApplication.class, args);
        HashMap<String, Aeropuerto> aeropuertos = FuncionesLectura.leerAeropuertos("data/Aeropuerto.husos.v2.txt");
        HashMap<Integer, Vuelo> vuelos = FuncionesLectura.leerVuelos("data/planes_vuelo.v3.txt", aeropuertos);
        String rutaArchivos = "data/pack_enviado_";
        String[] ciudades = { "SKBO", "SEQM", "SUAA", "SCEL", "SABE", "EBCI", "EHAM", "WMKK", "VIDP", "ZBAA"};
        HashMap<String, Envio> envios = new HashMap<String, Envio>();
        for (int i = 0; i < ciudades.length; i++) {
            envios.putAll(FuncionesLectura.leerEnvios(rutaArchivos + ciudades[i] + ".txt", aeropuertos,9));
        }

        ArrayList<Paquete> paquetes = new ArrayList<Paquete>();
        for (Envio e : envios.values()) {
            paquetes.addAll(e.getPaquetes());
        }
        System.out.println("Envios: " + envios.size());
        System.out.println("Paquetes: " + paquetes.size());

        // ZoneId zoneId = ZoneId.of("GMT+1");
        // ZonedDateTime horaEnPeru = ZonedDateTime.now();
        // ZonedDateTime horachina = horaEnPeru.withZoneSameInstant(zoneId);
        // System.out.println("Hora en "+ zoneId + ": " + horachina);
        
        // for (Vuelo a : vuelos.values()) {
        //     ZonedDateTime fechaHora1 = a.getFechaHoraSalida();
        //     ZonedDateTime fechaHora2 = a.getFechaHoraLlegada();
        //     ZoneId zoneId = ZoneId.of("GMT-4");
        //     System.out.println(fechaHora1 +  "  " + fechaHora2);
        //     System.out.println(fechaHora1.getZone() +  "  " + fechaHora2.getZone());
        //     System.out.println(fechaHora1.withZoneSameInstant(zoneId));
            
        // }
        

        for(Aeropuerto a: aeropuertos.values()){
            //ZonedDateTime ahora = ZonedDateTime.now();
            //ZonedDateTime comparar = ZonedDateTime.now().plusHours(12);

            //System.out.println(ahora.withZoneSameInstant(a.getZonaHoraria().toZoneId()));
            //System.out.println(ahora.getZone());

            // System.out.println(a.getZonaHoraria().getID());
            // String gmt;
            // if(a.getGmt()>=0){
            //     gmt = "GMT+"+a.getGmt();
            // }else{
            //     gmt = "GMT"+ a.getGmt();
            // }
            // System.out.println(zdt.withZoneSameInstant(ZoneId.of(gmt)));
        }
        
        // for (Paquete paq : paquetes) {

        //     System.out.println(envios.get(paq.getCodigoEnvio()).getOrigen() + " " + envios.get(paq.getCodigoEnvio()).getDestino());
        //     System.out.println(envios.get(paq.getCodigoEnvio()).getFechaHoraSalida() + "  " + envios.get(paq.getCodigoEnvio()).getFechaHoraLlegadaPrevista());
        //     System.out.println(paq.getTiempoRestante().toMinutes());
        // }
        

        /*
        int i=0;
        for (Paquete paq : paquetes) {
            i++;
            String formattedIndex = String.format("%4d", i); // Alineación a la derecha, ancho del campo de 4 caracteres
            String formattedId = String.format("%10s", paq.getIdPaquete()); // Alineación a la derecha, ancho del campo de 10 caracteres
            //System.out.println("Paquete " + formattedIndex + ": " + formattedId);
            //System.out.println("Envio del paquete: " + envios.get(paq.getCodigoEnvio()).getOrigen() + " " +  envios.get(paq.getCodigoEnvio()).getDestino());
            
            ZonedDateTime fechaSalida = envios.get(paq.getCodigoEnvio()).getFechaHoraSalida();
            System.out.println(fechaSalida);
            System.out.println("Envio del paquete: "  + fechaSalida.getYear() + fechaSalida.getMonthValue() + fechaSalida.getDayOfMonth());
        }
          */

        ACO.run_v2(aeropuertos, vuelos, envios, paquetes, 10);

        /* MPA 
        int tamanioSolucion = 5;
        // Initialize the owo
        int[] owo = new int[tamanioSolucion * paquetes.size()];
        try {
            FileWriter writer = new FileWriter("output/results_chiquito_"+LocalDate.now()+".csv");
        
            writer.write("Iteracion,Tiempo de ejecucion (ms),Paquetes entregados,Porcentaje de paquetes entregados,Paquetes no entregados\n");
            double promedio=0;
            int iteraciones=30;
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
            if (Auxiliares.solucionValidav2(aeropuertos, vuelos, envios, p, false) == false) {
                noEntregados.add(p);
            }
        }

        //Una solución
        int verPaquete=0;        
        //System.out.println("L solucion es valida: " + esSolucionValida);
        System.out.println("Funcion validación:  ");
        Paquete auxPaquete = noEntregados.get(verPaquete);
        Boolean esSolucionValida = Auxiliares.solucionValidav2(aeropuertos, vuelos, envios, auxPaquete, true);
        System.out.println("La solución es valida: " + esSolucionValida);
         */
    }
    
}
