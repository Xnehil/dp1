package com.dp1.backend.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp1.backend.models.RutaPosible;
import com.dp1.backend.repository.RutaPosibleRepository;

@Service
public class RutaPosibleService {
    @Autowired
    private RutaPosibleRepository rutaPosibleRepository;

    public RutaPosible createRutaPosible(RutaPosible rutaPosible)
    {
        try {
            return rutaPosibleRepository.save(rutaPosible);
        } catch (Exception e) {
            return null;
        }
    }

    public RutaPosible getRutaPosible(int id)
    {
        try {
            return rutaPosibleRepository.findById(id).get();
        } catch (Exception e) {
            return null;
        }
    }

    public RutaPosible updateRutaPosible(RutaPosible rutaPosible){
        try {
            if (rutaPosible == null)
            {
                return null;
            }
            return rutaPosibleRepository.save(rutaPosible);
        } catch (Exception e) {
            return null;
        }
    }

    public String deleteRutaPosible(int id){
        try {
            RutaPosible rutaPosible = rutaPosibleRepository.findById(id).get();
            if (rutaPosible != null) {
                rutaPosibleRepository.delete(rutaPosible);
            }
            else {
                return "Ruta posible no hallada";
            }
            return "Ruta posible eliminada";
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }

    public ArrayList<RutaPosible> getAllColeccioRutaPosibles()
    {
        return (ArrayList<RutaPosible>) rutaPosibleRepository.findAll();
    }
}
