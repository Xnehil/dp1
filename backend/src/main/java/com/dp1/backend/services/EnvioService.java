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

    public String createEnvio(Envio envio) {
        try {
            Aeropuerto origen = datosEnMemoriaService.getAeropuertos().get(envio.getOrigen());
            Aeropuerto destino = datosEnMemoriaService.getAeropuertos().get(envio.getDestino());
            // Agregar fecha de salida considerando la hora actual y la diferencia horaria
            ZonedDateTime fechaHoraSalida = ZonedDateTime.now();
            
            // logger.info("Envío llegando con fecha de salida: " + envio.getFechaHoraSalida());
            if(envio.getFechaHoraSalida() != null) {
                fechaHoraSalida = envio.getFechaHoraSalida();
            }
            //Para recuperar la hora original:
            //1. Las fechas llegan en UTC de una zona horaria Lima (-5), pero realmente son de origen.gmt
            //2. Se convierte a la zona horaria de origen
            fechaHoraSalida = fechaHoraSalida.plusHours((-5)-origen.getGmt());

            // logger.info("Fecha de salida: " + fechaHoraSalida);
            
            Boolean mismoContinente = origen.getContinente().equals(destino.getContinente());
            // Agregar fecha de llegada prevista considerando la hora de salida y la
            // distancia entre los aeropuertos
            ZonedDateTime fechaHoraLlegadaPrevista = fechaHoraSalida.plusDays(mismoContinente ? 1 : 2);
            fechaHoraLlegadaPrevista = fechaHoraLlegadaPrevista.withZoneSameLocal(destino.getZoneId());

            envio.setFechaHoraSalida(fechaHoraSalida);
            envio.setFechaHoraLlegadaPrevista(fechaHoraLlegadaPrevista);
            // logger.info("Todo bien hasta fechas. Guardando envio");
            // for (Paquete paq : envio.getPaquetes())
            //     System.out.println("Codigo: " + paq.getId() + " " + paq.getIdPaquete());
            // logger.info(envio.toString());
            //Nueva precaución
            envio.setReceptor(null);
            envio.setEmisor(null);
            envio.setPaquetes(null);
            envio = envioRepository.save(envio);
            logger.info("Todo bien hasta primer guardado");
            envio.setCodigoEnvio(envio.getOrigen() + envio.getId());
            envio.setPaquetes(null);
            envioRepository.save(envio);
            logger.info("Todo bien hasta segundo guardado");

            String codigosPaquetes = "";
            for (int i = 0; i < envio.getCantidadPaquetes(); i++) {
                // Guardar paquetes
                Paquete paquete = new Paquete();
                paquete.setCodigoEnvio(envio.getCodigoEnvio());
                int codigoPaquete = 1000000 * origen.getIdAeropuerto() + 100 * envio.getId() + (i + 1);
                paquete.setIdPaquete(codigoPaquete);
                paquete.setCostosRuta(null);
                paquete.setFechasRuta(null);
                paquete.setRuta(null);
                paquete.setRutaPosible(null);
                Paquete paqueteNuevo= paqueteService.createPaquete(paquete);
                codigosPaquetes += paqueteNuevo.getId() + " ";
            }
            logger.info("Todo bien hasta guardado de paquetes");
            return codigosPaquetes;
        } catch (Exception e) {
            logger.error("Error al crear envio: ");
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public Envio getEnvio(int id) {
        try {
            return envioRepository.findById(id).get();
        } catch (Exception e) {
            return null;
        }
    }

    public Envio getEnvioCodigo(String codigo) {
        try {
            return envioRepository.findByCodigoEnvio(codigo);
        } catch (Exception e) {
            return null;
        }
    }

    public Envio updateEnvio(Envio envio) {
        try {
            if (envio == null) {
                System.out.println("El envio fue null");
                return null;
            }
            return envioRepository.save(envio);
        } catch (Exception e) {
            System.out.println("Excepcion: " + e.getMessage());
            return null;
        }
    }

    public String deleteEnvio(int id) {
        try {
            Envio envio = envioRepository.findById(id).get();
            if (envio != null) {
                envioRepository.delete(envio);
            } else {
                return "Envio no encontrado";
            }
            return "Envio eliminado";
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }

    public ArrayList<Envio> getEnvios() {
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

    // @Transactional
    // public HashMap<String, Envio> getEnviosEntre(ZonedDateTime fechaHoraInicio,
    // ZonedDateTime fechaHoraFin)
    // {
    // HashMap<String, Envio> enviosEntre = new HashMap<>();
    // List <Envio> envios = getEnviosAfterDate(fechaHoraInicio);
    // // logger.info("Envios despues de fecha inicio");
    // for (Envio envio : envios) {
    // // logger.info(envio.getFechaHoraSalida());
    // // logger.info(fechaHoraInicio);
    // if (envio.getFechaHoraSalida().isAfter(fechaHoraInicio) &&
    // envio.getFechaHoraSalida().isBefore(fechaHoraFin)) {
    // List<Paquete> paquetes =
    // paqueteService.getPaquetesByCodigoEnvio(envio.getCodigoEnvio());
    // envio.setPaquetes(paquetes);
    // enviosEntre.put(envio.getCodigoEnvio(), envio);
    // }
    // }
    // return enviosEntre;
    // }

    //@Transactional
    public HashMap<String, Envio> getEnviosEntre(ZonedDateTime fechaHoraInicio, ZonedDateTime fechaHoraFin) {
        HashMap<String, Envio> enviosEntre = new HashMap<>();
        try {
            List<Envio> envios = getEnviosAfterDate(fechaHoraInicio);
            // logger.info("Envios despues de fecha inicio: " + envios.size());
            for (Envio envio : envios) {
                // logger.info("Fecha de salida: " + envio.getFechaHoraSalida());
                // logger.info("Fecha de fin: " + fechaHoraFin);
                if (envio.getFechaHoraSalida().isAfter(fechaHoraInicio)
                        && envio.getFechaHoraSalida().isBefore(fechaHoraFin)) {
                    try {
                        List<Paquete> paquetes = paqueteService.getPaquetesByCodigoEnvio(envio.getCodigoEnvio());
                        envio.setPaquetes(paquetes);
                        enviosEntre.put(envio.getCodigoEnvio(), envio);
                    } catch (Exception e) {
                        // Manejo de excepciones específicas para paquetes
                        logger.info("Error obteniendo paquetes para el envio: " + envio.getCodigoEnvio(), e);
                        // Opcionalmente podrías lanzar una excepción personalizada o manejarlo de otra
                        // manera
                    }
                }
            }
            // logger.info("Envios entre fechas: " + enviosEntre.size());
        } catch (Exception e) {
            logger.info("Error obteniendo envíos después de la fecha de inicio: " + fechaHoraInicio);
            // Lanzar excepción para notificar al controlador
            throw new RuntimeException("Error al obtener envíos después de la fecha de inicio", e);
        }
        return enviosEntre;
    }

    public HashMap<String, Envio> getEnviosEntrev2(ZonedDateTime fechaHoraInicio, ZonedDateTime fechaHoraFin) {
        HashMap<String, Envio> enviosEntre = new HashMap<>();
        try {
            List<Envio> envios = getEnviosAfterDate(fechaHoraInicio);
            logger.info("Envios despues de fecha inicio: " + envios.size());
            for (Envio envio : envios) {
                if (envio.getFechaHoraSalida().isAfter(fechaHoraInicio)
                        && envio.getFechaHoraSalida().isBefore(fechaHoraFin)) {
                    try {
                        List<Paquete> paquetes = paqueteService.getPaquetesByCodigoEnvio(envio.getCodigoEnvio());
                        envio.setPaquetes(paquetes);
                        enviosEntre.put(envio.getCodigoEnvio(), envio);
                    } catch (Exception e) {
                        // Manejo de excepciones específicas para paquetes
                        logger.info("Error obteniendo paquetes para el envio: " + envio.getCodigoEnvio(), e);
                        // Opcionalmente podrías lanzar una excepción personalizada o manejarlo de otra
                        // manera
                    }
                }
                else{
                    logger.info("Fecha de salida: " + envio.getFechaHoraSalida());
                    logger.info("Fecha de fin: " + fechaHoraFin);
                }
            }
        } catch (Exception e) {
            logger.info("Error obteniendo envíos después de la fecha de inicio: " + fechaHoraInicio);
            // Lanzar excepción para notificar al controlador
            throw new RuntimeException("Error al obtener envíos después de la fecha de inicio", e);
        }
        return enviosEntre;
    }

}
