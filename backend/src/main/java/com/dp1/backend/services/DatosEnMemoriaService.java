package com.dp1.backend.services;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.ColeccionRuta;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Paquete;
import com.dp1.backend.models.RutaPosible;
import com.dp1.backend.models.Vuelo;
import com.dp1.backend.utils.FuncionesLectura;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class DatosEnMemoriaService {
    private HashMap<String, Aeropuerto> aeropuertos = new HashMap<>();
    private HashMap<Integer, Vuelo> vuelos = new HashMap<>();

    //Mapas para rutas
    private HashMap<String, ColeccionRuta> rutasPosibles = new HashMap<>();
    private HashSet<String> rutasPosiblesSet = new HashSet<>();


    public HashSet<String> getRutasPosiblesSet() {
        return this.rutasPosiblesSet;
    }

    public void setRutasPosiblesSet(HashSet<String> rutasPosiblesSet) {
        this.rutasPosiblesSet = rutasPosiblesSet;
    }

    public String getWorkingDirectory() {
        return this.workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public ColeccionRutaService getColeccionRutaService() {
        return this.coleccionRutaService;
    }

    public void setColeccionRutaService(ColeccionRutaService coleccionRutaService) {
        this.coleccionRutaService = coleccionRutaService;
    }


    private final static Logger logger = LogManager.getLogger(DatosEnMemoriaService.class);
    private String workingDirectory = System.getProperty("user.dir");

    @Autowired 
    private ColeccionRutaService coleccionRutaService;

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
            if(vuelo.getDistanciaVuelo()<0 || vuelo.getDuracionVuelo()<0){
                logger.info("Vuelo: " + vuelo.getId() + " Origen: " + vuelo.getOrigen() + " Destino: " + vuelo.getDestino() + " Fecha salida: " + vuelo.getFechaHoraSalida() + " Fecha llegada: " + vuelo.getFechaHoraLlegada() + " Duración: " + vuelo.getDuracionVuelo() + " Distancia: " + vuelo.getDistanciaVuelo());
            }
        }
    }

    @PostConstruct
    @Transactional
    public void init(){
        coleccionRutaService.getAllColeccionRutas().forEach(cr -> {
            rutasPosibles.put(cr.getCodigoRuta(), cr);
            String ruta = cr.getCodigoRuta();
            for (RutaPosible rp : cr.getRutasPosibles()) {
                String sucesionVuelos = "";
                for (Integer vueloId : rp.getFlights()) {
                    sucesionVuelos += ("-"+vueloId);
                }
                ruta += sucesionVuelos;
                if (!rutasPosiblesSet.contains(ruta)) {
                    rutasPosiblesSet.add(ruta);
                }
            }
        });

        logger.info("Rutas posibles: " + rutasPosibles.size());
        logger.info("Rutas posibles set: " + rutasPosiblesSet.size());
        // for (ColeccionRuta cr : rutasPosibles.values()) {
        //     logger.info("Ruta: " + cr.getCodigoRuta());
        //     for (RutaPosible rp : cr.getRutasPosibles()) {
        //         logger.info("Ruta posible: " + rp.getFlights().size());
        //         for (Integer vueloId : rp.getFlights()) {
        //             logger.info("Vuelo: " + vueloId);
        //         }
        //     }
        // }
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
            horaAterrizaje = horaAterrizaje.plusDays(vuelo.getCambioDeDia());
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
                horaAterrizaje = horaAterrizaje.plusDays(vuelo.getCambioDeDia());

    
                if (horaActual.isAfter(horaDespegue) && horaActual.isBefore(horaAterrizaje)) {
                    // logger.info("Hora despegue en hora local: " + horaDespegue.withZoneSameInstant(ZoneId.of("America/Lima")));
                    // logger.info("Hora aterrizaje en hora local: " + horaAterrizaje.withZoneSameInstant(ZoneId.of("America/Lima")));
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


    public HashMap<String,ColeccionRuta> getRutasPosibles() {
        return this.rutasPosibles;
    }

    public void setRutasPosibles(HashMap<String,ColeccionRuta> rutasPosibles) {
        this.rutasPosibles = rutasPosibles;
    }

    public boolean seTieneruta(String ruta){
        return rutasPosiblesSet.contains(ruta);
    }

    public void insertarRuta(Envio envio, Paquete paquete){
        String llave = envio.getOrigen()+envio.getDestino();
        ColeccionRuta cr = rutasPosibles.get(llave);
        if(cr == null){
            cr = new ColeccionRuta();
            cr.setCodigoRuta(llave);
            cr.setRutasPosibles(new ArrayList<RutaPosible>());
            RutaPosible rp = new RutaPosible();
            rp.setColeccionRuta(cr);
            rp.setFlights(paquete.getRuta());
            cr.getRutasPosibles().add(rp);
            rutasPosibles.put(llave, cr);
            // logger.info("Ruta creada: " + llave);
            //Guardar cr en bd
        }
        RutaPosible rp = new RutaPosible();
        rp.setColeccionRuta(cr);
        rp.setFlights(paquete.getRuta());
        cr.getRutasPosibles().add(rp);
        //Guardarías rp en bd


        String llave2 = envio.getOrigen()+envio.getDestino();
        for (int i: paquete.getRuta()) {
            llave2 += "-"+i;
        }
        rutasPosiblesSet.add(llave2);
        // logger.info("Ruta agregada en set: " + llave2);

        //
    }



    
}
