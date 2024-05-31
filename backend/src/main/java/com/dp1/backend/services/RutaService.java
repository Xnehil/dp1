package com.dp1.backend.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp1.backend.models.Ruta;
import com.dp1.backend.repository.RutaRepository;

@Service
public class RutaService {
    @Autowired
    private RutaRepository rutaRepository;

    public Ruta createRuta(Ruta ruta)
    {
        try {
            return rutaRepository.save(ruta);
        } catch (Exception e) {
            return null;
        }
    }

    public Ruta getRuta(int id)
    {
        try {
            return rutaRepository.findById(id).get();
        } catch (Exception e) {
            return null;
        }
    }

    public Ruta updateRuta(Ruta ruta){
        try {
            if (ruta == null)
            {
                return null;
            }
            return rutaRepository.save(ruta);
        } catch (Exception e) {
            return null;
        }
    }

    public String deleteRuta(int id){
        try {
            Ruta ruta = rutaRepository.findById(id).get();
            if (ruta != null) {
                rutaRepository.delete(ruta);
            }
            else {
                return "Ruta no hallada";
            }
            return "Aeropuerto eliminado";
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }

    public ArrayList<Ruta> getAeropuertos()
    {
        return (ArrayList<Ruta>) rutaRepository.findAll();
    }
}
