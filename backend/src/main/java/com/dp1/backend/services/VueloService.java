package com.dp1.backend.services;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Vuelo;
import com.dp1.backend.repository.VueloRepository;
import com.dp1.backend.utils.FuncionesLectura;

@Service
public class VueloService {
    private AeropuertoService aeropuertoService;
    private VueloRepository vueloRepository;

    private static final Logger logger = LogManager.getLogger(VueloService.class);

    @Autowired
    public VueloService(VueloRepository vueloRepository, AeropuertoService aeropuertoService) {
        this.vueloRepository = vueloRepository;
        this.aeropuertoService = aeropuertoService;
    }

    public Vuelo createVuelo(Vuelo vuelo) {
        try {
            return vueloRepository.save(vuelo);
        } catch (Exception e) {
            return null;
        }
    }

    public Vuelo getVuelo(int id) {
        try {
            return vueloRepository.findById(id).get();
        } catch (Exception e) {
            return null;
        }
    }

    public Vuelo updateVuelo(Vuelo vuelo) {
        try {
            if (vuelo == null) {
                return null;
            }
            return vueloRepository.save(vuelo);
        } catch (Exception e) {
            return null;
        }
    }

    public String deleteVuelo(int id) {
        try {
            Vuelo vuelo = vueloRepository.findById(id).get();
            if (vuelo != null) {
                vueloRepository.delete(vuelo);
            } else {
                return "Vuelo no encontrado";
            }
            return "Vuelo eliminado";
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }

    public ArrayList<Vuelo> getVuelos() {
        try {
            return (ArrayList<Vuelo>) vueloRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<Vuelo> getVuelosEnElAire() {
        try {
            ArrayList<Vuelo> vuelos = (ArrayList<Vuelo>) vueloRepository.findAll();
            // Fetch aeropuertos para obtener las zona horarias
            ArrayList<Aeropuerto> aeropuertos = aeropuertoService.getAeropuertos();
            HashMap<String, ZoneId> zonasHorarias = new HashMap<String, ZoneId>();
            for (Aeropuerto aeropuerto : aeropuertos) {
                zonasHorarias.put(aeropuerto.getCodigoOACI(), aeropuerto.getZoneId());
                // System.out.println("Aeropuerto: " + aeropuerto.getCodigoOACI() + " Zona
                // horaria: " + aeropuerto.getZoneId());
            }
            // Conservar solo los que hayan despegado y no hayan aterrizado
            ArrayList<Vuelo> vuelosEnElAire = new ArrayList<Vuelo>();
            for (Vuelo vuelo : vuelos) {
                ZonedDateTime horaActual = ZonedDateTime.now();
                ZoneId zonaOrigen = zonasHorarias.get(vuelo.getOrigen());
                ZoneId zonaDestino = zonasHorarias.get(vuelo.getDestino());

                if (zonaOrigen != null && zonaDestino != null) {
                    ZonedDateTime horaDespegue = ZonedDateTime.now().withZoneSameInstant(zonaOrigen);
                    ZonedDateTime horaAterrizaje = ZonedDateTime.now().withZoneSameInstant(zonaDestino);

                    horaDespegue = horaDespegue.with(vuelo.getFechaHoraSalida().toLocalTime());
                    horaAterrizaje = horaAterrizaje.with(vuelo.getFechaHoraLlegada().toLocalTime());

                    if (horaActual.isAfter(horaDespegue) && horaActual.isBefore(horaAterrizaje)) {
                        vuelosEnElAire.add(vuelo);
                    }
                } else {
                    System.out.println("Error: No se encontró la zona horaria para el origen o destino del vuelo "
                            + vuelo.getId()+ " Origen: " + vuelo.getOrigen() + " Destino: " + vuelo.getDestino());
                }
            }
            // System.out.println("Vuelos en el aire: " + vuelosEnElAire.size());
            // System.out.println("Vuelos totales: " + vuelos.size());
            return vuelosEnElAire;
        } catch (Exception e) {
            System.out.println("Error: " + e.getLocalizedMessage());
            return null;
        }
    }

    public int guardarVuelosArchivo() {
        HashMap<Integer, Vuelo> vuelos = new HashMap<Integer, Vuelo>();
        // Leer vuelos de un archivo
        HashMap<String, Aeropuerto> aeropuertos = FuncionesLectura.leerAeropuertos("data/Aeropuerto.husos.v2.txt");
        vuelos = FuncionesLectura.leerVuelos("data/planes_vuelo.v3.txt", aeropuertos);
        try {
            vueloRepository.saveAll(vuelos.values());
            logger.info("Vuelos guardados en la base de datos: " + vuelos.size());
            return vuelos.size();
        } catch (Exception e) {
            logger.error("Error en guardarVuelosArchivo: " + e.getMessage());
        }
        return 0;
    }
}
