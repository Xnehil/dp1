package com.dp1.backend.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp1.backend.models.ColeccionRuta;
import com.dp1.backend.repository.ColeccionRutaRepository;

@Service
public class ColeccionRutaService {
    @Autowired
    private ColeccionRutaRepository rutaRepository;

    public ColeccionRuta createRuta(ColeccionRuta ruta)
    {
        try {
            return rutaRepository.save(ruta);
        } catch (Exception e) {
            return null;
        }
    }

    public ColeccionRuta getRuta(int id)
    {
        try {
            return rutaRepository.findById(id).get();
        } catch (Exception e) {
            return null;
        }
    }

    public ColeccionRuta updateRuta(ColeccionRuta ruta){
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
            ColeccionRuta ruta = rutaRepository.findById(id).get();
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

    public ArrayList<ColeccionRuta> getAllColeccionRutas()
    {
        return (ArrayList<ColeccionRuta>) rutaRepository.findAll();
    }
}
