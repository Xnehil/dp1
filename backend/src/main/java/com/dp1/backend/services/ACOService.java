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
    
    public boolean ejecutarAco(){
        HashMap<String, Aeropuerto> aeropuertos = new HashMap<String, Aeropuerto>();
        HashMap<Integer, Vuelo> vuelos = new HashMap<Integer, Vuelo>();
        HashMap<String, Envio> envios = new HashMap<String, Envio>();
        ArrayList<Paquete> paquetes = new ArrayList<Paquete>();
        cargarDatos(aeropuertos, vuelos, envios, paquetes);
        try{
            ACO.run_v2(aeropuertos, vuelos, envios, paquetes, 20);
        }
        catch(Exception e){
            return false;
        }
        return true;

    }

    private void cargarDatos(HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos, HashMap<String, Envio> envios, ArrayList<Paquete> paquetes){
        aeropuertos = FuncionesLectura.leerAeropuertos("data/Aeropuerto.husos.v2.txt");
        vuelos = FuncionesLectura.leerVuelos("data/planes_vuelo.v3.txt", aeropuertos);
        String rutaArchivos = "data/pack_enviado_";
        String[] ciudades = { "SKBO", "SEQM", "SUAA", "SCEL", "SABE", "EBCI", "EHAM", "WMKK", "VIDP", "ZBAA"};
        envios = new HashMap<String, Envio>();
        for (int i = 0; i < ciudades.length; i++) {
            envios.putAll(FuncionesLectura.leerEnvios(rutaArchivos + ciudades[i] + ".txt", aeropuertos,20));
        }

        paquetes = new ArrayList<Paquete>();
        for (Envio e : envios.values()) {
            paquetes.addAll(e.getPaquetes());
        }
    }
        

}
