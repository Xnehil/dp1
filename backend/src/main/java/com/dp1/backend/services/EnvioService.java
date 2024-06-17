package com.dp1.backend.services;

import java.time.ZonedDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Paquete;
import com.dp1.backend.repository.EnvioRepository;

@Service
public class EnvioService {
    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private DatosEnMemoriaService datosEnMemoriaService;

    @Autowired
    private PaqueteService paqueteService;

    public String createEnvio(Envio envio)
    {
        try {
            Aeropuerto origen = datosEnMemoriaService.getAeropuertos().get(envio.getOrigen());
            Aeropuerto destino = datosEnMemoriaService.getAeropuertos().get(envio.getDestino());
            //Agregar fecha de salida considerando la hora actual y la diferencia horaria
            ZonedDateTime fechaHoraSalida = ZonedDateTime.now();
            fechaHoraSalida.withZoneSameInstant(origen.getZoneId());

            Boolean mismoContinente = origen.getContinente().equals(destino.getContinente());
            //Agregar fecha de llegada prevista considerando la hora de salida y la distancia entre los aeropuertos
            ZonedDateTime fechaHoraLlegadaPrevista = fechaHoraSalida.plusDays(mismoContinente ? 1 : 2);
            fechaHoraLlegadaPrevista.withZoneSameLocal(destino.getZoneId());

            envio.setFechaHoraSalida(fechaHoraSalida);
            envio = envioRepository.save(envio);
            envio.setCodigoEnvio(envio.getOrigen()+envio.getId());

            for (int i = 0; i < envio.getCantidadPaquetes(); i++) {
                //Guardar paquetes
                Paquete paquete = new Paquete();
                paquete.setCodigoEnvio(envio.getCodigoEnvio());
                paqueteService.createPaquete(paquete);
            }
            envioRepository.save(envio);
            return "Envio creado";
        } catch (Exception e) {
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
}
