package com.dp1.backend.services;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Paquete;
import com.dp1.backend.models.Vuelo;
import com.dp1.backend.utils.ACO;
import com.dp1.backend.utils.FuncionesLectura;

@Service
public class ACOService {

    private ArrayList<Paquete> paquetes = new ArrayList<Paquete>();
    
    public boolean ejecutarAco(){
        paquetes.clear();

        HashMap<String, Aeropuerto> aeropuertos = new HashMap<String, Aeropuerto>();
        HashMap<Integer, Vuelo> vuelos = new HashMap<Integer, Vuelo>();
        HashMap<String, Envio> envios = new HashMap<String, Envio>();
        cargarDatos(aeropuertos, vuelos, envios, paquetes);
        //Imprimir datos
        System.out.println("Aeropuertos: " + aeropuertos.size());
        System.out.println("Vuelos: " + vuelos.size());
        System.out.println("Envios: " + envios.size());
        System.out.println("Paquetes: " + paquetes.size());
        try{
            //Medit tiempo de ejecución
            Long startTime = System.currentTimeMillis();
            ACO.run_v2(aeropuertos, vuelos, envios, paquetes, 20);
            Long endTime = System.currentTimeMillis();
            System.out.println("Tiempo de ejecución: " + (endTime - startTime) + " ms");
        }
        catch(Exception e){
            System.out.println("Error en ejecutarAco: " + e.getMessage());
            return false;
        }
        return true;

    }

    public boolean guardarRutas(){
        try{
            //To do fátima
        }
        catch(Exception e){
            System.out.println("Error en guardarRutas: " + e.getMessage());
            return false;
        }
        return true;
    }

    private void cargarDatos(HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos, HashMap<String, Envio> envios, ArrayList<Paquete> paquetes){
        //Ahora mismo está leyendo datos de archivos, pero debería leer de la base de datos
        aeropuertos.putAll(FuncionesLectura.leerAeropuertos("data/Aeropuerto.husos.v2.txt"));
        vuelos.putAll(FuncionesLectura.leerVuelos("data/planes_vuelo.v3.txt", aeropuertos));
        String rutaArchivos = "data/pack_enviado_";
        String[] ciudades = { "SKBO", "SEQM", "SUAA", "SCEL", "SABE", "EBCI", "EHAM", "WMKK", "VIDP", "ZBAA"};
        for (int i = 0; i < ciudades.length; i++) {
            envios.putAll(FuncionesLectura.leerEnvios(rutaArchivos + ciudades[i] + ".txt", aeropuertos,20));
        }
    
        for (Envio e : envios.values()) {
            paquetes.addAll(e.getPaquetes());
        }
    }
        

}
