package com.dp1.backend.services;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp1.backend.models.ColeccionRuta;
import com.dp1.backend.repository.ColeccionRutaRepository;

@Service
public class ColeccionRutaService {
    private static final Logger logger = LogManager.getLogger(ColeccionRutaService.class);

    @Autowired
    private ColeccionRutaRepository rutaRepository;

    public ColeccionRuta createColeccionRuta(ColeccionRuta ruta)
    {
        try {

            return rutaRepository.save(ruta);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    public ColeccionRuta getColeccionRuta(int id)
    {
        try {
            return rutaRepository.findById(id).get();
        } catch (Exception e) {
            return null;
        }
    }

    public ColeccionRuta updateColeccionRuta(ColeccionRuta ruta){
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

    public String deleteColeccionRuta(int id){
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
