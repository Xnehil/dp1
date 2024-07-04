package com.dp1.backend.services;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Paquete;
import com.dp1.backend.repository.EnvioRepository;

import jakarta.transaction.Transactional;

@Service
public class EnvioService {
    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private DatosEnMemoriaService datosEnMemoriaService;

    @Autowired
    private PaqueteService paqueteService;

    private final static Logger logger = LogManager.getLogger(EnvioService.class);

    public String createEnvio(Envio envio)
    {
        try {
            Aeropuerto origen = datosEnMemoriaService.getAeropuertos().get(envio.getOrigen());
            Aeropuerto destino = datosEnMemoriaService.getAeropuertos().get(envio.getDestino());
            //Agregar fecha de salida considerando la hora actual y la diferencia horaria
            ZonedDateTime fechaHoraSalida = ZonedDateTime.now();
            fechaHoraSalida=fechaHoraSalida.withZoneSameInstant(origen.getZoneId());

            Boolean mismoContinente = origen.getContinente().equals(destino.getContinente());
            //Agregar fecha de llegada prevista considerando la hora de salida y la distancia entre los aeropuertos
            ZonedDateTime fechaHoraLlegadaPrevista = fechaHoraSalida.plusDays(mismoContinente ? 1 : 2);
            fechaHoraLlegadaPrevista=fechaHoraLlegadaPrevista.withZoneSameLocal(destino.getZoneId());

            envio.setFechaHoraSalida(fechaHoraSalida);
            envio.setFechaHoraLlegadaPrevista(fechaHoraLlegadaPrevista);
            logger.info("Todo bien hasta fechas. Guardando envio");
            for(Paquete paq: envio.getPaquetes())
                System.out.println("Codigo: " + paq.getId() + " "  + paq.getIdPaquete());
            logger.info(envio.toString());
            envio = envioRepository.save(envio);
            logger.info("Todo bien hasta primer guardado");
            envio.setCodigoEnvio(envio.getOrigen()+envio.getId());
            envio.setPaquetes(null);
            envioRepository.save(envio);
            logger.info("Todo bien hasta segundo guardado");

            String codigosPaquetes = "";
            for (int i = 0; i < envio.getCantidadPaquetes(); i++) {
                //Guardar paquetes
                Paquete paquete = new Paquete();
                paquete.setCodigoEnvio(envio.getCodigoEnvio());
                int codigoPaquete = 1000000*origen.getIdAeropuerto() + 100*envio.getId() + (i+1) ;
                paquete.setIdPaquete(codigoPaquete);
                codigosPaquetes += codigoPaquete + " ";
                paquete.setCostosRuta(null);
                paquete.setFechasRuta(null);
                paquete.setRuta(null);
                paqueteService.createPaquete(paquete);
            }
            logger.info("Todo bien hasta guardado de paquetes");
            return codigosPaquetes;
        } catch (Exception e) {
            logger.error("Error al crear envio: ");
            e.printStackTrace();
            return e.getMessage();  
        }
    }

    public Envio getEnvio(int id)
    {
        try {
            return envioRepository.findById(id).get();
        } catch (Exception e) {
            return null;
        }
    }

    public Envio updateEnvio(Envio envio){
        try {
            if (envio == null)
            {
                return null;
            }
            return envioRepository.save(envio);
        } catch (Exception e) {
            System.out.println("Excepcion: " + e.getMessage());
            return null;
        }
    }

    public String deleteEnvio(int id){
        try {
            Envio envio = envioRepository.findById(id).get();
            if (envio != null) {
                envioRepository.delete(envio);
            }
            else {
                return "Envio no encontrado";
            }
            return "Envio eliminado";
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }

    public ArrayList<Envio> getEnvios()
    {
        return (ArrayList<Envio>) envioRepository.findAll();
    }

    public List<Envio> getMostRecentEnvios(int limit) {
        return envioRepository.findTopByOrderByFechaHoraSalidaDesc(PageRequest.of(0, limit));
    }

    public List<Envio> getEnviosBeforeDate(ZonedDateTime limitDate) {
        return envioRepository.findByFechaHoraSalidaBefore(limitDate);
    }

    public List<Envio> getEnviosAfterDate(ZonedDateTime limitDate) {
        return envioRepository.findByFechaHoraSalidaAfter(limitDate);
    }

    @Transactional
    public HashMap<String, Envio> getEnviosEntre(ZonedDateTime fechaHoraInicio, ZonedDateTime fechaHoraFin)
    {
        HashMap<String, Envio> enviosEntre = new HashMap<>();
        List <Envio> envios = getEnviosAfterDate(fechaHoraInicio);
        // logger.info("Envios despues de fecha inicio");
        for (Envio envio : envios) {
            // logger.info(envio.getFechaHoraSalida());
            // logger.info(fechaHoraInicio);
            if (envio.getFechaHoraSalida().isAfter(fechaHoraInicio) && envio.getFechaHoraSalida().isBefore(fechaHoraFin)) {
                List<Paquete> paquetes = paqueteService.getPaquetesByCodigoEnvio(envio.getCodigoEnvio());
                envio.setPaquetes(paquetes);
                enviosEntre.put(envio.getCodigoEnvio(), envio);
            }
        }
        return enviosEntre;
    }
}
