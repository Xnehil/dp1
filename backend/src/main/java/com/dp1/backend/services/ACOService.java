package com.dp1.backend.services;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Paquete;
import com.dp1.backend.models.Vuelo;
import com.dp1.backend.models.Ruta;
import com.dp1.backend.utils.ACO;
import com.dp1.backend.utils.Auxiliares;
import com.dp1.backend.utils.FuncionesLectura;
@Service
public class ACOService {
    private static final Logger logger = LogManager.getLogger(ACOService.class);
    private ArrayList<Paquete> paquetes = new ArrayList<Paquete>();

    public boolean ejecutarAco(ZonedDateTime horaActual) {
        paquetes.clear();

        HashMap<String, Aeropuerto> aeropuertos = new HashMap<String, Aeropuerto>();
        HashMap<Integer, Vuelo> vuelos = new HashMap<Integer, Vuelo>();
        HashMap<String, Envio> envios = new HashMap<String, Envio>();
        cargarDatosV2(aeropuertos, vuelos, envios, paquetes, horaActual);
        // Imprimir datos
        logger.info("Ejecutando ACO para: ");
        logger.info("Aeropuertos: " + aeropuertos.size());
        logger.info("Vuelos: " + vuelos.size());
        logger.info("Envios: " + envios.size());
        logger.info("Paquetes: " + paquetes.size());

        try {
            // Medit tiempo de ejecución
            Long startTime = System.currentTimeMillis();
            ACO.run_v2(aeropuertos, vuelos, envios, paquetes, 20);
            Long endTime = System.currentTimeMillis();
            Long totalTime = endTime - startTime;
            logger.info("Tiempo de ejecución: " + totalTime + " ms");
            int paquetesEntregados = Auxiliares.verificacionTotalPaquetes(aeropuertos, vuelos, envios, paquetes);
            logger.info("Paquetes entregados con función André: " + paquetesEntregados);

        } catch (Exception e) {
            logger.error("Error en ejecutarAco: " + e.getMessage());
            return false;
        }
        return true;

    }

    public boolean guardarRutas(){
        HashMap<String, Ruta> rutas = new HashMap<String, Ruta>();
        try{
            //To do fátima
        }
        catch(Exception e){
            logger.error("Error en guardarRutas: " + e.getMessage());
            return false;
        }
        return true;
    }

    private void cargarDatos(HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos,
            HashMap<String, Envio> envios, ArrayList<Paquete> paquetes) {
        // Ahora mismo está leyendo datos de archivos, pero debería leer de la base de
        // datos
        String workingDirectory = System.getProperty("user.dir");
        if (workingDirectory.trim().equals("/")) {
            workingDirectory = "/home/inf226.982.2b/";
        } else {
            workingDirectory = "";
        }
        aeropuertos.putAll(FuncionesLectura.leerAeropuertos(workingDirectory + "data/Aeropuerto.husos.v2.txt"));
        vuelos.putAll(FuncionesLectura.leerVuelos(workingDirectory + "data/planes_vuelo.v3.txt", aeropuertos));
        String rutaArchivos = "data/pack_enviado_";
        String[] ciudades = { "SKBO", "SEQM", "SUAA", "SCEL", "SABE", "EBCI", "EHAM", "WMKK", "VIDP", "ZBAA" };
        for (int i = 0; i < ciudades.length; i++) {
            envios.putAll(FuncionesLectura.leerEnvios(rutaArchivos + ciudades[i] + ".txt", aeropuertos, 20));
        }

        for (Envio e : envios.values()) {
            paquetes.addAll(e.getPaquetes());
        }
    }

    private void cargarDatosV2(HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos,
            HashMap<String, Envio> envios, ArrayList<Paquete> paquetes,
            ZonedDateTime horaActual) {
        // Ahora mismo está leyendo datos de archivos, pero debería leer de la base de
        // datos
        String workingDirectory = System.getProperty("user.dir");
        if (workingDirectory.trim().equals("/")) {
            workingDirectory = "/home/inf226.982.2b/";
        } else {
            workingDirectory = "";
        }
        aeropuertos.putAll(FuncionesLectura.leerAeropuertos(workingDirectory + "data/Aeropuerto.husos.v2.txt"));
        vuelos.putAll(FuncionesLectura.leerVuelos(workingDirectory + "data/planes_vuelo.v3.txt", aeropuertos));
        String rutaArchivos = "data/pack_enviado_";
        String[] ciudades = { "SKBO", "SEQM", "SUAA", "SCEL", "SABE", "EBCI", "EHAM", "WMKK", "VIDP", "ZBAA" };
        for (int i = 0; i < ciudades.length; i++) {
            envios.putAll(FuncionesLectura.leerEnvios(rutaArchivos + ciudades[i] + ".txt", aeropuertos, 20));
        }

        HashMap<String, Envio> enviosActual = new HashMap<>();
        
        for (Map.Entry<String, Envio> entry : envios.entrySet()) {
            Envio envio = entry.getValue();
            // Verificar si la hora del envío es posterior a la hora actual
            if (envio.getFechaHoraLlegadaPrevista().isAfter(horaActual)) {
                // Agregar al nuevo HashMap
                enviosActual.put(entry.getKey(), envio);
            }
        }

        for (Envio e : enviosActual.values()) {
            paquetes.addAll(e.getPaquetes());
        }
    }

}
