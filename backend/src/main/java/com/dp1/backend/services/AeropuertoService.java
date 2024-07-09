package com.dp1.backend.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.repository.AeropuertoRepository;

@Service
public class AeropuertoService {
    @Autowired
    private AeropuertoRepository aeropuertoRepository;

    @Autowired
    private DatosEnMemoriaService datosEnMemoriaService;

    public Aeropuerto createAeropuerto(Aeropuerto aeropuerto)
    {
        try {
            return aeropuertoRepository.save(aeropuerto);
        } catch (Exception e) {
            return null;
        }
    }

    public Aeropuerto getAeropuerto(int id)
    {
        try {
            return aeropuertoRepository.findById(id).get();
        } catch (Exception e) {
            return null;
        }
    }

    public Aeropuerto updateAeropuerto(Aeropuerto aeropuerto){
        try {
            if (aeropuerto == null)
            {
                return null;
            }
            return aeropuertoRepository.save(aeropuerto);
        } catch (Exception e) {
            return null;
        }
    }

    public String deleteAeropuerto(int id){
        try {
            Aeropuerto aeropuerto = aeropuertoRepository.findById(id).get();
            if (aeropuerto != null) {
                aeropuertoRepository.delete(aeropuerto);
            }
            else {
                return "Aeropuerto no encontrado";
            }
            return "Aeropuerto eliminado";
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }

    public ArrayList<Aeropuerto> getAeropuertos()
    {
        return (ArrayList<Aeropuerto>) aeropuertoRepository.findAll();
    }

    public ArrayList<Aeropuerto> getAeropuertosMemoria()
    {
        ArrayList<Aeropuerto> aeropuertos = new ArrayList<Aeropuerto>();
        for (Aeropuerto aeropuerto : datosEnMemoriaService.getAeropuertos().values()) {
            aeropuertos.add(aeropuerto);
        }
        return aeropuertos;
    }

    public Aeropuerto getAeropuertoByCodigo(String codigo)
    {
        try {
            return datosEnMemoriaService.getAeropuertos().get(codigo);
        } catch (Exception e) {
            return null;
        }
    }
}