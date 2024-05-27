package com.dp1.backend.services;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Vuelo;
import com.dp1.backend.utils.FuncionesLectura;

@Service
public class DatosEnMemoriaService {
    private HashMap<String, Aeropuerto> aeropuertos = new HashMap<>();
    private HashMap<Integer, Vuelo> vuelos = new HashMap<>();
    private HashMap<String, ArrayList<Integer>> salidasPorHora = new HashMap<>();
    private final static Logger logger = LogManager.getLogger(DatosEnMemoriaService.class);
    private String workingDirectory = System.getProperty("user.dir");

    public DatosEnMemoriaService() {
        logger.info("Inicializando DatosEnMemoriaService con working directory: " + workingDirectory);
        if(workingDirectory.trim().equals("/")){
            workingDirectory = "/home/inf226.982.2b/";
        } 
        else{
            workingDirectory = "";
        }

        aeropuertos.putAll(FuncionesLectura.leerAeropuertos(workingDirectory+"data/Aeropuerto.husos.v2.txt"));
        vuelos.putAll(FuncionesLectura.leerVuelos(workingDirectory+"data/planes_vuelo.v3.txt",aeropuertos));
        for (Vuelo vuelo : vuelos.values()) {
            ZonedDateTime horaDespegue = vuelo.getFechaHoraSalida();
            horaDespegue = horaDespegue.withZoneSameInstant(ZoneId.of("GMT-5"));
            String cadenaIndex = horaDespegue.getHour() + ":" + horaDespegue.getMinute();
            if (salidasPorHora.containsKey(cadenaIndex)) {
                salidasPorHora.get(cadenaIndex).add(vuelo.getId());
            } else {
                ArrayList<Integer> vuelosEnHora = new ArrayList<>();
                vuelosEnHora.add(vuelo.getId());
                salidasPorHora.put(cadenaIndex, vuelosEnHora);
            }
        }
    }


    public HashMap<String,ArrayList<Integer>> getSalidasPorHora() {
        return this.salidasPorHora;
    }

    public void setSalidasPorHora(HashMap<String,ArrayList<Integer>> salidasPorHora) {
        this.salidasPorHora = salidasPorHora;
    }



    public Map<String,Aeropuerto> getAeropuertos() {
        return this.aeropuertos;
    }

    public void setAeropuertos(HashMap<String,Aeropuerto> aeropuertosVuelosMap) {
        this.aeropuertos = aeropuertosVuelosMap;
    }

    public Map<Integer,Vuelo> getVuelos() {
        return this.vuelos;
    }

    public void setVuelos(HashMap<Integer,Vuelo> vuelosMap) {
        this.vuelos = vuelosMap;
    }

    public ArrayList<Vuelo> getVuelosEnElAire(ZonedDateTime horaActual) {
    try {
        // Keep only flights that have taken off and have not landed
        ArrayList<Vuelo> vuelosEnElAire = new ArrayList<Vuelo>();
        for (Vuelo vuelo : vuelos.values()) {
            ZonedDateTime horaDespegue = vuelo.getFechaHoraSalida();
            ZonedDateTime horaAterrizaje = vuelo.getFechaHoraLlegada();

            horaDespegue = horaDespegue.with(horaActual.toLocalDate());
            horaAterrizaje = horaAterrizaje.with(horaActual.toLocalDate());
            if(vuelo.getCambioDeDia())
            {
                horaAterrizaje = horaAterrizaje.plusDays(1);
            }
            if (horaActual.isAfter(horaDespegue) && horaActual.isBefore(horaAterrizaje)) {
                // logger.info("Hora despegue: " + horaDespegue+ " zona horaria: "+horaDespegue.getZone());
                // logger.info("Hora aterrizaje: " + horaAterrizaje+ " zona horaria: "+horaAterrizaje.getZone());
                // logger.info("Hora actual: " + horaActual+ " zona horaria: "+horaActual.getZone());
                // logger.info("Decisión: Vuelo N°" + vuelo.getId() + " en el aire");
                vuelosEnElAire.add(vuelo);
            }
        }
        // logger.info("Vuelos en el aire: " + vuelosEnElAire.size());
        return vuelosEnElAire;
        } catch (Exception e) {
            System.out.println("Error: " + e.getLocalizedMessage());
            return null;
        }
    }

    public HashMap<Integer,Vuelo> getVuelosEnElAireMap(ZonedDateTime horaActual) {
        try {
            // Keep only flights that have taken off and have not landed
            HashMap<Integer,Vuelo> vuelosEnElAire = new HashMap<Integer,Vuelo>();
            for (Vuelo vuelo : vuelos.values()) {
                ZonedDateTime horaDespegue = vuelo.getFechaHoraSalida();
                ZonedDateTime horaAterrizaje = vuelo.getFechaHoraLlegada();

                horaDespegue = horaDespegue.with(horaActual.toLocalDate());
                horaAterrizaje = horaAterrizaje.with(horaActual.toLocalDate());
                if(vuelo.getCambioDeDia())
                {
                    horaAterrizaje = horaAterrizaje.plusDays(1);
                }
    
                if (horaActual.isAfter(horaDespegue) && horaActual.isBefore(horaAterrizaje)) {
                    // logger.info("Hora despegue: " + horaDespegue+ " zona horaria: "+horaDespegue.getZone());
                    // logger.info("Hora aterrizaje: " + horaAterrizaje+ " zona horaria: "+horaAterrizaje.getZone());
                    // logger.info("Hora actual: " + horaActual+ " zona horaria: "+horaActual.getZone());
                    // logger.info("Decisión: Vuelo N°" + vuelo.getId() + " en el aire");
                    vuelosEnElAire.put(vuelo.getId(),vuelo);
                }
            }
            // logger.info("Vuelos en el aire: " + vuelosEnElAire.size());
            return vuelosEnElAire;
        } catch (Exception e) {
            System.out.println("Error: " + e.getLocalizedMessage());
            return null;
        }
    }

    
}