package com.dp1.backend.services;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Vuelo;
import com.dp1.backend.utils.FuncionesLectura;

@Service
public class DatosEnMemoriaService {
    private HashMap<String, Aeropuerto> aeropuertos = new HashMap<>();
    private HashMap<Integer, Vuelo> vuelos = new HashMap<>();

    public DatosEnMemoriaService() {
        aeropuertos.putAll(FuncionesLectura.leerAeropuertos("data/Aeropuerto.husos.v2.txt"));
        vuelos.putAll(FuncionesLectura.leerVuelos("data/planes_vuelo.v3.txt",aeropuertos));
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

            if (horaActual.isAfter(horaDespegue) && horaActual.isBefore(horaAterrizaje)) {
                vuelosEnElAire.add(vuelo);
            }
        }
        return vuelosEnElAire;
    } catch (Exception e) {
        System.out.println("Error: " + e.getLocalizedMessage());
        return null;
    }
}
    
}
